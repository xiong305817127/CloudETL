/**
 * 创建调度组件
 */

import { Row, Col, Form, Input, InputNumber, Checkbox, Select, DatePicker, message } from 'antd';
import moment from 'moment';
import { createTaskSchedule } from '../../../../services/analysisTask';
import { dateFormat } from '../../../../utils/utils';
import Modal from 'components/Modal';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

const format1 = 'YYYY-MM-DD HH:mm:ss';

class Schedule extends React.Component {

  state = {
    useCron: false,
    recurringFlag: false,
    intervalUnit: 'h',
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.visible && !this.props.visible) {
      const { trigger } = nextProps.data;
      this.setState({
        useCron: trigger.cronExpression || false,
        recurringFlag: trigger.recurringFlag || false,
        intervalUnit: trigger.intervalUnit || 'h',
      });
    }
  }

  handleOk() {
    this.props.form.validateFields(async (err, values) => {
      if (!err) {
        const formData = {
          id: this.props.data.trigger.id || 0,
          taskId: this.props.data.viewId,
          type: this.state.useCron ? 'cron' : 'date',
          recurringFlag: this.state.recurringFlag,
        };
        if (this.state.useCron) {
          formData.cronExpression = values.cronExpression;
        } else {
          formData.startTime = values.startTime.format(format1);
          formData.intervalValue = values.intervalValue;
          formData.intervalUnit = this.state.intervalUnit;
        }
        const { data } = await createTaskSchedule(formData);
        if (data && data.flag) {
          message.success('创建调度成功');
          this.props.form.resetFields();
          this.props.onOk(formData.id === 0);
        }
      }
    });
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  // 使用corn表达式
  handleUseCorn = (e) => {
    const useCron = e.target.checked;
    this.setState({ useCron });
  }

  // 设置可重复
  handlerecurringFlag = (e) => {
    const recurringFlag = e.target.checked;
    this.setState({ recurringFlag });
  }

  // 修改单位
  handleChangeUnit = (value) => {
    this.setState({
      intervalUnit: value,
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { data } = this.props;
    const { trigger } = data;

    return (<Modal
      maskClosable={false}
      title="调度管理"
      visible={this.props.visible}
      closable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
    >
      <Form>
        <fieldset className="ui-fieldset">
          <legend>任务信息</legend>
          <Row>
            <Col span="4" style={{textAlign: 'right'}}>任务名称：</Col>
            <Col span="8">{data.name}</Col>
            <Col span="4" style={{textAlign: 'right'}}>任务描述：</Col>
            <Col span="8">{data.description}</Col>
          </Row>
        </fieldset>

        <fieldset className="ui-fieldset" style={{marginTop: 20}}>
          <legend>调度设置</legend>
          <Row>
            <Col span="12">
              <FormItem
                label="时间："
                {...formItemLayout}
              >
                {getFieldDecorator('startTime', {
                  initialValue: moment(trigger.startTime || dateFormat(new Date), format1),
                })(
                  <DatePicker disabled={this.state.useCron} format={format1} showTime={true} />
                )}
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem>
                <Checkbox disabled={this.state.useCron} checked={this.state.recurringFlag} onChange={this.handlerecurringFlag}>重复每</Checkbox>
                {getFieldDecorator('intervalValue', {
                  initialValue: trigger.intervalValue || 1,
                })(
                  <InputNumber disabled={this.state.useCron || !this.state.recurringFlag} min={1} style={{width: 60}} />
                )}
                <Select
                  disabled={this.state.useCron || !this.state.recurringFlag}
                  value={this.state.intervalUnit}
                  style={{width: 80, marginLeft: 10}}
                  onChange={this.handleChangeUnit}
                >
                  <Option value="s">秒</Option>
                  <Option value="m">分</Option>
                  <Option value="h">时</Option>
                  <Option value="d">天</Option>
                  <Option value="w">周</Option>
                  <Option value="M">月</Option>
                  <Option value="y">年</Option>
                </Select>
              </FormItem>
            </Col>
          </Row>

          <FormItem>
            <Checkbox checked={this.state.useCron} onChange={this.handleUseCorn}>使用Cron表达式</Checkbox>
            {getFieldDecorator('cronExpression', {
              initialValue: trigger.cronExpression,
            })(
              <Input disabled={!this.state.useCron} style={{width: '70%'}} />
            )}
          </FormItem>
        </fieldset>
      </Form>
    </Modal>);
  }
}

export default Form.create()(Schedule);
