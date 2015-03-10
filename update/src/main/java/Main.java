import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import static java.net.HttpURLConnection.*;
import static java.nio.file.StandardOpenOption.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 *
 * @author ponkotuy, b-wind
 * Date: 15/03/09.
 */
public class Main {
    public static void main(String[] args) {
        try {
            List<String> urls = getProperties("update.properties");
            for(String uStr: urls) {
                URL url = new URL(uStr);
                Path dst = Paths.get(url.getPath()).getFileName();
                if( ! Files.exists(dst) ) {
                    System.out.println(dst.getFileName() + "は存在しません。ダウンロードします。");
                }
                boolean updated = request(url, dst);
                if ( updated ) {
                  System.out.println(dst.getFileName() + "のダウンロードが完了しました");
                } else {
                  System.out.println(dst.getFileName() + "に変更はありません");
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

    public static List<String> getProperties(String fName) throws IOException {
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath(fName);
        Properties p = new Properties();
        try(InputStream is = Files.newInputStream(path, READ)) {
            p.load(is);
        }
        List<String> result = new ArrayList<>(p.size());
        for(Object prop: p.keySet()) {
            String key = (String) prop;
            result.add(p.getProperty(key));
        }
        return result;
    }

    public static boolean request(URL url, Path dst) throws IOException {
        FileTime fileModified = Files.exists(dst)
            ? Files.getLastModifiedTime(dst,NOFOLLOW_LINKS)
            : null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true); // 301,302 Redirect の自動適応
            if ( fileModified != null ) {
                connection.setIfModifiedSince(fileModified.toMillis()); // ローカルファイルの最終更新時刻を設定
            }
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept-Encoding","pack200-gzip, gzip"); //  gzip pack200 形式の Content Negotiation 設定
            connection.setRequestProperty("User-Agent","MyFleetGirls Updater");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if ( responseCode == HTTP_OK ) { // 202 OK
                try (InputStream is = connection.getInputStream()) {
                    if ( contentEncoding(connection,"pack200-gzip") ) {
                    Pack200.Unpacker unpacker = Pack200.newUnpacker();
                    try (OutputStream os = Files.newOutputStream(dst, WRITE, CREATE, TRUNCATE_EXISTING)) {
                      try (JarOutputStream jar = new JarOutputStream(os)) {
                        unpacker.unpack(new GZIPInputStream(is), jar);
                      }
                    }
                } else if ( contentEncoding(connection,"gzip") ) {
                    Files.copy(new GZIPInputStream(is), dst, REPLACE_EXISTING);
                } else {
                    Files.copy(is, dst, REPLACE_EXISTING);
                }
              }
              long requestModified = connection.getLastModified();
              if ( requestModified != 0 ) {
                  Files.setLastModifiedTime(dst,FileTime.fromMillis(requestModified)); // サーバー側の最終更新時刻に合わせる
              }
              return true;
            } else if ( responseCode == HTTP_NOT_MODIFIED ) { // 304 Not Modified
              return false;
            } else {
              throw new RuntimeException("Unknown status:"+responseCode);
            }
        } finally {
          connection.disconnect();
        }
    }

    public static boolean contentEncoding(HttpURLConnection connection,String encoding) {
        List<String> contentEncodings = connection.getHeaderFields().get( "Content-Encoding" );
        if ( contentEncodings != null && contentEncodings.contains(encoding) ) {
          return true;
        } else {
          return false;
        }
    }
}
