package parser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class ParserUtil
{
	private ParserUtil() {}

	public static String TreeObjectToString(Tree tree, TreePrint tp)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tp.printTree(tree, pw);
		return sw.getBuffer().toString().trim();
	}

	public static TreePrint setOptions(List<String> outputOptions, TreebankLanguagePack tlp) throws Exception
	{
		String outputFormatStr = "oneline";  // default
		String outputFormatOptionsStr = "";

		// for output formatting
		if (outputOptions != null && outputOptions.size() > 0)
		{
			int ofIndex = outputOptions.indexOf("-outputFormat");
			int ofoIndex = outputOptions.indexOf("-outputFormatOptions");

			if (ofIndex >= 0)
			{
				outputFormatStr = outputOptions.get(ofIndex+1);
			}
			if (ofoIndex >= 0)
			{
				outputFormatOptionsStr = outputOptions.get(ofoIndex+1);
			}
			if (ofIndex < 0 && ofoIndex < 0)
			{
				throw new Exception("Invalid option(s): " + outputOptions.toString());
			}
		}

		return new TreePrint(outputFormatStr, outputFormatOptionsStr, tlp);

		// for everything else; disabled for now
		//        if (!options.isEmpty())
		//        {
		//        	String[] remainingOptions = new String[options.size()];
		//        	options.toArray(remainingOptions);
		//        	parser.setOptionFlags(remainingOptions);
		//        	customParserOptionsSet = true;
		//        }
	}
}
