Apache Thrift Server for the Stanford Parser
============================================

This is a client/server setup of the Stanford Parser which uses [Apache Thrift](http://thrift.apache.org/).

Things you can do with it:

* Parse text that is both tokenized (by sending an array (Java ArrayList, Python list, etc.) of tokens) or untokenized (any ordinary text).
* Send unicode (optional), receive unicode (always).
* Parse things in a multithreaded way (Thrift provides ten threads).
* Receive one parse tree per sentence in the format "(ROOT (S (NP (DT This)) (VP (VBZ is) (NP (DT a) (JJ parse) (NN tree.)))))".
* Receive the score (probability) along with that parse tree.
* Communicate with the server using the language of your choice (with some additional coding if your choice isn't "Java" or "Python").


How to Communicate with the Server on Research Boxes
----------------------------------------------------

<b>The server will usually be running on edsel, but check via `qstatx`.  The port will always be 9999.</b>
For some examples, please see scripts/parse-java.sh for a Java client, scripts/parse-python.sh for a Python client.

Additionally, here's an example showing how to parse one sentence of pre-tokenized data at a time:

```python
from stanfordparser import StanfordParser
from stanfordparser.ttypes import *
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

transport = TSocket.TSocket("bragi.research.ets.org", 9999)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = StanfordParser.Client(protocol)
transport.open()
for sentence in sentences:  # This is a list of tokenized sentences
    tree = client.parse_tokens(sentence)[0] # because we only sent over a single sentence; parse_tokens ALWAYS returns an array
print tree.tree, "\t", tree.score  # to see the parse tree AND its score
transport.close()
```


How to Get a Server Running Elsewhere
-------------------------------------

Make sure you have the Stanford Parser installed, and the stanford-parser.jar and stanford-parser-models.jar on your CLASSPATH.

1. Install the latest version of Thrift.  Add all of the jars in the installation directory's lib directory to your CLASSPATH.
2. From this git project, download stanford-parser-wrapper.jar for the server and Java client, gen-py for the Python client, to your preferred location.
3. Add stanford-parser-wrapper.jar to your CLASSPATH.
4. Add the gen-py directory to your PYTHONPATH.
5. Use scripts/start_server.sh <port number> to start a server.



How to Modify and then Recompile the Clients/Server
---------------------------------------------------

Assuming you are already able to run a server as per the instructions above, and have this project cloned, modify the code ONLY if you're faimilar enough with Thrift to do so and then run `ant` to rebuild.