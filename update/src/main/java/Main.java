import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author ponkotuy
 * Date: 14/04/06.
 */
public class Main {
    public static void main(String[] args) {
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
                        download(url, dst);
                        System.out.println(dst.getFileName() + "のダウンロードが完了しました");
                    }
                } else {
                    System.out.println(dst.getFileName() + "は存在しません。ダウンロードします。");
                    download(url, dst);
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
        return Files.size(dst) == url.openConnection().getContentLengthLong();
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

    public static void download(URL url, Path dst) throws IOException {
        try(InputStream is = url.openConnection().getInputStream()) {
            try (OutputStream os = Files.newOutputStream(dst, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
                readAll(is, os);
            }
        }
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
