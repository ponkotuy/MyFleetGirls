import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author ponkotuy
 * Date: 14/04/06.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        try {
            List<String> urls = getProperties("update.properties");
            for(String uStr: urls) {
                URL url = new URL(uStr);
                Path dst = Paths.get(url.getPath()).getFileName();
                if(Files.exists(dst)) {
                    if (compareFileSize(url, dst)) {
                        System.out.println(dst.getFileName() + "に変更はありません");
                    } else {
                        System.out.println(dst.getFileName() + "の更新を見つけました。更新します");
                        Files.delete(dst);
                        download(withRedirect(url), dst);
                        System.out.println(dst.getFileName() + "のダウンロードが完了しました");
                    }
                } else {
                    System.out.println(dst.getFileName() + "は存在しません。ダウンロードします。");
                    download(withRedirect(url), dst);
                    System.out.println(dst.getFileName() + "のダウンロードが完了しました");
                }
            }
        } catch (MalformedURLException e) {
            System.err.println("おやっ、URLの書式に異常です！ぽんこつさんが悪いです！");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("おやっ、IOExceptionです！");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static boolean compareFileSize(URL url, Path dst) throws IOException {
        return Files.size(dst) == withRedirect(url).getContentLengthLong();
    }

    public static List<String> getProperties(String fName) throws IOException {
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath(fName);
        Properties p = new Properties();
        try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            p.load(is);
        }
        List<String> result = new ArrayList<>(p.size());
        for(Object prop: p.keySet()) {
            String key = (String) prop;
            result.add(p.getProperty(key));
        }
        return result;
    }

    public static URLConnection withRedirect(URL url) throws IOException {
        if(url.getProtocol().equals("http")) {
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int code = http.getResponseCode();
            if(300 <= code && code < 400) { // Redirect
                URL newUrl = new URL(http.getHeaderField("Location"));
                return withRedirect(newUrl);
            } else if(200 <= code && code < 300) {
                return http;
            } else {
                throw new IOException("Error status code: " + code);
            }
        } else if(url.getProtocol().equals("https")) {
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            // TODO: オレオレ証明書設定。本番で外しても動くことを確認したらすみやかに削除
            https.setSSLSocketFactory(getSSLSocketFactory());
            https.setHostnameVerifier(new MyVerifier());
            return https;
        }
        throw new IOException("Unknown protocol: " + url.getProtocol());
    }

    public static void download(URLConnection conn, Path dst) throws IOException {
        try(InputStream is = conn.getInputStream()) {
            try (OutputStream os = Files.newOutputStream(dst, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
                readAll(is, os);
            }
        }
    }

    private static SSLSocketFactory getSSLSocketFactory() throws SSLException {
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            TrustManager[] tms = {new MyTrustManager()};
            sc.init(null, tms, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new SSLException(e);
        }
        return sc.getSocketFactory();
    }

    public static void readAll(InputStream is, OutputStream os) throws IOException {
        byte [] buffer = new byte[1024];
        while(true) {
            int len = is.read(buffer);
            if(len < 0) {
                break;
            }
            os.write(buffer, 0, len);
        }
    }
}
