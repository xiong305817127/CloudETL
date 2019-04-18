/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.trans.stepdetail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.steps.csvinput.CsvInput;
import org.pentaho.di.trans.steps.textfileinput.EncodingType;
import org.pentaho.di.trans.steps.textfileinput.TextFileInput;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;
import org.pentaho.di.www.CarteSingleton;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;

/**
 * CsvInput  related Detail Service
 * @author XH
 * @since 2017年6月12日
 *
 */
@SuppressWarnings("deprecation")
@Service
public class CsvInputDetailService implements StepDetailService {


	/* 
	 * 
	 */
	@Override
	public String getStepDetailType() {
		return "CsvInput";
	}

	/* 
	 *  flag: getFields
	 */
	@Override
	public List<Object> dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getFields":
			return getCsvFields(param);
		default:
			return null;

		}
	}

	/**
	 * @param param
	 * @return Csv Fields list
	 * @throws Exception 
	 */
	private List<Object> getCsvFields(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName", "delimiter", "enclosure","headerPresent","encoding"); 

		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String filePath = FilePathUtil.getRealFileName(owner,fileName);

		InputStream inputStream = null;
		FileObject fileObject = null;
		try {

			String delimiter = params.get("delimiter").toString();
			String enclosure = params.get("enclosure").toString();
			String realEncoding = params.get("encoding").toString();
			boolean headerPresent = Boolean.valueOf(params.get("headerPresent").toString());

			fileObject = KettleVFS.getFileObject( filePath );
			if ( !( fileObject instanceof LocalFile ) ) {
				// We can only use NIO on local files at the moment, so that's what we
				// limit ourselves to.
				throw new KettleException( "Only Local Files are Supported" );
			}
			inputStream = KettleVFS.getInputStream( fileObject );
			InputStreamReader reader;
			if ( Utils.isEmpty( realEncoding ) ) {
				reader = new InputStreamReader( inputStream );
			} else {
				reader = new InputStreamReader( inputStream, realEncoding );
			}
			EncodingType encodingType = EncodingType.guessEncodingType( reader.getEncoding() );

			// Read a line of data to determine the number of rows...
			//
			String line =  TextFileInput.getLine(  CarteSingleton.getInstance().getLog(), reader, encodingType, TextFileInputMeta.FILE_FORMAT_UNIX, new StringBuilder( 1000 ) );

			// Split the string, header or data into parts...
			//
			String[] fieldNames =  CsvInput.guessStringsFromLine( CarteSingleton.getInstance().getLog() , line, delimiter, enclosure, null );

			if ( !headerPresent ) {
				// Don't use field names from the header...
				// Generate field names F1 ... F10
				//
				DecimalFormat df = new DecimalFormat( "000" );
				for ( int i = 0; i < fieldNames.length; i++ ) {
					fieldNames[i] = "Field_" + df.format( i );
				}
			} else {
				if ( !Utils.isEmpty( enclosure ) ) {
					for ( int i = 0; i < fieldNames.length; i++ ) {
						if ( fieldNames[i].startsWith( enclosure )
								&& fieldNames[i].endsWith( enclosure ) && fieldNames[i].length() > 1 ) {
							fieldNames[i] = fieldNames[i].substring( 1, fieldNames[i].length() - 1 );
						}
					}
				}
			}

			// Trim the names to make sure...
			//
			for ( int i = 0; i < fieldNames.length; i++ ) {
				fieldNames[i] = Const.trim( fieldNames[i] );
			}

			// Update the GUI
			//
			List<Object> result=Lists.newArrayList();
			for ( int i = 0; i < fieldNames.length; i++ ) {
				result.add(new Object[]{fieldNames[i], ValueMetaInterface.TYPE_STRING  });
			}

			//		      if ( samples >= 0 ) {
			//		    	  doScan();
			//		      }
			return result;

		}  finally {
			try {
				if(fileObject != null){
					fileObject.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
			} catch ( Exception e ) {
				// Ignore close errors
			}
		}
	}

	//	 private String doScan( int samples,List<String> fileNameList) throws KettleException {
	//
	//		    String line = "";
	//		    long fileLineNumber = 0;
	//
	//		    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
	//
	//		    int nrfields = meta.inputFiles.inputFields.length;
	//
	//		    RowMetaInterface outputRowMeta = new RowMeta();
	//		    meta.getFields( outputRowMeta, null, null, null, transMeta, null, null );
	//
	//		    // Remove the storage meta-data (don't go for lazy conversion during scan)
	//		    for ( ValueMetaInterface valueMeta : outputRowMeta.getValueMetaList() ) {
	//		      valueMeta.setStorageMetadata( null );
	//		      valueMeta.setStorageType( ValueMetaInterface.STORAGE_TYPE_NORMAL );
	//		    }
	//
	//		    RowMetaInterface convertRowMeta = outputRowMeta.cloneToType( ValueMetaInterface.TYPE_STRING );
	//
	//		    // How many null values?
	//		    int[] nrnull = new int[nrfields]; // How many times null value?
	//
	//		    // String info
	//		    String[] minstr = new String[nrfields]; // min string
	//		    String[] maxstr = new String[nrfields]; // max string
	//		    boolean[] firststr = new boolean[nrfields]; // first occ. of string?
	//
	//		    // Date info
	//		    boolean[] isDate = new boolean[nrfields]; // is the field perhaps a Date?
	//		    int[] dateFormatCount = new int[nrfields]; // How many date formats work?
	//		    boolean[][] dateFormat = new boolean[nrfields][Const.getDateFormats().length]; // What are the date formats that
	//		    // work?
	//		    Date[][] minDate = new Date[nrfields][Const.getDateFormats().length]; // min date value
	//		    Date[][] maxDate = new Date[nrfields][Const.getDateFormats().length]; // max date value
	//
	//		    // Number info
	//		    boolean[] isNumber = new boolean[nrfields]; // is the field perhaps a Number?
	//		    int[] numberFormatCount = new int[nrfields]; // How many number formats work?
	//		    boolean[][] numberFormat = new boolean[nrfields][Const.getNumberFormats().length]; // What are the number format
	//		                                                                                       // that work?
	//		    double[][] minValue = new double[nrfields][Const.getDateFormats().length]; // min number value
	//		    double[][] maxValue = new double[nrfields][Const.getDateFormats().length]; // max number value
	//		    int[][] numberPrecision = new int[nrfields][Const.getNumberFormats().length]; // remember the precision?
	//		    int[][] numberLength = new int[nrfields][Const.getNumberFormats().length]; // remember the length?
	//
	//		    for ( int i = 0; i < nrfields; i++ ) {
	//		      BaseFileInputField field = meta.inputFiles.inputFields[i];
	//
	//		      if ( log.isDebug() ) {
	//		        debug = "init field #" + i;
	//		      }
	//
	//		      if ( replaceMeta ) { // Clear previous info...
	//
	//		        field.setName( meta.inputFiles.inputFields[i].getName() );
	//		        field.setType( meta.inputFiles.inputFields[i].getType() );
	//		        field.setFormat( "" );
	//		        field.setLength( -1 );
	//		        field.setPrecision( -1 );
	//		        field.setCurrencySymbol( dfs.getCurrencySymbol() );
	//		        field.setDecimalSymbol( "" + dfs.getDecimalSeparator() );
	//		        field.setGroupSymbol( "" + dfs.getGroupingSeparator() );
	//		        field.setNullString( "-" );
	//		        field.setTrimType( ValueMetaInterface.TRIM_TYPE_NONE );
	//		      }
	//
	//		      nrnull[i] = 0;
	//		      minstr[i] = "";
	//		      maxstr[i] = "";
	//		      firststr[i] = true;
	//
	//		      // Init data guess
	//		      isDate[i] = true;
	//		      for ( int j = 0; j < Const.getDateFormats().length; j++ ) {
	//		        dateFormat[i][j] = true;
	//		        minDate[i][j] = Const.MAX_DATE;
	//		        maxDate[i][j] = Const.MIN_DATE;
	//		      }
	//		      dateFormatCount[i] = Const.getDateFormats().length;
	//
	//		      // Init number guess
	//		      isNumber[i] = true;
	//		      for ( int j = 0; j < Const.getNumberFormats().length; j++ ) {
	//		        numberFormat[i][j] = true;
	//		        minValue[i][j] = Double.MAX_VALUE;
	//		        maxValue[i][j] = -Double.MAX_VALUE;
	//		        numberPrecision[i][j] = -1;
	//		        numberLength[i][j] = -1;
	//		      }
	//		      numberFormatCount[i] = Const.getNumberFormats().length;
	//		    }
	//
	//		    TextFileInputMeta strinfo = (TextFileInputMeta) meta.clone();
	//		    for ( int i = 0; i < nrfields; i++ ) {
	//		      strinfo.inputFiles.inputFields[i].setType( ValueMetaInterface.TYPE_STRING );
	//		    }
	//
	//		    // Sample <samples> rows...
	//		    debug = "get first line";
	//
	//		    StringBuilder lineBuffer = new StringBuilder( 256 );
	//		    int fileFormatType = meta.getFileFormatTypeNr();
	//
	//		    // If the file has a header we overwrite the first line
	//		    // However, if it doesn't have a header, take a new line
	//		    //
	//
	//		    line = TextFileInputUtils.getLine( log, reader, encodingType, fileFormatType, lineBuffer );
	//		    fileLineNumber++;
	//		    int skipped = 1;
	//
	//		    if ( meta.content.header ) {
	//
	//		      while ( line != null && skipped < meta.content.nrHeaderLines ) {
	//		        line = TextFileInputUtils.getLine( log, reader, encodingType, fileFormatType, lineBuffer );
	//		        skipped++;
	//		        fileLineNumber++;
	//		      }
	//		    }
	//		    int linenr = 1;
	//
	//		    List<StringEvaluator> evaluators = new ArrayList<StringEvaluator>();
	//
	//		    // Allocate number and date parsers
	//		    DecimalFormat df2 = (DecimalFormat) NumberFormat.getInstance();
	//		    DecimalFormatSymbols dfs2 = new DecimalFormatSymbols();
	//		    SimpleDateFormat daf2 = new SimpleDateFormat();
	//
	//		    boolean errorFound = false;
	//		    while ( !errorFound && line != null && ( linenr <= samples || samples == 0 ) && !monitor.isCanceled() ) {
	//		      monitor.subTask( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Task.ScanningLine", ""
	//		          + linenr ) );
	//		      if ( samples > 0 ) {
	//		        monitor.worked( 1 );
	//		      }
	//
	//		      if ( log.isDebug() ) {
	//		        debug = "convert line #" + linenr + " to row";
	//		      }
	//		      RowMetaInterface rowMeta = new RowMeta();
	//		      meta.getFields( rowMeta, "stepname", null, null, transMeta, null, null );
	//		      // Remove the storage meta-data (don't go for lazy conversion during scan)
	//		      for ( ValueMetaInterface valueMeta : rowMeta.getValueMetaList() ) {
	//		        valueMeta.setStorageMetadata( null );
	//		        valueMeta.setStorageType( ValueMetaInterface.STORAGE_TYPE_NORMAL );
	//		      }
	//
	//		      String delimiter = transMeta.environmentSubstitute( meta.content.separator );
	//		      String enclosure = transMeta.environmentSubstitute( meta.content.enclosure );
	//		      String escapeCharacter = transMeta.environmentSubstitute( meta.content.escapeCharacter );
	//		      Object[] r =
	//		          TextFileInputUtils.convertLineToRow( log, new TextFileLine( line, fileLineNumber, null ), strinfo, null, 0,
	//		              outputRowMeta, convertRowMeta, FileInputList.createFilePathList( transMeta, meta.inputFiles.fileName,
	//		                  meta.inputFiles.fileMask, meta.inputFiles.excludeFileMask, meta.inputFiles.fileRequired, meta
	//		                      .includeSubFolderBoolean() )[0], rownumber, delimiter, enclosure, escapeCharacter, null,
	//		              new BaseFileInputStepMeta.AdditionalOutputFields(), null, null, false, null, null, null, null, null );
	//
	//		      if ( r == null ) {
	//		        errorFound = true;
	//		        continue;
	//		      }
	//		      rownumber++;
	//		      for ( int i = 0; i < nrfields && i < r.length; i++ ) {
	//		        StringEvaluator evaluator;
	//		        if ( i >= evaluators.size() ) {
	//		          evaluator = new StringEvaluator( true );
	//		          evaluators.add( evaluator );
	//		        } else {
	//		          evaluator = evaluators.get( i );
	//		        }
	//
	//		        String string = rowMeta.getString( r, i );
	//
	//		        if ( i == 0 ) {
	//		          System.out.println();
	//		        }
	//		        evaluator.evaluateString( string );
	//		      }
	//
	//		      fileLineNumber++;
	//		      if ( r != null ) {
	//		        linenr++;
	//		      }
	//
	//		      // Grab another line...
	//		      //
	//		      line = TextFileInputUtils.getLine( log, reader, encodingType, fileFormatType, lineBuffer );
	//		    }
	//
	//		    monitor.worked( 1 );
	//		    monitor.setTaskName( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Task.AnalyzingResults" ) );
	//
	//		    // Show information on items using a dialog box
	//		    //
	//		    StringBuilder message = new StringBuilder();
	//		    message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.ResultAfterScanning", ""
	//		        + ( linenr - 1 ) ) );
	//		    message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.HorizontalLine" ) );
	//
	//		    for ( int i = 0; i < nrfields; i++ ) {
	//		      BaseFileInputField field = meta.inputFiles.inputFields[i];
	//		      StringEvaluator evaluator = evaluators.get( i );
	//		      List<StringEvaluationResult> evaluationResults = evaluator.getStringEvaluationResults();
	//
	//		      // If we didn't find any matching result, it's a String...
	//		      //
	//		      if ( evaluationResults.isEmpty() ) {
	//		        field.setType( ValueMetaInterface.TYPE_STRING );
	//		        field.setLength( evaluator.getMaxLength() );
	//		      } else {
	//		        StringEvaluationResult result = evaluator.getAdvicedResult();
	//		        if ( result != null ) {
	//		          // Take the first option we find, list the others below...
	//		          //
	//		          ValueMetaInterface conversionMeta = result.getConversionMeta();
	//		          field.setType( conversionMeta.getType() );
	//		          field.setTrimType( conversionMeta.getTrimType() );
	//		          field.setFormat( conversionMeta.getConversionMask() );
	//		          field.setDecimalSymbol( conversionMeta.getDecimalSymbol() );
	//		          field.setGroupSymbol( conversionMeta.getGroupingSymbol() );
	//		          field.setLength( conversionMeta.getLength() );
	//		          field.setPrecision( conversionMeta.getPrecision() );
	//
	//		          nrnull[i] = result.getNrNull();
	//		          minstr[i] = result.getMin() == null ? "" : result.getMin().toString();
	//		          maxstr[i] = result.getMax() == null ? "" : result.getMax().toString();
	//		        }
	//		      }
	//
	//		      message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.FieldNumber", "" + ( i
	//		          + 1 ) ) );
	//
	//		      message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.FieldName", field
	//		          .getName() ) );
	//		      message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.FieldType", field
	//		          .getTypeDesc() ) );
	//
	//		      switch ( field.getType() ) {
	//		        case ValueMetaInterface.TYPE_NUMBER:
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.EstimatedLength", ( field
	//		              .getLength() < 0 ? "-" : "" + field.getLength() ) ) );
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.EstimatedPrecision", field
	//		              .getPrecision() < 0 ? "-" : "" + field.getPrecision() ) );
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.NumberFormat", field
	//		              .getFormat() ) );
	//
	//		          if ( !evaluationResults.isEmpty() ) {
	//		            if ( evaluationResults.size() > 1 ) {
	//		              message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.WarnNumberFormat" ) );
	//		            }
	//
	//		            for ( StringEvaluationResult seResult : evaluationResults ) {
	//		              String mask = seResult.getConversionMeta().getConversionMask();
	//
	//		              message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.NumberFormat2",
	//		                  mask ) );
	//		              message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.TrimType", seResult
	//		                  .getConversionMeta().getTrimType() ) );
	//		              message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.NumberMinValue",
	//		                  seResult.getMin() ) );
	//		              message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.NumberMaxValue",
	//		                  seResult.getMax() ) );
	//
	//		              try {
	//		                df2.applyPattern( mask );
	//		                df2.setDecimalFormatSymbols( dfs2 );
	//		                double mn = df2.parse( seResult.getMin().toString() ).doubleValue();
	//		                message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.NumberExample", mask,
	//		                    seResult.getMin(), Double.toString( mn ) ) );
	//		              } catch ( Exception e ) {
	//		                if ( log.isDetailed() ) {
	//		                  log.logDetailed( "This is unexpected: parsing [" + seResult.getMin() + "] with format [" + mask
	//		                      + "] did not work." );
	//		                }
	//		              }
	//		            }
	//		          }
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.NumberNrNullValues", ""
	//		              + nrnull[i] ) );
	//		          break;
	//		        case ValueMetaInterface.TYPE_STRING:
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.StringMaxLength", ""
	//		              + field.getLength() ) );
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.StringMinValue",
	//		              minstr[i] ) );
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.StringMaxValue",
	//		              maxstr[i] ) );
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.StringNrNullValues", ""
	//		              + nrnull[i] ) );
	//		          break;
	//		        case ValueMetaInterface.TYPE_DATE:
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateMaxLength", field
	//		              .getLength() < 0 ? "-" : "" + field.getLength() ) );
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateFormat", field
	//		              .getFormat() ) );
	//		          if ( dateFormatCount[i] > 1 ) {
	//		            message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.WarnDateFormat" ) );
	//		          }
	//		          if ( !Utils.isEmpty( minstr[i] ) ) {
	//		            for ( int x = 0; x < Const.getDateFormats().length; x++ ) {
	//		              if ( dateFormat[i][x] ) {
	//		                message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateFormat2", Const
	//		                    .getDateFormats()[x] ) );
	//		                Date mindate = minDate[i][x];
	//		                Date maxdate = maxDate[i][x];
	//		                message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateMinValue",
	//		                    mindate.toString() ) );
	//		                message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateMaxValue",
	//		                    maxdate.toString() ) );
	//
	//		                daf2.applyPattern( Const.getDateFormats()[x] );
	//		                try {
	//		                  Date md = daf2.parse( minstr[i] );
	//		                  message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateExample", Const
	//		                      .getDateFormats()[x], minstr[i], md.toString() ) );
	//		                } catch ( Exception e ) {
	//		                  if ( log.isDetailed() ) {
	//		                    log.logDetailed( "This is unexpected: parsing [" + minstr[i] + "] with format [" + Const
	//		                        .getDateFormats()[x] + "] did not work." );
	//		                  }
	//		                }
	//		              }
	//		            }
	//		          }
	//		          message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.DateNrNullValues", ""
	//		              + nrnull[i] ) );
	//		          break;
	//		        default:
	//		          break;
	//		      }
	//		      if ( nrnull[i] == linenr - 1 ) {
	//		        message.append( BaseMessages.getString( PKG, "TextFileCSVImportProgressDialog.Info.AllNullValues" ) );
	//		      }
	//		      message.append( Const.CR );
	//
	//		    }
	//
	//		    monitor.worked( 1 );
	//		    monitor.done();
	//
	//		    return message.toString();
	//
	//		  }
	//
	//	

}
