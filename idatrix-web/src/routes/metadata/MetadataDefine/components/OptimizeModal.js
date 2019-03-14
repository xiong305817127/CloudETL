/**
 * 调优参数组件
 */
import React from 'react';
import { Form, Input, Select, Radio } from 'antd';
import dbTypeValue from 'config/dbTypeValue.config';
import Modal from 'components/Modal';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout = {
  labelCol: { span: 8, offset: 0},
  wrapperCol: { span: 13, offset: 0},
};

class OptimizeModal extends React.Component {
  handleOK = () => {
    this.props.form.validateFields((err, values) => {
      if (err) return;
      if (typeof this.props.onOk === 'function') {
        this.props.onOk(values);
      }
      this.props.form.resetFields();
    });
  };

  handleCancel = () => {
    this.props.form.resetFields();
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  };

  // hive表单
  hiveForm = () => {
    const { optimize, readonly } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (<Form>
      <FormItem
        label="字段分隔符"
        {...formItemLayout}
      >
        {getFieldDecorator('fieldsTerminated', {
          initialValue: optimize.fieldsTerminated || '\\t',
        })(
          <Input disabled={readonly} maxLength="20" />
        )}
      </FormItem>
      <FormItem
        label="行分隔符"
        {...formItemLayout}
      >
        {getFieldDecorator('linesTerminated', {
          initialValue: optimize.linesTerminated || '\\n',
        })(
          <Input disabled={readonly} maxLength="20" />
        )}
      </FormItem>
      <FormItem
        label="存储格式"
        {...formItemLayout}
      >
        {getFieldDecorator('storedType', {
          initialValue: optimize.storedType || 'TEXTFILE',
        })(
          <Select disabled={readonly}>
            <Option value="SEQUENCEFILE">SEQUENCEFILE</Option>
            <Option value="TEXTFILE">TEXTFILE</Option>
            <Option value="PARQUET">PARQUET</Option>
          </Select>
        )}
      </FormItem>
    </Form>)
  };

  // hbase表单
  hbaseForm = () => {
    const { optimize, readonly } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (<Form>
      <FormItem
        label="只写入一次"
        {...formItemLayout}
      >
        {getFieldDecorator('immutableRows', {
          initialValue: optimize.immutableRows || false,
        })(
          <Radio.Group disabled={readonly}>
            <Radio value={true}>是</Radio>
            <Radio value={false}>否</Radio>
          </Radio.Group>
        )}
      </FormItem>
      <FormItem
        label="列编码方式"
        {...formItemLayout}
      >
        {getFieldDecorator('columnEncodedBytes', {
          initialValue: optimize.columnEncodedBytes || 'NONE',
        })(
          <Select disabled={readonly}>
            <Option value="NONE">NONE</Option>
            <Option value="1">使用1byte编码</Option>
            <Option value="2">使用2byte编码</Option>
            <Option value="3">使用3byte编码</Option>
            <Option value="4">使用4byte编码</Option>
          </Select>
        )}
      </FormItem>
      <FormItem
        label="历史版本数量"
        {...formItemLayout}
      >
        {getFieldDecorator('version', {
          initialValue: optimize.version || '1',
        })(
          <Input disabled={readonly} type="number" />
        )}
      </FormItem>
    </Form>)
  };

  render() {
    return (
      <Modal
        title="调优选项"
        visible={this.props.visible}
        onOk={this.handleOK}
        onCancel={this.handleCancel}
        width={400}
      >
        {this.props.type == dbTypeValue.hive ? this.hiveForm() : null}
        {this.props.type == dbTypeValue.hbase ? this.hbaseForm() : null}
      </Modal>
    );
  }
}

export default Form.create()(OptimizeModal);
