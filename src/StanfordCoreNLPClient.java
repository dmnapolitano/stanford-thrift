// Generated code
import CoreNLP.*;

import java.io.InputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.List;


import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class StanfordCoreNLPClient {

    public static void main(String [] args) {

        String server = "";
        Integer port = 0;
        String inputFilename = "";

        if (args.length == 3) {
            server = args[0];
            port = Integer.parseInt(args[1]);
            inputFilename = args[2];
        }
        else {
            System.err.println("Usage: StanfordCoreNLPClient <server> <port> <inputfile>");
            System.exit(2);
        }

        try {
            TTransport transport;
            transport = new TSocket(server, port);
            transport.open();

            TProtocol protocol = new  TBinaryProtocol(transport);
            StanfordCoreNLP.Client client = new StanfordCoreNLP.Client(protocol);

            perform(client, inputFilename);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(StanfordCoreNLP.Client client, String inputFilename) throws TException
    {
        FileReader infile = null;

        try {
            infile = new FileReader(inputFilename);
            BufferedReader in = new BufferedReader(infile);
            while (in.ready()) {
                String sentence = in.readLine();
                List<ParseTree> trees = client.parse_text(sentence, null);
                for (ParseTree tree : trees)
                {
                    System.out.println(tree.tree);
                }
            }
            in.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
    }
}
