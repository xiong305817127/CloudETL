import {Form, Input} from 'antd';
import Modal from 'components/Modal';
const FormItem = Form.Item;
const { TextArea } = Input;
//自定义按钮去掉onOk默认命令：onOk={() => this.setModal1Visible(false)}
class AddEasyTable extends React.Component {

  state = {
    modal1Visible: false,
    fields: {
      id: '',
      name: '',
      description: '',
    },
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.fields) {
      this.setState({
        fields: { ...nextProps.fields }
      });
    }
    if (typeof nextProps.modalVisible === 'boolean') {
      this.setModal1Visible(nextProps.modalVisible);
    }
  }

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        if (typeof this.props.okCb === 'function') {
          this.props.okCb(Object.assign({}, values, { id: this.state.fields.id }));
          this.setState({
            fields: {
              id: '',
              name: '',
              description: '',
            },
          });
          this.setModal1Visible(false);
          this.props.form.resetFields();
        }
      }
    });
  }
  //取消操作时也需要清空表单，防止影响到编辑状态
  handleCancel() {
    if (typeof this.props.cancelCb === 'function') {
      this.props.cancelCb();
    }
    this.props.form.resetFields();
  }

  setModal1Visible(modal1Visible) {
    this.setState({ modal1Visible });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { id, name, description } = this.state.fields;

    return (
      <div>
        <Modal
          maskClosable={false}
          title={ (id ? '编辑' : '新增') + '应用' }
          visible={this.state.modal1Visible}
          onOk={(e) => this.handleSubmit(e)}
          onCancel={() => this.handleCancel()}
        >

          <Form style={{margin:'0% 5%'}}>

            <FormItem label="应用名称: " style={{marginBottom:'8px',marginLeft:"20px"}}>

              {getFieldDecorator('name', {
                initialValue: name,
                rules: [{ required: true, message: '应用名称不能为空' },{  message: '只能允许中文、数字、字母和下划线作为应用名称' ,pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/}],
              })(
                <Input maxLength="100" spellCheck={false} />
              )}

            </FormItem>

            <FormItem label="应用说明: " style={{marginBottom:'8px',marginLeft:"20px"}}>

              {getFieldDecorator('description', {
                initialValue: description,
              })(
                <TextArea  maxLength="250" rows={4} spellCheck={false}/>
              )}

            </FormItem>

          </Form>

        </Modal>

      </div>
    );
  }
}
export default Form.create()(AddEasyTable);
