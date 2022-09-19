package io.shulie.takin.web.biz.service.pressureresource.common.dy;

public class DynamicCompilerUtil {
    public static void main(String[] args) throws Exception {
        check("import  com.example.demo.entity.User ;\nUser user = new User();\nuser.setName(\"挡板\");\nreturn user ;");
    }

    public static String check(String source) throws ClassNotFoundException {
        String[] values = source.split("\n");
        StringBuilder src = new StringBuilder();
        src.append("package io.shulie.remote.compiler;\n");
        StringBuilder body = new StringBuilder();
        body.append("public class DynaClass {\n");
        body.append("public String dyMethod() {\n");
        for (int i = 0; i < values.length; i++) {
            if (values[i].trim().startsWith("import")) {
                //src.append(values[i]).append("\n");
            } else {
                body.append(values[i]).append("\n");
            }
        }
        body.append("}\n");
        body.append("}\n");
        src.append(body);
        String fullName = "io.shulie.remote.compiler.DynaClass";
        boolean result = DynamicCompiler.compile(fullName, src.toString());
        if (result) {

        } else {
            Class.forName(fullName);
        }
        return "xx";
    }
}