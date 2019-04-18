/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPTableOutput 的 Field域
 * @author JW
 * @since 05-12-2017
 *
 */
public class TableOutputFieldDto {
	
    private String columnName;
    private String streamName;
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getColumnName() {
        return columnName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
    public String getStreamName() {
        return streamName;
    }

}
