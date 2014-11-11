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
         "(ROOT (S (NP (PRP He)) (VP (VBD served) (NP (NP (CD three) (NNS terms)) (VP (VBG representing) (NP (NP (DT the) (NAC (JJ 13th) (NNP District) (PP (IN in) (NP (DT the) (NNP Illinois)))) (NNP Senate)) (PP (IN from) (NP (CD 1997) (TO to) (CD 2004)))))) (, ,) (S (VP (VBG running) (ADVP (RB unsuccessfully)) (PP (IN for) (NP (NP (DT the) (NNP United) (NNPS States) (NNP House)) (PP (IN of) (NP (NP (NNS Representatives)) (PP (IN in) (NP (CD 2000)))))))))) (. .)))",
         "(ROOT (S (NP (DT This)) (VP (MD will) (VP (VB help) (S (NP (DT the) (NN firm)) (VP (VBP determine) (NP (NP (NP (NP (DT the) (NN size)) (PP (IN of) (NP (DT the) (NN job)))) (VP (VBN based) (PP (IN on) (NP (NP (JJ numerical) (NNS data)) (PP (IN for) (NP (NN example))))))) (, ,) (NP (NP (DT the) (NN size)) (PP (IN of) (NP (NP (DT the) (NN revenue) (NN number)) (, ,) (NP (NP (DT the) (JJ net) (NN loss)) (CC or) (NP (NP (JJ net) (NN income) (NN trend)) (PP (IN from) (NP (JJ previous) (NNS years))) (, ,) (NP (FW etc)))))))))))) (. .)))"]

tokenized_sentences = ["Barack Hussein Obama II is the 44th and current President of the United States , in office since 2009 .",
                       u"He is the first African American to hold the office .",
                       u"Born in Honolulu , Hawaii , Obama is a graduate of Columbia University and Harvard Law School , where he was president of the Harvard Law Review .",
                       u"He was a community organizer in Chicago before earning his law degree .",
                       u"He worked as a civil rights attorney in Chicago and taught constitutional law at the University of Chicago Law School from 1992 to 2004 .",
                       u"He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004 , running unsuccessfully for the United States House of Representatives in 2000 ."]

arbitrary_text = u"Barack Hussein Obama II is the 44th and current President of the United States, in office since 2009.  He is the first African American to hold the office.  Born in Honolulu, Hawaii, Obama is a graduate of Columbia University and Harvard Law School, where he was president of the Harvard Law Review.  He was a community organizer in Chicago before earning his law degree.  He worked as a civil rights attorney in Chicago and taught constitutional law at the University of Chicago Law School from 1992 to 2004.  He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004, running unsuccessfully for the United States House of Representatives in 2000."


transport = TSocket.TSocket(server, port)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = StanfordCoreNLP.Client(protocol)

transport.open()

try:
    result = client.resolve_coreferences_in_trees(trees)
    for r in result:
        print r
    print
    result = client.resolve_coreferences_in_tokenized_sentences(tokenized_sentences)
    for r in result:
        print r
    print
    result = client.resolve_coreferences_in_text(arbitrary_text)
    for r in result:
        print r

except Exception as e:
    print e

transport.close()
