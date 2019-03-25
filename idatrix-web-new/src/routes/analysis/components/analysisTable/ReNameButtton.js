/**
 * Created by Administrator on 2017/6/23 0023.
 */
import { Button, Form, Input, message } from 'antd';
import PropTypes from 'prop-types';
import Empower from '../../../../components/Empower';
import Modal from 'components/Modal';

const FormItem = Form.Item;

import { renameListTable } from  '../../../../services/analysis';

class ReNameButtton extends React.Component {
  state = {
    visible: false,
  }

  showModal = () => {
    this.setState({
      visible: true,
    });
  }

  // 提交表单
  handleOk = () => {
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const filePath = this.props.filePath;
        const fileName = this.props.oldFileName;
        const formData = {
          filePath,
          fileName,
          newFileName: values.newFileName,
        };
        // 重命名目录成功后，鼠标焦点需要离开重命名前选择的目录
        renameListTable(formData).then(({data}) => {
          const { form } = this.props;
          if (data.code === "200") {
            this.setState({ visible: false });
            this.props.onSuccess();
            form.resetFields();
          } else {
            form.setFields({
              newFileName: {
                value: form.getFieldValue('newFileName'),
                errors: [new Error(data.msg)],
              }
            });
          }
        });
      }
    });
  };
  handleCancel = () => {
    this.setState({ visible: false });
  };

  render(){
    const { getFieldDecorator } = this.props.form;
    const { visible} = this.state;
    return (
      <div>
        <Empower api="/hdfs/file/rename">
          <Button type='primary' disabled={this.props.disabled} onClick={this.showModal}>
            重命名
          </Button>
        </Empower>
        <Modal
          maskClosable={false}
          visible={visible}
          title="重命名"
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          footer={[
            <Button key="back" size="large" onClick={this.handleCancel} >取消</Button>,
            <Button key="submit" type="primary" size="large"  onClick={this.handleOk}>
              确认
            </Button>,
          ]}
        >
          <Form>
            <FormItem
              label="重命名："
              labelCol={{ span: 5 }}
              wrapperCol={{ span: 18 }}
            >
              {getFieldDecorator('newFileName', {
                initialValue: this.props.oldFileName,
                rules: [{ required: true, message: '请输入重命名目录名称' }],
              })(
                <Input />
              )}
            </FormItem>
          </Form>
        </Modal>
      </div>
    );
  }
}
ReNameButtton.propTypes = {
  oldFileName: PropTypes.string.isRequired,
  filePath: PropTypes.string.isRequired,
  onSuccess: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
};
const WrappedApp = Form.create()(ReNameButtton);
export default WrappedApp;
