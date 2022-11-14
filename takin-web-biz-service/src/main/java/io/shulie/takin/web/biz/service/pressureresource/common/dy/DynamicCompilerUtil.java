package io.shulie.takin.web.biz.service.pressureresource.common.dy;

import javax.tools.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DynamicCompilerUtil {
    public static void main(String[] args) throws Exception {
        check("import  com.example.demo.entity.User ;\nUser user = new User()\nuser.setName(\"挡板\");\nreturn user ;");
    }

    public static String check(String source) throws ClassNotFoundException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        JavaFileObject testFile = generateTest(source);
        Iterable<? extends JavaFileObject> classes = Arrays.asList(testFile);
        javaCompiler.getTask(null, standardJavaFileManager, collector, null, null, classes).call();
        List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();
        String rs = "";
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            String msg = diagnostic.getMessage(Locale.ENGLISH);
            if (msg.contains("类DyClass是公共的")) {
                continue;
            }
            if (msg.contains("程序包") && msg.contains("不存在")) {
                continue;
            }
            if (msg.contains("找不到符号")) {
                continue;
            }
            rs = msg;
        }
        return rs;
    }

    private static JavaFileObject generateTest(String source) {
        String[] values = source.split("\n");
        StringBuilder src = new StringBuilder();
        src.append("package io.shulie.takin.web.biz.service.pressureresource.common.dy;\n");
        StringBuilder body = new StringBuilder();
        body.append("public class DyClass {\n");
        body.append("public String dyMethod() {\n");
        for (int i = 0; i < values.length; i++) {
            if (values[i].trim().startsWith("import")) {
                src.append(values[i]).append("\n");
            } else {
                body.append(values[i]).append("\n");
            }
        }
        body.append("}\n");
        body.append("}\n");
        src.append(body);
        StringObject so = null;
        try {
            String fullName = "io.shulie.takin.web.biz.service.pressureresource.common.dy.DyClass";
            so = new StringObject(fullName, src.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return so;

    }
}