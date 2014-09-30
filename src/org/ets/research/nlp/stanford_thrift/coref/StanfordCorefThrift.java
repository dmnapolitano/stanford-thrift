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


package org.ets.research.nlp.stanford_thrift.coref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ets.research.nlp.stanford_thrift.general.CoreNLPThriftUtil;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;


public class StanfordCorefThrift 
{
	private StanfordCoreNLP coref;
	
	public StanfordCorefThrift()
	{
		// This works, as opposed to creating a 
		// edu.stanford.nlp.pipeline.DeterministicCorefAnnotator
		// object directly, because the coreference code runs the
		// parse tree a few times on its own, despite it having
		// been run (and parse trees having been stored) as part
		// of the mandatory NER.  Creating the object this way,
		// the coreference system can create new org.ets.research.nlp.stanford_thrift.parser objects
		// on-the-fly, despite the fact that they're never
		// initialized here.  Very strange.  These parsers
		// seem to use the default PCFG model.
		Properties props = new Properties();
		props.put("annotators", "dcoref");
		coref = new StanfordCoreNLP(props, false);
	}
	
	public List<String> getCoreferencesFromAnnotation(Annotation annotation)
	{
		coref.annotate(annotation);
		return MUCStyleOutput(annotation);
	}

	@SuppressWarnings("unused")
	private void newStyleCoreferenceGraphOutput(Annotation annotation)
	{
		// display the new-style coreference graph
		//List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class); 
		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		if (corefChains != null) 
		{			
			for (CorefChain chain : corefChains.values()) 
			{	
				CorefChain.CorefMention representative = chain.getRepresentativeMention();
				for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) 
				{
					System.out.println(mention);
					if (mention == representative)
						continue;
					// all offsets start at 1!
					System.out.println("\t"
							+ mention.mentionID + ": (Mention from sentence " + mention.sentNum + ", "
							+ "Head word = " + mention.headIndex 
							+ ", (" + mention.startIndex + "," + mention.endIndex + ")"
							+ ")"
							+ " -> " 
							+ "(Representative from sentence " + representative.sentNum + ", " 
							+ "Head word = " + representative.headIndex 
							+ ", (" + representative.startIndex + "," + representative.endIndex + ")"
							+ "), that is: \"" +
							mention.mentionSpan + "\" -> \"" +
							representative.mentionSpan + "\"");
				}
			}
		}
	}

	private List<String> MUCStyleOutput(Annotation annotation)
	{
		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		Map<Integer, Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>> mentionMap = 
				new HashMap<Integer, Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>>();
		
		List<String> mucOutput = new ArrayList<String>();

		for (CorefChain chain : corefChains.values()) 
		{
			CorefChain.CorefMention ref = chain.getRepresentativeMention();
			
			for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder())
			{
				if (mention != ref)
				{
					// first add the mention itself
					Pair<CorefChain.CorefMention,CorefChain.CorefMention> mentions =
							new Pair<CorefChain.CorefMention, CorefChain.CorefMention>(mention, ref);
					if (mentionMap.containsKey(mention.sentNum))
					{
						Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> value =
								mentionMap.get(mention.sentNum);
						value.put(mention.startIndex, mentions);
						mentionMap.put(mention.sentNum, value);
					}
					else
					{
						Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> startIndexToMentionMap =
								new HashMap<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>();
						startIndexToMentionMap.put(mention.startIndex, mentions);
						mentionMap.put(mention.sentNum, startIndexToMentionMap);
					}
					
					// now make sure the representative is there (TODO make this code less redundant)
					Pair<CorefChain.CorefMention,CorefChain.CorefMention> refMention =
							new Pair<CorefChain.CorefMention, CorefChain.CorefMention>(ref, ref);
					if (mentionMap.containsKey(ref.sentNum))
					{
						Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> value =
								mentionMap.get(ref.sentNum);
						value.put(ref.startIndex, refMention);
						mentionMap.put(ref.sentNum, value);
					}
					else
					{
						Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> startIndexToMentionMap =
								new HashMap<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>();
						startIndexToMentionMap.put(ref.startIndex, refMention);
						mentionMap.put(ref.sentNum, startIndexToMentionMap);
					}
				}
			}
		}


		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (Integer sentenceNum : mentionMap.keySet())
		{
			CoreMap currentSentence = sentences.get(sentenceNum-1);
			Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> currentSetOfMentions =
					mentionMap.get(sentenceNum);
			CorefChain.CorefMention lastMention = null;
			String outputString = "";
			for (CoreLabel token : currentSentence.get(CoreAnnotations.TokensAnnotation.class))
			{
				if (currentSetOfMentions.containsKey(token.index()))
				{
					lastMention = currentSetOfMentions.get(token.index()).first();
					CorefChain.CorefMention ref = currentSetOfMentions.get(token.index()).second();
					outputString += "<COREF ID=\"" + lastMention.mentionID + "\"";
					if (lastMention.mentionID != ref.mentionID)
					{
						outputString += " REF=\"" + ref.mentionID + "\"";
					}
					outputString += ">";
				}
				if (lastMention != null && token.index() == lastMention.endIndex)
				{
					outputString += "</COREF> ";
				}
				outputString += token.word() + " ";
			}
			mucOutput.add(CoreNLPThriftUtil.closeHTMLTags(outputString.replaceAll(" </", "</")));
		}
		
		return mucOutput;
	}

	
	/**
	 * @param args
	 */
//	public static void main(String[] args) 
//	{
//		StanfordCorefThrift org.ets.research.nlp.stanford_thrift.coref = new StanfordCorefThrift();
//		
//		//String testSentences = "By proposing a meeting date, Eastern moved one step closer toward reopening current high-cost contract agreements with its unions.";
//		String testSentences = "Barack Hussein Obama II is the 44th and current President of the United States, in office since 2009. "
//				+ "He is the first African American to hold the office.  "
//				+ "Born in Honolulu, Hawaii, Obama is a graduate of Columbia University and Harvard Law School, where he was president of the Harvard Law Review. "
//				+ "He was a community organizer in Chicago before earning his law degree. "
//				+ "He worked as a civil rights attorney in Chicago and taught constitutional law at the University of Chicago Law School from 1992 to 2004. "
//				+ "He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004, running unsuccessfully for the United States House of Representatives in 2000.";
//		List<String> results = org.ets.research.nlp.stanford_thrift.coref.getCoreferencesFromText(testSentences);
//		for (String s : results)
//		{
//			System.out.println(s);
//		}
//		
//		System.out.println();
//		
//		List<String> trees = new ArrayList<String>();
//		trees.add("(ROOT (S (NP (NNP Barack) (NNP Hussein) (NNP Obama) (NNP II)) (VP (VBZ is) (NP (NP (DT the) (JJ 44th) (CC and) (JJ current) (NN President)) (PP (IN of) (NP (DT the) (NNP United) (NNPS States)))) (, ,) (PP (IN in) (NP (NP (NN office)) (PP (IN since) (NP (CD 2009)))))) (. .)))");
//        trees.add("(ROOT (S (NP (PRP He)) (VP (VBZ is) (NP (DT the) (JJ first) (NNP African) (NNP American)) (S (VP (TO to) (VP (VB hold) (NP (DT the) (NN office)))))) (. .)))");
//        trees.add("(ROOT (S (S (VP (VBN Born) (PP (IN in) (NP (NNP Honolulu) (, ,) (NNP Hawaii))))) (, ,) (NP (NNP Obama)) (VP (VBZ is) (NP (NP (DT a) (NN graduate)) (PP (IN of) (NP (NP (NNP Columbia) (NNP University)) (CC and) (NP (NNP Harvard) (NNP Law) (NNP School))))) (, ,) (SBAR (WHADVP (WRB where)) (S (NP (PRP he)) (VP (VBD was) (NP (NP (NN president)) (PP (IN of) (NP (DT the) (NNP Harvard) (NNP Law) (NNP Review)))))))) (. .)))");
//        trees.add("(ROOT (S (NP (PRP He)) (VP (VBD was) (NP (NP (DT a) (NN community) (NN organizer)) (PP (IN in) (NP (NNP Chicago)))) (PP (IN before) (S (VP (VBG earning) (NP (PRP$ his) (NN law) (NN degree)))))) (. .)))");
//        trees.add("(ROOT (S (NP (PRP He)) (VP (VP (VBD worked) (PP (IN as) (NP (NP (DT a) (JJ civil) (NNS rights) (NN attorney)) (PP (IN in) (NP (NNP Chicago)))))) (CC and) (VP (VBD taught) (NP (JJ constitutional) (NN law)) (PP (IN at) (NP (NP (DT the) (NNP University)) (PP (IN of) (NP (NP (NNP Chicago) (NNP Law) (NNP School)) (PP (IN from) (NP (CD 1992))))))) (PP (TO to) (NP (CD 2004))))) (. .)))");
//        trees.add("(ROOT (S (NP (PRP He)) (VP (VBD served) (NP (NP (CD three) (NNS terms)) (VP (VBG representing) (NP (NP (DT the) (NAC (JJ 13th) (NNP District) (PP (IN in) (NP (DT the) (NNP Illinois)))) (NNP Senate)) (PP (IN from) (NP (CD 1997) (TO to) (CD 2004)))))) (, ,) (S (VP (VBG running) (ADVP (RB unsuccessfully)) (PP (IN for) (NP (NP (DT the) (NNP United) (NNPS States) (NNP House)) (PP (IN of) (NP (NP (NNS Representatives)) (PP (IN in) (NP (CD 2000)))))))))) (. .)))");
//		Annotation annotation = CoreNLPThriftUtil.getAnnotationFromParseTrees(trees);
//		StanfordNERThrift org.ets.research.nlp.stanford_thrift.ner = new StanfordNERThrift();
//        List<String> results = org.ets.research.nlp.stanford_thrift.coref.getCoreferencesFromAnnotation(org.ets.research.nlp.stanford_thrift.ner.annotateForNamedEntities(annotation));
//		for (String s : results)
//		{
//			System.out.println(s);
//		}
//	}
}
