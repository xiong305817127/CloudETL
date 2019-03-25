import React from "react";
import { connect } from 'dva';
import { Form,Button,Input,Row,Col,Select } from 'antd';
import Modal from "components/Modal.js";
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

const FormItem = Form.Item;
const ButtonGroup = Button.Group;

class DesensitizationDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const { desensitizations } = props.model.config;
      let data = [];
      if ( desensitizations ) {
        let count = 0;
        for (let index of desensitizations) {
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
  };

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
          fieldInStream:options
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
    const { desensitizations } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = ["fieldInStream","fieldOutStream","ruleType","startPositon","length","replacement","ignoreSpace"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(desensitizations){
          sendFields = desensitizations
        }
      }
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        desensitizations:sendFields
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
			"fieldInStream": "",
			"fieldOutStream": "",
			"ruleType": "mask",
			"startPositon": 0,
			"length": 0,
			"replacement": "*",
			"ignoreSpace": "true"
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
    title: '输入流字段',
    dataIndex: 'fieldInStream',
    width:"18%",
    key: 'fieldInStream',
    selectable:true
  }, {
    title: '输出流字段',
    dataIndex: 'fieldOutStream',
    width:"18%",
    key: 'fieldOutStream',
    editable:true
  }, {
    title: '规则',
    dataIndex: 'ruleType',
    width:"10%",
    key: 'ruleType',
    selectable:true,
    selectArgs:[<Select.Option key="mask" value="mask">掩码</Select.Option>,
    <Select.Option key="truncation" value="truncation">截断</Select.Option>,
  ]
  } ,{
    title: '开始位置',
    dataIndex: 'startPositon',
    width:"10%",
    key: 'startPositon',
    editable:true
  },{
    title: '长度',
    dataIndex: 'length',
    width:"10%",
    key: 'length',
    editable:true
  },{
    title: '掩码字符',
    dataIndex: 'replacement',
    width:"18%",
		key: 'replacement',
		editable:true
  } ,{
    title: '忽略空格',
    dataIndex: 'ignoreSpace',
    key: 'ignoreSpace',
		selectable:true,
		selectArgs:selectType.get("T/F")
  }];

  getFields(){
    const { InputData } = this.state;
    let args = [];
    let count = 0;
    for(let index of InputData){
      args.push({
        "key":count,
        "fieldInStream": index.name,
				"fieldOutStream": "",
				"ruleType": "mask",
				"startPositon": 0,
				"length": 0,
				"replacement": "*",
				"ignoreSpace": "true"
      });
      count++;
    }
    this.refs.editTable.updateTable(args,count);
  }

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
        title="数据脱敏"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={900}
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
                <p style={{marginLeft:"5px"}}>要处理的字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.getFields.bind(this)}>获取字段</Button>
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
const Desensitization = Form.create()(DesensitizationDialog);

export default connect()(Desensitization);
