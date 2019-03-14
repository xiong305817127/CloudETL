import { Form, Input, message } from 'antd';
import { withRouter } from 'react-router';
import { addTask } from '../../../../services/analysisTask';
import Modal from 'components/Modal';

const FormItem = Form.Item;
const { TextArea } = Input;
const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};

class Editor extends React.Component {

  handleOk() {
    this.props.form.validateFields(async (err, values) => {
      if (!err) {
        const { data } = await addTask(values);
        if (data && data.flag) {
          const { taskid } = data.data;
          const path = `/analysis/TaskManage/EditTaskManage/${taskid}`;
          this.props.router.push({
            pathname: path,
          })
          // message.success('新增任务成功');
          this.props.form.resetFields();
          this.props.onOk();
        } else {
          if (data.msg.indexOf('任务名称') > -1) {
            this.props.form.setFields({
              name: {
                value: values.name,
                errors: [new Error(data.msg)],
              }
            });
          }
        }
      }
    });
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  render() {
    const { getFieldDecorator } = this.props.form;

    return (<Modal
      maskClosable={false}
      title="新增任务"
      visible={this.props.visible}
      closable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
    >
      <Form>
        <FormItem
          label="任务名称："
          {...formItemLayout}
        >
          {getFieldDecorator('name', {
            rules: [
              { required: true, message: '任务名称不能为空' },
              { message: '任务名称必须以字母开头，后面可以接字母、数字、下划线及中划线' ,pattern: /^[a-zA-Z][\w-]*$/}
            ],
          })(
            <Input maxLength="50" spellCheck={false}/>
          )}
        </FormItem>

        <FormItem
          label="描述："
          {...formItemLayout}
        >
          {getFieldDecorator('description', {
            rules: [
              { required: true, message: '任务描述不能为空' },
            ]
          })(
            <TextArea maxLength="250" rows={4} spellCheck={false}/>
          )}
        </FormItem>
      </Form>
    </Modal>);
  }
}

export default Form.create()(withRouter(Editor));
