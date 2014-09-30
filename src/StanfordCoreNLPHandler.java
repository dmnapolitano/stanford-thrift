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

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.thrift.TApplicationException;
import org.ets.research.nlp.stanford_thrift.coref.StanfordCorefThrift;
import org.ets.research.nlp.stanford_thrift.general.CoreNLPThriftConfig;
import org.ets.research.nlp.stanford_thrift.general.CoreNLPThriftUtil;
import org.ets.research.nlp.stanford_thrift.ner.StanfordNERThrift;
import org.ets.research.nlp.stanford_thrift.parser.StanfordParserThrift;
import org.ets.research.nlp.stanford_thrift.parser.StanfordSRParserThrift;
import org.ets.research.nlp.stanford_thrift.tagger.StanfordTaggerThrift;
import org.ets.research.nlp.stanford_thrift.tokenizer.StanfordTokenizerThrift;
import org.ets.research.nlp.stanford_thrift.tregex.StanfordTregexThrift;

import CoreNLP.NamedEntity;
import CoreNLP.ParseTree;
import CoreNLP.StanfordCoreNLP;
import CoreNLP.TaggedToken;


public class StanfordCoreNLPHandler implements StanfordCoreNLP.Iface
{
	private StanfordParserThrift parser;
	private StanfordNERThrift ner;
	private StanfordCorefThrift coref;
	private StanfordTregexThrift tregex;
	private StanfordTaggerThrift tagger;
	private StanfordTokenizerThrift tokenizer;
	private StanfordSRParserThrift srparser;


	public StanfordCoreNLPHandler(String configFilePath) throws Exception
	{
		try
		{
			System.err.println("Reading in configuration from " + configFilePath + "...");
			CoreNLPThriftConfig config = new CoreNLPThriftConfig(configFilePath);

			String parserModelPath = config.getParserModel();
			if (parserModelPath != null)
			{
				System.err.println("Initializing Parser...");
				parser = new StanfordParserThrift(parserModelPath);
			}

			System.err.println("Initializing Named Entity Recognizer...");
			ner = new StanfordNERThrift(config.getNERModels());

			System.err.println("Initializing Coreference Resolver...");
			coref = new StanfordCorefThrift();

			System.err.println("Initializing Tregex...");
			tregex = new StanfordTregexThrift();

			String taggerModelPath = config.getTaggerModel();
			if (taggerModelPath != null)
			{
				System.err.println("Initializing Tagger...");
				tagger = new StanfordTaggerThrift(taggerModelPath);
			}

			System.err.println("Initializing Tokenizer...");
			tokenizer = new StanfordTokenizerThrift();

			String srModelPath = config.getSRParserModel();
			if (srModelPath != null)
			{
				System.err.println("Initializing shift-reduce org.ets.research.nlp.stanford_thrift.parser...");
				srparser = new StanfordSRParserThrift(config.getSRParserModel());
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/* Begin Stanford Parser methods */
	public List<ParseTree> parse_text(String text, List<String> outputFormat) throws TApplicationException
	{
		if (outputFormat == null)
		{
			List<String> oF = new ArrayList<String>();
			return parser.parse_text(text, oF);
		}
		return parser.parse_text(text, outputFormat);
	}

	public ParseTree parse_tokens(List<String> tokens, List<String> outputFormat) throws TApplicationException
	{
		if (outputFormat == null)
		{
			List<String> oF = new ArrayList<String>();
			return parser.parse_tokens(tokens, oF);
		}
		return parser.parse_tokens(tokens, outputFormat);
	}

	public ParseTree parse_tagged_sentence(String taggedSentence, List<String> outputFormat, String divider) throws TApplicationException
	{
		if (outputFormat == null)
		{
			List<String> oF = new ArrayList<String>();
			return parser.parse_tagged_sentence(taggedSentence, oF, divider);
		}
		return parser.parse_tagged_sentence(taggedSentence, outputFormat, divider);
	}

	public String lexicalize_parse_tree(String tree) throws TApplicationException
	{
		return parser.lexicalize_parse_tree(tree);
	}
	/* End Stanford Parser methods */


	/* Begin Stanford NER methods */
	public List<NamedEntity> get_entities_from_text(String text) throws TApplicationException
	{
		List<ParseTree> parseTreeObjects = parser.parse_text(text, null);
		List<String> parseTrees = CoreNLPThriftUtil.ParseTreeObjectsToString(parseTreeObjects);
		return ner.getNamedEntitiesFromTrees(parseTrees);
	}

	public List<NamedEntity> get_entities_from_tokens(List<String> tokens) throws TApplicationException
	{
		ParseTree parseTreeObject = parser.parse_tokens(tokens, null);
		List<String> parseTrees = new ArrayList<String>();
		parseTrees.add(parseTreeObject.tree);
		return ner.getNamedEntitiesFromTrees(parseTrees);
	}

	public List<NamedEntity> get_entities_from_trees(List<String> trees)
	{
		return ner.getNamedEntitiesFromTrees(trees);
	}
	/* End Stanford NER methods */


	/* Begin Stanford Coref methods */
	public List<String> resolve_coreferences_in_text(String text) throws TApplicationException
	{
		List<ParseTree> parseTreeObjects = parser.parse_text(text, null);
		List<String> parseTrees = CoreNLPThriftUtil.ParseTreeObjectsToString(parseTreeObjects);
		Annotation annotation = ner.annotateForNamedEntities(CoreNLPThriftUtil.getAnnotationFromParseTrees(parseTrees));
		return coref.getCoreferencesFromAnnotation(annotation);
	}

	// TODO: Why did I...?
	public List<String> resolve_coreferences_in_tokenized_sentences(List<String> sentencesWithTokensSeparatedBySpace) throws TApplicationException
	{
		List<String> parseTrees = new ArrayList<String>();
		for (String sentence : sentencesWithTokensSeparatedBySpace)
		{
			List<String> tokens = Arrays.asList(sentence.split(" "));
			ParseTree parseTreeObject = parser.parse_tokens(tokens, null);
			parseTrees.add(parseTreeObject.tree);
		}
		Annotation annotation = CoreNLPThriftUtil.getAnnotationFromParseTrees(parseTrees);
		annotation = ner.annotateForNamedEntities(annotation);
		return coref.getCoreferencesFromAnnotation(annotation);
	}

	public List<String> resolve_coreferences_in_trees(List<String> trees)
	{
		Annotation annotation = CoreNLPThriftUtil.getAnnotationFromParseTrees(trees);
		annotation = ner.annotateForNamedEntities(annotation);
		return coref.getCoreferencesFromAnnotation(annotation);
	}
	/* End Stanford Coref methods */


	/* Begin Stanford Tregex methods */
	public List<String> evaluate_tregex_pattern(String parseTree, String tregexPattern)
	{
		return tregex.evaluateTregexPattern(parseTree, tregexPattern);
	}
	/* End Stanford Tregex methods */


	/* Begin Stanford Tagger methods */
	public List<List<TaggedToken>> tag_text(String untokenizedText)
	{
		return tagger.tag_text(untokenizedText);
	}

	public List<TaggedToken> tag_tokenized_sentence(List<String> tokenizedSentence)
	{
		return tagger.tag_tokenized_sentence(tokenizedSentence);
	}
	/* End Stanford Tagger methods */


	/* Begin Stanford Tokenizer methods */
	public String untokenize_sentence(List<String> sentenceTokens)
	{
		return tokenizer.untokenizeSentence(sentenceTokens);
	}

	public List<List<String>> tokenize_text(String arbitraryText)
	{
		return tokenizer.tokenizeText(arbitraryText);
	}
	/* End Stanford Tokenizer methods */


	/* Begin Stanford Shift-Reduce Parser methods */
	public ParseTree sr_parse_tagged_sentence(String taggedSentence, List<String> outputFormat, String divider) throws TApplicationException
	{
		return srparser.sr_parse_tagged_sentence(taggedSentence, outputFormat, divider);
	}

	public List<ParseTree> sr_parse_text(String untokenizedText, List<String> outputFormat) throws TApplicationException
	{
		try
		{
			List<ParseTree> results = new ArrayList<ParseTree>();
			List<List<TaggedToken>> posTaggedText = tagger.tag_text(untokenizedText);
			for (List<TaggedToken> taggedSentence : posTaggedText)
			{
				List<TaggedWord> taggedWords = CoreNLPThriftUtil.convertTaggedTokensToTaggedWords(taggedSentence);
				results.add(srparser.parseTaggedWords(taggedWords, outputFormat));
			}
			return results;
		}
		catch (Exception e)
		{
			// FIXME
			throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
		}
	}

	public ParseTree sr_parse_tokens(List<String> tokenizedSentence, List<String> outputFormat) throws TApplicationException
	{
		try
		{
			List<TaggedToken> taggedSentence = tagger.tag_tokenized_sentence(tokenizedSentence);
			return srparser.parseTaggedWords(CoreNLPThriftUtil.convertTaggedTokensToTaggedWords(taggedSentence), outputFormat);
		}
		catch (Exception e)
		{
			// FIXME
			throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
		}
	}
	/* End Stanford Shift-Reduce Parser methods */


	public void ping()
	{
		System.out.println("ping()");
	}

	public void zip()
	{
		System.out.println("zip()");
	}
}
