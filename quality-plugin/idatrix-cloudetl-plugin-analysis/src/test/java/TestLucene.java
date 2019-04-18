import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class TestLucene {

	public static void main(String[] args) throws Exception {
		
		String keyword = "地球";
		
//		createIndex(indexPath, "tudou");
//
//		IndexSearcher indexSearcher = null;
//		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//		indexSearcher = new IndexSearcher(indexReader);
		@SuppressWarnings("resource")
		Analyzer analyzer = new SynonymsAnalyzer("D:\\aaaa\\synonyms.txt");
		
		TokenStream s = analyzer.tokenStream("fieldValue", keyword);
		displayTokens(s);
		
//		QueryParser queryParser = new QueryParser("fieldValue", analyzer);
//		Query query = queryParser.parse(keyword);
		
//		TopDocs td = indexSearcher.search(query,10);
//		for (int i = 0; i < td.totalHits; i++) {
//			Document document = indexSearcher.doc(td.scoreDocs[i].doc);
//			System.out.println( document.get("fieldValue") );
//		}
	}
	
    public static String displayTokens(TokenStream ts) throws IOException
    {
    	StringBuffer sb = new StringBuffer();
        CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
        TypeAttribute typeAttr = ts.addAttribute(TypeAttribute.class);
        ts.reset();
        while (ts.incrementToken())
        {
            String token = termAttr.toString();
            System.out.println(typeAttr.type()+" | "+token);
//            System.out.print(offsetAttribute.startOffset() + "-" + offsetAttribute.endOffset() + "[" + token + "] ");
        }
        System.out.println();
        ts.end();
        ts.close();
        return sb.toString();
    }
	
	public static void createIndex(String indexPath,String value) throws IOException{
		Directory directory = FSDirectory.open(Paths.get(indexPath));
		Analyzer analyzer = new SynonymsAnalyzer("D:\\aaaa\\synonyms.txt");
		
//		IKAnalyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig( analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		
		indexWriter.deleteAll();
		
		Document document1 = new Document();
		document1.add(new TextField("fieldValue", value, Store.YES));
		indexWriter.addDocument(document1);

		indexWriter.close();
	}
	

}

class SynonymsAnalyzer extends Analyzer {
	
	String synonymsFile;
	
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
			factory.inform(new FilesystemResourceLoader(Paths.get(synonymsFile).getParent()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new TokenStreamComponents(token, factory.create(token));
	}

}
