import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox,Tabs,Row,Col,message,Tooltip,Icon,Popover } from 'antd';
import Modal from "components/Modal.js";
import EditTable from '../../../common/EditTable';
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const FormItem = Form.Item;

class JsonDialog extends React.Component {
  //1.初始化
  constructor(props){
    super(props);
    const { visible } = props.model;

    if(visible === true){
      const { inputFields,fileName } = props.model.config;
      let data = [];
      let data1 = [];
      let count = 0;
      let count1 = 0;
      if(inputFields){
        for(let index of inputFields){
          data.push({
            ...index,
            "key":count++,
            "type":index.type == 0?"":index.type
          })
        }
      }
      if(fileName){
        for(let index of fileName){
          data1.push({
            "key":count1++,
            ...index
          })
        }
      }

      this.state = {
        inputFields:data,
        fileName:data1,
        inputSelect:[],
        tableSelect:[],
        path:""
      }
    }
  };
  //2.预加载
  componentDidMount(){
    const { getInputFields,transname,text,getDataStore } = this.props.model;

    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    //控件传入的参数集合：数组
    getInputFields(obj, data => {
      // console.log(111,data)
      this.setState({
        inputSelect:data
      })
    });
    // getOutFields(obj, data => {
    //   console.log(222,data)
    // });
    let obj1 = {};
    obj1.type = "data";
    obj1.path = "";
    //控件传入的路径
    getDataStore(obj1,data=>{
      const { path } = data;
      this.setState({
        path:path
      })
    })
  };
  //3.文件-标签页的表格：添加
  handleAddFields(){
    const { getFileExist } = this.props.model;
    const { getFieldValue } = this.props.form;
    const args = getFieldValue("file");
    if(!(args && args.trim())){
      message.info("请先选择'文件或其他目录'");
    }

    let obj = {
      type:"input",
      path:args,
      depth:""
    };
    if(args && args.trim()){
      getFileExist(obj,data =>{
        if(data === "200"){
          const data1 = {
            "fileName": args,
            "fileMask":'',
            "excludeFileMask": '',
            "sourceConfigurationName": "",
            "fileRequired": "N",
            "includeSubFolders": "N"
          };
          this.refs.editTable.handleAdd(data1);
        }
      })
    }
  }
  //4.文件-标签页的表格：删除
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };
  //5.字段-标签页的表格方法：添加行、删除行、获取
  handleAddFields1 = ()=>{
    const data = {
      "name": "",
      "path": "",
      "type": "",
      "format": "",
      "length": "",
      "precision": "",
      "currency": "",
      "decimal": "",
      "group": "",
      "trimType": 'none',
      "repeat": false
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleDeleteFields1 = ()=>{
    if(this.refs.editTable1.state.dataSource.length>0){
      this.refs.editTable1.handleDelete();
    }else {
      message.info('请先选中需要删除的行')
    }
  };
  handleGetField1(){
    if(this.state.inputSelect.length>0){
      const data = {
        name:this.state.inputSelect[0].name,
        path:this.state.path,
        "type": "",
        "format": "",
        "length": "",
        "precision": "",
        "currency": "",
        "decimal": "",
        "group": "",
        "trimType": 'none',
        "repeat": false
      };
      this.refs.editTable1.handleAdd(data);
    }
  };
  //6.提交参数：确定与取消
  handleFormSubmit(){
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,config,text,formatTable } = this.props.model;
    const { inputFiles,fileName } = config;
     
    form.validateFields((err, values) => {
      if (err) {
        return
      };
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "fileName", "fileMask", "excludeFileMask", "fileRequired", "includeSubFolders", "sourceConfigurationName"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args)
        }
      }else{
        if(fileName){
          sendFields = fileName
        }
      };
      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let args = [ "name","path", "type", "format","length","precision","currency","decimal","group","trimType","repeat"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,args)
        };
      }else{
        if(inputFiles){
          sendFields1 = inputFiles
        }
      };
      let sendFields2 = {};
      const { getFieldValue } = this.props.form;
      sendFields2.shortFilenameField = getFieldValue('shortFilenameField');
      sendFields2.extensionField = getFieldValue('extensionField');
      sendFields2.pathField = getFieldValue('pathField');
      sendFields2.sizeField = getFieldValue('sizeField');
      sendFields2.hiddenField = getFieldValue('hiddenField');
      sendFields2.lastModificationField = getFieldValue('lastModificationField');
      sendFields2.uriField = getFieldValue('uriField');
      sendFields2.rootUriField = getFieldValue('rootUriField');
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "fileName":sendFields,
        "inputFields":sendFields1,
        "additionalOutputFields":sendFields2,
        "includeFilename":values.includeFilename,
        "includeRowNumber":values.includeRowNumber,
        "removeSourceField":values.removeSourceField,
        "afile":values.afile,
        "rowLimit":values.rowLimit,
        "valueField":values.valueField,
        "inFields":values.inFields,
        "addResultFile":values.addResultFile,
        "rowNumberField":values.rowNumberField,
        "ignoreMissingPath":values.ignoreMissingPath,
        "readurl":values.readurl,
        "doNotFailIfNoFile":values.doNotFailIfNoFile,
        "filenameField":values.filenameField,
        "ignoreEmptyFile":values.ignoreEmptyFile,
      }
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });

    })
  }
  setModelHide (){
    const {  dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  }
  /**其他：文件模板与上传浏览文件*/
  getFieldList(name){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;

    let obj = treeViewConfig.get(panel)[name];
    let path =  name ==="model"?"":formatFolder(getFieldValue("file"));
    let updateModel = name ==="model"?this.setFolder1.bind(this):this.setFolder.bind(this);

    let viewPath = "";

    if(name != "model"){
			if(path.substr(0,1) !== "/"){
				path = `${this.state.path}${path}`
			}
			viewPath = path;
    }

    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
          ...obj.obj,
          path:path,
        },
        viewPath:viewPath,
        updateModel:updateModel
      }
    })
  };
  //模板文件名称
  setFolder1(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
        "templeteFile":str
      })
    }
  };
  //目录文件名称
  setFolder(str){
		const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
        "file":str
      })
    }
  };
  //得到表名:不存在的方法
  handleFoucsTable(){
    const { getDetails,transname,text,panel } = this.props.model;
    const { getFieldValue } = this.props.form;
    if(getFieldValue("templeteFile") || (this.refs.editTable &&this.refs.editTable.state.dataSource.length>0) ){
      let obj = {};
      let args = [];
      let fileName = "";
      obj.transName = transname;
      obj.stepName = text;
      obj.detailType = panel;
      for(let index of this.refs.editTable.state.dataSource){
        args.push(index.fileName);
      }

      if(getFieldValue("templeteFile")){
        fileName = "access::"+getFieldValue("templeteFile");
      }else{
        fileName = "data::"+args[0];
      }
      obj.detailParam = {
        flag:"getTables",
        fileName:fileName
      };

      getDetails(obj,data =>{
        if(data){
          this.setState({
            tableSelect:data
          })
        }

      })
    }else{
      message.info("文件模板与文件列表不可都为空！");
      this.setState({
        tableSelect:[]
      });
    }
  }
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
  //改变单选值:只选中一个
  handleChange(e,num){
    const { setFieldsValue } = this.props.form;
    if(e.target.checked){
      if(num === 1){
        setFieldsValue({
          "readurl":false
        })
      }else{
        setFieldsValue({
          "afile":false
        })
      }
    }

  }

  //表格-标题
  fileColumns =  [
    {
      //   title: '源配置',//无法保存
      //   dataIndex: 'sourceConfigurationName',
      //   width:"20%",
      //   key: 'sourceConfigurationName',
      //   editable:true,
      // }, {
      title: '文件/目录',
      dataIndex: 'fileName',
      width:"30%",
      key: 'fileName',
      editable:true
    }, {
      title: '通配符',
      dataIndex: 'fileMask',
      width:"15%",
      key: 'fileMask',
      editable:true,
    }, {
      title: '通配符号(排除)',
      dataIndex: 'excludeFileMask',
      width:"15%",
      key: 'excludeFileMask',
      editable:true,
    }, {
      title: '要求',
      dataIndex: 'fileRequired',
      key: 'fileRequired',
      width:"20%",
      selectable:true,
      selectArgs:[<Select.Option key="Y" value="Y">是</Select.Option>,
        <Select.Option key="N" value="N">否</Select.Option>
      ]
    }, {
      title: '包含子目录',
      dataIndex: 'includeSubFolders',
      key: 'includeSubFolders',
      selectable:true,
      width:"20%",
      selectArgs:[<Select.Option key="Y" value="Y">是</Select.Option>,
        <Select.Option key="N" value="N">否</Select.Option>
      ]
    }];
  filedsColumns = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width:"10%",
      editable:true
    },{
      title:(()=>(
          <span>路径 <Tooltip placement="top" title="路径的填写方式要遵照jsonpath规则.详情请参考：http://goessner.net/articles/JsonPath/." arrowPointAtCenter>
          <Icon type="question-circle-o" />
        </Tooltip> </span>
        ))(),
      dataIndex: 'path',
      key: 'path',
      width:"10%",
      editable:true
    },{
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width:"11%",
      selectable:true,
      selectArgs:[
        <Select.Option key="Number" value="Number">Number</Select.Option>,
        <Select.Option key="Date" value="Date">Date</Select.Option>,
        <Select.Option key="String" value="String">String</Select.Option>,
        <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
        <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
        <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
        <Select.Option key="Serializable" value="Serializable">Serializable</Select.Option>,
        <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
        <Select.Option key="Timestamp" value="Timestamp">Timestamp</Select.Option>,
        <Select.Option key="Internet Address" value="Internet Address">Internet Address</Select.Option>
      ]

    }, {
      title: '格式',
      dataIndex: 'format',
      key: 'format',
      width:"6%",
      editable:true,
    },{
      title: '长度',
      dataIndex: 'length',
      key: 'length',
      width:"6%",
      editable:true,
    }, {
      title: '精度',
      dataIndex: 'precision',
      key: 'precision',
      width:"6%",
      editable:true,
    }, {
      title: '货币',
      dataIndex: 'currency',
      key: 'currency',
      width:"8%",
      editable:true,
    },{
      title: '十进制',
      dataIndex: 'decimal',
      key: 'decimal',
      width:"8%",
      editable:true,
    },{
      title: '组',
      dataIndex: 'group',
      key: 'group',
      width:"8%",
      editable:true,
    },{
      title: '去除空字符的方式',
      dataIndex: 'trimType',
      key: 'trimType',
      width:"18%",
      selectable:true,
      selectArgs:[<Select.Option key="none" value="none">不去掉空格</Select.Option>,
      <Select.Option key="left" value="left">去掉左空格</Select.Option>,
      <Select.Option key="right" value="right">去掉右空格</Select.Option>,
      <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>,
      ]
    },{
      title: '重复',
      dataIndex: 'repeat',
      key: 'repeat',
      selectable:true,
      width:"6%",
      selectArgs:[<Select.Option key="true" value="true">是</Select.Option>,
        <Select.Option key="false" value="false">否</Select.Option>
      ]
    }
  ];
  //输入框-布局
  formItemLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 },
  };
  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  formItemLayout2 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 11 },
  };
  formItemLayout3 = {
    labelCol: { span:8},
    wrapperCol: { span: 14 },
  };
  formItemLayout4 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 16 },
  };
  formItemLayout5 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 14 },
  };
  formItemLayout6 = {
    labelCol: { span: 7 },
    wrapperCol: { span: 14 },
  };
  formItemLayout7 = {
    labelCol: { span: 9 },
    wrapperCol: { span: 14},
  };
  formItemLayout8 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15},
  };
  formItemLayout9 = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16},
  };
  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    const { path } = this.state;
    const setDisabled = ()=>{
      if(getFieldValue("inFields") === undefined){
        return config.inFields;
      }else{
        if(getFieldValue("inFields")){
          return getFieldValue("inFields");
        }else {
          return false;
        }
      }
    }//源定义在一个字段里
    const setDisabled1 = ()=>{
      if(getFieldValue("includeFilename") === undefined){
        return config.includeFilename;
      }else{
        if(getFieldValue("includeFilename")){
          return getFieldValue("includeFilename");
        }else {
          return false;
        }
      }
    }//在输出中包含文件名
    const setDisabled2 = ()=>{
      if(getFieldValue("addResultFile") === undefined){
        return config.addResultFile;
      }else{
        if(getFieldValue("addResultFile")){
          return getFieldValue("addResultFile");
        }else {
          return false;
        }
      }
    }//添加文件名
    const setDisabled3 = ()=>{
      if(getFieldValue("includeRowNumber") === undefined){
        return config.includeRowNumber;
      }else{
        if(getFieldValue("includeRowNumber")){
          return getFieldValue("includeRowNumber");
        }else {
          return false;
        }
      }
    };//在输出中包含行数

   
    return (
      <Modal
        visible={visible}
        title="Json输入"
        wrapClassName="vertical-center-modal"
        width={780}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
          <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
        maskClosable={false}
        onCancel ={this.setModelHide.bind(this)}
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
          <TabPane tab="文件" key="1" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset" style={{marginTop:10}}>
              <legend>&nbsp;&nbsp;从字段获取源</legend>
              <Form>
                <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                  {getFieldDecorator('inFields', {
                    valuePropName: 'checked',
                    initialValue:config.inFields?config.inFields:false
                  })(
                    <Checkbox >源定义在一个字段里？</Checkbox>
                  )}
                </FormItem>
                <FormItem label="从字段获取源"   style={{marginBottom:"8px"}} {...this.formItemLayout}>
                  {getFieldDecorator('valueField', {
                    initialValue:config.valueField?config.valueField:''
                  })(
                    <Select
                      mode="combobox"
                      allowClear
                      placeholder="从字段获取源"
                      disabled={!setDisabled()}
                    >
                      {
                        this.state.inputSelect?this.state.inputSelect.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):''
                      }
                    </Select>
                  )}
                </FormItem>
                <Row style={{marginLeft:"60px"}} >
                  <Col span={7}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('afile', {
                        valuePropName: 'checked',
                        initialValue:config.afile?config.afile:false,
                        onChange:(e)=>{
                          this.handleChange(e,1)
                        }
                      })(
                        <Checkbox disabled={!setDisabled()}>源是一个文件名？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={7}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('readurl', {
                        valuePropName: 'checked',
                        initialValue:config.readurl?config.readurl:false,
                        onChange:(e)=>{
                          this.handleChange(e,2)
                        }
                      })(
                        <Checkbox disabled={!setDisabled()} >以Url获取源？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={7}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('removeSourceField', {
                        valuePropName: 'checked',
                        initialValue:config.removeSourceField?config.removeSourceField:false
                      })(
                        <Checkbox disabled={!setDisabled()}>下行不通过字段？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset" style={{marginTop:10}}>
              <legend>&nbsp;&nbsp;文件或路径列表</legend>
              <Form>
                <FormItem label="文件模板" style={{marginBottom:8}} {...this.formItemLayout2}>
                  <div>
                    {getFieldDecorator('templeteFile', {//自定义
                      initialValue:''
                    })(
                      <Input placeholder="当选择文件名定义在一个字段里，文件模板不可为空"  />
                    )}
                    <ButtonGroup>
                      <Button key="1" onClick={()=>{this.getFieldList("model")}} >浏览</Button>
                      <Button key="2" onClick={()=>{this.handleFileUpload("model")}}>上传</Button>
                    </ButtonGroup>
                  </div>
                </FormItem>
                <FormItem label="文件或其他目录"   style={{marginBottom:8}} {...this.formItemLayout2}>
                  <div>
                    {getFieldDecorator('file', {//自定义
                      initialValue:""
                    })(
                      <Input disabled={setDisabled()} />
                    )}
                    <ButtonGroup>
                      <Button key="1" title="添加到下表" disabled={setDisabled()} onClick={()=>{this.getFieldList("list")}} >浏览</Button>
											<Button key="3" title="只能上传到默认目录" onClick={this.handleAddFields.bind(this)}>添加</Button>
                      <Button key="2" disabled={setDisabled()} onClick={()=>{this.handleFileUpload("list")}}>上传</Button>
                    </ButtonGroup>
                  </div>
                </FormItem>
								<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                <Row style={{margin:10}}>
                  <Col span={24} style={{textAlign:"right"}}>
                    <Button  size={"small"}  onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                  </Col>
                  <Col span={24} style={{margin:'10px 0px'}}>
                    <EditTable
                      columns={this.fileColumns}
                      disabled={setDisabled()}
                      ref="editTable"
                      scroll={{y: 140}}
                      rowSelection={true}
                      size={"small"}
                      count={1}
                      dataSource = {this.state.fileName}
                    />
                  </Col>
                  {/*显示文件名:
                   <div style={{margin:'0px 10px 0px 150px'}}>
                   <Button onClick={this.newModel} disabled={setDisabled8()}>显示文件名</Button>
                   </div>
                   */}
                </Row>
              </Form>
            </fieldset>
          </TabPane>
          <TabPane tab="内容" key="2" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset" style={{marginTop:10}}>
              <legend>&nbsp;&nbsp;设置</legend>
              <Form>
                <Row style={{marginLeft:"15%"}}>
                  <Col span={7}>
                    <FormItem  {...this.formItemLayout}>
                      {getFieldDecorator('ignoreEmptyFile', {
                        valuePropName: 'checked',
                        initialValue:config.ignoreEmptyFile?config.ignoreEmptyFile:false
                      })(
                        <Checkbox>忽略空文件</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={9}>
                    <FormItem  {...this.formItemLayout}>
                      {getFieldDecorator('doNotFailIfNoFile', {
                        valuePropName: 'checked',
                        initialValue:config.doNotFailIfNoFile?config.doNotFailIfNoFile:false
                      })(
                        <Checkbox>如果没有文件不进行报错</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem {...this.formItemLayout}>
                      {getFieldDecorator('ignoreMissingPath', {
                        valuePropName: 'checked',
                        initialValue:config.ignoreMissingPath?config.ignoreMissingPath:false
                      })(
                        <Checkbox>忽略不完整的路径</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem  label="限制" style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('rowLimit', {
                    initialValue:config.rowLimit?config.rowLimit:''
                  })(
                    <Input />
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset" >
              <legend>&nbsp;&nbsp;添加字段</legend>
              <Form>
                <Row  style={{marginLeft:"10%",marginBottom:"8px"}}>
                  <Col span={6}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('includeFilename', {
                        valuePropName: 'checked',
                        initialValue:config.includeFilename?config.includeFilename:false
                      })(
                        <Checkbox>在输出中包含文件名?</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={18}>
                    <FormItem  label="包含文件名的字段" style={{marginBottom:"0px"}} {...this.formItemLayout6}>
                      {getFieldDecorator('filenameField', {
                        initialValue:config.filenameField
                      })(
                        <Input disabled={!setDisabled1()} />
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row  style={{marginLeft:"10%",marginBottom:"8px"}}>
                  <Col span={6}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('includeRowNumber', {
                        valuePropName: 'checked',
                        initialValue:config.includeRowNumber?config.includeRowNumber:false
                      })(
                        <Checkbox >在输出中包含行数？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={18}>
                    <FormItem  label="包含行数的字段名" style={{marginBottom:"0px"}} {...this.formItemLayout6}>
                      {getFieldDecorator('rowNumberField', {
                        initialValue:config.rowNumberField
                      })(
                        <Input disabled={!setDisabled3()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset" >
              <legend>&nbsp;&nbsp;添加到结果文件名</legend>
              <Form style={{marginLeft:"10%",marginBottom:"8px"}}>
                <FormItem  {...this.formItemLayout6}>
                  {getFieldDecorator('addResultFile', {
                    valuePropName: 'checked',
                    initialValue:config.addResultFile?config.addResultFile:false,
                  })(
                    <Checkbox >添加文件名</Checkbox>
                  )}
                </FormItem>
              </Form>
            </fieldset>
          </TabPane>
          <TabPane tab="字段" key="3" style={{border:"1px solid #D9D9D9"}}>
            <Row style={{margin:10}}>
              <Col span={12}>
                <ButtonGroup size={"small"}>
                  {/**<Button key="1" onClick={this.handleGetField1.bind(this)}>获取字段</Button>**/}
                  <Button key="2" onClick={this.handleAddFields1.bind(this)}>添加字段</Button>
                 
                </ButtonGroup>
              </Col>
              <Col span={12} style={{textAlign:"right"}}>
                <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
              </Col>

              <Col span={24} style={{margin:'10px 0px'}}>
                <EditTable
                  columns={this.filedsColumns}
                  ref="editTable1"
                  scroll={{y: 300,x:1200}}
                  rowSelection={true}
                  size={"small"}
                  count={1}
                  dataSource = {this.state.inputFields}
                />
              </Col>
            </Row>
          </TabPane>
          <TabPane tab="其他输出字段" key="4" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset" >
              <Form style={{margin:10}}>
                <FormItem label="文件名字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('shortFilenameField', {
                    initialValue:config.additionalOutputFields.shortFilenameField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="扩展名字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('extensionField', {
                    initialValue:config.additionalOutputFields.extensionField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="路径字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('pathField', {
                    initialValue:config.additionalOutputFields.pathField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="文件大小字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('sizeField', {
                    initialValue:config.additionalOutputFields.sizeField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="是否为隐藏文件字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('hiddenField', {
                    initialValue:config.additionalOutputFields.hiddenField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="最后修改时间字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('lastModificationField', {
                    initialValue:config.additionalOutputFields.lastModificationField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="Uri字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('uriField', {
                    initialValue:config.additionalOutputFields.uriField
                  })(
                    <Input />
                  )}
                </FormItem>
              {/*  <FormItem label="Root uri字段"   style={{marginBottom:"8px"}} {...this.formItemLayout6}>
                  {getFieldDecorator('rootUriField', {
                    initialValue:config.additionalOutputFields.rootUriField
                  })(
                    <Input />
                  )}
                </FormItem>*/}
              </Form>
            </fieldset>

          </TabPane>
        </Tabs>
      </Modal>
    );
  }
}

const JsonInput = Form.create()(JsonDialog);
export default connect()(JsonInput);
