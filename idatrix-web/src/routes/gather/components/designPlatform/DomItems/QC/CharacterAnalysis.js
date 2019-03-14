/**
 * Created by Steven Leo on 2018.10/09.
 */
import React from "react";
import { connect } from 'dva';
import { Form,Select,Input,Row,Col,Tag  } from 'antd';
import PluginComponent from "./HOC/PluginComponent"

const FormItem = Form.Item;

class CertificatesAnalysis extends React.Component {

  constructor(props){
    super(props);
    this.state = {
        standardKey: -1,
        dictionaryList: [],
        selectedValues: []
    }
  }

  handleCreate = (cb) => {

    const form = this.props.form;
    const { panel,transname,description,text,config } = this.props.model;
    const { standardKey } = config;
    const { data} = this.props;

    form.validateFields((err, values) => {

      if (err) {
        return;
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        nodeName: values.text,
        fieldNames: [values.fieldNames],
        standardKey: this.state.standardKey !== -1 ? this.state.standardKey : standardKey ,
      };

      cb(obj);

    });
  };

  /**
   * 更新树
   */
  changeKey = (value)=>{

    const {GetDic} = this.props.model;
    GetDic({id: value, all: true },(result)=>{
        this.setState({
            standardKey: value,
            dictionaryList: result.list
        });
    });
  }

  componentDidMount(){
    const { config} = this.props.model;
    const { standardKey } = config;

    if(standardKey){
        this.changeKey(standardKey);
    }
  }

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    const { ifNoName , data} = this.props;

    const formItemLayout1 = {
      labelCol: { span: 4 },
      wrapperCol: { span: 18 },
    };

    
    // 获取后台配置
    const [name] = config.fieldNames
    const {standardKey} = config

    // 获取dictionaryList
    console.log(data.dictionaryList, this.state.dictionaryList);
    return (
        <Form >
            <FormItem label="步骤自定义"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
                {getFieldDecorator('text', {
                initialValue: text,
                rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                    {validator: handleCheckName,message: '步骤名称已存在，请更改!' }
                ]
                })(
                <Input />
                )}
            </FormItem>
            {   
                data.InputData.length > 0 &&
                <FormItem label="要检验的字段"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
                    {getFieldDecorator('fieldNames', {
                    initialValue: name,
                    rules: [
                        {whitespace:true, required: true, message: '请选择检测的字段' }                        
                    ]
                    })(
                        <Select>
                            {
                                data.InputData.map((index)=>
                                    <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                                )
                            }
                        </Select>
                    )}
                </FormItem>
            }
            {!(data.InputData.length > 0) && (
            <FormItem
                label="要检验的字段"
                style={{ marginBottom: "8px" }}
                {...formItemLayout1}
            >
                {getFieldDecorator("nodata", {
                initialValue: "nodata",
                rules: [
                    { whitespace: true, required: true, message: "请输入步骤名称" },
                    {validator: ifNoName,message: '需要选择一个字段' }
                ]
                })(
                <Select disabled={true}>
                    <Select.Option key="nodata" value="nodata">
                    前置步骤无可选字段
                    </Select.Option>
                </Select>
                )}
            </FormItem>
            )}
            {
                data.dictionaryList.length > 0 &&
                <FormItem label="选择标准值"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
                    {getFieldDecorator('standardKey', {
                        initialValue: standardKey ? standardKey : "",
                        rules: [
                            { required: true, message: '请选择检测的字段' }                        
                        ]
                    })(
                        <Select 
                            onChange={this.changeKey}
                        >
                            {
                                data.dictionaryList.map((val)=>(
                                    <Select.Option key={val.id} value={val.id}>
                                        {val.dictName}
                                    </Select.Option>
                                ))
                            }
                        </Select>
                    )}
                </FormItem>
            }
            {
                this.state.dictionaryList.length > 0 &&
                <Row>
                    <Col span={18} offset={4}>
                            {
                                this.state.dictionaryList.map((val)=>{
                                    return (
                                        <Row key={val.id} style={{lineHeight:"36px"}}>
                                            <Col span={3} >
                                                {val.stdVal1}
                                            </Col>
                                            <Col span={1}>：</Col>
                                            <Col span={16}>
                                                {val.references.map(v =>{
                                                        return (<Tag>{v}</Tag>)
                                                    }
                                                )}
                                            </Col>
                                        </Row>

                                    )
                                })
                            }
                    </Col>
                </Row>
            }
        </Form>
    );
  }
}
const CertificatesAnalysisForm = Form.create()(PluginComponent(CertificatesAnalysis,{width:960,dictionaryId: -1}));

export default connect()(CertificatesAnalysisForm);
