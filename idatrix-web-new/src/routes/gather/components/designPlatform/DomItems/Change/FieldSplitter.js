import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class SplitterDialog extends React.Component {

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
        dataSource:data,
        InputData:[]
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      })
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
    const { fieldName } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "fieldName", "fieldID", "fieldRemoveID", "fieldType", "fieldFormat", "fieldGroup", "fieldDecimal", "fieldCurrency", "fieldLength", "fieldPrecision", "fieldNullIf", "fieldIfNull", "trimType"];
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
  };

  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldName": null,
      "fieldID": null,
      "fieldRemoveID": false,
      "fieldType": "",
      "fieldFormat": null,
      "fieldGroup": null,
      "fieldDecimal": null,
      "fieldCurrency": null,
      "fieldLength": "",
      "fieldPrecision": "",
      "fieldNullIf": null,
      "fieldIfNull": null,
      "trimType": "none"
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*文件表格*/
  columns =  [
    {
    title: '新的字段',
    dataIndex: 'fieldName',
    width:"8%",
    key: 'fieldName',
    editable:true
  }, {
    title: 'ID',
    dataIndex: 'fieldID',
    width:"8%",
    key: 'fieldID',
    editable:true
  }, {
    title: '移除ID',
    dataIndex: 'fieldRemoveID',
    width:"6%",
    key: 'fieldRemoveID',
    selectable:true,
    selectArgs:selectType.get("T/F")
  } ,{
    title: '类型',
    dataIndex: 'fieldType',
    width:"10%",
    key: 'fieldType',
    selectable:true,
    selectArgs:selectType.get("numberType")
  },{
    title: '长度',
    dataIndex: 'fieldLength',
    width:"7%",
    key: 'fieldLength',
    editable:true
  },{
    title: '精度',
    dataIndex: 'fieldPrecision',
    width:"7%",
    key: 'fieldPrecision',
    editable:true
  },{
    title: '格式',
    dataIndex: 'fieldFormat',
    width:"7%",
    key: 'fieldFormat',
    editable:true
  },{
    title: '分组符号',
    dataIndex: 'fieldGroup',
    width:"7%",
    key: 'fieldGroup',
    editable:true
  },{
    title: '小数点符号',
    dataIndex: 'fieldDecimal',
    width:"7%",
    key: 'fieldDecimal',
    editable:true
  },{
    title: '货币符号',
    dataIndex: 'fieldCurrency',
    width:"7%",
    key: 'fieldCurrency',
    editable:true
  },{
    title: 'Nullif',
    dataIndex: 'fieldNullIf',
    width:"7%",
    key: 'fieldNullIf',
    editable:true
  },{
    title: '缺省',
    dataIndex: 'fieldIfNull',
    width:"7%",
    key: 'fieldIfNull',
    editable:true
  },{
    title: '去除空格类型',
    dataIndex: 'trimType',
    key: 'trimType',
    selectable:true,
    selectArgs:selectType.get("trimType")
  }];

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span:6 },
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="拆分字段"
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
          <FormItem label="步骤名称"    {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="需要拆分的字段"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
            {getFieldDecorator('splitField', {
              initialValue: config.splitField
            })(
              <Select>
                {
                  this.state.InputData.map((index)=>
                    <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <FormItem label="分隔符"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('delimiter', {
              initialValue: config.delimiter
            })(
              <Input />
            )}
          </FormItem>

          <FormItem label="Enclosure"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('enclosure', {
              initialValue: config.enclosure
            })(
              <Input />
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>字段</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300,x:1500}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const FieldSplitter = Form.create()(SplitterDialog);

export default connect()(FieldSplitter);
