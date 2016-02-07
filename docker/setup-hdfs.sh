#! /bin/bash

service ssh start
$HADOOP_HOME/etc/hadoop/hadoop-env.sh
$HADOOP_HOME/sbin/start-dfs.sh
sleep 20s

$HADOOP_HOME/bin/hdfs dfs -mkdir /hbase

$HADOOP_HOME/sbin/stop-dfs.sh