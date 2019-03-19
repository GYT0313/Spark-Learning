from pyspark import SparkContext
from pyspark.streaming import StreamingContext


# 创建一个sc   和时间间隔为4s 的ssc
sc = SparkContext("spark://master:7077", "AccumulatorsBroadcastCheckpoint")
ssc = StreamingContext(sc, 4)


# 广播变量，用与筛选列表
def getWordBlacklist(sparkContext):
    if ("wordBlacklist" not in globals()): # 全局变量是否包含
        globals()["wordBlacklist"] = sparkContext.broadcast(["a", "b", "c"])
    return globals()["wordBlacklist"]

# 累加器用于计数筛选掉的数量
def getDroppedWordsCounter(sparkContext):
    if ("droppedWordCounter" not in globals()):
        globals()["droppedWordCounter"] = sparkContext.accumulator(0)
    return globals()["droppedWordCounter"]


def echo(time, rdd):
    # 创建或获取广播变量
    blacklist = getWordBlacklist(rdd.context)
    # 创建或获取累加器
    droppedWordCounter = getDroppedWordsCounter(rdd.context)

    def filterFunc(wordCount):
        # 如果该单词在筛选列表中，则累加器求和
        if wordCount[0] in blacklist.value:
            droppedWordCounter.add(wordCount[1])
            False
        else:
            True
    # 过滤，必须调用collect() 行为操作
    rdd.filter(filterFunc).collect()
    # 输出累加器
    print('droppedWordCounter = %s' % droppedWordCounter)


# 创建DStream，并读取套接字
lines = ssc.socketTextStream("master", 9999)

# 单词计数
words = lines.flatMap(lambda line: line.split(" "))
pairs = words.map(lambda word: (word, 1))
wordCounts = pairs.reduceByKey(lambda x, y: x + y)

# 将每个RDD的前10个元素打印
wordCounts.foreachRDD(echo)

ssc.start() # 启动计算
ssc.awaitTermination() # 等待终止