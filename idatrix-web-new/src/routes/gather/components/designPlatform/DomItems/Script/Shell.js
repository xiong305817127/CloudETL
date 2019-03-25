/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react';
import { Button, Form, Input, Radio,Select,Checkbox,Row,Col,Tabs   } from 'antd';
import Modal from "components/Modal.js";
import { connect } from 'dva'
import EditTable from '../../../common/EditTable';

import AceEditor from 'react-ace';
import brace from 'brace';

import 'brace/mode/mysql';
import 'brace/theme/github';
import 'brace/ext/language_tools';

const { TextArea } = Input;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

class Shell extends React.Component{

  constructor(props){
   super(props);
    const { config } = props.model;
    let tableFields = [];
    let count = 0;

    if(config.arguments){
      for(let index of  config.arguments){
        tableFields.push({
          key:count,
          name:index.name
        });
        count++;
      }
    }
    this.state = {
      db_list: [],
      InputData:[],
      input_fields:tableFields,
      textAreaValue:decodeURIComponent(config.script) != "null"?decodeURIComponent(config.script):""
    }
  }

  setModelHide(){
    const{ dispatch,form } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  }

  handleFormSubmitShell (){
    const{ form } = this.props;
    const { panel,transname,description,key,saveEntry,text,config,formatTable } = this.props.model;
  
    form.validateFields((err, values) => {
    	console.log(values,"values");
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let arg = [  "arguments"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
        }
      }else{
        if(config.arguments){
          sendFields = config.arguments
        }
      }

     let obj = {};
      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.entryParams = {
        ...values,
        "script": encodeURIComponent(this.state.textAreaValue),
        "arguments": sendFields
      };
       console.log(obj,"obj");

	    saveEntry(obj,key,data=>{
	        if(data.code === "200"){
	          this.setModelHide();
	        }
	      });
    })
  }

  handleAdd = ()=>{
    const data = {
      name:""
    };
    this.refs.editTable.handleAdd(data);
  };
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  getInputFields(){
    const { InputData } = this.state;
    let args = [];
    let count = 0;
    for(let index of  InputData){
      args.push({
         key:count,
          name:index.name
      });
      count++;
    }
    this.refs.editTable.updateTable(args,count);
  };

  columns = [{
    title: '参数',
    dataIndex: 'arguments',
    key: 'arguments',
    selectable:true
  }];

  onTextAreaChange(newValue){
    this.setState({
      textAreaValue:newValue
    });
  }

   callback(key) {
	  console.log(key);
	}

   handleChange(value) {
	  console.log(value); 
	}
   click(e){
     console.log(e.target.checked,"false");
   }

  onTextAreaChange(newValue){
    this.setState({
      textAreaValue:newValue
    });
  }

  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 12 },
  };

  formItemLayout2 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 8 },
  };

  render(){
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    const setDisabled = ()=>{
      if(getFieldValue("argFromPrevious") === undefined){
        return config.argFromPrevious;
      }else{
        if(getFieldValue("argFromPrevious")){
          return getFieldValue("argFromPrevious");
        }else {
          return false;
        }
      }
    };
    const setDisabled1 = ()=>{
      if(getFieldValue("setLogfile") === undefined){
        return config.setLogfile;
      }else{
        if(getFieldValue("setLogfile")){
          return getFieldValue("setLogfile");
        }else {
          return false;
        }
      }
    };
    return(
      <Modal
        visible={visible}
        title="Shell"
        style={{zIndex:50}}
        width={800}
        maskClosable={false}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleFormSubmitShell.bind(this)}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
                ]}
        onCancel = {this.setModelHide.bind(this)}
      >
        <Form >
          <FormItem label="步骤名称"  {...this.formItemLayout} >
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称',
                validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
            
        <Tabs onChange={this.callback} type="card">
		    <TabPane tab="一般" key="1">
	                <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('argFromPrevious', {
		                   valuePropName: 'checked',
		                   initialValue:config.argFromPrevious
		                 })(
		                   <Checkbox onClick={this.click}>插入脚本</Checkbox>
		                 )}
	               </FormItem>
	               <FormItem label="脚本文件名"  {...this.formItemLayout}  style={{marginBottom:"8px"}}>
	                   <Row>
			             <Col span={20}>
				            {getFieldDecorator('filename', {
				              initialValue:config.filename
				            })(
				              <Input disabled={setDisabled()}/>
				            )}
			             </Col>
			             <Col span={4}>
			                 <Button>浏览</Button>
			             </Col>
			          </Row>
			       </FormItem>
			       <FormItem label="工作路径"  {...this.formItemLayout}  style={{marginBottom:"8px"}}>
                         {getFieldDecorator('workDirectory', {
				              initialValue:config.workDirectory
				            })(
				              <Input/>
				            )}
			       </FormItem>

			       <FormItem {...this.formItemLayout2} label="日志设置"></FormItem>
                   
			        <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('setLogfile', {
		                   valuePropName: 'checked',
		                   initialValue:config.setLogfile
		                 })(
		                   <Checkbox >指定日志文件</Checkbox>
		                 )}
	               </FormItem>
	                <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('setAppendLogfile', {
		                   valuePropName: 'checked',
		                   initialValue:config.setAppendLogfile
		                 })(
		                   <Checkbox disabled={!setDisabled1()}>追加日志文件</Checkbox>
		                 )}
	               </FormItem>
	               <FormItem label="日志文件名称"  {...this.formItemLayout}  style={{marginBottom:"8px"}}>
			            {getFieldDecorator('readField', {
			              initialValue:config.readField
			            })(
			              <Input disabled={!setDisabled1()}/>
			            )}
			       </FormItem>
			       <FormItem label="日志文件扩展名"  {...this.formItemLayout}  style={{marginBottom:"8px"}}>
			            {getFieldDecorator('logext', {
			              initialValue:config.logext
			            })(
			              <Input disabled={!setDisabled1()}/>
			            )}
			       </FormItem>
			         <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('addDate', {
		                   valuePropName: 'checked',
		                   initialValue:config.addDate
		                 })(
		                   <Checkbox disabled={!setDisabled1()}>日志文件中包含日期</Checkbox>
		                 )}
	               </FormItem>
	               <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('addTime', {
		                   valuePropName: 'checked',
		                   initialValue:config.addTime
		                 })(
		                   <Checkbox disabled={!setDisabled1()}>日志文件中包含时间</Checkbox>
		                 )}
	               </FormItem>
	                <FormItem {...this.formItemLayout} label="日志级别" hasFeedback>
			          {getFieldDecorator('logFileLevel', {
			          })(
			            <Select disabled={!setDisabled1()}>
			              <Option value="0">没有日志</Option>
			              <Option value="1">错误日志</Option>
			              <Option value="2">最小日志</Option>
			              <Option value="3">基本日志</Option>
			              <Option value="4">详细日志</Option>
			              <Option value="5">调试</Option>
			              <Option value="6">行级日志(非常详细)</Option>
			            </Select>
			          )}
			        </FormItem>
			         <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('execPerRow', {
		                   valuePropName: 'checked',
		                   initialValue:config.execPerRow
		                 })(
		                   <Checkbox disabled={setDisabled()}>将上一次结果为参数？</Checkbox>
		                 )}
	               </FormItem>
	                <FormItem  style={{marginBottom:"10px",marginLeft:'25%'}}>
		                 {getFieldDecorator('insertScript', {
		                   valuePropName: 'checked',
		                   initialValue:config.insertScript
		                 })(
		                   <Checkbox disabled={setDisabled()}>对每个输入行执行一次？</Checkbox>
		                 )}
	               </FormItem>
                <FormItem {...this.formItemLayout2} label="字段"></FormItem>
			      <div style={{margin:"0px 30px 15px 30px"}}>
		            <Row style={{margin:"5px 0",width:"100%"}}  >
		              <Col span={12}>
		                <ButtonGroup  size={"small"} >
		                  <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
		                  <Button onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
		                </ButtonGroup>
		              </Col>
		              <Col span={12} style={{textAlign:"right"}}>
		                <Button  size={"small"}  onClick={this.getInputFields.bind(this)}>获取字段</Button>
		              </Col>
		            </Row>
		            <EditTable columns={this.columns} dataSource = {this.state.input_fields} rowSelection={true} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="editTable"   count={4}/>
		          </div>
	              
		    </TabPane>
		    <TabPane tab="脚本" key="2">
                 <FormItem label="JavaScript"  {...this.formItemLayout1} style={{marginBottom:"8px",display:'none'}}>
		            {getFieldDecorator('script', {
		              initialValue: decodeURIComponent(config.script) != "null"?decodeURIComponent(config.script):"",
		            })(
		               <TextArea   />
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
		    </TabPane>
		</Tabs>


        </Form>
      </Modal>
    )
  }
}


const ShellModel = Form.create()(Shell);

export default connect()(ShellModel);
