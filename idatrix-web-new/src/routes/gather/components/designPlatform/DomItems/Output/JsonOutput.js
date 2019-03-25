//一、环境
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, message,Icon,Tooltip  } from 'antd';
import Modal from 'components/Modal';
import EditTable from '../../../common/EditTable';
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
const ButtonGroup = Button.Group;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;

//二、渲染
class JsonOutputInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible,config } = props.model;
    if(visible === true) {
      const {outputFields} = config;
      let data = [];
      let count1 = 0;
      if (outputFields) {
        for (let index of outputFields) {
          data.push({
            key: count1,
            ...index
          });
          count1++;
        }
      }
      // console.log(data1,111);
      this.state = {
        dataSource:data,
        InputData:[], //下拉选项：控件传过来的数组
        selectedValue:'1', //下拉权限,操作方式切换
        activeKey:"1",//标签页选中
        miniVisible:false,
        path:""
      };
    }
  };
  //2.前后控件参数：
  componentDidMount(){
    const { getInputFields,transname,text,getDataStore } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      // console.log(data,123);
      if(data){
        this.setState({InputData:data });
        console.log(data,"12data");
      }
    });

    let obj1 = {};
    obj1.type = "output";
    obj1.path = "";
    getDataStore(obj1,data=>{
      const { path } = data;
      this.setState({
        path:path
      })
    })
  };
  //3.1.提交表单：
  handleFormSubmit = (e) =>{
    e.preventDefault();
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,formatTable,config } = this.props.model;
    const {outputFields} = config;
    form.validateFields((err, values) => {
      console.log(values,111)
      if(err){
        return
      }
      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let arg = ["fieldName","elementName"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,arg)
        }
      }else{
        if(outputFields){
          sendFields1 = outputFields
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;//控件基本参数+5
      obj.config = {//表单参数设置
        outputFields:sendFields1,
        ...values
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  };
  //显示文件夹
  miniFormSubmit = (e) =>{
    e.preventDefault();
    const form = this.props.form;
    const { config } = this.props.model;
    form.validateFields((err, values) => {
      if (err) {
        return
      }
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
  miniModelHide (){
    this.setState({
      miniVisible: false,
      // confirmLoading: false,
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
    wrapperCol: { span: 15 },
  };
  formItemLayout2 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 },
  };
  formItemLayout5 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 17 },
  };
  //4.2.自定义标题
  columns = [
    {
      title: '字段名',
      dataIndex: 'fieldName',
      key: 'fieldName',
      width:"50%",
      selectable:true,
    }, {
      title: '元素名称',
      dataIndex: 'elementName',
      key: 'elementName',
      // width:"50%",
      // selectable:false,
      editable:true,
    }];
  //4.3.表格方法：
  handleAdd1 = ()=>{
    const data = {
      "fieldName": "",
      "elementName": "",
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleAuto1 = ()=>{
    if(this.state.InputData.length > 0){
      let args = [];
      let count = 0;
      for(let index of this.state.InputData){
        args.push({
          key:count,
          fieldName:index.name,//只有name内容来之控件
          elementName:index.name,//只有name内容来之控件
        });
        count++;
      }
      this.refs.editTable1.updateTable(args,count);
    }else {
      message.info('未找到对应的控件字段')
    }
  };
  handleDelete1 = ()=>{
    this.refs.editTable1.handleDelete();
  };
  //4.4.表格下拉框：
  initFuc1(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");//对应数组InputData的属性名对应标题key:name
    // console.log(that,'获取model方法：updateOptions');
    that.updateOptions({
      fieldName:options,//可下拉
    });
  };
  //4.5.切换栏:
  callback=(value)=>{
    // console.log(value);
    this.setState({
      activeKey:value
    })
  };
  //4.6.浏览
  onhandleChange(){
    const {dispatch} = this.props;
		const {panel} = this.props.model;
		const { getFieldValue,formatFolder } = this.props.form;
		let path = formatFolder(getFieldValue("fileName"));
		let obj = obj = treeViewConfig.get(panel)["list"];;
		
		if(path.substr(0,1) !== "/"){
			path = `${this.state.path}${path}`
		}
		let viewPath = path;

    let updateModel = this.setFolder.bind(this);
    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
          ...obj.obj,path
        },
        viewPath:viewPath,
        updateModel:updateModel
      }
    })
  };




  //设置文件名:setFieldsValue与getFieldDecorator、getFieldProps不能共用
  setFolder(str){
    if(!str) return false;

    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      "fileName":str
    })

  };
  //4.7上传
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

  //4.9.选择操作
  handleChangeValue=(value)=>{
    console.log(value,222);
    this.setState({
      selectedValue:value
    })
  };

  render() {

    const {path} = this.state;
    const { getFieldDecorator, getFieldValue} = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    const setDisabled8 = ()=>{
      if(getFieldValue("servletOutput") === undefined){
        return config.servletOutput;
      }else{
        if(getFieldValue("servletOutput")){
          return getFieldValue("servletOutput");
        }else {
          return false;
        }
      }
    };//servLet
    return (
      <Row>
        <Col>
          <Modal
          maskClosable={false}
          visible={visible}
          title="Json输出"
          onCancel={this.setModelHide.bind(this)}
          wrapClassName="vertical-center-modal"
          width={750}
          footer={[
            <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
            <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
          ]}
        >
          <FormItem label="步骤名称"   {...this.formItemLayout}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input spellCheck={false}/>
            )}
          </FormItem>
          <Tabs onChange={this.callback} type="card" >


            <TabPane tab="内容" key="1" style={{border:"1px solid #D9D9D9"}}>

              <FormItem label="操作"   {...this.formItemLayout} style={{margin:'15px 0 0 0',width:'100%'}}>
                {getFieldDecorator('operationType', {
                  initialValue:config.operationType+""
                })(
                  <Select style={{width:'100%'}} onChange={this.handleChangeValue}>
                    <Select.Option key="0" value="0">Output value</Select.Option>
                    <Select.Option key="1" value="1">写到文件</Select.Option>
                    <Select.Option key="2" value="2">Output value and write to file</Select.Option>
                  </Select>
                )}
              </FormItem>

              <fieldset className="ui-fieldset">
                <legend>&nbsp;&nbsp;设置</legend>
                <Form >
                  <FormItem label="条目名称"   {...this.formItemLayout2} style={{marginBottom:8}}>
                    {getFieldDecorator('jsonBloc', {
                      initialValue:config.jsonBloc,
                    })(
                      <Input spellCheck={false}/>
                    )}
                  </FormItem>
                  <FormItem label="一个数据条目的数据行"   {...this.formItemLayout5} style={{marginBottom:8}}>
                    {getFieldDecorator('nrRowsInBloc', {
                      initialValue:config.nrRowsInBloc,
                    })(
                        <Input spellCheck={false} title="在此指定一个区块中的行数.0表示同一个唯一区块中的所有行."/> 
                    )}
                     
                  </FormItem>
                  <FormItem label="输出值"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('outputValue', {
                      initialValue:config.outputValue,
                    })(
                        <Input spellCheck={false} disabled={this.state.selectedValue==='1'?true:false} placeholder="输出值内容"/>
                    )}
                  </FormItem>
                  <FormItem label="兼容模式"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('compatibilityMode', {
                      valuePropName: 'checked',
                      initialValue:config.compatibilityMode?config.compatibilityMode:false,
                    })(
                      <Checkbox/>
                    )}
                  </FormItem>
                </Form>
              </fieldset>

              <fieldset className="ui-fieldset">
                <legend>&nbsp;&nbsp;输出文件</legend>
                <Form >
									<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                  <FormItem label="文件名"   {...this.formItemLayout1} style={{marginBottom:0}}>
                    {getFieldDecorator('fileName', {
                      initialValue:config.fileName,
                    })(
                      <Input spellCheck={false} disabled={setDisabled8()}/>
                    )}
                    <Button key="1" onClick={()=>{ this.onhandleChange()}}>浏览</Button>
                  </FormItem>
                  <FormItem label="追加方式"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('fileAppended', {
                      valuePropName: 'checked',
                      initialValue:config.fileAppended
                    })(
                      <Checkbox disabled={setDisabled8()}/>
                    )}
                  </FormItem>
                  <FormItem label="创建父文件夹"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('createparentfolder', {
                      valuePropName: 'checked',
                      initialValue:config.createparentfolder
                    })(
                      <Checkbox disabled={setDisabled8()}/>
                    )}
                  </FormItem>
                  <FormItem label="启动时不创建文件夹"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('doNotOpenNewFileInit', {
                      valuePropName: 'checked',
                      initialValue:config.doNotOpenNewFileInit
                    })(
                      <Checkbox disabled={setDisabled8()}/>
                    )}
                  </FormItem>

                  <FormItem label="扩展名"   {...this.formItemLayout2} style={{marginBottom:8}}>
                    {getFieldDecorator('extension', {
                      initialValue:config.extension,
                    })(
                      <Input spellCheck={false} disabled={setDisabled8()}/>
                    )}
                  </FormItem>
                  <FormItem label="编码"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('encoding', {
                      initialValue:config.encoding,
                    })(
                      <Select disabled={this.state.selectedValue==='1'?true:false}>
                        <Select.Option value="GBK">GBK</Select.Option>
                        <Select.Option value="ISO-8859-1">ISO-8859-1</Select.Option>
                        <Select.Option value="GB2312">GB2312</Select.Option>
                        <Select.Option value="UTF-8">UTF-8</Select.Option>
                        <Select.Option value="Big5">Big5</Select.Option>
                      </Select>
                    )}
                  </FormItem>

                  <FormItem label="发送服务到servlet"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('servletOutput', {
                      valuePropName: 'checked',
                      initialValue:config.servletOutput
                    })(
                      <Checkbox disabled={this.state.selectedValue==='1'?true:false}/>
                    )}
                  </FormItem>

                  <FormItem label="添加日期到文件名"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('dateInFilename', {
                      valuePropName: 'checked',
                      initialValue:config.dateInFilename
                    })(
                      <Checkbox disabled={setDisabled8()}/>
                    )}
                  </FormItem>

                  <FormItem label="添加时间到文件名"   {...this.formItemLayout2} style={{marginBottom:0}}>
                    {getFieldDecorator('timeInFilename', {
                      valuePropName: 'checked',
                      initialValue:config.timeInFilename
                    })(
                      <Checkbox disabled={setDisabled8()}/>
                    )}
                  </FormItem>
                  {/*<div style={{margin:'0px 10px 0px 150px'}}>*/}
                    {/*<Button onClick={this.newModel} disabled={setDisabled8()}>显示文件名</Button>*/}
                  {/*</div>*/}
                  <FormItem label="添加文件到结果文件"   {...this.formItemLayout2} style={{marginBottom:8}}>
                    {getFieldDecorator('addToResult', {
                      valuePropName: 'checked',
                      initialValue:config.addToResult
                    })(
                      <Checkbox disabled={setDisabled8()}/>
                    )}
                  </FormItem>

                </Form>
              </fieldset>
            </TabPane>

            <TabPane tab="字段" key="2" style={{border:"1px solid #D9D9D9"}}>
                <Row style={{margin:10}}>
                  <Col span={12}>
                    <ButtonGroup size={"small"}>
                      <Button key="1" onClick={this.handleAuto1.bind(this)}>获取字段</Button>
                      <Button key="2" onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12} style={{textAlign:"right"}}>
                    <Button  size={"small"}  onClick={this.handleDelete1.bind(this)}>删除字段</Button>
                  </Col>

                  <Col span={24} style={{margin:'10px 0px'}}>
                    <EditTable
                      columns={this.columns}
                      dataSource={this.state.dataSource}
                      scroll={{y: 300}}
                      initFuc={this.initFuc1.bind(this)}
                      rowSelection={true}
                      size={"small"}
                      count={0}
                      ref="editTable1"
                      tableStyle="editTableStyle5"
                    />
                  </Col>
                </Row>
            </TabPane>
          </Tabs>
        </Modal>
        </Col>
        {/*过滤文件名*/}
        {/*<Col>*/}
          {/*<Modal*/}
            {/*visible={this.state.miniVisible}*/}
            {/*title="输出文件"*/}
            {/*wrapClassName="vertical-center-modal"*/}
            {/*onCancel={this.miniModelHide.bind(this)}*/}
            {/*width={500}*/}
            {/*footer={[*/}
              {/*<Button key="submit" type="primary" size="large" onClick={this.miniFormSubmit.bind(this)}>确定</Button>,*/}
              {/*<Button key="back" size="large" onClick={this.miniModelHide.bind(this)}>取消</Button>,*/}
            {/*]}*/}
          {/*>*/}
            {/*<Form >*/}

              {/*<FormItem label="过滤"   {...this.formItemLayout1} style={{marginBottom:8}}>*/}
                {/*{getFieldDecorator('a', {*/}
                  {/*initialValue:null,*/}
                {/*})(*/}
                  {/*<Input spellCheck={false}/>*/}
                {/*)}*/}
                {/*<ButtonGroup>*/}
                  {/*<Button key="1" onClick={()=>{ console.log(111)}}><Icon type="scan" /></Button>*/}
                  {/*<Button key="2"  onClick={()=>{console.log(222)}}><Icon type="search" /></Button>*/}
                {/*</ButtonGroup>*/}
              {/*</FormItem>*/}

              {/*<FormItem label="输出文件"   {...this.formItemLayout2} style={{marginBottom:8}}>*/}
                {/*<EditTable*/}
                  {/*columns={this.columns}*/}
                  {/*dataSource={[]}*/}
                  {/*scroll={{y: 300}}*/}
                  {/*// initFuc={this.initFuc2.bind(this)}*/}
                  {/*rowSelection={true}*/}
                  {/*size={"small"}*/}
                  {/*count={0}*/}
                  {/*ref="editTable2"*/}
                  {/*tableStyle="editTableStyle5"*/}
                  {/*style={{marginTop:10}}*/}
                {/*/>*/}
              {/*</FormItem>*/}

            {/*</Form>*/}
          {/*</Modal>*/}
        {/*</Col>*/}
      </Row>
    )
  }
}
//三、传参、调用：
const JsonOutput = Form.create()(JsonOutputInput);
export default connect()(JsonOutput);
