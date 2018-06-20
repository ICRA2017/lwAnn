FROM openjdk:7

RUN git clone https://github.com/ICRA2017/lwAnn.git

CMD ["java", "-jar", "lwAnn/bin/lwAnn.jar"]