import React from "react";
import { connect } from "dva";
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
  Col,
  Modal
} from "antd";
const FormItem = Form.Item;
import Style from "../ResourceContent.css";
import EditTable from "../../common/EditTable";
import Empower from "../../../../../components/Empower";
import {
  getsave_list,
  getServer_list,
  check_Cluster
} from "../../../../../services/gather";
import { withRouter } from "react-router";

let Timer;
class DataClustModel extends React.Component {
  constructor(props) {
    super(props);

    const { servers } = props.resourcecontent.config;
    let args = [];
    let count = 1;
    if (servers && servers.length > 0) {
      for (let index of servers) {
        args.push({
          key: count,
          ...index,
          master: index.master ? "是" : "否"
        });
        count++;
      }
    }

    this.state = {
      dataSource: args,
      visible: false,
      selectedRowKeys: [],
      serveList: [],
      loading: true,
      selectedRows: []
    };
  }

  //tabel一般选项
  columns = [
    {
      title: "服务器名称",
      dataIndex: "serverName",
      key: "serverName",
      width: "60%"
    },
    {
      title: "是否为主服务器",
      dataIndex: "master",
      key: "master"
    }
  ];

  columns1 = [
    {
      title: "服务器名称",
      dataIndex: "serverName",
      key: "serverName",
      width: "60%"
    },
    {
      title: "是否为主服务器",
      dataIndex: "master",
      key: "master"
    }
  ];

  formItemLayout = {
    labelCol: {
      span: 4,
      xl: { span: 3 }
    },
    wrapperCol: {
      span: 8,
      xl: { span: 7 }
    }
  };
  formItemLayout1 = {
    labelCol: {
      span: 10
    },
    wrapperCol: { span: 8 }
  };

  formatTable = (obj, obj1) => {
    let args = [];
    for (let index of obj) {
      let obj = {};
      for (let index1 of obj1) {
        if (index[index1] && index[index1].toString().trim()) {
          obj[index1] = index[index1];
        }
      }
      args.push(obj);
    }
    return args;
  };

  handleSubmit = e => {
    e.preventDefault();
    const form = this.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if (this.refs.Table) {
        if (this.refs.Table.state.dataSource.length > 0) {
          let args = ["serverName", "master", "status"];
          sendFields = this.formatTable(this.refs.Table.state.dataSource, args);
          for (let index of sendFields) {
            index.master = index.master === "是" ? true : false;
          }
        }
      }
      values.servers = sendFields;
      this.getSave(values);
    });
  };
  getHide() {
    const form = this.props.form;
    form.resetFields();
    const { location, router } = this.props;
    router.push({ ...location, query: {} });
  }

  getSave(obj) {
    const { username } = this.props.account;
    getsave_list({ ...obj, owner: username }).then(res => {
      const { code } = res.data;
      if (code === "200") {
        message.success("保存成功");
        this.getHide();
      }
    });
  }

  getServer() {
    this.setState({
      loading: true,
      visible: true
    });
    getServer_list().then(res => {
      const { code, data } = res.data;

      if (code === "200") {
        if (data) {
          let args = [];
          let count = 1;
          for (let index of data) {
            args.push({
              ...index,
              key: count,
              master: index.master ? "是" : "否",
              serverName: index.name
            });
            count++;
          }

          this.setState({
            serveList: args,
            loading: false
          });
        }
      }
    });
  }

  rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      this.setState({
        selectedRowKeys: selectedRowKeys,
        selectedRows: selectedRows
      });
    }
  };

  handleModelSure() {
    let isHas = 0;

    if (this.state.selectedRows.length > 0) {
      for (let index of this.state.selectedRows) {
        if (index.master === "是") {
          isHas++;
        }
      }
    }
    if (isHas === 0) {
      message.error("必须选择一个主服务器");
    } else if (isHas > 1) {
      message.error("不能选择多个主服务器");
    } else {
      this.refs.Table.updateTable(
        this.state.selectedRows,
        this.state.selectedRows.length
      );
    }
    this.setModelCancel();
  }

  setModelCancel() {
    this.setState({
      visible: false,
      selectedRowKeys: [],
      serveList: [],
      selectedRows: []
    });
    this.state.selectedRowKeys.splice(0);
    this.state.serveList.splice(0);
  }
  handleCancel = () => {
    this.setState({
      visible: false
    });
  };

  handleConfirm = (rule, value, callback) => {
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
          check_Cluster(value).then(res => {
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

  render() {
    const { config } = this.props.resourcecontent;

    const { getFieldDecorator } = this.props.form;
    const { selectedRowKeys } = this.state;

    const setDisabled = () => {
      if (selectedRowKeys.length > 0) {
        return false;
      } else {
        return true;
      }
    };
    return (
      <div
        className={Style.ServerCenter}
        style={{ paddingLeft: "30px", paddingTop: "30px" }}
      >
        <div className={Style.divTabs}>
          <Form>
            <FormItem label="schema名称" {...this.formItemLayout}>
              {getFieldDecorator("name", {
                initialValue: config.name,
                rules: [
                  { required: true, message: "请输入schema名称" },
                  {
                    validator: this.handleConfirm,
                    message: "schema名称已存在，请更改!"
                  }
                ]
              })(<Input disabled={config.name ? true : false} />)}
            </FormItem>
            <FormItem label="端口" {...this.formItemLayout}>
              {getFieldDecorator("port", {
                initialValue: config.port ? config.port : 40000,
                rules: [
                  {
                    required: true,
                    pattern: /^([1-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
                    message: "请输入正确的端口号"
                  }
                ]
              })(<Input />)}
            </FormItem>

            <FormItem
              label="schema缓存大小"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout}
            >
              {getFieldDecorator("buffer", {
                initialValue: config.buffer ? config.buffer : 2000
              })(<Input />)}
            </FormItem>

            <FormItem
              label="刷新间隔"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout}
            >
              {getFieldDecorator("rows", {
                initialValue: config.rows ? config.rows : 5000
              })(<Input />)}
            </FormItem>
            <Row style={{ marginBottom: "8px" }}>
              <Col span={4} xl={3}>
                &nbsp;
              </Col>
              <Col span={6} xl={5}>
                <FormItem style={{ marginBottom: "8px" }}>
                  {getFieldDecorator("compress", {
                    valuePropName: "checked",
                    initialValue: config.compress ? config.compress : true
                  })(<Checkbox>是否压缩</Checkbox>)}
                </FormItem>
              </Col>
              <Col span={10}>
                <FormItem
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("dynamic", {
                    valuePropName: "checked",
                    initialValue: config.dynamic ? config.dynamic : false
                  })(<Checkbox>Dynamic cluster</Checkbox>)}
                </FormItem>
              </Col>
            </Row>
            <Row style={{ marginBottom: "8px" }}>
              <Col span={3} xl={2}>
                &nbsp;
              </Col>
              <Col span={5} xl={4}>
                <div>子服务器:</div>
              </Col>
              <Col style={{ textAlign: "right" }} span={5} xl={4}>
                <Button size={"small"} onClick={this.getServer.bind(this)}>
                  获取服务器
                </Button>
              </Col>
            </Row>
            <Row>
              <Col span={3} xl={2}>
                &nbsp;
              </Col>
              <Col span={10} xl={8}>
                <EditTable
                  extendDisabled={true}
                  columns={this.columns}
                  tableStyle="editTableStyle5"
                  scroll={{ y: 140 }}
                  size={"small"}
                  dataSource={this.state.dataSource}
                  ref="Table"
                />
              </Col>
            </Row>

            <Row style={{ bottom: "-30px" }}>
              <Col span={2}>&nbsp;</Col>
              <Col span={5} xl={3} style={{ textAlign: "right" }}>
                <Empower api="/cloud/editCluster.do">
                  <Button type="primary" onClick={this.handleSubmit.bind(this)}>
                    保存
                  </Button>
                </Empower>
              </Col>
              <Col span={5} xl={4} style={{ textAlign: "center" }}>
                <Button onClick={this.getHide.bind(this)}>取消</Button>
              </Col>
            </Row>
          </Form>
          <Modal
            title="服务器列表"
            visible={this.state.visible}
            wrapClassName="vertical-center-modal"
            onCancel={this.handleCancel}
            footer={[
              <Button
                key="submit"
                type="primary"
                disabled={setDisabled()}
                size="large"
                onClick={this.handleModelSure.bind(this)}
              >
                确定
              </Button>,
              <Button
                key="back"
                size="large"
                onClick={this.setModelCancel.bind(this)}
              >
                取消
              </Button>
            ]}
          >
            <Table
              loading={this.state.loading}
              pagination={false}
              scroll={{ y: 400 }}
              rowSelection={this.rowSelection}
              columns={this.columns1}
              dataSource={this.state.serveList}
            />
          </Modal>
        </div>
      </div>
    );
  }
}

const ClusterModel = Form.create()(DataClustModel);

export default withRouter(
  connect(({ resourcecontent, account }) => ({
    resourcecontent,
    account
  }))(ClusterModel)
);
