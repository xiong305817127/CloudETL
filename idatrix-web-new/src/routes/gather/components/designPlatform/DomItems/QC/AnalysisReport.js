/**
 * Created by Steven Leo on 2018/10/08.
 */
import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card } from 'antd';
import PluginComponent from "./HOC/PluginComponent";

const FormItem = Form.Item;

class AnalysisiReport extends React.Component {

  constructor(props){
    super(props);
  }

  handleCreate = (cb) => {

    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
    const { nodeName } = config;
    const { hideModal } = this.props;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      if(nodeName === values.node_name && text === values.text){
          hideModal();
          return
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        nodeName: values.node_name
      };

      cb(obj);

    });
  };


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    // 获取后台配置
    const dataSet = config ? config.nodeName : "";

    return (
        <Form >
          <FormItem label="步骤名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }
            ]
            })(
              <Input />
            )}
          </FormItem>

          {/* FormItem中的字段名出现大写就会报错，这个问题后续处理 */}
          <FormItem label="分析报表名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('node_name', {
              initialValue: dataSet,
              rules: [{  required: true, message: '请输入报表名称' }]
            })(
              <Input />
            )}
          </FormItem>
        </Form>
    );
  }
}
const AnalysisiReports = Form.create()(PluginComponent(AnalysisiReport));

export default connect()(AnalysisiReports);
