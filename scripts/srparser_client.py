#!/usr/bin/env python
# -*- coding: utf-8 -*-


from corenlp import StanfordCoreNLP
from corenlp.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

import sys


# get command line arguments
args = sys.argv[1:]
if len(args) != 2:
    sys.stderr.write('Usage: srparser_client.py <server> <port>\n')
    sys.exit(2)
else:
    server = args[0]
    port = int(args[1])


# Taken from the English Wikipedia entry for "Fox". :)
#arbitrary_text = u"Members of about 37 species are referred to as foxes, of which only 12 species actually belong to the Vulpes genus of \"true foxes\".  By far the most common and widespread species of fox is the red fox (Vulpes vulpes), although various species are found on almost every continent.  The presence of fox-like carnivores all over the globe, together with their widespread reputation for cunning, has contributed to their appearance in popular culture and folklore in many societies around the world (see also Foxes in culture).  The hunting of foxes with packs of hounds, long an established pursuit in Europe, especially the British Isles, was exported by European settlers to various parts of the New World."

#tokenized_sentences = [u"Members of about 37 species are referred to as foxes , of which only 12 species actually belong to the Vulpes genus of `` true foxes '' .".split(" "),
#                       u"By far the most common and widespread species of fox is the red fox -LRB- Vulpes vulpes -RRB- , although various species are found on almost every continent .".split(" "),
#                       u"The presence of fox-like carnivores all over the globe , together with their widespread reputation for cunning , has contributed to their appearance in popular culture and folklore in many societies around the world -LRB- see also Foxes in culture -RRB- .".split(" "),
#                       u"The hunting of foxes with packs of hounds , long an established pursuit in Europe , especially the British Isles , was exported by European settlers to various parts of the New World .".split(" "),
#                       u"Barack Hussein Obama II is the 44th and current President of the United States , in office since 2009 .".split(" "),
#                       u"He is the first African American to hold the office .".split(" "),
#                       u"Born in Honolulu , Hawaii , Obama is a graduate of Columbia University and Harvard Law School , where he was president of the Harvard Law Review .".split(" "),
#                       u"He was a community organizer in Chicago before earning his law degree .".split(" "),
#                       u"He worked as a civil rights attorney in Chicago and taught constitutional law at the University of Chicago Law School from 1992 to 2004 .".split(" "),
#                       u"He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004 , running unsuccessfully for the United States House of Representatives in 2000 .".split(" ")]

#tokenized_sentence = u"Members of about 37 species are referred to as foxes , of which only 12 species actually belong to the Vulpes genus of `` true foxes '' .".split(" ")

tagged_sentence = u"Members/NNS of/IN about/IN 37/CD species/NNS are/VBP referred/VBN to/TO as/IN foxes/NNS ,/, of/IN which/WDT only/RB 12/CD species/NNS actually/RB belong/VBP to/TO the/DT Vulpes/NNP genus/NN of/IN ``/`` true/JJ foxes/NNS ''/'' ./."

test_tagged_sentence = u"Jane's/DT dog/NN will/MD come/VB too/RB ./."

#weird_sentence = [u'While', u'the', u'child', u'spends', u'about', u'five', u'hours', u'or', u'less', u'with', u'his', u'parents', u',', u'and', u'whenever', u'that', u'child', u'wants', u'to', u'go', u'out', u'he', u'will', u'most', u'probably', u'go', u'out', u'with', u'his', u'friends', u'which', u'are', u'his', u'classmates', u',', u'so', u'most', u'of', u'his', u'school', u'life', u'will', u'be', u'spent', u'with', u'his', u'classmates', u',', u'and', u'this', u'will', u'have', u'a', u'great', u'affect', u'on', u'his', u'personality', u'which', u'will', u'determine', u'the', u'way', u'the', u'child', u'will', u'react', u'towards', u'his', u'school', u'and', u'will', u'determine', u'how', u'he', u'will', u'use', u'his', u'life', u'.']

#ahs_test = "And be it further enacted, That the seat of government of said Territory is hereby located temporarily at Fort Leavenworth; and that such portions of the public buildings as may not be actually used and needed for military purposes, may be occupied and used, under the direction of the Governor and Legislative Assembly, for such public purposes as may be required under the provisions of this act."

# Make socket
transport = TSocket.TSocket(server, port)

# Buffering is critical. Raw sockets are very slow
transport = TTransport.TBufferedTransport(transport)

# Wrap in a protocol
protocol = TBinaryProtocol.TBinaryProtocol(transport)

# Create a client to use the protocol encoder
client = StanfordCoreNLP.Client(protocol)

# Connect!
transport.open()

# This list is for options for how we'd like the output formatted.  See README.md for the full list of possible options.
# Note that the DEFAULT is what you would get if you specified "oneline" on the command line, or "None" here.
# You have to pass in something, and unfortunately it doesn't seem like that something can be None or an empty list.
# See http://diwakergupta.github.io/thrift-missing-guide/#_defining_structs for a possible explanation as to why...
# So, the following examples are VALID values for the second argument to these parse_* methods.
# (There are, of course, many more valid combinations depending on what the Stanford Parser supports.)
#outputOptions = ["-outputFormat", "typedDependencies,penn", "-outputFormatOptions", "basicDependencies"]
outputOptions = ["-outputFormat", "oneline,typedDependencies"]

'''
try:
    parse_trees = client.parse_text(ahs_test, outputOptions)
    for result in parse_trees:
        sys.stdout.write(result.tree.strip() + " [" + str(result.score) + "]\n")
        sys.stdout.write(client.lexicalize_parse_tree(result.tree.strip()) + "\n\n")
except Exception as e:
    print e

print

try:
    parse_trees = client.parse_text(arbitrary_text, outputOptions)
    for result in parse_trees:
        sys.stdout.write(result.tree.strip() + " [" + str(result.score) + "]\n")
        #sys.stdout.write(client.lexicalize_parse_tree(result.tree.strip()) + "\n\n")
except Exception as e:
    print e

print
'''
'''
for sentence in tokenized_sentences:
    try:
        tree = client.parse_tokens(sentence, outputOptions)
        sys.stdout.write(tree.tree.strip() + " [" + str(tree.score) + "]\n")
    except Exception as e:
        print e

print
'''
'''
try:
    tree = client.parse_tokens(weird_sentence, outputOptions)
    sys.stdout.write(tree.tree.strip() + "\n\n")
except Exception as e:
    print e
'''

results = client.sr_parse_tagged_sentence(tagged_sentence, None, "/")
print results

print

results = client.sr_parse_tagged_sentence(test_tagged_sentence, outputOptions, "/")
sys.stdout.write("\n" + results.tree.strip() + "\n")


# All done
transport.close()
