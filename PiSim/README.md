#本项目为作品移动客户端

## 配置


src\main\java\parameter\IP.java

文件指定了TA及RSU的IP地址。可以根据具体情况修改

## 运行
### 本地运行
若要连接本地局域网内的TA及RSU，需要在文件
src\main\java\com\example\mygaode\MapActivity.java
第110行注释掉后台自动提交线程的报告。(该线程会持续在WiFi扫描状态和打开热点状态切换，因此无法稳定连接到局域网内)

此外需要在IP.java中配置本地TA的地址和RSU的地址

### 云端运行
若要连接云端的TA及RSU运行，可以直接连接手机并编译运行。可执行文件中提供编译好的应用安装包PiSim.APK文件