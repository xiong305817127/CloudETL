/**
 * Created by Administrator on 2017/12/19 0019.
 */
//一、环境
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox} from 'antd';
import Modal from 'components/Modal';
import AceEditor from 'react-ace';
import withDatabase from "../../../common/withDatabase";
import brace from 'brace';

import 'brace/mode/mysql';
import 'brace/theme/github';
import 'brace/ext/language_tools';

const { Option } = Select;
const FormItem = Form.Item;

//二、渲染
class DynamicSQLRowInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible,config } = props.model;
    if(visible === true) {
      this.state = {
        InputData:[], //下拉选项：控件传过来的数组
        dbList:[], //SQL连接名称
        textAreaValue:decodeURIComponent(config.sql)!= "null"?decodeURIComponent(config.sql):""
      };
    }
  };
  //2.前后控件参数：
  componentDidMount(){
    const { getInputFields,transname,text } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      // console.log(data,123);
      if(data){
        this.setState({InputData:data });
      }
    });
  };
  //3.1.提交表单：
  handleFormSubmit = (e) =>{
    e.preventDefault();
    const form = this.props.form;
		const { panel,transname,description,key,saveStep,text } = this.props.model;
		const {
			schemaId,
			schema,
			databaseId,
			database
		} = this.props.databaseData;
		
    form.validateFields((err, values) => {
      if(err){
        return
      }
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;//控件基本参数+5
      obj.config = {//表单参数设置
        "sql": encodeURIComponent(this.state.textAreaValue),
				...values,
				schemaId,
				schema,
				databaseId,
				connection:database
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  };
  //3.2.关闭对话框：打开对话框--在初始化Model触发状态
  setModelHide (){
    const {  dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };
  /**4.其他：*/
    //4.1.对话框布局
  formItemLayout1 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 11 },
  };
  formItemLayout2 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 },
  };
  //4.4.步骤条：
  next=()=> {
    const current = this.state.current + 1;
    this.setState({ current:current<3?current:2 });
  };
  prev=()=> {
    const current = this.state.current - 1;
    this.setState({ current });
  };

  onTextAreaChange(newValue){
    this.setState({
      textAreaValue:newValue
    });
	}

	getSchemaList(id){
		if (id === undefined) return;
    const { setFieldsValue } = this.props.form;
    const { getSchemaList } = this.props;

    setFieldsValue({
      schema: ""
    });

    //调用高阶组件的通用方法
    getSchemaList(id);
	}



  render() {
    const { getFieldDecorator } = this.props.form;
		const { visible,config,text,handleCheckName } = this.props.model;
		const {
      databaseList,
      schemaList,
      database,
      schema
		} = this.props.databaseData;
		const {  getTableList } = this.props;

    return (
      <Modal
        maskClosable={false}
        visible={visible}
        title="执行动态 SQL"
        onCancel={this.setModelHide.bind(this)}
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
          <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
      >
        <FormItem label="步骤名称"   {...this.formItemLayout2}>
          {getFieldDecorator('text', {
            initialValue:text,
            rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
              {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
          })(
            <Input spellCheck={false}/>
          )}
        </FormItem>

        <Form style={{border:"1px solid #D9D9D9",padding:15}}>
          <FormItem label="数据库连接" {...this.formItemLayout2} style={{marginBottom:8}} hasFeedback>
            {getFieldDecorator('connection', {
              initialValue:database,
              rules: [{ required: true, message: '请选择数据库链接' }]
            })(
              <Select
                placeholder="数据库连接"
								onChange={this.getSchemaList.bind(this)}
              >
                 {databaseList.map(index => (
                  <Option key={index.id} value={index.id}>
                    {index.name}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
					<FormItem
            label="模式名称"
            style={{ marginBottom: "8px" }}
            {...this.formItemLayout2}
          >
            {getFieldDecorator("schema", {
              initialValue: schema,
              rules: [{ required: true, message: "请选择模式名称" }]
            })(
              <Select onChange={getTableList}>
                {schemaList.map(index => (
                  <Select.Option key={index.schemaId} value={index.schemaId}>
                    {index.schema}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem label="SQL字段名称"   {...this.formItemLayout2} style={{marginBottom:8}}>
            {getFieldDecorator('sqlfieldname', {
              initialValue:config.sqlfieldname,
            })(
              <Select
                mode="combobox"
                allowClear
                placeholder="SQL字段名称"
              >
                {
                  this.state.InputData.length>0?this.state.InputData.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):null
                }
              </Select>
            )}
          </FormItem>
          <FormItem label="返回的行数"   {...this.formItemLayout2} style={{marginBottom:8}}>
            {getFieldDecorator('rowLimit', {
              initialValue:config.rowLimit,
            })(
              <Input spellCheck={false}/>
            )}
          </FormItem>
          <FormItem label="外连接"   {...this.formItemLayout2} style={{marginBottom:8}}>
            {getFieldDecorator('outerJoin', {
              valuePropName: 'checked',
              initialValue:config.outerJoin?config.outerJoin:false,
            })(
              <Checkbox/>
            )}
          </FormItem>
          <FormItem label="替换变量"   {...this.formItemLayout2} style={{marginBottom:8}}>
            {getFieldDecorator('replacevars', {
              valuePropName: 'checked',
              initialValue:config.replacevars?config.replacevars:false,
            })(
              <Checkbox/>
            )}
          </FormItem>
          <FormItem label="查询参数改变"   {...this.formItemLayout2} style={{marginBottom:8}}>
            {getFieldDecorator('queryonlyonchange', {
              valuePropName: 'checked',
              initialValue:config.queryonlyonchange?config.queryonlyonchange:false,
            })(
              <Checkbox/>
            )}
          </FormItem>
          <p style={{marginLeft:"8%"}}>要执行的SQL脚本（用 ; 号分隔语句，问号将被参数替换）</p>
          <div style={{padding:"8px 30px"}}>
            <AceEditor
              mode="mysql"
              theme="github"
              onChange={this.onTextAreaChange.bind(this)}
              name="gather_tableInput"
              className="autoTextArea"
              showGutter={true}
              width={"100%"}
              height={"240px"}
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
        </Form>

      </Modal>
    );
  }
}
//三、传参、调用：
const DynamicSQLRow = Form.create()(DynamicSQLRowInput);
export default connect()(withDatabase(DynamicSQLRow));
