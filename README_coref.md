How to Resolve Coreferences with the Stanford Deterministic Coreference Resolution System (dcoref) via this Apache Thrift Server
================================================================================================================================

## How to Interact with the Methods and Data Structures

Presently, the return type of all of the methods below is a Java `ArrayList<String>`/Python `unicode` list, where each element is the original, tokenized sentence, annotated in-line with MUC-style SGML.  Please run `scripts/coref_client.py` in order to see examples of this output, as it is produced by each of these methods.  In short, each annotation has an `ID` field, and, most likely, a `REF` field.  The numbers are assigned interally by dcoref during the coreference resolution process.  If dcoref thinks a word or phrase could be a coreference, it is assigned an `ID`.  If that coreference refers to another one, the one it is referring to is noted by `REF`.  Thus, you may see output like:

```XML
<COREF ID="1">Barack Obama</COREF> ... . 
<COREF ID="4" REF="1">He</COREF> ... .
```

Notice that these results span two sentences.  You'll typically want to call these methods with more than one sentence, although you could, just as easily, call any of them with only one.  If you'd like to resolve coreferences in:

* arbitrary (potentially several sentences worth of), untokenized, un-parsed, un-tagged text, and you're cool with CoreNLP handling all of those tasks for you, call `resolve_coreferences_in_text(text)`.  `text` is a Java `String`/Python `str` or `unicode`.
* one or more sentences' worth of tokens (the output from some sentence and then word tokenizer), where each sentence's tokens are separated by a space.  For example, if your tokenizer produced:

```Python
output = [["Barack", "Hussein", "Obama", "II", "is", "the", "44th", "and", "current", "President", "of", "the", "United", "States", ",", "in", "office", "since", "2009" "."],
       	  ["He", "is", "the", "first", "African", "American", "to", "hold", "the", "office", "."]]
```

you can then create a list `tokenized_sentences` as

```Python
tokenized_sentences = [" ".join(o) for o in output]
```

and then call `resolve_coreferences_in_tokenized_sentences(tokenized_sentences)`.

* one or more parse trees in Stanford Parser's "oneline" output format, call `resolve_coreferences_in_trees(trees)`, where `trees` is a Java `List<String>`/Python list containing `str`/`unicode`.

Each one of these methods will call Stanford's Named Entity Recognizer prior to running dcoref (it is required in order for dcoref to perform its magic).