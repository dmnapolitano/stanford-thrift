package org.ets.research.nlp.stanford_thrift;
/*
  Apache Thrift Server for Stanford CoreNLP (stanford-thrift)
  Copyright (C) 2013 Diane M. Napolitano, Educational Testing Service
  
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation, version 2
  of the License.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;


// Generated code
import CoreNLP.*;

public class StanfordCoreNLPServer 
{
    public static class ServerThread implements Runnable 
    {
        private Integer port;
        private final int THREAD_POOL_SIZE = 10;

        @SuppressWarnings("rawtypes")
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
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
                args.maxWorkerThreads(THREAD_POOL_SIZE);
                args.processor(processor);
                args.executorService(new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE));
                TServer server = new TThreadPoolServer(args);
                
            	// From https://github.com/m1ch1/mapkeeper/blob/eb798bb94090c7366abc6b13142bf91e4ed5993b/stubjava/StubServer.java#L93
                /*TNonblockingServerTransport trans = new TNonblockingServerSocket(port);
                TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(trans);
                args.transportFactory(new TFramedTransport.Factory());
                args.protocolFactory(new TBinaryProtocol.Factory());
                args.processor(processor);
                args.selectorThreads(4);
                args.workerThreads(32);
                TServer server = new TThreadedSelectorServer(args);*/

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
    @SuppressWarnings("rawtypes")
	public static StanfordCoreNLP.Processor processor;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) 
    {
        Integer portNum = 0;
        String configFile = "";

        if (args.length != 2) 
        {
            System.err.println("Usage: StanfordCoreNLPServer <port> <config file>");
            System.exit(2);
        }
        else 
        {
            portNum = Integer.parseInt(args[0]);
            configFile = args[1];
        }

        org.apache.log4j.BasicConfigurator.configure();
        
        try 
        {
            handler = new StanfordCoreNLPHandler(configFile);
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
