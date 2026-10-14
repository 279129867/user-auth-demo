# 构建阶段：用 Maven 打包
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
# 使用阿里云 Maven 镜像，避免直连 repo.maven.apache.org 被国际带宽拖死
COPY settings.xml /root/.m2/settings.xml
COPY pom.xml .
# 先下载依赖，利用 Docker 层缓存加速
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段：精简 JRE 镜像
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 非 root 运行，安全加固
RUN addgroup -S app && adduser -S app -G app
COPY --from=builder /build/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
