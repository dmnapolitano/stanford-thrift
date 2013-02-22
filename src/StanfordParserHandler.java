import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

// Generated code
import parser.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;

public class StanfordParserHandler implements StanfordParser.Iface {

    private LexicalizedParser parser;
    private boolean customOutputOptionsSet;
    private boolean customParserOptionsSet;
    private TreePrint treePrinter;

    public StanfordParserHandler(String modelFile) {
        loadModel(modelFile);
        treePrinter = new TreePrint("oneline", "", new PennTreebankLanguagePack());
        customOutputOptionsSet = false;
        customParserOptionsSet = false;
    }

    private void loadModel(String modelFile)
    {
        if (modelFile.equals("")) {
            parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", new String[]{});
        }
        else {
            parser = LexicalizedParser.loadModel(modelFile, new String[]{});
        }
    }

    private void setOptions(List<String> options)
    {
        String outputFormats = "";
        String outputFormatOptions = "";

        // for output formatting
        if (options.contains("-outputFormat"))
        {
            int argIndex = options.indexOf("-outputFormat");
            outputFormats = options.get(argIndex+1);
            options.remove(argIndex + 1);
            options.remove(argIndex);
            customOutputOptionsSet = true;
        }
        if (options.contains("-outputFormatOptions"))
        {
            int argIndex = options.indexOf("-outputFormatOptions");
            outputFormatOptions = options.get(argIndex+1);
            options.remove(argIndex + 1);
            options.remove(argIndex);
            customOutputOptionsSet = true;
        }

        treePrinter = new TreePrint(outputFormats, outputFormatOptions, new PennTreebankLanguagePack());

        // for everything else
        if (!options.isEmpty())
        {
            String[] remainingOptions = new String[options.size()];
            options.toArray(remainingOptions);
            parser.setOptionFlags(remainingOptions);
            customParserOptionsSet = true;
        }
    }

    private void resetOptions(String modelFile)
    {
        if (customParserOptionsSet)
        {
            loadModel(modelFile);
            customParserOptionsSet = false;
        }

        if (customOutputOptionsSet)
        {
            treePrinter = new TreePrint("oneline", "", new PennTreebankLanguagePack());
            customOutputOptionsSet = false;
        }
    }

    public List<ParseTree> parse_text(String sentence) throws TApplicationException
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        List<ParseTree> results = new ArrayList<ParseTree>();

        try
        {
        	// assume no tokenization was done; use Stanford's default tokenizer
        	DocumentPreprocessor preprocess = new DocumentPreprocessor(new StringReader(sentence));
        	Iterator<List<HasWord>> foundSentences = preprocess.iterator();
        	while (foundSentences.hasNext())
        	{
        		Tree parseTree = parser.apply(foundSentences.next());
        		treePrinter.printTree(parseTree, pw);
        		results.add(new ParseTree(sw.getBuffer().toString().trim(), parseTree.score()));
        	}
        }
        catch (Exception e)
        {
        	throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
        }
        	
        // Odds are threads will be reused, so reset the options every time
        // resetOptions()
        return results;
    }

    public List<ParseTree> parse_tokens(List<String> tokens) throws TApplicationException
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        List<ParseTree> results = new ArrayList<ParseTree>();
    	
    	// assume an array of tokens was passed in
    	if (tokens.contains("\n"))
    	{
    		StringBuilder builder = new StringBuilder();
    		// at least one sentence worth of tokens
    		for(String token : tokens)
    		{
    			builder.append(token+" ");
    		}
    		String[] multipleSentences = builder.toString().split("\n");
    		for (String s : multipleSentences)
    		{
    			try
    			{
    				List<CoreLabel> crazyStanfordFormat = Sentence.toCoreLabelList(s.trim().split(" "));
    				Tree parseTree = parser.apply(crazyStanfordFormat);
    				treePrinter.printTree(parseTree, pw);
    				results.add(new ParseTree(sw.getBuffer().toString().trim(), parseTree.score()));
    			}
    			catch (Exception e)
    			{
    				throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
    			}
    		}
    	}
    	else
    	{
    		try
    		{
    			// a single sentence worth of tokens
    			String[] tokenArray = new String[tokens.size()];
    			tokens.toArray(tokenArray);
    			List<CoreLabel> crazyStanfordFormat = Sentence.toCoreLabelList(tokenArray);
    			Tree parseTree = parser.apply(crazyStanfordFormat);
    			treePrinter.printTree(parseTree, pw);
    			results.add(new ParseTree(sw.getBuffer().toString().trim(), parseTree.score()));
    		}
    		catch (Exception e)
    		{
    			//throw new TException(e.getMessage(), e.getCause());
    			throw new TApplicationException(TApplicationException.INTERNAL_ERROR, "bar");
    		}
    	}
    	// Odds are threads will be reused, so reset the options every time. :\
    	//resetOptions();
    	return results;
    }
    
    public void ping() {
        System.out.println("ping()");
    }

    public void zip() {
        System.out.println("zip()");
    }
}
