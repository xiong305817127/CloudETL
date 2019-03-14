import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'

class SystemInfo extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    this.state={
       visibleS:false,
    };
   
      const {compareFields,fieldNames,types} = props.model.config;
      let data = [];
      let data1 = [];
        for (let index in fieldNames) {
          console.log(index,"index  fieldname",fieldNames);
        	data.push({
        		fieldNames:fieldNames[index],
        		types:types[index]
        	})
        }
        
        var c = data.concat(data1);
        var obj = Object.assign(data, data1);
       console.log(c,"cdcdcdcdcdcd",obj,"data",data,"data1",data1);
      this.state = {
        dataSource:c,
        dataSource1:data1,
        InputData:[]
      }
  }

   /*文件表格*/
  columns =  [{
    title: '名称',
    dataIndex: 'fieldNames',
    width:"50%",
    key: 'fieldNames',
    editable:true
  }, {
    title: '类型',
    dataIndex: 'types',
    width:"50%",
    key: 'types',
    selectable:true,
    selectArgs:[
       <Select.Option key="0" value="system date (variable)">系统日期（可变）</Select.Option>,
       <Select.Option key="1" value="system date (fixed)">系统日期（固定）</Select.Option>,
       <Select.Option key="2" value="start date range">开始日期范围（转换）</Select.Option>,
       <Select.Option key="3" value="end date range">结束日期范围（转换）</Select.Option>,
       <Select.Option key="4" value="job start date range">开始日期范围（作业）</Select.Option>,
       <Select.Option key="5" value="job end date range">结束日期范围（作业）</Select.Option>,
       <Select.Option key="6" value="yesterday start">昨天00:00:00</Select.Option>,
       <Select.Option key="7" value="yesterday end">昨天23:59:59</Select.Option>,
       <Select.Option key="8" value="today start">今天00:00:00</Select.Option>,
       <Select.Option key="9" value="today end">今天23:59:59</Select.Option>,
       <Select.Option key="10" value="tomorrow start">明天00:00:00</Select.Option>,
       <Select.Option key="11" value="tomorrow end">明天23:59:59</Select.Option>,
       <Select.Option key="12" value="last month start">上月第一天的00:00:00</Select.Option>,
       <Select.Option key="13" value="last month end">上月最后一天的23:59:59</Select.Option>,
       <Select.Option key="14" value="this month start">本月第一天的00:00:00</Select.Option>,
       <Select.Option key="15" value="this month end">本月最后一天的23:59:59</Select.Option>,
       <Select.Option key="16" value="next month start">下个月第一天的00:00:00</Select.Option>,
       <Select.Option key="17" value="next month end">下个月最后一天的23:59:59</Select.Option>,
       <Select.Option key="18" value="copy of step">步骤复制</Select.Option>,
       <Select.Option key="19" value="transformation name">转换名称</Select.Option>,
       <Select.Option key="20" value="transformation file name">转换文件名</Select.Option>,
       <Select.Option key="21" value="User modified">最后修改转换的用户</Select.Option>,
       <Select.Option key="22" value="Date modified">转换的最后修改日期</Select.Option>,
       <Select.Option key="23" value="batch ID">转换ID</Select.Option>,
       <Select.Option key="24" value="job batch ID">父作业ID</Select.Option>,
       <Select.Option key="25" value="Hostname(Network setup)">Hostname(Network setup)</Select.Option>,
       <Select.Option key="26" value="Hostname">主机名</Select.Option>,
       <Select.Option key="27" value="IP address">IP地址</Select.Option>,
       <Select.Option key="28" value="command line argument 1">命令行参数1</Select.Option>,
       <Select.Option key="29" value="command line argument 2">命令行参数2</Select.Option>,
       <Select.Option key="30" value="command line argument 3">命令行参数3</Select.Option>,
       <Select.Option key="31" value="command line argument 4">命令行参数4</Select.Option>,
       <Select.Option key="32" value="command line argument 5">命令行参数5</Select.Option>,
       <Select.Option key="33" value="command line argument 6">命令行参数6</Select.Option>,
       <Select.Option key="34" value="command line argument 7">命令行参数7</Select.Option>,
       <Select.Option key="35" value="command line argument 8">命令行参数8</Select.Option>,
       <Select.Option key="36" value="command line argument 9">命令行参数9</Select.Option>,
       <Select.Option key="37" value="command line argument 10">命令行参数10</Select.Option>,
       <Select.Option key="38" value="kettle version">Kettle版本</Select.Option>,
       <Select.Option key="39" value="kettle build version">Kettle编译版本</Select.Option>,
       <Select.Option key="40" value="kettle build date">Kettle编译日期</Select.Option>,
       <Select.Option key="41" value="Current PID">Current process identifier(PID)</Select.Option>,
       <Select.Option key="42" value="JVM max memory">JVM max memory</Select.Option>,
       <Select.Option key="43" value="JVM total memory">JVM total memory</Select.Option>,
       <Select.Option key="44" value="JVM free memory">JVM free memory</Select.Option>,
       <Select.Option key="45" value="JVM availabe memory">JVM availabe memory</Select.Option>,
       <Select.Option key="46" value="Availabe processors">Availabe processors</Select.Option>,
       <Select.Option key="47" value="jvm cpu tim">JVM CPU time(milliseconds)</Select.Option>,
       <Select.Option key="48" value="total physical memory size">Total physicial memory size(bytes)</Select.Option>,
       <Select.Option key="49" value="total swap space size">Total swap space size(bytes)</Select.Option>,
       <Select.Option key="50" value="committed virtual memory size">Committed virtual size(bytes)</Select.Option>,
       <Select.Option key="51" value="free physical memory size">Free physical memory size(bytes)</Select.Option>,
       <Select.Option key="52" value="free swap space size">Free swap space size(bytes)</Select.Option>,
       <Select.Option key="53" value="last week start">First day of last week 00:00:00</Select.Option>,
       <Select.Option key="54" value="last week end">Last day of last week 23:59:59</Select.Option>,
       <Select.Option key="55" value="last week open end">Last working day of last week 23:59:59</Select.Option>,
       <Select.Option key="56" value="last week start us">First day of last week 00:00:00(US)</Select.Option>,
       <Select.Option key="57" value="last week end us">First day of last week 23:59:59(US)</Select.Option>,
       <Select.Option key="58" value="this week start">First day of this week 00:00:00</Select.Option>,
       <Select.Option key="59" value="this week end">First day of this week 23:59:59</Select.Option>,
       <Select.Option key="60" value="this week open end">Last working day of this week 23:59:59</Select.Option>,
       <Select.Option key="61" value="this week start us">First day of this week 00:00:00</Select.Option>,
       <Select.Option key="62" value="this week end us">First day of this week 23:59:59</Select.Option>,
       <Select.Option key="63" value="next week start">Last working day of this week 23:59:59</Select.Option>,
       <Select.Option key="64" value="next week end">First day of this week 00:00:00(US)</Select.Option>,
       <Select.Option key="65" value="next week start us">First day of this week 23:59:59(US)</Select.Option>,
       <Select.Option key="66" value="next week end us">First day of next week 00:00:00</Select.Option>,
       <Select.Option key="67" value="prev quarter start">First day of next week 23:59:59</Select.Option>,
       <Select.Option key="68" value="prev quarter end">Last working day of next week 23:59:59</Select.Option>,
       <Select.Option key="69" value="this quarter start">First day of next week 00:00:00(US)</Select.Option>,
       <Select.Option key="70" value="this quarter end">First day of next week 23:59:59(US)</Select.Option>,
       /*<Select.Option key="71" value="next quarter start">First day of last quarter 00:00:00</Select.Option>,
       <Select.Option key="72" value="next quarter end">First day of last quarter 23:59:59</Select.Option>,
       <Select.Option key="73" value="prev year start">First day of this quarter 00:00:00</Select.Option>,
       <Select.Option key="74" value="next year start">First day of this quarter 23:59:59</Select.Option>,
       <Select.Option key="75" value="next quarter start">First day of next quarter 00:00:00</Select.Option>,
       <Select.Option key="76" value="next quarter end">First day of next quarter 23:59:59</Select.Option>,*/
       <Select.Option key="77" value="prev year start">First day of next year 00:00:00</Select.Option>,
       <Select.Option key="78" value="prev year end">First day of next year 23:59:59</Select.Option>,
       <Select.Option key="79" value="this year start">First day of this year 00:00:00</Select.Option>,
       <Select.Option key="80" value="this year end">First day of this year 23:59:59</Select.Option>,
       <Select.Option key="81" value="next year start">First day of last year 00:00:00</Select.Option>,
       <Select.Option key="82" value="next year end">First day of last year 23:59:59</Select.Option>,
       <Select.Option key="83" value="Previous job entry result">Previous job entry result</Select.Option>,
       <Select.Option key="84" value="Previous job entry exit status">Previous job entry exit status</Select.Option>,
       <Select.Option key="85" value="previous result entry nr">previous result entry nr</Select.Option>,
       <Select.Option key="86" value="previous result nr errors">previous result nr errors</Select.Option>,
       <Select.Option key="87" value="previous result nr lines input">previous result nr lines input</Select.Option>,
       <Select.Option key="88" value="previous result nr lines output">previous result nr lines output</Select.Option>,
       <Select.Option key="89" value="previous result nr lines read">previous result nr lines read</Select.Option>,
       <Select.Option key="90" value="previous result nr lines updated">previous result nr lines updated</Select.Option>,
       <Select.Option key="91" value="previous result nr lines written">previous result nr lines written</Select.Option>,
       <Select.Option key="92" value="previous result nr lines deleted">previous result nr lines deleted</Select.Option>,
       <Select.Option key="93" value="previous result nr lines rejeted">previous result nr lines rejeted</Select.Option>,
       <Select.Option key="94" value="previous result nr rows">previous result nr rows</Select.Option>,
       <Select.Option key="95" value="previous result is stopped">previous result is stopped</Select.Option>,
       <Select.Option key="96" value="previous result nr files">previous result nr files</Select.Option>,
       <Select.Option key="97" value="previous result nr files retrieved">previous result nr files retrieved</Select.Option>,
       <Select.Option key="98" value="previous result log text">previous result log text</Select.Option>,
      ]
  }];

 hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = () => {
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
     const { compareFields } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }

        let sendFields = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["fieldNames","types"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(compareFields){
            sendFields = compareFields;
          }
      }
      console.log(sendFields,"sendFields");
        let D1 = [];
        for(let index of sendFields){
        	D1.push(index.fieldNames);
        }
         let D2 = [];
        for(let index of sendFields){
        	D2.push(index.types);
        }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "fieldNames":D1,
        "types":D2
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  }

    /*增加字段*/
  handleAdd = ()=>{
    const data = {
        "fieldNames": null,
        "types": null,
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
      }
      
    return (
      <Modal
        visible={visible}
        title="获取系统信息"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
        onCancel = {this.hideModal}>
        <Form >
          <FormItem label="步骤名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>

	          <div style={{margin:"10px 5% 0"}}>
		          <Row style={{marginBottom:"5px"}}>
		            <Col span={12}>
		              <p style={{marginLeft:"5px"}}>字段值：</p>
		            </Col>
		            <Col span={12}>
		              <ButtonGroup size={"small"} style={{float:"right"}} >
		                <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
		                <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
		              </ButtonGroup>
		            </Col>
		          </Row>
		            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
		        </div>
           </Form>
      </Modal>
    );
  }
}
const SystemInfoList = Form.create()(SystemInfo);

export default connect()(SystemInfoList);
