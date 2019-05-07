from pyspark import SparkContext
from pyspark.streaming import StreamingContext

def functionToCreateContext():
    # 创建一个sc   和时间间隔为4s 的ssc
    sc = SparkContext("spark://master:7077", "NetworkWordCountAndWindow")
    ssc = StreamingContext(sc, 8)
    # 设置检查的目录
    ssc.checkpoint("hdfs://master:9000/tmp/spark/checkpoint")

    # 创建DStream，并读取套接字
    lines = ssc.socketTextStream("master", 9999)

    # 单词计数
    words = lines.flatMap(lambda line: line.split(" "))
    pairs = words.map(lambda word: (word, 1))
    #wordCounts = pairs.reduceByKey(lambda x, y: x + y)
    # 设置逆函数，窗口和滑动
    wordCounts = pairs.reduceByKeyAndWindow(lambda x, y: x + y, lambda x, y: x - y, 24, 8)
    # 设置检查点的间隔（比如待会出现的报错就可能是因为某些
    # 数据未来得及被检查点保存，将会导致数据的丢失）
    # 官网建议：DStream的5-10个滑动间隔的检查点间隔是一个很好的设置。
    wordCounts.checkpoint(16)
    # 将每个RDD的前10个元素打印
    wordCounts.pprint()

    return ssc

# 如果存在检查的目录则读取，没有则新建
ssc = StreamingContext.getOrCreate("hdfs://master:9000/tmp/spark/checkpoint", functionToCreateContext)


ssc.start() # 启动计算
ssc.awaitTermination() # 等待终止