/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react';
import { Button, Form, Input,Checkbox,Row,Col,Select } from 'antd';
import Modal from "components/Modal.js";
import { connect } from 'dva'
import EditTable from '../../../common/EditTable';
import AceEditor from 'react-ace';
import brace from 'brace';

import 'brace/mode/javascript';
import 'brace/theme/github';
import 'brace/ext/language_tools';

const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const { TextArea } = Input;

class Script extends  React.Component{

  constructor(props){
    super(props);
    const { visible,config } = props.model;
    if(visible === true){
      const { fields } = props.model.config;
      let data = [];
      let count = 0;
      if(fields){
        for(let index of fields){
          data.push({
            key:count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        textAreaValue:decodeURIComponent(config.jsScripts[0].value) != "null"?decodeURIComponent(config.jsScripts[0].value):""
      }
    }
  }

  setModelHide (){
    const {  dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  getSQLFields(){
    let names = [];
    let str = this.state.textAreaValue;

    let strs = str.split("\n");
    if(strs.length === 0){
      return
    }
    for(let index of  strs){
      let strs1 = index.split(";");
      if(strs1.length === 0){
          continue;
      }
       for(let index1 of strs1){
         if(index1){
           var regRes = index1.trim().match(/(\W+var|^var)\s+(_*[a-zA-Z]{1}[\w,\s]*)([\s;=]+|$)/);
           if(regRes){
             if (regRes.length >= 2) {
               var arr = regRes[2].split(',');
               for (var i = 0; i < arr.length; i++) {
                 names.push(arr[i].trim());
               }
             }
           }else{
              continue;
           }
         }
       }
    }
    console.log(names);

    let data = [];
    let count = 0;
    for(let name of names){
      data.push({
        name:name,
        rename:"",
        type:"",
        length:"",
        precision:"",
        replace:"true",
        key:name
      });
      count++;
    };

    this.refs.editTable.updateTable(data,count);
    this.setState({
      dataSource:data,
      updateStatus:"isUpdate"
    })
  }


  handleAdd = ()=>{
    const data = {
      name:"",
      rename:"",
      type:"",
      length:"",
      precision:"",
      replace:""
    };
    this.refs.editTable.handleAdd(data);
  };
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  handleFormSubmit(){
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
    const {  fields } = config;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = [  "name", "rename", "type", "length", "precision", "replace"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
      }else{
        if(fields){
          sendFields = fields;
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
          "compatible": values.compatible,
          "optimizationLevel": values.optimizationLevel,
            "jsScripts": [
            {
              "name": "sqlvalue",
              "type": 0,
              "value": encodeURIComponent( this.state.textAreaValue)
            }
          ],
          "fields": sendFields
      }
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  }

  columns = [{
    title: '字段名称',
    dataIndex: 'name',
    width:"20%",
    key: 'name',
    editable:true,
  }, {
    title: '改名为',
    dataIndex: 'rename',
    key: 'rename',
    editable:true,
  }, {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width:"20%",
    selectable:true,
    selectArgs:[
      <Select.Option key="None" value="0">None</Select.Option>,
      <Select.Option key="Number" value="1">Number</Select.Option>,
      <Select.Option key="String" value="2">String</Select.Option>,
      <Select.Option key="Date" value="3">Date</Select.Option>,
      <Select.Option key="Boolean" value="4">Boolean</Select.Option>,
      <Select.Option key="Integer" value="5">Integer</Select.Option>,
      <Select.Option key="BigNumber" value="6">BigNumber</Select.Option>,
      <Select.Option key="Serializable" value="7">Serializable</Select.Option>,
      <Select.Option key="Binary" value="8">Binary</Select.Option>,
      <Select.Option key="Timestamp" value="9">Timestamp</Select.Option>,
      <Select.Option key="Internet Address" value="10">Internet Address</Select.Option>
    ]
  }, {
    title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"10%",
    editable:true,
  }, {
    title: '精度',
    dataIndex: 'precision',
    width:"13%",
    key: 'precision',
    editable:true,
  }, {
    title: "是否更名",
    dataIndex: 'replace',
    key: 'replace',
    width:"11%",
    selectable:true,
    selectArgs:[<Select.Option  key="true" value="true">是</Select.Option>,
      <Select.Option key="false" value="false">否</Select.Option>
    ]
  }
  ];

  onTextAreaChange(newValue){
    this.setState({
      textAreaValue:newValue
    });
  }




  render(){
     const { visible,config,text,handleCheckName } = this.props.model;
     const {getFieldDecorator} = this.props.form;

       const formItemLayout = {
         labelCol: { span: 6 },
         wrapperCol: { span: 14 },
       };
       const formItemLayout1 = {
         labelCol: { span:5 },
         wrapperCol: { span: 18},
       };
       const formItemLayout2 = {

         wrapperCol: { span:24 },
       };

     return(
       <Modal
         visible={visible}
         title="JavaScript代码"
         wrapClassName="vertical-center-modal"
         width={750}
         maskClosable={false}
         footer={[
                  <Button key="submit" type="primary" size="large"  onClick={()=>{this.handleFormSubmit()}}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={()=>{this.setModelHide();}}>取消</Button>,
                ]}
         onCancel = {()=>{this.setModelHide()}}
       >
         <Form >
           <FormItem label="步骤名称"  {...formItemLayout}>
             {getFieldDecorator('text', {
               initialValue:text,
               rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                 {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
             })(
               <Input />
             )}
           </FormItem>
           <Row style={{marginLeft:"11%"}}>
             <Col span={18}>
               <FormItem label="优化级别"  {...formItemLayout1}>
                 {getFieldDecorator('optimizationLevel', {
                   initialValue:config.optimizationLevel
                 })(
                   <Input />
                 )}
               </FormItem>
             </Col>
             <Col span={1}>
               <FormItem>
                 {getFieldDecorator("compatible", {
                   valuePropName: 'checked',
                   initialValue: config.compatible,
                 })(
                   <Checkbox />
                 )}
               </FormItem>
             </Col>
           </Row>
           <FormItem {...formItemLayout2} style={{margin:"0 5% 15px",display:"none"}} >
             {getFieldDecorator('SQLValue',{
               initialValue: decodeURIComponent(config.jsScripts[0].value) != "null"?decodeURIComponent(config.jsScripts[0].value):""
             })(
               <TextArea placeholder="//Script here" style={{fontSize:"16px",height:100}} />
             )}
           </FormItem>
           <div style={{padding:"0px 35px 10px 35px"}}>
             <AceEditor
               mode="javascript"
               theme="github"
               onChange={this.onTextAreaChange.bind(this)}
               name="gather_tableInput"
               className="autoTextArea"
               showGutter={true}
               width={"100%"}
               height={"300px"}
               fontSize={16}
               editorProps={{$blockScrolling: true}}
               value={this.state.textAreaValue}
               wrapEnabled={true}
               setOptions={{
                  enableBasicAutocompletion: true,
                  enableLiveAutocompletion: true,
                  enableSnippets: false,
                  showLineNumbers: true,
                  tabSize: 2
             }}
             />
           </div>
           <div style={{margin:"0 5% 15px 35px"}}>
             <Row style={{margin:"5px 0",width:"100%"}}  >
               <Col span={12}>
                 <ButtonGroup size={"small"} >
                   <Button     onClick={this.handleAdd}>添加字段</Button>
                   <Button     onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                 </ButtonGroup>
               </Col>
               <Col span={12} style={{textAlign:"right"}}>
                 <Button  size={"small"}  onClick={this.getSQLFields.bind(this)}>获取变量</Button>
               </Col>
             </Row>
             <EditTable  columns={this.columns} dataSource = {this.state.dataSource} rowSelection={true} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="editTable"   count={4}/>
           </div>
         </Form>
       </Modal>
     )
   }
}

const ScriptValueMod = Form.create()(Script);

export default connect()(ScriptValueMod);
