import org.apache.commons.lang.CharUtils;

public class test {

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws  Exception {

//		FileInputStream in = new FileInputStream( new File("C:\\Users\\Administrator\\Desktop\\2018“最佳拍档”英语比赛活动信息(螺岭外国语).doc"));
//		
//		HWPFDocument wordDoc = new HWPFDocument(in);
//		
//		OPCPackage opcPackage = OPCPackage.open( in );
//		XWPFWordExtractor wordExtractor = new XWPFWordExtractor(opcPackage);
//		System.out.println(wordExtractor.getText());
		
		String content = "abd人类哈\r哈哈cds\n队午晚餐；  交通：包含行程中城\t" ;
		
		System.out.println(content.toString());
		System.out.println(content.toString().length());
		
		StringBuffer contentBuffer = new StringBuffer();
		int sz = content.length();
		for (int i = 0; i < sz; i++) {
			char c = content.charAt(i);
			System.out.print(c);
			System.out.print(" | ");
			if( isChinese(c) ) {
				System.out.println(true);
				contentBuffer.append(c);
			}else {
				System.out.println(false);
			}
		}
		
		System.out.println(contentBuffer.toString());
		System.out.println(contentBuffer.toString().length());
		
	}
	
	
	public static boolean isChinese(char c) {  
		
		if(CharUtils.isAscii(c) ) {
			return true ;
		}
		
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }

}
