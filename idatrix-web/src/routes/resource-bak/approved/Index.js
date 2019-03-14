import React from 'react';
import { Select, Input, Button, Row, Col, message } from 'antd';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import { DEFAULT_PAGE_SIZE } from 'constants';
import Search from 'components/Search';
import TableList from 'components/TableList';
import Empower from 'components/Empower';
import { getLabelByTreeValue } from 'utils/metadataTools';
import { takeBackPermits } from 'services/approved';
import Style from './style.css';
import Modal from 'components/Modal';

const Option = Select.Option;

// 状态选项
const statusOptions = [
  // { label: '审核中', value: 1 },
  { label: '通过', value: 2 },
  { label: '未通过', value: 3 },
  { label: '已回收', value: 4 },
  // { label: '已撤回', value: 5 },
];

class Approved extends React.Component {
  // 1.初始化
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [],
      selectedRows: [],
    };
  }

  columns = [
    {
      title: '申请人',
      dataIndex: 'creator',
      width: '160px',
    }, /*{
      title: '申请人组织',
      dataIndex: 'applyDept',
    },*/ {
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
    const { approvedModel } = props;
    this.setState({
      approvedModel,
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

  // 回收权限
  handleClickTakeBack() {
    const { router} = this.props;

    Modal.confirm({
      content: '确定要回收权限吗',
      onOk: async () => {
        const { dispatch, location: { query } } = this.props;
        const { selectedRowKeys } = this.state;
        const { data: { code, msg } } = await takeBackPermits(selectedRowKeys);
        if (code == "200") {
          message.success('回收权限成功');
          dispatch({
            type: 'approvedModel/getList',
            payload: query,
          });
          this.setState({ selectedRowKeys: [] });
          router.push("/resources/approved")
        }
      }
    });
  }

  // 判断回收权限按钮是否禁用
  isDisabledOfRevokeButton() {
    const { selectedRows, selectedRowKeys } = this.state;
    if (selectedRowKeys.length === 0) return true;
    return selectedRows.some(item => item.status !== 2);
  }

  render() {
    const { selectedRowKeys } = this.state;
    const { approvedModel, location: { pathname, query } } = this.props;
    // const pageSize = parseInt(query.pageSize) || DEFAULT_PAGE_SIZE;

    return (<div>
      <header className="padding_20">
        <Search
          defaultValue={query.keyword || ''}
          placeholder="可按申请人、数据资源名、资源所属组织模糊搜索"
          style={{ width: '500px' }}
          onSearch={value => this.onSearch(value)}
        />
      </header>
      <Row style={{margin:20}}>
        <Col span={12}>
          <Empower api="/myResourceController/batchToReCycle">
            <Button
              type="primary"
              disabled={this.isDisabledOfRevokeButton()}
              onClick={this.handleClickTakeBack.bind(this)}
            >回收权限</Button>
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
        dataSource={approvedModel.rows}
        showIndex
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys,
        }}
        pagination={{total: approvedModel.total}}
        style={{margin: 20}}
      />
    </div>);
  }
}

export default connect(({ system, approvedModel, resourcesCommonChange }) => ({
  system,
  approvedModel,
  resourcesCommonChange,
}))(withRouter(Approved));
