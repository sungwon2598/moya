FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test
FROM eclipse-temurin:21-jre
WORKDIR /app
# COPY 명령어 수정
COPY --from=builder /app/build/libs/moya-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="\
    -Xms128m \
    -Xmx256m \
    -XX:+UseSerialGC \
    -XX:MaxHeapFreeRatio=70 \
    -XX:MinHeapFreeRatio=40 \
    -XX:+UseStringDeduplication \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/heapdump.hprof"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]