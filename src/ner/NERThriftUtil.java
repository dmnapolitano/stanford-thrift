package ner;

import CoreNLP.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AfterAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class NERThriftUtil 
{
	public static List<CoreMap> adjustCharacterOffsets(List<CoreMap> sentences, boolean setOriginalText)
	{
		List<CoreMap> sentencesCopy = sentences;
	
		for (CoreMap sentence : sentencesCopy)
		{
			List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			int characterCount = 0;
			for (int i = 0; i < sentenceTokens.size(); i++)
			{
				CoreLabel token = sentenceTokens.get(i);
				if (setOriginalText)
				{
					token.set(CoreAnnotations.OriginalTextAnnotation.class, token.get(CoreAnnotations.TextAnnotation.class) + " ");
				}
				int startCharacterCount = characterCount;
				int endCharacterCount = startCharacterCount + token.get(CoreAnnotations.OriginalTextAnnotation.class).length();
				token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, startCharacterCount);
				token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, endCharacterCount);
				sentenceTokens.set(i, token);
				characterCount = endCharacterCount;
			}
			sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
		}
		return sentencesCopy;
	}
	
	public static List<NamedEntity> toNamedEntityObjects(CoreMap results)
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
