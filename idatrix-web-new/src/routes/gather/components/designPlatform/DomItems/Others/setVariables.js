import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class SetDialog extends React.Component {

  constructor(props){
    super(props);

    const { visible } = props.model;
    if(visible === true) {
      const {fields} = props.model.config;
      let data = [];
      if (fields) {
        let count = 0;
        for (let index of fields) {
          data.push({
            "key": count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        InputData:[]
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,getInputSelect } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      });
      if(this.refs.editTable){
        let options = getInputSelect(data,"name");
        this.refs.editTable.updateOptions({
          fieldName:options
        });
      }
    })
  };

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = () => {

    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
    const { fields } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [  "fieldName", "variableName", "variableType", "defaultValue"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(fields){
          sendFields = fields
        }
      }
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        fields:sendFields,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  };


  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldName": "",
      "variableName":"",
      "variableType":"",
      "defaultValue":""
    };
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*文件表格*/
  columns =  [
    {
    title: '字段名称',
    dataIndex: 'fieldName',
    width:"25%",
    key: 'fieldName',
    selectable:true
  }, {
    title: '变量名',
    dataIndex: 'variableName',
    width:"20%",
    key: 'variableName',
    editable:true
  }, {
    title: '变量活动类型',
    dataIndex: 'variableType',
    width:"25%",
    key: 'variableType',
    selectable:true,
    selectArgs:[
      <Select.Option key="JVM" value="JVM">在Java虚拟机中有效</Select.Option>,
      <Select.Option key="PARENT_JOB" value="PARENT_JOB">在父作业中有效</Select.Option>,
      <Select.Option key="GP_JOB" value="GP_JOB">在祖父作业中有效</Select.Option>,
      <Select.Option key="ROOT_JOB" value="ROOT_JOB">在根作业中有效</Select.Option>
    ]
  } ,{
    title: '默认值',
    dataIndex: 'defaultValue',
    key: 'defaultValue',
    editable:true
  }];

  handleFocus(){
    const { InputData } = this.props.model;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "fieldName": index.name,
        "variableName":"",
        "variableType":"",
        "defaultValue":""
      });
      count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckName,config } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="设置环境变量"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={750}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                ]}
        onCancel = {this.hideModal}
      >
        <Form >
          <FormItem label="步骤名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label=""   style={{marginBottom:"8px",marginLeft:"30%"}}  {...formItemLayout1}>
            {getFieldDecorator('useFormatting', {
              valuePropName: 'checked',
              initialValue:config.useFormatting
            })(
              <Checkbox >apply formatting</Checkbox>
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>字段值：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.handleFocus.bind(this)}>获取字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const SetVarianles = Form.create()(SetDialog);

export default connect()(SetVarianles);
