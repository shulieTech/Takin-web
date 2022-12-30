package io.shulie.takin.web.biz.utils;

import com.alibaba.excel.util.DateUtils;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class SshInitUtilTest {

    private Session session;
    private final String username;
    private final String host;
    private final String password;

    public SshInitUtilTest(String username, String host, String password) {
        this.username = username;
        this.host = host;
        this.password = password;
    }

    private Session getSession() {
        return this.session;
    }

    private void connect() {
        JSch ssh = new JSch();
        try {
            this.session = ssh.getSession(this.username, this.host);
            this.session.setPassword(this.password);
            this.session.setServerAliveCountMax(0);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            this.session.connect(3000);
            System.out.println("连接成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        getSession().disconnect();
    }

    private void shellCmd(String... cmds) {

        System.out.println("执行如下命令:\n" + String.join("\n", cmds));
        ChannelShell channel = null;
        try {
            channel = (ChannelShell) session.openChannel("shell");
            try (
                    InputStream in = channel.getInputStream();
                    OutputStream os = channel.getOutputStream();
            ) {
                channel.setPty(true);
                channel.connect();
                for (String cmd : cmds) {
                    os.write((cmd + "\r\n").getBytes());
                    os.flush();
                    Thread.sleep(500);
                }
                os.write(("exit" + "\r\n").getBytes());
                os.flush();
                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) {
                            break;
                        }
                        System.out.println(new String(tmp, 0, i));
                    }
                    if (channel.isClosed()) {
                        if (in.available() > 0) {
                            continue;
                        }
                        System.out.println("exit-status: " + channel.getExitStatus());
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (channel != null)
                channel.disconnect();
        }
    }

    private void sshCopyId(String ip, String password) {
        connect();
        String[] sshCopyIdCmd = new String[]{
                "ssh-copy-id -p 22 root@" + ip,
                "yes",
                password
        };
        shellCmd(sshCopyIdCmd);
        close();
    }

    private String addNewNodeFile(List<String> ips, Long tenantId) {
        connect();
        String[] ipArray = new String[ips.size() + 1];
        String fileName = "/data/k8s_pkgs/new_node_" + tenantId + "_" + DateUtils.format(new Date(), DateUtils.DATE_FORMAT_10);
        for (int i = 0; i < ips.size() + 1; i++) {
            if (i == 0) {
                ipArray[i] = "touch " + fileName;
                continue;
            }
            ipArray[i] = "echo " + ips.get(i - 1) + " >> " + fileName;

        }
        shellCmd(ipArray);
        close();
        return fileName;
    }

    private void addNode(String filename){
        connect();
        String logName = filename.substring(filename.lastIndexOf("/")+1);
        String addNodeCmd = "cd /data/k8s_pkgs && nohup sh add_node.sh " + filename + " > node_add_logs/add_"+logName+".log &";
        shellCmd(addNodeCmd);
        close();
    }

    private void addLabelToNode(List<String> nodeNames,  String key, String value){
        connect();
        String [] nodeNameArray = new String[nodeNames.size()];
        for(int i =0; i< nodeNames.size(); i++){
            nodeNameArray[i] = "kubectl label node " + nodeNames.get(i) + " " + key +"=" + value;
        }
        shellCmd(nodeNameArray);
        close();
    }

    //测试
    public static void main(String[] args) throws Exception {
        SshInitUtilTest ssh = new SshInitUtilTest("root", "36.138.67.201", "L%nux@78p");
//        SshInitUtilTest ssh1 = new SshInitUtilTest("root", "36.138.67.218", "L%nux@78p");

//        ssh.sshCopyId("36.138.67.180","L%nux@78p");
//        ssh1.sshCopyId("36.138.67.180","L%nux@78p");
//        List<String> ss = new ArrayList<>();
//        ss.add("36.138.67.180");
////        ss.add("36.138.67.180");
//        //进入 /data/k8s_pkgs
//        // ip 写入 文件
//        String filename = ssh.addNewNodeFile(ss, 1L);
//        //执行node 扩容
//        ssh.addNode(filename);
//        ssh.addLabelToNode("36.138.67.180", "test", "abc");

    }
}

