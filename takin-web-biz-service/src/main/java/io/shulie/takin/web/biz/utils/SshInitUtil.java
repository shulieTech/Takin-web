package io.shulie.takin.web.biz.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

@Slf4j
public class SshInitUtil {

    private  String IP = "";// 远程服务器地址
    private  String USR = "";// 远程服务器用户名
    private  String PSWORD = ""; // 远程服务器密码


    private  String DEFAULTCHART = "UTF-8";

    private  Connection connection = new Connection(IP);// 创建对象

    public SshInitUtil(String ip,String password,String username){
        this.IP = ip;
        this.PSWORD = password;
        this.USR = username;
    }

    /**
     * login:ssh用户登录验证，使用用户名和密码来认证. <br/>
     * @return boolean
     * @since JDK 1.8
     */
    public  boolean login() {
        //创建远程连接，默认连接端口为22，如果不使用默认，可以使用方法
        try {
            connection.connect();
            //使用用户名和密码登录 有秘钥可以使用authenticateWithPublicKey验证
            return connection.authenticateWithPassword(USR, PSWORD);
        } catch (IOException e) {
            log.error("用户{}密码{}登录服务器{}失败！", USR, PSWORD, IP, e);
        }
        return false;
    }

    /**
     * 远程执行shll脚本或者命令
     *
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    public  String execute(String cmd) {
        String result = "";
        try {
            boolean isAuthed = login();
            if (isAuthed && connection != null) {
                Session session = connection.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result = processStdout(session.getStdout(), DEFAULTCHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if (StringUtils.isBlank(result)) {
                    log.info("得到标准输出为空,链接connection:" + connection + ",执行的命令：" + cmd);
                    result = processStdout(session.getStderr(), DEFAULTCHART);
                } else {
                    log.info("执行命令成功,链接connection:" + connection + ",执行的命令：" + cmd);
                }
                connection.close();
                session.close();
            }
        } catch (IOException e) {
            log.error("执行命令失败,链接connection:" + connection + ",执行的命令：" + cmd + "  " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     *
     * @param in      输入流对象
     * @param charset 编码
     * @return 以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        ;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + " ");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
