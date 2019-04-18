package com.ys.idatrix.quality.steps.analysis.character;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.util.Version;

public class SynonymsAnalyzer extends Analyzer {

	private String synonymsFile;
	
	public SynonymsAnalyzer(String synonymsFile) {
		this.synonymsFile =synonymsFile ;
	}

	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		
		Tokenizer token = new KeywordTokenizer();
		
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("luceneMatchVersion", Version.LUCENE_5_5_4.toString());
		paramsMap.put("synonyms", synonymsFile);
		SynonymFilterFactory factory = new SynonymFilterFactory(paramsMap);
		try {
			factory.inform(new FilesystemResourceLoader(Paths.get(synonymsFile).getParent(),new ClasspathResourceLoader(SynonymsAnalyzer.class.getClassLoader())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new TokenStreamComponents(token, factory.create(token));
	}
	
	/**
	 * 获取关键字的标准值,不是标准值或者参考值,则返回null
	 * @param fieldValue
	 * @return
	 * @throws IOException
	 */
	public String getSynonyms(String fieldValue) throws IOException {
		
		TokenStream ts = tokenStream("fieldValue", fieldValue);
		try {

			//CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
	        TypeAttribute typeAttr = ts.addAttribute(TypeAttribute.class);
	        ts.reset();
	        while (ts.incrementToken())
	        {
	           // String token = termAttr.toString();
	            String type = typeAttr.type();
	            if("SYNONYM".equalsIgnoreCase(type)) {
	            	return fieldValue ;
	            }
	        }
	        
		}finally {
			  ts.end();
			  ts.close();
		}
		return null;
	}

}
