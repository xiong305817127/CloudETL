/**
 * Created by Administrator on 2018/1/17.
 */
import React from 'react'
import { connect } from 'dva'
import { Form,Button,Table,Row,Col } from 'antd'
import Modal from "components/Modal.js";

const FormItem = Form.Item;
const existArgs = ["engineName", "engineType" ,"breakpointsContinueEnable","breakpointsforceLocal","clearingLog", "safeMode", "gatherMetrics", "rebootAutoRun", "logLevel", "breakpointsRemote", "breakpointsContinue"];

const columns = [
	{
    title: '参数',
    dataIndex: 'key',
    key:'key',
    width:"50%"
  }, {
    title: '值',
    dataIndex: 'value',
    key:'value',
    width:"50%"
  },
]


const Excute = ({ analysisDetails,dispatch })=>{

  const { visible2,excute } = analysisDetails;
  const { engineName,engineType,clearingLog,safeMode,gatherMetrics,rebootAutoRun,params,logLevel,breakpointsRemote,breakpointsContinue } = excute;
	
	console.log(excute,"执行");

	let dataSource = [];

	if(params){
		Object.keys(params).forEach(index=>{
			if(!existArgs.includes(index)){
				dataSource.push({	key:index,value:params[index] })
			}
		})
	}
 
  const handleHide = ()=>{
    dispatch({
      type:"analysisDetails/save",
      payload:{
        visible2:false
      }
    });
  };

  const getlogLevel = (logLevel)=>{
    switch (logLevel){
      case "Nothing":
            return "没有日志";
      case "Error":
        return "错误日志";
      case "Minimal":
        return "最小日志";
      case "Rowlevel":
        return "行级日志(非常详细)";
      case "Detailed":
        return "详细日志";
      case "Debug":
        return "调试";
      default:
        return "基本日志";
    }
  };



  return(
    <Modal
      title={"执行配置"}
			wrapClassName="vertical-center-modal"
			className="ant-advanced-search-form"
			style={{ paddingBottom:0 }}
      visible={visible2}
			zIndex={1020}
			width={800}
      onCancel={handleHide}
      footer={[
        <Button key="submit" type="primary" size="large"  onClick={handleHide}>关闭</Button>
      ]}
      onCancel={handleHide}
    >
      <Form>
				<Row gutter={40}>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="引擎名称" >
							<span className="ant-form-text">{engineName}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="执行方式">
							<span className="ant-form-text">{engineType}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="日志级别">
							<span className="ant-form-text">{getlogLevel(logLevel)}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="启用安全模式">
							<span className="ant-form-text">{safeMode ? "是" : "否"}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="收集性能指标">
							<span className="ant-form-text">{gatherMetrics ? "是" : "否"}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="运行前清除日志">
							<span className="ant-form-text">{clearingLog ? "是" : "否"}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="重启服务(运行中)后自动运行">
							<span className="ant-form-text">{rebootAutoRun ? "是" : "否"}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="如果运行中断,下次从中断处恢复运行">
							<span className="ant-form-text">{breakpointsContinue ? "是" : "否"}</span>
						</FormItem>
					</Col>
					<Col span={12}>
						<FormItem  style={{ marginBottom:8 }}  label="是否远程自动从中断处恢复运行">
							<span className="ant-form-text">{breakpointsRemote ? "是" : "否"}</span>
						</FormItem>
					</Col>
				</Row>
			</Form>
			<p>执行参数：</p>
			<Table dataSource={dataSource} columns={columns}  size={"small"} pagination={false}  scroll={{ y:300 }}  />
    </Modal>
  )
};


const RunExcute = Form.create()(Excute);

export default connect(({ analysisDetails }) => ({
  analysisDetails
}))(RunExcute);
