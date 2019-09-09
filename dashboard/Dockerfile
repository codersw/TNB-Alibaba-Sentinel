# 基于openjdk8镜像
FROM openjdk:8-jdk-alpine
# 将本地文件夹挂在到当前容器
VOLUME /tmp
# 复制文件到容器
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
# 声明需要暴露的端口
EXPOSE 9000
# 配置容器启动后执行的命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]