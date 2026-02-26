FROM har.gagogroup.cn/base-software/amazoncorretto:11

ENV TZ=Asia/Shanghai

COPY snowy-web-app/target/snowy-web-app-2.0.0.jar /gago-sonwy.jar

ENTRYPOINT ["java","-Xmx2G","-Xms1G","-jar","gago-sonwy.jar"]
