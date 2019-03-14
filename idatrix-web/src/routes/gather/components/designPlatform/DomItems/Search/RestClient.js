/**HttpClient查询控件*/
//一、
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, message } from 'antd';
import Modal from 'components/Modal';
import EditTable from '../../../common/EditTable';
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
const ButtonGroup = Button.Group;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
//二、
class HttpClientInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible,config } = props.model;
    if(visible === true) {
      const {headers,parameter,matrixParameter} = config;
      let data1 = [];
      let data2 = [];
      let data3 = [];
      let count1 = 0;
      let count2 = 0;
      let count3 = 0;
      if (headers) {
        for (let index of headers) {
          data1.push({
            key: count1,
            ...index
          });
          count1++;
        }
      }
      if (parameter) {
        for (let index of parameter) {
          data2.push({
            key: count2,
            ...index
          });
          count2++;
        }
      }
      if (matrixParameter) {
        for (let index of matrixParameter) {
          data3.push({
            key: count3,
            ...index
          });
          count3++;
        }
      }
      // console.log(data1,111);
      this.state = {
        dataSource1:data1,
        dataSource2:data2,
        dataSource3:data3,
        InputData:[], //下拉选项：控件传过来的数组
				selectedValue:config.bodyField || 'GET', //下拉选项,请求方式切换
				
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
      }
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
  };
  //3.1.提交表单：
  handleFormSubmit = (e) =>{
    e.preventDefault();
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,formatTable,config } = this.props.model;
    const {headers,parameter,matrixParameter} = config;
    form.validateFields((err, values) => {
      if(err){
        return
      }
      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let arg = ["field","name"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,arg)
        }
      }else{
        if(headers){
          sendFields1 = headers
        }
      }
      let sendFields2 = [];
      if(this.refs.editTable2){
        if(this.refs.editTable2.state.dataSource.length>0){
          let arg = ["field","name"];
          sendFields2 = formatTable(this.refs.editTable2.state.dataSource,arg)
        }
      }else{
        if(parameter){
          sendFields2 = parameter
        }
      }
      let sendFields3 = [];
      if(this.refs.editTable3){
        if(this.refs.editTable3.state.dataSource.length>0){
          let arg = ["field","name"];
          sendFields3 = formatTable(this.refs.editTable3.state.dataSource,arg)
        }
      }else{
        if(matrixParameter){
          sendFields3 = matrixParameter
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;//控件基本参数+5
      obj.config = {//表单参数设置
        headers:sendFields1,
        parameter:sendFields2,
        matrixParameter:sendFields3,
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
	
	formItemLayout3 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 12 }
  };
  //4.2.自定义标题
  columnsRest = [
    {
      title: '名称',
      dataIndex: 'field',
      key: 'field',
      width:"50%",
      selectable:true,//selectable:true
    }, {
      title: '参数',
      dataIndex: 'name',
      key: 'name',
      // width:"30%",
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
  columnsMatrix = [
    {
      title: '独立参数1',
      dataIndex: 'field',
      key: 'field',
      width:"50%",
      selectable:true,
      // editable:true
    }, {
      title: '独立参数2',
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
  handleAdd3 = ()=>{
    const data = {
      "field": "",
      "name": ""
    };
    this.refs.editTable3.handleAdd(data);
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
  handleAuto3 = ()=>{
    if(this.state.InputData.length >0){
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
      this.refs.editTable3.updateTable(args,count);
    }else {
      message.error('未找到对应的控件字段')
    }
  };
  handleDelete1 = ()=>{
    this.refs.editTable1.handleDelete();
  };
  handleDelete2 = ()=>{
    this.refs.editTable2.handleDelete();
  };
  handleDelete3 = ()=>{
    this.refs.editTable3.handleDelete();
  };
  //4.4.表格下拉框：
  initFuc1(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");//对应数组InputData的属性名设置下拉内容
    // console.log(that,'获取model方法：updateOptions');
    that.updateOptions({
      field:options,
    });
  };
  initFuc2(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      field:options,
    });
  };
  initFuc3(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      field:options,
    });
  };
  //4.5.下拉选项：
  handleChange = (value) => {
    // console.log(value,111);
    this.setState({
      selectedValue : value,
    });
  };
  //4.6.上传文件：自定义model
  //文件模板
  getFieldList(name){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;
    // console.log(name,111);//列表名称：'list'
    // console.log(panel);//Rest
    let obj = treeViewConfig.get(panel)[name];//constant.js对应方法:Rest[list]
    let path = formatFolder(getFieldValue("trustStoreFile"));//获取后台目录
    let updateModel = this.setFolder.bind(this);

		let viewPath = "";
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
        "trustStoreFile":str
      })
    }
  };
  //调用文件上传组件
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

  render() {
    const { getFieldDecorator, getFieldValue} = this.props.form;
		const { visible,config,text,handleCheckName } = this.props.model;
		const { path } = this.state;

    //手动输入开关：disabled=true
    const setDisabled1 = ()=>{
      if(getFieldValue("urlInField") === undefined){
        return config.urlInField;//false
      }else{
        if(getFieldValue("urlInField")){
          return getFieldValue("urlInField");//true
        }else {
          return false;
        }
      }
    };
    //省略选中方法赋值：true/false
    const setDisabled2 = ()=>{
      if(getFieldValue("dynamicMethod") === undefined){
        return config.dynamicMethod;
      }else{
        if(getFieldValue("dynamicMethod")){
          return getFieldValue("dynamicMethod");//true
        }else {
          return false;
        }
      }
    };
    const setDisabled3 = ()=>{//记住密码无关联disable
      if(getFieldValue("preemptive") === undefined){
        return config.preemptive;
      }else{
        if(getFieldValue("preemptive")){
          return getFieldValue("preemptive");//true
        }else {
          return false;
        }
      }
    };

    return (
      <Modal
        maskClosable={false}
        visible={visible}
        title="Rest客户端查询"
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
              initialValue:text?text:'',
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
        </Form>

        <Tabs type="card" style={{margin:"0 3%"}}>

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
                    valuePropName: 'checked',
                    initialValue:config.urlInField?config.urlInField:false
                  })(
                    <Checkbox />
                  )}
                </FormItem>
                <FormItem label="URL可选项：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('urlField', {
                    initialValue:config.urlField?config.urlField:''
                  })(
                    <Select
                      allowClear
                      disabled={!setDisabled1()}
                    >
                      {
                        this.state.InputData.length>0?this.state.InputData.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):null
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="自定义请求方式：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('method', {
                    initialValue:config.method?config.method:''
                  })(
                    <Select
                      allowClear
                      disabled={setDisabled2()}
                      onChange={this.handleChange}
                    >
                      <Select.Option value="GET">GET</Select.Option>
                      <Select.Option value="POST">POST</Select.Option>
                      <Select.Option value="PUT">PUT</Select.Option>
                      <Select.Option value="DELETE">DELETE</Select.Option>
                      <Select.Option value="HEAD">HEAD</Select.Option>
                      <Select.Option value="OPTIONS">OPTIONS</Select.Option>
                      <Select.Option value="PATCH">PATCH</Select.Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem label="获取请求方式？"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('dynamicMethod', {
                    valuePropName: 'checked',
                    initialValue:config.dynamicMethod?config.dynamicMethod:false
                  })(
                    <Checkbox />
                  )}
                </FormItem>
                <FormItem label="获取的请求方式：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('methodFieldName', {
                    initialValue:config.methodFieldName?config.methodFieldName:''
                  })(
                    <Select
                      // mode="combobox"
                      allowClear
                      disabled={!setDisabled2()}
                    >
                      {
                        this.state.InputData.length>0?this.state.InputData.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):null
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="主体内容：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('bodyField', {
                    initialValue:config.bodyField?config.bodyField:''
                  })(
                    <Select
                      disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}
                      allowClear
                    >
                      {
                        this.state.InputData.length>0?this.state.InputData.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):null
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="文件格式类型：" {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('applicationType', {
                    initialValue:config.applicationType?config.applicationType:''
                  })(
                    <Select
                      allowClear
                    >
                      <Select.Option value="TEXT PLAIN">TEXT PLAIN</Select.Option>
                      <Select.Option value="XML">XML</Select.Option>
                      <Select.Option value="JSON">JSON</Select.Option>
                      <Select.Option value="XHTML">XHTML</Select.Option>
                      <Select.Option value="FORM URLENCODED">FORM URLENCODED</Select.Option>
                      <Select.Option value="ATOM XML">ATOM XML</Select.Option>
                      <Select.Option value="SVG XML">SVG XML</Select.Option>
                      <Select.Option value="TEXT XML">TEXT XML</Select.Option>
                    </Select>
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;输出过滤器</legend>
              <Form >
                <FormItem label="结果字段名"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('fieldName', {
                    initialValue:config.fieldName?config.fieldName:'',
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="状态节点名称"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('resultCodeFieldName', {
                    initialValue:config.resultCodeFieldName?config.resultCodeFieldName:'',
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="请求时间"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('responseTimeFieldName', {
                    initialValue:config.responseTimeFieldName?config.responseTimeFieldName:'',
                    rules: [{ whitespace:true, required: false, message: 'HTTP状态节点名称' },]
                  })(
                    <Input/>
                  )}
                </FormItem>
                <FormItem label="请求头部信息"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('responseHeaderFieldName', {
                    initialValue:config.responseHeaderFieldName?config.responseHeaderFieldName:'',
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
          </TabPane>

          <TabPane tab="验证与SSL加密" key="2" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;HTTP身份验证</legend>
              <Form >
                <FormItem label="用户登录"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('httpLogin', {
                    initialValue:config.httpLogin?config.httpLogin:'',
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="登录密码"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('httpPassword', {
                    initialValue:config.httpPassword?config.httpPassword:'',
                  })(
                    <Input type="password" autoComplete='off'/>
                  )}
                </FormItem>
                <FormItem label="记住用户与密码"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('preemptive', {//参数false
                    valuePropName: 'checked',
                    initialValue:config.preemptive?config.preemptive:false,
                  })(
                    <Checkbox  />
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;使用代理</legend>
              <Form >
                <FormItem label="代理主机"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('proxyHost', {
                    initialValue:config.proxyHost?config.proxyHost:'',
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="代理端口"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('proxyPort', {
                    initialValue:config.proxyPort?config.proxyPort:'',
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend style={{marginTop:8}}>&nbsp;&nbsp;SSL加密</legend>
							<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
              <Form >
                <FormItem label="上传文件"  {...this.formItemLayout3} style={{marginBottom:8}}>
                  {getFieldDecorator('trustStoreFile', {
                    initialValue:config.trustStoreFile?config.trustStoreFile:'',
                  })(
                    <Input />
									)}
									<Button key="1" onClick={()=>{ this.getFieldList("list")}} >浏览</Button>
                  <Button key="2" title="只能上传到默认目录" onClick={()=>{this.handleFileUpload("list")}}>上传</Button>
                </FormItem>
                <FormItem label="存储密码"  {...this.formItemLayout1} style={{marginBottom:8}}>
                  {getFieldDecorator('trustStorePassword', {
                    initialValue:config.trustStorePassword?config.trustStorePassword:'',
                  })(
                    <Input type="password" autoComplete='off'/>
                  )}
                </FormItem>
              </Form>
            </fieldset>
          </TabPane>

          <TabPane tab="字段" key="3" >
            <div style={{margin:"10px 0"}}>
              <Row>
                <Col span={24}><p style={{margin:"0 0 10px 5px"}}>头部信息:</p></Col>
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
                columns={this.columnsRest}
                dataSource={this.state.dataSource1}
                scroll={{y: 300}}
                initFuc={this.initFuc1.bind(this)}
                rowSelection={true}
                size={"small"}
                count={0}
                ref="editTable1"
                tableStyle="editTableStyle5"
                style={{marginTop:10}}
              />
            </div>

            <div style={{margin:"10px 0"}}>
              <Row>
                <Col span={24}><p style={{margin:"0 0 10px 5px"}}>请求参数:</p></Col>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button key="1" onClick={this.handleAuto2.bind(this)} disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}>获取字段</Button>
                    <Button key="2" onClick={this.handleAdd2.bind(this)} disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}>添加字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12} style={{textAlign:"right"}}>
                  <Button  size={"small"}  onClick={this.handleDelete2.bind(this)} disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}>删除字段</Button>
                </Col>
              </Row>

              <EditTable
                columns={this.columnsHeader}
                dataSource={this.state.dataSource2}
                size={"small"}
                style={{marginTop:10}}
                ref="editTable2"
                tableStyle="editTableStyle5"
                initFuc={this.initFuc2.bind(this)}
                scroll={{y: 300}}
                rowSelection={true}
                count={0}
              />
            </div>

            <div style={{margin:"10px 0"}}>
              <Row>
                <Col span={24}><p style={{margin:"0 0 10px 5px"}}>独立参数:</p></Col>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button key="1" onClick={this.handleAuto3.bind(this)} disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}>获取字段</Button>
                    <Button key="2" onClick={this.handleAdd3.bind(this)} disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}>添加字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12} style={{textAlign:"right"}}>
                  <Button  size={"small"}  onClick={this.handleDelete3.bind(this)} disabled={this.state.selectedValue ==="GET"||this.state.selectedValue ==="HEAD"||this.state.selectedValue ==="OPTIONS"}>删除字段</Button>
                </Col>
              </Row>
              <EditTable
                columns={this.columnsMatrix}
                dataSource={this.state.dataSource3}
                size={"small"}
                style={{marginTop:10}}
                ref="editTable3"
                tableStyle="editTableStyle5"
                initFuc={this.initFuc3.bind(this)}
                scroll={{y: 300}}
                rowSelection={true}
                count={0}
              />
            </div>

          </TabPane>

        </Tabs>

      </Modal>
    );
  }
}
//三、
const HttpClient = Form.create()(HttpClientInput);
export default connect()(HttpClient);
