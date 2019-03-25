import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Tabs,Row,Col,Upload,Card,Tooltip } from 'antd';
import Modal from "components/Modal.js";
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { treeViewConfig } from '../../../../constant';

class FileNamesDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { selectedFiles } = props.model.config;
      let data = [];
      let count = 0;
      if(selectedFiles){
        for(let index of selectedFiles){
          data.push({
            "key":count,
              ...index,
              includeSubFolders:index.includeSubFolders === "Y"?"是":"否"
          });
          count++;
        }
      }

      this.state = {
        dataSource:data,
        tableSelect:[],
        path:""
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,getDataStore } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        tableSelect:data
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
    const { selectedFiles } = config;


    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [  "fileName", "fileMask", "excludeFileMask", "fileRequired", "includeSubFolders"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(selectedFiles){
          sendFields = selectedFiles
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "fileField":values.fileField ,
        "filenameField": values.filenameField,
        "wildcardField": values.wildcardField,
        "excludeWildcardField": values.excludeWildcardField,
        "includeSubFolders": values.includeSubFolders,
        "filterFileType": values.filterFileType,
        "includeRowNumber": values.includeRowNumber,
        "rowNumberField": values.rowNumberField,
        "doNotFailIfNoFile": (values.doNotFailIfNoFile == true) ? values.doNotFailIfNoFile="false" : values.doNotFailIfNoFile="true",
        "limit": values.limit,
        "addResult": values.addResult,
        "selectedFiles":sendFields
      };
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

    let obj = {
      type:"input",
      path,
      depth:""
		};
		
		if(!(path && path.trim())){
			return false;
		}

      getFileExist(obj,data =>{
        if(data === "200"){
          const data1 = {
            "fileName": path,
            "fileMask":"",
            "excludeFileMask": "",
            "fileRequired": "否",
            "includeSubFolders":"否"
          };
          this.refs.editTable.handleAdd(data1);
        }
      })
  };

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
    selectArgs:[<Select.Option key="Y" value="是" >是</Select.Option>,
      <Select.Option key="N" value="否">否</Select.Option>
    ]
  }, {
    title: '包含子目录',
    dataIndex: 'includeSubFolders',
    key: 'includeSubFolders',
    selectable:true,
    width:"17%",
    selectArgs:[<Select.Option key="Y" value="是">是</Select.Option>,
      <Select.Option key="N" value="否">否</Select.Option>
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

  getFieldList(name){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;

    let obj = treeViewConfig.get(panel)[name];
    let path = formatFolder(getFieldValue("file"));

    let viewPath = "";

		if(path.substr(0,1) !== "/"){
			path = `${this.state.path}${path}`
		}
    viewPath = path;

    let updateModel = this.setFolder.bind(this);
    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
          ...obj.obj,
          path:path
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


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { visible,config,text,handleCheckName,isMetacube } = this.props.model;
    const { path } = this.state;

    const setDisabled = ()=>{

      if(getFieldValue("fileField") === undefined){
        return config.fileField;
      }else{
        if(getFieldValue("fileField")){
          return getFieldValue("fileField");
        }else {
          return false;
        }
      }
    };


    return (

      <Modal
        visible={visible}
        title="获取文件名"
        wrapClassName="vertical-center-modal limitText"
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
								<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                <FormItem label="文件或目录"   style={{marginBottom:"8px"}} {...this.formItemLayout2}>
                  <div>
                    {getFieldDecorator('file', {
                      initialValue:""
                    })(
                      <Input disabled={setDisabled() }/>
                    )}
                      <Button disabled={setDisabled() } onClick={()=>{this.getFieldList("list")}}>浏览</Button>
											<Button   disabled={setDisabled()}  onClick={this.handleFileInput.bind(this)}>添加</Button>
                  </div>
                </FormItem>
                <div style={{margin:"0 5%"}}>
                  <Row style={{marginBottom:"5px"}}>
                    <Col span={12}>
                      <p style={{marginLeft:"5px"}}>已经选择的文件名称：</p>
                    </Col>
                    <Col span={12}>
                      <Button style={{float:"right"}} size={"small"} disabled={setDisabled()}  onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                    </Col>
                  </Row>
                  <EditTable  columns={this.fileColumns}   disabled={setDisabled()} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
                </div>
                <p style={{marginTop:15,marginLeft:"5%"}}>从字段获取文件名</p>

                <Row>
                   <Col span={12}>
                     <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                       {getFieldDecorator('fileField', {
                         valuePropName: 'checked',
                         initialValue:config.fileField
                       })(
                         <Checkbox >文件名定义在字段里</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                   <Col span={12}>
                     <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                       {getFieldDecorator('includeSubFolders', {
                         valuePropName: 'checked',
                         initialValue:config.includeSubFolders
                       })(
                         <Checkbox disabled={isMetacube}>创建子目录</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                </Row>
                <FormItem  label="从字段获取文件名"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('filenameField', {
                    initialValue:config.filenameField
                  })(
                  <Select allowClear disabled={!setDisabled()} >
                    {
                      this.state.tableSelect.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>))
                    }
                  </Select>
                  )}
                </FormItem>
                <FormItem label="从字段获取通配符"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('wildcardField', {
                    initialValue:config.wildcardField
                  })(
                    <Select allowClear  disabled={!setDisabled()}>
                      {
                        this.state.tableSelect.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="通配符（排除）"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('excludeWildcardField', {
                    initialValue:config.excludeWildcardField
                  })(
                    <Select allowClear disabled={!setDisabled()} >
                      {
                        this.state.tableSelect.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>))
                      }
                    </Select>
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="过滤" key="2">
                <FormItem label="获取"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('filterFileType', {
                    initialValue:config.filterFileType
                  })(
                    <Select>
                      <Select.Option value="all_files">所有文件</Select.Option>
                      <Select.Option value="only_files">只获取文件</Select.Option>
                      <Select.Option value="only_folders">只获取目录</Select.Option>
                    </Select>
                  )}
                </FormItem>
                <Card title="附件字段"  className="CetFileName" style={{ width: "100%"}}>
                  <Row  style={{padding:"10px 0"}}>
                    <Col span={8}>
                      <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                        {getFieldDecorator('includeRowNumber', {
                          valuePropName: 'checked',
                          initialValue:config.includeRowNumber
                        })(
                          <Checkbox >在输出中包括行号</Checkbox>
                        )}
                      </FormItem>
                    </Col>
                    <Col span={16}>
                      <FormItem  label="行号字段名" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                        {getFieldDecorator('rowNumberField', {
                          initialValue:config.rowNumberField
                        })(
                          <Input />
                        )}
                      </FormItem>
                    </Col>
                  </Row>
                </Card>
                <Row  style={{padding:"10px 0"}}>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"0px",marginLeft:"60px"}} {...this.formItemLayout}>
                      {getFieldDecorator('doNotFailIfNoFile', {
                        valuePropName: 'checked',
                        initialValue:!config.doNotFailIfNoFile
                      })(
                        <Checkbox >当没有文件目录时不报错</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={16}>
                    <FormItem  label="限制" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                      {getFieldDecorator('limit', {
                        initialValue:config.limit
                      })(
                        <Input disabled={!setDisabled()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Card title="增加到结果文件中"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
                  <FormItem  style={{marginBottom:"0px",marginLeft:"60px",padding:"10px 0"}} {...this.formItemLayout}>
                    {getFieldDecorator('addResult', {
                      valuePropName: 'checked',
                      initialValue:config.addResult
                    })(
                      <Checkbox >将文件名增加到结果文件列表中</Checkbox>
                    )}
                  </FormItem>
                </Card>
              </TabPane>
            </Tabs>
          </div>

        </Form>
      </Modal>
    );



  }
}
const GetFileNames = Form.create()(FileNamesDialog);

export default connect()(GetFileNames);
