from pyspark import SparkContext
from pyspark.streaming import StreamingContext

# 创建一个sc（使用集群）   和时间间隔为4s 的ssc
sc = SparkContext("spark://master:7077", "ReadFromFile")
ssc = StreamingContext(sc, 5)



lines = ssc.textFileStream("hdfs://master:9000/user/hadoop/spark_data/")
lines.pprint()
'''
# 单词计数
words = lines.flatMap(lambda line: line.split(" "))
pairs = words.map(lambda word: (word, 1))
wordCounts = pairs.reduceByKey(lambda x, y: x + y)

# 将每个RDD的前10个元素打印
wordCounts.pprint()
'''

ssc.start() # 启动计算


ssc.awaitTermination() # 等待终止