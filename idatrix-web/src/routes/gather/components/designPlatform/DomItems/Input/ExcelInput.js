import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox,Tabs,Row,Col,Card,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
import EditTable from '../../../common/EditTable';
import ExcelInputModel from '../Model/ExcelInputModel';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;

class ExcelDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible,prevStepNames } = props.model;
    if(visible === true){
      const { fileName,field,sheetName } = props.model.config;
      let data = [];
      let data1 = [];
      let data2 = [];
      if(fileName){
        let count = 0;
        for(let index of fileName){
          data.push({
            "key":count,
            ...index
          });
          count++;
        }
      }
      if(sheetName){
        let count1 = 0;
        for(let index of sheetName){
          data1.push({
            "key":count1,
            ...index
          });
          count1++;
        }
      }
      if(field){
        let count2 = 0;
        for(let index of field){
          data2.push({
            "key":count2,
            ...index
          });
          count2++;
        }
      }
      this.state = {
        fileSource:data,
        sheetSource:data1,
        fieldsSource:data2,
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
      let sheetTable = [];
      let fieldsTable = [];
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

      if(this.refs.sheetTable){
        if(this.refs.sheetTable.state.dataSource.length>0){
          let args = [ "sheetName","startRow","startColumn"];
          sheetTable = formatTable(this.refs.sheetTable.state.dataSource,args);
        }
      }else{
        if(sheetName){
          sheetTable = sheetName
        }
      }

      if(this.refs.fieldsTable){
        if(this.refs.fieldsTable.state.dataSource.length>0){
          let args = [  "name", "typedesc", "length", "precision", "trimtypecode", "repeated", "format", "currencysymbol", "decimalsymbol", "groupsymbol"];
          fieldsTable = formatTable(this.refs.fieldsTable.state.dataSource,args);
        }
      }else{
        if(field){
          fieldsTable = field
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        fileName:fileTable,
        field:fieldsTable,
        sheetName:sheetTable,
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
    let type = "input";

    let obj = {
      type:type,
      path,depth:""
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

  /*工作表格*/
  sheetColumns =  [{
    title: '工作表名称',
    dataIndex: 'sheetName',
    key: 'sheetName',
    editable:true
  }, {
    title: '起始行',
    dataIndex: 'startRow',
    width:'25%',
    key: 'startRow',
    editable:true
  }, {
    title: '起始列',
    width:'25%',
    dataIndex: 'startColumn',
    key: 'startColumn',
    editable:true
  }
  ];
  /*工作表格增加字段*/
  handleSheetAdd(){
      const data = {
        "sheetName": "",
        "startRow": 0,
        "startColumn": 0
      }
     this.refs.sheetTable.handleAdd(data);
  }
  /*工作表删除字段*/
  handleSheetDelete(){
    this.refs.sheetTable.handleDelete();
  }
  /*获取工作表名称*/
  getSheetName(){
    const { getFieldValue } = this.props.form;
    const { getDetails,transname,text,panel } = this.props.model;

      let fileName = "";
      if(getFieldValue("templeteFile")){
        fileName = "excel::"+getFieldValue("templeteFile");
      }else{
        if(this.refs.fileTable && this.refs.fileTable.state.dataSource[0]){
          fileName = "data::"+this.refs.fileTable.state.dataSource[0].fileName;
        }
      }
      if(fileName && fileName.trim()){
          let obj = {};
          obj.transName = transname;
          obj.stepName = text;
          obj.detailType = panel;
          obj.detailParam = {
            flag:"getSheets",
            fileName:fileName,
            spreadSheetType:getFieldValue("spreadSheetType"),
            encoding:getFieldValue("encoding")
          };

          getDetails(obj,data =>{
            const { dispatch } = this.props;
            if(data && data.length>0){
              dispatch({
                 type:"excelinputmodel/show",
                  tableNames:data,
                  visible:true,
                  handleSheetUpdate:this.handleSheetUpdate.bind(this)
              })
            }
          })


      }else{
        message.error("请选择文件中的文件模板或文件目录");
      }
  }
  /*更新工作表方法*/
  handleSheetUpdate(args){
    let names = [];
    let count = 0;
    if(args.length>0){
        for(let index of args){
            names.push({
              "key":index.key,
              "sheetName": index.title,
              "startRow": 0,
              "startColumn": 0
            })
            count++;
        }
    }
    this.refs.sheetTable.updateTable(names,count);
  }


  /*字段表格*/
  fieldsColumns = [{
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width:"13%",
    editable:true
  }, {
    title: '类型',
    dataIndex: 'typedesc',
    key: 'typedesc',
    width:"12%",
    selectable:true,
    selectArgs:[
      <Select.Option key="1" value="Number">Number</Select.Option>,
      <Select.Option key="3" value="Date">Date</Select.Option>,
      <Select.Option key="2" value="String">String</Select.Option>,
      <Select.Option key="4" value="Boolean">Boolean</Select.Option>,
      <Select.Option key="5" value="Integer">Integer</Select.Option>,
      <Select.Option key="6" value="BigNumber">BigNumber</Select.Option>,
      <Select.Option key="8" value="Binary">Binary</Select.Option>,
      <Select.Option key="9" value="Timestamp">Timestamp</Select.Option>,
      <Select.Option key="10" value="Internet Address">Address</Select.Option>
    ]

  }, {
    title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"8%",
    editable:true,
  }, {
    title: '精度',
    dataIndex: 'precision',
    key: 'precision',
    width:"8%",
    editable:true,
  },{
    title: '去除空格类型',
    dataIndex: 'trimtypecode',
    key: 'trimtypecode',
    width:"15%",
    selectable:true,
    selectArgs:[<Select.Option key="none" value="none">不去掉空格</Select.Option>,
      <Select.Option key="left" value="left">去掉左空格</Select.Option>,
      <Select.Option key="right" value="right">去掉右空格</Select.Option>,
      <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>,
    ]
  }, {
    title: '重复',
    dataIndex: 'repeated',
    key: 'repeated',
    width:"8%",
    selectable:true,
    selectArgs:[<Select.Option key="true" value="true">是</Select.Option>,
      <Select.Option key="false" value="false">否</Select.Option>
    ]
  },{
    title: '格式',
    dataIndex: 'format',
    key: 'format',
    width:"8%",
    editable:true,
  },{
    title: '货币符号',
    dataIndex: 'currencysymbol',
    key: 'currencysymbol',
    width:"8%",
    editable:true,
  },{
    title: '小数',
    dataIndex: 'decimalsymbol',
    key: 'decimalsymbol',
    width:"8%",
    editable:true,
  },{
    title: '分组',
    dataIndex: 'groupsymbol',
    key: 'groupsymbol',
    width:"8%",
    editable:true,
  }
  ];
  /*字段表格增加字段*/
  handleFieldsAdd(){
    const data = {
      "name": "",
      "typedesc": "",
      "length": "",
      "precision": "",
      "trimtypecode": "",
      "repeated": "",
      "format": "",
      "currencysymbol": "",
      "decimalsymbol": "",
      "groupsymbol": ""
    }
    this.refs.fieldsTable.handleAdd(data);
  }
  /*字段表删除字段*/
  handleFieldsDelete(){
    this.refs.fieldsTable.handleDelete();
  }
  /*获取头部数据字段*/
  getHeaderDataFields(){
    const { getFieldValue } = this.props.form;
    const { getDetails,transname,text,panel,formatTable} = this.props.model;
    let args = [];
    let args1 = [];
    let obj = {};
    let fileName = "";
    if(this.refs.fileTable && this.refs.fileTable.state.dataSource.length>0){
      for(let index of this.refs.fileTable.state.dataSource){
        args.push(index.fileName);
      }
    }

    if(getFieldValue("templeteFile")){
      fileName = "excel::"+getFieldValue("templeteFile");
    }else{
      if(args[0]){
        fileName = "data::"+args[0];
      }
    }

    if(this.refs.sheetTable && this.refs.sheetTable.state.dataSource.length>0){
      args1 = formatTable(this.refs.sheetTable.state.dataSource,["sheetName","startRow","startColumn"]);
    }
    if(fileName && fileName.trim()){

    obj.transName = transname;
    obj.stepName = text;
    obj.detailType = panel;
    obj.detailParam = {
      flag:"getFields",
      fileName:fileName,
      spreadSheetType:getFieldValue("spreadSheetType"),
      encoding:getFieldValue("encoding"),
      sheetName:args1
    };

    getDetails(obj,data=>{
        if(data.length>0){
            let args = [];
            let count = 0;
            for(let index of data){
              args.push({
                "key":count,
                "name": index[0],
                "typedesc": index[1],
                "length":index[2]?index[2]:"-1",
                "precision":index[3]?index[3]:"-1",
                "trimtypecode": index[4],
                "repeated": index[5] === "Y"?"true":"false",
                "format": "",
                "currencysymbol": "",
                "decimalsymbol": "",
                "groupsymbol": ""
              });
              count++;
            }
          this.refs.fieldsTable.updateTable(args,count);
        }
    })
  }else{
    message.error("请选择文件中的文件模板或文件目录");
  }
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


  /*设置文件名*/
  setFolder(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
          "file":str
      })
    }
  };

  setFolder1(str){
    const { setFieldsValue } = this.props.form;

    if(str){
      setFieldsValue({
        "templeteFile":str
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
      if(getFieldValue("errorIgnored") === undefined){
        return !config.errorHandling.errorIgnored;
      }else{
        if(getFieldValue("errorIgnored")){
          return !getFieldValue("errorIgnored");
        }else {
          return true;
        }
      }
    }

    return (

      <Modal
        visible={visible}
        title="Excel输入"
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
                <FormItem label="表格类型(引擎)"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('spreadSheetType', {
                    initialValue:config.spreadSheetType
                  })(
                    <Select>
                      <Option value="POI">Excel 2007 XLSX (Apache POI)</Option>
                        <Option value="JXL">Excel 97-2003 XLS (JXL)</Option>
                      <Option value="SAX_POI">Excel 2007 XLSX (Apache POI Streaming)</Option>
                      <Option value="ODS">Open Office ODS (ODFDOM)</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem label="文件模板"    {...this.formItemLayout2}>
                  <div>
                    {getFieldDecorator('templeteFile', {
                      initialValue:config.templeteFile
                    })(
                      <Input  />
                    )}
                    <Button  onClick={()=>{this.getFieldList("model")}} >浏览</Button>
                    <Button  onClick={()=>{this.handleFileUpload("model")}}>上传</Button>
                  </div>
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
              <TabPane tab="工作表" key="2">
                <Row style={{marginBottom:"5px"}}>
                  <Col span={12}>
                    <p style={{marginLeft:"5px"}}>要读取的工作列表：</p>
                  </Col>
                  <Col span={12}>
                    <ButtonGroup size={"small"} style={{float:"right"}} >
                      <Button  onClick={this.handleSheetAdd.bind(this)} >添加字段</Button>
                      <Button  onClick={this.getSheetName.bind(this)}>获取工作表名称</Button>
                      <Button  onClick={this.handleSheetDelete.bind(this)}>删除字段</Button>
                    </ButtonGroup>
                  </Col>
                </Row>
                <EditTable  columns={this.sheetColumns} rowSelection={true} dataSource = {this.state.sheetSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="sheetTable"   count={4}/>
              </TabPane>
              <TabPane tab="内容" key="3">
                <Row  style={{marginLeft:"10%",marginBottom:"8px"}}>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('startsWithHeader', {
                        valuePropName: 'checked',
                        initialValue:config.startsWithHeader
                      })(
                        <Checkbox >头部</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('ignoreEmptyRows', {
                        valuePropName: 'checked',
                        initialValue:config.ignoreEmptyRows
                      })(
                        <Checkbox >非空记录</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('stopOnEmpty', {
                        valuePropName: 'checked',
                        initialValue:config.stopOnEmpty
                      })(
                        <Checkbox >停在空记录</Checkbox>
                      )}
                    </FormItem>
                  </Col>

                </Row>
                <FormItem label="限制"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('rowLimit', {
                    initialValue:config.rowLimit
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="编码"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('encoding', {
                    initialValue:config.encoding?config.encoding:"GBK"
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
                <Card title="结果文件名"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
                  <FormItem  style={{marginBottom:"0px",marginLeft:"60px",padding:"10px 0"}} {...this.formItemLayout}>
                    {getFieldDecorator('isaddresult', {
                      valuePropName: 'checked',
                      initialValue:config.isaddresult
                    })(
                      <Checkbox >添加文件名</Checkbox>
                    )}
                  </FormItem>
                </Card>
              </TabPane>
              <TabPane tab="错误处理" key="4">
                <Row  style={{marginLeft:"10%",marginBottom:"8px"}}>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('strictTypes', {
                        valuePropName: 'checked',
                        initialValue:config.strictTypes
                      })(
                        <Checkbox >严格类型？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('errorIgnored', {
                        valuePropName: 'checked',
                        initialValue:config.errorIgnored
                      })(
                        <Checkbox >忽略错误？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('errorLineSkipped', {
                        valuePropName: 'checked',
                        initialValue:config.errorLineSkipped
                      })(
                        <Checkbox disabled={setDisabled1()}>跳过错误行？</Checkbox>
                      )}
                    </FormItem>
                  </Col>


                </Row>
                <Row>
                  <Col span={15}>
                    <FormItem label="告警文件目录"   style={{marginBottom:"8px"}} {...this.formItemLayout9}>
                      {getFieldDecorator('warningFilesDestinationDirectory', {
                        initialValue:config.warningFilesDestinationDirectory
                      })(
                        <Input disabled={setDisabled1()}/>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem label="扩展名"   style={{marginBottom:"8px"}} {...this.formItemLayout9}>
                      {getFieldDecorator('warningFilesExtension', {
                        initialValue:config.warningFilesExtension
                      })(
                        <Input disabled={setDisabled1()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row>
                  <Col span={15}>
                    <FormItem label="错误文件目录"   style={{marginBottom:"8px"}} {...this.formItemLayout9}>
                      {getFieldDecorator('errorFilesDestinationDirectory', {
                        initialValue:config.errorFilesDestinationDirectory
                      })(
                        <Input disabled={setDisabled1()}/>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem label="扩展名"   style={{marginBottom:"8px"}} {...this.formItemLayout9}>
                      {getFieldDecorator('errorFilesExtension', {
                        initialValue:config.errorFilesExtension
                      })(
                        <Input disabled={setDisabled1()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row>
                  <Col span={15}>
                    <FormItem label="失败行数文件目录"   style={{marginBottom:"8px"}} {...this.formItemLayout9}>
                      {getFieldDecorator('lineNumberFilesDestinationDirectory', {
                        initialValue:config.lineNumberFilesDestinationDirectory
                      })(
                        <Input disabled={setDisabled1()}/>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem label="扩展名"   style={{marginBottom:"8px"}} {...this.formItemLayout9}>
                      {getFieldDecorator('lineNumberFilesExtension', {
                        initialValue:config.lineNumberFilesExtension
                      })(
                        <Input disabled={setDisabled1()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
              </TabPane>
              <TabPane tab="字段" key="5">
                <Row style={{marginBottom:"5px"}}>
                  <Col span={12}>
                    <ButtonGroup  size={"small"}>
                      <Button   onClick={this.handleFieldsAdd.bind(this)}>添加字段</Button>
                      <Button   onClick={this.getHeaderDataFields.bind(this)} >获取头部数据字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12}>
                     <Button style={{float:"right"}}  size={"small"} onClick={this.handleFieldsDelete.bind(this)} >删除字段</Button>
                  </Col>
                </Row>
                <EditTable  columns={this.fieldsColumns}  tableStyle="editTableStyle5" ref="fieldsTable" scroll={{y: 300,x:1000}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.fieldsSource}/>

              </TabPane>


              <TabPane tab="其他输出字段" key="6">
                <FormItem label="文件名称字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('fileField', {
                    initialValue:config.fileField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="工作表名称字段"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('sheetField', {
                    initialValue:config.sheetField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="表单的行号列"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('sheetRowNumberField', {
                    initialValue:config.sheetRowNumberField
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="行号列"   style={{marginBottom:"8px"}} {...this.formItemLayout4}>
                  {getFieldDecorator('rowNumberField', {
                    initialValue:config.rowNumberField
                  })(
                    <Input />
                  )}
                </FormItem>
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
        <ExcelInputModel />
      </Modal>
    );


  }
}
const ExcelInput = Form.create()(ExcelDialog);

export default connect()(ExcelInput);
