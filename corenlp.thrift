namespace java CoreNLP
namespace py corenlp

struct ParseTree 
{
	1:string tree,
	2:double score
}

struct NamedEntity
{
	1:string entity,
	2:string tag,
	3:i32 startOffset,
	4:i32 endOffset
}

exception SerializedException
{
    1: required binary payload
}

service StanfordCoreNLP
{
    void ping(),
    oneway void zip(),
    list<ParseTree> parse_text(1:string text, 2:list<string> outputFormat),
    ParseTree parse_tokens(1:list<string> tokens, 2:list<string> outputFormat),
    list<NamedEntity> get_entities_from_text(1:string text),
    list<NamedEntity> get_entities_from_tokens(1:list<string> tokens),
    list<NamedEntity> get_entities_from_trees(1:list<string> trees),
    list<string> resolve_coreferences_in_text(1:string text),
    list<string> resolve_coreferences_in_tokenized_sentences(1:list<string> sentencesWithTokensSeparatedBySpace),
    list<string> resolve_coreferences_in_trees(1:list<string> trees)
}
