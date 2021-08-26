//package io.shulie.takin.web.other;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.CharArrayWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.Collections;
//import java.util.List;
//
//import com.alibaba.fastjson.JSONObject;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.ZipUtil;
//import io.shulie.takin.web.common.constant.AppConstants;
//import io.shulie.takin.web.common.constant.ProbeConstants;
//import io.shulie.takin.web.common.util.CommonUtil;
//import org.apache.commons.compress.archivers.ArchiveEntry;
//import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
//import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
//import org.junit.Test;
//
///**
// * 压缩测试
// *
// * @author liuchuan
// * @date 2021/6/4 4:30 下午
// */
//public class CompressTest implements ProbeConstants, AppConstants {
//
//    @Test
//    public void testJson() {
//        String s = "{\n"
//            + "    \"dataSourceBusiness\":{\n"
//            + "        \"master\":\"10.7.66.2:7000\",\n"
//            + "        \"nodes\":\"10.7.66.2:7001,10.7.66.5:7003\"\n"
//            + "    },\n"
//            + "    \"dataSourceBusinessPerformanceTest\":{\n"
//            + "      \"master\":\"10.7.66.187:6379\",\n"
//            + "        \"nodes\":\"10.7.66.187:6379\",\n"
//            + "        \"password\":\"\",\n"
//            + "\"database\":0\n"
//            + "    }\n"
//            + "}";
//
//        Object object = JSONObject.parse(s);
//        System.out.println(object.toString());
//    }
//
//    @Test
//    public void testVersion() {
//        String version = "4.0.1.9";
//        System.out.println(checkProbeVersion(version));
//    }
//
//    private boolean checkProbeVersion(String probeVersionString) {
//        String[] probeVersionArray = probeVersionString.split(ENGLISH_PERIOD);
//        if (probeVersionArray.length < 1) {
//            return false;
//        }
//
//        for (int i = 0; i < probeVersionArray.length; i++) {
//            if (i > 2) {
//                return true;
//            }
//
//            if (Integer.parseInt(probeVersionArray[i]) < PROBE_VERSION_BASE[i]) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    @Test
//    public void testStringJoin() {
//        List<String> strings = Collections.emptyList();
//        System.out.println(String.join(",", strings));
//    }
//
//    @Test
//    public void testUncompress() {
//        String compressPath
//            = "/Users/loseself/data/project/takin-web/public/file/tmp/probe/20210608/1623117361707simulator.tar.gz";
//        File compressFile = new File(compressPath);
//
//        // 解压
//        String savePath = CommonUtil.getAbsoluteUploadPath("/public/file/", "probe");
//        String uncompressPath = savePath + System.currentTimeMillis();
//        uncompressGz(compressFile, uncompressPath);
//
//        File uncompressFile = new File(uncompressPath);
//        List<String> messages = AgentFileResolver.check(uncompressFile);
//
//        // 替换文件内容
//        String configPath = String.format("%s%s%s%s%s%s%s", uncompressPath, File.separator, "simulator", File.separator,
//            "config", File.separator, "simulator.properties");
//        replaceTextContent(configPath);
//
//        // 压缩
//        String simulator = uncompressPath + File.separator + "simulator";
//        String zipPath = savePath + System.currentTimeMillis() + ".zip";
//        ZipUtil.zip(simulator, zipPath);
//
//        // 删除解压之前的文件
//        FileUtil.del(uncompressFile);
//    }
//
//    @Test
//    public void testCheck() {
//        File file = new File("/Users/loseself/data/project/takin-web/public/file/probe/20210604/simulator");
//        List<String> errorMessages = AgentFileResolver.check(file);
//        System.out.println(errorMessages);
//    }
//
//    @Test
//    public void testDeCompressTarGzip() throws IOException {
//        //解压文件
//        File source = new File("/Users/loseself/data/project/takin-web/public/file/tmp/probe/20210604/1622789172680simulator.tar.gz");
//
//        //解压到哪
//        String targetPath = "/Users/loseself/data/project/takin-web/public/file/probe/20210604";
//
//        if (!source.exists()) {
//            throw new IOException("您要解压的文件不存在");
//        }
//
//    }
//
//    /**
//     * 替换文本文件中的 非法字符串
//     *
//     * @param path 文件路径
//     */
//    public void replaceTextContent(String path) {
//            // 读
//        File file = new File(path);
//        try (FileReader in = new FileReader(file);
//             BufferedReader bufIn = new BufferedReader(in)) {
//
//            // 内存流, 作为临时流
//            CharArrayWriter tempStream = new CharArrayWriter();
//            // 替换
//            String line;
//            while ((line = bufIn.readLine()) != null) {
//                // 替换每行中, 符合条件的字符串
//                // takin.web.url
//                if (line.startsWith("takin.web.url")) {
//                    line = replaceAfterEqualMark(line, "takinUrl");
//                }
//
//                // user.app.key
//                if (line.startsWith("user.app.key")) {
//                    line = replaceAfterEqualMark(line, "userAppKey");
//                }
//
//                // pradar.user.id
//                if (line.startsWith("pradar.user.id")) {
//                    line = replaceAfterEqualMark(line, "11");
//                }
//
//                // 将该行写入内存
//                tempStream.write(line);
//                // 添加换行符
//                tempStream.append(System.getProperty("line.separator"));
//            }
//
//            // 将内存中的流 写入 文件
//            FileWriter out = new FileWriter(file);
//            tempStream.writeTo(out);
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 替换等号后面的值
//     *
//     * @param aim 字符串
//     * @return 替换后的字符串
//     */
//    private String replaceAfterEqualMark(String aim, String valueString) {
//        return aim.substring(0, aim.indexOf(EQUAL_MARK) + 1) + valueString;
//    }
//
//    /**
//     * 判断压缩文件是否被损坏，并返回该文件的解压目录
//     *
//     * @param entry tar
//     * @param targetDir 解压到的路径
//     * @return 路径
//     */
//    private Path zipSlipProtect(ArchiveEntry entry, String targetDir)  {
//        Path targetDirResolved = Paths.get(targetDir).resolve(entry.getName());
//        Path normalizePath = targetDirResolved.normalize();
//
//        if (!normalizePath.startsWith(targetDir)) {
//            throw new RuntimeException("压缩文件已被损坏: " + entry.getName());
//        }
//
//        return normalizePath;
//    }
//
//    /**
//     * 解压 gz
//     *
//     * @param source 压缩文件
//     * @param targetPath 输出路径
//     */
//    private void uncompressGz(File source, String targetPath) {
//        // InputStream输入流，以下四个流将tar.gz读取到内存并操作
//        // BufferedInputStream缓冲输入流
//        // GzipCompressorInputStream解压输入流
//        // TarArchiveInputStream解tar包输入流
//        try (InputStream fi = new FileInputStream(source);
//             BufferedInputStream bi = new BufferedInputStream(fi);
//             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
//             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {
//
//            ArchiveEntry entry;
//            while ((entry = ti.getNextEntry()) != null) {
//
//                //获取解压文件目录，并判断文件是否损坏
//                Path newPath = zipSlipProtect(entry, targetPath);
//
//                if (entry.isDirectory()) {
//                    //创建解压文件目录
//                    Files.createDirectories(newPath);
//                } else {
//                    //再次校验解压文件目录是否存在
//                    Path parent = newPath.getParent();
//                    if (parent != null) {
//                        if (Files.notExists(parent)) {
//                            Files.createDirectories(parent);
//                        }
//                    }
//                    // 将解压文件输入到TarArchiveInputStream，输出到磁盘newPath目录
//                    Files.copy(ti, newPath, StandardCopyOption.REPLACE_EXISTING);
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("解压gz文件错误!");
//        }
//    }
//}
