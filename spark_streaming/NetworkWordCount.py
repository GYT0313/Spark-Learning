from pyspark import SparkContext
from pyspark.streaming import StreamingContext

# 创建一个sc（使用本地2个core）   和时间间隔为4s 的ssc
sc = SparkContext("spark://master:7077", "NetworkWordCount")
ssc = StreamingContext(sc, 4)

# 创建DStream，并读取套接字
lines = ssc.socketTextStream("master", 9999)

# 单词计数
words = lines.flatMap(lambda line: line.split(" "))
pairs = words.map(lambda word: (word, 1))
wordCounts = pairs.reduceByKey(lambda x, y: x + y)

# 将每个RDD的前10个元素打印
wordCounts.pprint()


ssc.start() # 启动计算
ssc.awaitTermination() # 等待终止
