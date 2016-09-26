package com.ponkotuy.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MFGKeyStore {
    private static final String TrustStoreFile = "myfleetgirls.keystore";
    private static final String TrustStorePass = "myfleetgirls";
    private static final String SslContextProtocol = "TLSv1";

    private static Logger logger = LoggerFactory.getLogger(MFGKeyStore.class);

    private SSLContext sslContext;

    public MFGKeyStore() throws IOException, GeneralSecurityException {
        logger.info("Generate CustomSslContext.");
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try {
            logger.debug("TrustStore loading from file.file:{}",TrustStoreFile);
            InputStream io = new FileInputStream(TrustStoreFile);
            trustStore.load(io, TrustStorePass.toCharArray());
        } catch (FileNotFoundException e) {
            try {
                logger.debug("TrustStore not found. file:{} err:{}",TrustStoreFile,e.getMessage());
                ClassLoader cl = getClass().getClassLoader();
                File file = new File(cl.getResource(TrustStoreFile).getFile());
                InputStream io = new FileInputStream(file);
                logger.debug("TrustStore loading by ClassLoader.");
                trustStore.load(io, TrustStorePass.toCharArray());
            } catch (Throwable e2) {
                logger.error("Can't load TrustStore by ClassLorder.",e2);
            }
        } catch (Throwable e) {
            logger.error("Can't load TrustStore unknown Exception.",e);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        logger.debug("Using SslContext :{}",SslContextProtocol);
        sslContext = SSLContext.getInstance(SslContextProtocol);
        sslContext.init(null, tmf.getTrustManagers(), null);
        logger.info("CustomSslContext initialized.");
    }

    public SSLContext getSslContext() {
        return sslContext;
    }
}
