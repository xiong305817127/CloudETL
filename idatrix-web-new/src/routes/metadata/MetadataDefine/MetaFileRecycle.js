/**
 * 文件目录类回收站
 * @model  ./metaFileRecycle.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'react-router';
import { Button, Icon, Tooltip, message } from 'antd';
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import { restoreFile, deleteFile } from 'services/metadataDefine';
import { deepCopy } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import ViewFile from './components/ViewFile';
import Modal from 'components/Modal';

import Style from './style.css';

class AppPage extends React.Component {

  state = {
    selectedRows: [],
    selectedRowKeys: [],
  }

  columns = [
    {
      title: '文件目录描述',
      dataIndex: 'dirName',
      key: 'dirName',
      render: (text,record) => {
        return <a onClick={()=>{this.handleView(record)}}>{text}</a>
      }
    }, {
      title: '文件存储目录',
      dataIndex: 'storDir',
      key: 'storDir',
      render: (text) => {
        const { hdfsPlanList } = this.props.metadataCommon;
        let result = [];
        try {
          const arr = typeof text === 'string' ? JSON.parse(text) : text;
          arr.forEach(id => {
            const found = hdfsPlanList.find(it => it.value == id);
            if (found) result.push(found.label);
          });
        } catch (err) {}
        return result.join('/');
      },
    }, {
      title: '所属组织',
      dataIndex: 'dept',
      key: 'dept',
      render: (text) => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text,departmentsOptions);
      },
    }, {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      render: (text) => text && text !== 'null' ? (<div className="word25" title={text}>{text}</div>) : null,
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
    dispatch({ type: 'metadataCommon/getHdfsTree' });
  }

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'metaFileRecycle/getList', payload: query });
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
        const formData = this.state.selectedRows.map(row => ({ fileid: row.fileid }));
        const { data } = await restoreFile(formData);
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
        const formData = this.state.selectedRows.map(row => ({ fileid: row.fileid }));
        const { data } = await deleteFile(formData);
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
    dispatch({ type: 'metaFileDefine/save', payload: { view, viewMode: 'read' }});
    dispatch({ type: 'metaFileDefine/showView'});
  }

  render() {
    const { metaFileRecycle, metaFileDefine } = this.props;
    const { list, total } = metaFileRecycle;

    return <div style={{padding:20, backgroundColor: '#fff'}}>

      <div className={Style['btns-wrap']}>
        <Button><Link to="/MetadataDefine?model=file">返回</Link></Button>
        <Empower api="/frontMetafileInfoController/batchToRecovery">
          <Button type="primary"
            disabled={this.state.selectedRowKeys.length < 1}
            onClick={this.handleRestore.bind(this)}
          >还原</Button>
        </Empower>
        <Empower api="/frontMetafileInfoController/batchToDelete">
          <Button type="primary"
            disabled={this.state.selectedRowKeys.length < 1}
            onClick={this.handleDeleteForever.bind(this)}
          >永久删除</Button>
        </Empower>
      </div>

      <TableList
        showIndex
        rowKey="fileid"
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
      {metaFileDefine.viewVisible ? <ViewFile /> : null}

    </div>
  }
}

export default connect(({ metaFileRecycle, metaFileDefine, metadataCommon }) => ({
  metaFileRecycle,
  metaFileDefine,
  metadataCommon,
}))(AppPage);
