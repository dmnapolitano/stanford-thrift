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
import general.CoreNLPThriftUtil;


public class StanfordNERThrift 
{
	private StanfordCoreNLP pipeline;
	private NERCombinerAnnotator ner;

	public StanfordNERThrift()
	{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, parse, lemma, ner");
		pipeline = new StanfordCoreNLP(props, true);
		ner = (NERCombinerAnnotator)StanfordCoreNLP.getExistingAnnotator("ner");

//		try
//		{
//			ner = new NERCombinerAnnotator(false, 
//					"edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz",
//					"edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz",
//			"edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
//		}
//		catch (Exception e)
//		{
//			// TODO
//			e.printStackTrace();
//		}
	}


	public List<NamedEntity> getNamedEntitiesFromText(String text)
	{
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		
		List<NamedEntity> allFoundEntities = new ArrayList<NamedEntity>();

		List<CoreMap> sentences = CoreNLPThriftUtil.adjustCharacterOffsets(annotation.get(CoreAnnotations.SentencesAnnotation.class), false); 

		for (CoreMap sentence : sentences)
		{
			List<NamedEntity> thisSentencesEntities = toNamedEntityObjects(sentence);
			//System.out.println(thisSentencesEntities);
			allFoundEntities.addAll(thisSentencesEntities);
		}
		return allFoundEntities;
	}


	public List<NamedEntity> getNamedEntitiesFromTrees(List<String> parseTrees)
	{	
		List<NamedEntity> allFoundEntities = new ArrayList<NamedEntity>();
		
		Annotation sentences = CoreNLPThriftUtil.getAnnotationFromParseTrees(parseTrees);
		ner.annotate(sentences);

		List<CoreMap> sentenceMap = sentences.get(CoreAnnotations.SentencesAnnotation.class); 
		for (CoreMap sentence : sentenceMap)
		{
			List<NamedEntity> thisSentencesEntities = toNamedEntityObjects(sentence);
			//System.out.println(thisSentencesEntities);
			allFoundEntities.addAll(thisSentencesEntities);
		}

		return allFoundEntities;
	}
	
	private List<NamedEntity> toNamedEntityObjects(CoreMap results)
	{
		List<NamedEntity> entities = new ArrayList<NamedEntity>();
		String inline = "";

		final String background = "O";
		String prevTag = background;

		List<CoreLabel> tokens = results.get(CoreAnnotations.TokensAnnotation.class);	
		for (Iterator<CoreLabel> wordIter = tokens.iterator(); wordIter.hasNext();) 
		{
			CoreLabel wi = wordIter.next();
			String tag = StringUtils.getNotNullString(wi.ner());
			String current = StringUtils.getNotNullString(wi.get(CoreAnnotations.OriginalTextAnnotation.class));
			Integer beginPosition = wi.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
			Integer endPosition = wi.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
			if (!tag.equals(prevTag)) 
			{
				if (!prevTag.equals(background) && !tag.equals(background)) 
				{
					inline += "," + endPosition + ")" + "(" + tag + ",";
					inline += current;
				} 
				else if (!prevTag.equals(background)) 
				{
					inline += "," + endPosition + ")" + "(";
					inline += current;
				} 
				else if (!tag.equals(background)) 
				{
					inline += "(" + tag + "," + beginPosition + ",";
					inline += current;
				}
			} 
			else 
			{
				if (!tag.equals(background))
				{
					inline += current;
				}
			}
			if (!tag.equals(background) && !wordIter.hasNext()) 
			{
				inline += "(" + tag + "," + beginPosition + ",";
				inline += current;
				prevTag = background;
			} 
			else 
			{
				prevTag = tag;
			}
			inline += StringUtils.getNotNullString(wi.get(AfterAnnotation.class));
		}

		Pattern pattern = Pattern.compile("\\(([A-Z].+?)\\)");
		Matcher matches = pattern.matcher(inline);
		while (matches.find())
		{
			String[] info = matches.group(0).split("\\,");
			for (int i = 0; i < info.length; i++)
			{
				info[i] = info[i].replaceAll("(^\\()|(\\))$", "");
			}
			entities.add(new NamedEntity(info[2].trim(), info[0], Integer.parseInt(info[1]), Integer.parseInt(info[3])));
		}

		return entities;
	}
}
