package io.shulie.takin.web.biz.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;

@Slf4j
public class SshInitUtil {

    private String IP = "";// 远程服务器地址
    private String USR = "";// 远程服务器用户名
    private String PSWORD = ""; // 远程服务器密码
    private Integer port = 22;
    private String DEFAULTCHART = "UTF-8";


    public SshInitUtil(String ip, String password, String username) {
        this.IP = ip;
        this.PSWORD = password;
        this.USR = username;
    }

    public String execute(String command,Integer timeout) {
        String out = "";
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(USR, IP, port);
            session.setConfig("StrictHostKeyChecking", "no");

            session.setPassword(PSWORD);
            session.setTimeout(timeout);
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);

            channelExec.connect();
            out = IOUtils.toString(in, "UTF-8");
            channelExec.disconnect();
            session.disconnect();
        } catch (Exception e) {
            log.error("执行命令出现异常", e);
        }
        return out;
    }

}
