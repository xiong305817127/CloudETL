/*
   alisa 
 */
import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Tabs,Row,Col,Upload,Card,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import ExcelInputModel from '../Model/ExcelInputModel';

class ReadContentInput extends React.Component {

  constructor(props){
    super(props);
    const { visible,prevStepNames } = props.model;
    if(visible === true){
      const { files,field,sheetName } = props.model.config;
      console.log(props.model.config,"props.model.config");
      let data = [];
      let data1 = [];
      let data2 = [];
      if(files){
        let count = 0;
        for(let index of files){
          data.push({
            "key":count,
            ...index
          });
          count++;
        }
      }
    
      this.state = {
        fileSource:data,
        tableSelect:prevStepNames,
        saveSelect:[],
        path:""
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
        saveSelect:data
      })
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
    const { fileName,sheetName,field } = config;


    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let fileTable = [];
      if(this.refs.fileTable ){
        if(this.refs.fileTable.state.dataSource.length>0){
          let args = [  "fileName", "fileMask", "excludeFileMask", "fileRequired", "includeSubFolders"];
          fileTable = formatTable(this.refs.fileTable.state.dataSource,args);
        }
      }else{
        if(fileName){
          fileTable = fileName
        }
      }
   

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        files:fileTable,
        ...values
      }
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  }

  /*文件名添加字段*/
  handleFileInput = ()=>{
    const { getFieldValue } = this.props.form;
    const { getFileExist } = this.props.model;
    const path = getFieldValue("file");

    let obj = {
      type:"input",
      path,
      depth:""
    };

    if(path && path.trim()){
      getFileExist(obj,data =>{
        if(data === "200"){
          const data1 = {
            "fileName": path,
            "fileMask":"",
            "excludeFileMask": "",
            "fileRequired": "N",
            "includeSubFolders": "N"
          };
          this.refs.fileTable.handleAdd(data1);
        }
      })
    }
  };
  /*文件表格*/
  fileColumns =  [{
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
  /*文件表增加字段*/
  handleFileAdd = ()=>{
    const data = {
      "fileName": "",
      "fileMask":"",
      "excludeFileMask": "",
      "fileRequired": "N",
      "includeSubFolders": "N"
    }
    this.refs.fileTable.handleAdd(data);
  }
  /*工作表删除字段*/
  handleFileDelete = ()=>{
    this.refs.fileTable.handleDelete();
  }
 
  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  formItemLayout = {
    wrapperCol: { span:18},
  };
  formItemLayout2 = {
    labelCol: { span: 6 },
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

  formItemLayout10 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 11},
  };


  getFieldList(name){
  	
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;
    let obj = treeViewConfig.get(panel)[name];
    let path = name ==="list"?formatFolder(getFieldValue("file")):"";
    let updateModel = this.setFolder.bind(this);

    let type = obj.obj.type;
    let viewPath = "";

    if(name !== "model"){
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

    const setDisabled = ()=>{
      if(getFieldValue("acceptingFilenames") === undefined){
        return config.acceptingFilenames;
      }else{
        if(getFieldValue("acceptingFilenames")){
          return getFieldValue("acceptingFilenames");
        }else {
          return false;
        }
      }
    }
   const setDisabled1 = ()=>{
      if(getFieldValue("includeFileName") === undefined){
        return config.includeFileName;
      }else{
        if(getFieldValue("includeFileName")){
          return getFieldValue("includeFileName");
        }else {
          return false;
        }
      }
    }
  

    return (

      <Modal
        visible={visible}
        title="读取内容"
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
                <FormItem label="文件类型"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('type', {
                    initialValue:config.type?config.type:"word"
                  })(
                    <Select allowClear>
                      <Option key="pdf" value="pdf">PDF</Option>
                      <Option key="word" value="word">Word</Option>
                      <Option key="text" value="text">TEXT</Option>
                      <Option key="excel" value="excel">Excel</Option>
                      <Option key="ppt" value="ppt">PPT</Option>
                    </Select>
                  )}
                </FormItem>
                <div className="tableLimitArea">  
                <p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                <FormItem label="文件或目录"   style={{marginBottom:"8px"}} {...this.formItemLayout10}>
                  <div>
                    {getFieldDecorator('file', {
                      initialValue:""
                    })(
                      <Input disabled={setDisabled() }/>
                    )}
                      <Button disabled={setDisabled()} onClick={()=>{this.getFieldList("list")}} >浏览</Button>
                      <Button disabled={setDisabled()} title="添加到下表" onClick={this.handleFileInput.bind(this)}>添加</Button>
                      <Button disabled={setDisabled()} title="只能上传到默认目录" onClick={()=>{this.handleFileUpload("list")}}>上传</Button>
                  </div>
                </FormItem>
                <Row style={{width:"100%"}}>
                   <Col span={12}>
                      <div>文件或目录列表：</div>
                   </Col>
                   <Col span={12}>
                       <Button style={{float:"right"}} disabled={setDisabled()} size={"small"} onClick={this.handleFileDelete.bind(this)} >删除字段</Button>
                   </Col>
                </Row>
                </div>
                <EditTable  columns={this.fileColumns}   disabled={setDisabled()} tableStyle="editTableStyle5" ref="fileTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.fileSource}/>
                  <p style={{marginTop:15}}>从前面的步骤获取文件名</p>
                <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                  {getFieldDecorator('acceptingFilenames', {
                    valuePropName: 'checked',
                    initialValue:config.acceptingFilenames
                  })(
                    <Checkbox >从以前的步骤接受文件名</Checkbox>
                  )}
                </FormItem>
                <FormItem label="从哪个步骤读文件名"   style={{marginBottom:"8px"}} {...this.formItemLayout3}>
                  {getFieldDecorator('acceptingStepName', {
                    initialValue:config.acceptingStepName
                  })(
                    <Select disabled={!setDisabled()} >
                      {
                        this.state.tableSelect.map((index)=>(<Select.Option key={index}>{index}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="保存文件名的字段名"   style={{marginBottom:"8px"}} {...this.formItemLayout3}>
                  {getFieldDecorator('acceptingField', {
                    initialValue:config.acceptingField,
                  })(
                    <Select disabled={!setDisabled()} >
                      {
                        this.state.saveSelect.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
              </TabPane>
             
              <TabPane tab="设置" key="2">
                   
                    <FormItem label="文件内容字段名"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
		                  {getFieldDecorator('contentFieldName', {
		                    initialValue:config.contentFieldName
		                  })(
		                    <Input />
		                  )}
		            </FormItem>
		            <Row>
                        <Col offset={5}>
                             <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout3}>
			                      {getFieldDecorator('pageRow', {
			                        valuePropName: 'checked',
			                        initialValue:config.pageRow
			                      })(
			                        <Checkbox >一页/一段/一行是否作为一条新数据</Checkbox>
			                      )}
			                  </FormItem>
                        </Col>
                     </Row>
	                          <FormItem label="文件编码"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
				                  {getFieldDecorator('encoding', {
				                    initialValue:config.encoding?config.encoding:"UTF-8"
				                  })(
				                    <Select>
				                      <Option value="GBK">GBK</Option>
				                      <Option value="ISO-8859-1">ISO-8859-1</Option>
				                      <Option value="GB2312">GB2312</Option>
				                      <Option value="UTF-8">UTF-8</Option>
				                      <Option value="Big5">Big5</Option>
				                    </Select>
				                  )}
				                </FormItem>
                      <Row>
				        <Col offset={5}>
                            <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout3}>
			                      {getFieldDecorator('includeFileName', {
			                        valuePropName: 'checked',
			                        initialValue:config.includeFileName
			                      })(
			                        <Checkbox >是否包含文件路径</Checkbox>
			                      )}
			                 </FormItem>
				        </Col>
				      </Row>
				         <FormItem label="文件名称字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
			                  {getFieldDecorator('fileNameFieldName', {
			                    initialValue:config.fileNameFieldName?config.fileNameFieldName:"uri"
			                  })(
			                    <Input disabled={!setDisabled1()}/>
			                  )}
			            </FormItem>
			          <Row>
			            <Col offset={5}>
                             <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout3}>
				                  {getFieldDecorator('addResultFile', {
				                    valuePropName: 'checked',
				                    initialValue:config.addResultFile
				                  })(
				                    <Checkbox >文件加入结果</Checkbox>
				                  )}
				             </FormItem>
			            </Col>
                   </Row>
              </TabPane>
             

            </Tabs>
          </div>

        </Form>
         <ExcelInputModel />
      </Modal>
    );


  }
}
const ReadContentInputFrom = Form.create()(ReadContentInput);

export default connect()(ReadContentInputFrom);
