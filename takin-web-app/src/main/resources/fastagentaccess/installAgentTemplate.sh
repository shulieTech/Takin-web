#!/bin/bash

exit_check(){
    if  [ $? == 1 ];then
        echo "ERROR!...pleck check it and try again"
        exit 1
    else
        echo "ok...."
    fi
}

echo "开始删除旧的agent的包"
rm -rf simulator-agent.zip
rm -rf simulator-agent

DOWNLOAD_URL='{|downloadUrl|}'
echo "开始下载agent包，url: "$DOWNLOAD_URL

TEST_DOWNLOAD=$( curl $DOWNLOAD_URL 2>&1)
if [[ $TEST_DOWNLOAD == *"0000_0100_0001"* ]]; then
	echo "agent包下载失败，退出进程。"
	rm -rf simulator-agent.zip
	exit 1
fi

DOWNLOAD_RESULT=$( curl -# --output simulator-agent.zip $DOWNLOAD_URL 2>&1)
echo $DOWNLOAD_RESULT
if [[ $DOWNLOAD_RESULT == *"100.0%"* ]]; then
	echo 'agent包下载成功。'
else
	echo "agent包下载失败，退出进程。"
	rm -rf simulator-agent.zip
	exit 1
fi

echo "开始解压agent包"
unzip -d ./simulator-agent/ -o simulator-agent.zip -x __MACOSX/*
exit_check

echo "开始生成启动参数"
PROJECT_NAME={|projectName|}
echo "项目名称: "$PROJECT_NAME
CURRENT_PATH=$(pwd)
echo "当前路径: "$CURRENT_PATH

echo "删除startLinkAgent.txt"
rm -rf startLinkAgent.txt
exit_check

echo "获取java版本"
JAVA_VERSION=$(java -version 2>&1 |awk 'NR==1{gsub(/"/,"");print $3}');
if [ -z "$JAVA_VERSION" ]; then
	echo '无法获取java版本,请手动输入JDK版本对应的版本\n 1.5.0: JDK1.5\n 1.6.0: JDK1.6\n 1.7.0: JDK1.7\n 1.8.0: JDK1.8\n 9.0: JDK9\n 10.0: JDK10\n 11.0: JDK11\n 12.0: JDK12\n 13.0: JDK13'
	read javaVersion
	JAVA_VERSION=$javaVersion
fi

#echo 'java 版本:'$javaVersion
#echo 'java 版本:'$JAVA_VERSION
echo "根据不同的jdk将启动参数输出到startLinkAgent.txt中"
if [[ $JAVA_VERSION == *"1.5."* ]] || [[ $JAVA_VERSION == *"1.6."* ]] || [[ $JAVA_VERSION == *"1.7."* ]]; then
	echo 'JDK版本: '$JAVA_VERSION
	echo '-XX:PermSize=256M -XX:MaxPermSize=512M -Xbootclasspath/a:'$JAVA_HOME'/lib/tools.jar -javaagent:'$CURRENT_PATH'/simulator-agent/bootstrap/transmittable-thread-local-2.12.1.jar -javaagent:'$CURRENT_PATH'/simulator-agent/simulator-launcher-instrument.jar -Dpradar.project.name='$PROJECT_NAME' -Djdk.attach.allowAttachSelf=true' >> startLinkAgent.txt
elif [[ $JAVA_VERSION == *"1.8."* ]]; then
	echo 'JDK版本: '$JAVA_VERSION
	echo '-XX:MetaspaceSize=512M -XX:MaxMetaspaceSize=512M -Xbootclasspath/a:'$JAVA_HOME'/lib/tools.jar -javaagent:'$CURRENT_PATH'/simulator-agent/bootstrap/transmittable-thread-local-2.12.1.jar -javaagent:'$CURRENT_PATH'/simulator-agent/simulator-launcher-instrument.jar -Dpradar.project.name='$PROJECT_NAME' -Djdk.attach.allowAttachSelf=true' >> startLinkAgent.txt
elif [[ $JAVA_VERSION == *"9."* ]] || [[ $JAVA_VERSION == *"10."* ]] || [[ $JAVA_VERSION == *"11."* ]] || [[ $JAVA_VERSION == *"12."* ]] || [[ $JAVA_VERSION == *"13."* ]]; then
	echo 'JDK版本: '$JAVA_VERSION
	echo '--add-exports=java.base/jdk.internal.loader=ALL-UNNAMED --add-exports=java.base/jdk.internal.module=ALL-UNNAMED --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED -XX:MetaspaceSize=512M -XX:MaxMetaspaceSize=512M -Xbootclasspath/a:'$JAVA_HOME'/lib/tools.jar -javaagent:'$CURRENT_PATH'/simulator-agent/bootstrap/transmittable-thread-local-2.12.1.jar -javaagent:'$CURRENT_PATH'/simulator-agent/simulator-launcher-instrument.jar -Dpradar.project.name='$PROJECT_NAME' -Djdk.attach.allowAttachSelf=true -Dsimulator.use.premain=true' >> startLinkAgent.txt
fi

echo "请使用以下启动参数如下"
cat startLinkAgent.txt
echo "
注意：
1、如果使用jdk1.8并且使用默认的垃圾回收器则需要显示配置-XX:SurvivorRatio=8 -XX:-UseAdaptiveSizePolicy，因为 jdk8默认垃圾回收器使用UseParallelGC，该垃圾回收器默认启动了 AdaptiveSizePolicy，即会根据预期和实际动态调整Survivor、eden 和 old 的比例，可能会造成非预期下的 fullgc 问题.
2、如果使用了skyWalking，确保skyWalking版本不低于8.1.0，并且需要添加启动参数-Dskywalking.agent.is_cache_enhanced_class=true -Dskywalking.agent.class_cache_mode=MEMORY
如果内存紧张则可使用-Dskywalking.agent.is_cache_enhanced_class=true -Dskywalking.agent.class_cache_mode=FILE，即文件缓存"
#echo '启动参数写入完毕，请查看startLinkAgent.txt'
