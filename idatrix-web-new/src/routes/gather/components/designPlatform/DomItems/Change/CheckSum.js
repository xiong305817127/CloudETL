/**
 * Created by Administrator on 2017/6/20.
 */
import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';

class CheckSumDialog extends React.Component {

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
            "fieldName":index
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
    const { fieldName } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "fieldName"];
          let args1  = formatTable(this.refs.editTable.state.dataSource,args);
          for(let index of args1){
            sendFields.push(index["fieldName"]);
          }
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
      "fieldName":""
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };


  /*文件表格*/
  columns =  [{
    title: '字段名称',
    dataIndex: 'fieldName',
    width:"92%",
    key: 'fieldName',
    selectable:true
  }];

  handleFocus(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "fieldName": index.name
      });
      count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  }



  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };


    const setDisabled = ()=>{

      if(getFieldValue("checksumtype") === undefined){

        if(config.checksumtype === "CRC32" || config.checksumtype === "ADLER32"){
            console.log(config.checksumtype);
            return  true;
        }else{
            return  false;
        }
      }else{
        if(getFieldValue("checksumtype")){
          if(getFieldValue("checksumtype") === "CRC32" || getFieldValue("checksumtype") === "ADLER32"){
            return  true;
          }else{
            return  false;
          }
        }
      }
    }

    return (

      <Modal
        visible={visible}
        title="增加校验列"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={500}
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
          <FormItem label="类型"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('checksumtype', {
              initialValue:config.checksumtype
            })(
              <Select>
                 <Option   value="CRC32">CRC 32</Option>
                 <Option   value="ADLER32">ADLER 32</Option>
                 <Option   value="MD5">MD5</Option>
                 <Option   value="SHA-1">SHA-1</Option>
                 <Option   value="SHA-256">SHA-256</Option>
              </Select>
            )}
          </FormItem>
          <FormItem label="结果类型"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('resultType', {
              initialValue:config.resultType+""
            })(
              <Select  disabled={setDisabled()}>
                <Select.Option   value="0">String</Select.Option>
                <Select.Option   value="1">Hexadecimal</Select.Option>
                <Select.Option   value="2">Binary</Select.Option>
              </Select>
            )}
          </FormItem>
          <FormItem label="结果字段"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('resultfieldName', {
              initialValue:config.resultfieldName
            })(
              <Input />
            )}
          </FormItem>
          <FormItem   style={{marginBottom:"8px",marginLeft:"22%"}}  {...formItemLayout1}>
            {getFieldDecorator('compatibilityMode', {
              valuePropName: 'checked',
              initialValue:config.compatibilityMode
            })(
              <Checkbox>兼容模式</Checkbox>
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>校验使用的字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.handleFocus.bind(this)}>获取字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}    tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const CheckSum = Form.create()(CheckSumDialog);

export default connect()(CheckSum);
