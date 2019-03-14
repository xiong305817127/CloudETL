import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'
import { selectType } from '../../../../constant';
class RowGenerator extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    this.state={
       visibleS:false,
    };
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
        InputData:[],
        configData:[]
      }
    }
  }

   /*文件表格*/
  columns =  [{
    title: '名称',
    dataIndex: 'fieldName',
    width:"10%",
    key: 'fieldName',
    editable:true
  }, {
    title: '类型',
    dataIndex: 'fieldType',
    width:"10%",
    key: 'fieldType',
    selectable:true,
    selectArgs:selectType.get("type")
  }, {
    title: '格式',
    dataIndex: 'fieldFormat',
    width:"10%",
    key: 'fieldFormat',
    editable:true
  }, {
    title: '长度',
    dataIndex: 'fieldLength',
    width:"10%",
    key: 'fieldLength',
    editable:true
  }, {
    title: '精度',
    dataIndex: 'fieldPrecision',
    width:"10%",
    key: 'fieldPrecision',
    editable:true
  }, {
    title: '货币类型',
    dataIndex: 'currency',
    width:"10%",
    key: 'currency',
    editable:true
  }, {
    title: '小数',
    dataIndex: 'decimal',
    width:"10%",
    key: 'decimal',
    editable:true
  }, {
    title: '分组',
    dataIndex: 'group',
    width:"10%",
    key: 'group',
    editable:true
  }, {
    title: '值',
    dataIndex: 'value',
    width:"10%",
    key: 'value',
    editable:true
  }, {
    title: '设为空串？',
    dataIndex: 'setEmptyString',
    width:"10%",
    key: 'setEmptyString',
    selectable:true,
    selectArgs:selectType.get("T/F")
  }];

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
    	console.log(values,"...values");
      if (err) {
        return;
      }

        let sendFields = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["fieldName","fieldType","fieldFormat","currency","decimal","group","value","fieldLength","fieldPrecision","setEmptyString"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(fieldName){
            sendFields = fieldName;
          }
      }


      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        "fieldName":sendFields
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
        "fieldLength": null,
        "fieldPrecision": null,
        "setEmptyString": null
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  onValueChange(){

  }

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    console.log(config,"config000-000000");
    
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
      }
      const setDisabled = ()=>{
        if(getFieldValue("neverEnding") === undefined){
          return config.neverEnding;
        }else{
          if(getFieldValue("neverEnding")){
            return getFieldValue("neverEnding");
          }else {
            return false;
          }
        }
      };
    return (
      <Modal
        visible={visible}
        title="生成记录"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={850}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
        onCancel = {this.hideModal}>
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

          <FormItem label="限制"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('rowLimit', {
              initialValue:config.rowLimit,
            })(
              <Input disabled={setDisabled()}/>
            )}
          </FormItem>

          <FormItem  style={{marginBottom:"8px",marginLeft:"10%"}}  {...formItemLayout1}>
                {getFieldDecorator('neverEnding', {
                  valuePropName: 'checked',
                  initialValue:config.neverEnding,
                  onChange:this.onValueChange.bind(this)
                })(
                  <Checkbox >永远不停止生成行</Checkbox>
                )}
            </FormItem>

            <FormItem label="毫秒间隔（延迟）"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
	            {getFieldDecorator('intervalInMs', {
	              initialValue:config.intervalInMs,
	            })(
	              <Input disabled={setDisabled() === false}/>
	            )}
	          </FormItem>

            <FormItem label="当前行时间字段名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
	            {getFieldDecorator('rowTimeField', {
	              initialValue:config.rowTimeField,
	            })(
	              <Input disabled={setDisabled() === false}/>
	            )}
	          </FormItem>

	          <FormItem label="上一行时间字段名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
	            {getFieldDecorator('lastTimeField', {
	              initialValue:config.lastTimeField,
	            })(
	              <Input disabled={setDisabled() === false}/>
	            )}
	          </FormItem>

	          <div style={{margin:"10px 5% 0"}}>
		          <Row style={{marginBottom:"5px"}}>
		            <Col span={12}>
		              <p style={{marginLeft:"5px"}}>字段值：</p>
		            </Col>
		            <Col span={12}>
		              <ButtonGroup size={"small"} style={{float:"right"}} >
		                <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
		                <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
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
const RowGeneratorList = Form.create()(RowGenerator);

export default connect()(RowGeneratorList);
