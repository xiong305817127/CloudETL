import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class AddXMLDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { outputFields } = props.model.config;
      let data = [];
      let count = 0;
      if(outputFields){
        for(let index of outputFields){
          data.push({
            key:count,
            ...index
          })
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

  initFuc(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      fieldname:options
    });
  };

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
  };

  handleCreate = () => {
    const {panel,description,transname,key,saveStep,text,config,formatTable} = this.props.model;
    const { outputFields } = config;
    const form = this.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){

        if(this.refs.editTable.state.dataSource.length>0){
          let arg = ["fieldname", "elementname", "type", "format", "currencysymbol", "decimalsymbol", "groupingsymbol", "nullstring","length", "precision", "attribute", "attributeparentname"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
        }
      }else{
        if(outputFields){
          sendFields = outputFields;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "outputFields": sendFields,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  }

  handleAdd = ()=>{
    const data = {
      "fieldname": null,
      "elementname": null,
      "type": "",
      "format": null,
      "currencysymbol": null,
      "decimalsymbol": null,
      "groupingsymbol": null,
      "nullstring": null,
      "length": "",
      "precision": "",
      "attribute": "",
      "attributeparentname": null
    };
    this.refs.editTable.handleAdd(data);
  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  formItemLayout5 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 14 },
  };


  /*表格1*/
  Columns = [
    {
    title: '字段名',
    dataIndex: 'fieldname',
    key: 'fieldname',
    width:"10%",
    selectable:true
  },{
    title: '元素名',
    dataIndex: 'elementname',
    key: 'elementname',
    width:"10%",
    editable:true
  } ,{
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width:"9%",
    selectable:true,
    selectArgs:selectType.get("numberType")
  },{
    title: '格式',
    dataIndex: 'format',
    key: 'format',
    width:"16%",
    selectable:true,
    selectArgs:selectType.get("date")
  }, {
    title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"6%",
    editable:true
  }, {
    title: '精度',
    dataIndex: 'precision',
    key: 'precision',
    width:"6%",
    editable:true,
  },{
    title: '货币',
    dataIndex: 'currencysymbol',
    key: 'currencysymbol',
    width:"6%",
    editable:true,
  },{
    title: '小数',
    dataIndex: 'decimalsymbol',
    key: 'decimalsymbol',
    width:"6%",
    editable:true,
  },{
    title: '数字分组符号',
    dataIndex: 'groupingsymbol',
    key: 'groupingsymbol',
    width:"6%",
    editable:true,
  },{
    title: '空',
    dataIndex: 'nullstring',
    key: 'nullstring',
    width:"6%",
    editable:true
  },{
    title: '属性',
    dataIndex: 'attribute',
    key: 'attribute',
    width:"6%",
    selectable:true,
    selectArgs:selectType.get("T/F")
  },{
    title: '父属性名',
    dataIndex: 'attributeparentname',
    key: 'attributeparentname',
    editable:true
  }
  ];

  getValue(value){
      let args = new Map([
            ["Number","1"],
            ["String","2"],
            ["Date","3"],
            ["Boolean","4"],
            ["Integer","5"],
            ["BigNumber","6"],
            ["Binary","6"],
            ["BigNumber","8"],
            ["Timestamp","9"],
            ["Internet Address","10"]
        ]);
      if(args.has(value)){
         return args.get(value);
      };
  }

  handleFocus(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;

    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "fieldname": index.name,
        "elementname": null,
        "type": index.type == 0?"":this.getValue(index.type),
        "format": null,
        "currencysymbol": index.currencysymbol ,
        "decimalsymbol": index.decimalsymbol ,
        "groupingsymbol": index.groupingsymbol ,
        "nullstring": null,
        "length":index.length == 0?"":index.length,
        "precision": index.precision == 0?"":index.precision,
        "attribute": "",
        "attributeparentname": null
      });
      count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  }


  render() {
    const { getFieldDecorator} = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };



    return (
      <Modal
        visible={visible}
        title="XML生成器"
        wrapClassName="vertical-center-modal"
        width={750}
        maskClosable={false}
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="步骤名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }],
            })(
              <Input />
            )}
          </FormItem>
          <Tabs  style={{margin:"20px 8% 0  8%"}}  type="card">
            <TabPane tab="内容" key="1">
              <FormItem label="编码"   style={{marginBottom:"8px"}} {...formItemLayout}>
                {getFieldDecorator('encoding', {
                  initialValue:config.encoding?config.encoding:"GBK"
                })(
                  <Select>
                    <Select.Option value="GBK">GBK</Select.Option>
                    <Select.Option value="ISO-8859-1">ISO-8859-1</Select.Option>
                    <Select.Option value="GB2312">GB2312</Select.Option>
                    <Select.Option value="UTF-8">UTF-8</Select.Option>
                    <Select.Option value="Big5">Big5</Select.Option>
                  </Select>
                )}
              </FormItem>
              <FormItem label="输出值"   style={{marginBottom:"8px"}} {...formItemLayout}>
                {getFieldDecorator('valueName', {
                  initialValue:config.valueName
                })(
                  <Input />
                )}
              </FormItem>
              <FormItem label="根 XML 元素"   style={{marginBottom:"8px"}} {...formItemLayout}>
                {getFieldDecorator('rootNode', {
                  initialValue:config.rootNode
                })(
                  <Input />
                )}
              </FormItem>

              <Row style={{marginLeft:"10%"}}>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"10px",marginLeft:"10%"}} {...this.formItemLayout}>
                    {getFieldDecorator('omitXMLheader', {
                      valuePropName: 'checked',
                      initialValue:config.omitXMLheader
                    })(
                      <Checkbox >忽略XML头部</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"10px",marginLeft:"10%"}} {...this.formItemLayout}>
                    {getFieldDecorator('omitNullValues', {
                      valuePropName: 'checked',
                      initialValue:config.omitNullValues
                    })(
                      <Checkbox >忽略XML中的null值</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
            </TabPane>
            <TabPane tab="字段" key="2">
              <Row style={{margin:"5px 0",width:"100%"}}  >

                <Col span={12}>
                  <ButtonGroup size={"small"} >
                    <Button    onClick={this.handleAdd}>添加字段</Button>
                    <Button    onClick={this.handleFocus.bind(this)} >获取字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12}  style={{textAlign:"right"}}>
                  <Button  size={"small"}   onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                </Col>
              </Row>
              <EditTable  initFuc={this.initFuc.bind(this)}  rowSelection={true} columns={this.Columns} dataSource = {this.state.dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 300,x:1600}} ref="editTable"   count={4}/>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    );
  }
}
const AddXML = Form.create()(AddXMLDialog);

export default connect()(AddXML);
