package io.shulie.takin.web.biz.service.pressureresource.common.dy;

import javax.tools.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DynamicCompiler {
    private static JavaFileManager fileManager;

    public DynamicCompiler() {
        this.fileManager = initManger();
    }

    private JavaFileManager initManger() {
        if (fileManager != null) {
            return fileManager;
        } else {
            JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
            fileManager = new ClassFileManager(javaCompiler.getStandardFileManager(diagnosticCollector, null, null));
            return fileManager;
        }
    }

    /**
     * 编译源码并加载，获取Class对象
     *
     * @param fullName
     * @param sourceCode
     * @return
     * @throws ClassNotFoundException
     */
    public Class compileAndLoad(String fullName, String sourceCode) throws ClassNotFoundException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> javaFileObjectList = new ArrayList<JavaFileObject>();
        javaFileObjectList.add(new CharSequenceJavaFileObject(fullName, sourceCode));
        boolean result = javaCompiler.getTask(null, fileManager, null, null, null, javaFileObjectList).call();
        if (result) {
            return this.fileManager.getClassLoader(null).loadClass(fullName);
        } else {
            return Class.forName(fullName);
        }
    }

    /**
     * 编译源码并加载，获取Class对象
     *
     * @param fullName
     * @param sourceCode
     * @return
     * @throws ClassNotFoundException
     */
    public static boolean compile(String fullName, String sourceCode) throws ClassNotFoundException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> javaFileObjectList = new ArrayList<JavaFileObject>();
        javaFileObjectList.add(new CharSequenceJavaFileObject(fullName, sourceCode));
        boolean result = javaCompiler.getTask(null, fileManager, null, null, null, javaFileObjectList).call();
        return result;
    }

    /**
     * 关闭fileManager
     *
     * @throws IOException
     */
    public void closeFileManager() throws IOException {
        this.fileManager.close();
    }

}