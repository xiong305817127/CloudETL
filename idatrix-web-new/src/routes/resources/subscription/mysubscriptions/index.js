import React from 'react';
import { Form, Row, Col, Input, DatePicker, Button, Select } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter, hashHistory } from 'react-router';
import Modal from 'components/Modal';
import { shareMethodArgs } from '../../constants.js';
import moment from 'moment';
import CheckView from '../../common/CheckView/index';

const { RangePicker } = DatePicker;
const FormItem = Form.Item;

const index = ({ form, dispatch, mysubscriptionsModel, location, router }) => {

  const { getFieldDecorator } = form;
  const { total, datasource, selectedRowKeys, loading, selectedRows, changeHistory, selectName, datasource1 } = mysubscriptionsModel;
  const { query } = location;

  console.log(total, '总数');
  console.log(datasource, '数据数组');

  const columns = [
    {
      title: '资源代码',
      key: 'code',
      dataIndex: 'code',
      width: '12%',
    }, {
      title: '资源名称',
      key: 'name',
      dataIndex: 'name',
      width: '12%',
    }, {
      title: '订阅编号',
      key: 'subNo',
      dataIndex: 'subNo',
      width: '10%',
    }, {
      title: '提供方',
      key: 'deptName',
      dataIndex: 'deptName',
      width: '10%',
    },,
    {
      title: '申请日期',
      key: 'applyDate',
      dataIndex: 'applyDate',
      width: '10%',
    },
    {
      title: '交换方式',
      key: 'shareMethod',
      dataIndex: 'shareMethod',
      width: '11%',
      render: (text, record) => {

        return (<span>{shareMethodArgs[text].title}</span>);
      },
    },
    {
      title: '状态',
      key: 'subscribeStatus',
      dataIndex: 'subscribeStatus',
      width: '5%',
    }, {
      title: '审批人',
      key: 'approver',
      dataIndex: 'approver',
      width: '5%',
    }, {
      title: '截止日期',
      key: 'endTime',
      dataIndex: 'endTime',
      width: '8%',
    }, {
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      render: (text, record) => {
        const { dbShareMethod, shareMethod, subscribeStatus } = record;
        const method = shareMethod;

        return (
          <div>
            <a style={{ fontSize: 14 }} onClick={() => { handleCheckViewClick(record); }} >查看</a>&nbsp;&nbsp;
            <a style={{ fontSize: 14 }} onClick={() => { handleCheckView(record); }} >订阅详情</a>&nbsp;&nbsp;
            {
                 shareMethod === 3 && dbShareMethod === 0 ? null : subscribeStatus === '订阅成功' ? <a style={{ fontSize: 14 }} onClick={() => { handleOprater(record); }}>{shareMethodArgs[method].oprater}</a> : null
              }
          </div>
        );
      },
    },
  ];

  // 查看按钮
  const handleCheckViewClick = (record) => {
    dispatch({
      type: 'checkview/getEditResource',
      payload: { id: record.resourceId },
    });
  };

  // 订阅详情
  const handleCheckView = (record) => {
    hashHistory.push(`/resources/subscription/mysubscriptions/DetailsList/${record.id}`);
  };

  const handleOprater = (record) => {
    const { dbShareMethod, shareMethod } = record;
    let method = shareMethod;
    if (shareMethod === 3 && dbShareMethod === 2) {
      method = 4;
    }
    // 文件下载界面
    if (method === 2) {
      hashHistory.push(`/resources/subscription/mysubscriptions/downloadfile/${record.resourceId}`);
      // 数据库服务共享方式
    } else if (method === 4) {
      dispatch({
        type: 'serveroptian/getList',
        payload: { id: record.id, name: record.name, subNo: record.subNo },
      });
      hashHistory.push(`/resources/subscription/mysubscriptions/dataserver/${record.id}`);
      // 服务共享方式
    } else {
      dispatch({
        type: 'mysubscriptionsModel/getHistory',
        selectName: record.name,
        payload: {
          taskId: record.subNo,
        },
      });
    }
  };

  const columns1 = [
    {
      title: '交换任务',
      dataIndex: 'etlRunningId',
      key: 'etlRunningId',
      width: '20%',
    }, {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: '15%',
    }, {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      width: '28%',
    }, {
      title: '结束时间',
      dataIndex: 'endTime',
      key: 'endTime',
      width: '28%',
    }, {
      title: '数据量',
      dataIndex: 'dataCount',
      key: 'dataCount',
      width: '10%',
    }];

  const handleHistoryClick = () => {
    dispatch({
      type: 'mysubscriptionsModel/save',
      payload: {
        selectName: [],
        datasource1: [],
        changeHistory: false,
      },
    });
  };

  // 查询
  const handleSearch = () => {
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      for (const index of Object.keys(values)) {
        if (index === 'date') {
          if (values[index] && values[index].length > 0) {
            query.applyStartTime = values[index][0].format('YYYY-MM-DD');
            query.applyEndTime = values[index][1].format('YYYY-MM-DD');
          } else {
            delete query.applyStartTime;
            delete query.applyEndTime;
          }
        } else if (values[index]) {
          query[index] = values[index];
        } else {
          delete query[index];
        }
      }
      query.page = 1;
      router.push({ ...location, query });
    });
  };

  const formItemLayout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
  };

  // 删除数据
  const handleBatchAgree = () => {
    if (selectedRows.length === 0) return;

    const args = [];
    for (const index of selectedRows) {
      args.push(index.id);
    }

    dispatch({ type: 'releaseApproval/getBatchProcess', payload: { ids: args.join() } });
  };

  const onChangeAllSelect = (e, record) => {
    dispatch({ type: 'releaseApproval/save', payload: { selectedRowKeys: e, selectedRows: record } });
  };

  const dateFormat = 'YYYY-MM-DD';


  const { name, code, shareMethod, deptName, subStatus, date, applyStartTime, applyEndTime } = query;

  return (
    <div style={{ margin: 20 }}>
      <Form className="btn_std_group">
        <Row gutter={20} >
          <Col span={8}>
            <FormItem label={'资源名称'} {...formItemLayout}>
              {getFieldDecorator('name', {
                initialValue: name || '',
              })(
                <Input />,
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={'资源代码'} {...formItemLayout}>
              {getFieldDecorator('code', {
                 initialValue: code || '',
               })(
                <Input />,
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={'交换方式'} {...formItemLayout}>
              {getFieldDecorator('shareMethod', {
                initialValue: shareMethod || '',
              })(
                <Select>
                  <Option value="" key="all">全部</Option>
                  <Option value="db" key="1">交换平台-数据库</Option>
                  <Option value="file" key="2">交换平台-文件下载</Option>
                  <Option value="service" key="3">交换平台-服务</Option>
                </Select>,
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={'提供方名称'} {...formItemLayout}>
              {getFieldDecorator('deptName', {
                initialValue: deptName || '',
              })(
                <Input />,
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={'订阅状态'} {...formItemLayout}>
              {getFieldDecorator('subStatus', {
                initialValue: subStatus || '',
              })(
                <Select>
                  <Option value="" key="all">全部</Option>
                  <Option value="wait_approve" key="wait_approve">待审核</Option>
                  <Option value="success" key="success">订阅成功</Option>
                  <Option value="failed" key="failed">已拒绝</Option>
                </Select>,
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={'申请日期'} {...formItemLayout}>
              {getFieldDecorator('date', {
                 initialValue: applyStartTime && applyEndTime ? [moment(applyStartTime, dateFormat), moment(applyEndTime, dateFormat)] : [],
               })(
                <RangePicker format={dateFormat} disabledDate={curr => !(curr && curr < moment().endOf('day'))} />,
              )}
            </FormItem>
          </Col>
          {/* <Col span={7} style={{ display:'block'}}>
            <FormItem label={"申请日期"}  {...formItemLayout}>
              {getFieldDecorator("date",{
                initialValue:date?moment(date, dateFormat):null,
              })(
                 <DatePicker format={dateFormat} />
              )}
            </FormItem>
          </Col>*/}
          <Col span={24} className="search_btn">
            <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
          </Col>
        </Row>
      </Form>
      <div style={{ marginTop: 20 }}>
        <TableList
          showIndex
          loading={loading}
          rowKey="__index"
          columns={columns}
          dataSource={datasource}
          pagination={{ total }}
          rowSelection={{
              onChange: (e, record) => { onChangeAllSelect(e, record); },
              selectedRowKeys,
            }}
          />
      </div>
      <Modal
        visible={changeHistory}
        title={`交换历史(${selectName})`}
        onOk={handleHistoryClick}
        onCancel={handleHistoryClick}
        footer={[<Button type="primary" key="historyModal" onClick={handleHistoryClick}>确定</Button>]}
        width={600}
        >
        <TableList
          rowKey="__index"
          columns={columns1}
          dataSource={datasource1}
          pagination={false}
          scroll={{ y: 300 }}
          />
      </Modal>
      <CheckView />
    </div>
  );
};

export default connect(({ mysubscriptionsModel, serveroptian }) => ({ mysubscriptionsModel, serveroptian }))(withRouter(Form.create()(index)));
