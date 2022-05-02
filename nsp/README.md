#本项目为作品NSP

## 配置
src\main\java\com\pisim\nsp\parameterUtil\IP.java

文件指定了TA的IP地址，默认为云端地址。运行项目时可以根据具体情况修改

src\main\resources\application.properties 

文件指定了项目运行端口为6688，运行项目时可以根据实际情况修改

src\main\resources\application.yml

文件指定了数据库链接配置，默认链接云端数据库，运行项目时可以根据实际情况修改

## 运行
### 终端运行
参数一为打包可执行文件路径，参数二为双线性配置文件路径，参数三为TA的IP地址。运行命令如下:

```java -jar .\nsp-0.3.0.jar "http://192.168.31.215:6688/" ```

命令末尾附加 --server.port=6600 可以指定NSP的运行端口

### IDE运行
在src\main\java\com\pisim\nsp\NspApplication.java文件中注释掉从终端获取参数的方式，直接在代码中指定参数，然后运行