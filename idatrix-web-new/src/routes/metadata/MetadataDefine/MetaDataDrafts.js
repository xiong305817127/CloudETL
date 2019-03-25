/**
 * 数据表类草稿箱
 * @model  ./metaDataDrafts.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link, withRouter } from 'react-router';
import { Button, Icon, TreeSelect, message } from 'antd';
import Empower from 'components/Empower';
import Modal from 'components/Modal';
import Search from 'components/Search';
import TableList from 'components/TableList';
import { createEntyTable, moveMetadataToRecycle } from 'services/metadataDefine';
import { deepCopy } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import EditorTableStep1 from './components/EditorTableStep1';
import EditorTableStep2 from './components/EditorTableStep2';
import ViewTableStep1 from './components/ViewTableStep1';
import ViewTableStep2 from './components/ViewTableStep2';
import Acquisition from './components/Acquisition';

import Style from './style.css';

class AppPage extends React.Component {

  state = {
    selectedRows: [],
    selectedRowKeys: [],
  }

  columns = [
    {
      title: '表中文名称',
      dataIndex: 'metaNameCn',
      key: 'metaNameCn',
      render: (text,record) => {
        return <a onClick={()=>{this.handleView(record)}}>{text}</a>
      }
    }, {
      title: '表英文名称',
      dataIndex: 'metaNameEn',
      key: 'metaNameEn',
    }, {
      title: '数据库名称',
      render: (text, { dataSource }) => dataSource ? dataSource.dbDatabasename : '',
    }, {
      title: '类型',
      dataIndex: 'dsType',
      key: 'dsType',
      width: 100,
      render: (text) => {
        return ({
          '2': 'Oracle',
          '3': 'MySQL',
          '4': 'Hive',
          '5': 'Hbase',
          '14':'DM',
          '8':"PostgreSql"
        })[text];
      },
    }, {
      title: '模式',
      dataIndex: 'metadataSchema',
      render: (text)=>{
        return (typeof text !== "undefined" && text.name !== "") ? text.name : "无";
      }
    },{
      title: '所属组织',
      dataIndex: 'dept',
      key: 'dept',
      render: (text) => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text,departmentsOptions);
      },
    }, {
      title: '拥有者',
      dataIndex: 'owner',
      key: 'owner',
    }, {
      title: '创建者',
      dataIndex: 'creator',
    }, {
      title: '创建日期',
      dataIndex: 'createTime',
      key: 'createTime',
    }, {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      render: (text) => (<div className="word25" title={text}>{text}</div>)
    }, {
      title: '公开等级',
      dataIndex: 'publicStats',
      width: 100,
      render: (text) => text == 1 ? '授权公开' : '不公开',
    }, {
      title: '操作',
      render: (text, record) =>  record.canEdited ? (
        <a><Icon onClick={()=>{this.handleEdit(record)}} type="edit" className="op-icon"/></a>
      ) : null,
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
  }

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'metaDataDrafts/getList', payload: query });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 生效
  async handleEffect() {
    Modal.confirm({
      content: '确认要使草稿生效吗？',
      onOk: async () => {
        const { account } = this.props;
        const { selectedRowKeys } = this.state;
        const query = {
          userId: account.id,
          ids: selectedRowKeys.join(','),
        };
        const { data } = await createEntyTable(query);
        if (data && data.code === '200') {
          message.success('已生效');
          this.setState({
            selectedRows: [],
            selectedRowKeys: [],
          })
          this.reloadList();
        }
      }
    });
  }

  // 输入回收站
  handleMoveToRecycle() {
    Modal.confirm({
      content: '确认要移入回收站吗？',
      onOk: async () => {
        const formData = this.state.selectedRows.map(row => ({ metaid: row.metaid }));
        const { data } = await moveMetadataToRecycle(formData);
        if (data && data.code === '200') {
          message.success('已移入回收站');
          this.setState({
            selectedRows: [],
            selectedRowKeys: [],
          })
          this.reloadList();
        }
      }
    });
  }

  // 编辑
  handleEdit(record) {
    const { dispatch } = this.props;
    const view = deepCopy(record);
    dispatch({ type: 'metaDataDefine/save', payload: { view, viewMode: 'draft' }});
    dispatch({ type: 'metaDataDefine/showEditor', step: 1});
  }

  // 查看
  handleView(record) {
    const { dispatch } = this.props;
    const view = deepCopy(record);
    dispatch({ type: 'metaDataDefine/save', payload: { view, viewMode: 'read' }});
    dispatch({ type: 'metaDataDefine/showView', step: 1});
  }

  handleClickSearch(keyword, dept) {
    const { router, location } = this.props;
    const query = {
      keyword,
      dept: dept ? dept : null,
    };
    router.push({ ...location, query });
  }

  // 选择组织
  handleChangeDept(dept) {
    console.log("1234",dept);
    const { query: { keyword } } = this.props.location;
    this.handleClickSearch(keyword, dept);
  }

  isDisabled() {
    const { selectedRows, selectedRowKeys } = this.state;
    return selectedRowKeys.length === 0 || selectedRows.some(it => !it.canEdited);
  }

  render() {
    const { metaDataDrafts, metaDataDefine } = this.props;
    const { departmentsTree } = this.props.metadataCommon;
    const { query } = this.props.location;
    const { list, total } = metaDataDrafts;
    const {view} = metaDataDefine;
    
    return <div style={{padding:20, backgroundColor: '#fff'}}>

      <header>
        <Search
          defaultValue={query.keyword || ''}
          onSearch={this.handleClickSearch.bind(this)}
          placeholder="可以按表中文名称、表英文名称进行模糊搜索"
        />
        <TreeSelect
          placeholder="请选择组织"
          treeData={departmentsTree}
          onChange={(value)=>{this.handleChangeDept(value)}}
          treeDefaultExpandAll
          style={{ width: 200 }}
          allowClear
        />
        <div className={Style['btns-wrap']} style={{ marginTop: 20 }}>
          <Button><Link to="/MetadataDefine">返回</Link></Button>
          <Empower api="/frontMetadataInfoController/createMetadata">
            <Button
              type="primary"
              disabled={this.isDisabled()}
              onClick={this.handleEffect.bind(this)}
            >生效</Button>
          </Empower>
          <Empower api="/frontMetadataInfoController/createMetadata">
            <Button
              type="primary"
              disabled={this.isDisabled()}
              onClick={this.handleMoveToRecycle.bind(this)}
            >移入回收站</Button>
          </Empower>
        </div>
      </header>

      <TableList
        showIndex
        rowKey="metaid"
        columns={this.columns}
        dataSource={list}
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys: this.state.selectedRowKeys,
        }}
        pagination={{total}}
        style={{marginTop:'20px'}}
      />

      {/* 加载编辑器 */}
      {metaDataDefine.editorStep1Visible ? <EditorTableStep1 /> : null}
      {metaDataDefine.editorStep2Visible ? <EditorTableStep2 /> : null}

      {/* 加载查看窗口 */}
      {metaDataDefine.viewStep1Visible ? <ViewTableStep1 /> : null}
      {metaDataDefine.viewStep2Visible ? <ViewTableStep2 metaNameCn={view.metaNameCn? view.metaNameCn : ""} /> : null}
      {metaDataDefine.viewAcquisitionVisible ? <Acquisition /> : null}

    </div>
  }
}

export default connect(({ metaDataDrafts, metaDataDefine, metadataCommon, account }) => ({
  metaDataDrafts,
  metaDataDefine,
  metadataCommon,
  account,
}))(withRouter(AppPage));
