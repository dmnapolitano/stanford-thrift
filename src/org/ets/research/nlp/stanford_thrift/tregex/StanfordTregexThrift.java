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


package org.ets.research.nlp.stanford_thrift.tregex;

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
