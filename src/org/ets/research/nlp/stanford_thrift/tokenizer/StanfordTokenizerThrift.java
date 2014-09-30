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


package org.ets.research.nlp.stanford_thrift.tokenizer;

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
