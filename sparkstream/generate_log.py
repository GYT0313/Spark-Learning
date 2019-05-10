# -*- coding:utf-8 -*-

import random
import time

"""
Pyhotn 日志产生器
date+target_web+ip+search_engine+status_code
"""

url_paths = [
    "class/112.html",
    "class/128.html",
    "class/145.html",
    "class/116.html",
    "class/131.html",
    "class/130.html",
    "learn/821",
    "course/list"
]

http_referers = [
    "http://www.baidu.com/s?wd={query}",
    "https://www.sogou.com/web?query={query}",
    "http://cn.bing.com/search?q={query}",
    "https://search.yahoo.com/search?p={query}",
    "https://www.google.com.hk/search?&q={query}",
    "https://www.so.com/s?&q={query}"
]

search_keyword = [
    "Spark SQL实战",
    "Hadoop基础",
    "Storm实战",
    "Spark Streaming实战",
    "大数据面试"
]

ip_slices = [132,156,124,10,12,7,8,9,10,19,36,33,29,167,66,67,88,143,187,30,46,50,87,94]

status_codes = ["200", "404", "500"]

# url
def sample_url():
    return random.sample(url_paths, 1)[0]

# ip
def sample_ip():
    slice = random.sample(ip_slices, 4)
    return ".".join([str(item) for item in slice])

# referer
def sample_referer():
    if random.uniform(0, 1) > 0.4:
        return "-"
    refer_str = random.sample(http_referers, 1)
    query_str = random.sample(search_keyword, 1)
    return refer_str[0].format(query=query_str[0])

# status_code
def sample_status_code():
    return random.sample(status_codes, 1)[0]

def generate_log(count = 10):
    time_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())

    f = open("/home/hadoop/data/project/logs/access.log", "w+")

    while count >= 1:
        query_log = "{localtime}\t{url}\t{ip}\t{referer}\t{status_codes}".format(\
            localtime=time_str, url=sample_url(), ip=sample_ip(), \
            referer=sample_referer(), status_codes=sample_status_code())
        f.write(query_log + "\n")
        count -= 1

def main():
    generate_log(100)


if __name__ == '__main__':
    main()