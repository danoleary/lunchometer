FROM openjdk:8-jre-alpine
COPY ./build/libs/lunchometer.jar /root/lunchometer.jar
COPY webapp /root/webapp
WORKDIR /root
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "lunchometer.jar"]
