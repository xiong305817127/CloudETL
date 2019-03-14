import React from 'react';
import { Form, Row, Col, Button, Collapse } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter, hashHistory } from 'react-router';

const Panel = Collapse.Panel;

const index = ({  serveroptian }) => {

  const {  name, loading, subNo, datasource,datasource1,datasource2 } = serveroptian;

  const columns = [
    {
      title: 'HTTP请求地址',
      key: 'webUrl',
      dataIndex: 'webUrl',
      width: "50%",

    }, {
      title: '服务参数',
      key: 'subKey',
      dataIndex: 'subKey',
      width: "50%",

    }
  ];


  const columns1 = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: "20%"
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: "15%"
    }, {
      title: '是否必须',
      dataIndex: 'file',
      key: 'file',
      width: "28%"
    }, {
      title: '描述',
      dataIndex: 'dateli',
      key: 'dateli',
      width: "28%"
    }];

  const columns2 = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: "20%"
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: "15%"
    }, {
      title: '示例值',
      dataIndex: 'size',
      key: 'size',
      width: "28%"
    }, {
      title: '描述',
      dataIndex: 'dateli',
      key: 'dateli',
      width: "28%"
    }];

  const getBsck = () => {
    hashHistory.goBack();
  }
  return (
    <div style={{ margin: "20" }}>
      <Row style={{ margin: "20px 0px 20px 0px" }} >
        <Col offset={1}><Button type="primary" value="返回" onClick={getBsck}>返回</Button></Col>
      </Row>

      <Row style={{ margin: "10px 0px 10px 0px" }} >
        <Col span={4} offset={1}>API名称：{name}</Col>
        <Col span={5}>服务编码：{subNo}</Col>
      </Row>

      <Collapse defaultActiveKey={['1']}>
        <Panel header="公共参数" key="1">
          <Row>
            <Col>
              <TableList
                showIndex
                loading={loading}
                rowKey='__index'
                columns={columns}
                dataSource={datasource}
                pagination={false}
              />
            </Col>
          </Row>
        </Panel>
        <Panel header="请求参数" key="2">
          <Row>
            <Col>
              <TableList
                showIndex
                loading={loading}
                rowKey='__index'
                columns={columns1}
                dataSource={datasource1}
                pagination={false}
              />
            </Col>
          </Row>
        </Panel>
        <Panel header="返回参数" key="3">
          <Row>
            <Col>
              <TableList
                showIndex
                loading={loading}
                rowKey='__index'
                columns={columns2}
                dataSource={datasource2}
                pagination={false}
              />
            </Col>
          </Row>
        </Panel>
      </Collapse>
    </div>
  )
}

export default connect(({ serveroptian 
}) => ({ serveroptian }))(index);