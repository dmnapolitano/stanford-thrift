namespace java parser
namespace py stanfordparser

struct ParseTree 
{
	1:string tree,
	2:double score
}

exception SerializedException
{
    1: required binary payload
}

service StanfordParser
{
    void ping(),
    list<ParseTree> parse_text(1:string text, 2:list<string> outputFormat),
    ParseTree parse_tokens(1:list<string> tokens, 2:list<string> outputFormat) throws (1:SerializedException se),
    oneway void zip()
}
