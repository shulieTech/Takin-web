package io.shulie.takin.cloud.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuyh
 * @date 2020/4/18 16:00
 */
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static File createFileDE(String filePathName) {
        File file = new File(filePathName);
        if (file.exists()) {
            if (!file.delete()) {
                return null;
            }
        }
        if (!makeDir(file.getParentFile())) {
            return null;
        }

        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return file;
    }

    public static boolean makeDir(File dir) {
        if (!dir.exists()) {
            File parent = dir.getParentFile();
            if (parent != null) {
                makeDir(parent);
            }
            return dir.mkdir();
        }
        return true;
    }

    public static List<File> getDirectoryFiles(String dir, String fileEndsWith) {
        if (dir == null) {
            return null;
        }
        File fileDir = new File(dir);
        if (!fileDir.isDirectory()) {
            LOGGER.warn("Expected a dir, but not: '{}'", fileDir.getPath());
        }
        if (!fileDir.isAbsolute()) {
            LOGGER.warn("Expected a absolute path, bu not: '{}'", fileDir.getPath());
        }
        File[] files = fileDir.listFiles(file -> {
            // 没有匹配规则的话返回第一个
            if (fileEndsWith == null) {return true;}
            // 匹配文件名
            else {return file.getName().endsWith(fileEndsWith);}
        });
        if (files == null || files.length == 0) {return null;}
        return new ArrayList<>(Arrays.asList(files));
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    if (!deleteDir(new File(child))) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public static boolean writeTextFile(String content, String filePathName) {
        File file = createFileDE(filePathName);
        if (file == null) {
            return false;
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    public static String readTextFileContent(File file) {
        InputStreamReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            char[] buffer = new char[32];
            int length;
            while ((length = reader.read(buffer)) > 0) {
                stringBuilder.append(buffer, 0, length);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LOGGER.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                        TakinCloudExceptionEnum.FILE_CLOSE_ERROR, e);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 构建目录
     *
     * @param outputDir -
     * @param subDir    -
     */
    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        //子目录不为空
        if (!(subDir == null || "".equals(subDir.trim()))) {
            file = new File(outputDir + "/" + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }

    public static void tarGzFileToFile(String sourcePath, String extractPath) {
        TarInputStream tarIn = null;
        try {
            tarIn = new TarInputStream(new GZIPInputStream(
                new BufferedInputStream(new FileInputStream(sourcePath))),
                1024 * 2);
            //创建输出目录
            createDirectory(extractPath, null);

            TarEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    //是目录
                    entry.getName();
                    //创建空目录
                    createDirectory(extractPath, entry.getName());
                } else {
                    //是文件
                    File tmpFile = new File(extractPath + "/" + entry.getName());
                    //创建输出目录
                    createDirectory(tmpFile.getParent() + "/", null);
                    try (OutputStream out = new FileOutputStream(tmpFile)) {
                        int length;
                        byte[] b = new byte[2048];
                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                    } catch (IOException ex) {
                        LOGGER.error("tarGzFileToFile 异常", ex);
                        throw ex;
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error("异常代码【{}】,异常内容：tar包解压归档文件出现异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_TAR_ERROR, ex);
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                }
            } catch (IOException ex) {
                LOGGER.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.FILE_CLOSE_ERROR, ex);
            }
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir：要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            LOGGER.warn("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) {break;}
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) {break;}
            }
        }
        // 删除目录成功
        if (flag) {
            // 删除当前目录
            if (dirFile.delete()) {LOGGER.warn("删除目录[{}]成功！", dir);} else {return false;}
            return true;
        }
        LOGGER.warn("删除目录失败！");
        return false;
    }

    /**
     * 删除单个文件
     *
     * @param fileName：要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LOGGER.warn("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                LOGGER.warn("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LOGGER.warn("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
