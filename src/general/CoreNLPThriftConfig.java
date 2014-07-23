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

package general;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreNLPThriftConfig
{
	private Map<String, Object> config;

	public CoreNLPThriftConfig(String configFile) throws Exception
	{
		try
		{
			config = new HashMap<String, Object>();
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#"))
				{
					// It's a comment; ignore it.
					continue;
				}
				String[] info = line.split("=");
				if (info[1].startsWith(" \""))
				{
					// single string
					config.put(info[0].trim(), new String(info[1].replaceAll("\"", "").trim()));
				}
				else if (info[1].startsWith(" ["))
				{
					// list
					String[] options = info[1].replaceAll("[\\[\\]\" ]", "").split(",");
					List<String> optionsList = Arrays.asList(options);
					config.put(info[0].trim(), optionsList);
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public String getParserModel()
	{
		return (String)config.get("parser_model");
	}

	@SuppressWarnings("unchecked")
	public List<String> getNERModels()
	{
		Object nerModels = config.get("ner_models");
		if (nerModels.getClass().getCanonicalName().contains("String"))
		{
			List<String> returnVal = new ArrayList<String>();
			returnVal.add((String)nerModels);
			return returnVal;
		}
		return (List<String>)config.get("ner_models");
	}

	public String getTaggerModel()
	{
		return (String)config.get("tagger_model");
	}

	public String getSRParserModel()
	{
		return (String)config.get("shift-reduce_model");
	}
}
