/**
 * 文件目录类主组件
 * @model  ./metaFile.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'react-router';
import { Row, Col, Button, Icon, message } from 'antd';
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import { downloadFile } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import { importMetadatafile, exportMetafile, moveMetafileToRecycle } from 'services/metadataDefine';
import EditorFile from './components/EditorFile';
import ViewFile from './components/ViewFile';
import Upload from 'components/Upload';

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
        return <a className="word25" title={text} onClick={()=>{this.handleView(record.fileid)}}>{text}</a>
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
      width:"35%",
      render: (text) => text && text !== 'null' ? (<div className="word25" title={text}>{text}</div>) : null,
    }, {
      title: '操作',
      render: (text,record) => {
        return <a><Icon onClick={()=>{this.handleModify(record.fileid)}} type="edit" className="op-icon"/></a>
      }
    }
  ];

  componentDidMount(){
    const { dispatch }=this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
    dispatch({ type: 'metadataCommon/getHdfsTree' });
  }

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'metaFileDefine/getList', payload: query });
  }

  // 新建
  handleAddNew() {
    const { dispatch }=this.props;
    dispatch({ type: 'metaFileDefine/view'});
    dispatch({ type: 'metaFileDefine/save', payload: { viewMode: 'new' }});
    dispatch({ type: 'metaFileDefine/showEditor'});
  }

  // 编辑
  handleModify(id) {
    const { dispatch } = this.props;
    dispatch({ type: 'metaFileDefine/view', id});
    dispatch({ type: 'metaFileDefine/save', payload: { viewMode: 'edit' }});
    dispatch({ type: 'metaFileDefine/showEditor'});
  }

  // 查看
  handleView(id) {
    const { dispatch } = this.props;
    dispatch({ type: 'metaFileDefine/view', id});
    dispatch({ type: 'metaFileDefine/showView'});
  }

  // 导出
  handleExport() {
    const ids = this.state.selectedRowKeys.join(',');
    downloadFile(`${exportMetafile}?ids=${ids}`);
  }

  // 移入回收站
  async handleMoveToRecycle() {
    const formData = this.state.selectedRowKeys.map(id => ({ fileid: id }));
    const { data } = await moveMetafileToRecycle(formData);
    if (data && data.code === '200') {
      message.success('已移入回收站');
      this.setState({
        selectedRows: [],
        selectedRowKeys: [],
      })
      this.reloadList();
    }
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
        dispatch({ type: 'metaFileDefine/getList' });
      }
    }
  };

  render() {
    const { account, metaFileDefine } = this.props;
    const { source } = metaFileDefine;

    return <div>
      <Row>
        <Col span="14" className="btn_group_10">
          <Empower api="/frontMetafileInfoController/batchInsert">
            <Button type="primary" onClick={this.handleAddNew.bind(this)}>新建</Button>
          </Empower>
          <Empower api="/frontMetafileInfoController/moveToRecycle">
            <Button type="primary" 
              disabled={this.state.selectedRowKeys.length<1} 
              onClick={this.handleMoveToRecycle.bind(this)}>
              移入回收站
            </Button>
          </Empower>
        </Col>
        <Col span="10" style={{textAlign: 'right', fontSize: 18}}>
          <Empower api="/frontMetadataInfoController/getRecycle">
            <Link to="/MetadataDefine/fileRecycle">
              <Icon type="delete" />
              <span style={{marginLeft: "10px"}}>回收站</span>
            </Link>
          </Empower>
        </Col>
      </Row>

      <TableList
        showIndex
        rowKey="fileid"
        columns={this.columns}
        dataSource={source.list}
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys: this.state.selectedRowKeys,
        }}
        pagination={{total: source.total}}
        className="margin_20_0"
      />

      {metaFileDefine.editorVisible ? <EditorFile /> : null}
      {metaFileDefine.viewVisible ? <ViewFile /> : null}

    </div>
  }
}

export default connect(({ metaFileDefine, account, metadataCommon }) => ({
  metaFileDefine,
  account,
  metadataCommon,
}))(AppPage);
