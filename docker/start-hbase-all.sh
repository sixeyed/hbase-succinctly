#!/bin/bash

# Hadoop
service ssh start
$HADOOP_HOME/etc/hadoop/hadoop-env.sh
$HADOOP_HOME/sbin/start-dfs.sh

# HBase servers:
$HBASE_HOME/bin/hbase-daemon.sh start zookeeper
$HBASE_HOME/bin/hbase-daemon.sh start master
$HBASE_HOME/bin/hbase-daemon.sh start regionserver

# Thrift:
$HBASE_HOME/bin/hbase-daemon.sh start thrift

# Stargate:
$HBASE_HOME/bin/hbase rest start
