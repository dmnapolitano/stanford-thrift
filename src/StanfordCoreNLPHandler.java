import ner.StanfordNERThrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

import parser.StanfordParserThrift;

import CoreNLP.*;

import java.util.List;


public class StanfordCoreNLPHandler implements StanfordCoreNLP.Iface 
{
	private StanfordParserThrift parser;
	private StanfordNERThrift ner;

    public StanfordCoreNLPHandler() 
    {
    	// TODO: Some models are loaded multiple times.  Fix this. :/
    	parser = new StanfordParserThrift("");
    	ner = new StanfordNERThrift();
    }

    /* Begin Stanford Parser methods */
    public List<ParseTree> parse_text(String text, List<String> outputFormat) throws TApplicationException
    {
    	return parser.parse_text(text, outputFormat);
    }

    public ParseTree parse_tokens(List<String> tokens, List<String> outputFormat) throws TApplicationException
    {
    	return parser.parse_tokens(tokens, outputFormat);
    }
    /* End Stanford Parser methods */
    
    /* Begin Stanford NER methods */
    public List<NamedEntity> getNamedEntitiesFromText(String text)
    {
    	return ner.getNamedEntitiesFromText(text);
    }
    
    public List<NamedEntity> getNamedEntitiesFromTrees(List<String> trees)
    {
    	return ner.getNamedEntitiesFromTrees(trees);
    }
    /* End Stanford NER Methods */
    
    public void ping() 
    {
        System.out.println("ping()");
    }

    public void zip() 
    {
        System.out.println("zip()");
    }
}
