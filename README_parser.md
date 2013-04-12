How to Get Parse Trees from the Stanford Parser via this Apache Thrift Server
=============================================================================

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
   Given a single sentence worth of output from the sentence and word tokenizers of your choice, return that sentence's corresponding result from Stanford Parser.  Does not use Stanford's tokenizers.

* `parse_tagged_sentence(taggedSentence, outputFormat, delimiter)` where `taggedSentence` is a single POS-tagged sentence (Java `String`/Python `str`/`unicode`), `outputFormat` is a Java `List<String>`/Python list containing `str`/`unicode`, and `delimiter` is a Java `String`/Python `str`/`unicode` containing the single character that separates the word from the tag in your `taggedSentence`.
	Returns: A `ParseTree` object.
	Given a single Penn Treebank part-of-speech-tagged sentence from the tokenizer and tagger combination of your choice, have Stanford generate a parse tree based on those tags.

   
##### What one can do with the `outputFormat` argument to both of these methods

The purpose of the `outputFormat` argument is to allow one to supply arguments in the same style as one would via command-line call to the Stanford Parser. **The only command-line switches supported here are `-outputFormat` and `-outputFormatOptions`, but they are supported in full.**  By that I mean any valid argument to each of those options is also valid here.
You can also pass in `null`/`None` and that will return parse trees in this server's default format of `-outputFormat "oneline"`.
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