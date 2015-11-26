using Autofac;
using HBase.Stargate.Client.Api;
using HBase.Stargate.Client.Autofac;
using HBase.Stargate.Client.Models;
using System;
using System.Linq;

namespace Sixeyed.Succinctly.HBase.ConsoleApp
{
    class Program
    {
        static void Main(string[] args)
        {
            var stargate = BuildStargate();
            Console.WriteLine("Starting...");

            DemonstrateReading(stargate);
            DemonstrateScanning(stargate);
            DemonstrateWriting(stargate);

            Console.WriteLine("Done.");
            Console.ReadLine();
        }

        private static IStargate BuildStargate()
        {
            var stargateUrl = "http://127.0.0.1:8080";
            var builder = new ContainerBuilder();
            builder.RegisterModule(new StargateModule(new StargateOptions
            {
                ServerUrl = stargateUrl
            }));

            var container = builder.Build();
            var stargate = container.Resolve<IStargate>();
            return stargate;
        }

        private static void DemonstrateReading(IStargate stargate)
        {
            // reading data for one row:
            var value = stargate.ReadValue("access-logs", "elton|jericho|201511", "t", "1106");
            Console.WriteLine("Single cell value: " + value);

            var cellSet = stargate.FindCells("access-logs", "elton|jericho|201511");
            value = cellSet.First(x => x.Identifier.CellDescriptor.Qualifier == "1106").Value;
            Console.WriteLine("LINQed cell value: " + value);
        }

        private static void DemonstrateScanning(IStargate stargate)
        {
            // scanning rows:
            var options = new ScannerOptions
            {
                TableName = "access-logs",
                StartRow = "elton|jericho|201510",
                StopRow = "elton|jericho|x",
            };
            var scanner = stargate.CreateScanner(options);
            var totalUsage = 0;
            while (scanner.MoveNext())
            {
                var cells = scanner.Current;
                foreach (var cell in cells)
                {
                    totalUsage += int.Parse(cell.Value);
                }
            }
            Console.WriteLine("Sum from row scan: " + totalUsage);

            // scanning with a filter:
            options = new ScannerOptions
            {
                TableName = "access-logs",
                StartRow = "elton|jericho|201510",
                StopRow = "elton|jericho|x",
                Filter = new ColumnPrefixFilter("11")
            };
            scanner = stargate.CreateScanner(options);
            totalUsage = 0;
            while (scanner.MoveNext())
            {
                var cells = scanner.Current;
                foreach (var cell in cells)
                {
                    totalUsage += int.Parse(cell.Value);
                }
            }
            Console.WriteLine("Sum from filtered row scan: " + totalUsage);
        }

        private static void DemonstrateWriting(IStargate stargate)
        {
            // update a single cell value:
            stargate.WriteValue("100", "access-logs", "elton|jericho|201510", "t", "2908");
            Console.WriteLine("Updated single cell");

            // set multiple cell values:
            var update = new Cell(new Identifier
            {
                Row = "elton|jericho|201510",
                CellDescriptor = new HBaseCellDescriptor
                {
                    Column = "t",
                    Qualifier = "2908"
                }
            }, "120");

            var insert = new Cell(new Identifier
            {
                Row = "elijah|jericho|201511",
                CellDescriptor = new HBaseCellDescriptor
                {
                    Column = "t",
                    Qualifier = "1117"
                }
            }, "360");

            var cells = new CellSet(new Cell[] { update, insert });
            cells.Table = "access-logs";

            stargate.WriteCells(cells);
            Console.WriteLine("Updated multiple cells");
        }
    }
}
