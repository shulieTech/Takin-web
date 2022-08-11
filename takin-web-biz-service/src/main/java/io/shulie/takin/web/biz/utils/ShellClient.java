package io.shulie.takin.web.biz.utils;

import cn.hutool.core.io.FileUtil;

import io.shulie.takin.web.biz.pojo.dto.ShellInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ClassName:    ShellClient
 * Package:    io.shulie.takin.plugin.enginecall
 * Description:
 * Datetime:    2022/7/11   22:44
 * Author:   chenhongqiao@shulie.com
 */
@Component
@Slf4j
@Data
public class ShellClient {

    @Value("${shell.expect.exec.timeout:5}")
    private String execTimeout;

    @PostConstruct
    public ShellClient shellClient() {
        ShellInfo info = new ShellInfo();
        info.setSsh(false);
        info.setNoPass(false);
        //检测except工具
        File expectFile = new File("/usr/bin/expect");
        if (!FileUtil.exist(expectFile)) {
            //下载expect
            info.setCmd(" yum install expect -y");
            exec(info);
        }

        //检测sshpass 工具
        File sshpassFile = new File("/usr/bin/sshpass");
        if (!FileUtil.exist(sshpassFile)) {
            sshpassFile = new File("/usr/local/bin/sshpass");
            if (!FileUtil.exist(sshpassFile)) {
                //下载sshpass
                info.setCmd(" yum install sshpass -y");
                exec(info);
            }
        }

        //检测ssh 公钥
        String home = System.getProperties().getProperty("user.home");
        File keyPubFile = new File(home + "/.ssh/id_rsa");
        if (!FileUtil.exist(keyPubFile)) {
            //生成公钥
            info.setCmd(" ssh-keygen");
            Map<String, String> expectMap = new LinkedHashMap<>();
            expectMap.put("Enter file in which to save the key*", "");
            expectMap.put("*empty for no passphrase*", "");
            expectMap.put("Enter same passphrase again:", "");
            info.setExpectMap(expectMap);
            info.setSpawn(true);
            exec(info);
        }


        return this;
    }

    /**
     * 执行命令
     */
    public List<String> exec(ShellInfo info) {
        String cmdStr = getCmd(info);
        String cmd[] = {"sh", "-c", cmdStr};
        List<String> outputs = new ArrayList<>();
        BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while (Objects.nonNull(line = reader.readLine())) {
                outputs.add(line);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(reader)) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outputs;
    }

    /**
     * 获取cmd
     *
     * @param info shell相关参数
     * @return
     */
    private String getCmd(ShellInfo info) {
        if (StringUtils.isBlank(info.getCmd())) {
            return info.getCmd();
        }
        String cmd = "";
        if (info.isSsh()) {
            cmd = "ssh " + info.getName() + "@" + info.getIp() + " \"" + info.getCmd() + "\"";
        } else {
            cmd = "\"" + info.getCmd() + "\"";
        }

        if (info.isSpawn()) {
            cmd = "/usr/bin/expect <<-EOF\n" +
                    "set timeout " + execTimeout + "\n" +
                    "spawn " + cmd + "\n" +
                    "expect {\n";

            if (!CollectionUtils.isEmpty(info.getExpectMap())) {
                //添加except
                int size = info.getExpectMap().size();
                Set<String> keys = info.getExpectMap().keySet();
                int i = 0;
                for (String key : keys) {
                    i++;
                    if (i != size) {
                        cmd += "\"" + key + "\" { send \"" + info.getExpectMap().get(key) + "\\r\"; exp_continue }\n";
                    } else {
                        cmd += "\"" + key + "\" { send \"" + info.getExpectMap().get(key) + "\\r\" }\n";
                    }
                }
                cmd += "\n" +
                        "}\n" +
                        "expect eof\n" +
                        "EOF\n";

            }
        } else if (!info.isNoPass()) {
            //非免密
            cmd = "sshpass -p " + info.getPassword() + " " + cmd;
        }
        return cmd;
    }
}
