import { Button } from 'antd';
import Modal from 'components/Modal';
//自定义按钮去掉onOk默认命令：
class NewResourcesTablel extends React.Component {
  state = {
    modal1Visible: false,
  }

  setModal1Visible(modal1Visible) {
    this.setState({ modal1Visible });
  }


  render() {
    return (
      <div>
        <Button type="primary" onClick={() => this.setModal1Visible(true)}>新建</Button>
        <Modal
          title="新建"
          style={{ top: 300 }}
          visible={this.state.modal1Visible}
          onOk={() => this.setModal1Visible(false)}
          onCancel={() => this.setModal1Visible(false)}
        >
          <table >
            表格
          </table>

        </Modal>

      </div>
    );
  }
}
export default NewResourcesTablel
