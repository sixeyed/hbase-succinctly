package com.sixeyed.succinctly.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

//Demonstrates using the Java API to connect to HBase.
//Sample code for Syncfusion's HBase Succinctly eBook.

//To try this out locally, the easiest way is with Docker. Install Docker and add a hosts entry
//pointing 'hbase' to the IP address for your container host. Then run a container from the public 
//image, and run the HBase shell script to insert test data:

// docker run -d -p 2181:2181 -p 60010:60010 -p 60000:60000  -p 60020:60020 -p 60030:60030  -p 8080:8080 -p 8085:8085  -p 9090:9090 -p 9095:9095  --name hbase -h hbase  sixeyed/hbase-succinctly
// docker exec -it hbase hbase shell /hbase-scripts/setup.hbsh

public class HBaseExamples {

    public static void main(String[] args) throws IOException, InterruptedException {

//to include logging, uncomment this line:        
//BasicConfigurator.configure();
        Configuration config = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(config);

        demonstrateGet(connection);
        demonstrateScanner(connection);
        demonstratePut(connection);
        demonstrateBatchPut(connection);

        connection.close();
    }

    private static void demonstrateGet(Connection connection) throws IOException {
        Table access_logs = connection.getTable(TableName.valueOf("access-logs"));
        Get get = new Get(Bytes.toBytes("elton|jericho|201511"));
        Result result = access_logs.get(get);
        printCells(result);
        //output - whole row:
        //t:1106 = 120
        //t:1107 = 650

        get = new Get(Bytes.toBytes("elton|jericho|201511"));
        get.addFamily(Bytes.toBytes("t"));
        result = access_logs.get(get);
        printCells(result);
        //output - single column family:
        //t:1106 = 120
        //t:1107 = 650

        get = new Get(Bytes.toBytes("elton|jericho|201511"));
        get.addColumn(Bytes.toBytes("t"), Bytes.toBytes("1106"));
        result = access_logs.get(get);
        printCells(result);
    }

    private static void demonstrateScanner(Connection connection) throws IOException {
        Table access_logs = connection.getTable(TableName.valueOf("access-logs"));
        Scan scan = new Scan(Bytes.toBytes("elton|jericho|201510"), Bytes.toBytes("elton|jericho|x"));
        ResultScanner scanner = access_logs.getScanner(scan);
        for (Result result : scanner) {
            printCells(result);
        }

        //output - two whole rows:
        //[elton|jericho|201510] t:2908 = 80
        //[elton|jericho|201511] t:1106 = 120
        //[elton|jericho|201511] t:1107 = 650
        //reset the Scan which was modified during the fetch:
        scan = new Scan(Bytes.toBytes("elton|jericho|201510"), Bytes.toBytes("elton|jericho|x"));
        scan.setFilter(new ValueFilter(CompareOp.EQUAL, new RegexStringComparator("[5-9][0-9]0")));
        scanner = access_logs.getScanner(scan);
        for (Result result : scanner) {
            printCells(result);
        }

        //output - one cell:
        //[elton|jericho|201511] t:1107 = 650
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(new QualifierFilter(CompareOp.EQUAL, new RegexStringComparator("[0-9]{2}0[7-8]")));
        filterList.addFilter(new ValueFilter(CompareOp.EQUAL, new RegexStringComparator("[0-9]0")));
        scan = new Scan(Bytes.toBytes("elton|jericho|201510"), Bytes.toBytes("elton|jericho|x"));
        scan.setFilter(filterList);
        scanner = access_logs.getScanner(scan);
        for (Result result : scanner) {
            printCells(result);
        }

        //output - two cells:
        //[elton|jericho|201510] t:2908 = 80
        //[elton|jericho|201511] t:1107 = 650
    }

    private static void demonstratePut(Connection connection) throws IOException {
        Table access_logs = connection.getTable(TableName.valueOf("access-logs"));
        Put log = new Put(Bytes.toBytes("elton|jericho|201511"));
        log.addColumn(Bytes.toBytes("t"), //family
                Bytes.toBytes("1621"),
                Bytes.toBytes("340"));
        access_logs.put(log);

        //result - updated cell value:
        //t:1621 = 120
        Put put = new Put(Bytes.toBytes("elton|jericho|201511"));
        put.addColumn(Bytes.toBytes("t"), //family
                Bytes.toBytes("1622"),
                Bytes.toBytes(0.1));
        access_logs.put(put);

        //result - updated cell value:
        //t:1622 = 120
        Put newLog = new Put(Bytes.toBytes("elton|jericho|201511"));
        log.addColumn(Bytes.toBytes("t"),
                Bytes.toBytes("1622"),
                Bytes.toBytes("100"));
        access_logs.checkAndPut(Bytes.toBytes("elton|jericho|201511"),
                Bytes.toBytes("t"), //family
                Bytes.toBytes("1621"),
                Bytes.toBytes("34000"),
                newLog);

        //result - not updated, checked value doesn't match
    }

    private static void demonstrateBatchPut(Connection connection) throws IOException, InterruptedException {
        List<Row> batch = new ArrayList<Row>();

        Put put1 = new Put(Bytes.toBytes("elton|jericho|201512"));
        put1.addColumn(Bytes.toBytes("t"),
                Bytes.toBytes("0109"),
                Bytes.toBytes("670"));
        batch.add(put1);

        Put put2 = new Put(Bytes.toBytes("elton|jericho|201601"));
        put2.addColumn(Bytes.toBytes("t"),
                Bytes.toBytes("0110"),
                Bytes.toBytes("110"));
        batch.add(put2);

        Put put3 = new Put(Bytes.toBytes("elton|jericho|201602"));
        put3.addColumn(Bytes.toBytes("t"),
                Bytes.toBytes("0206"),
                Bytes.toBytes("500"));
        batch.add(put3);

        Table access_logs = connection.getTable(TableName.valueOf("access-logs"));
        Object[] results = new Object[batch.size()];
        access_logs.batch(batch, results);
    }

    private static void printCells(Result result) {
        for (Cell cell : result.listCells()) {
            System.out.println("[" + Bytes.toString(CellUtil.cloneRow(cell)) + "] "
                    + Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                    + Bytes.toString(CellUtil.cloneQualifier(cell)) + " = "
                    + Bytes.toString(CellUtil.cloneValue(cell)));
        }
    }

}
