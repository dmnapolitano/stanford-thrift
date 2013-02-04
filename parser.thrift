namespace java parser
namespace py stanfordparser

service StanfordParser {

    void ping(),
    string parse_sentence(1:string sentence),
    string parse_tokens(1:list<string> tokens),
    oneway void zip()
}
