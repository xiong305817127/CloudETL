/**
 * ES索引类主组件
 * @model  ./es.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'react-router';
import { Row, Col, Button, Icon, Tooltip, Popconfirm, Popover, message } from 'antd';
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import Upload from 'components/Upload';
import { downloadFile } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import { importES, exportES, updateESStatus } from 'services/metadataDefine';
import EditorES from './components/EditorES';

import Style from './style.css';

class AppPage extends React.Component {

  state = {
    selectedRows: [],
    selectedRowKeys: [],
  }

  columns = [
    {
      title: '索引编码',
      dataIndex: 'indexCode',
      render: (text,record) => {
        return <a onClick={()=>{this.handleView(record.indexId)}}>{text}</a>
      }
    }, {
      title: '说明',
      dataIndex: 'description',
      render: (text) => text && text !== 'null' ? (<div className="word25" title={text}>{text}</div>) : null,
    }, {
      title: '当前版本',
      dataIndex: 'currentVersion',
    }, {
      title: '最新版本',
      dataIndex: 'latestVersion',
    }, {
      title: '创建者',
      dataIndex: 'creator',
    }, {
      title: '所属组织',
      dataIndex: 'dept',
      render: (text) => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text,departmentsOptions);
      },
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
      width: 130,
      render: (text,record) => {
        return <div>
          <Tooltip title="查看日志">
            <Popover title="日志" content={<pre>{record.log}</pre>} trigger="click">
              <a><Icon type="file-text" className="op-icon"/></a>
            </Popover>
          </Tooltip>
          <Empower api="/EsIndexController/history">
            <Tooltip title="历史版本">
              <a><Icon onClick={()=>{this.handleShowHistory(record)}} type="switcher" className="op-icon"/></a>
            </Tooltip>
          </Empower>
          {/*<Tooltip title="版本切换">
            <a><Icon onClick={()=>{this.handleSwitchVersion(record.indexId)}} type="retweet" className="op-icon"/></a>
          </Tooltip>*/}
          <Empower api="/EsIndexController/Modify" disabled={!record.canEdited}>
            <Tooltip title="修改">
              <a><Icon onClick={()=>{this.handleModify(record.indexId)}} type="edit" className="op-icon"/></a>
            </Tooltip>
          </Empower>
          {record.status !== 1 && record.status !== 2 ? (
            <Empower api="/EsIndexController/updateStatus" disabled={!record.canEdited}>
              <Tooltip title="启动">
                <a><Icon onClick={()=>{this.handleSwitchStatus(record, 'start')}} type="play-circle-o" className="op-icon"/></a>
              </Tooltip>
            </Empower>
          ) : null}
          {record.status === 1 ? (
            <Empower api="/EsIndexController/updateStatus" disabled={!record.canEdited}>
              <Tooltip title="停止">
                <a><Icon onClick={()=>{this.handleSwitchStatus(record, 'stop')}} type="pause-circle-o" className="op-icon"/></a>
              </Tooltip>
            </Empower>
          ) : null}
        </div>
      }
    }
  ];

  componentDidMount(){
    const { dispatch }=this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
  }

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'metaES/getList', payload: query });
  }

  // 新建
  handleAddNew() {
    const { dispatch }=this.props;
    dispatch({ type: 'metaES/view'});
    dispatch({ type: 'metaES/save', payload: { viewMode: 'new' }});
    dispatch({ type: 'metaES/showEditor'});
  }

  // 编辑
  handleModify(id) {
    const { dispatch } = this.props;
    dispatch({ type: 'metaES/view', id});
    dispatch({ type: 'metaES/save', payload: { viewMode: 'edit' }});
    dispatch({ type: 'metaES/showEditor'});
  }

  // 历史版本
  handleShowHistory(record) {
    const { router } = this.props;
    router.push({
      pathname: '/MetadataDefine/es-hisrory',
      query: {
        indexCode: record.indexCode,
      },
    });
  }

  // 版本切换
  handleSwitchVersion(id) {
    const { dispatch } = this.props;
    console.log('版本切换', id);
  }

  // 删除
  async handleDelete(id) {
    this.reloadList();
  }

  // 切换状态
  async handleSwitchStatus(record, mode) {
    const { indexCode, currentVersion } = record;
    const formData = {
      indexCode,
      currentVersion,
      status: mode === 'start' ? 1 : 0,
    };
    const { data } = await updateESStatus(formData);
    if (data && data.code === '200') {
      this.reloadList();
      message.success('操作成功');
    }
  }

  // 查看
  handleView(id) {
    const { dispatch } = this.props;
    dispatch({ type: 'metaES/view', id});
    dispatch({ type: 'metaES/save', payload: { viewMode: 'read' }});
    dispatch({ type: 'metaES/showEditor'});
  }

  // 导出
  handleExport() {
    const ids = this.state.selectedRowKeys.join(',');
    downloadFile(`${exportES}?ids=${ids}`);
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 导入状态跟踪
  handleImportStatusChange(res) {
    const { dispatch } = this.props;
    const { file: { response }, event} = res;
    // 上传进度100
    if (event && event.percent === 100) {
      // console.log('上传完成');
    }
    // 服务器返回信息
    if (response) {
      if (response.code === '200') {
        message.success('导入成功');
        dispatch({ type: 'metaES/getList' });
      }
    }
  };

  render() {
    const { account, metaES } = this.props;
    const { source } = metaES;

    return <div>
      <Row>
        <Col span="14">
          <Empower api="/EsIndexController/IndexAndType">
            <Button type="primary" onClick={this.handleAddNew.bind(this)}>新建</Button>
          </Empower>
        </Col>
        <Col span="10" style={{textAlign: 'right', fontSize: 18}}>
        </Col>
      </Row>

      <TableList
        showIndex
        rowKey="indexId"
        columns={this.columns}
        dataSource={source.list}
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys: this.state.selectedRowKeys,
        }}
        pagination={{total: source.total}}
        className="padding_20_0"
      />

      {metaES.editorVisible ? <EditorES /> : null}

    </div>
  }
}

export default connect(({ metaES, account, metadataCommon }) => ({
  metaES,
  account,
  metadataCommon,
}))(AppPage);
