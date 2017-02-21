FROM hseeberger/scala-sbt

COPY . .

RUN sbt update assembly

EXPOSE 8081

CMD java -jar target/scala-2.11/cube-range-sum-assembly-1.0.jar

