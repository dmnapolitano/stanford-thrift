#!/usr/bin/env python

# The purpose of this client is to show how to send over a few sentences as you tokenize them with expunct.
# It is also for me to unit test things. >:)
# For an alternative way to call the parser (with Python), please see parser_client.py.

from stanfordparser import StanfordParser
from stanfordparser.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

from bs4 import UnicodeDammit
import re
import sys

import expunct

# get command line arguments
args = sys.argv[1:]
if len(args) != 2:
    sys.stderr.write('Usage: parser_client_with_expunct.py <server> <port>\n')
    sys.exit(2)
else:
    server = args[0]
    port = int(args[1])


# Good for testing long sentences
#sentences = ["Well aware that the opinions and belief of men depend not on their own will , but follow involuntarily the evidence proposed to their minds ; that Almighty God hath created the mind free , and manifested his supreme will that free it shall remain by making it altogether insusceptible of restraint ; that all attempts to influence it by temporal punishments , or burthens , or by civil incapacitations , tend only to beget habits of hypocrisy and meanness , and are a departure from the plan of the holy author of our religion , who being lord both of body and mind , yet chose not to propagate it by coercions on either , as was in his almighty power to do , but to exalt it by its influence on reason alone ; that the impious presumption of legislature and ruler , civil as well as ecclesiastical , who , being themselves but fallible and uninspired men , have assumed dominion over the faith of others , setting up their own opinions and modes of thinking as the only true and infallible , and as such endeavoring to impose them on others , hath established and maintained false religions over the greatest part of the world and through all time : that to compel a man to furnish contributions of money for the propagation of opinions which he disbelieves and abhors is sinful and tyrannical ; that even the forcing him to support this or that teacher of his own religious persuasion is depriving him of the comfortable liberty of giving his contributions to the particular pastor whose morals he would make his pattern and whose powers he feels most persuasive to righteousness , and is withdrawing from the ministry those temporary rewards which , proceeding from an approbation of their personal conduct , are an additional incitement to earnest and unremitting labors for the instruction of mankind ; that our civil rights have no dependence on our religious opinions , any more than our opinions in physics or geometry ; and therefore the proscribing any citizen as unworthy the public confidence by laying upon him an incapacity of being called to offices of trust or emolument , unless he profess or renounce this or that religious opinion , is depriving him injudiciously of those privileges and advantages to which , in common with his fellow citizens , he has a natural right ; that it tends also to corrupt the principles of that very religion it is meant to encourage , by bribing with a monopoly of worldly honors and emoluments those who will externally profess and conform to it ; that though indeed these are criminals who do not withstand such temptation , yet neither are those innocent who lay the bait in their way ; that the opinions of men are not the object of civil government , nor under its jurisdiction ; that to suffer the civil magistrate to intrude his powers into the field of opinion and to restrain the profession or propagation of principles on supposition of their ill tendency is a dangerous fallacy , which at once destroys all religious liberty , because he being of course judge of that tendency will make his opinions the rule of judgment and approve or condemn the sentiments of others only as they shall square with or suffer from his own ; that it is time enough for the rightful purposes of civil government for its officers to interfere when principles break out into overt acts against peace and good order ; and finally , that the truth is great and will prevail if left to herself ; that she is the proper and sufficient antagonist to error , and has nothing to fear from the conflict unless by human interposition disarmed of her natural weapons , free argument and debate ; errors ceasing to be dangerous when it is permitted freely to contradict them ."]

# Good for testing what is probably the most common use-case.  Taken from the English Wikipedia entry for "Fox". :)
sentences = ["Members of about 37 species are referred to as foxes, of which only 12 species actually belong to the Vulpes genus of \"true foxes\".", "By far the most common and widespread species of fox is the red fox (Vulpes vulpes), although various species are found on almost every continent.", "The presence of fox-like carnivores all over the globe, together with their widespread reputation for cunning, has contributed to their appearance in popular culture and folklore in many societies around the world (see also Foxes in culture).", "The hunting of foxes with packs of hounds, long an established pursuit in Europe, especially the British Isles, was exported by European settlers to various parts of the New World."]


transport = TSocket.TSocket(server, port)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = StanfordParser.Client(protocol)

transport.open()

# This list is for options for how we'd like the output formatted.  See README.md for the full list of possible options.
# Note that the DEFAULT is what you would get if you specified "oneline" on the command line, or "None" here.
#outputOptions = ["-outputFormat", "typedDependencies,penn", "-outputFormatOptions", "basicDependencies"]
#outputOptions = None
outputOptions = ["-outputFormat", "wordsAndTags"]

for sentence in sentences:
    #sentence = UnicodeDammit(sentence, ["windows-1252", "utf8"]).unicode_markup
    sentence = expunct.word_tokenize(sentence, ptb_normalization=True) # You ought to use this flag if interacting with Stanford anything afterwards
    # if pre-Expuncted
    #sentence = sentence.split(" ")
    try:
        tree = client.parse_tokens(sentence, outputOptions)
        tree = tree.tree.strip()

        # Sometimes a sentence you send over as one sentence is interpreted as two by the Stanford Parser.
        # This bit of code is here to make sure no parse trees are lost in the results when the results
        # are of the default "oneline" format.  This is particularly important if these results are going
        # into say, a Python list, where each element is a parse tree.
        if "\n(ROOT" in tree:
            for t in re.split(r'(\n\(ROOT.+)', tree):
                if len(t) > 0:
                    print t.strip()
        else:
            print tree
    except Exception as e:
        print e

transport.close()
