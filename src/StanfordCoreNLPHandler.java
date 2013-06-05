import ner.StanfordNERThrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

import coref.StanfordCorefThrift;
import edu.stanford.nlp.pipeline.Annotation;

import parser.StanfordParserThrift;
import tagger.StanfordTaggerThrift;
import tokenizer.StanfordTokenizerThrift;
import tregex.StanfordTregexThrift;

import CoreNLP.*;

import general.CoreNLPThriftUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StanfordCoreNLPHandler implements StanfordCoreNLP.Iface 
{
    private StanfordParserThrift parser;
    private StanfordNERThrift ner;
    private StanfordCorefThrift coref;
    private StanfordTregexThrift tregex;
    private StanfordTaggerThrift tagger;
    private StanfordTokenizerThrift tokenizer;
    
    // TODO: This NEEDS to be able to accept paths to alternate models other than just the Parser.
    public StanfordCoreNLPHandler(String parserModelFilePath)
    {
    	System.err.println("Initializing Parser...");
    	parser = new StanfordParserThrift(parserModelFilePath);
    	System.err.println("Initializing Named Entity Recognizer...");
    	ner = new StanfordNERThrift();
    	System.err.println("Initializing Coreference Resolver...");
    	coref = new StanfordCorefThrift();
    	System.err.println("Initializing Tregex...");
    	tregex = new StanfordTregexThrift();
    	System.err.println("Initializing Tagger...");
    	tagger = new StanfordTaggerThrift();
    	System.err.println("Initializing Tokenizer...");
    	tokenizer = new StanfordTokenizerThrift();
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
    
    public ParseTree parse_tagged_sentence(String taggedSentence, List<String> outputFormat, String divider) throws TApplicationException
    {
    	if (outputFormat == null)
    	{
    		List<String> oF = new ArrayList<String>();
    		return parser.parse_tagged_sentence(taggedSentence, oF, divider);
    	}
    	return parser.parse_tagged_sentence(taggedSentence, outputFormat, divider);
    }
    /* End Stanford Parser methods */
    
    
    /* Begin Stanford NER methods */
    public List<NamedEntity> get_entities_from_text(String text) throws TApplicationException
    {
    	//return ner.getNamedEntitiesFromText(text);
    	List<ParseTree> parseTreeObjects = parser.parse_text(text, null);
    	List<String> parseTrees = CoreNLPThriftUtil.ParseTreeObjectsToString(parseTreeObjects);
    	return ner.getNamedEntitiesFromTrees(parseTrees);
    }
    
    public List<NamedEntity> get_entities_from_tokens(List<String> tokens) throws TApplicationException
    {
    	ParseTree parseTreeObject = parser.parse_tokens(tokens, null);
    	List<String> parseTrees = new ArrayList<String>();
    	parseTrees.add(parseTreeObject.tree);
    	return ner.getNamedEntitiesFromTrees(parseTrees);
    }
    
    public List<NamedEntity> get_entities_from_trees(List<String> trees)
    {
    	return ner.getNamedEntitiesFromTrees(trees);
    }
    /* End Stanford NER methods */
    
    
    /* Begin Stanford Coref methods */
    public List<String> resolve_coreferences_in_text(String text) throws TApplicationException
    {
    	List<ParseTree> parseTreeObjects = parser.parse_text(text, null);
    	List<String> parseTrees = CoreNLPThriftUtil.ParseTreeObjectsToString(parseTreeObjects);
    	Annotation annotation = ner.annotateForNamedEntities(CoreNLPThriftUtil.getAnnotationFromParseTrees(parseTrees));
    	return coref.getCoreferencesFromAnnotation(annotation);
    }
    
    // TODO: Why did I...?
    public List<String> resolve_coreferences_in_tokenized_sentences(List<String> sentencesWithTokensSeparatedBySpace) throws TApplicationException
    {
    	List<String> parseTrees = new ArrayList<String>();
    	for (String sentence : sentencesWithTokensSeparatedBySpace)
    	{
    		List<String> tokens = Arrays.asList(sentence.split(" "));
    		ParseTree parseTreeObject = parser.parse_tokens(tokens, null);
    		parseTrees.add(parseTreeObject.tree);
    	}
    	Annotation annotation = CoreNLPThriftUtil.getAnnotationFromParseTrees(parseTrees);
    	annotation = ner.annotateForNamedEntities(annotation);
    	return coref.getCoreferencesFromAnnotation(annotation);
    }
    
    public List<String> resolve_coreferences_in_trees(List<String> trees)
    {
    	Annotation annotation = CoreNLPThriftUtil.getAnnotationFromParseTrees(trees);
    	annotation = ner.annotateForNamedEntities(annotation);
    	return coref.getCoreferencesFromAnnotation(annotation);
    }
    /* End Stanford Coref methods */
    
    
    /* Begin Stanford Tregex methods */
    public List<String> evaluate_tregex_pattern(String parseTree, String tregexPattern)
    {
    	return tregex.evaluateTregexPattern(parseTree, tregexPattern);
    }
    /* End Stanford Tregex methods */
    
    
    /* Begin Stanford Tagger methods */
    public List<List<TaggedToken>> tag_text(String untokenizedText)
    {
    	return tagger.tag_text(untokenizedText);
    }
    
    public List<TaggedToken> tag_tokenized_sentence(List<String> tokenizedSentence)
    {
    	return tagger.tag_tokenized_sentence(tokenizedSentence);
    }
    /* End Stanford Tagger methods */
    
    
    /* Begin Stanford Tokenizer methods */
    public String untokenize_sentence(List<String> sentenceTokens)
    {
    	return tokenizer.untokenizeSentence(sentenceTokens);
    }
    
    public List<List<String>> tokenize_text(String arbitraryText)
    {
    	return tokenizer.tokenizeText(arbitraryText);
    }
    /* End Stanford Tokenizer methods */
    
    
    public void ping() 
    {
        System.out.println("ping()");
    }

    public void zip() 
    {
        System.out.println("zip()");
    }
}
