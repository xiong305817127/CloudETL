/**
 * Created by Administrator on 2017/3/13.
 */
import React from "react";
import { Button, Form, Input } from "antd";
import Modal from "components/Modal.js";
import { connect } from "dva";
import {
  getEdit_trans_attributes,
  getSave_trans_attributes,
  getCheck_trans_name,
  getNew_trans
} from "../../../../../services/gather";

const FormItem = Form.Item;
const { TextArea } = Input;

let Timer;

class Trans extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      info_name: "",
      description: "",
      dispatch: props.dispatch,
      visible: false,
      viewId: props.viewId,
      repeat: false
    };
  }

  setModelHide = () => {
    this.setState({
      visible: false
    });
  };

  handleSure = () => {
    const { form } = this.props;
    const { actionName, viewId } = this.props.newtrans;
    const { owner } = this.props.transheader;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      const { dispatch } = this.props;
      if (actionName === "newTrans") {
        getNew_trans(values).then(res => {
          dispatch({
            type: "workview/newFile",
            name: values.info_name,
            model: "view",
            viewId: viewId
          });
          dispatch({
            type: "app/changeStatus",
            status: "design"
          });
        });
      } else if (actionName === "showTrans") {
        const { info_name } = this.props.newtrans;
        const { dispatch } = this.props;
        values.name = info_name;
        values.newname = values.info_name === info_name ? "" : values.info_name;

        getSave_trans_attributes({ ...values, owner }).then(res => {
          dispatch({
            type: "designplatform/queryTransList",
            payload: {
              isMap: true
            }
          });
          if (values.newname) {
            dispatch({
              type: "workview/changeName",
              name: values.info_name,
              viewId: viewId
            });
          }
        });
      }
      form.resetFields();
      this.setModelHide();
    });
  };

  componentWillReceiveProps(nextProps) {
    console.count("componentWillReceiveProps:");

    const { actionName, info_name, visible, viewId } = nextProps.newtrans;

    if (actionName === "newTrans" && this.state.visible === false) {
      this.setState({
        visible: visible,
        info_name: "",
        description: "",
        viewId: viewId
      });
    } else if (actionName === "showTrans" && this.state.visible === false) {
      const { owner } = this.props.transheader;

      getEdit_trans_attributes({ name: info_name, owner }).then(res => {
        const { description } = res.data;
        this.setState({
          info_name: info_name,
          description: description ? description : "",
          visible: visible,
          viewId: viewId
        });
      });
    }
  }

  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  formItemLayout2 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };

  handleConfirm = (rule, value, callback) => {
    const { info_name } = this.state;
    const { owner } = this.props.transheader;

    if (value && info_name != value) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        getCheck_trans_name({ name: value, owner }).then(res => {
          const { result } = res.data;

          if (result === true) {
            callback(true);
          } else {
            callback();
          }
        });
      }, 300);
    } else {
      callback();
    }
  };

  getText(actionName) {
    if (actionName === "newTrans") {
      return "新建";
    } else {
      return "编辑";
    }
  }

  render() {
    const { actionName } = this.props.newtrans;
    const { getFieldDecorator } = this.props.form;

    return (
      <Modal
        visible={this.state.visible}
        title={this.getText(actionName) + "转换"}
        wrapClassName="vertical-center-modal"
        okText="Create"
        footer={[
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={() => {
              this.handleSure();
            }}
          >
            确定
          </Button>,
          <Button
            key="back"
            size="large"
            onClick={() => {
              this.setModelHide();
            }}
          >
            取消
          </Button>
        ]}
        style={{ zIndex: 50 }}
      >
        <Form>
          <FormItem label="转换名称" {...this.formItemLayout}>
            {getFieldDecorator("info_name", {
              initialValue: this.state.info_name,
              rules: [
                { required: true, message: "请输入名称" },
                {
                  validator: this.handleConfirm,
                  message: "名称已存在，请更改!"
                }
              ]
            })(<Input />)}
          </FormItem>
          <FormItem label="描述" {...this.formItemLayout2}>
            {getFieldDecorator("description", {
              initialValue: this.state.description
            })(<TextArea />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}

const NewTransModel = Form.create()(Trans);

export default connect(({ newtrans, transheader }) => ({
  newtrans,
  transheader
}))(NewTransModel);
