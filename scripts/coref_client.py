#!/usr/bin/env python

from corenlp import StanfordCoreNLP
from corenlp.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

#from bs4 import UnicodeDammit
#import re
import sys


# get command line arguments
args = sys.argv[1:]
if len(args) != 2:
    sys.stderr.write('Usage: coref_client.py <server> <port>\n')
    sys.exit(2)
else:
    server = args[0]
    port = int(args[1])


trees = ["(ROOT (S (NP (NNP Barack) (NNP Hussein) (NNP Obama) (NNP II)) (VP (VBZ is) (NP (NP (DT the) (JJ 44th) (CC and) (JJ current) (NN President)) (PP (IN of) (NP (DT the) (NNP United) (NNPS States)))) (, ,) (PP (IN in) (NP (NP (NN office)) (PP (IN since) (NP (CD 2009)))))) (. .)))",
         "(ROOT (S (NP (PRP He)) (VP (VBZ is) (NP (DT the) (JJ first) (NNP African) (NNP American)) (S (VP (TO to) (VP (VB hold) (NP (DT the) (NN office)))))) (. .)))",
         "(ROOT (S (S (VP (VBN Born) (PP (IN in) (NP (NNP Honolulu) (, ,) (NNP Hawaii))))) (, ,) (NP (NNP Obama)) (VP (VBZ is) (NP (NP (DT a) (NN graduate)) (PP (IN of) (NP (NP (NNP Columbia) (NNP University)) (CC and) (NP (NNP Harvard) (NNP Law) (NNP School))))) (, ,) (SBAR (WHADVP (WRB where)) (S (NP (PRP he)) (VP (VBD was) (NP (NP (NN president)) (PP (IN of) (NP (DT the) (NNP Harvard) (NNP Law) (NNP Review)))))))) (. .)))",
         "(ROOT (S (NP (PRP He)) (VP (VBD was) (NP (NP (DT a) (NN community) (NN organizer)) (PP (IN in) (NP (NNP Chicago)))) (PP (IN before) (S (VP (VBG earning) (NP (PRP$ his) (NN law) (NN degree)))))) (. .)))",
         "(ROOT (S (NP (PRP He)) (VP (VP (VBD worked) (PP (IN as) (NP (NP (DT a) (JJ civil) (NNS rights) (NN attorney)) (PP (IN in) (NP (NNP Chicago)))))) (CC and) (VP (VBD taught) (NP (JJ constitutional) (NN law)) (PP (IN at) (NP (NP (DT the) (NNP University)) (PP (IN of) (NP (NP (NNP Chicago) (NNP Law) (NNP School)) (PP (IN from) (NP (CD 1992))))))) (PP (TO to) (NP (CD 2004))))) (. .)))",
         "(ROOT (S (NP (PRP He)) (VP (VBD served) (NP (NP (CD three) (NNS terms)) (VP (VBG representing) (NP (NP (DT the) (NAC (JJ 13th) (NNP District) (PP (IN in) (NP (DT the) (NNP Illinois)))) (NNP Senate)) (PP (IN from) (NP (CD 1997) (TO to) (CD 2004)))))) (, ,) (S (VP (VBG running) (ADVP (RB unsuccessfully)) (PP (IN for) (NP (NP (DT the) (NNP United) (NNPS States) (NNP House)) (PP (IN of) (NP (NP (NNS Representatives)) (PP (IN in) (NP (CD 2000)))))))))) (. .)))"]

#text = "My name is Diane and I live in New Jersey.  I sometimes go to New York.  The Food and Drug Administration is an organization."
more_trees = ["(ROOT (S (S (NP (PRP$ My) (NN name)) (VP (VBZ is) (NP (NNP Diane)))) (CC and) (S (NP (PRP I)) (VP (VBP live) (PP (IN in) (NP (NNP New) (NNP Jersey))))) (. .)))",
         "(ROOT (S (NP (PRP I)) (ADVP (RB sometimes)) (VP (VBP go) (PP (TO to) (NP (NNP New) (NNP York)))) (. .)))",
         "(ROOT (S (NP (DT The) (NNP Food) (CC and) (NNP Drug) (NNP Administration)) (VP (VBZ is) (NP (DT an) (NN organization))) (. .)))"]
#tokenized_sentences = [["My", "name", "is", "Diane", "and", "I", "live", "in", "New", "Jersey", "."], 
#                       ["I", "sometimes", "go", "to", "New", "York", "."],
#                       ["The", "Food", "and", "Drug", "Administration", "is", "an", "organization", "."]]

transport = TSocket.TSocket(server, port)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = StanfordCoreNLP.Client(protocol)

transport.open()

try:
    result = client.resolve_coreferences_in_trees(trees)
    print result
#    print
#    result = client.resolve_coreferences_in_trees(more_trees)
#    print result
#    print
#    for sentence in tokenized_sentences:
#        result = client.get_entities_from_tokens(sentence)
#        print result

except Exception as e:
    print e

transport.close()
