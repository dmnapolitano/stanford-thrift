namespace java parser
namespace py stanfordparser

service StanfordParser {

    void ping(),
    string parse(1:string sentence),
    oneway void zip()
}
