How to Run Regular Expressions over Parse Trees with Stanford Tregex via this Apache Thrift Server
==================================================================================================

## How to Interact with the Methods and Data Structures

Presently, there is only one method, `evaluate_tregex_pattern(parse_tree, tregex_pattern)` where `parse_tree` is a Java `String`/Python `str` or `unicode` containing a single sentence's parse tree (probably the output from the Stanford Parser), and `tregex_pattern` is a Java `String`/Python `str` or `unicode` containing a valid Tregex pattern that you wish to evaluate on this tree.
The return value is a Java `ArrayList<String>`/Python `unicode` list, where each element is a match against the parse tree of the specified Tregex pattern.
I'm pretty sure `parse_tree` can be in ANY of the Stanford Parser output formats, although the only one I have tried is the `oneline` format.