/**
 * 数据表类回收站
 * @model  ./metaDataRecycle.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'react-router';
import { Button, Icon, Tooltip, message } from 'antd';
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import { restoreTable, deleteTable } from 'services/metadataDefine';
import { deepCopy } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import ViewTableStep1 from './components/ViewTableStep1';
import ViewTableStep2 from './components/ViewTableStep2';
import Modal from 'components/Modal';

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
    },{
      title: '模式',
      dataIndex: 'metadataSchema',
      render: (text)=>{
        return (typeof text !== "undefined" && text.name !== "") ? text.name : "无";
      }
    }, {
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
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
  }

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'metaDataRecycle/getList', payload: query });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 还原
  async handleRestore() {
    Modal.confirm({
      content: '确认要还原吗？',
      onOk: async () => {
        const formData = this.state.selectedRows.map(row => ({ metaid: row.metaid }));
        const { data } = await restoreTable(formData);
        if (data && data.code === '200') {
          message.success('已还原');
          this.setState({
            selectedRows: [],
            selectedRowKeys: [],
          })
          this.reloadList();
        }
      }
    });
  }

  // 永久删除
  handleDeleteForever() {
    Modal.confirm({
      content: '永久删除后将无法找回，确认要执行此操作吗？',
      onOk: async () => {
        const formData = this.state.selectedRows.map(row => ({ metaid: row.metaid }));
        const { data } = await deleteTable(formData);
        if (data && data.code === '200') {
          message.success('已删除');
          this.setState({
            selectedRows: [],
            selectedRowKeys: [],
          });
          this.reloadList();
        }
      }
    });
  }

  // 查看
  handleView(record) {
    const { dispatch } = this.props;
    const view = deepCopy(record);
    dispatch({ type: 'metaDataDefine/save', payload: { view, viewMode: 'read' }});
    dispatch({ type: 'metaDataDefine/showView', step: 1});
  }

  hasCannotEdited() {
    return this.state.selectedRows.some(it => !it.canEdited);
  }

  render() {
    const { metaDataRecycle, metaDataDefine } = this.props;
    const { list, total } = metaDataRecycle;
    const { view } = metaDataDefine;
    
    return <div style={{padding:20, backgroundColor: '#fff'}}>

      <div className={Style['btns-wrap']}>
        <Button><Link to="/MetadataDefine">返回</Link></Button>
        <Empower api="/frontMetadataInfoController/restore">
          <Button type="primary"
            disabled={this.state.selectedRowKeys.length < 1 || this.hasCannotEdited()}
            onClick={this.handleRestore.bind(this)}
          >还原</Button>
        </Empower>
        <Empower api="/frontMetadataInfoController/batchToDelete">
          <Button type="primary"
            disabled={this.state.selectedRowKeys.length < 1 || this.hasCannotEdited()}
            onClick={this.handleDeleteForever.bind(this)}
          >永久删除</Button>
        </Empower>
      </div>

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

      {/* 加载查看窗口 */}
      {metaDataDefine.viewStep1Visible ? <ViewTableStep1 /> : null}
      {metaDataDefine.viewStep2Visible ? <ViewTableStep2 metaNameCn={view.metaNameCn ? view.metaNameCn : ""}/> : null}

    </div>
  }
}

export default connect(({ metaDataRecycle, metaDataDefine, metadataCommon }) => ({
  metaDataRecycle,
  metaDataDefine,
  metadataCommon,
}))(AppPage);
