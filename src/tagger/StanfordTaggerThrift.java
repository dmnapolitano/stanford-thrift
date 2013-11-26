/*
  Apache Thrift Server for Stanford CoreNLP (stanford-thrift)
  Copyright (C) 2013 Diane M. Napolitano, Educational Testing Service
  
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation, version 2
  of the License.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


package tagger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import CoreNLP.*;

public class StanfordTaggerThrift 
{
	private MaxentTagger tagger;
	
	public StanfordTaggerThrift(String taggerModel)
	{
		tagger = new MaxentTagger(taggerModel);
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
