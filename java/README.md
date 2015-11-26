Sample code for Syncfusion's HBase Succinctly eBook, Chapter 4 - Connecting with the Java API.

To try this out locally, the easiest way is with Docker. Install Docker and add a hosts entry
pointing 'hbase' to the IP address for your container host. 

Then run a container from the public image, and run the HBase shell script to insert test data:

* docker run -d -p 2181:2181 -p 60010:60010 -p 60000:60000  -p 60020:60020 -p 60030:60030  -p 8080:8080 -p 8085:8085  -p 9090:9090 -p 9095:9095  --name hbase -h hbase  sixeyed/hbase-succinctly

* docker exec -it hbase hbase shell /hbase-scripts/setup.hbsh
