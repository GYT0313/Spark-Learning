package com.gyt.utils;

import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Map;

/**
 * Hbase操作工具类
 */

public class HBaseUtils {

    HBaseAdmin admin = null;
    Configuration conf = null;

    private HBaseUtils() {
        conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "master:2181,slave1:2181," +
                "slave2:2181");
        conf.set("hbase.rootdir", "hdfs://master:9000/hbase");

        try {
            admin = new HBaseAdmin(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HBaseUtils instance = null;
    public static synchronized HBaseUtils getInstance() {
        if (null == instance) {
            instance = new HBaseUtils();
        }
        return  instance;
    }

    public HTable getTable(String tableName) {
        HTable table = null;

        try {
            table = new HTable(conf, tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * 添加一条记录到Hbase
     * @param tableName 表名
     * @param rowKey 行键
     * @param cf    列族
     * @param column    列
     * @param value 值
     */
    public void put(String tableName, String rowKey, String cf, String column, String value) {
        HTable table = getTable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(cf), Bytes.toBytes(column), Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Long> query(String tableName, String condition) throws Exception {
        Map<String, Long> map = new HashedMap();

        HTable table = getTable(tableName);
        String cf = "info";
        String qualifier = "click_count";

        Scan scan = new Scan();
        Filter filter = new PrefixFilter(condition.getBytes());
        scan.setFilter(filter);

        ResultScanner rs = table.getScanner(scan);

        for (Result result : rs) {
            String rk = Bytes.toString(result.getRow());
            Long value = Bytes.toLong(result.getValue(cf.getBytes(), qualifier.getBytes()));
            map.put(rk, value);
        }

        return  map;
    }








    public static void main(String[] args) throws Exception {
//        HTable table = HBaseUtils.getInstance().getTable("course_clickcount");
//        System.out.println(table.getName().getNameAsString());
//        HBaseUtils.getInstance().put("course_clickcount", "20191111_1",
//                "info", "click_count", "5");

        Map<String, Long> map = HBaseUtils.getInstance().query("course_clickcount", "20190511");

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }


    }

}