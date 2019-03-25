import { Button,Form,Input,message } from 'antd';
import PropTypes from 'prop-types';
import {newListTable} from  '../../../../services/analysis';
import Empower from '../../../../components/Empower';
import Modal from 'components/Modal';
const FormItem = Form.Item;

class NewListButtton extends React.Component {
  state = {
    loading: false,
    visible: false,
  }

  showModal = () => {
    this.setState({
      visible: true,
    });
  }

  // 提交新建：增加一个参数fileName
  handleOk = () => {
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const filePath = this.props.filePath;
        const formData = {
          filePath,
          newFileName: values.newFileName,
        };
        newListTable(formData).then(({data}) => {
          const { form } = this.props;
          if (data.code === "200") {
            // message.success('新建成功');
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
    const { visible, loading  } = this.state;
    return (
      <div>
        <Empower api="/hdfs/file/new">
          <Button type='primary'  onClick={this.showModal} style={{marginLeft:'20px',marginBottom:'5px'}}>
            新建目录
          </Button>
        </Empower>
        <Modal
          maskClosable={false}
          visible={visible}
          title="新建目录"
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          footer={[
            <Button key="back" size="large" onClick={this.handleCancel} >取消</Button>,
            <Button key="submit" type="primary" size="large" loading={loading} onClick={this.handleOk}>
              确认
            </Button>,
          ]}
        >
          <Form>

            <FormItem
              ref="form"
              label="目录名称"
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 18 }}
            >
              {getFieldDecorator('newFileName', {
                initialValue: "",
                rules: [
                  { required: true, message: '只能允许中文、数字、字母和下划线作为目录名称' ,pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/}
                ],
              })(
                <Input maxLength="50"  spellCheck={false}/>
              )}
            </FormItem>

          </Form>
        </Modal>
      </div>
    );
  }
}

NewListButtton.propTypes = {
  filePath: PropTypes.string.isRequired,
  onSuccess: PropTypes.func.isRequired,
};

const App = Form.create()(NewListButtton);
export default App;
