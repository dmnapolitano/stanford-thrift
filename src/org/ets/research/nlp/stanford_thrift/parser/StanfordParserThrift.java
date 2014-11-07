/*
  Apache Thrift Server for Stanford CoreNLP (stanford-thrift)
  Copyright (C) 2013 Diane M. Napolitano, Educational Testing Service

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation, version 2
  of the License.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package org.ets.research.nlp.stanford_thrift.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.thrift.TApplicationException;
import org.ets.research.nlp.stanford_thrift.general.CoreNLPThriftUtil;

import CoreNLP.ParseTree;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.DefaultPaths;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeFunctions;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.Trees;

public class StanfordParserThrift
{

    private LexicalizedParser parser;
    private TreePrint treePrinter;
    private TreebankLanguagePack tlp;

    public StanfordParserThrift(String modelFile)
    {
        loadModel(modelFile);
        tlp = new PennTreebankLanguagePack();
        // TODO: Initialize treePrinter here in case it could ever not be initialized below?
    }

    private void loadModel(String modelFile)
    {
        if (modelFile.equals("") || modelFile == null) {
            parser = LexicalizedParser.loadModel(DefaultPaths.DEFAULT_PARSER_MODEL, new String[]{});
        }
        else {
            parser = LexicalizedParser.loadModel(modelFile, new String[]{});
        }
    }

    public List<ParseTree> parse_text(String text, List<String> outputFormat) throws TApplicationException
    {
        List<ParseTree> results = new ArrayList<ParseTree>();

        try
        {
            treePrinter = ParserUtil.setOptions(outputFormat, tlp);

            // assume no tokenization was done; use Stanford's default org.ets.research.nlp.stanford_thrift.tokenizer
            DocumentPreprocessor preprocess = new DocumentPreprocessor(new StringReader(text));
            Iterator<List<HasWord>> foundSentences = preprocess.iterator();
            while (foundSentences.hasNext())
            {
                Tree parseTree = parser.apply(foundSentences.next());
                results.add(new ParseTree(ParserUtil.TreeObjectToString(parseTree, treePrinter), parseTree.score()));
            }
        }
        catch (Exception e)
        {
            // FIXME
            throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
        }

        return results;
    }

    /**
     * @param tokens One sentence worth of tokens at a time.
     * @return A ParseTree object of the String representation of the tree, plus its probability.
     * @throws TApplicationException
     */
    public ParseTree parse_tokens(List<String> tokens, List<String> outputFormat) throws TApplicationException
    {
        try
        {
            treePrinter = ParserUtil.setOptions(outputFormat, tlp);

            // a single sentence worth of tokens
            String[] tokenArray = new String[tokens.size()];
            tokens.toArray(tokenArray);
            List<CoreLabel> crazyStanfordFormat = Sentence.toCoreLabelList(tokenArray);
            Tree parseTree = parser.apply(crazyStanfordFormat);
            return new ParseTree(ParserUtil.TreeObjectToString(parseTree, treePrinter), parseTree.score());
        }
        catch (Exception e)
        {
            // FIXME
            throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
        }
    }

    public ParseTree parse_tagged_sentence(String taggedSentence, List<String> outputFormat, String divider) throws TApplicationException
    {
        try
        {
            treePrinter = ParserUtil.setOptions(outputFormat, tlp);

            // a single sentence worth of tagged text, better be properly tokenized >:D
            Tree parseTree = parser.apply(CoreNLPThriftUtil.getListOfTaggedWordsFromTaggedSentence(taggedSentence, divider));
            return new ParseTree(ParserUtil.TreeObjectToString(parseTree, treePrinter), parseTree.score());
        }
        catch (Exception e)
        {
            // FIXME
            throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
        }
    }

    /** If one were to call any of these other methods to get a parse tree for some input sentence
     * with the -outputFormatOptions flag of "lexicalize", they would receive their parse tree,
     * in the -outputFormat of their choice, with every leaf marked with it's head word.
     * This function does exactly that on an existing parse tree.
     * NOTE that this WILL re-lexicalize a pre-lexicalized tree, so don't pass in a tree that
     * has been lexicalized and expect to get back the same thing as what you passed in.
     */
    public String lexicalize_parse_tree(String tree) throws TApplicationException
    {
        try
        {
            Tree parseTree = Tree.valueOf(tree);
            Tree lexicalizedTree = Trees.lexicalize(parseTree, tlp.headFinder());
            treePrinter = ParserUtil.setOptions(null, tlp); // use defaults
            Function<Tree, Tree> a = TreeFunctions.getLabeledToDescriptiveCoreLabelTreeFunction();
            lexicalizedTree = a.apply(lexicalizedTree);
            return ParserUtil.TreeObjectToString(lexicalizedTree, treePrinter);
        }
        catch (Exception e)
        {
            // FIXME
            throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
        }
    }
}

