package general;

import java.io.BufferedReader;
import java.io.FileReader;
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
		return (List<String>)config.get("ner_models");
	}
}
