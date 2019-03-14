import React from 'react';
import { Select, Button, Row, Col, message } from 'antd';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import Search from 'components/Search';
import TableList from 'components/TableList';
import Empower from 'components/Empower';
import { getLabelByTreeValue } from 'utils/metadataTools';
import { passYes, passNo } from 'services/unapproved';
import Modal from 'components/Modal';

const Option = Select.Option;

class Unapproved extends React.Component {
  constructor(props){
    super(props);
    this.state = {
      dataSource: [{id: 1}],
      selectedRowKeys: [],
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
    const { unapprovedModel } = props;
    this.setState({
      unapprovedModel,
    });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys });
  }

  // 搜索
  onSearch(val) {
    const { router, location: { pathname, query } } = this.props;
    query.keyword = val;
    router.push({ pathname, query });
  }

  // 通过
  handleClickPassYes() {
    const modal = Modal.confirm({
      content: '确定要通过吗',
      onOk: async () => {
        const { dispatch, location: { query } } = this.props;
        const { selectedRowKeys } = this.state;
        const { data: { code, msg } } = await passYes(selectedRowKeys);
        if (code == "200") {
          message.success('已通过');
          dispatch({
            type: 'unapprovedModel/getList',
            payload: query,
          });
          this.setState({ selectedRowKeys: [] });
          modal.destroy();
          router.push("/resources/unapproved")
        }
      }
    });
  }

  // 不通过
  handleClickPassNo() {
    const {router} = this.props;
    const modal = Modal.confirm({
      content: '确定要拒绝吗',
      onOk: async () => {
        const { dispatch, location: { query } } = this.props;
        const { selectedRowKeys } = this.state;
        const { data: { code, msg } } = await passNo(selectedRowKeys);
        if (code == "200") {
          message.success('已拒绝');
          dispatch({
            type: 'unapprovedModel/getList',
            payload: query,
          });
          this.setState({ selectedRowKeys: [] });

          // 销毁弹窗
          modal.destroy();
          router.push("/resources/unapproved")
        }
      }
    });

  }

  render() {
    const { selectedRowKeys } = this.state;
    const { unapprovedModel, location: { pathname, query } } = this.props;
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
          <Empower api="/myResourceController/batchToPass">
            <Button
              type="primary"
              disabled={selectedRowKeys.length === 0}
              onClick={this.handleClickPassYes.bind(this)}
            >通过</Button>
          </Empower>
          <Empower api="/myResourceController/batchToUnPass">
            <Button
              type="danger"
              disabled={selectedRowKeys.length === 0}
              onClick={this.handleClickPassNo.bind(this)}
              style={{marginLeft:10}}
            >不通过</Button>
          </Empower>
        </Col>
      </Row>

      <TableList
        rowKey="id"
        columns={this.columns}
        dataSource={unapprovedModel.rows}
        showIndex
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys,
        }}
        pagination={{total: unapprovedModel.total}}
        style={{margin: 20}}
      />
    </div>);
  }
}

export default connect(({ system, unapprovedModel, resourcesCommonChange }) => ({
  system,
  unapprovedModel,
  resourcesCommonChange,
}))(withRouter(Unapproved));
