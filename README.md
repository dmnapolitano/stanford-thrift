Apache Thrift Server for Stanford CoreNLP
=========================================

This is a client/server setup of Stanford's [CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml) which uses [Apache Thrift](http://thrift.apache.org/).

Things you can do with it:

* Send over text/data in a variety of formats and receive:
	- Parse Trees **See README_parser.md**
	- Named Entities **See README_ner.md**
	- Resolved Coreferences **See README_coref.md**
	- Stanford Tregex patterns evaluated over parse trees **See README_tregex.md**
	- Sentences tagged for Part-of-Speech **See README_tagger.md**
* Send unicode (optional), receive unicode (always).
* Do these things in a multithreaded way without having to think about it too much (Thrift provides ten threads).
* Communicate with the server using the language of your choice (with some additional coding if your choice isn't "Java" or "Python").

Please see the wiki for more specific information on how to communicate with the server, how to set up Thrift and Stanford CoreNLP, and how to modify this code.