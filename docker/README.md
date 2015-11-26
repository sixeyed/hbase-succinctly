HBase running in pseudo-distributed mode, with the REST and Thrift APIs started.

Usage, with all HBase ports mapped:

* docker run -d -p 60010:60010 -p 60000:60000 -p 60020:60020 -p 60030:60030 -p 8080:8080 -p 8085:8085 -p 9090:9090 -p 9095:9095 --name hbase -h hbase sixeyed/hbase-succinctly

[Cloudera's blog post](http://blog.cloudera.com/blog/2013/07/guide-to-using-apache-hbase-ports/) tells you what all the ports do.

To connect with the Java API, you will need an entry in your hosts file, pointing 'hbase' to the container host IP, e.g. on localhost:

* 127.0.0.1	hbase

You can run HBase Shell by exec-ing into the running container:

* docker exec -it hbase hbase shell
