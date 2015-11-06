#! /bin/bash

wget --quiet http://mirrors.ukfast.co.uk/sites/ftp.apache.org/hbase/stable/hbase-1.1.2-bin.tar.gz
tar xzf hbase-1.1.2-bin.tar.gz -C /opt/
ln -s /opt/hbase-1.1.2 /opt/hbase

rm hbase-1.1.2-bin.tar.gz
