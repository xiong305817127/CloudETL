import React from 'react';
import {Select, Table, Input, Button, Row, Col, message} from 'antd';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import { DEFAULT_PAGE_SIZE } from 'constants';
import TableList from 'components/TableList';
import { doReapply, undoApply } from 'services/myApplication';
import Search from 'components/Search';
import Empower from 'components/Empower';
import { getLabelByTreeValue } from 'utils/metadataTools';
import Style from './style.css';
import Modal from 'components/Modal';

const Option = Select.Option;
const Column = Table.Column;

// 状态选项
const statusOptions = [
  { label: '审核中', value: 1 },
  { label: '通过', value: 2 },
  { label: '未通过', value: 3 },
  { label: '已回收', value: 4 },
  { label: '已撤回', value: 5 },
];

class MyApplication extends React.Component {
  //1.初始化
  constructor(props){
    super(props);
    this.state = {
      selectedRowKeys: [],
      selectedRows: [],
    };
  }

  columns = [
    {
      title: '所申请的数据资源',
      dataIndex: 'resourceName',
    }, {
      title: '资源类型',
      dataIndex: 'resourceType',
      render: text => text === 1 ? '数据表' : '文件目录',
    }, {
      title: '资源所属组织',
      dataIndex: 'resourceDept',
      render: (text) => {
        const { allDepartmentsOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, allDepartmentsOptions) || '';
      },
    }, {
      title: '申请的权限',
      dataIndex: 'authValue',
      render: (text) => {
        const { permits } = this.props.resourcesCommonChange;
        const list = [...permits.tableOptions, ...permits.fileOptions];
        const names = [];
        list.forEach(item => {
          // 和运算，取出权限信息
          if (item.value & text) {
            names.push(item.label);
          }
        });
        return names.join('、');
      }
    }, {
      title: '申请日期',
      dataIndex: 'createtime',
      width: '160px',
    }, {
      title: '审批人',
      dataIndex: 'auditusr',
      width: '160px',
    }, {
      title: '审批状态',
      dataIndex: 'status',
      width: '100px',
      render: (text) => {
        let label;
        statusOptions.some((item) => {
          if (item.value == text) {
            label = item.label;
            return true;
          }
          return false;
        });
        return label;
      },
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'resourcesCommonChange/getDepartments' });
    dispatch({ type: 'resourcesCommonChange/findOrgnazation' });
    dispatch({ type: 'resourcesCommonChange/getPermitsOptions' });
    this.mergeStates(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.mergeStates(nextProps);
  }

  // 合并所需状态
  mergeStates(props) {
    const { myApplication } = props;
    this.setState({
      myApplication,
    });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 搜索
  onSearch(val) {
    const { router, location: { pathname, query } } = this.props;
    query.keyword = val;
    router.push({ pathname, query });
  }

  // 状态过滤
  handleChangeFilter(val) {
    const { router, location: { pathname, query } } = this.props;
    query.status = val;
    if (val == 0) { // 查询全部
      delete query.status;
    }
    router.push({ pathname, query });
  }

  // 重新申请
  async handleClickReapply() {
    const { dispatch, location: { query } } = this.props;
    const { selectedRowKeys } = this.state;
    const { data: { code, msg } } = await doReapply(selectedRowKeys);
    if (code == 0) {
      message.success('已重新申请');
      dispatch({
        type: 'myApplication/getList',
        payload: query,
      });
      this.setState({ selectedRowKeys: [] });
    }
  }

  // 撤回申请
  handleClickUndoApply() {
    Modal.confirm({
      content: '确定要撤回申请吗',
      onOk: async () => {
        const { dispatch, location: { query } } = this.props;
        const { selectedRowKeys } = this.state;
        const { data: { code, msg } } = await undoApply(selectedRowKeys);
        if (code == 0) {
          message.success('已撤回申请');
          dispatch({
            type: 'myApplication/getList',
            payload: query,
          });
          this.setState({ selectedRowKeys: [] });
        }
      }
    });
  }

  // 判断重新申请按钮是否禁用
  isDisabledOfReapplyButton() {
    const { selectedRows, selectedRowKeys } = this.state;
    if (selectedRowKeys.length === 0) return true;
    return selectedRows.some(item => item.status === 1 || item.status === 2);
  }

  // 判断撤回申请按钮是否禁用
  isDisabledOfRevokeButton() {
    const { selectedRows, selectedRowKeys } = this.state;
    if (selectedRowKeys.length === 0) return true;
    return selectedRows.some(item => item.status !== 1);
  }

  render() {
    const { selectedRowKeys, selectedRows } = this.state;
    const { location: { pathname, query }, myApplication } = this.props;
    // const pageSize = parseInt(query.pageSize) || DEFAULT_PAGE_SIZE;

    return (<div >
      <header className="padding_20">
        <Search
          defaultValue={query.keyword || ''}
          placeholder="可按数据资源名、资源所属组织进行模糊搜索"
          style={{ width: '500px' }}
          onSearch={value => this.onSearch(value)}
        />
      </header>
      <Row style={{margin:20}}>
        <Col span={12}>
          <Empower api="/myResourceController/batchToReApprove">
            <Button
              type="primary"
              disabled={this.isDisabledOfReapplyButton()}
              onClick={this.handleClickReapply.bind(this)}
            >重新申请</Button>
          </Empower>
          <Empower api="/myResourceController/batchToRevoke">
            <Button
              type="primary"
              disabled={this.isDisabledOfRevokeButton()}
              onClick={this.handleClickUndoApply.bind(this)}
              style={{marginLeft:10}}
            >撤回申请</Button>
          </Empower>
        </Col>
        <Col span={12} style={{textAlign: 'right'}}>
          <span>状态过滤：</span>
          <Select value={query.status || '0'} style={{ width: 120 }}
            onChange={this.handleChangeFilter.bind(this)}
          >
            <Option value="0">全部</Option>
            {statusOptions.map((item, index) => (
              <Option key={index} value={String(item.value)}>{item.label}</Option>
            ))}
          </Select>
        </Col>
      </Row>

      <TableList
        rowKey="id"
        columns={this.columns}
        dataSource={myApplication.rows}
        showIndex
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys,
        }}
        pagination={{total: myApplication.total}}
        style={{margin: 20}}
      />
    </div>);
  }
}

export default connect(({ system, myApplication, resourcesCommonChange }) => ({
  system,
  myApplication,
  resourcesCommonChange,
}))(withRouter(MyApplication));
