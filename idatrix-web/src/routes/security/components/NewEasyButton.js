import { Button,Form,Input } from 'antd';
import Modal from 'components/Modal';
const FormItem = Form.Item;

class NewEasyButtton extends React.Component {
  state = { visible: false }
  showModal = () => {
    this.setState({
      visible: true,
    });
  }
  handleOk = (e) => {
    console.log(e);
    this.setState({
      visible: false,
    });
  }
  handleCancel = (e) => {
    console.log(e);
    this.setState({
      visible: false,
    });
  }
  render() {
    return (
      <div>
        <Button type="primary" onClick={this.showModal}>新建</Button>
        <Modal
          title="新建/修改操作"
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form style={{margin:'0% 5%'}}>

            <FormItem   label="操作名称: ">

              <Input placeholder="请输入操作名称" />

            </FormItem>

            <FormItem   label="操作路径: " >

              <Input type="textarea" rows={4} placeholder="请输入操作路径" />

            </FormItem>

          </Form>
        </Modal>
      </div>
    );
  }
}

export default NewEasyButtton
