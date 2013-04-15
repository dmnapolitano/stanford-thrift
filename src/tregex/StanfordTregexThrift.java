package tregex;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class StanfordTregexThrift 
{
	public StanfordTregexThrift()
	{
		
	}
	
	public List<String> evaluateTregexPattern(String parseTree, String tregexPattern)
	{
		List<String> foundMatches = new ArrayList<String>();
		
		TregexPattern pattern = TregexPattern.compile(tregexPattern);
		TregexMatcher matches = pattern.matcher(Tree.valueOf(parseTree));
		while (matches.find())
		{
			foundMatches.add(matches.getMatch().pennString());
		}
		return foundMatches;
	}
}
