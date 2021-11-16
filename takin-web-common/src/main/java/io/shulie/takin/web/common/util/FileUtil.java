package io.shulie.takin.web.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qianshui
 * @date 2020/5/12 上午10:31
 */
@Slf4j
public class FileUtil {

    public static final int READ_SIZE = 10 * 1024;

    /**
     * 文件权限 读写
     */
    public static final String FILE_PERMISSION_RW = "rw";

    public static List<File> convertMultipartFileList(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<File> fileList = Lists.newArrayList();
        multipartFiles.stream().forEach(data -> {
            File file = convertMultipartFile(data);
            if (file != null) {
                fileList.add(file);
            }
        });
        return fileList;
    }

    public static String replaceFileName(String fileName) {
        return fileName.replace(" ", "");
    }

    public static File convertMultipartFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getSize() <= 0L) {
            return null;
        }
        File toFile = new File(multipartFile.getOriginalFilename());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            outputStream = new FileOutputStream(toFile);
            int bytesRead = 0;
            byte[] buffer = new byte[READ_SIZE];
            while ((bytesRead = inputStream.read(buffer, 0, READ_SIZE)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //替换文件名中的空格
        if (multipartFile.getOriginalFilename().contains(" ")) {
            return rename(toFile, replaceFileName(multipartFile.getOriginalFilename()), true);
        }
        return toFile;
    }

    public static void deleteTempFile(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.size() == 0) {
            return;
        }
        multipartFiles.stream().forEach(data -> {
            File file = new File(replaceFileName(data.getOriginalFilename()));
            if (file.exists()) {
                file.delete();
            }
        });
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     *
     * @param file       被修改的文件
     * @param newName    新的文件名，包括扩展名
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件
     */
    public static File rename(File file, String newName, boolean isOverride) {
        final Path path = file.toPath();
        final CopyOption[] options = isOverride ? new CopyOption[] {StandardCopyOption.REPLACE_EXISTING}
            : new CopyOption[] {};
        try {
            return Files.move(path, path.resolveSibling(newName), options).toFile();
        } catch (IOException e) {
            log.error("修改文件名称异常", e);
        }
        return file;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        boolean flag = false;
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            return deleteDirectory(filePath);
        }

        try {
            flag = file.delete();
        } catch (SecurityException exception) {
            file.deleteOnExit();
        }
        return flag;
    }

    /**
     * 删除文件夹
     *
     * @param dir 文件夹地址
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }

    /**
     * 文件拷贝
     *
     * @param src  源文件
     * @param dest 目标文件
     */
    public static void copyFile(File src, File dest) {
        deleteFile(dest.getAbsolutePath());
        try {
            // 创建文件夹
            File destDir = dest.getParentFile();
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件拷贝异常", e);
        }
    }

    /**
     * 文件夹拷贝
     *
     * @param sourcePath 源文件夹
     * @param destPath   目标文件夹
     */
    public static void copyDir(String sourcePath, String destPath) {
        File start = new File(sourcePath);
        File end = new File(destPath);
        // 获取该文件夹下的所有文件以及目录的名字
        String[] filePath = start.list();
        if (filePath == null) {
            return;
        }
        if (!end.exists()) {
            end.mkdir();
        }
        for (String temp : filePath) {
            //查看其数组中每一个是文件还是文件夹
            File sourceFile = new File(start.getAbsolutePath() + File.separator + temp);
            if (sourceFile.isDirectory()) {
                //为文件夹，进行递归
                copyDir(sourcePath + File.separator + temp, destPath + File.separator + temp);
            } else {
                //为文件则进行拷贝
                copyFile(sourceFile, new File(destPath + File.separator + temp));
            }
        }
    }
}
