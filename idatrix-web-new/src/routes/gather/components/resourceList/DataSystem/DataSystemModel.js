/**
 * Created by Administrator on 2017/9/5.
 */
import React from "react";
import { connect } from "dva";
import {
  Button,
  Tabs,
  Checkbox,
  Form,
  Input,
  Cascader,
  Row,
  Col,
  message,
  Select
} from "antd";
import Style from "./DataSystemModel.css";
import EditTable from "../../common/EditTable";
import Empower from "../../../../../components/Empower";
import { withRouter } from "react-router";
import { strEnc, strDec } from "utils/EncryptUtil";
import JNDIModal from "./JNDIModal";

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const SelectOption = Select.Option;
const ButtonGroup = Button.Group;
const { TextArea } = Input;

let Timer;
class DataDetail extends React.Component {
  constructor(props) {
    super(props);

    const { config, JNDIargs } = props.resourcecontent;
    let args = ["MYSQL", "0"];
    if (config.type) {
      args = [config.type, config.access];
    }

    if (config.options && config.options.length > 0) {
      let count = 0;
      for (let index of config.options) {
        if (index) {
          this.dataSource[count].optKey = index.optKey;
          this.dataSource[count].optVal = index.optVal;
        }
        count++;
      }
    }

    this.state = {
      value: args,
      update: "update",
      activeKey: "1",
      disabled: true,
      JNDIargs: JNDIargs
    };
  }

  componentWillMount() {
    const { value } = this.state;
    this.requestJNDI(value);
  }

  componentWillReceiveProps(nextProps) {
    const { JNDIargs } = nextProps.resourcecontent;
    this.setState({
      JNDIargs: JNDIargs
    });
  }

  getModel(config) {
    const { value } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { JNDIargs } = this.state;

    switch (this.getType(value)) {
      case 11:
        return (
          <div>
            <FormItem label="主机名称" {...this.formItemLayout2}>
              {getFieldDecorator("hostname11", {
                initialValue: config.hostname,
                rules: [{ required: true, message: "请输入主机名称" }]
              })(<Input />)}
            </FormItem>
            <FormItem label="数据库名称" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName11", {
                initialValue: config.databaseName,
                rules: [
                  {
                    required: true,
                    message: "请输入数据库名称",
                    pattern: /^(?=[a-z])\w+$/i,
                    message: "只能使用字母、数字、下划线，且必须以字母开头"
                  }
                ]
              })(<Input />)}
            </FormItem>
            <FormItem label="端口号" {...this.formItemLayout2}>
              {getFieldDecorator("port11", {
                initialValue: config.port,
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
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username11", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password11", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
          </div>
        );
      case 12:
        return (
          <div>
            <FormItem label="主机名称" {...this.formItemLayout2}>
              {getFieldDecorator("hostname12", {
                initialValue: config.hostname,
                rules: [{ required: true, message: "请输入主机名称" }]
              })(<Input />)}
            </FormItem>
            <FormItem label="数据库名称" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName12", {
                initialValue: config.databaseName,
                rules: [
                  { required: true, message: "请输入数据库名称" },
                  {
                    pattern: /^(?=[a-z])\w+$/i,
                    message: "只能使用字母、数字、下划线，且必须以字母开头"
                  }
                ]
              })(<Input />)}
            </FormItem>
            <FormItem label="端口号" {...this.formItemLayout2}>
              {getFieldDecorator("port12", {
                initialValue: config.port,
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
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username12", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password12", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
            <FormItem
              style={{ margin: "0px", marginLeft: "13%" }}
              {...this.formItemLayout1}
            >
              {getFieldDecorator("modifier12", {
                valuePropName: "checked",
                initialValue: config.modifier
              })(<Checkbox>Use Result Streaming Cursor</Checkbox>)}
            </FormItem>
          </div>
        );
      case 13:
        return (
          <div>
            <FormItem label="主机名称" {...this.formItemLayout2}>
              {getFieldDecorator("hostname13", {
                initialValue: config.hostname,
                rules: [{ required: true, message: "请输入主机名称" }]
              })(<Input />)}
            </FormItem>
            <FormItem label="数据库名称" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName13", {
                initialValue: config.databaseName,
                rules: [
                  { required: true, message: "请输入数据库名称" },
                  {
                    pattern: /^(?=[a-z])\w+$/i,
                    message: "只能使用字母、数字、下划线，且必须以字母开头"
                  }
                ]
              })(<Input />)}
            </FormItem>
            <FormItem
              label="数据表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("dataTableSpace13", {
                initialValue: config.dataTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="索引表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("indexTableSpace13", {
                initialValue: config.indexTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="端口号"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("port13", {
                initialValue: config.port,
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
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username13", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password13", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
          </div>
        );
      case 14:
        return (
          <div>
            <FormItem label="主机名称" {...this.formItemLayout2}>
              {getFieldDecorator("hostname14", {
                initialValue: config.hostname,
                rules: [{ required: true, message: "请输入主机名称" }]
              })(<Input />)}
            </FormItem>
            <FormItem label="数据库名称" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName14", {
                initialValue: config.databaseName,
                rules: [
                  { required: true, message: "请输入数据库名称" },
                  {
                    pattern: /^(?=[a-z])\w+$/i,
                    message: "只能使用字母、数字、下划线，且必须以字母开头"
                  }
                ]
              })(<Input />)}
            </FormItem>
            <FormItem
              label="示例名称"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("sqlServerInstance14", {
                initialValue: config.sqlServerInstance
              })(<Input />)}
            </FormItem>
            <FormItem label="端口号" {...this.formItemLayout2}>
              {getFieldDecorator("port14", {
                initialValue: config.port,
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
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username14", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password14", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
            <FormItem
              style={{ margin: "0px", marginLeft: "13%" }}
              {...this.formItemLayout1}
            >
              {getFieldDecorator("useIntegratedSecurity14", {
                valuePropName: "checked",
                initialValue: config.useIntegratedSecurity
              })(<Checkbox>使用集成安全策略</Checkbox>)}
            </FormItem>
            <FormItem
              style={{ margin: "0px", marginLeft: "13%" }}
              {...this.formItemLayout1}
            >
              {getFieldDecorator("useDoubleDecimalSeparator14", {
                valuePropName: "checked",
                initialValue: config.useDoubleDecimalSeparator
              })(<Checkbox>使用..作为模式跟表的分隔符</Checkbox>)}
            </FormItem>
          </div>
        );
      case 15:
        return (
          <div>
            <FormItem
              label="自定义连接 URL"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("url", {
                initialValue: config.url
              })(<Input />)}
            </FormItem>
            <FormItem
              label="自定义驱动类名称"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("driver", {
                initialValue: config.driver
              })(<Input />)}
            </FormItem>
            <FormItem
              label="命名空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("dataTableSpace15", {
                initialValue: config.dataTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username15", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password15", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
          </div>
        );
      case 21:
        return (
          <div>
            <FormItem label="ODBC DSN源名称" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName21", {
                initialValue: config.databaseName,
                rules: [{ required: true, message: "请输入ODBC DSN源名称" }]
              })(<Input />)}
            </FormItem>
            <FormItem
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username21", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password21", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
          </div>
        );
      case 22:
        return (
          <div>
            <FormItem label="ODBC DSN源名称" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName22", {
                initialValue: config.databaseName,
                rules: [{ required: true, message: "请输入ODBC DSN源名称" }]
              })(<Input />)}
            </FormItem>
            <FormItem
              label="数据表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("dataTableSpace22", {
                initialValue: config.dataTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="索引表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("indexTableSpace22", {
                initialValue: config.indexTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username22", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password22", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
          </div>
        );
      case 31:
        return (
          <div>
            <FormItem label="JNDI源名称" {...this.formItemLayout1}>
              <Row gutter={8}>
                <Col span={14}>
                  {getFieldDecorator("databaseName31", {
                    initialValue: config.databaseName,
                    rules: [{ required: true, message: "请输入JNDI源名称" }]
                  })(
                    <Select>
                      {JNDIargs.map(index => {
                        return (
                          <SelectOption key={index.key} value={index.name}>
                            {index.name}
                          </SelectOption>
                        );
                      })}
                    </Select>
                  )}
                </Col>
                <Col span={10}>{this.jndiDataSource(31)}</Col>
              </Row>
            </FormItem>
          </div>
        );
      case 32:
        return (
          <div>
            <FormItem label="JNDI源名称" {...this.formItemLayout1}>
              <Row gutter={8}>
                <Col span={14}>
                  {getFieldDecorator("databaseName32", {
                    initialValue: config.databaseName,
                    rules: [{ required: true, message: "请输入JNDI源名称" }]
                  })(
                    <Select>
                      {JNDIargs.map(index => {
                        return (
                          <SelectOption key={index.key} value={index.name}>
                            {index.name}
                          </SelectOption>
                        );
                      })}
                    </Select>
                  )}
                </Col>
                <Col span={10}>{this.jndiDataSource(32)}</Col>
              </Row>
            </FormItem>
            <FormItem
              style={{ margin: "0px", marginLeft: "13%" }}
              {...this.formItemLayout1}
            >
              {getFieldDecorator("modifier32", {
                valuePropName: "checked",
                initialValue: config.modifier
              })(
                <Checkbox value="public" disabled>
                  Use Result Streaming Cursor
                </Checkbox>
              )}
            </FormItem>
          </div>
        );
      case 33:
        return (
          <div>
            <FormItem label="JNDI源名称" {...this.formItemLayout1}>
              <Row gutter={8}>
                <Col span={14}>
                  {getFieldDecorator("databaseName33", {
                    initialValue: config.databaseName,
                    rules: [{ required: true, message: "请输入JNDI源名称" }]
                  })(
                    <Select>
                      {JNDIargs.map(index => {
                        return (
                          <SelectOption key={index.key} value={index.name}>
                            {index.name}
                          </SelectOption>
                        );
                      })}
                    </Select>
                  )}
                </Col>
                <Col span={10}>{this.jndiDataSource(33)}</Col>
              </Row>
            </FormItem>
            <FormItem
              label="数据表空间"
              style={{ marginBottom: "9px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("dataTableSpace33", {
                initialValue: config.dataTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="索引表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("indexTableSpace33", {
                initialValue: config.indexTableSpace
              })(<Input />)}
            </FormItem>
          </div>
        );
      case 34:
        return (
          <div>
            <FormItem label="JNDI源名称" {...this.formItemLayout1}>
              <Row gutter={8}>
                <Col span={14}>
                  {getFieldDecorator("databaseName34", {
                    initialValue: config.databaseName,
                    rules: [{ required: true, message: "请输入JNDI源名称" }]
                  })(
                    <Select>
                      {JNDIargs.map(index => {
                        return (
                          <SelectOption key={index.key} value={index.name}>
                            {index.name}
                          </SelectOption>
                        );
                      })}
                    </Select>
                  )}
                </Col>
                <Col span={10}>{this.jndiDataSource(34)}</Col>
              </Row>
            </FormItem>
            <FormItem
              style={{ margin: "0px", marginLeft: "13%" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("useDoubleDecimalSeparator34", {
                valuePropName: "checked",
                initialValue: config.useDoubleDecimalSeparator
              })(<Checkbox>使用..作为模式跟表的分隔符</Checkbox>)}
            </FormItem>
          </div>
        );
      case 41:
        return (
          <div>
            <FormItem label="SID" {...this.formItemLayout2}>
              {getFieldDecorator("databaseName41", {
                initialValue: config.databaseName,
                rules: [{ required: true, message: "请输入SID" }]
              })(<Input />)}
            </FormItem>
            <FormItem
              label="数据表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("dataTableSpace41", {
                initialValue: config.dataTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="索引表空间"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("indexTableSpace41", {
                initialValue: config.indexTableSpace
              })(<Input />)}
            </FormItem>
            <FormItem
              label="用户名"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("username41", {
                initialValue: config.username
              })(<Input />)}
            </FormItem>
            <FormItem
              label="密码"
              style={{ marginBottom: "8px" }}
              {...this.formItemLayout2}
            >
              {getFieldDecorator("password41", {
                initialValue: config.password
                  ? strDec(
                      config.password,
                      config.name,
                      config.hostname,
                      config.port
                    )
                  : ""
              })(<Input type="password" />)}
            </FormItem>
          </div>
        );
    }
  }

  getType(value) {
    switch (value[1]) {
      case "0":
        switch (value[0]) {
          case "DB2":
            return 11;
          case "HIVE3":
            return 11;
          case "MARIADB":
            return 11;
          case "GREENPLUM":
            return 11;
          case "MYSQL":
            return 12;
          case "ORACLE":
            return 13;
          case "DM7":
            return 12;
          case "POSTGRESQL":
            return 11;
          case "MSSQL":
            return 14;
          case "SYBASE":
            return 11;
          case "GENERIC":
            return 15;
          case "HBASETABLE":
            return 15;
        }
      case "1":
        switch (value[0]) {
          case "MSACCESS":
            return 21;
          case "MARIADB":
            return 21;
          case "GREENPLUM":
            return 21;
          case "DB2":
            return 21;
          case "MYSQL":
            return 21;
          case "GENERIC":
            return 21;
          case "HBASETABLE":
            return 21;
          case "ORACLE":
            return 22;
          case "DM7":
            return 22;
          case "POSTGRESQL":
            return 21;
          case "MSSQL":
            return 21;
          case "SYBASE":
            return 21;
        }
      case "4":
        switch (value[0]) {
          case "DB2":
            return 31;
          case "MARIADB":
            return 31;
          case "GREENPLUM":
            return 31;
          case "MYSQL":
            return 32;
          case "GENERIC":
            return 32;
          case "HBASETABLE":
            return 32;
          case "ORACLE":
            return 33;
          case "DM7":
            return 33;
          case "POSTGRESQL":
            return 31;
          case "MSSQL":
            return 34;
          case "SYBASE":
            return 31;
        }
      case "2":
        switch (value[0]) {
          case "ORACLE":
            return 41;
          case "DM7":
            return 41;
        }
      default:
        return 21;
    }
  }

  residences = [
    {
      value: "MSACCESS",
      label: "MS Access",
      children: [
        {
          value: "1",
          label: "ODBC"
        }
      ]
    },
    {
      value: "DB2",
      label: "IBM DB2",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "HIVE3",
      label: "Hadoop Hive 2",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        }
      ]
    },
    {
      value: "MYSQL",
      label: "My SQL",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "MARIADB",
      label: "MariaDB",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "GREENPLUM",
      label: "Greenplum",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "GENERIC",
      label: "Generic database",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "HBASETABLE",
      label: "Phoenix Hbase Table",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "ORACLE",
      label: "Oracle",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "2",
          label: "OCI"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "DM7",
      label: "DM",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        }
      ]
    },
    {
      value: "POSTGRESQL",
      label: "PostgreSQL",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "MSSQL",
      label: "MS SQL Server",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    },
    {
      value: "SYBASE",
      label: "Sybase",
      children: [
        {
          value: "0",
          label: "Native(JDBC)"
        },
        {
          value: "1",
          label: "ODBC"
        },
        {
          value: "4",
          label: "JNDI"
        }
      ]
    }
  ];

  cascaderChange(value) {
    this.setState({ value });
    this.requestJNDI(value);
  }

  formItemLayout1 = {
    labelCol: {
      span: 4,
      lg: { span: 4 },
      xl: { span: 3 }
    },
    wrapperCol: {
      span: 10,
      xl: { span: 8 }
    }
  };

  formItemLayout2 = {
    labelCol: {
      span: 4,
      lg: { span: 4 },
      xl: { span: 3 }
    },
    wrapperCol: {
      span: 8,
      xl: { span: 7 }
    }
  };

  formItemLayout3 = {
    labelCol: { span: 24 },
    wrapperCol: { span: 5 }
  };

  handleTabsClick(activeKey) {
    this.setState({ activeKey });
  }
  handleChange(e) {
    this.setState({
      disabled: !e.target.checked
    });
  }

  /*格式化表格*/
  formatTable(obj, obj1) {
    let args = [];
    for (let index of obj) {
      let item = {};
      for (let index1 of obj1) {
        if (index[index1] && index[index1].toString().trim()) {
          item[index1] = index[index1];
        }
      }
      if (JSON.stringify(item) != "{}") {
        args.push(item);
      }
    }
    return args;
  }

  handleCreate(e) {
    e.preventDefault();
    const form = this.props.form;
    const { save_db_connection } = this.props.transheader;
    const { options } = this.props.resourcecontent.config;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      let obj = {};
      obj.name = values.name;
      obj.type = values.residence[0];
      obj.access = values.residence[1];
      obj.hostname = values["hostname" + this.getType(values.residence)];
      obj.port = values["port" + this.getType(values.residence)];
      obj.url = values.url;
      obj.driver = values.driver;
      obj.dataTableSpace =
        values["dataTableSpace" + this.getType(values.residence)];
      obj.indexTableSpace =
        values["indexTableSpace" + this.getType(values.residence)];
      obj.sqlServerInstance =
        values["sqlServerInstance" + this.getType(values.residence)];
      obj.useIntegratedSecurity =
        values["useIntegratedSecurity" + this.getType(values.residence)];
      obj.useDoubleDecimalSeparator =
        values["useDoubleDecimalSeparator" + this.getType(values.residence)];
      obj.databaseName =
        values["databaseName" + this.getType(values.residence)];
      obj.username = values["username" + this.getType(values.residence)];

      if (
        values["password" + this.getType(values.residence)] &&
        values["password" + this.getType(values.residence)].length > 0
      ) {
        obj.password = strEnc(
          values["password" + this.getType(values.residence)],
          values.name,
          values["hostname" + this.getType(values.residence)],
          values["port" + this.getType(values.residence)]
        );
      }

      let sendFields = [];
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let args = ["optKey", "optVal"];
          sendFields = this.formatTable(
            this.refs.editTable.state.dataSource,
            args
          );
        }
      } else {
        if (options) {
          sendFields = options;
        }
      }
      obj.options = sendFields;

      save_db_connection(obj).then(res => {
        const { code } = res.data;
        if (code === "200") {
          message.success("保存成功");
          this.handleCancel();
        }
      });
    });
  }

  handleCancel() {
    const form = this.props.form;
    form.resetFields();
    const { location, router } = this.props;
    router.push({ ...location, query: {} });
  }

  dataSource = [
    {
      key: 1,
      optKey: "",
      optVal: ""
    },
    {
      key: 2,
      optKey: "",
      optVal: ""
    },
    {
      key: 3,
      optKey: "",
      optVal: ""
    },
    {
      key: 4,
      optKey: "",
      optVal: ""
    },
    {
      key: 5,
      optKey: "",
      optVal: ""
    }
  ];
  columns = [
    {
      title: "命名参数",
      dataIndex: "optKey",
      editable: true,
      width: "50%"
    },
    {
      title: "值",
      dataIndex: "optVal",
      editable: true,
      width: "50%"
    }
  ];

  columns1 = [
    {
      title: "初始大小",
      dataIndex: "startNum",
      margin: "0 8px",
      editable: true
    },
    {
      title: "最大空闲空间",
      dataIndex: "endNum",
      editable: true
    }
  ];
  dataSource1 = [
    {
      key: 1,
      startNum: "",
      endNum: ""
    }
  ];
  columns2 = [
    {
      title: "参数名",
      dataIndex: "name",
      width: "274px"
    },
    {
      title: "值",
      dataIndex: "num",
      editable: true
    }
  ];
  dataSource2 = [
    {
      key: 1,
      name: "defaultAutoCommit",
      num: "false"
    },
    {
      key: 2,
      name: "defaultReadOnly",
      num: ""
    },
    {
      key: 3,
      name: "defaultTransactionIsolation",
      num: ""
    },
    {
      key: 4,
      name: "defaultCatalog",
      num: ""
    },
    {
      key: 5,
      name: "initialSize",
      num: 0
    },
    {
      key: 6,
      name: "maxActive",
      num: 8
    },
    {
      key: 7,
      name: "maxIdle",
      num: 8
    },
    {
      key: 8,
      name: "minIdle",
      num: 0
    },
    {
      key: 9,
      name: "maxWait",
      num: -1
    },
    {
      key: 10,
      name: "validationQuery",
      num: ""
    },
    {
      key: 11,
      name: "testOnBorrow",
      num: "true"
    },
    {
      key: 12,
      name: "testOnReturn",
      num: "false"
    },
    {
      key: 13,
      name: "testWhileIdle",
      num: "false"
    },
    {
      key: 14,
      name: "timeBetweenEvictionRunsMillis",
      num: ""
    },
    {
      key: 15,
      name: "poolPreparedStatements",
      num: "false"
    },
    {
      key: 16,
      name: "maxOpenPreparedStatements",
      num: -1
    },
    {
      key: 17,
      name: "accessToUnderlyingConnectAllowed",
      num: "false"
    },
    {
      key: 18,
      name: "removeAbandoned",
      num: "false"
    },
    {
      key: 19,
      name: "removeAbandonedTimeout",
      num: 300
    },
    {
      key: 20,
      name: "logAbandoned",
      num: "false"
    }
  ];
  dataSource3 = [
    {
      key: 1,
      ID: "",
      name: "",
      port: "",
      dataName: "",
      username: "",
      password: ""
    },
    {
      key: 2,
      ID: "",
      name: "",
      port: "",
      dataName: "",
      username: "",
      password: ""
    },
    {
      key: 3,
      ID: "",
      name: "",
      port: "",
      dataName: "",
      username: "",
      password: ""
    },
    {
      key: 4,
      ID: "",
      name: "",
      port: "",
      dataName: "",
      username: "",
      password: ""
    },
    {
      key: 5,
      ID: "",
      name: "",
      port: "",
      dataName: "",
      username: "",
      password: ""
    }
  ];
  columns3 = [
    {
      title: "分区ID",
      dataIndex: "ID",
      editable: true
    },
    {
      title: "主机名称",
      dataIndex: "name",
      editable: true
    },
    {
      title: "端口",
      dataIndex: "port",
      editable: true
    },
    {
      title: "数据库名称",
      dataIndex: "dataName",
      editable: true
    },
    {
      title: "用户名",
      dataIndex: "username",
      editable: true
    },
    {
      title: "密码",
      dataIndex: "password",
      editable: true
    }
  ];

  handleConfirm = (rule, value, callback) => {
    const { check_dbname_result } = this.props.transheader;
		const { config } = this.props.resourcecontent;
		console.log(config,"配置");
    if (value && value.trim() && value === config.name) {
      callback();
    } else {
      if (value && value.trim()) {
        if (Timer) {
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(() => {
          check_dbname_result({ name: value }).then(res => {
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

  /*针对JDNI数据源的操作*/
  /*JNDI数据源*/
  jndiDataSource(num) {
    return (
      <ButtonGroup>
        <Button onClick={this.handleNewJNDI.bind(this)}>新建</Button>
        <Button
          onClick={() => {
            this.handleEditJNDI(num);
          }}
        >
          编辑
        </Button>
      </ButtonGroup>
    );
  }

  /*更新JNDI列表*/
  requestJNDI(value) {
    const { dispatch } = this.props;
    if (value[1] === "4") {
      dispatch({
        type: "resourcecontent/getJndiList",
        payload: {
          type: value[0]
        }
      });
    }
  }

  /*编辑JNDI源*/
  handleEditJNDI(num) {
    const { dispatch, form } = this.props;
    const { getFieldValue } = form;
    const { value } = this.state;

    const name = getFieldValue("databaseName" + num);
    if (value) {
      dispatch({
        type: "resourcecontent/editJndi",
        payload: {
          type: value[0],
          name: name,
          visible: true
        }
      });
    } else {
      message.info("请先选择要编辑的JNDI源名称！");
    }
  }

  /*新建JNDI源*/
  handleNewJNDI() {
    const { dispatch } = this.props;
    const { value } = this.state;

    dispatch({
      type: "resourcecontent/changeStatus",
      payload: {
        JNDIconfig: {
          type: value[0]
        },
        visible: true
      }
    });
  }

  render() {
    const { config } = this.props.resourcecontent;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { canEdit } = this.props;

    const setDisabled = () => {
      return !getFieldValue("userPool");
    };

    const setDisabled1 = () => {
      return !getFieldValue("userCluster");
    };

    return (
      <div id="data_detail">
        <Row className={Style.ColList}>
          <Col
            span={2}
            className={this.state.activeKey === "1" ? Style.click : ""}
            onClick={() => {
              this.handleTabsClick("1");
            }}
          >
            {" "}
            一般
          </Col>
          <Col
            span={2}
            className={this.state.activeKey === "2" ? Style.click : ""}
            onClick={() => {
              this.handleTabsClick("2");
            }}
          >
            高级
          </Col>
          <Col
            span={2}
            className={this.state.activeKey === "3" ? Style.click : ""}
            onClick={() => {
              this.handleTabsClick("3");
            }}
          >
            选项
          </Col>
          <Col
            span={2}
            className={this.state.activeKey === "4" ? Style.click : ""}
            onClick={() => {
              this.handleTabsClick("4");
            }}
          >
            连接池
          </Col>
          <Col
            span={2}
            className={this.state.activeKey === "5" ? Style.click : ""}
            onClick={() => {
              this.handleTabsClick("5");
            }}
          >
            集群
          </Col>
        </Row>

        <Form id="datasystem_form">
          <Tabs type="card" activeKey={this.state.activeKey}>
            <TabPane tab="一般" key="1">
              <FormItem
                label="连接名称"
                style={{ marginTop: "20px" }}
                {...this.formItemLayout2}
              >
                {getFieldDecorator("name", {
                  initialValue: config.name,
                  rules: [
                    { required: true, message: "请输入连接名称" },
                    {
                      validator: this.handleConfirm,
                      message: "连接名称已存在，请更改!"
                    }
                  ]
                })(<Input disabled={config.name ? true : false} />)}
              </FormItem>
              <FormItem label="连接类型/方式" {...this.formItemLayout2}>
                {getFieldDecorator("residence", {
                  initialValue: this.state.value
                })(
                  <Cascader
                    options={this.residences}
                    onChange={value => this.cascaderChange(value)}
                  />
                )}
              </FormItem>
              {this.getModel(config)}
            </TabPane>
            <TabPane tab="高级" key="2">
              <FormItem
                {...this.formItemLayout}
                style={{
                  marginBottom: "0px",
                  marginTop: "20px",
                  marginLeft: "50px"
                }}
              >
                {getFieldDecorator("boolType", {
                  valuePropName: "checked",
                  initialValue: config.boolType
                })(<Checkbox>支持布尔数据类型</Checkbox>)}
              </FormItem>
              <FormItem
                {...this.formItemLayout}
                style={{ marginBottom: "0px", marginLeft: "50px" }}
              >
                {getFieldDecorator("timestamp", {
                  valuePropName: "checked",
                  initialValue: config.timestamp
                })(<Checkbox>Supports the timestamp data type</Checkbox>)}
              </FormItem>
              <FormItem
                {...this.formItemLayout}
                style={{ marginBottom: "0px", marginLeft: "50px" }}
              >
                {getFieldDecorator("idBracket", {
                  valuePropName: "checked",
                  initialValue: config.idBracket
                })(<Checkbox>标识符使用引号括起来</Checkbox>)}
              </FormItem>
              <FormItem
                {...this.formItemLayout}
                style={{ marginBottom: "0px", marginLeft: "50px" }}
              >
                {getFieldDecorator("lowercase", {
                  valuePropName: "checked",
                  initialValue: config.lowercase
                })(<Checkbox>强制标识符使用小写字母</Checkbox>)}
              </FormItem>
              <FormItem
                {...this.formItemLayout}
                style={{ marginBottom: "0px", marginLeft: "50px" }}
              >
                {getFieldDecorator("uppercase", {
                  valuePropName: "checked",
                  initialValue: config.uppercase
                })(<Checkbox>强制标识符使用大学字母</Checkbox>)}
              </FormItem>
              <FormItem
                {...this.formItemLayout}
                style={{ marginBottom: "0px", marginLeft: "50px" }}
              >
                {getFieldDecorator("reserved", {
                  valuePropName: "checked",
                  initialValue: config.reserved
                })(<Checkbox>Preserve case of reserved words</Checkbox>)}
              </FormItem>
              <FormItem
                label="默认名称(在没有其他模式名时使用)"
                {...this.formItemLayout3}
                style={{ marginBottom: "0px", marginLeft: "50px" }}
              >
                {getFieldDecorator("defaultName", {
                  initialValue: config.defaultName
                })(<Input />)}
              </FormItem>
              <FormItem
                label="请输入连接成功后要执行的SQL语句,用分号(;)隔开"
                {...this.formItemLayout}
                style={{ marginBottom: "8px", marginLeft: "50px" }}
              >
                {getFieldDecorator("inputSQL", {
                  initialValue: config.inputSQL
                })(<TextArea style={{ width: "400px" }} />)}
              </FormItem>
            </TabPane>
            <TabPane tab="选项" key="3">
              <div style={{ padding: "20px" }}>
                <EditTable
                  extendDisabled={true}
                  columns={this.columns}
                  dataSource={this.dataSource}
                  size={"small"}
                  ref="editTable"
                  count={6}
                />
              </div>
            </TabPane>
            <TabPane tab="连接池" key="4">
              <div style={{ padding: "20px" }}>
                <FormItem style={{ margin: "0 5px" }}>
                  {getFieldDecorator("userPool", {
                    valuePropName: "checked",
                    initialValue: config.userPool
                  })(<Checkbox>使用连接池</Checkbox>)}
                </FormItem>
                <EditTable
                  extendDisabled={true}
                  size={"small"}
                  disabled={setDisabled()}
                  tableStyle="editTableStyle3"
                  columns={this.columns1}
                  dataSource={this.dataSource1}
                  pagination={false}
                />
                <EditTable
                  extendDisabled={true}
                  rowSelection={true}
                  size={"small"}
                  disabled={setDisabled()}
                  tableStyle="editTableStyle2"
                  columns={this.columns2}
                  dataSource={this.dataSource2}
                  scroll={{ y: 260 }}
                  pagination={false}
                />
                <Button
                  size={"small"}
                  disabled={setDisabled()}
                  style={{
                    marginTop: "10px",
                    marginRight: "5px",
                    float: "right"
                  }}
                >
                  恢复默认设置
                </Button>
              </div>
            </TabPane>
            <TabPane tab="集群" key="5">
              <div style={{ padding: "20px" }}>
                <FormItem style={{ margin: "0 5px" }}>
                  {getFieldDecorator("userCluster", {
                    valuePropName: "checked",
                    initialValue: config.userCluster
                  })(<Checkbox>使用集群</Checkbox>)}
                </FormItem>
                <div
                  style={{
                    marginTop: "20px",
                    marginBottom: "10px",
                    marginLeft: "5px"
                  }}
                >
                  命名参数：
                </div>
                <EditTable
                  extendDisabled={true}
                  disabled={setDisabled1()}
                  size={"small"}
                  columns={this.columns3}
                  dataSource={this.dataSource3}
                />
              </div>
            </TabPane>
          </Tabs>
          <Row className={Style.BottomRow}>
            <Col span={12} style={{ textAlign: "right" }}>
              <Empower api={canEdit ? "/db/editDbConnection.do" : ""}>
                <Button
                  onClick={this.handleCreate.bind(this)}
                  type="primary"
                  htmlType="submit"
                >
                  保存
                </Button>
              </Empower>
            </Col>
            <Col span={12} style={{ textAlign: "center" }}>
              <Button onClick={this.handleCancel.bind(this)}>取消</Button>
            </Col>
          </Row>
        </Form>
        <JNDIModal />
      </div>
    );
  }
}

const DataSystemModel = Form.create()(DataDetail);

export default withRouter(
  connect(({ resourcecontent, transheader }) => ({
    resourcecontent,
    transheader
  }))(DataSystemModel)
);
