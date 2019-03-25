/**
 * ES索引类历史版本
 * @model  ./ESHistory.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'react-router';
import { Button, Icon, Tooltip, Popconfirm, message } from 'antd';
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import { switchESVersion, deleteESVersion, getESDetail } from 'services/metadataDefine';
import { deepCopy } from 'utils/utils';
import EditorES from './components/EditorES';

import Style from './style.css';

class AppPage extends React.Component {

  state = {
    selectedRows: [],
    selectedRowKeys: [],
  }

  columns = [
    {
      title: '说明',
      dataIndex: 'description',
      render: (text, record) => text && text !== 'null' ? (<a onClick={()=>{this.handleView(record)}} className="word25" title={text}>{text}</a>) : null,
    }, {
      title: '版本号',
      dataIndex: 'currentVersion',
    }, {
      title: '状态',
      dataIndex: 'status',
      render: (text) => {
        let result = '';
        switch(text) {
          case 0: result = '停用'; break;
          case 1: result = '启用'; break;
          case 2: result = '草稿'; break;
        }
        return result;
      }
    }, {
      title: '操作',
      width: 80,
      render: (text,record) => {
        return <div className={Style['btns-wrap']}>
          <Empower api="/EsIndexController/Modify" disabled={!record.canEdited}>
            <Tooltip title="修改">
              <a><Icon onClick={()=>{this.handleModify(record)}} type="edit" className="op-icon"/></a>
            </Tooltip>
          </Empower>
          <Empower api="/EsIndexController/Modify" disabled={!record.canEdited}>
            <Tooltip title="删除">
              <Popconfirm title="确定要删除该索引吗？" onConfirm={()=>{this.handleDelete(record)}}>
                <a><Icon type="delete" className="op-icon"/></a>
              </Popconfirm>
            </Tooltip>
          </Empower>
        </div>
      }
    }
  ];

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'ESHistory/getList', payload: query });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 删除
  async handleDelete(record) {
    const { indexId } = record;
    const { data } = await deleteESVersion({
      indexId,
    });
    if (data && data.code === '200') {
      this.reloadList();
      message.success('删除成功');
    }
  }

  // 切换版本
  async handleSwitchVersion() {
    const record = this.state.selectedRows[0];
    const { data } = await switchESVersion({
      indexCode: record.indexCode,
      currentVersion: record.currentVersion,
      oldVersion: record.latestVersion,
    });
    if (data && data.code === '200') {
      this.reloadList();
      message.success('切换成功');
    }
  }

  // 查看
  handleView(record) {
    this.loadView(record, 'read');
  }

  // 编辑
  handleModify(record) {
    this.loadView(record, 'edit');
  }

  // 加载详情窗口
  async loadView(record, mode) {
    const { dispatch } = this.props;
    const view = deepCopy(record);
    dispatch({ type: 'metaES/save', payload: { view, viewMode: mode }});
    dispatch({ type: 'metaES/showEditor'});
    const { data } = await getESDetail({ indexId: record.indexId }); // 获取字段列表
    const viewTabs = data && data.data && data.data.tabs || [];
    view.viewTabs = viewTabs;
    dispatch({ type: 'metaES/save', payload: { view }});
  }

  render() {
    const { ESHistory, metaES } = this.props;
    const { list, total } = ESHistory;

    return <div style={{padding:20, backgroundColor: '#fff'}}>

      <div className={Style['btns-wrap']}>
        <Button><Link to="/MetadataDefine?model=es">返回</Link></Button>
        <Empower api="/EsIndexController/switchVersion">
          <Button type="primary"
            disabled={this.state.selectedRowKeys.length < 1 || this.state.selectedRows.some(it => !it.canEdited)}
            onClick={this.handleSwitchVersion.bind(this)}
          >变更为当前版本</Button>
        </Empower>
      </div>

      <TableList
        showIndex
        rowKey="indexId"
        columns={this.columns}
        dataSource={list}
        rowSelection={{
          type: 'radio',
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys: this.state.selectedRowKeys,
        }}
        pagination={{total}}
        style={{marginTop:'20px'}}
      />

      {/* 加载查看窗口 */}
      {metaES.editorVisible ? <EditorES /> : null}

    </div>
  }
}

export default connect(({ ESHistory, metaES }) => ({
  ESHistory,
  metaES,
}))(AppPage);
