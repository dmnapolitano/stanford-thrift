package coref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ner.StanfordNERThrift;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.DeterministicCorefAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import general.CoreNLPThriftUtil;


public class StanfordCorefThrift 
{
//	private StanfordCoreNLP pipeline;
	private DeterministicCorefAnnotator coref;
	
	public StanfordCorefThrift()
	{
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, parse, lemma, ner, dcoref");
//		pipeline = new StanfordCoreNLP(props, true);
		coref = new DeterministicCorefAnnotator(new Properties());
	}
	
//	public List<String> getCoreferencesFromText(String text)
//	{
//		Annotation annotation = new Annotation(text);
//		pipeline.annotate(annotation);
//		return MUCStyleOutput(annotation);
//	}
	
	public List<String> getCoreferencesFromTrees(List<String> parseTrees, StanfordNERThrift ner)
	{
		Annotation annotation = ner.getNamedEntityAnnotationFromTrees(parseTrees);
		coref.annotate(annotation);
		return MUCStyleOutput(annotation);
	}

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
//		StanfordCorefThrift coref = new StanfordCorefThrift();
//		
//		//String testSentences = "By proposing a meeting date, Eastern moved one step closer toward reopening current high-cost contract agreements with its unions.";
//		String testSentences = "Barack Hussein Obama II is the 44th and current President of the United States, in office since 2009. "
//				+ "He is the first African American to hold the office.  "
//				+ "Born in Honolulu, Hawaii, Obama is a graduate of Columbia University and Harvard Law School, where he was president of the Harvard Law Review. "
//				+ "He was a community organizer in Chicago before earning his law degree. "
//				+ "He worked as a civil rights attorney in Chicago and taught constitutional law at the University of Chicago Law School from 1992 to 2004. "
//				+ "He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004, running unsuccessfully for the United States House of Representatives in 2000.";
//		List<String> results = coref.getCoreferencesFromText(testSentences);
//		for (String s : results)
//		{
//			System.out.println(s);
//		}
//		
//		System.out.println();
//		
//		List<String> trees = new ArrayList<String>();
//		trees.add("(ROOT (S (NP (NP (NNS Members)) (PP (IN of) (NP (QP (RB about) (CD 37)) (NNS species)))) (VP (VBP are) (VP (VBN referred) (PP (TO to) (NP (NP (RB as) (NNS foxes)) (, ,) (SBAR (WHPP (IN of) (WHNP (WDT which))) (S (NP (QP (RB only) (CD 12)) (NNS species)) (ADVP (RB actually)) (VP (VBP belong) (PP (TO to) (NP (NP (DT the) (NNP Vulpes) (NNS genus)) (PP (IN of) (NP (`` ``) (JJ true) (NNS foxes) ('' '')))))))))))) (. .)))");
//		//String[] tokensArr = "Members of about 37 species are referred to as foxes , of which only 12 species actually belong to the Vulpes genus of `` true foxes '' .".split(" ");
//		results = coref.getCoreferencesFromTrees(trees);
//		for (String s : results)
//		{
//			System.out.println(s);
//		}
//	}
}
