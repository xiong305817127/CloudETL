/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

/**
 * StepParserInterface <br/>
 * @author JW
 * @since 2018年1月26日
 * 
 */
public interface StepDataRelationshipParser {
	
	public static final Log  relationshiplogger = LogFactory.getLog("StepDataRelationshipParser");
	
	/**
	 * 这个接口需实现的功能： <br/>
	 * 
	 * 1. 输出步骤的数据节点（实际引入的外部数据和产生的数据输出，不是输入流或输出流） <br/>
	 * - 方法： <br/>
	 * - 1）判断步骤功能类型：输入步骤、输出步骤、执行步骤、转换步骤、其它步骤 <br/>
	 * - 2）判断步骤数据类型：数据库、接口、文件、或设置变量（环境参数） <br/>
	 * - 3）按上面的方法，封装步骤的输入或输出数据节点（DataNode），其中： <br/>
	 * 		a. 如果该数据节点与输入流或输出流中的field对应，则以field的name为key，把DataNode存到Map中（inputDataNodes或outputDataNodes） <br/>
	 * 		b. 如果没有任何输入流或输出流中的field与该DataNode对应（比如步骤中直接操作数据库中的表，并不产生任何输入输出流），则以该DataNode的GUID字符串作为key，把DataNode存到Map中 <br/>
	 * - 其中，设置变量（环境参数）产生的数据作为DUMMY类型的节点进行处理，GUID中的dummyId为所设置的变量或参数名，且以该DataNode的GUID字符串作为key <br/>
	 * 
	 * - 所有数据节点保存到sdr.inputDataNodes或sdr.outputDataNodes中 <br/>
	 * 
	 * 2. 输出步骤的数据关系 <br/>
	 * - 步骤中可能产生的数据关系： <br/>
	 * - 1）输入流（inputStream）和输出流（outputStream）之间的关系 <br/>
	 * - 2）输入流（inputStream）与输出数据节点（outputDataNodes）的关系 <br/>
	 * - 3）输入数据节点（inputDataNodes）与输出流（outputStream）的关系 <br/>
	 * - 4）输入数据节点（inputDataNodes）与输出数据节点（outputDataNodes）的关系 <br/>
	 * - 其中，输入或输出流中的field作为STEP_OR_ENTRY类型的数据节点进行处理，GUID中保存流中field的名字（field: xxx）！ <br/>
	 * 
	 * - 所有关系保存到sdr.dataRelationship中 <br/>
	 * 
	 * @param transMeta
	 * @param stepMeta
	 * @param sdr
	 */
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)  throws Exception  ;

}
