/**
 * Created by Steven Leo on 2018/10/08.
 */
import React from "react";
import { connect } from "dva";
import { Form, Select, Button, Input, Radio, Icon, Tooltip, Checkbox,Row,Col } from "antd";

import styles from "./QC.less";
import PluginComponent from "./HOC/PluginComponent";

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;

class CertificatesAnalysis extends React.Component {
  constructor(props) {
    super(props);

    this.text1 =
      "18位的第二代身份证号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码、八位数字出生日期码、三位数字顺序码和一位数字校验码。";
    this.text2 =
      "第一代身份证都是15位的,其中从左至右前六位是反映地址的,如同一个县区镇的人员,前六位是一致的,而15位号码的,从第七位数至12位数是反映个人出生日期的.7、8两位数是年（如1979年出处的，出生年只取后两位即79），9、10两位是月、11、12两位数是日。最后三位是顺序码，即在同一地同年同日生人作区分的，其中最后一位是偶数的分配给女性";
  }

  handleCreate = (cb) => {
    const form = this.props.form;
    const {
      panel,
      transname,
      description,
      key,
      saveStep,
      text,
      config,
      formatTable
    } = this.props.model;
    const { nodeName } = config;
    const { data } = this.props;

    form.validateFields((err, values) => {

      if (err) {
        return;
      }

      if (nodeName === values.node_name && text === values.text) {
        hideModal();
        return;
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = text === values.text ? "" : values.text;
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        nodeName: values.text,
        referenceValues: values.fieldTypes,
        fieldNames: [values.fieldNames]
      };

      cb(obj);
    });
  };

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { text, config, visible, handleCheckName } = this.props.model;
    const { data, ifNoName } = this.props;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };

    // 获取后台配置
    const referenceValues = config.referenceValues;
    const [name] = config.fieldNames;

    return (
      <Form>
        <FormItem
          label="步骤自定义"
          style={{ marginBottom: "8px" }}
          {...formItemLayout1}
        >
          {getFieldDecorator("text", {
            initialValue: text ? text : "身份信息检测",
            rules: [
              { whitespace: true, required: true, message: "请输入步骤名称" },
              { validator: handleCheckName, message: "步骤名称已存在，请更改!" }
            ]
          })(<Input />)}
        </FormItem>
        {data.InputData.length > 0 && (
          <FormItem
            label="要检验的字段"
            style={{ marginBottom: "8px" }}
            {...formItemLayout1}
          >
            {getFieldDecorator("fieldNames", {
              initialValue: name ? name : "nodata",
              rules: [
                {
                  whitespace: true,
                  required: true,
                  message: "请选择检测的字段"
                },
                { validator: ifNoName, message: "请选择要检测的字段!" }
              ]
            })(
              <Select>
                <Select.Option key="nodata" value="nodata">
                  请选择
                </Select.Option>
                {data.InputData.map(index => (
                  <Select.Option key={index.name} value={index.name}>
                    {index.name}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
        )}
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
                { validator: ifNoName, message: "前置步骤无可选字段!" }
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
        <FormItem
          label="参考值"
          style={{ marginBottom: "8px" }}
          {...formItemLayout1}
        >
          {getFieldDecorator("fieldTypes", {
            initialValue: referenceValues,
            rules: [{ required: true, message: "请选择身份证规则" }]
          })(
            <CheckboxGroup className={styles.radio_group} >
                <Row>
                    <Col span={24}>
                        <Checkbox value="Card18">18位身份证号规则</Checkbox>
                        <Tooltip placement="rightTop" title={this.text1}>
                            <Icon type="info-circle"   />
                        </Tooltip>
                    </Col>
                    <Col span={24}>
                        <Checkbox value="Card15">旧身份证15位规则</Checkbox>
                        <Tooltip placement="rightTop" title={this.text2}>
                            <Icon type="info-circle"   />
                        </Tooltip>
                    </Col>
                </Row>
            </CheckboxGroup>
          )}
        </FormItem>
      </Form>
    );
  }
}

const CertificatesAnalysisForm = Form.create()(
  PluginComponent(CertificatesAnalysis)
);

export default connect()(CertificatesAnalysisForm);
