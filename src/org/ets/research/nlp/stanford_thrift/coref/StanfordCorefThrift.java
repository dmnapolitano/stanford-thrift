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


package org.ets.research.nlp.stanford_thrift.coref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ets.research.nlp.stanford_thrift.general.CoreNLPThriftUtil;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;


public class StanfordCorefThrift
{
    private StanfordCoreNLP coref;

    public StanfordCorefThrift()
    {
        // This works, as opposed to creating a
        // edu.stanford.nlp.pipeline.DeterministicCorefAnnotator
        // object directly, because the coreference code runs the
        // parse tree a few times on its own, despite it having
        // been run (and parse trees having been stored) as part
        // of the mandatory NER.  Creating the object this way,
        // the coreference system can create new org.ets.research.nlp.stanford_thrift.parser objects
        // on-the-fly, despite the fact that they're never
        // initialized here.  Very strange.  These parsers
        // seem to use the default PCFG model.
        Properties props = new Properties();
        props.put("annotators", "dcoref");
        coref = new StanfordCoreNLP(props, false);
    }

    public List<String> getCoreferencesFromAnnotation(Annotation annotation)
    {
        coref.annotate(annotation);
        return MUCStyleOutput(annotation);
    }

    @SuppressWarnings("unused")
    private void newStyleCoreferenceGraphOutput(Annotation annotation)
    {
        // display the new-style coreference graph
        //List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        if (corefChains != null)
        {
            for (CorefChain chain : corefChains.values())
            {
                CorefChain.CorefMention representative = chain.getRepresentativeMention();
                for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder())
                {
                    System.out.println(mention);
                    if (mention == representative)
                        continue;
                    // all offsets start at 1!
                    System.out.println("\t"
                            + mention.mentionID + ": (Mention from sentence " + mention.sentNum + ", "
                            + "Head word = " + mention.headIndex
                            + ", (" + mention.startIndex + "," + mention.endIndex + ")"
                            + ")"
                            + " -> "
                            + "(Representative from sentence " + representative.sentNum + ", "
                            + "Head word = " + representative.headIndex
                            + ", (" + representative.startIndex + "," + representative.endIndex + ")"
                            + "), that is: \"" +
                            mention.mentionSpan + "\" -> \"" +
                            representative.mentionSpan + "\"");
                }
            }
        }
    }

    private List<String> MUCStyleOutput(Annotation annotation)
    {
        Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        Map<Integer, Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>> mentionMap =
                new HashMap<Integer, Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>>();

        List<String> mucOutput = new ArrayList<String>();

        for (CorefChain chain : corefChains.values())
        {
            CorefChain.CorefMention ref = chain.getRepresentativeMention();

            for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder())
            {
                if (mention != ref)
                {
                    // first add the mention itself
                    Pair<CorefChain.CorefMention,CorefChain.CorefMention> mentions =
                            new Pair<CorefChain.CorefMention, CorefChain.CorefMention>(mention, ref);
                    if (mentionMap.containsKey(mention.sentNum))
                    {
                        Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> value =
                                mentionMap.get(mention.sentNum);
                        value.put(mention.startIndex, mentions);
                        mentionMap.put(mention.sentNum, value);
                    }
                    else
                    {
                        Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> startIndexToMentionMap =
                                new HashMap<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>();
                        startIndexToMentionMap.put(mention.startIndex, mentions);
                        mentionMap.put(mention.sentNum, startIndexToMentionMap);
                    }

                    // now make sure the representative is there (TODO make this code less redundant)
                    Pair<CorefChain.CorefMention,CorefChain.CorefMention> refMention =
                            new Pair<CorefChain.CorefMention, CorefChain.CorefMention>(ref, ref);
                    if (mentionMap.containsKey(ref.sentNum))
                    {
                        Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> value =
                                mentionMap.get(ref.sentNum);
                        value.put(ref.startIndex, refMention);
                        mentionMap.put(ref.sentNum, value);
                    }
                    else
                    {
                        Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> startIndexToMentionMap =
                                new HashMap<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>>();
                        startIndexToMentionMap.put(ref.startIndex, refMention);
                        mentionMap.put(ref.sentNum, startIndexToMentionMap);
                    }
                }
            }
        }


        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (Integer sentenceNum : mentionMap.keySet())
        {
            CoreMap currentSentence = sentences.get(sentenceNum-1);
            Map<Integer, Pair<CorefChain.CorefMention, CorefChain.CorefMention>> currentSetOfMentions =
                    mentionMap.get(sentenceNum);
            CorefChain.CorefMention lastMention = null;
            String outputString = "";
            for (CoreLabel token : currentSentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                if (currentSetOfMentions.containsKey(token.index()))
                {
                    lastMention = currentSetOfMentions.get(token.index()).first();
                    CorefChain.CorefMention ref = currentSetOfMentions.get(token.index()).second();
                    outputString += "<COREF ID=\"" + lastMention.mentionID + "\"";
                    if (lastMention.mentionID != ref.mentionID)
                    {
                        outputString += " REF=\"" + ref.mentionID + "\"";
                    }
                    outputString += ">";
                }
                if (lastMention != null && token.index() == lastMention.endIndex)
                {
                    outputString += "</COREF> ";
                }
                outputString += token.word() + " ";
            }
            mucOutput.add(CoreNLPThriftUtil.closeHTMLTags(outputString.replaceAll(" </", "</")));
        }

        return mucOutput;
    }
}
