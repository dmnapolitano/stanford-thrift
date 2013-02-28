Apache Thrift Server for the Stanford Parser
============================================

This is a client/server setup of the Stanford Parser which uses [Apache Thrift](http://thrift.apache.org/).

Things you can do with it:

* Parse text that is both tokenized (by sending an array (Java ArrayList, Python list, etc.) of tokens) or untokenized (any ordinary text).
* Send unicode (optional), receive unicode (always).
* Parse things in a multithreaded way without having to think about it too much (Thrift provides ten threads).
* Receive one parse tree per sentence in the format "(ROOT (S (NP (DT This)) (VP (VBZ is) (NP (DT a) (JJ parse) (NN tree.)))))".
* Receive the score (probability) along with that parse tree.
* Communicate with the server using the language of your choice (with some additional coding if your choice isn't "Java" or "Python").


## How to Communicate with the Server on Research Boxes

**The server will usually be running on edsel, but check via `qstatx`.  The port will always be 9999.**
For some examples, please see the scripts/ directory for Python clients, src/StanfordParserClient.java for a Java client.


## How to Interact with the Methods and Data Structures

The core return type here is a data structure called `ParseTree` which has two members:

* `tree`: A string representing your parse tree (or, quite optionally, parse treeS; keep reading).
* `score`: A double representing the score for that parse.

In order to get these `ParseTree` objects, you have two choices, depending on whether or not you'd like Stanford's tokenizer to do some of the work for you.  The arguments are supplied in both Python and Java terms for ease of understanding, but again, see the clients if you're confused.  Keep reading for more information on the `outputFormat` parameter to each of these methods.

* `parse_text(text, outputFormat)` where `text` is a Java `String`/Python `str` or `unicode`, `outputFormat` is a Java `List<String>`/Python list containing `str`/`unicode`.
  Returns: Java `List<ParseTree>`/Python list containing `ParseTree` objects.
  Given any untokenized, arbitrary text, use Stanford's sentence and word tokenizers to do that bit of the work.

* `parse_tokens(tokens, outputFormat)` where `tokens` is a Java `List<String>`/Python list containing `str`/`unicode`, `outputFormat` is a Java `List<String>`/Python list containing `str`/`unicode`.
   Returns: A `ParseTree` object.
   Given a single sentence worth of output from toksent and expunct (for example), return that sentence's corresponding result from Stanford Parser.  Does not use Stanford's tokenizers.
   
##### What one can do with the `outputFormat` argument to both of these methods

The purpose of the `outputFormat` argument is to allow one to supply arguments in the same style as one would via command-line call to the Stanford Parser. **The only command-line switches supported here are `-outputFormat` and `-outputFormatOptions`, but they are supported in full.**  By that I mean any valid argument to each of those options is also valid here.
You can also supply multiple `-outputFormat` arguments, but note: you'll get back all of those parse trees, but altogether in the `tree` member of the returned `ParseTree` object, separated by two newlines (`\n\n`).
Thus, a call to a client object `client` that looks like:

```python
result = client.parse_tokens(["The", "cat", "sat", "on", "the", "mat", "."], 
                             ["-outputFormat", "typedDependencies,penn", "-outputFormatOptions", "basicDependencies"])
```

will have the following inside the `tree` member:

```Python
(ROOT
  (S
    (NP (DT The) (NN cat))
    (VP (VBD sat)
      (PP (IN on)
        (NP (DT the) (NN mat))))
    (. .)))

det(cat-2, The-1)
nsubj(sat-3, cat-2)
root(ROOT-0, sat-3)
prep(sat-3, on-4)
det(mat-6, the-5)
pobj(on-4, mat-6)
```

and then


## How to Get a Server Running Elsewhere

Make sure you have the Stanford Parser installed, and the stanford-parser.jar and stanford-parser-models.jar on your CLASSPATH.

1. Install the latest version of Thrift.  Add all of the jars in the installation directory's lib directory to your CLASSPATH.
2. Clone the operational branch of this project with `git clone -b operational` followed by the SSH address above.  (You can clone the master branch, but that will also give you the source code, which you may not be interested in here).
3. Add the gen-py directory to your PYTHONPATH.
4. Run `scripts/start_server.sh <port number>` to start a server.



## How to Modify and then Recompile the Clients and Server

Assuming you are already able to run a server as per the instructions above, and have this project's master branch cloned, modify the code ONLY if you're faimilar enough with Thrift to do so and then run `ant` to rebuild.  This will result in an updated stanford-parser-wrapper.jar in the same place you ran `ant` from (which is probably the place you cloned master to).