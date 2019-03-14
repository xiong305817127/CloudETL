/**
 * 从现有表导入字段 - 选择表
 */
import React from 'react';
import { connect } from 'dva';
import { Table, Cascader } from 'antd';
import { getMetaTableList } from 'services/metadataDefine';

import Search from 'components/Search';
import Modal from 'components/Modal';

class Import extends React.Component {
  state = {
    selectedRows: [],
    dataSource: [],
    total:0,
    current:1,
    dsType: ""
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
    this.loadList();
  }

  // 现有表全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  handleSubmit() {
    const tables = this.state.selectedRows.map(row => ({
      metaid: String(row.metaid),
      metaName: row.metaNameCn || row.metaNameEn,
      dsType: row.dsType
    }));

    this.props.onClose();
    this.props.onOk(tables);
  }

  handleCancel() {
    this.props.onClose();
  }

  handleChangeDept(arr) {
    const dept = arr[arr.length - 1];
    this.loadList({ dept });
  }

  handleClickSearch(value) {
    this.loadList({ keyword: value });
  }

  handlePageChange(value){
    console.log(value);
    this.setState({
      current:value
    })
    this.loadList({ page: value });
  }

  async loadList(query) {
    const { account } = this.props;
    const page =query && query.page?Object.assign({ page: 1, rows: 10 },query):{ page: 1, rows: 10 };
    const formData = {
      metaNameCn: '',
      // metaType: 1,
      renterId: account.renterId,
      ...query,
    };
    if(query && query.page){
      delete formData.page
    }
    const { data } = await getMetaTableList(page, formData);
    this.setState({
      selectedRows: [],
      selectedRowKeys: [],
      total:data && data.data && data.data.total || 0,
      dataSource: data && data.data && data.data.rows || [],
    });
  }

  // 现有表
  columns = [{
    title: '表英文名称',
    width: 135,
    dataIndex: 'metaNameEn',
  }, {
    title: '表中文名称',
    width: 180,
    dataIndex: 'metaNameCn',
  }, {
    title: '数据库类型',
    render: (text, record) => ({
      '2': 'Oracle',
      '3': 'MySQL',
      '4': 'Hive',
      '5': 'Hbase',
      '14':'DM',
      '8':"PostgreSql"
    })[record.dsType],
  }];

  render() {
    const { departmentsTree } = this.props.metadataCommon;
    console.log(this.state,"更新状态");

    return (<Modal
      title="现有元数据表选择"
      visible
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      closable={false}
      zIndex={1020}

      okText="保存"
    >
      <header>
        <Search
          onSearch={this.handleClickSearch.bind(this)}
          placeholder="按表中文名、英文名进行查询"
          width="300px"
        />
        <Cascader
          placeholder="选择组织"
          options={departmentsTree}
          onChange={this.handleChangeDept.bind(this)}
        />
      </header>
      <Table
        showIndex
        rowKey="metaid"
        columns={this.columns}
        dataSource={this.state.dataSource}
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys: this.state.selectedRowKeys,
        }}

        pagination={{
          total:this.state.total,
          onChange:this.handlePageChange.bind(this),
          pageSize:10,
          current:this.state.current
        }}
        total={this.state.total}
        scroll={{ y: 240 }}
        size="small"
        style={{ marginTop: 10 }}
      />
    </Modal>);
  }
}

export default connect(({ metadataCommon, account }) => ({
  metadataCommon,
  account,
}))(Import);
