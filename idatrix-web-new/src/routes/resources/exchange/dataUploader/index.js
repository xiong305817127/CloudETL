import React from "react";
import { connect } from "dva";

import { Tabs, Form, Input, Icon, Row, Col, Button, Card } from "antd";
import styles from "./datauploader.less";

// 引入组件
import Table1 from "./components/table1";
import Table2 from "./components/table2";
import Table3 from "./components/table3";
import Tab2Form from "./components/tab2.form";

const TabPane = Tabs.TabPane;
const FormItem = Form.FormItem;
const Table1Btns = Table1.Btns;
const Table2Btns = Table2.Btns;

// tab1的form组件
const Tab1Form = ({ handleSubmit, form }) => {
  const { getFieldDecorator } = form;

  return (
    <Form onSubmit={handleSubmit} className={styles.formReset}>
      <Form.Item label="用户名">
        {getFieldDecorator("userName", {
          rules: [{ required: true, message: "请输入用户名" }]
        })(
          <Input
            prefix={<Icon type="user" style={{ color: "rgba(0,0,0,.25)" }} />}
            placeholder="Username"
          />
        )}
      </Form.Item>
      <Form.Item label="密码">
        {getFieldDecorator("password", {
          rules: [{ required: true, message: "请输入密码" }]
        })(
          <Input
            prefix={<Icon type="lock" style={{ color: "rgba(0,0,0,.25)" }} />}
            type="password"
            placeholder="Password"
          />
        )}
      </Form.Item>

      <Button type="primary" htmlType="submit">
        提交
      </Button>
    </Form>
  );
};

const BaseLayout = () => {
  const callback = (...rest) => {
    console.log(rest);
  };

  const handleSubmit = () => {};

  const Tab1FormCreated = Form.create()(Tab1Form);

  return (
    <div style={{ padding: 16 }}>
      <Tabs defaultActiveKey="2" onChange={callback}>
        <TabPane tab="获取用户认证令牌" key="1">
          <Row gutter={16}>
            <Col span={12}>
              <Tab1FormCreated handleSubmit={handleSubmit} />
            </Col>
            <Col span={24}>
              <p style={{ marginTop: 32 }}>返回值</p>
              <Input.TextArea />
            </Col>
          </Row>
        </TabPane>
        <TabPane tab="接口功能" key="2">
          <Row gutter={16}>
            <Col span={24}>
              <Tab2Form />
            </Col>
            <Col span={12}>
              <Table1Btns />
            </Col>
            <Col span={12}>
              <Table2Btns />
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Card
                size="small"
                title="目录分类字段"
                style={{ marginBottom: "16px" }}
              >
                <Table1 />
              </Card>
            </Col>
            <Col span={12}>
              <Card title="信息资源信息字段" size="small">
                <Table2 />
              </Card>
            </Col>
          </Row>
        </TabPane>
        <TabPane tab="市级目录分类列表" key="3">
          <Row gutter={16}>
            <Col span={12}>
              <Card
                size="small"
                title="目录分类字段"
                style={{ marginBottom: "16px" }}
              >
                <Table3 />
              </Card>
            </Col>
            <Col span={12}>
              <p>查询实际目列表下行接口</p>
              <Button type="primary">查询</Button>
            </Col>
          </Row>
        </TabPane>
      </Tabs>
    </div>
  );
};
export default connect()(() => {
  return <BaseLayout />;
});
