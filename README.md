# cloud-sample

## 一、docker方式
### 打包docker镜像
```
cd pom
mvn docker:build
```
### docker镜像运行
```
docker run -d -e JAVA_OPTS="-Dspring.profiles.active=prod" -p 8080:8080 --net=host --name app -t spring.io/app
```

## jar包形式
### jar包启动
```
java -jar -Xms512m -Xmx512m -Dspring.profiles.active=prod -Dserver.port=8080 -Dlogging.path=logs/app app.jar &
```

### 配置刷新
```
curl http://localhost:8762/actuator/refresh
```
