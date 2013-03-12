package ner;

import CoreNLP.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.AfterAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.NERCombinerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotatorUtils;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;


public class StanfordNERThrift 
{
//	public class NamedEntity
//	{
//		public String entity;
//		public String tag;
//		public int startOffset;
//		public int endOffset;
//
//		public NamedEntity(String e, String t, int s, int n)
//		{
//			entity = e;
//			tag = t;
//			startOffset = s;
//			endOffset = n;
//		}
//
//		public String toString()
//		{
//			return tag + " = " + "\"" + entity + "\" (" + startOffset + "," + endOffset + ")";
//		}
//	}

	private StanfordCoreNLP pipeline;
	private NERCombinerAnnotator ner;

	public StanfordNERThrift()
	{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, parse, lemma, ner");
		pipeline = new StanfordCoreNLP(props, true);

		try
		{
			ner = new NERCombinerAnnotator(false, 
					"edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz",
					"edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz",
			"edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
		}
		catch (Exception e)
		{
			// TODO
			e.printStackTrace();
		}
	}


	public List<NamedEntity> getNamedEntitiesFromText(String text)
	{
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		
		List<NamedEntity> allFoundEntities = new ArrayList<NamedEntity>();

		List<CoreMap> sentences = NERThriftUtil.adjustCharacterOffsets(annotation.get(CoreAnnotations.SentencesAnnotation.class), false); 

		for (CoreMap sentence : sentences)
		{
			List<NamedEntity> thisSentencesEntities = NERThriftUtil.toNamedEntityObjects(sentence);
			//System.out.println(thisSentencesEntities);
			allFoundEntities.addAll(thisSentencesEntities);
		}
		return allFoundEntities;
	}


	public List<NamedEntity> getNamedEntitiesFromTrees(List<String> parseTrees)
	{	
		List<NamedEntity> allFoundEntities = new ArrayList<NamedEntity>();
		
		List<CoreMap> sentences = new ArrayList<CoreMap>();
		List<String> allTokens = new ArrayList<String>();
		int tokenOffset = 0;
		for (String tree : parseTrees)
		{
			List<String> tokens = new ArrayList<String>();
			String[] firstSplit = tree.split("\\) ");
			for (String f : firstSplit)
			{
				String[] secondSplit = f.split("\\(");
				String[] tagAndToken = secondSplit[secondSplit.length-1].trim().replaceAll("\\)+$", "").split(" ");
				tokens.add(tagAndToken[1]);
			}
			allTokens.addAll(tokens);
			String[] tokensArr = new String[tokens.size()];
			tokens.toArray(tokensArr);
			List<CoreLabel> sentenceTokens = Sentence.toCoreLabelList(tokensArr);
			String originalText = Sentence.listToString(tokens);

			CoreMap sentence = new Annotation(originalText);
			sentence.set(CharacterOffsetBeginAnnotation.class, 0);
			sentence.set(CharacterOffsetEndAnnotation.class, sentenceTokens.get(sentenceTokens.size() - 1).get(TextAnnotation.class).length());
			sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
			sentence.set(CoreAnnotations.TokenBeginAnnotation.class, tokenOffset);
			tokenOffset += sentenceTokens.size();
			sentence.set(CoreAnnotations.TokenEndAnnotation.class, tokenOffset);
			ParserAnnotatorUtils.fillInParseAnnotations(false, true, sentence, Tree.valueOf(tree));

			sentences.add(sentence);
		}

		Annotation allSentences = new Annotation(Sentence.listToString(allTokens));
		allSentences.set(CoreAnnotations.SentencesAnnotation.class, 
				NERThriftUtil.adjustCharacterOffsets(sentences, true));

		ner.annotate(allSentences);

		List<CoreMap> sentenceMap = allSentences.get(CoreAnnotations.SentencesAnnotation.class); 
		for (CoreMap sentence : sentenceMap)
		{
			List<NamedEntity> thisSentencesEntities = NERThriftUtil.toNamedEntityObjects(sentence);
			//System.out.println(thisSentencesEntities);
			allFoundEntities.addAll(thisSentencesEntities);
		}

		return allFoundEntities;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		StanfordNERThrift test = new StanfordNERThrift();
		String testSentences = "My name is Diane and I live in New Jersey.  I sometimes go to New York.  The Food and Drug Administration is an organization.";
		test.getNamedEntitiesFromText(testSentences);
		List<String> trees = new ArrayList<String>();
		trees.add("(ROOT (S (S (NP (PRP$ My) (NN name)) (VP (VBZ is) (NP (NNP Diane)))) (CC and) (S (NP (PRP I)) (VP (VBP live) (PP (IN in) (NP (NNP New) (NNP Jersey))))) (. .)))");
		trees.add("(ROOT (S (NP (PRP I)) (ADVP (RB sometimes)) (VP (VBP go) (PP (TO to) (NP (NNP New) (NNP York)))) (. .)))");
		trees.add("(ROOT (S (NP (DT The) (NNP Food) (CC and) (NNP Drug) (NNP Administration)) (VP (VBZ is) (NP (DT an) (NN organization))) (. .)))");
		test.getNamedEntitiesFromTrees(trees);
	}
}
