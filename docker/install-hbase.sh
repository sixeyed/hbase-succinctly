#! /bin/bash

wget --quiet http://mirror.ox.ac.uk/sites/rsync.apache.org/hbase/1.1.3/hbase-1.1.3-bin.tar.gz
tar xzf hbase-1.1.3-bin.tar.gz -C /opt/
ln -s /opt/hbase-1.1.3 /opt/hbase
rm hbase-1.1.3-bin.tar.gz
