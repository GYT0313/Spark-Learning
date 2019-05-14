# 🌟Spark
学习Spark时所有代码，包括示例等。

+ spark_streaming

Python版Spark Streaming 练习代码
+ sparkstream

IDEA完整项目：Scala版Spark Streaming 练习代码
+ sparkstream_web

构建Spring Boot项目可视化sparkstream项目中实战项目写入HBase的处理数据
+ streaming_study_notes

包括Flume、Kafka、Spark Streaming的所有配置代码、创建、启动命令笔记
+ lib

包含了sparkstream和sparkstream_web的打包后的JAR包。
运行示例：

运行sparkstream-1.0-SNAPSHOT.jar 下的sparkstream-1.0-SNAPSHOT.jar
1. 先启动flume -- 笔记可以在streaming_study_notes/project_streaming_flume.txt找到
```
# 运行
bin/flume-ng agent \
--name simple-agent \
--conf $FLUME_HOME/conf \
--conf-file $FLUME_HOME/conf/flume_push_streaming.conf \
-Dflume.root.logger=INFO,console
```

2. 启动spark
```
# 提交到服务器
spark-submit \
--class com.gyt.sparkstream.FlumePushWordCount \
--master local[2] \
--packages org.apache.spark:spark-streaming-flume_2.11:2.3.2 \
/home/hadoop/lib/sparkstream-1.0-SNAPSHOT.jar \
master 41414
```

3. telnet 测试
```
hadoop@master:~/flume-1.9.0$ telnet master 44444
Trying 10.0.2.12...
Connected to master.
Escape character is '^]'.
hello spark
OK
hello flume
OK
```

4. 结果
```
-------------------------------------------
Time: 1557801168000 ms
-------------------------------------------
(hello,2)
(spark,1)
(flume,1)
```

+ data

sparkstream项目下实战项目的Python日志生成代码和Flume配置文件
