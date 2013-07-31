#!/usr/bin/env python
# -*- coding: utf-8 -*-

# The purpose of this client is to show how to send over a few sentences in every way possible.
# It is also for me to unit test things. >:)

from corenlp import StanfordCoreNLP
from corenlp.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

import sys


# get command line arguments
args = sys.argv[1:]
if len(args) != 2:
    sys.stderr.write('Usage: parser_client.py <server> <port>\n')
    sys.exit(2)
else:
    server = args[0]
    port = int(args[1])


# Good for testing long sentences
#sentences = ["Well aware that the opinions and belief of men depend not on their own will , but follow involuntarily the evidence proposed to their minds ; that Almighty God hath created the mind free , and manifested his supreme will that free it shall remain by making it altogether insusceptible of restraint ; that all attempts to influence it by temporal punishments , or burthens , or by civil incapacitations , tend only to beget habits of hypocrisy and meanness , and are a departure from the plan of the holy author of our religion , who being lord both of body and mind , yet chose not to propagate it by coercions on either , as was in his almighty power to do , but to exalt it by its influence on reason alone ; that the impious presumption of legislature and ruler , civil as well as ecclesiastical , who , being themselves but fallible and uninspired men , have assumed dominion over the faith of others , setting up their own opinions and modes of thinking as the only true and infallible , and as such endeavoring to impose them on others , hath established and maintained false religions over the greatest part of the world and through all time : that to compel a man to furnish contributions of money for the propagation of opinions which he disbelieves and abhors is sinful and tyrannical ; that even the forcing him to support this or that teacher of his own religious persuasion is depriving him of the comfortable liberty of giving his contributions to the particular pastor whose morals he would make his pattern and whose powers he feels most persuasive to righteousness , and is withdrawing from the ministry those temporary rewards which , proceeding from an approbation of their personal conduct , are an additional incitement to earnest and unremitting labors for the instruction of mankind ; that our civil rights have no dependence on our religious opinions , any more than our opinions in physics or geometry ; and therefore the proscribing any citizen as unworthy the public confidence by laying upon him an incapacity of being called to offices of trust or emolument , unless he profess or renounce this or that religious opinion , is depriving him injudiciously of those privileges and advantages to which , in common with his fellow citizens , he has a natural right ; that it tends also to corrupt the principles of that very religion it is meant to encourage , by bribing with a monopoly of worldly honors and emoluments those who will externally profess and conform to it ; that though indeed these are criminals who do not withstand such temptation , yet neither are those innocent who lay the bait in their way ; that the opinions of men are not the object of civil government , nor under its jurisdiction ; that to suffer the civil magistrate to intrude his powers into the field of opinion and to restrain the profession or propagation of principles on supposition of their ill tendency is a dangerous fallacy , which at once destroys all religious liberty , because he being of course judge of that tendency will make his opinions the rule of judgment and approve or condemn the sentiments of others only as they shall square with or suffer from his own ; that it is time enough for the rightful purposes of civil government for its officers to interfere when principles break out into overt acts against peace and good order ; and finally , that the truth is great and will prevail if left to herself ; that she is the proper and sufficient antagonist to error , and has nothing to fear from the conflict unless by human interposition disarmed of her natural weapons , free argument and debate ; errors ceasing to be dangerous when it is permitted freely to contradict them ."]

# Taken from the English Wikipedia entry for "Fox". :)
arbitrary_text = u"Members of about 37 species are referred to as foxes, of which only 12 species actually belong to the Vulpes genus of \"true foxes\".  By far the most common and widespread species of fox is the red fox (Vulpes vulpes), although various species are found on almost every continent.  The presence of fox-like carnivores all over the globe, together with their widespread reputation for cunning, has contributed to their appearance in popular culture and folklore in many societies around the world (see also Foxes in culture).  The hunting of foxes with packs of hounds, long an established pursuit in Europe, especially the British Isles, was exported by European settlers to various parts of the New World."

tokenized_sentences = [u"Members of about 37 species are referred to as foxes , of which only 12 species actually belong to the Vulpes genus of `` true foxes '' .".split(" "),
                       u"By far the most common and widespread species of fox is the red fox -LRB- Vulpes vulpes -RRB- , although various species are found on almost every continent .".split(" "),
                       u"The presence of fox-like carnivores all over the globe , together with their widespread reputation for cunning , has contributed to their appearance in popular culture and folklore in many societies around the world -LRB- see also Foxes in culture -RRB- .".split(" "),
                       u"The hunting of foxes with packs of hounds , long an established pursuit in Europe , especially the British Isles , was exported by European settlers to various parts of the New World .".split(" ")]

# a particularly useful example for coreference
more_tokenized_sentences = [u"Barack Hussein Obama II is the 44th and current President of the United States , in office since 2009 .".split(" "),
                            u"He is the first African American to hold the office .".split(" "),
                            u"Born in Honolulu , Hawaii , Obama is a graduate of Columbia University and Harvard Law School , where he was president of the Harvard Law Review .".split(" "),
                            u"He was a community organizer in Chicago before earning his law degree .".split(" "),
                            u"He worked as a civil rights attorney in Chicago and taught constitutional law at the University of Chicago Law School from 1992 to 2004 .".split(" "),
                            u"He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004 , running unsuccessfully for the United States House of Representatives in 2000 .".split(" ")]

tokenized_sentence = u"Members of about 37 species are referred to as foxes , of which only 12 species actually belong to the Vulpes genus of `` true foxes '' .".split(" ")
tagged_sentence = u"Members/NNS of/IN about/IN 37/CD species/NNS are/VBP referred/VBN to/TO as/IN foxes/NNS ,/, of/IN which/WDT only/RB 12/CD species/NNS actually/RB belong/VBP to/TO the/DT Vulpes/NNP genus/NN of/IN ``/`` true/JJ foxes/NNS ''/'' ./."

weird_sentence = [u'While', u'the', u'child', u'spends', u'about', u'five', u'hours', u'or', u'less', u'with', u'his', u'parents', u',', u'and', u'whenever', u'that', u'child', u'wants', u'to', u'go', u'out', u'he', u'will', u'most', u'probably', u'go', u'out', u'with', u'his', u'friends', u'which', u'are', u'his', u'classmates', u',', u'so', u'most', u'of', u'his', u'school', u'life', u'will', u'be', u'spent', u'with', u'his', u'classmates', u',', u'and', u'this', u'will', u'have', u'a', u'great', u'affect', u'on', u'his', u'personality', u'which', u'will', u'determine', u'the', u'way', u'the', u'child', u'will', u'react', u'towards', u'his', u'school', u'and', u'will', u'determine', u'how', u'he', u'will', u'use', u'his', u'life', u'.']

ahs_test = "And be it further enacted, That the seat of government of said Territory is hereby located temporarily at Fort Leavenworth; and that such portions of the public buildings as may not be actually used and needed for military purposes, may be occupied and used, under the direction of the Governor and Legislative Assembly, for such public purposes as may be required under the provisions of this act."

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
#outputOptions = ["-outputFormat", "typedDependencies,penn", "-outputFormatOptions", "basicDependencies"]
#outputOptions = []
outputOptions = ["-outputFormat", "oneline"]
#outputOptions = ["-outputFormat", "oneline,typedDependencies"]


try:
    parse_trees = client.parse_text(ahs_test, outputOptions)
    for result in parse_trees:
        sys.stdout.write(result.tree.strip() + " [" + str(result.score) + "]\n")
        sys.stdout.write(client.lexicalize_parse_tree(result.tree.strip()) + "\n\n")
except Exception as e:
    print e

'''
print

for sentence in tokenized_sentences:
    try:
        tree = client.parse_tokens(sentence, outputOptions)
        sys.stdout.write(tree.tree.strip() + " [" + str(tree.score) + "]\n")
    except Exception as e:
        print e
'''

print

'''
for sentence in more_tokenized_sentences:
    try:
        tree = client.parse_tokens(sentence, outputOptions)
        sys.stdout.write(tree.tree.strip()+"\n")
    except Exception as e:
        print e
'''

'''
try:
    tree = client.parse_tokens(weird_sentence, outputOptions)
    sys.stdout.write(tree.tree.strip() + "\n\n")
except Exception as e:
    print e
'''

tree = client.parse_tagged_sentence(tagged_sentence, outputOptions, "/")
sys.stdout.write("\n" + tree.tree.strip() + "\n")

# All done
transport.close()
