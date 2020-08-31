package top.chenzhimeng.fu_community.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class FileUtil {

    private static String filesPath;

    @Value("${files-path}")
    private void init(String filesPath) {
        FileUtil.filesPath = filesPath;
    }

    /**
     * 将multipartFile存到本地
     *
     * @param file     待保存的文件
     * @param dateNow  现在的日期（资源目录）
     * @param fileName 文件名
     */
    public static void transferTo(MultipartFile file, String dateNow, String fileName) throws IOException {
        File dir = new File(filesPath + dateNow);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        file.transferTo(new File(dir, fileName));
    }

    /**
     * 根据url删除一个资源
     *
     * @param url 待删除的资源
     * @return 删除结果
     */
    public static boolean delete(String url) {
        File file = new File(filesPath + url.substring(url.indexOf("/media/") + 7));
        log.info("delete: " + file.getAbsolutePath());
        return file.delete();
    }
}
