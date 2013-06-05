How to Get Tokenized Text (and Untokenized Text) From the Stanford PTB Tokenizer via this Apache Thrift Server
==============================================================================================================

## How to Interact with the Methods

Two methods here, hopefully pretty straightforward:

* `untokenize_sentence(sentenceTokens)` where `sentenceTokens` is a Python `list`/Java `ArrayList<String>` corresponding to one sentence worth of tokens.
  Returns: a Python `unicode`/Java `String` which is `sentenceTokens` untokenized by Stanford CoreNLP.
* `tokenize_text(arbitraryText)` where `arbitraryText` is a Python `str` or `unicode`/Java `String` holding some arbitrary text you'd like tokenized.
  Returns: a Python `list` of lists of `unicode` objects/Java `ArrayList<ArrayList<String>`, where each sub-list is a list of tokens corresponding to one sentence (so each element in the outer list is one sentence).

The only thing this doesn't do yet is simply return untokenized sentences (i.e., per each sentence split, don't tokenize each sentence).  Of course you _could_ call `untokenize_sentence()` on each sentence returned by `tokenize_text()` if you really needed this and didn't feel like waiting for me to implement it. :grin:

For examples of how to call both of these methods, see `scripts/tokenizer_client.py`.