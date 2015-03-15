package com.ponkotuy.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class MFGKeyStore {
    private static final String TrustStoreFile = "myfleetgirls.keystore";
    private static final String TrustStorePass = "myfleetgirls";

    private SSLContext sslContext;

    public MFGKeyStore() throws IOException, GeneralSecurityException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try(InputStream io = new FileInputStream(TrustStoreFile)){
            trustStore.load(io, TrustStorePass.toCharArray());
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
