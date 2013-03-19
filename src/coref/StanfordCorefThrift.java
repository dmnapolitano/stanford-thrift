package coref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import ner.StanfordNERThrift;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.DeterministicCorefAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Triple;


public class StanfordCorefThrift 
{
	public static List<String> getCoreferencesFromText(String text)
	{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, parse, lemma, ner, dcoref");
		//props.put("dcoref.score", true);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props, true);
		//StanfordCoreNLP pipeline = new StanfordCoreNLP((Properties)null);
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);

		//		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		//		for (Entry<Integer, CorefChain> e : corefChains.entrySet())
		//		{
		//			System.out.println(e);
		//		}
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences)
		{
			System.out.println(sentences.indexOf(sentence)+1 + ": " + sentence);
		}

		//		newStyleCoreferenceGraphOutput(annotation);
		MUCStyleOutput(annotation);
		return null;
	}

	public static void newStyleCoreferenceGraphOutput(Annotation annotation)
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

	public static void MUCStyleOutput(Annotation annotation)
	{
		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		Map<Integer, Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>> mentionMap
			= new HashMap<Integer, Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>>();
		
		int lastSentenceNumber = 1;
		Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> startIndexToMentionMap
			= new HashMap<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>();
		
		for (CorefChain chain : corefChains.values()) 
		{
			CorefChain.CorefMention representative = chain.getRepresentativeMention();
			for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder())
			{
				Pair<CorefChain.CorefMention,CorefChain.CorefMention> mentions 
					= new Pair<CorefChain.CorefMention, CorefChain.CorefMention>(mention, representative);
				if (mentionMap.containsKey(lastSentenceNumber))
				{
					Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> value =
						mentionMap.get(lastSentenceNumber);
					value.put(mention.startIndex, mentions);
					mentionMap.put(lastSentenceNumber, value);
				}
				else if (lastSentenceNumber == mention.sentNum)
				{
					startIndexToMentionMap.put(mention.startIndex, mentions);
				}
				else
				{
					mentionMap.put(lastSentenceNumber, startIndexToMentionMap);
					lastSentenceNumber = mention.sentNum;
					System.err.println(lastSentenceNumber);
					startIndexToMentionMap = new HashMap<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>();
				}
			}
		}
		System.out.println(mentionMap);

		
//		for (Integer sentenceNum : mentionMap.keySet())
//		{
//			CorefChain.CorefMention lastMention = null;
//			String outputString = "";
//			for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class))
//			{
//				if (mentionMap.containsKey(token.index()))
//			{
//				lastMention = startIndexToMentionMap.get(token.index()).first();
//				CorefChain.CorefMention representative = startIndexToMentionMap.get(token.index()).second();
//				outputString += "<COREF ID=\"" + lastMention.mentionID + "\"";
//				if (lastMention.mentionID != representative.mentionID)
//				{
//					outputString += " REF=\""
//						+ representative.mentionID;
//				}
//				outputString += ">";
//			}
//			if (lastMention != null && token.index() == lastMention.endIndex)
//			{
//				outputString += "</COREF> ";
//			}
//			outputString += token.word() + " ";
//		}
//		System.out.println(outputString.replaceAll(" </", "</"));
	}


	//	public static List<String> getCoreferencesFromTrees(List<String> parseTrees)
	//	{
	//		StanfordNERThrift ner = new StanfordNERThrift();
	//		Annotation annotation = ner.getNamedEntityAnnotationFromTrees(parseTrees);
	//		
	//		DeterministicCorefAnnotator coref = new DeterministicCorefAnnotator(new Properties());
	//		coref.annotate(annotation);
	//		
	//		// display the new-style coreference graph
	//		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	//		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	//		if (corefChains != null && sentences != null) 
	//		{
	//			for (CorefChain chain : corefChains.values()) 
	//			{
	//				CorefChain.CorefMention representative = chain.getRepresentativeMention();
	//				for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) 
	//				{
	//					if (mention == representative)
	//						continue;
	//					// all offsets start at 1!
	//					System.out.println("\t(" + mention.sentNum + "," +
	//							mention.headIndex + ",[" +
	//							mention.startIndex + "," +
	//							mention.endIndex + ")) -> (" +
	//							representative.sentNum + "," +
	//							representative.headIndex + ",[" +
	//							representative.startIndex + "," +
	//							representative.endIndex + ")), that is: \"" +
	//							mention.mentionSpan + "\" -> \"" +
	//							representative.mentionSpan + "\"");
	//				}
	//			}
	//		}
	//		return null;
	//	}



	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		String testSentences = "Snow is precipitation in the form of flakes of crystalline water ice that fall from clouds. Since snow is composed of small ice particles, it is a granular material. It has an open and therefore soft structure, unless subjected to external pressure. Snowflakes come in a variety of sizes and shapes. Types that fall in the form of a ball due to melting and refreezing, rather than a flake, are known as hail, ice pellets or snow grains.";
		//String testSentences = "By proposing a meeting date, Eastern moved one step closer toward reopening current high-cost contract agreements with its unions.";
		getCoreferencesFromText(testSentences);
		//List<String> trees = new ArrayList<String>();
		//trees.add("(ROOT (S (NP (NP (NNS Members)) (PP (IN of) (NP (QP (RB about) (CD 37)) (NNS species)))) (VP (VBP are) (VP (VBN referred) (PP (TO to) (NP (NP (RB as) (NNS foxes)) (, ,) (SBAR (WHPP (IN of) (WHNP (WDT which))) (S (NP (QP (RB only) (CD 12)) (NNS species)) (ADVP (RB actually)) (VP (VBP belong) (PP (TO to) (NP (NP (DT the) (NNP Vulpes) (NNS genus)) (PP (IN of) (NP (`` ``) (JJ true) (NNS foxes) ('' '')))))))))))) (. .)))");
		//String[] tokensArr = "Members of about 37 species are referred to as foxes , of which only 12 species actually belong to the Vulpes genus of `` true foxes '' .".split(" ");
		//getCoreferencesFromTrees(trees);
	}
}
