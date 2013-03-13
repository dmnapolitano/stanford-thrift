import ner.StanfordNERThrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

import parser.StanfordParserThrift;

import CoreNLP.*;

import general.CoreNLPThriftUtil;

import java.util.ArrayList;
import java.util.List;


public class StanfordCoreNLPHandler implements StanfordCoreNLP.Iface 
{
	private StanfordParserThrift parser;
	private StanfordNERThrift ner;

    public StanfordCoreNLPHandler() 
    {
    	parser = new StanfordParserThrift("");
    	ner = new StanfordNERThrift();
    }

    /* Begin Stanford Parser methods */
    public List<ParseTree> parse_text(String text, List<String> outputFormat) throws TApplicationException
    {
    	if (outputFormat == null)
    	{
    		List<String> oF = new ArrayList<String>();
    		return parser.parse_text(text, oF);
    	}
    	return parser.parse_text(text, outputFormat);
    }

    public ParseTree parse_tokens(List<String> tokens, List<String> outputFormat) throws TApplicationException
    {
    	if (outputFormat == null)
    	{
    		List<String> oF = new ArrayList<String>();
    		return parser.parse_tokens(tokens, oF);
    	}
    	return parser.parse_tokens(tokens, outputFormat);
    }
    /* End Stanford Parser methods */
    
    /* Begin Stanford NER methods */
    public List<NamedEntity> getNamedEntitiesFromText(String text) throws TApplicationException
    {
    	List<ParseTree> parseTreeObjects = parser.parse_text(text, null);
    	List<String> parseTrees = CoreNLPThriftUtil.ParseTreeObjectsToString(parseTreeObjects);
    	//return ner.getNamedEntitiesFromText(text);
    	return ner.getNamedEntitiesFromTrees(parseTrees);
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
