How to Get Named Entities from the Stanford Named Entity Recognizer (NER) via this Apache Thrift Server
=======================================================================================================

## How to Interact with the Methods and Data Structures

The core return type here is a data structure called `NamedEntity` which has four members:

* `entity`: A string containing the actual named entity itself, potentially a multi-word expression if that's what Stanford NER recognized.
* `tag`: A string containing the tag assigned to this named entity (PERSON, LOCATION, etc.).  Should always be upper-case.
* `startOffset`: All named entities exist in some sentence.  This integer represents the starting character offset of this named entity in its sentence.
* `endOffset`: Like `startOffset`, only tells you the character offset of the last character of the named entity in its sentence.

In order to get these `NamedEntity` objects, you have three choices, depending on what kind of data you'd like to recognize named entities in.  The return type for ALL of these is a Java `ArrayList`/Python list containing `NamedEntity` objects corresponding to entities recognized across the ENTIRETY of your text, no matter how many sentences, parse trees, etc. were passed in.  If you'd like to recognize named entities in:

* arbitrary (potentially several sentences worth of), untokenized, un-parsed, un-tagged text, and you're cool with CoreNLP handling all of those tasks for you, call `get_entities_from_text(text)`.  `text` is a Java `String`/Python `str` or `unicode`.
* one sentence worth of tokens (the output from some sentence and then word tokenizer), call `get_entities_from_tokens(tokens)`, where `tokens` is a Java `List<String>`/Python list containing `str`/`unicode`.  Since Stanford NER requires either parse trees or POS-tagged text, the Stanford Parser will be called.
* one or more parse trees in Stanford Parser's "oneline" output format, call `get_entities_from_trees(trees)`, where `trees` is a Java `List<String>`/Python list containing `str`/`unicode`.