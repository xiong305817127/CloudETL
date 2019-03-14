import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'
import { selectType } from '../../../../constant';

class CalculatorDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {calculation} = props.model.config;
      let data = [];
      if (calculation) {
        let count = 0;
        for (let index of calculation) {
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
  };

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,getInputSelect } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj,data => {
      this.setState({InputData:data });
      if(this.refs.editTable){
        let options = getInputSelect(data,"name");
        this.refs.editTable.updateOptions({
          fielda:options,
          fieldb:options,
          fieldc:options
        });
      }
    });
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
    const { calculation } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "fieldname", "calctype", "fielda", "fieldb", "fieldc", "valuetype", "valuelength", "valueprecision", "removedfromresult", "conversionmask", "decimalsymbol", "groupingsymbol", "currencysymbol"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(calculation){
          sendFields = calculation
        }
      }

      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        calculation:sendFields
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
      "fieldname": null,
      "calctype":"",
      "fielda": null,
      "fieldb": null,
      "fieldc": null,
      "valuetype": "",
      "valuelength": "",
      "valueprecision": "",
      "removedfromresult": false,
      "conversionmask": null,
      "decimalsymbol": null,
      "groupingsymbol": null,
      "currencysymbol": null
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
    title: '新字段',
    dataIndex: 'fieldname',
    width:"7%",
    key: 'fieldname',
    editable:true
  }, {
    title: '计算',
    dataIndex: 'calctype',
    width:"20%",
    key: 'calctype',
    selectable:true,
    selectArgs:selectType.get("number")
  },{
    title: '字段A',
    dataIndex: 'fielda',
    width:"7%",
    key: 'fielda',
    selectable:true
  },{
    title: '字段B',
    dataIndex: 'fieldb',
    width:"7%",
    key: 'fieldb',
    selectable:true
  },{
    title: '字段C',
    dataIndex: 'fieldc',
    width:"7%",
    key: 'fieldc',
    selectable:true
  },{
    title: '值类型',
    dataIndex: 'valuetype',
    width:"10%",
    key: 'valuetype',
    selectable:true,
    selectArgs:selectType.get("numberType")
  },{
    title: '长度',
    dataIndex: 'valuelength',
    width:"5%",
    key: 'valuelength',
    editable:true
  },{
    title: '精确度',
    dataIndex: 'valueprecision',
    width:"5%",
    key: 'valueprecision',
    editable:true
  },{
    title: '移除',
    dataIndex: 'removedfromresult',
    width:"5%",
    key: 'removedfromresult',
    selectable:true,
    selectArgs:selectType.get("T/F")
  },{
    title: 'Conversion mask',
    dataIndex: 'conversionmask',
    width:"10%",
    key: 'conversionmask',
    selectable:true
  },{
    title: '小数点符号',
    dataIndex: 'decimalsymbol',
    width:"5%",
    key: 'decimalsymbol',
    editable:true
  },{
    title: '分组符号',
    dataIndex: 'groupingsymbol',
    width:"5%",
    key: 'groupingsymbol',
    editable:true
  },{
    title: '货币符号',
    dataIndex: 'currencysymbol',
    key: 'currencysymbol',
    editable:true
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
        title="计算器"
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
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300,x:1800}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const Calculator = Form.create()(CalculatorDialog);

export default connect()(Calculator);
