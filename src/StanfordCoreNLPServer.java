import java.net.ServerSocket;
import java.net.InetAddress;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

// Generated code
import CoreNLP.*;

public class StanfordCoreNLPServer 
{
    public static class ServerThread implements Runnable 
    {
        private Integer port;

        public ServerThread(StanfordCoreNLP.Processor processor, Integer portNum) 
        {
            port = portNum;
        }

        public void run() 
        {
            try 
            {
                // Initialize the transport socket
                TServerTransport serverTransport = new TServerSocket(port);

                // There are a bunch of servers that one can use.
                // See https://github.com/m1ch1/mapkeeper/wiki/Thrift-Java-Servers-Compared for more details

                // Use this for a single-threaded server. Shouldn't really be used in production.
                // TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

                // Use this for a multithreaded server with the max number of workers set to 10 (to avoid taking over the server)
                // Default minimum number of workers for this server type is 5 which is fine.
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
                args.maxWorkerThreads(10);
                args.processor(processor);
                TServer server = new TThreadPoolServer(args);

                System.out.println("The CoreNLP server is running...");
                server.serve();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    public static StanfordCoreNLPHandler handler;

    public static StanfordCoreNLP.Processor processor;

    public static void main(String [] args) {

        Integer portNum = 0;
        String modelFile = "";

        if (args.length < 1 || args.length > 2) 
        {
            System.err.println("Usage: StanfordCoreNLPServer <port> [<modelFile>]");
            System.exit(2);
        }
        else if (args.length == 1) 
        {
            portNum = Integer.parseInt(args[0]);
        }
        else 
        {
            portNum = Integer.parseInt(args[0]);
            modelFile = args[1];
        }

        try 
        {
            //handler = new StanfordCoreNLPHandler(modelFile);
        	handler = new StanfordCoreNLPHandler();
            processor = new StanfordCoreNLP.Processor(handler);
            Runnable r = new ServerThread(processor, portNum);
            new Thread(r).start();
        } 
        catch (Exception x) 
        {
            x.printStackTrace();
        }
    }
}
