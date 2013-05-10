How to Get Part-of-Speech Tags from the Stanford POS Tagger via this Apache Thrift Server
=========================================================================================

## How to Interact with the Methods and Data Structures

The core return type here is a data structure called `TaggedToken` which has two members:

* `tag`: A Unicode string containing the Penn Treebank part-of-speech tag assigned to this token.  Should always be upper-case.
* `token`: The token, a Unicode string, from the sentence, with that corresponding part-of-speech tag.  Whatever its original casing was will be the same here.

A `List<TaggedToken>` is a Python list of `TaggedToken` objects/Java `ArrayList<TaggedToken>` that corresponds to the sentence that was part-of-speech tagged, maintaining order of the tokens in the sentence.  Depending on what you would like to tag, you'll get back either a single sentence worth of tags or multiple sentences worth (depending on how many sentences were found by Stanford's Tokenizer).  If you'd like to tag:

* arbitrary (potentially several sentences worth of), untokenized text, and you're cool with CoreNLP performing the necessary tokenization, call `tag_text(untokenizedText)`.  `untokenizedText` is a Java `String`/Python `str` or `unicode`.
Returns: a Java `List<List<TaggedToken>>`, which corresponds to an `ArrayList` of sentences in the order they were in the `untokenizedText`.
* one sentence worth of tokens (the output from some sentence and then word tokenizers of your choosing), call `tag_tokenized_sentence(tokens)`, where `tokens` is a Java `List<String>`/Python list containing `str`/`unicode`.
Returns: a Java `ArrayList<TaggedToken>`/Python list of `TaggedToken` objects.

For examples of how to call both of these methods, and how one can do some nice things with the value(s) returned, see `scripts/tagger_client.py`.