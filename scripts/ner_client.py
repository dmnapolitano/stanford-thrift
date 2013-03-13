#!/usr/bin/env python

from corenlp import StanfordCoreNLP
from corenlp.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

from bs4 import UnicodeDammit
import re
import sys


# get command line arguments
args = sys.argv[1:]
if len(args) != 2:
    sys.stderr.write('Usage: ner_client.py <server> <port>\n')
    sys.exit(2)
else:
    server = args[0]
    port = int(args[1])


# for testing named entity systems
text = "My name is Diane and I live in New Jersey.  I sometimes go to New York.  The Food and Drug Administration is an organization."
trees = ["(ROOT (S (S (NP (PRP$ My) (NN name)) (VP (VBZ is) (NP (NNP Diane)))) (CC and) (S (NP (PRP I)) (VP (VBP live) (PP (IN in) (NP (NNP New) (NNP Jersey))))) (. .)))",
         "(ROOT (S (NP (PRP I)) (ADVP (RB sometimes)) (VP (VBP go) (PP (TO to) (NP (NNP New) (NNP York)))) (. .)))",
         "(ROOT (S (NP (DT The) (NNP Food) (CC and) (NNP Drug) (NNP Administration)) (VP (VBZ is) (NP (DT an) (NN organization))) (. .)))"]
tokenized_sentences = [["My", "name", "is", "Diane", "and", "I", "live", "in", "New", "Jersey", "."], 
                       ["I", "sometimes", "go", "to", "New", "York", "."],
                       ["The", "Food", "and", "Drug", "Administration", "is", "an", "organization", "."]]

transport = TSocket.TSocket(server, port)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = StanfordCoreNLP.Client(protocol)

transport.open()


try:
    result = client.get_entities_from_text(text)
    print result
    print
    result = client.get_entities_from_trees(trees)
    print result
    print
    for sentence in tokenized_sentences:
        result = client.get_entities_from_tokens(sentence)
        print result

except Exception as e:
    print e

transport.close()