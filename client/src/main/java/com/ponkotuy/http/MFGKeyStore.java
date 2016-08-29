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

    private static Logger logger = LoggerFactory.getLogger(MFGKeyStore.class);

    private SSLContext sslContext;

    public MFGKeyStore() throws IOException, GeneralSecurityException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try {
            InputStream io = new FileInputStream(TrustStoreFile);
            trustStore.load(io, TrustStorePass.toCharArray());
        } catch (FileNotFoundException e) {
            try {
                ClassLoader cl = getClass().getClassLoader();
                File file = new File(cl.getResource(TrustStoreFile).getFile());
                InputStream io = new FileInputStream(file);
                trustStore.load(io, TrustStorePass.toCharArray());
            } catch (Throwable e2) {
                logger.error("Can't load trustStore by ClassLorder",e2);
            }
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(null, tmf.getTrustManagers(), null);
    }

    public SSLContext getSslContext() {
        return sslContext;
    }
}
