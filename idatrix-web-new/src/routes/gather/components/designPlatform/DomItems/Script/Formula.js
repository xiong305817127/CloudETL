import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'
import { selectType } from '../../../../constant';
import FormulaFucModal from '../Model/FormulaModel';

class Formula extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {formulas} = props.model.config;
      let data = [];
      if (formulas) {
        let count = 0;
        for (let index of formulas) {
          data.push({
            "key": count++,
            ...index
          });
        }
      }
      this.state = {
        dataSource:data,
        InputData:[],
        fucProps:{
          visible:false,
          key:"",
          value:""
        }
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
          replaceField:options,
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
    const { formulas } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [ "fieldName","formula","valueType","valueLength","valuePrecision","replaceField"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(formulas){
          sendFields = formulas
        }
      }

      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        formulas:sendFields
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
      "fieldName":null,
      "formula":null,
      "valueType":2,
      "valueLength":0,
      "valuePrecision":0,
      "replaceField":null
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
    dataIndex: 'fieldName',
    width:"20%",
    key: 'fieldName',
    editable:true
  }, {
    title: '公式',
    dataIndex: 'formula',
    width:"20%",
    className:"formula",
    key: 'formula',
    editable:true
  },{
    title: '值类型',
    dataIndex: 'valueType',
    width:"15%",
    key: 'valueType',
    selectable:true,
    selectArgs:selectType.get("numberType")
  },{
    title: '长度',
    dataIndex: 'valueLength',
    width:"8%",
    key: 'valueLength',
    editable:true
  },{
    title: '精度',
    dataIndex: 'valuePrecision',
    width:"8%",
    key: 'valuePrecision',
    editable:true
  },{
    title: '替换值',
    dataIndex: 'replaceField',
    key: 'replaceField',
    selectable:true,
  }];

  //弹框更改事件
  handleFucOk =(value)=>{
    const { fucProps } = this.state;
    fucProps.visible = false;
    let data = this.refs.editTable.state.dataSource;
    data = data.map(index=>{
      if(index.key === fucProps.key){
        index.formula = value
      }
      return index;
    })
    this.refs.editTable.updateTable(data,data.length);

    this.setState({
      fucProps
    })
  }

   handleFucCancel =()=>{
    const { fucProps } = this.state;
    fucProps.visible = false;
    this.setState({
      fucProps
    })
  }

  handleFucChange = (value,callback)=>{
      const {getDetails,transname,text,panel} = this.props.model;
      const {InputData} = this.state;
      getDetails({
          transName:transname,
          stepName:text,
          detailType:panel,
          detailParam:{
            flag:"evaluator",inputFields:InputData.map(index=>index.name),
            expression:value
          }
        },data=>{
            callback(data);
        })
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckName } = this.props.model;
    const { fucProps } = this.state;

    console.log(fucProps,"值");

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="公式"
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
            <EditTable  
              onRow={(record,key,name) => {
                return {
                  onDoubleClick: (e) => {
                      if(e.target.parentNode.parentNode.parentNode.className === "formula"){
                          const { fucProps } = this.state;
                          fucProps.visible = true;
                          fucProps.key = key;
                          fucProps.value = e.target.value?e.target.value:"";
                          this.setState({
                             fucProps
                          })
                      }
                  }     
                };
              }}
              columns={this.columns} ref="editTable" scroll={{y: 300}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
        <FormulaFucModal 
            onOk={this.handleFucOk.bind(this)} 
            onChange={this.handleFucChange.bind(this)} 
            onCancel={this.handleFucCancel.bind(this)} {...fucProps} />
      </Modal>
    );
  }
}

export default connect()(Form.create()(Formula));
