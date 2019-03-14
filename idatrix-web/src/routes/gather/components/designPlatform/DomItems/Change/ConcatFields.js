import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class ConcatDialog extends React.Component {


  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { fields } = props.model.config;
      let data = [];
      let count = 0;
      if(fields){
        for(let index of fields){
          data.push({
            key:count,
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
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      });
      if(this.refs.editTable){
        let options = getInputSelect(data,"name");
        this.refs.editTable.updateOptions({
          name:options
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
    const {panel,description,transname,key,saveStep,text,config,formatTable} = this.props.model;
    const { fields } = config;
    const form = this.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){

        if(this.refs.editTable.state.dataSource.length>0){
          let arg = ["name", "type", "format", "currencyType", "decimal", "group", "nullif", "trimType","length","precision"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
        }
      }else{
        if(fields){
          sendFields = fields;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "fields": sendFields,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  };

  handleAdd = ()=>{
    const data = {
      "name": null,
      "type": null,
      "format": null,
      "currencyType": null,
      "decimal": null,
      "group": null,
      "nullif": null,
      "trimType": null,
      "length": 0,
      "precision": 0
    };
    this.refs.editTable.handleAdd(data);
  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  handleFocus(){
      const { InputData } = this.state;
      let tabel1 = [];
      let count1 = 0;

      for(let index of InputData){
        tabel1.push({
          "key":count1,
          "name": index.name,
          "type": index.type,
          "format": null,
          "currencyType": index.currencySymbol,
          "decimal": index.decimalSymbol,
          "group": index.groupingSymbol,
          "nullif": null,
          "trimType": index.trimType,
          "length": index.length,
          "precision": index.precision
        });
        count1++;
      }
    this.refs.editTable.updateTable(tabel1,count1);
  }

  /*插入分隔符*/
  insertTab(){
    const { setFieldsValue,getFieldValue } = this.props.form;
    let str = getFieldValue("separator");

    str = "\t" + str;

    setFieldsValue({
      separator:str
    });
  }

  formItemLayout5 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 14 },
  };


  /*表格1*/
  Columns = [
    {
    title: '目标字段',
    dataIndex: 'name',
    key: 'name',
    width:"10%",
    selectable:true
  },{
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width:"12%",
    selectable:true,
    selectArgs:selectType.get("type")
  } ,{
    title: '格式化',
    dataIndex: 'format',
    key: 'format',
    selectable:true,
    selectArgs:selectType.get("date")
  },{
    title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"8%",
    editable:true,
  }, {
    title: '精度',
    dataIndex: 'precision',
    key: 'precision',
    width:"8%",
    editable:true,
  }, {
    title: '货币类型',
    dataIndex: 'currencyType',
    key: 'currencyType',
    width:"8%",
    editable:true,
  },{
    title: '小数',
    dataIndex: 'decimal',
    key: 'decimal',
    width:"8%",
    editable:true,
  },{
    title: '分组',
    dataIndex: 'group',
    key: 'group',
    width:"8%",
    editable:true,
  },{
    title: '空格类型',
    dataIndex: 'trimType',
    key: 'trimType',
    width:"12%",
    selectable:true,
    selectArgs:selectType.get("trimType")

  },{
    title: 'Null',
    dataIndex: 'nullif',
    key: 'nullif',
    width:"8%",
    editable:true
  }
  ];


  render() {
    const { getFieldDecorator} = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };



    return (
      <Modal
        visible={visible}
        title="连接字段"
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
          <FormItem label="目标字段名"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('targetFieldName', {
              initialValue: config.targetFieldName,
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="目标字段长度"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('targetFieldLength', {
              initialValue: config.targetFieldLength,
            })(
              <Input />
            )}
          </FormItem>

          <FormItem label="分隔符"   style={{marginBottom:"8px"}} {...formItemLayout1}>
            <Row>
              <Col span={16}>
            {getFieldDecorator('separator', {
              initialValue:config.separator
            })(
              <Input  />
            )}
              </Col>
              <Col span={8}>
                <Button  onClick = {this.insertTab.bind(this)}>插入TAB</Button>
              </Col>
            </Row>
          </FormItem>
          <FormItem label="封闭符"   style={{marginBottom:"8px"}} {...formItemLayout}>
            {getFieldDecorator('enclosure', {
              initialValue:config.enclosure
            })(
              <Input  />
            )}
          </FormItem>

          <Tabs  style={{margin:"20px 8% 0  8%"}}  type="card">
            <TabPane tab="主选项" key="1">
              <Row style={{margin:"5px 0",width:"100%"}}  >
                <Col span={12}>
                  <ButtonGroup size={"small"} >
                    <Button    onClick={this.handleAdd}>添加字段</Button>
                    <Button  size={"small"} onClick={this.handleFocus.bind(this)} >获取字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12}  style={{textAlign:"right"}}>
                  <Button  size={"small"}   onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                </Col>
              </Row>
              <EditTable  rowSelection={true} columns={this.Columns} dataSource = {this.state.dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 240,x:1100}} ref="editTable"   count={4}/>
            </TabPane>
            <TabPane tab="数据库字段" key="2">
              <Row style={{marginLeft:"10%"}}>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"10px"}} {...this.formItemLayout}>
                    {getFieldDecorator('removeSelectedFields', {
                      valuePropName: 'checked',
                      initialValue:config.removeSelectedFields
                    })(
                      <Checkbox >移除选中字段</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row  style={{marginLeft:"10%"}}>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                    {getFieldDecorator('enclosureForced', {
                      valuePropName: 'checked',
                      initialValue:config.enclosureForced
                    })(
                      <Checkbox >强制在字段周围加封闭符？</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                    {getFieldDecorator('enclosureFixDisabled', {
                      valuePropName: 'checked',
                      initialValue:config.enclosureFixDisabled
                    })(
                      <Checkbox >禁用封闭符修复？</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row style={{marginLeft:"10%"}}>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                    {getFieldDecorator('header', {
                      valuePropName: 'checked',
                      initialValue:config.header
                    })(
                      <Checkbox >头部？</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                    {getFieldDecorator('footer', {
                      valuePropName: 'checked',
                      initialValue:config.footer
                    })(
                      <Checkbox >尾部？</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>
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
              <Row style={{marginLeft:"10%"}}>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                    {getFieldDecorator('pad', {
                      valuePropName: 'checked',
                      initialValue:config.pad
                    })(
                      <Checkbox >字段右填充或裁剪？</Checkbox>
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                    {getFieldDecorator("fastDump", {
                      valuePropName: 'checked',
                      initialValue:config.fastDump
                    })(
                      <Checkbox >快速数据存储(无格式)？</Checkbox>
                    )}
                  </FormItem>
                </Col>
              </Row>

              <FormItem label="分拆...每一行"   style={{marginBottom:"8px"}} {...formItemLayout}>
                {getFieldDecorator('splitevery', {
                  initialValue:config.splitevery
                })(
                  <Input />
                )}
              </FormItem>
              <FormItem label="添加文件结束行"   style={{marginBottom:"8px"}} {...formItemLayout}>
                {getFieldDecorator('endedLine', {
                  initialValue:config.endedLine
                })(
                  <Input />
                )}
              </FormItem>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    );
  }
}
const ConcatFields = Form.create()(ConcatDialog);

export default connect()(ConcatFields);
