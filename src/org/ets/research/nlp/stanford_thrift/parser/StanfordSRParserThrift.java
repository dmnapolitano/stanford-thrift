/*
  Apache Thrift Server for Stanford CoreNLP (stanford-thrift)
  Copyright (C) 2014 Diane M. Napolitano, Educational Testing Service

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

import java.util.List;

import org.apache.thrift.TApplicationException;
import org.ets.research.nlp.stanford_thrift.general.CoreNLPThriftUtil;

import CoreNLP.ParseTree;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class StanfordSRParserThrift {

	private ShiftReduceParser model;
	private TreebankLanguagePack tlp;

	public StanfordSRParserThrift(String srParserModel)
	{
		model = ShiftReduceParser.loadModel(srParserModel);
		tlp = new PennTreebankLanguagePack();
	}

	public ParseTree sr_parse_tagged_sentence(String taggedSentence, List<String> outputFormat, String divider) throws TApplicationException
	{
		try
		{
			// a single sentence worth of tagged text, better be properly tokenized >:D
			List<TaggedWord> taggedWords = CoreNLPThriftUtil.getListOfTaggedWordsFromTaggedSentence(taggedSentence, divider);
			return parseTaggedWords(taggedWords, outputFormat);
		}
		catch (Exception e)
		{
			// FIXME
			throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
		}
	}

	public ParseTree parseTaggedWords(List<TaggedWord> taggedWords, List<String> outputFormat) throws Exception
	{
		TreePrint treePrinter = ParserUtil.setOptions(outputFormat, tlp);
		Tree parseTree = model.apply(taggedWords);
		// TODO: Do these parse trees have scores, like the lexicalized ones do?
		return new ParseTree(ParserUtil.TreeObjectToString(parseTree, treePrinter), parseTree.score());
	}
}
