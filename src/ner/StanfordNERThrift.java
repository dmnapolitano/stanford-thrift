package ner;

import CoreNLP.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.NERCombinerAnnotator;
import edu.stanford.nlp.util.CoreMap;

import general.CoreNLPThriftUtil;


public class StanfordNERThrift 
{
	/*
	 * TODO: When I add in the bit for the Stanford Tagger, add a method here that recognizes
	 * named entities from POS-tagged text, similar to the one that recognizes them from parse
	 * trees.
	 */
	
//	private StanfordCoreNLP pipeline;
	private NERCombinerAnnotator ner;

	public StanfordNERThrift()
	{
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, parse, lemma, ner");
//		pipeline = new StanfordCoreNLP(props, true);
//		ner = (NERCombinerAnnotator)StanfordCoreNLP.getExistingAnnotator("ner");

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


//	public List<NamedEntity> getNamedEntitiesFromText(String text)
//	{
//		Annotation annotation = new Annotation(text);
//		pipeline.annotate(annotation);

//		List<CoreMap> sentences = CoreNLPThriftUtil.adjustCharacterOffsets(annotation.get(CoreAnnotations.SentencesAnnotation.class), false); 
//		return toNamedEntityObjects(sentences);
//	}


	public List<NamedEntity> getNamedEntitiesFromTrees(List<String> parseTrees)
	{	
		Annotation annotation = CoreNLPThriftUtil.getAnnotationFromParseTrees(parseTrees);
		ner.annotate(annotation);
		List<CoreMap> sentenceMap = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		return toNamedEntityObjects(sentenceMap);
	}
	
	public Annotation annotateForNamedEntities(Annotation annotation)
	{
		Annotation withNE = annotation.copy();
		ner.annotate(withNE);
		return withNE;
	}
	
	private List<NamedEntity> toNamedEntityObjects(List<CoreMap> results)
	{
		List<NamedEntity> entities = new ArrayList<NamedEntity>();
		
		Stack<CoreLabel> namedEntityStack = new Stack<CoreLabel>();
		for (CoreMap sentence : results)
		{
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			for (Iterator<CoreLabel> wordIter = tokens.iterator(); wordIter.hasNext();)
			{
				CoreLabel wi = wordIter.next();
				if (namedEntityStack.empty() || wi.ner().equals(namedEntityStack.peek().ner()))
				{
					namedEntityStack.push(wi);
				}
				else
				{
					String tag = "";
					String entity = "";
					int startIndex = namedEntityStack.peek().beginPosition();
					int endIndex = 0;
					while (!namedEntityStack.empty())
					{
						CoreLabel popped = namedEntityStack.pop();
						tag = popped.ner();
						entity = popped.word() + " " + entity;
						if (popped.endPosition() > endIndex)
						{
							endIndex = popped.endPosition();
						}
					}
					if (!tag.equals("O"))
					{
						entities.add(new NamedEntity(entity.trim(), tag, startIndex, endIndex));
					}
					namedEntityStack.push(wi);
				}
			}
		}
	
		return entities;
	}
}
