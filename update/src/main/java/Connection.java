import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.*;

/**
 * @author ponkotuy
 * @since 15/03/11.
 */
public class Connection {
    public static URLConnection withRedirect(URL url, long lastModified) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Accept-Encoding", "pack200-gzip, gzip");
        conn.setRequestProperty("User-Agent", "MyFleetGirls Updater");
        conn.setUseCaches(false);
        conn.setIfModifiedSince(lastModified);
        HttpURLConnection http = (HttpURLConnection) conn;
        int code = http.getResponseCode();
        if(code == HTTP_NOT_MODIFIED) {
            return null;
        } else if(300 <= code && code < 400) { // Redirect
            URL newUrl = new URL(http.getHeaderField("Location"));
            return withRedirect(newUrl, lastModified);
        } else if(200 <= code && code < 300) {
            return http;
        } else {
            throw new IOException("Error status code: " + code);
        }
    }

    public static void download(URLConnection conn, Path dst) throws IOException {
        conn.connect();

        String content = conn.getHeaderField("Content-Encoding");
        try(InputStream is = conn.getInputStream()) {
            if(content == null) {
                Files.copy(is, dst, REPLACE_EXISTING);
            } else switch (content) {
                case "pack200-gzip":
                    pack20ODownload(conn, dst);
                    break;
                case "gzip":
                    Files.copy(new GZIPInputStream(is), dst, REPLACE_EXISTING);
                    break;
                default:
                    Files.copy(is, dst, REPLACE_EXISTING);
            }
        }

        long requestModified = conn.getLastModified();
        if(requestModified != 0) {
            Files.setLastModifiedTime(dst, FileTime.fromMillis(requestModified)); // サーバー側の最終更新時刻に合わせる
        }
    }

    public static void pack20ODownload(URLConnection conn, Path dst) throws IOException {
        try(InputStream is = conn.getInputStream();
            OutputStream os = Files.newOutputStream(dst, WRITE, CREATE, TRUNCATE_EXISTING);
            JarOutputStream jar = new JarOutputStream(os)) {
            Pack200.Unpacker unpacker = Pack200.newUnpacker();
            unpacker.unpack(new GZIPInputStream(is), jar);
        }
    }
}
