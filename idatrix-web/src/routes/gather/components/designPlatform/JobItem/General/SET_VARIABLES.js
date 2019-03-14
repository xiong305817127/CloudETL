import React from "react";
import { connect } from 'dva';
import { Button, Form, Input,Select,Checkbox,Row,Col} from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const Option = Select.Option;
import { treeViewConfig } from '../../../../constant';
import EditTable from '../../../common/EditTable';

class SET_VARIABLES extends React.Component {
 constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { variableName } = props.model.config;
      let data = [];
      let count = 0;
      if(variableName){
        for(let index of variableName){
          data.push({
            key:count,
            ...index
          });
          count++;
        }
      }

      this.state = {
        dataSource:data,
        path:""
      }
    }
  };

    componentDidMount(){
        const { getDataStore } = this.props.model;
        let obj1 = {};
        obj1.type = "data";
        obj1.path = "";
        getDataStore(obj1,data=>{
            const { path } = data;
            this.setState({
                path:path
            })
        })
    }

  /*表格1*/
  columns = [
     {
      title: '变量名',
      dataIndex: 'variableName',
      editable:true,
      width:"33%"
     },{
      title: '值',
      dataIndex: 'variableValue',
      editable:true,
      width:"33%"
     },{
      title: '变量有效范围',
      dataIndex: 'variableType',
      width:"33%",
      selectable:true,
      selectArgs:[
         <Select.Option key="JVM" value="0">在JVM中有效</Select.Option>,
         <Select.Option key="job" value="1">在前作业有效</Select.Option>,
         <Select.Option key="Fjob" value="2">在父作业有效</Select.Option>,
         <Select.Option key="Sjob" value="3">在根作业有效</Select.Option>,
      ]
    }];


  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate(e){
    e.preventDefault();
    const {panel,description,transname,key,saveEntry,text,formatTable,config} = this.props.model;
    const { variableName} = config;

    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];

      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "variableName", "variableValue", "variableType"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(variableName){
          sendFields = variableName
        }
      }

      let obj = {};
      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.parallel= values.parallel;
      obj.entryParams = {
        filename:values.filename,
        fileVariableType:values.fileVariableType,
        replaceVars:values.replaceVars,
        variableName:sendFields
      };

      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };

  handleAdd = ()=>{
    const data = {
       "variableName":"",
       "variableValue":"",
       "variableType":"",
    };
    this.refs.editTable.handleAdd(data);
  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  getFolderList(name){
     const {dispatch} = this.props;
     const {getFieldValue} = this.props.form;
    const {panel,formatFolder} = this.props.model;
    let obj = treeViewConfig.get(panel)[name];
    let updateModel = this.setFolder.bind(this);
		let path = formatFolder(getFieldValue("filename"));
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

  setFolder(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
          "filename":str
      })
    }
  }

    /*调用文件上传组件*/
  handleFileUpload1(){
    const {dispatch} = this.props;
    dispatch({
      type:"uploadfile/showModal",
      payload:{
        visible:true,
        model:"data"
      }
    });
  };
  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;
    const { path } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };


    return (
       <Modal
        visible={visible}
        title="设置变量"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}>
        <Form >
          <FormItem label="作业项名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
               <Input />
            )}
          </FormItem>
          {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'9rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
						<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
           <FormItem label="属性文件名"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('filename', {
              initialValue: config.filename,
            })(
               <Input min={0} style={{width:'80%'}} />
            )}
            <Button onClick={()=>{this.getFolderList("list")}}>浏览</Button>
            <Button onClick={this.handleFileUpload1.bind(this)} >上传</Button>
          </FormItem>

           <FormItem label="变量有效范围"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('fileVariableType', {
              initialValue: config.fileVariableType+"",
            })(
                <Select  style={{ width: 300 }} >
                <Option value="0">在JVM中有效</Option>
                <Option value="1">在前作业有效</Option>
                <Option value="2">在父作业有效</Option>
                <Option value="3">在根作业有效</Option>
              </Select>
            )}
          </FormItem>
	         <FormItem label="变量替换"  {...formItemLayout} style={{marginBottom:"8px"}}>
	            {getFieldDecorator('replaceVars', {
	              initialValue: config.replaceVars,
	            })(
	               <Checkbox />
	            )}
	          </FormItem>
             <Row style={{margin:"5px 0",width:"100%"}}  >
                  <Col  span={12}>
                    <Button size={"small"}  onClick={this.handleAdd.bind(this)}>添加变量</Button>
                  </Col>
                  <Col span={12}>
                  <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields.bind(this)} >删除变量</Button>
                  </Col>
              </Row>
             <EditTable columns={this.columns} rowSelection={true} dataSource = {this.state.dataSource} size={"small"} tableStyle="editTableStyle5" ref="editTable"  count={6}/>

        </Form>
      </Modal>
    );
  }
}
const VariablesForm = Form.create()(SET_VARIABLES);
export default connect()(VariablesForm);
