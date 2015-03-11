import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author ponkotuy
 * Date: 15/03/09.
 */
public class MyVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
