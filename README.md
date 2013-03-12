Apache Thrift Server for Stanford CoreNLP
=========================================

This is a client/server setup of Stanford's CoreNLP which uses [Apache Thrift](http://thrift.apache.org/).

Things you can do with it:

* Send over text/data in a variety of formats and receive:
	- Parse Trees **See README_parser.md**
	- Named Entities **See README_ner.md**
* Send unicode (optional), receive unicode (always).
* Do these things in a multithreaded way without having to think about it too much (Thrift provides ten threads).
* Communicate with the server using the language of your choice (with some additional coding if your choice isn't "Java" or "Python").


## How to Communicate with the Server on Research Boxes

**The server will usually be running on edsel, but check via `qstatx`.  The port will always be 9999.**
For some examples, please see the scripts/ directory for Python clients, src/StanfordCoreNLPClient.java for a Java client.

To write/use a Java client, add `/home/nlp-text/dynamic/NLPTools/thrift/all_thrift_jars.jar`, plus every jar in `/home/nlp-text/dynamic/NLPTools/stanford-core` except the sources and javadoc ones, to your `$CLASSPATH`.
To write/use a Python client, add `/home/nlp-text/dynamic/NLPTools/stanford-thrift/gen-py` to your `$PYTHONPATH.`


### How to Set Up Thrift on Your Local Machine

You can communicate with the server on edsel from your local machine in the exact same way you would from any one of the servers.
As far as I can tell, the required libraries aren't bundled with Thrift, so you can either download `/home/nlp-text/dynamic/NLPTools/thrift/all_thrift_jars.jar` or `/home/nlp-text/dynamic/NLPTools/thrift/lib` from our servers to make sure you have all of them.
Then download Thrift from the website above and follow the relevant instructions under "Build and Install the Apache Thrift Compiler".


## How to Get a Server Running Elsewhere

If you are on a research server, the latest version of Stanford CoreNLP will always be in `/home/nlp-text/dynamic/NLPTools/stanford-core`, and Thrift can be found in `/home/nlp-text/dynamic/NLPTools/thrift`.
Make sure the Stanford CoreNLP jars, as described above, and also either the entire contents of thrift/lib OR `/home/nlp-text/dynamic/NLPTools/thrift/all_thrift_jars.jar`, are on your CLASSPATH.  (The latter is the contents of the former, bundled into one jar using Ant.)

1. Clone the operational branch of this project with `git clone -b operational` followed by the SSH address above.  (You can clone the master branch, but that will also give you the source code, which you may not be interested in here).
2. Add the gen-py directory to your PYTHONPATH.
3. Run `scripts/start_server.sh <port number>` to start a server.



## How to Modify and then Recompile the Clients and Server

Assuming you are already able to run a server as per the instructions above, and have this project's master branch cloned, modify the code ONLY if you're faimilar enough with Thrift to do so and then run `ant` to rebuild.  This will result in an updated stanford-corenlp-wrapper.jar in the same place you ran `ant` from (which is probably the place you cloned master to).