import React from "react";
import { connect } from "dva";
import TableList from "../../../components/TableList";
import moment from "moment";
import { Form, Button, Row, Col, Input, Select, DatePicker } from "antd";
import { withRouter } from "dva/router";
import qs from "querystring";
import "./index.less";

const { RangePicker } = DatePicker;

const { Option } = Select;

const getPoType = type => {
  switch (type) {
    case 1:
      return "普通操作";
    case 2:
      return "登录";
    case 3:
      return "登出";
    default:
      return "普通操作";
  }
};

const LoginLogo = ({ logoManage, form, router, location }) => {
  const { dataSource, total, loading } = logoManage;
  const { getFieldDecorator } = form;
  const { pathname, query } = location;

  const columns = [
    {
      title: "服务名",
      dataIndex: "server",
      className: "td-center",
      width: "15%"
    },
    {
      title: "请求地址",
      dataIndex: "resource",
      className: "td-center",
      width: "25%"
    },
    {
      title: "请求方式",
      dataIndex: "methodType",
      className: "td-center",
      width: 50
    },
    {
      title: "客户端地址",
      dataIndex: "clientIp",
      className: "td-center",
      width: 50
    },
    {
      title: "操作类型",
      dataIndex: "opType",
      className: "td-center",
      width: 50,
      render: text => {
        return getPoType(text);
      }
    },
    {
      title: "用户名",
      dataIndex: "userName",
      className: "td-center",
      width: 50
    },
    {
      title: "访问时间",
      dataIndex: "visitTime",
      className: "td-center",
      width: "15%",
      render: text => {
        return moment(text).format("YYYY-MM-DD HH:mm:ss");
      }
    },
    {
      title: "结果",
      dataIndex: "result",
      className: "td-center",
      width: 50,
      render: text => (text === "success" ? "成功" : "失败")
    }
  ];

  const handleClick = e => {
    e.preventDefault();
    form.validateFields((err, values) => {
      if (!err) {
        if (values.opType === "all") {
          delete values.opType;
        }
        if (values.result === "all") {
          delete values.result;
        }
        if (!values.userName.trim()) {
          delete values.userName;
        }
        if (values.date.length === 2) {
          values.loginDateStart = values.date[0].format("YYYY-MM-DD HH:mm:ss");
          values.loginDateEnd = values.date[1].format("YYYY-MM-DD HH:mm:ss");
        }
        delete values.date;
        router.push(`${pathname}?${qs.stringify(values)}`);
      }
    });
  };

  return (
    <div style={{ padding: 16 }}>
      <Form className="ant-advanced-search-form" style={{ marginBottom: 20 }}>
        <Row gutter={30}>
          <Col span={4} style={{ display: "block" }}>
            <Form.Item label="用户名" style={{ margin: 0 }}>
              {getFieldDecorator("userName", {
                initialValue: query.userName?query.userName:""
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col span={3}>
            <Form.Item label="操作类型" style={{ margin: 0 }}>
              {getFieldDecorator("opType", {
                initialValue: query.opType?query.opType:"all"
              })(
                <Select>
                  <Option value="all">全部</Option>
                  <Option value="1">普通操作</Option>
                  <Option value="2">登录</Option>
                  <Option value="3">登出</Option>
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col span={3}>
            <Form.Item label="结果" style={{ margin: 0 }}>
              {getFieldDecorator("result", {
                initialValue:query.result?query.result:"all"
              })(
                <Select>
                  <Option value="all">全部</Option>
                  <Option value="1">成功</Option>
                  <Option value="2">失败</Option>
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item label="日期区间" style={{ margin: 0 }}>
              {getFieldDecorator("date", {
                initialValue:query.loginDateStart && query.loginDateEnd?[moment(query.loginDateStart), moment(query.loginDateEnd)]:[]
              })(<RangePicker format="YYYY-MM-DD HH:mm:ss" />)}
            </Form.Item>
          </Col>
          <Col span={4} style={{ textAlign: "left" }}>
            <Button type="primary" onClick={handleClick}>
              查询
            </Button>
          </Col>
        </Row>
      </Form>
      <TableList
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        showIndex //序号
        pagination={{
          total //从后台获取totalCount赋值total，以及组件自动计算分页
        }}
      />
    </div>
  );
};

const Index = Form.create()(LoginLogo);

export default connect(({ logoManage }) => ({ logoManage }))(withRouter(Index));
