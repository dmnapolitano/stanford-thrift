#!/usr/bin/env python

import sys

from stanfordparser import StanfordParser
from stanfordparser.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


# get command line arguments
args = sys.argv[1:]
if len(args) != 3:
    sys.stderr.write('Usage: StanfordParserClient.py <server> <port> <inputfile>\n')
    sys.exit(2)
else:
    server = args[0]
    port = int(args[1])
    infile = args[2]


try:

    # Make socket
    transport = TSocket.TSocket(server, port)

    # Buffering is critical. Raw sockets are very slow
    transport = TTransport.TBufferedTransport(transport)

    # Wrap in a protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)

    # Create a client to use the protocol encoder
    client = StanfordParser.Client(protocol)

    # Connect!
    transport.open()

    # parse each line of the file
    with open(infile, 'r') as f:
        for line in f:
            parse = client.parse_sentence(line)
            sys.stdout.write(parse + '\n')

    # Close!
    transport.close()

except Thrift.TException, tx:
    print '%s' % (tx.message)
