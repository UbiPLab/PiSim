#本项目为作品TA

## 配置
src\main\resources\application.properties 

文件指定了项目运行端口为6688，运行项目时可以根据实际情况修改

src\main\resources\application.yml

文件指定了数据库链接配置，默认链接云端数据库，运行项目时可以根据实际情况修改

## 运行
### 终端运行
参数一为打包可执行文件路径，参数二为双线性配置文件，参数三为参数文件。运行命令如下:

```java -jar .\ta-0.3.0.jar .\config\pairing.properties .\config\params.properties ```

命令末尾附加 --server.port=6688 可以指定TA的运行端口

### IDE运行
在src\main\java\com\example\secureserver\SecureserverApplication.java文件中注释掉从终端获取参数的方式，直接在代码中指定参数，然后运行