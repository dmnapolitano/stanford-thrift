package tokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

public class StanfordTokenizerThrift 
{
	public StanfordTokenizerThrift()
	{
	}
	
	public String untokenizeSentence(List<String> sentenceTokens)
	{
		return PTBTokenizer.ptb2Text(sentenceTokens);
	}
	
	public List<List<String>> tokenizeText(String arbitraryText)
	{
		List<List<String>> tokenizedSentences = new ArrayList<List<String>>();
		
    	DocumentPreprocessor preprocess = new DocumentPreprocessor(new StringReader(arbitraryText));
    	Iterator<List<HasWord>> foundSentences = preprocess.iterator();
    	while (foundSentences.hasNext())
    	{
    		List<HasWord> tokenizedSentence = foundSentences.next();
    		List<String> tokenizedSentenceAsListOfStrings = new ArrayList<String>();
    		for (HasWord w : tokenizedSentence)
    		{
    			tokenizedSentenceAsListOfStrings.add(w.word());
    		}
    		tokenizedSentences.add(tokenizedSentenceAsListOfStrings);
    	}
    	
    	return tokenizedSentences;
	}
}
