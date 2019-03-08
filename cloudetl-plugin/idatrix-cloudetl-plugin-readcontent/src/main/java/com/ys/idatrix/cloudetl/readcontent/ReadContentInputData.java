package com.ys.idatrix.cloudetl.readcontent;

import java.io.LineNumberReader;

import org.apache.commons.vfs2.FileObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POITextExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.spreadsheet.KWorkbook;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta.TYPE_CODE;

public class ReadContentInputData  extends BaseStepData implements StepDataInterface {

	 public FileInputList files;
	 public TYPE_CODE type;
	 public RowMetaInterface outputRowMeta;
	 
	 public boolean isFileRow = false ;//是否文件中 一行/一页/一节 为一条数据
	 
	 //正在处理的文件的索引号
	 public int filenr = 0;
	 //正在读取的行数
	 public int rownr = 0 ;
	 //正在使用的文件
	 public FileObject  file ;
	//正在使用的文件名
	 public String  filename ;
	 //是否开始下一个文件
	 public boolean isNextFile = true ;
	 
	 //word
	 public HWPFDocument wordDoc ; //doc
	 public XWPFDocument wordDocx; //docx
		
	 public int  sectionNumber =0 ; //word文档的 小节 数量
	 
	 public POITextExtractor wordExtractor ;

	 //pdf
	 public PDDocument pdfDocument;
	 public PDFTextStripper pdfts ; 
	 public int pageNumber = 0 ;
	 
	 //text
	 public LineNumberReader textReader ;
	 
	 //Excel 
	 public KWorkbook workbook;
	 public String[] sheetNames ;
	 public int sheetnr = 0;
	 	
	 //PPt
	 public POITextExtractor pptExtractor ;
	 @SuppressWarnings("rawtypes")
	public SlideShow slide;
	 public int slidenr = 0;
}
