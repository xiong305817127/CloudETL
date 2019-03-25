/**
 * Created by Steven Leo on 2018.10/09.
 */
import React from "react";
import { connect } from 'dva';
import { Form,Select,Checkbox,Input,Row,Col} from 'antd';
import PluginComponent from "./HOC/PluginComponent"

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;

class CertificatesAnalysis extends React.Component {

  constructor(props){
    super(props);
    this.state = {
        selectAll: false,
        indeterminate: false,
        covered: false
    }
  }

  handleSelectAll = ()=>{


    const {setFieldsValue} = this.props.form;
    const { ifNoName , data} = this.props;

    setFieldsValue({
        referenceValues: !this.state.selectAll ? data.dictionaryList.allValue : []
    });

    this.setState({
        selectAll: !this.state.selectAll,
        covered: true
    });
  }

  checkSelected = (checkedValue)=>{
    this.setState({
        indeterminate: checkedValue.length === 0 ? false : true
    })
  }

  handleCreate = (cb) => {

    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
    const { nodeName } = config;
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
        referenceValues: values.referenceValues
      };

      cb(obj);

    });
  };


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
    const {referenceValues}  = config;

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
                    "references" in data.dictionaryList &&
                    <FormItem 
                        label="选择样式"
                        style={{marginBottom: "8px"}}
                        {...formItemLayout1}
                    >
                        {getFieldDecorator("referenceValues", {
                        initialValue:  referenceValues ? referenceValues : [],
                        rules: [
                            {required: true, message: "请选择参考值" }
                        ]
                        })(
                            <CheckboxGroup 
                                style={{border:"1px solid #f0f0f0",borderRadius:"8px", padding: "16px"}}
                                onChange={this.checkSelected}
                            >
                                <Row>
                                    {  data.dictionaryList.allValue.map((val,index)=>(
                                        <Col span={8} key={index + val}>
                                            <Checkbox value={val}>
                                                {val}
                                            </Checkbox>
                                        </Col>
                                    ))}
                                </Row>
                            </CheckboxGroup>
                        )}
                    </FormItem>
                }
                {
                    "references" in data.dictionaryList &&
                    <Row>
                        <Col span={18} offset={4}>
                            <Checkbox
                                indeterminate={this.state.indeterminate}
                                onChange={this.handleSelectAll}
                                checked={this.state.selectAll}
                            >
                                全选
                            </Checkbox>
                        </Col>
                    </Row>
                }
            </Form>
    );
  }
}
const CertificatesAnalysisForm = Form.create()(PluginComponent(CertificatesAnalysis,{width:960,dictionaryId: 2}));

export default connect()(CertificatesAnalysisForm);
