package tregex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		Set<String> nodes = matches.getNodeNames();
		while (matches.find())
		{
			foundMatches.add(matches.getMatch().pennString());
			for (String node : nodes)
			{
				foundMatches.add(matches.getNode(node).pennString());
			}
		}
		return foundMatches;
	}
}
