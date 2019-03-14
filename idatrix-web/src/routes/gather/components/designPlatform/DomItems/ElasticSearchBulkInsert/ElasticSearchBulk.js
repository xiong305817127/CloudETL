import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Tabs,Row,Col,Upload,Card,Cascader,message } from 'antd';
import Modal from "components/Modal.js";
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const FormItem = Form.Item;
const Option = Select.Option;
import EditTable from '../../../common/EditTable';

class ElasticSearchBulk extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { fields,servers,settings } = props.model.config;
      let data = [];
      let data1 = [];
      let data2 = [];

      if(fields){
        let count = 0;
        for(let index of fields){

          data.push({
            "key":count,
            ...index
          });
          count++;
        }
      }
      if(servers){
        let count = 0;
        for(let index of servers){

          data1.push({
            "key":count,
            ...index
          });
          count++;
        }
      }
      if(settings){
        let count = 0;
        for(let index of settings){

          data2.push({
            "key":count,
            ...index
          });
          count++;
        }
      }
      this.state = {
       ElasticList1:data1,
       ElasticList2:data,
       ElasticList3:data2,
        InputData:[]
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      });
    })
  };

  setModelHide (){
	      const {  dispatch } = this.props;
        dispatch({
          type:'items/hide',
          visible:false
        });
	  }

  columns1 = [
    {
    title: '地址',
    dataIndex: 'address',
    key: 'address',
    width:"50%",
    editable:true
  },{
    title: '端口',
    dataIndex: 'port',
    key: 'port',
    width:"50%",
    editable:true
  }];
  handleAdd(){
    const data = {
      "address": "",
      "port": ""
    };
    this.refs.servers.handleAdd(data);
  }
  handleDeleteFields(){
    this.refs.servers.handleDelete();
  }

  columns2 = [
    {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width:"50%",
    selectable:true
  },{
    title: '目标名称',
    dataIndex: 'targetName',
    key: 'targetName',
    width:"50%",
    selectable:true
  }];
  handleAdd1(){
    const data = {
      "name": "",
      "targetName": ""
    };
    this.refs.fields.handleAdd(data);
  }
  handleDeleteFields1(){
    this.refs.fields.handleDelete();
  }
  handleGetFields(){
      const {InputData} = this.state;
      let count = 0;
      let args = [];
      for(let index of InputData){
          args.push({
             key:count,
            name:index.name,
            targetName:index.name
          });
        count++;
      }
    this.refs.fields.updateTable(args,count);
  }
  initFuc(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      name:options
    });
  };


  columns3 = [
      {
    title: 'Setting',
    dataIndex: 'setting',
    key: 'setting',
    width:"50%",
    editable:true
  },{
    title: '值',
    dataIndex: 'value',
    key: 'value',
    width:"50%",
    editable:true
  }];
  handleAdd2(){
    const data = {
      "setting": "",
      "value": ""
    }
    this.refs.settings.handleAdd(data);
  }
  handleDeleteFields2(){
    this.refs.settings.handleDelete();
  }

  handleCreate(){
    const form =  this.props.form;
    const { panel,transname,description,key,saveStep,config,text,formatTable } = this.props.model;
    const { fields,servers,settings } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      let sendFields2 = [];
      if(this.refs.servers){
        if(this.refs.servers.state.dataSource.length>0){
          let args = [ "address", "port"];
          sendFields = formatTable(this.refs.servers.state.dataSource,args);
        }
      }else{
        if(servers){
          sendFields = servers
        }
      }
      if(this.refs.fields){
        if(this.refs.fields.state.dataSource.length>0){
          let args = ["name", "targetName"];

          sendFields1 = formatTable(this.refs.fields.state.dataSource,args);
        }
      }else{
        if(fields){
          sendFields1 = fields
        }
      }
      if(this.refs.settings){
        if(this.refs.settings.state.dataSource.length>0){
          let args = [ "setting", "value"];
          sendFields2 = formatTable(this.refs.settings.state.dataSource,args);
        }
      }else{
        if(settings){
          sendFields2 = settings
        }
      }
      console.log(values);

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
	    "index": values.index,
	    "type": values.type,
	    "batchSize": values.batchSize,
	    "timeout": values.timeout,
	    "timeoutUnit": values.timeoutUnit,
	    "jsonField": values.jsonField,
	    "idOutField": values.idOutField,
	    "idInField": values.idInField,
	    "overWriteIfSameId": values.overWriteIfSameId,
	    "useOutput": values.useOutput,
	    "stopOnError": values.stopOnError,
	    "servers": sendFields,
	    "fields":sendFields1,
	    "settings":sendFields2,
	    "jsonInsert": values.jsonInsert
	   };
	   saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  }


testIndex(name){
   const { getFieldValue } = this.props.form;
   const { getDetails,transname,text,panel,formatTable,config } = this.props.model;
   const { servers,settings } = config;

    let sendFields =[];
    let sendFields1 =[];
    if(this.refs.servers){
      if(this.refs.servers.state.dataSource.length>0){
        let args = [ "address", "port"];
        sendFields = formatTable(this.refs.servers.state.dataSource,args);
        sendFields.map(index=> index.port = parseInt(index.port))
      }
    }else{
      if(servers){
        sendFields = servers
      }
    }
    if(this.refs.settings){
      if(this.refs.settings.state.dataSource.length>0){
        let args = [ "setting", "value"];
        sendFields1 = formatTable(this.refs.settings.state.dataSource,args);
      }
    }else{
      if(settings){
        sendFields1 = settings
      }
    }

  if(sendFields.length>0){
    let obj = {};
    obj.transName = transname;
    obj.stepName = text;
    obj.detailType = panel;
    if(name === "index"){
      obj.detailParam = {
        flag:"test",
        servers:sendFields,
        settings:sendFields1,
        index:getFieldValue("index")
      };
    }else{
      obj.detailParam = {
        flag:"test",
        servers:sendFields,
        settings:sendFields1, //alisa修改

      };
    };

    getDetails(obj,(data,message) => {
      message.success(message);
    })
  }else{
    message.error("请先添加服务器列表");
  }
}



  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    const setDisabled = ()=>{
      if(getFieldValue("useOutput") === undefined){
        return config.useOutput;
      }else{
        if(getFieldValue("useOutput")){
          return getFieldValue("useOutput");
        }else {
          return false;
        }
      }
    };
    const setDisabled1 = ()=>{
      if(getFieldValue("jsonInsert") === undefined){
        return config.jsonInsert;
      }else{
        if(getFieldValue("jsonInsert")){
          return getFieldValue("jsonInsert");
        }else {
          return false;
        }
      }
    };
     const selectAfter =  getFieldDecorator('timeoutUnit', {
       initialValue: config.timeoutUnit
     })(
       <Select  style={{width:"100px"}}>
         <Option value="NANOSECONDS">纳秒</Option>
         <Option value="MICROSECONDS">微秒</Option>
         <Option value="MILLISECONDS">毫秒</Option>
         <Option value="SECONDS">秒</Option>
         <Option value="MINUTES">分钟</Option>
         <Option value="HOURS">时</Option>
         <Option value="DAYS">天</Option>
       </Select>
     );

    return (
      <Modal
        visible={visible}
        title="ES批量加载"
        wrapClassName="vertical-center-modal"
        width={650}
        footer={[
            <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)}>确定</Button>,
            <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
        maskClosable={false}
        onCancel ={this.setModelHide.bind(this)}
      >
        <Form >
          <FormItem label="步骤名称"  {...this.formItemLayout}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <div style={{margin:"0 5%"}}>
            <Tabs type="card">
                <TabPane tab="一般" key="1">
                  <FormItem
                    {...this.formItemLayout}
                    label="测试"
                    style={{marginBottom:"8px"}}
                  >
                    <Button size={"small"} onClick={()=>{this.testIndex("index")}}>测试索引</Button>
                  </FormItem>
                   <FormItem  label="索引" style={{marginBottom:"8px"}} {...this.formItemLayout}>
	                  {getFieldDecorator('index', {
	                    initialValue:config.index
	                  })(
	                    <Input />
	                  )}
	                </FormItem>
	                <FormItem  label="类型" {...this.formItemLayout}>
	                  {getFieldDecorator('type', {
	                    initialValue:config.type
	                  })(
	                    <Input />
	                  )}
	                </FormItem>
                  <FormItem
                    {...this.formItemLayout}
                    label="选项"
                    style={{marginBottom:"8px"}}
                  >
                  </FormItem>
                    <FormItem  label="批量大小" style={{marginBottom:"8px"}} {...this.formItemLayout}>
	                  {getFieldDecorator('batchSize', {
	                    initialValue:config.batchSize
	                  })(
	                    <Input />
	                  )}
	                </FormItem>
	                <FormItem  style={{marginBottom:"8px",marginLeft:"25%"}} {...this.formItemLayout}>
	                  {getFieldDecorator('stopOnError', {
	                    valuePropName: 'checked',
	                    initialValue:config.stopOnError
	                  })(
	                    <Checkbox>发生错误后是否停止</Checkbox>
	                  )}
	                </FormItem>
	                <FormItem  label="Bath Timeout" style={{marginBottom:"8px"}} {...this.formItemLayout}>
	                  {getFieldDecorator('timeout', {
	                    initialValue:config.timeout,
                       rules:[{pattern:/^[0-9]*$/, message: '请输入正确的时间' }
                       ]
	                  })(
	                   <Input type="number" addonAfter={selectAfter} style={{ width: '100%' }} />
	                  )}
	                </FormItem>
	                 <FormItem  label="ID字段" style={{marginBottom:"8px"}} {...this.formItemLayout}>
	                  {getFieldDecorator('idInField', {
	                    initialValue:config.idInField
	                  })(
	                     <Select>
                         {
                           this.state.InputData.map(index=>
                             <Option key={index.name} value={index.name}>{index.name}</Option>
                           )
                         }
			                </Select>
	                  )}
	                </FormItem>
	                <FormItem  style={{marginBottom:"8px",marginLeft:"25%"}} {...this.formItemLayout}>
	                  {getFieldDecorator('overWriteIfSameId', {
	                    valuePropName: 'checked',
	                    initialValue:config.overWriteIfSameId,
	                  })(
	                    <Checkbox>如果已存在是否覆盖</Checkbox>
	                  )}
	                </FormItem>
	                <FormItem  style={{marginBottom:"8px",marginLeft:"25%"}} {...this.formItemLayout}>
	                  {getFieldDecorator('useOutput', {
	                    valuePropName: 'checked',
	                    initialValue:config.useOutput,
	                  })(
	                    <Checkbox>输出行</Checkbox>
	                  )}
	                </FormItem>
	                <FormItem  label="ID输出字段" style={{marginBottom:"8px"}} {...this.formItemLayout}>
	                  {getFieldDecorator('idOutField', {
	                    initialValue:config.idOutField
	                  })(
	                    <Input disabled={!setDisabled()} />
	                  )}
	                </FormItem>

                  <FormItem  style={{marginBottom:"8px",marginLeft:"25%"}} {...this.formItemLayout}>
	                  {getFieldDecorator('jsonInsert', {
	                    valuePropName: 'checked',
	                    initialValue:config.jsonInsert
	                  })(
	                    <Checkbox>JSON输入</Checkbox>
	                  )}
	                </FormItem>

	                 <FormItem  label="JSON 字段" style={{marginBottom:"8px"}} {...this.formItemLayout}>
	                  {getFieldDecorator('jsonField', {
	                    initialValue:config.jsonField
	                  })(
                      <Select disabled={!setDisabled1()}>
                        {
                          this.state.InputData.map(index=>
                            <Option key={index.name} value={index.name}>{index.name}</Option>
                          )
                        }
			              </Select>
	                  )}
	                </FormItem>
               </TabPane>
               <TabPane tab="服务" key="2">
                 <Row style={{margin:"5px 0",width:"100%"}} >
                    <Col span={12} size={"small"} >
                      <ButtonGroup size={"small"}>
                          <Button  onClick={this.handleAdd.bind(this)}>添加字段</Button>
                          <Button    onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                      </ButtonGroup>
                    </Col>
	                <Col span={12}  style={{textAlign:"right"}} >
                    <Button  size={"small"} onClick={()=>{this.testIndex("connect")}}>测试连接</Button>
                 </Col>
                </Row>
                  <EditTable  columns={this.columns1} dataSource = {this.state.ElasticList1} tableStyle="editTableStyle5" size={"small"} ref="servers" rowSelection={true}   count={0}/>
               </TabPane>
               <TabPane tab="字段" key="3">
                  <Row style={{margin:"5px 0",width:"100%"}}  >
                    <Col span={12}  >
                      <ButtonGroup>
                        <Button size={"small"}  onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                        <Button   size={"small"} onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                      </ButtonGroup>
                    </Col>
	                <Col span={12}  >
                    <Button style={{float:"right"}} size={"small"} onClick={this.handleGetFields.bind(this)} >获取字段</Button>
	                </Col>
                </Row>
                    <EditTable  columns={this.columns2}   initFuc={this.initFuc.bind(this)} dataSource = {this.state.ElasticList2} tableStyle="editTableStyle5" size={"small"} ref="fields"  rowSelection={true}    count={0}/>
               </TabPane>
                <TabPane tab="设置" key="4">
                  <Row style={{margin:"5px 0",width:"100%"}}  >
                    <Col span={12}  >
                      <Button size={"small"}  onClick={this.handleAdd2.bind(this)}>添加字段</Button>
                    </Col>
	                <Col span={12}  >
	                  <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields2.bind(this)} >删除字段</Button>
	                </Col>
                </Row>
                     <EditTable  columns={this.columns3} dataSource = {this.state.ElasticList3} tableStyle="editTableStyle5" size={"small"} ref="settings"  rowSelection={true}    count={0}/>
               </TabPane>
            </Tabs>
          </div>

        </Form>
      </Modal>
    );


  }
}
const ElasticSearchBulkInsert = Form.create()(ElasticSearchBulk);

export default connect()(ElasticSearchBulkInsert);
