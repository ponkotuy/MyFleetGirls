import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.net.ssl.*;
import java.security.*;

public class MFGKeyStore {
    private static final String trustStoreFile = "myfleetgirls.keystore";
    private static final String trustStorePass = "myfleetgirls";

    private SSLContext sslContext;

    public MFGKeyStore () throws IOException,GeneralSecurityException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try(InputStream io = new FileInputStream(trustStoreFile)){
            trustStore.load(io,trustStorePass.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        sslContext= SSLContext.getInstance("SSL");
        sslContext.init(null,tmf.getTrustManagers(),null);
    }

    public void setSSLSocketFactory(HttpURLConnection conn) {
        if ( conn instanceof HttpsURLConnection ) {
            SSLSocketFactory factory = sslContext.getSocketFactory();
            ((HttpsURLConnection)conn).setSSLSocketFactory(factory);
        }
    }
}
