# Takin组件
全链路压测-控制台，英文名：Takin-web

[![LICENSE](https://img.shields.io/github/license/pingcap/tidb.svg)](https://github.com/pingcap/tidb/blob/master/LICENSE)
[![Language](https://img.shields.io/badge/Language-Java-blue.svg)](https://www.java.com/)

# 项目简介
压测是指模拟超大负荷量测试软件系统在处于峰值时如何操作，例如模拟真实的软件和硬件环境和非正常的超多用户负荷，测试软件系统在长时间运行时的可靠性和响应时间。本公司产品在此基础之上引入了全链路压测概念，可以在生产环境对系统全链路进行全方位的压测，找出系统的性能风险问题和瓶颈水平，帮助测试人员提升系统稳定性
## 三个原则
- 一致性（Consistency）
- 隔离性（Isolation）
- 稳定性（Reliability）

## 应用领域
本产品适用于互联网、交通物流、新零售、电商、教育、金融、数字政务、医疗健康、游戏、制造、音视频等对软件性能有需求的行业。
目前在交通物流、新零售、教育等行业已有多家落地客户。

# 项目定位
-  全链路生产环境的应用性能监控
-  性能分析、精准定位、资源调优
-  应用性能管理
# 项目背景
全链路压测就好比是性能领域的核武器，保障企业IT生产的的稳定运行

# 环境依赖
- maven setting.xml配置 数列maven仓库
- Java version 1.8
- redis 本地
- mysql 可用公共服务
- influxdb 可用公共服务
- nginx 可用公共服务
```
sudo vim /etc/hosts
139.217.92.129 hadoop01
```

 
# 项目说明

## git说明
项目本身没有master分支，拉取代码后需要切换到当前版本分支

目前版本分支：4.2.3

## 项目本地启动

- idea配置 VM options配置如下
 ```
//takin-web.dylib：这个包可以询问同学～
-agentpath:/Users/xxxx/shulie/takin-web.dylib
-Xmx600m
-Xms600m
-Xmn600m
 ```
# 更新日志
- 5.0.2更新内容
- 5.0.1.2更新内容
- 5.0.1.1更新内容
- 5.0.1更新内容
- 5.0.0更新内容
  - 新增插件功能，




