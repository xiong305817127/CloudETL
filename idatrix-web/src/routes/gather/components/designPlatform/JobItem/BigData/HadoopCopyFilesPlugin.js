import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig } from '../../../../constant';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const Option = Select.Option;
import EditTable from '../../../common/EditTable';

class HadoopCopyFiles extends React.Component {
  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { filefolder } = props.model.config;
      let data = [];
      let count = 0;
      if(filefolder){
        for(let index of filefolder){
          data.push({
            key:count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        hodoopList:[],
        path:""
      }
    }
  };

  componentDidMount(){
    this.Request();
  }


  Request(){
    const { getHadoopServer,getDataStore } = this.props.model;
    getHadoopServer(data =>{
        this.setState({
          hodoopList:data
        });
    });

    let obj1 = {};
    obj1.type = "data";
    obj1.path = "";
    getDataStore(obj1,data=>{
      const { path } = data;
      this.setState({
        path:path
      });
    });
  }


  /*表格1*/
  Columns = [{
    title: '源环境',
    dataIndex: 'sourceConfigurationName',
    key: 'sourceConfigurationName',
    editable:true,
    width:"20%"
  },{
    title: '源文件/目录',
    dataIndex: 'sourceFilefolder',
    key: 'sourceFilefolder',
    width:"20%",
    editable:true
  },{
    title: '通配符',
    dataIndex: 'wildcard',
    key: 'wildcard',
    width:"18%",
    editable:true
  },{
    title: '目标环境',
    dataIndex: 'destinationConfigurationName',
    key: 'destinationConfigurationName',
    editable:true,
    width:"20%"
  }, {
    title: '目标文件/目录',
    dataIndex: 'destinationFilefolder',
    key: 'destinationFilefolder',
    editable:true
  }
  ];

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      };
      const { filefolder} = this.props.model.config;
      const { formatTable} = this.props.model;
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [   "sourceConfigurationName", "sourceFilefolder", "destinationConfigurationName", "destinationFilefolder", "wildcard"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(filefolder){
          sendFields = filefolder
        }
      }

      const {panel,description,transname,key,saveEntry,text} = this.props.model;

      let obj = {};

      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.parallel= values.parallel;
      obj.entryParams = {
        ...values,
        filefolder:sendFields
      };
      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };

  handleAdd = ()=>{
    const { getFieldValue } = this.props.form;
    const { getFileExist } = this.props.model;

    const file = getFieldValue("file");
    const files = getFieldValue("files");
    const targetFile = getFieldValue("targetFile");
    const targetFiles = getFieldValue("targetFiles");
    let obj = {};
    let obj1 = {};
    if(file && file != "local"){
      obj = {
        type:"hdfs",
        path:file+"::"+files,
        depth:""
      }
    }else{
      obj = {
        type:"input",
        path:files,
        depth:""
      }
    }
    if(targetFile && targetFile != "local"){
      obj1 = {
        type:"hdfs",
        path:targetFile+"::"+targetFiles,
        depth:""
      }
    }else{
      obj1 = {
        type:"input",
        path:targetFiles,
        depth:""
      }
    }

    if(file && files && targetFile && targetFiles){
      getFileExist(obj,data =>{
        if(data ===  0){
          getFileExist(obj1,data1 =>{
            if(data1 === 0) {
              const data3 = {
                "sourceConfigurationName": file,
                "sourceFilefolder": files,
                "destinationConfigurationName": targetFile,
                "destinationFilefolder": targetFiles,
                "wildcard": "*"
              }
              this.refs.editTable.handleAdd(data3);
            }
          })
        }
      });
    }else {
       message.error("请先完善上面字段! ");
    }

  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };


  /*文件模板*/
  getFieldList(name){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;

    let obj = treeViewConfig.get(panel)[name];

    let str = name === "model"?getFieldValue("file"):getFieldValue("targetFile");
    let str1 = name === "model"?formatFolder(getFieldValue("files")):formatFolder(getFieldValue("targetFiles"));
    let updateModel = name ==="model"?this.setFolder.bind(this):this.setFolder1.bind(this);

    let viewPath = "";
    let needUpFolder = false;

    if(str && str.trim()){

      let obj1 = {};
      let str2 = "";

      if(str === "local"){
        let type = "input";
				if(str1.substr(0,1) !== "/"){
					str1 = `${this.state.path}${str1}`
				}
				viewPath = str1;
        obj1 = {
          type:type,
          path:str1,
          depth:1
        }
      }else{
        obj1 = {
          type:"hdfs",
          path:str+"::"+ (str1?str1:"/"),
          depth:1
        };
        str2 = str+"::";
      };

      dispatch({
        type:"treeview/showTreeModel",
        payload:{
          ...obj,
          obj:obj1,
          needUpFolder:needUpFolder,
          updateModel:updateModel,
          prefixStr:str2,
          viewPath:viewPath
        }
      })

    }else{
      if(name === "model"){
        message.error("源环境不能为空！");
      }else{
        message.error("目标环境不能为空！");
      }
    }
  };

  /*设置文件名*/
  setFolder(str){
    if(!str){
      return;
    }
    const { setFieldsValue } = this.props.form;

    setFieldsValue({
      "files":str
    });
  };

  /*设置文件名*/
  setFolder1(str){
    if(!str){
        return;
    }
    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      "targetFiles":str
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;
    const { hodoopList,path } = this.state;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };

    const formItemLayout1 = {
      labelCol: { span: 5 },
      wrapperCol: { span: 14 }
    };

    return (
      <Modal
        visible={visible}
        title="Hadoop Copy Files"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
      >
        <Form >

          <FormItem label="作业项名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
              <Input  />
            )}
          </FormItem>
          {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'12rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
					<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
          <Tabs type="card">
            <TabPane tab="文件" key="1">
              <FormItem label="源环境"  {...formItemLayout1} style={{marginBottom:"8px"}}>
              {getFieldDecorator('file', {
                initialValue: ""
              })(
                <Select  >
                  <Option key="local" value="local">Local</Option>
                  { hodoopList.map(index=>{
                    return(
                      <Option key={index.name}>{index.name}</Option>
                    )
                  })
                  }
                </Select>
              )}
            </FormItem>

              <FormItem label="源文件/目录"  {...formItemLayout1} style={{marginBottom:"8px"}}>
                {getFieldDecorator('files', {
                  initialValue: ""
                })(
                  <Input />
                )}
                <Button onClick={()=>{this.getFieldList("model")}}>浏览</Button>
              </FormItem>
              <FormItem label="目标环境"  {...formItemLayout1} style={{marginBottom:"8px"}}>
                {getFieldDecorator('targetFile', {
                  initialValue: ""
                })(
                  <Select  >
                    <Option key="local" value="local">Local</Option>
                    { hodoopList.map(index=>{
                      return(
                        <Option key={index.name} value={index.name}>{index.name}</Option>
                      )
                    })
                    }
                  </Select>
                )}
              </FormItem>

              <FormItem label="目标文件/目录"  {...formItemLayout1} style={{marginBottom:"8px"}}>
                {getFieldDecorator('targetFiles', {
                  initialValue: ""
                })(
                  <Input />
                )}
                <Button onClick={()=>{this.getFieldList("list")}}>浏览</Button>
              </FormItem>
              <Row style={{marginBottom:"5px"}}>
                <Col span={12}>
                  <p style={{marginLeft:"5px"}}>文件/目录(请先添加文件/目录)：</p>
                </Col>
                <Col span={12}>
                  <ButtonGroup size={"small"} style={{float:"right"}} >
                    <Button     onClick={this.handleAdd.bind(this)}>添加</Button>
                    <Button     onClick={this.handleDeleteFields.bind(this)} >删除</Button>
                  </ButtonGroup>
                </Col>
              </Row>
              <EditTable columns={this.Columns} dataSource = {this.state.dataSource} count={1} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}}  rowSelection={true} ref="editTable" />
            </TabPane>
            <TabPane tab="设置" key="2">
              <Row>
                <Col span={8}>
                  <FormItem   {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('includeSubfolders', {
                      valuePropName: 'checked',
                      initialValue: config.includeSubfolders,
                    })(
                      <Checkbox>包括子目录</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem   {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('destinationIsAfile', {
                      valuePropName: 'checked',
                      initialValue: config.destinationIsAfile,
                    })(
                      <Checkbox>目标是文件</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem   {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('copyEmptyFolders', {
                      valuePropName: 'checked',
                      initialValue: config.copyEmptyFolders,
                    })(
                      <Checkbox>复制空目录</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={8}>
                  <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('createDestinationFolder', {
                      valuePropName: 'checked',
                      initialValue: config.createDestinationFolder,
                    })(
                      <Checkbox>创建目标目录</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('overwriteFiles', {
                      valuePropName: 'checked',
                      initialValue: config.overwriteFiles,
                    })(
                      <Checkbox>替换已经存在的文件</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('removeSourceFiles', {
                      valuePropName: 'checked',
                      initialValue: config.removeSourceFiles,
                    })(
                      <Checkbox>移除源文件</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={8}>
                  <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('argFromPrevious', {
                      valuePropName: 'checked',
                      initialValue: config.argFromPrevious,
                    })(
                      <Checkbox>复制上一个作业结果作为参数</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('addResultFilesname', {
                      valuePropName: 'checked',
                      initialValue: config.addResultFilesname,
                    })(
                      <Checkbox>增加文件</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    );
  }
}
const HadoopCopyFilesPlugin = Form.create()(HadoopCopyFiles);
export default connect()(HadoopCopyFilesPlugin);
