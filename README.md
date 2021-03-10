# graduation
毕业设计后端代码

幼儿园共建共享绘本图书馆管理平台的设计与实现

项目框架：springboot

安全框架：shiro 

数据库：mysql

数据库连接：mybatis-plus

代码文档工具：Swagger2

相关技术栈：MapStruct，AOP

服务端口：2048

swagger地址：http://localhost:2048/swagger-ui.html#/

打包：用maven的package命令，jar包在gateway模块下的target文件夹中。部署Linux时将application.yml配成prod

Linux开放2048端口：      

firewall-cmd --zone=public --add-port=2048/tcp --permanent

firewall-cmd --reload



