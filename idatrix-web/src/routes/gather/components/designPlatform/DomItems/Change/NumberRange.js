import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class RangeDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {rules} = props.model.config;
      let data = [];
      if (rules) {
        let count = 0;
        for (let index of rules) {
          data.push({
            "key":count,
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
    const { rules } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = ["lowerbound", "upperbound", "value"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(rules){
          sendFields = rules
        }
      }
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        rules:sendFields,
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
      "lowerbound": null,
      "upperbound": null,
      "value": null
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*文件表格*/
  columns =  [{
    title: '下限',
    dataIndex: 'lowerbound',
    key: 'lowerbound',
    width:"33%",
    editable:true
  },{
    title: '上界',
    dataIndex: 'upperbound',
    key: 'upperbound',
    width:"33%",
    editable:true
  },{
    title: '值',
    dataIndex: 'value',
    key: 'value',
    editable:true
  }];





  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckName,config } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="数值范围"
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
          <FormItem label="输入字段"  style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('inputField', {
              initialValue:config.inputField
            })(
              <Select >
                {
                  this.state.InputData.map((index)=>
                    <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <FormItem label="输出字段"  style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('outputField', {
              initialValue:config.outputField
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="缺省值" style={{marginBottom:"8px"}}   {...formItemLayout1}>
            {getFieldDecorator('fallBackValue', {
              initialValue:config.fallBackValue
            })(
              <Input />
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>范围(最小{"<= x< "}最大)：</p>
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
const NumberRange = Form.create()(RangeDialog);

export default connect()(NumberRange);
