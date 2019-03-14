/**HttpClient查询控件*/
//一、
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, message } from 'antd';
import Modal from 'components/Modal';
import EditTable from '../../../common/EditTable';
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
import { getWebUrl } from '../../../../../../services/gather';
const ButtonGroup = Button.Group;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
//二、
class HttpWebInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible,config } = props.model;
    if(visible === true) {
      const {fieldsIn,fieldsOut} = config;
      let data1 = [];
      let data2 = [];
      let count1 = 0;
      let count2 = 0;
      if (fieldsIn) {
        for (let index of fieldsIn) {
          data1.push({
            key: count1,
            ...index
          });
          count1++;
        }
      }
      if (fieldsOut) {
        for (let index of fieldsOut) {
          data2.push({
            key: count2,
            ...index
          });
          count2++;
        }
      }
      // console.log(data1,111);
      this.state = {
        //输出的数据
        inputFieldArgs:data1,
        //输入的数据
        outFieldArgs:data2,
        InputData:[], //下拉选项：控件传过来的数组
        WsdlData:[], //下拉选项：URL中获取数组
        //输出的名字
        outFieldContainerName:config.outFieldContainerName?config.outFieldContainerName:"输出",
				activeKey:"1",//标签页选中
				
				path:""
      };
    }
  };
  //2.前后控件参数：
  componentDidMount(){
    const { getInputFields,transname,text,config,getDataStore } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
        this.setState({InputData:data });
		});

		let obj2 = {};
		obj2.type = "data";
		obj2.path = "";
		getDataStore(obj2,data=>{
			const { path } = data;
			this.setState({
				path:path
			})
		})
	

    const {url,httpLogin,httpPassword} = config;

    let obj1 = {};
    obj1.transName = transname;
    obj1.user = httpLogin;
    obj1.password = httpPassword;
    obj1.url = url;

    getWebUrl(obj1).then( (res)=> {
      // console.log(res,111);
      const {data,code} = res.data;

      if(data && code === "200" ){
        this.setState({
          WsdlData:data
        });
      }
    });
  };
  //3.1.提交表单：
  handleFormSubmit = (e) =>{
    e.preventDefault();
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,formatTable,config } = this.props.model;
    const {fieldsIn,fieldsOut} = config;
    form.validateFields((err, values) => {
      if(err){
        return
      }
      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let arg = ["type","name","wsName"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,arg)
        }
      }else{
        if(fieldsIn){
          sendFields1 = fieldsIn
        }
      }
      let sendFields2 = [];
      if(this.refs.editTable2){
        if(this.refs.editTable2.state.dataSource.length>0){
          let arg = ["type","name","wsName"];
          sendFields2 = formatTable(this.refs.editTable2.state.dataSource,arg)
        }
      }else{
        if(fieldsOut){
          sendFields2 = fieldsOut
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;//控件基本参数+5
      obj.config = {//表单参数设置
        fieldsIn:sendFields1,
        fieldsOut:sendFields2,
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
  formItemLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 },
  };
  formItemLayout1 = {
    labelCol: { span: 4 },
    wrapperCol: { span: 13 },
  };
  formItemLayout2 = {
    labelCol: { span: 4 },
    wrapperCol: { span: 19 },
  };
  //4.2.自定义标题
  columnsIn = [
    {
      title: '输入名称',
      dataIndex: 'name',
      key: 'name',
      width:"30%",
      selectable:true,
    }, {
      title: 'WS名称',
      dataIndex: 'wsName',
      key: 'wsName',
      width:"30%",
      editable:true,
    }, {
      title: 'WS类型',
      dataIndex: 'type',
      key: 'type',
      // width:"50%",
      // selectable:false,
      editable:true,
    }];
  columnsOut = [
    {
      title: '输出名称',
      dataIndex: 'name',
      key: 'name',
      width:"30%",
      editable:true,
    }, {
      title: 'WS名称',
      dataIndex: 'wsName',
      key: 'wsName',
      width:"30%",
      editable:true,
    }, {
      title: 'WS类型',
      dataIndex: 'type',
      key: 'type',
      // width:"50%",
      // selectable:false,
      editable:true,
    }];
  //4.3.表格方法：
  handleAdd1 = ()=>{
    const data = {
      "name": "",
      "wsName": "",
      "type": ""
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleAdd2 = ()=>{
    const data = {
      "name": "",
      "wsName": "",
      "type": ""
    };
    this.refs.editTable2.handleAdd(data);
  };
  handleAuto1 = ()=>{
    if(this.state.InputData.length > 0){
      let args = [];
      let count = 0;
      for(let index of this.state.InputData){
        args.push({
          key:count++,
          ...index
        });
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
          key:count++,
          ...index
        });
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
  initFuc1(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");//对应数组InputData的属性名对应标题key:name
    // console.log(that,'获取model方法：updateOptions');
    that.updateOptions({
      name:options
    });
  };
  initFuc2(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      name:options//不需要设置
    });
  };
  //4.5.切换栏:
  changeTag =(a) => {
    // console.log("切换到",a);
    this.setState({
      activeKey: a,
    })
  };
  //4.5.下拉选中：
  handleChangeSelect =(e) => {
    // console.log(2222,e);
    const arry = this.state.WsdlData||[];
    const found = arry.find(it => it.method === e);

    console.log(found);
    const { outContainerName} = found;

    this.setState({
      outFieldContainerName:outContainerName,
      outFieldArgs:found.out,
      //输入的数据
      inputFieldArgs:found.in
    });

    if(this.refs.editTable1){
      let args = [];
      let count = 0;
      for(let index of found.in){
        args.push({
           key:count++,
            ...index
        });
      }
      this.refs.editTable1.updateTable(args,count);
    }

    if(this.refs.editTable2){
      let args = [];
      let count = 0;
      for(let index of found.out){
        args.push({
          key:count++,
          ...index
        });
      }
      this.refs.editTable2.updateTable(args,count);
    }
  };
  //4.6.浏览
  getFieldList(name){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;
    // console.log(name,111);//列表名称：'list'
    // console.log(panel);//Rest
    let obj = treeViewConfig.get(panel)[name];//constant.js对应方法:
    let path = formatFolder(getFieldValue("url"));//获取后台目录
    let updateModel = this.setFolder.bind(this);
		let viewPath = "";
		
		console.log(path,"路径");

		if(path.substr(0,1) !== "/"){
			path = `${this.state.path}${path}`
		}
    viewPath = path;

    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
          ...obj.obj,
          path
        },
        viewPath:viewPath,
        updateModel:updateModel
      }
    })

  };
  //设置文件名
  setFolder(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
        "url":str
      })
    }
  };
  //4.7.上传
  handleFileUpload(name){
    const {dispatch} = this.props;
    const {panel} = this.props.model;
    let obj = treeUploadConfig.get(panel)[name];

    dispatch({
      type:"uploadfile/showModal",
      payload:{
        ...obj,
        visible:true
      }
    });
  };
  //4.8.操作
  handleFileAction=()=>{
    const { transname } = this.props.model;
    const { getFieldValue } = this.props.form;
    const url = getFieldValue('url');
    const user = getFieldValue('httpLogin');
    const password = getFieldValue('httpPassword');
    let obj = {};
    obj.transName = transname;
    obj.user = user;
    obj.password = password;
    obj.url = url;
    if(/wsdl$/.test(url)){
      getWebUrl(obj).then( (res)=> {
        // console.log(res,111);
        const {data,code} = res.data;

        if(data && code === "200" ){
          this.setState({
            WsdlData:data
          });
          message.success('加载URL操作列表成功');
        }
      });
    }else{
      message.error('必须输入wsdl格式的URL');
    }
  };

  render() {
    const { getFieldDecorator, getFieldValue} = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    const { outFieldContainerName,path } = this.state;


    return (
      <Modal
        maskClosable={false}
        visible={visible}
        title="Web服务查询"
        onCancel={this.setModelHide.bind(this)}
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
          <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>
        ]}
      >
        <Form >
          <FormItem label="步骤名称"   {...this.formItemLayout}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input spellCheck={false}/>
            )}
          </FormItem>
        </Form>

        <Tabs defaultActiveKey="1" type="card" onChange={this.changeTag.bind(this)} activeKey={this.state.activeKey}>

          <TabPane tab="查询内容" key="1" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset" style={{marginTop:15}}>
              <legend style={{marginTop:8}}>&nbsp;&nbsp;设置</legend>
              <Form>
								<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                <FormItem label="URL"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('url', {
                    initialValue:config.url?decodeURIComponent(config.url):""
                  })(
                    <Input spellCheck={false}/>
                  )}
                  <ButtonGroup style={{marginLeft:3}}>
                    <Button key="1" onClick={()=>{this.handleFileAction()}} >加载</Button>
                    <Button key="2" onClick={()=>{ this.getFieldList("list")}}>浏览</Button>
                    <Button key="3" title="只能上传到默认目录" onClick={()=>{this.handleFileUpload("list")}}>上传</Button>
                  </ButtonGroup>
                </FormItem>
                <FormItem label="URL下拉列表：" {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('operationName', {
                    initialValue:config.operationName
                  })(
                    <Select
                      // mode="combobox"
                      allowClear
                      // placeholder="通过URL的“加载”操作，加载wsdl格式的列表"
                      onChange={this.handleChangeSelect}
                    >
                      {
                        this.state.WsdlData.length>0?this.state.WsdlData.map((index)=>(<Select.Option key={index.method}>{index.method}</Select.Option>)):null
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="请求名称"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('operationRequestName', {
                    initialValue:config.operationRequestName,
                  })(
                    <Input spellCheck={false}/>
                  )}
                </FormItem>
                <FormItem label="调用步骤" {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('callStep', {
                    initialValue:config.callStep,
                  })(
                    <Input disabled={false}/>
                  )}
                </FormItem>

                <FormItem label="数据传到输出"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('passingInputData', {
                    valuePropName: 'checked',
                    initialValue:config.passingInputData?config.passingInputData:false,
                  })(
                    <Checkbox />
                  )}
                </FormItem>
                <FormItem label="兼容模式"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('compatible', {
                    valuePropName: 'checked',
                    initialValue:config.compatible?config.compatible:false,
                  })(
                    <Checkbox />
                  )}
                </FormItem>
                <FormItem label="重复元素名称"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('repeatingElementName', {
                    initialValue:config.repeatingElementName,
                  })(
                    <Input disabled={false}/>
                  )}
                </FormItem>
                <FormItem label="返回完整服务"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('returningReplyAsString', {
                    valuePropName: 'checked',
                    initialValue:config.returningReplyAsString?config.returningReplyAsString:false,
                  })(
                    <Checkbox />
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;HTTP授权</legend>
              <Form>
                <FormItem label="用户登录"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('httpLogin', {
                    initialValue:config.httpLogin,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="登录密码"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('httpPassword', {
                    initialValue:config.httpPassword,
                  })(
                    <Input type="password" autoComplete='off'/>
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;HTTP代理</legend>
              <Form >
                <FormItem label="代理主机"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('proxyHost', {
                    initialValue:config.proxyHost,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="代理端口"  {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('proxyPort', {
                    initialValue:config.proxyPort,
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
          </TabPane>
          <TabPane tab='输入' key='2' >
            <Row>
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
              columns={this.columnsIn}
              dataSource={this.state.inputFieldArgs}
              scroll={{y: 300}}
              initFuc={this.initFuc1.bind(this)}
              rowSelection={true}
              size={"small"}
              count={0}
              ref="editTable1"
              tableStyle="editTableStyle5"
              style={{marginTop:10}}
            />
          </TabPane>
          <TabPane tab={outFieldContainerName} key='3' >

            <Row>
              <Col span={12}>
                <ButtonGroup size={"small"}  >
                  <Button key="1"  onClick={this.handleAuto2.bind(this)}>获取字段</Button>
                  <Button key="2"  onClick={this.handleAdd2.bind(this)}>添加字段</Button>
                </ButtonGroup>
              </Col>
              <Col span={12} size={"small"} style={{textAlign:"right"}}>
                <Button  size={"small"} onClick={this.handleDelete2.bind(this)}>删除字段</Button>
              </Col>
            </Row>
            <EditTable
              columns={this.columnsOut}
              dataSource={this.state.outFieldArgs}
              scroll={{y: 300}}
              initFuc={this.initFuc2.bind(this)}
              rowSelection={true}
              size={"small"}
              count={0}
              ref="editTable2"
              tableStyle="editTableStyle5"
              style={{marginTop:10}}
            />

          </TabPane>

        </Tabs>

      </Modal>
    );
  }
}
//三、
const WebInput = Form.create()(HttpWebInput);
export default connect()(WebInput);
