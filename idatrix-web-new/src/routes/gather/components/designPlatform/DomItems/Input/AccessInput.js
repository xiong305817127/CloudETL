import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox,Tabs,Row,Col,message } from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
import EditTable from '../../../common/EditTable';

const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const FormItem = Form.Item;

class AccessDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;

    if(visible === true){
      const { inputFields,fileName } = props.model.config;
      let data = [];
      let data1 = [];
      if(inputFields){
        let count = 0;
        for(let index of inputFields){
          data.push({
            ...index,
            "key":count++,
            "typedesc":index.typedesc == 0?"":index.typedesc
          })
        }
      }
      if(fileName){
        let count = 0;
        for(let index of fileName){
          data1.push({
            "key":count++,
            ...index
          })
        }
      };

      this.state = {
        inputFields:data,
        fileName:data1,
        path:"",
        inputSelect:[],
        tableSelect:[]
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,getDataStore,panel } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        inputSelect:data
      });
    });

    let obj1 = {};
    obj1.type = "data";
    obj1.path = "";
    getDataStore(obj1,data=>{
      const { path } = data;
      this.setState({
        path:path
      })
    })
  };

  handleAdd1 = ()=>{
    const data = {
      "name": null,
      "column": null,
      "typedesc": "",
      "format": null,
      "length": "",
      "precision": "",
      "currencysymbol": null,
      "decimalsymbol": null,
      "groupsymbol": null,
      "trimtypecode": "0",
      "repeated": false
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleDeleteFields1 = ()=>{
    this.refs.editTable1.handleDelete();
  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };


  setModelHide (){
    const {  dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  }

  handleFormSubmit(){
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,config,text,formatTable } = this.props.model;
    const { inputFiles,fileName } = config;


    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let args = [ "name", "column", "typedesc","format","length","precision","currencysymbol","decimalsymbol","groupsymbol","trimtypecode","repeated"];
          sendFields = formatTable(this.refs.editTable1.state.dataSource,args);
        }
      }else{
        if(inputFiles){
          sendFields = inputFiles
        }
      }
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "fileName", "fileMask", "excludeFileMask", "fileRequired", "includeSubFolders"];
          sendFields1 = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(fileName){
          sendFields1 = fileName
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "fileName":sendFields1,
        "inputFields":sendFields,
        ...values
      }
      saveStep(obj,key,data=>{
         if(data.code === "200"){
           this.setModelHide();
         }
      });

    })
  }
  /*添加*/
  handleFileInput(){
    const { getFileExist } = this.props.model;
    const { getFieldValue } = this.props.form;
    const path = getFieldValue("file");
    let type = "input";

    if(path && path.trim()){
      getFileExist({
				type, path,
        depth:""
			},data =>{
        if(data === "200"){
          const data1 = {
            "fileName": path,
            "fileMask":"",
            "excludeFileMask": "",
            "fileRequired": "N",
            "includeSubFolders": "N"
          };
          this.refs.editTable.handleAdd(data1);
        }
      })
    }
  }

  /*文件表格*/
  fileColumns =  [
    {
    title: '文件/目录',
    dataIndex: 'fileName',
    width:"22%",
    key: 'fileName',
    editable:true
  }, {
    title: '通配符',
    dataIndex: 'fileMask',
    width:"22%",
    key: 'fileMask',
    editable:true
  }, {
    title: '通配符号(排除)',
    dataIndex: 'excludeFileMask',
    width:"21%",
    key: 'excludeFileMask',
    editable:true,
  }, {
    title: '要求',
    dataIndex: 'fileRequired',
    key: 'fileRequired',
    width:"12%",
    selectable:true,
    selectArgs:[<Select.Option key="Y" value="Y">是</Select.Option>,
      <Select.Option key="N" value="N">否</Select.Option>
    ]
  }, {
    title: '包含子目录',
    dataIndex: 'includeSubFolders',
    key: 'includeSubFolders',
    selectable:true,
    width:"17%",
    selectArgs:[<Select.Option key="Y" value="Y">是</Select.Option>,
      <Select.Option key="N" value="N">否</Select.Option>
    ]
  }
  ];

  filedsColumns = [
    {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width:"10%",
    editable:true
  },{
    title: '列',
    dataIndex: 'column',
    key: 'column',
    width:"10%",
    editable:true
  },{
    title: '类型',
    dataIndex: 'typedesc',
    key: 'typedesc',
    width:"11%",
    selectable:true,
    selectArgs:[
      <Select.Option key="1" value="1">Number</Select.Option>,
      <Select.Option key="3" value="3">Date</Select.Option>,
      <Select.Option key="2" value="2">String</Select.Option>,
      <Select.Option key="4" value="4">Boolean</Select.Option>,
      <Select.Option key="5" value="5">Integer</Select.Option>,
      <Select.Option key="6" value="6">BigNumber</Select.Option>,
      <Select.Option key="8" value="8">Binary</Select.Option>,
      <Select.Option key="9" value="9">Timestamp</Select.Option>,
      <Select.Option key="10" value="10">Internet Address</Select.Option>
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
    dataIndex: 'currencysymbol',
    key: 'currencysymbol',
    width:"8%",
    editable:true,
  },{
    title: '十进制',
    dataIndex: 'decimalsymbol',
    key: 'decimalsymbol',
    width:"8%",
    editable:true,
  },{
    title: '组',
    dataIndex: 'groupsymbol',
    key: 'groupsymbol',
    width:"8%",
    editable:true,
  },{
    title: '去掉空格类型',
    dataIndex: 'trimtypecode',
    key: 'trimtypecode',
    width:"18%",
    selectable:true,
    selectArgs:[<Select.Option key="none" value="none">不去掉空格</Select.Option>,
      <Select.Option key="left" value="left">去掉左空格</Select.Option>,
      <Select.Option key="right" value="right">去掉右空格</Select.Option>,
      <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>,
    ]
  },{
      title: '重复',
      dataIndex: 'repeated',
      key: 'repeated',
    selectable:true,
    width:"6%",
    selectArgs:[<Select.Option key="true" value="true">是</Select.Option>,
      <Select.Option key="false" value="false">否</Select.Option>
    ]
    }
  ];

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  formItemLayout = {
    wrapperCol: { span:18},
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


  /*得到表名*/
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

  handleGetOutField(){
    const { getDetails,transname,text,panel } = this.props.model;
    const { getFieldValue,setFieldsValue } = this.props.form;
    let fileName = "";
      if(getFieldValue("templeteFile")){
        fileName = "access::"+getFieldValue("templeteFile");
      }else{
        if(this.refs.editTable && this.refs.editTable.state.dataSource[0]){
          fileName = "data::"+this.refs.editTable.state.dataSource[0].fileName;
        }else{
            message.info("文件模板与文件列表不可都为空！");
            this.setState({
              tableSelect:[]
            });
            setFieldsValue({"tableName":""})
            return false;
        }
      }

     if(getFieldValue("tableName")){
      let obj = {};
      obj.transName = transname;
      obj.stepName = text;
      obj.detailType = panel;
      obj.detailParam = {
        flag:"getFields",
        fileName:fileName,
        tableName:getFieldValue("tableName")
      };
      getDetails(obj,data =>{
          let args = [];
          let count = 0;
          for(let index of data){
             args.push({
               "key":count++,
               "name": index[0],
               "column": index[1],
               "typedesc": index[2] === 0?"":index[2],
               "format": index[3],
               "length": index[4],
               "precision": index[5],
               "currencysymbol": index[6],
               "decimalsymbol": index[7],
               "groupsymbol": index[8],
               "trimtypecode": index[9],
               "repeated": index[10] === "N"?"false":"true"
             })
          }
        this.refs.editTable1.updateTable(args,count);
      })
    }else {
        message.info("内容中，表不可为空！");
     }
  };

  /*文件模板*/
  getFieldList(name){
      const {dispatch} = this.props;
      const {getFieldValue} = this.props.form;
			const {formatFolder,panel} = this.props.model;

      let obj = treeViewConfig.get(panel)[name];
      let path =  name ==="model"?"":formatFolder(getFieldValue("file"));
      let updateModel = name ==="model"?this.setFolder1.bind(this):this.setFolder.bind(this);

    let type = obj.obj.type;
    let viewPath = "";

    if(name != "model"){
				if(path.substr(0,1) !== "/"){
					path = `${this.state.path}${path}`
				}
        type = obj.obj.type;
        viewPath = path;
    }

      dispatch({
         type:"treeview/showTreeModel",
          payload:{
              ...obj,
             obj:{
               ...obj.obj,
               path:path,
               type:type
             },
            viewPath:viewPath,
            updateModel:updateModel
          }
      })
  };

  setFolder1(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
        "templeteFile":str
      })
    }
  };

  /*设置文件名*/
  setFolder(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
          "file":str
      })
    }
  };

  /*调用文件上传组件*/
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
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    const { path } = this.state;

    console.log(this.state.inputFields,"输入信息111");

    const setDisabled = ()=>{
     if(getFieldValue("filefield") === undefined){
        return config.filefield;
      }else{
        if(getFieldValue("filefield")){
          return getFieldValue("filefield");
        }else {
          return false;
        }
      }
    }

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
    }

    const setDisabled2 = ()=>{
     if(getFieldValue("includeTablename") === undefined){
        return config.includeTablename;
      }else{
        if(getFieldValue("includeTablename")){
          return getFieldValue("includeTablename");
        }else {
          return false;
        }
      }
    }

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
    };

    return (

      <Modal
        visible={visible}
        title="Access输入"
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
            <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
            <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
        maskClosable={false}
        onCancel ={this.setModelHide.bind(this)}
      >
        <Form >
          <FormItem label="步骤名称"  {...this.formItemLayout1}>
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
              <TabPane tab="文件" key="1">
                <FormItem label="文件模板"   {...this.formItemLayout2}>
                  <div>
                    {getFieldDecorator('templeteFile', {
                      initialValue:config.templeteFile
                    })(
                      <Input  />
                    )}
                    <Button  onClick={()=>{this.getFieldList("model")}} >浏览</Button>
                    <Button  onClick={()=>{this.handleFileUpload("model")}} >上传</Button>
                  </div>
                </FormItem>
                <div className="tableLimitArea">
								<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                <FormItem label="文件或路径"   style={{marginBottom:"8px"}} {...this.formItemLayout2}>
                  <div>
                    {getFieldDecorator('file', {
                      initialValue:""
                    })(
                      <Input disabled={setDisabled()} />
                    )}
                    <Button disabled={setDisabled()} onClick={()=>{this.getFieldList("list")}} >浏览</Button>
                    <Button disabled={setDisabled()} title="添加到下表" onClick={this.handleFileInput.bind(this)} >添加</Button>
                    <Button disabled={setDisabled()} title="只能上传到默认目录" onClick={()=>{this.handleFileUpload("list")}} >上传</Button>
                  </div>
                </FormItem>
                <Row style={{width:"100%"}}  >
                    <Col span={12}>
                        <div>文件或路径列表：</div>
                    </Col>
                    <Col span={12}>
                        <Button style={{float:"right"}} disabled={setDisabled()} size={"small"} onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                    </Col>
                </Row>

                </div>
                <EditTable  columns={this.fileColumns}   disabled={setDisabled()} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.fileName}/>

                <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                  {getFieldDecorator('filefield', {
                    valuePropName: 'checked',
                    initialValue:config.filefield
                  })(
                    <Checkbox >文件名定义在一个字段里？</Checkbox>
                  )}
                </FormItem>
                <FormItem label="从字段获取文件名"   style={{marginBottom:"8px"}} {...this.formItemLayout3}>
                  {getFieldDecorator('dynamicFilenameField', {
                    initialValue:config.dynamicFilenameField
                  })(
                    <Select disabled={!setDisabled()} >
                      {
                        this.state.inputSelect.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="内容" key="2">
                <FormItem label="表"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('tableName', {
                    initialValue:config.tableName
                  })(
                    <Select  onFocus ={this.handleFoucsTable.bind(this)} >
                      {
                        this.state.tableSelect.map((index)=>(<Select.Option key={index}>{index}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
                <p style={{marginLeft:"5%",marginTop:15}}>附加字段</p>
                <Row  style={{marginLeft:"10%",marginBottom:"8px"}}>
                  <Col span={6}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('includeFilename', {
                        valuePropName: 'checked',
                        initialValue:config.includeFilename
                      })(
                        <Checkbox  disabled={setDisabled()}>在输出中包含文件名</Checkbox>
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
                      {getFieldDecorator('includeTablename', {
                        valuePropName: 'checked',
                        initialValue:config.includeTablename
                      })(
                        <Checkbox >在输出中包含表名？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={18}>
                    <FormItem  label="包含表名的字段名" style={{marginBottom:"0px"}} {...this.formItemLayout6}>
                      {getFieldDecorator('tablenameField', {
                        initialValue:config.tablenameField
                      })(
                        <Input disabled={!setDisabled2()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row  style={{marginLeft:"10%",marginBottom:"8px"}}>
                  <Col span={6}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('includeRowNumber', {
                        valuePropName: 'checked',
                        initialValue:config.includeRowNumber
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
                <FormItem  style={{marginBottom:"20px",marginLeft:"10%"}} {...this.formItemLayout}>
                  {getFieldDecorator('resetRowNumber', {
                    valuePropName: 'checked',
                    initialValue:config.resetRowNumber,
                  })(
                    <Checkbox disabled={!setDisabled3()}>每个文件的结果集行数</Checkbox>
                  )}
                </FormItem>
                <FormItem  label="限制" style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('rowLimit', {
                    initialValue:config.rowLimit
                  })(
                    <Input disabled={setDisabled()}/>
                  )}
                </FormItem>

                <p style={{marginLeft:"5%",marginTop:15}}>结果文件名</p>
                <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                  {getFieldDecorator('isaddresult', {
                    valuePropName: 'checked',
                    initialValue:config.isaddresult,
                  })(
                    <Checkbox >添加文件名</Checkbox>
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="字段" key="3">
                <Row style={{margin:"5px 0",width:"100%"}}  >
                  <Col span={12}  >
                    <ButtonGroup size={"small"}>
                      <Button    onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                      <Button     onClick={this.handleGetOutField.bind(this)}>获取字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12}>
                     <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                  </Col>
                </Row>
                <EditTable  columns={this.filedsColumns}   tableStyle="editTableStyle5" ref="editTable1" scroll={{y: 300,x:1200}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.inputFields}/>

              </TabPane>
              <TabPane tab="其他输出字段" key="4">
                <FormItem label="文件名字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('shortFileFieldName', {
                    initialValue:config.shortFileFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="扩展名字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('extensionFieldName', {
                    initialValue:config.extensionFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="路径字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('pathFieldName', {
                    initialValue:config.pathFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="文件大小字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('sizeFieldName', {
                    initialValue:config.sizeFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="是否为隐藏文件字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('hiddenFieldName', {
                    initialValue:config.hiddenFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="最后修改时间字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('lastModificationTimeFieldName', {
                    initialValue:config.lastModificationTimeFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="Uri字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('uriNameFieldName', {
                    initialValue:config.uriNameFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="Root uri字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('rootUriNameFieldName', {
                    initialValue:config.rootUriNameFieldName
                  })(
                    <Input />
                  )}
                </FormItem>
              </TabPane>
            </Tabs>
          </div>

        </Form>
      </Modal>
    );


  }
}
const AccessInput = Form.create()(AccessDialog);

export default connect()(AccessInput);
