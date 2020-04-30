# cloud-sample

### 打包docker镜像
```
cd pom
mvn docker:build
```
### 传递镜像java变量
```
docker run -d -e JAVA_OPTS="-Dspring.profiles.active=prod" -p 8080:8080 --net=host --name app -t spring.io/app
```