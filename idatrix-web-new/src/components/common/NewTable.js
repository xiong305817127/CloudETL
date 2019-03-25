import React from 'react'
import {Button,Form,Input} from 'antd';
import Modal from 'components/Modal';
const FormItem = Form.Item;


class NewTable extends React.Component {
  state = {
    modal2Visible: false,
  }

  setModal2Visible(modal2Visible) {
    this.setState({ modal2Visible });
  }
  render() {
    return (
      <div>
        <Button type="primary" onClick={() => this.setModal2Visible(true)}>新建</Button>
        <Modal
          title="新建应用"
          wrapClassName="vertical-center-modal"
          visible={this.state.modal2Visible}
          onOk={() => this.setModal2Visible(false)}
          onCancel={() => this.setModal2Visible(false)}
        >
          <Form style={{margin:'0% 5%'}}>
            <FormItem   label="应用名称: " style={{marginBottom:'8px',marginLeft:"20px"}}>
                <Input />
            </FormItem>

            <FormItem   label="应用说明: " style={{marginBottom:'8px',marginLeft:"20px"}}>

                <Input.TextArea   style={{height:60}}/>

            </FormItem>

          </Form>
        </Modal>
      </div>
    );
  }
}

export default NewTable;

