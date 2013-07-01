package tagger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.DefaultPaths;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import CoreNLP.*;

public class StanfordTaggerThrift 
{
	private MaxentTagger tagger;
	
	public StanfordTaggerThrift()
	{
		tagger = new MaxentTagger(DefaultPaths.DEFAULT_POS_MODEL);
	}
	
	public List<List<TaggedToken>> tag_text(String untokenizedText)
	{
		List<List<TaggedToken>> taggedAndTokenizedSentences = new ArrayList<List<TaggedToken>>();
		
		// assume no tokenization was done; use Stanford's default tokenizer
    	DocumentPreprocessor preprocess = new DocumentPreprocessor(new StringReader(untokenizedText));
    	Iterator<List<HasWord>> foundSentences = preprocess.iterator();
    	while (foundSentences.hasNext())
    	{
    		taggedAndTokenizedSentences.add(tagSingleSentence(foundSentences.next()));
    	}
    	
    	return taggedAndTokenizedSentences;
	}
	
	public List<TaggedToken> tag_tokenized_sentence(List<String> tokenizedSentence)
	{
		List<TaggedToken> taggedTokenizedSentence = new ArrayList<TaggedToken>();
		
		// a single sentence worth of tokens
    	String[] tokenArray = new String[tokenizedSentence.size()];
    	tokenizedSentence.toArray(tokenArray);
    	List<CoreLabel> crazyStanfordFormat = Sentence.toCoreLabelList(tokenArray);
    	return tagSingleSentence(crazyStanfordFormat);
	}
	
	private List<TaggedToken> tagSingleSentence(List<? extends HasWord> stanfordFormat)
	{
		List<TaggedWord> outputFromTagger = tagger.apply(stanfordFormat);
		List<TaggedToken> taggedSentence = new ArrayList<TaggedToken>();
		for (TaggedWord tw : outputFromTagger)
		{
			TaggedToken token = new TaggedToken();
			token.tag = tw.tag();
			token.token = tw.word();
			taggedSentence.add(token);
		}
		
		return taggedSentence;
	}
}
