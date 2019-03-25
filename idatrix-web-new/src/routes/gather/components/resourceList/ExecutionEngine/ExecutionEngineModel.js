import React from "react";
import { connect } from "dva";
import { withRouter } from "react-router";
import {
  Form,
  Select,
  Input,
  Button,
  Checkbox,
  Table,
  Radio,
  Tabs,
  message,
  Row,
  Col
} from "antd";
const FormItem = Form.Item;
const Option = Select.Option;

import {
  getServer_list,
  saveEngine,
  checkEngineName
} from "../../../../../services/gather";
import Style from "../ResourceContent.css";
import Empower from "../../../../../components/Empower";

let Timer;
class ExecutionEngineDatail extends React.Component {
  constructor(props) {
    super(props);
    let key = this.getValue(props.resourcecontent.config);
    this.state = {
      serverList: [],
      activeKey: key,
      canSelect: false
    };
  }

  componentWillMount() {
    this.Request();
  }

  Request() {
    getServer_list().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        this.setState({
          serverList: data,
          canSelect: false
        });
      }
    });
  }

  formItemLayout = {
    labelCol: { span: 2 },
    wrapperCol: { span: 6 }
  };
  formItemLayout6 = {
    labelCol: { span: 2 },
    wrapperCol: { span: 8 }
  };

  formItemLayout1 = {
    labelCol: { span: 4 },
    wrapperCol: { span: 8 }
  };
  formItemLayout2 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 4 }
  };
  formItemLayout4 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 20 }
  };

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }

      const { username } = this.props.account;
      if (values.local === "local") {
        values.local = true;
        values.remote = false;
        values.clustered = false;
      } else if (values.local === "remote") {
        values.local = false;
        values.remote = true;
        values.clustered = false;
      } else {
        values.local = false;
        values.remote = false;
        values.clustered = true;
        values.server = "Clustered";
      }

      saveEngine({ ...values, owner: username }).then(res => {
        const { code } = res.data;
        if (code === "200") {
          this.getHide();
          message.success("保存成功");
        }
      });
    });
  };

  handlecheckEngine = (rule, value, callback) => {
    const { config } = this.props.resourcecontent;

    if (value && value.trim() && value === config.name) {
      callback();
    } else {
      if (value && value.trim()) {
        if (Timer) {
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(() => {
          checkEngineName(value).then(res => {
            const { code, data } = res.data;
            if (code === "200") {
              const { result } = data;
              if (result === true) {
                callback(true);
              } else {
                callback();
              }
            }
          });
        }, 300);
      } else {
        callback();
      }
    }
  };

  handleFormLayoutChange = e => {
    const activeKey = e.target.value;
    this.setState({ activeKey });
  };

  getHide() {
    const form = this.props.form;
    form.resetFields();
    const { location, router } = this.props;
    router.push({ ...location, query: {} });
  }

  getValue(obj) {
    if (obj.local) {
      return "local";
    } else if (obj.remote) {
      return "remote";
    } else {
      return "clustered";
    }
  }

  showModel(status) {
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.resourcecontent;
    if (status === "local") {
      return (
        <div style={{ marginLeft: "5%" }}>
          说明描述:此转换将在云化数据集成系统服务器上运行。
        </div>
      );
    } else if (status === "remote") {
      const { serverList } = this.state;

      return (
        <div>
          <FormItem label="服务器" {...this.formItemLayout2}>
            {getFieldDecorator("server", {
              initialValue: config.server
            })(
              <Select>
                {serverList.map(index => {
                  return <Option key={index.name}>{index.name}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <Row>
            <Col span={3}>&nbsp;</Col>
            <Col>
              <FormItem {...this.formItemLayout4}>
                {getFieldDecorator("sendResources", {
                  valuePropName: "checked",
                  initialValue: config.sendResources
                })(<Checkbox>将资源发送到此服务器</Checkbox>)}
              </FormItem>
            </Col>
          </Row>
        </div>
      );
    } else {
      return (
        <div>
          <Row>
            <Col span={2}>&nbsp;</Col>
            <Col>
              <FormItem
                style={{ marginBottom: "0px" }}
                {...this.formItemLayout4}
              >
                {getFieldDecorator("logRemoteExecutionLocally", {
                  valuePropName: "checked",
                  initialValue: config.logRemoteExecutionLocally
                })(<Checkbox>在本地记录远程执行</Checkbox>)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={2}>&nbsp;</Col>
            <Col>
              <FormItem {...this.formItemLayout4}>
                {getFieldDecorator("showTransformations", {
                  valuePropName: "checked",
                  initialValue: config.showTransformations
                })(<Checkbox>显示转换</Checkbox>)}
              </FormItem>
            </Col>
          </Row>
        </div>
      );
    }
  }

  render() {
    const { config } = this.props.resourcecontent;
    const { getFieldDecorator } = this.props.form;

    return (
      <div
        className={Style.ServerCenter}
        style={{ paddingLeft: "20px", paddingTop: "20px" }}
      >
        <div className={Style.divTabs}>
          <Form onSubmit={this.handleSubmit}>
            <FormItem label="名称" {...this.formItemLayout}>
              {getFieldDecorator("name", {
                initialValue: config.name,
                rules: [
                  { required: true, message: "请输入执行引擎名称" },
                  {
                    validator: this.handlecheckEngine,
                    message: "执行引擎名称已存在，请更改!"
                  }
                ]
              })(<Input disabled={config.name ? true : false} />)}
            </FormItem>

            <FormItem label="描述" {...this.formItemLayout}>
              {getFieldDecorator("description", {
                initialValue: config.description
              })(<Input />)}
            </FormItem>
            <FormItem label="执行方式" {...this.formItemLayout6}>
              {getFieldDecorator("local", {
                initialValue: this.getValue(config),
                onChange: this.handleFormLayoutChange.bind(this)
              })(
                <Radio.Group style={{ marginBottom: 8 }}>
                  <Radio.Button value="local">Local</Radio.Button>
                  <Radio.Button value="remote">Remote</Radio.Button>
                  <Radio.Button
                    value="clustered"
                    disabled={this.state.canSelect}
                  >
                    Clustered
                  </Radio.Button>
                </Radio.Group>
              )}
            </FormItem>
            {this.showModel(this.state.activeKey)}

            <Row className={Style.BottomRow} style={{ bottom: "-185px" }}>
              <Col span={12} style={{ textAlign: "right" }}>
                <Empower api="/cloud/editEngine.do">
                  <Button type="primary" htmlType="submit">
                    保存
                  </Button>
                </Empower>
              </Col>
              <Col span={12} style={{ textAlign: "center" }}>
                <Button onClick={this.getHide.bind(this)}>取消</Button>
              </Col>
            </Row>
          </Form>
        </div>
      </div>
    );
  }
}

const ExecutionName = Form.create()(ExecutionEngineDatail);

export default withRouter(
  connect(({ resourcecontent, account }) => ({
    resourcecontent,
    account
  }))(ExecutionName)
);
