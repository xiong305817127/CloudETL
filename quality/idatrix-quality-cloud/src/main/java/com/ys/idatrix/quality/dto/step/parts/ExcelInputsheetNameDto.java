/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPExcelInput 的 sheetName 域
 * @author JW
 * @since 2017年6月7日
 *
 */
public class ExcelInputsheetNameDto {

	String sheetName;
	int startRow =0;
	int startColumn =0;
	/**
	 * @return sheetName
	 */
	public String getSheetName() {
		return sheetName;
	}
	/**
	 * @param sheetName 要设置的 sheetName
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	/**
	 * @return startRow
	 */
	public int getStartRow() {
		return startRow;
	}
	/**
	 * @param startRow 要设置的 startRow
	 */
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	/**
	 * @return startColumn
	 */
	public int getStartColumn() {
		return startColumn;
	}
	/**
	 * @param startColumn 要设置的 startColumn
	 */
	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}
	
	
}
