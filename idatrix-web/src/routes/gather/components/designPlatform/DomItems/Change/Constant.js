import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class ConstantDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {fieldName} = props.model.config;
      let data = [];
      if (fieldName) {
        let count = 0;
        for (let index of fieldName) {
          data.push({
            "key": count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data
      }
    }
  }

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
    const { fieldName } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [  "fieldName", "fieldType", "fieldFormat", "currency", "decimal", "group", "value", "fieldLength", "fieldPrecision", "setEmptyString"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(fieldName){
          sendFields = fieldName
        }
      }
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        fieldName:sendFields,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  }

  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldName": null,
      "fieldType": null,
      "fieldFormat": null,
      "currency": null,
      "decimal": null,
      "group": null,
      "value": null,
      "fieldLength": "",
      "fieldPrecision": "",
      "setEmptyString": false
    }
    this.refs.editTable.handleAdd(data);
  }

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  }

  /*文件表格*/
  columns =   [
    {
    title: '名称',
    dataIndex: 'fieldName',
    key: 'fieldName',
    width:"8%",
    editable:true
  }, {
    title: '类型',
    dataIndex: 'fieldType',
    key: 'fieldType',
    width:"16%",
    selectable:true,
    selectArgs:selectType.get("type")
  },{
    title: '格式',
    dataIndex: 'fieldFormat',
    key: 'fieldFormat',
    width:"12%",
    editable:true,
  },{
    title: '长度',
    dataIndex: 'fieldLength',
    key: 'fieldLength',
    width:"8%",
    editable:true,
  }, {
    title: '精确',
    dataIndex: 'fieldPrecision',
    key: 'fieldPrecision',
    width:"8%",
    editable:true,
  },{
    title: '当前的',
    dataIndex: 'currency',
    key: 'currency',
    width:"8%",
    editable:true
  },{
    title: '十进制的',
    dataIndex: 'decimal',
    key: 'decimal',
    width:"9%",
    editable:true
  },{
    title: '组',
    dataIndex: 'group',
    key: 'group',
    width:"8%",
    editable:true
  },{
    title: '值',
    dataIndex: 'value',
    key: 'value',
    width:"8%",
    editable:true
  },{
    title: '设为空串?',
    dataIndex: 'setEmptyString',
    key: 'setEmptyString',
    width:"10%",
    selectable:true,
    selectArgs:selectType.get("T/F")
  }
  ];


  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="增加常量"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={950}
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
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>增加常量字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
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
const Constant = Form.create()(ConstantDialog);

export default connect()(Constant);
