FROM java:7
MAINTAINER Elton Stoneman <elton@sixeyed.com>

ENV JAVA_HOME=/usr

RUN mkdir /hbase-setup
WORKDIR /hbase-setup

ADD install-hbase.sh /hbase-setup/
RUN ./install-hbase.sh

ADD hbase-site.xml /opt/hbase/conf/hbase-site.xml
ADD start-hbase-all.sh /opt/hbase/bin/start-hbase-all.sh

RUN mkdir /hbase-scripts
ADD hbase-scripts /hbase-scripts

# for a list of all the HBase ports, see http://blog.cloudera.com/blog/2013/07/guide-to-using-apache-hbase-ports/ 

# Servers
EXPOSE 60000
EXPOSE 60020
EXPOSE 2181

# APIs
EXPOSE 8080
EXPOSE 9090

# Web UIs
EXPOSE 60010
EXPOSE 60030
EXPOSE 8085
EXPOSE 9095

WORKDIR /opt/hbase/bin

ENV PATH=$PATH:/opt/hbase/bin

# run in pseudo-distributed mode with Stargate & Thrift:
CMD /opt/hbase/bin/start-hbase-all.sh
