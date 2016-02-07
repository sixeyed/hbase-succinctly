#!/bin/bash

# Hadoop
$HADOOP_HOME/bin/start-servers.sh

# HBase servers:
$HBASE_HOME/bin/hbase-daemon.sh start zookeeper
$HBASE_HOME/bin/hbase-daemon.sh start regionserver
$HBASE_HOME/bin/hbase-daemon.sh start master

$HBASE_HOME/bin/hbase shell ./for-hbase-succinctly.hbsh
$HBASE_HOME/bin/hbase shell ./for-hive-succinctly.hbsh