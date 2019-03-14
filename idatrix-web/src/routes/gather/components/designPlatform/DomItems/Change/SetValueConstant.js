import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class SetValueDialog extends React.Component {

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
          let args = [  "fieldName", "replaceValue", "replaceMask", "setEmptyString"];
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
      "fieldName": "",
      "replaceValue": "",
      "replaceMask": "",
      "setEmptyString": false
    };
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*文件表格*/
  columns =  [{
    title: '字段',
    dataIndex: 'fieldName',
    width:"20%",
    key: 'fieldName',
    selectable:true
  }, {
    title: '值替换',
    dataIndex: 'replaceValue',
    width:"20%",
    key: 'replaceValue',
    editable:true
  }, {
    title: '转换掩码(对日期类型)',
    dataIndex: 'replaceMask',
    key: 'replaceMask',
    selectable:true,
    width:"40%",
    selectArgs:[
      <Select.Option key="0" value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Select.Option>,
      <Select.Option key="1" value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Select.Option>,
      <Select.Option key="2" value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Select.Option>,
      <Select.Option key="3" value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Select.Option>,
      <Select.Option key="4" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Select.Option>,
      <Select.Option key="5" value="yyyy/MM/dd">yyyy/MM/dd</Select.Option>,
      <Select.Option key="6" value="yyyy-MM-dd">yyyy-MM-dd</Select.Option>,
      <Select.Option key="7" value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Select.Option>,
      <Select.Option key="8" value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Select.Option>,
      <Select.Option key="9" value="yyyyMMdd">yyyyMMdd</Select.Option>,
      <Select.Option key="10" value="MM/dd/yyyy">MM/dd/yyyy</Select.Option>,
      <Select.Option key="11" value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Select.Option>,
      <Select.Option key="12" value="MM-dd-yyyy">MM-dd-yyyy</Select.Option>,
      <Select.Option key="13" value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Select.Option>,
      <Select.Option key="14" value="MM/dd/yy">MM/dd/yy</Select.Option>,
      <Select.Option key="15" value="MM-dd-yy">MM-dd-yy</Select.Option>,
      <Select.Option key="16" value="dd/MM/yyyy">dd/MM/yyyy</Select.Option>,
      <Select.Option key="17" value="dd-MM-yyyy">dd-MM-yyyy</Select.Option>,
      <Select.Option key="18" value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Select.Option>,
    ]
  } ,{
    title: '设为空串？',
    dataIndex: 'setEmptyString',
    key: 'setEmptyString',
    selectable:true,
    selectArgs:selectType.get("T/F")
  }];

  getFields(){
    const {InputData} = this.state;
    let args = [];
    let count =0;
    for(let index of InputData){
      args.push({
        "key":count,
        "fieldName": index.name,
        "replaceValue": null,
        "replaceMask": null,
        "setEmptyString": false
      });
      count++;
    }
    this.refs.editTable.updateTable(args,count);
  }


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
        title="将字段设置为常量"
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
          <FormItem  style={{marginBottom:"8px",marginLeft:"30%"}}  {...formItemLayout1}>
            {getFieldDecorator('usevar', {
              valuePropName: 'checked',
              initialValue:config.usevar
            })(
              <Checkbox >在常量中使用变量</Checkbox>
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>要剪切的字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button  onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button  onClick={this.getFields.bind(this)}>获取字段</Button>
                  <Button  onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const SetValueConstant = Form.create()(SetValueDialog);

export default connect()(SetValueConstant);
