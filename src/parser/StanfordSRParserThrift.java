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


package parser;

import java.util.List;

import org.apache.thrift.TApplicationException;

import CoreNLP.ParseTree;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import general.CoreNLPThriftUtil;

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
			TreePrint treePrinter = ParserUtil.setOptions(outputFormat, tlp);

			// a single sentence worth of tagged text, better be properly tokenized >:D
			Tree parseTree = model.apply(CoreNLPThriftUtil.getListOfTaggedWordsFromTaggedSentence(taggedSentence, divider));
			// TODO: Do these parse trees have scores, like the lexicalized ones do?
			return new ParseTree(ParserUtil.TreeObjectToString(parseTree, treePrinter), parseTree.score());
		}
		catch (Exception e)
		{
			// FIXME
			throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
		}
	}
}
