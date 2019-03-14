/**HttpClient查询控件*/
//一、
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, message } from 'antd';
import Modal from 'components/Modal';
import EditTable from '../../../common/EditTable';
const ButtonGroup = Button.Group;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
//二、
class HttpClientInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible,config } = props.model;
    if(visible === true) {
      const {argument,headers} = props.model.config;
      let data1 = [];
      let data2 = [];
      let count1 = 0;
      let count2 = 0;
      if (argument) {
        for (let index of argument) {
          data1.push({
            key: count1,
            ...index
          });
          count1++;
        }
      }
      if (headers) {
        for (let index of headers) {
          data2.push({
            key: count2,
            ...index
          });
          count2++;
        }
      }
      // console.log(data1,111);
      this.state = {
        dataSource1:data1,
        dataSource2:data2,
        InputData:[] //下拉选项：控件传过来的数组
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
    const { panel,transname,description,key,saveStep,text,formatTable,config } = this.props.model;
    const {argument,headers} = config;
    form.validateFields((err, values) => {
      if(err){
        return
      }
      let sendFields = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let arg = ["field","name" ];
          sendFields = formatTable(this.refs.editTable1.state.dataSource,arg)
        }
      }else{
        if(argument){
          sendFields = argument
        }
      }
      let sendFields1 = [];
      if(this.refs.editTable2){
        if(this.refs.editTable2.state.dataSource.length>0){
          let arg = ["field","name"];
          sendFields1 = formatTable(this.refs.editTable2.state.dataSource,arg)
        }
      }else{
        if(headers){
          sendFields1 = headers
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;//控件基本参数+5
      obj.config = {//表单参数设置
        argument:sendFields,
        headers:sendFields1,
        ...values
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
  }
  /**4.其他：*/
  //4.1.对话框布局
  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  //4.2.自定义标题
  columnsHttp = [
    {
      title: '名称',
      dataIndex: 'field',
      key: 'field',
      width:"50%",
      selectable:true,//selectable:true
    }, {
      title: '命名参数',
      dataIndex: 'name',
      key: 'name',
      // width:"50%",
      editable:true
    }];
  columnsHeader = [
    {
      title: 'Field',
      dataIndex: 'field',
      key: 'field',
      width:"50%",
      selectable:true,
      // editable:true
    }, {
      title: 'Header',
      dataIndex: 'name',
      key: 'name',
      // width:"50%",
      // selectable:false,
      editable:true,
    }];
  //4.3.表格方法：
  handleAdd1 = ()=>{
    const data = {
      "field": "",
      "name": ""
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleAdd2 = ()=>{
    const data = {
      "field": "",
      "name": ""
    };
    this.refs.editTable2.handleAdd(data);
  };
  handleAuto1 = ()=>{
    if(this.state.InputData.length > 0){
      let args = [];
      let count = 0;
      for(let index of this.state.InputData){
        args.push({
          key:count,
          field:index.name,
          name:index.name
        });
        count++;
      }
      this.refs.editTable1.updateTable(args,count);
    }else {
      message.info('未找到对应的控件字段')
    }
  };
  handleAuto2 = ()=>{
    if(this.state.InputData.length > 0){
      let args = [];
      let count = 0;
      for(let index of this.state.InputData){
        args.push({
          key:count,
          field:index.name,
          name:index.name
        });
        count++;
      }
      this.refs.editTable2.updateTable(args,count);
    }else {
      message.info('未找到对应的控件字段')
    }
  };
  handleDelete1 = ()=>{
    this.refs.editTable1.handleDelete();
  };
  handleDelete2 = ()=>{
    this.refs.editTable2.handleDelete();
  };
  //4.4.表格下拉框：
  initFuc(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");//对应数组InputData的属性名设置下拉内容
    // console.log(that,'获取model方法：updateOptions');
    that.updateOptions({
      field:options,
    });
  };
  initFuc1(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      field:options,
    });
  };

  render() {
    const { getFieldDecorator, getFieldValue} = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    //手动输入开关：disabled=true
    const setDisabled1 = ()=>{
      if(getFieldValue("urlInField") === undefined){
        return config.urlInField;
      }else{
        if(getFieldValue("urlInField")){
          return getFieldValue("urlInField");//true
        }else {
          return false;
        }
      }
    };

    return (
      <Modal
        maskClosable={false}
        visible={visible}
        title="Http客户端查询"
        onCancel={this.setModelHide.bind(this)}
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
          <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
      >
        <Form >
          <FormItem label="步骤名称"   {...this.formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
        </Form>

        <Tabs type="card" >

          <TabPane tab="查询内容" key="1" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset" style={{marginTop:15}}>
              <legend style={{marginTop:8}}>&nbsp;&nbsp;设置</legend>
              <Form >
                <FormItem label="URL"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('url', {
                    initialValue:config.url?decodeURIComponent(config.url):''
                  })(
                    <Input disabled={setDisabled1()}/>
                  )}
                </FormItem>
                <FormItem label="获取URL"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('urlInField', {//参数未对应
                    valuePropName: 'checked',//checked={setDisabled1()}
                    initialValue:config.urlInField?config.urlInField:false
                  })(
                    <Checkbox />
                  )}
                </FormItem>
                <FormItem label="URL可选项：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('urlField', {
                    initialValue:config.urlField
                  })(
                    <Select
                      // mode="combobox"
                      allowClear
                      filterOption={false}
                      disabled={!setDisabled1()}
                    >
                      {
                        this.state.InputData.map((index)=>(<Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="编码方式：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('encoding', {
                    initialValue:config.encoding?config.encoding:''
                  })(
                    <Select
                      allowClear
                    >
                      <Select.Option value="GBK">GBK</Select.Option>
                      <Select.Option value="ISO-8859-1">ISO-8859-1</Select.Option>
                      <Select.Option value="GB2312">GB2312</Select.Option>
                      <Select.Option value="UTF-8">UTF-8</Select.Option>
                      <Select.Option value="Big5">Big5</Select.Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem label="连接超时"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('connectionTimeout', {
                    initialValue:config.connectionTimeout,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="套接字超时"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('socketTimeout', {
                    initialValue:config.socketTimeout,
                    rules: [{ whitespace:true, required: false, message: '套接字超时' },]
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="连接关闭等待时间"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('closeIdleConnectionsTime', {
                    initialValue:config.closeIdleConnectionsTime,
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;输出过滤器</legend>
              <Form >
                <FormItem label="结果字段名"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('fieldName', {
                    initialValue:config.fieldName,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="HTTP状态节点名称"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('resultCodeFieldName', {
                    initialValue:config.resultCodeFieldName,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="请求时间"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('responseTimeFieldName', {
                    initialValue:config.responseTimeFieldName,
                    rules: [{ whitespace:true, required: false, message: 'HTTP状态节点名称' },]
                  })(
                    <Input/>
                  )}
                </FormItem>
                <FormItem label="请求头部信息"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('responseHeaderFieldName', {
                    initialValue:config.responseHeaderFieldName,
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;HTTP身份验证</legend>
              <Form >
                <FormItem label="用户登录"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('httpLogin', {
                    initialValue:config.httpLogin,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="登录密码"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('httpPassword', {
                    initialValue:config.httpPassword,
                  })(
                    <Input type="password" autoComplete='off'/>
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;使用代理</legend>
              <Form >
                <FormItem label="代理主机"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('proxyHost', {
                    initialValue:config.proxyHost,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="代理端口"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('proxyPort', {
                    initialValue:config.proxyPort,
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
          </TabPane>

          <TabPane tab="字段" key="2" >
            <div style={{margin:"10px 0"}}>
              <Row style={{margin:"10px 0"}}>
                <Col span={24}><p style={{margin:"0 0 10px 5px"}}>命名参数:</p></Col>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button key="1" onClick={this.handleAuto1.bind(this)}>获取字段</Button>
                    <Button key="2" onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12} style={{textAlign:"right"}}>
                  <Button  size={"small"}  onClick={this.handleDelete1.bind(this)}>删除字段</Button>
                </Col>
              </Row>
              <EditTable
                columns={this.columnsHttp}
                dataSource={this.state.dataSource1}
                scroll={{y: 300}}
                initFuc={this.initFuc.bind(this)}
                rowSelection={true}
                size={"small"}
                count={0}
                ref="editTable1"
                tableStyle="editTableStyle5"
              />
            </div>

            <div style={{margin:"10px 0"}}>
              <Row style={{margin:"10px 0"}}>
                <Col span={24}><p style={{margin:"0 0 10px 5px"}}>自定义HTTP头部信息:</p></Col>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button key="1" onClick={this.handleAuto2.bind(this)}>获取字段</Button>
                    <Button key="2" onClick={this.handleAdd2.bind(this)}>添加字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12} style={{textAlign:"right"}}>
                  <Button  size={"small"}  onClick={this.handleDelete2.bind(this)}>删除字段</Button>
                </Col>
              </Row>
              <EditTable
                columns={this.columnsHeader}
                dataSource={this.state.dataSource2}
                initFuc={this.initFuc1.bind(this)}
                scroll={{y: 300}}
                rowSelection={true}
                size={"small"}
                count={0}
                ref="editTable2"
                tableStyle="editTableStyle5"
              />
            </div>
          </TabPane>

        </Tabs>

      </Modal>
    );
  }
}
//三、
const HttpInput = Form.create()(HttpClientInput);
export default connect()(HttpInput);
