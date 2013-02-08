namespace java parser
namespace py stanfordparser

struct ParseTree {
	1:string tree,
	2:double score
}

service StanfordParser {

    void ping(),
    list<ParseTree> parse_text(1:string sentence),
    list<ParseTree> parse_tokens(1:list<string> tokens),
    oneway void zip()
}
