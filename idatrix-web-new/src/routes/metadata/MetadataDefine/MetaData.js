/**
 * 数据表类主组件
 * @model  ./metaData.model.js
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'react-router';
import { Row, Col, Button, Icon, Tooltip, message } from 'antd';
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import { exportMetadata, uploadExample, moveMetadataToRecycle } from 'services/metadataDefine';
import { downloadFile } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import EditorTableStep1 from './components/EditorTableStep1';
import EditorTableStep2 from './components/EditorTableStep2';
import ViewTableStep1 from './components/ViewTableStep1';
import ViewTableStep2 from './components/ViewTableStep2';
import Acquisition from './components/Acquisition';
import { deepCopy } from 'utils/utils';

import Upload from 'components/Upload';
import Modal from 'components/Modal';

import Style from './style.css';
import {API_BASE_DOWNLOAD_FILE} from "constants";

class AppPage extends React.Component {

  state = {
    selectedRows: [],
    selectedRowKeys: [],
    uploadVisible: false,
    uploadFileList: [],
    soureFreeze: true,
    canDelete: false
  }

  columns = [
    {
      title: '表中文名称',
      dataIndex: 'metaNameCn',
      render: (text,record) => {
        return <a onClick={()=>{this.handleView(record)}}>{text}</a>
      }
    }, 
    {
      title: '表英文名称',
      dataIndex: 'metaNameEn',
    }, 
    {
      title: '数据库名称',
      render: (text, { dataSource }) => dataSource ? dataSource.dbDatabasename : '',
    }, {
      title: '类型',
      dataIndex: 'dsType',
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
    },     {
      title: '模式',
      dataIndex: 'metadataSchema',
      render: (text)=>{
        return (typeof text !== "undefined" && text.name !== "") ? text.name : "无";
      }
    }, {
      title: '所属组织',
      dataIndex: 'dept',
      render: (text) => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text,departmentsOptions);
      },
    },{
      title: '前置机或平台',
      dataIndex: 'sourceId',
      render: (text) => text == 1 ? '前置机' : '平台',
    },{
      title: '是否为直采',
      dataIndex: 'sourceType',
      render: (text,record) => {
        return text == 1 ? '直采' : '非直采';
      },
    },{
      title: '拥有者',
      dataIndex: 'owner',
    }, {
      title: '创建者',
      dataIndex: 'creator',
    }, {
      title: '创建日期',
      dataIndex: 'createTime',
    }, {
      title: '备注',
      dataIndex: 'remark',
      render: (text) => (<div className="word25" title={text}>{text}</div>)
    }, {
      title: '公开等级',
      dataIndex: 'publicStats',
      width: 100,
      render: (text) => text == 1 ? '授权公开' : '不公开',
    }, {
      title: '是否生成实体表',
      dataIndex: 'status',
      width: 100,
      render: (text) => text === 1 ? '已生成' : '未生成',
    } ,{
      title: '操作',
      className: 'td-nowrap',
      render: (text,record) => {
        return (<div className={Style['btns-wrap']}>
          {((record.dsType == 4) || (record.dsType == 5)) && record.status === 1 ? null : (
            <Empower api="/frontMetadataInfoController/updateMetadata" disabled={!record.canEdited}>
              <a target="_blank">
                <Tooltip title="编辑">
                  <Icon onClick={()=>this.handleModify(record.metaid,record.sourceType)} type="edit" className="op-icon"/>
                </Tooltip>
              </a>
            </Empower>
          )}
          {
            (record.sourceType !== 1) &&
            <Empower api="/FileController/uploadExample">
              <a target="_blank">
                <Tooltip title="上传样例" >
                  <Icon onClick={()=>this.handleShowUpload(record.metaid)} type="upload" className="op-icon"/>
                </Tooltip>
              </a>
            </Empower>
          }

          {record.fileName ? (<a>
            <Tooltip title="下载样例" >
              <Icon onClick={()=>this.handleDownload(record.fileName)} type="download" className="op-icon"/>
            </Tooltip>
          </a>) : null}
        </div>);
      }
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
  }

  // 刷新列表
  reloadList() {
    const { dispatch, location: { query } } = this.props;
    dispatch({ type: 'metaDataDefine/getList', payload: query });
  }

  // 切换表类型
  handleChangeType(metaType) {
    const { router, location } = this.props;
    const model = location.query.model || 'table';
    const dept = location.query.dept;
    const keyword = location.query.keyword;
    router.push({ ...location, query: { model, metaType, dept, keyword }});
    this.setState({
      selectedRows: [],
    });
  }

  // 新建
  handleAddNew() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/view' });
    dispatch({ type: 'metaDataDefine/save', payload: { viewMode: 'new',direct:false } });
    dispatch({ type: 'metaDataDefine/showEditor', step: 1 });
    console.log(this.props,"this.propscaiji");
  }

  // 采集新建
  handleAddNewAcq() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/view' });
    dispatch({ type: 'metaDataDefine/save', payload: { viewMode: 'new',direct:false } });
    dispatch({ type: 'metaDataDefine/showViewAcquisition', step: 1 });
  }

  // 编辑
  handleModify(id,sourceType) {
    const { dispatch } = this.props;
    const direct = sourceType == 1?true:false;

    dispatch({ type: 'metaDataDefine/view', id });
    dispatch({ type: 'metaDataDefine/save', payload: { viewMode: 'edit',direct } });
    dispatch({ type: 'metaDataDefine/showEditor', step: 1 });
  }

  // 查看
  handleView(record) {
    const { dispatch } = this.props;
    const view = deepCopy(record);
    dispatch({ type: 'metaDataDefine/save', payload: { view, viewMode: 'read' }});
    // dispatch({ type: 'metaDataDefine/showView', step: 1});
    // dispatch({ type: 'metaDataDefine/view', id });
    dispatch({ type: 'metaDataDefine/showView', step: 1 });
  }

  // 上传样式
  handleShowUpload(id) {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/view', id });
    this.setState({ uploadVisible: true });
  }

  // 导出字段
  handleExport() {
    const ids = this.state.selectedRowKeys.join(',');
    if (this.state.selectedRowKeys.length > 1) {
      message.warn('一次只能导出一个表');
    } else {
      downloadFile(`${exportMetadata}?ids=${ids}`);
      this.reloadList();
    }
  }

  // 移入回收站
  handleMoveToRecycle() {
    Modal.confirm({
      content: '确定要移入回收站吗？',
      onOk: async () => {
        const ids = this.state.selectedRowKeys.map(metaid => ({ metaid }));
        const { data } = await moveMetadataToRecycle(ids);
        if (data && data.code === '200') {
          message.success('已移入回收站');
          this.setState({
            selectedRows: [],
            selectedRowKeys: [],
            canDelete: false
          })
          this.reloadList();
        }
      },
    });
  }

  // 下载文件
  handleDownload(fileName) {
    downloadFile(`${API_BASE_DOWNLOAD_FILE}/fileOperate/downloadExample/${fileName}`);
  }

  // 查看历史版本
  showHistoryVersion() {
    this.props.router.push({
      pathname: '/MetadataDefine/hisrory',
      query: {
        ids: this.state.selectedRowKeys.join(','),
      },
    });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    const { account , metaDataDefine} = this.props;
    const { source } = metaDataDefine;
    const { username } = account;

    this.setState({ 
      canDelete: selectedRowKeys.length == 0 ? false : source.list.every(val=>{
        const findRow = selectedRowKeys.indexOf(val.metaid) > -1;
        if(findRow){
          return val.owner === username;
        }else{
          return true
        }
      }),
      selectedRowKeys, selectedRows,soureFreeze: selectedRows.some(val=>val.sourceType === 1)});
  }

  // 上传文件状态改变
  uploadStatusChange(e) {
    const { router } = this.props;
    this.setState({ uploadFileList: e.fileList });
    const { response } = e.file;
    if (response) {
      if (response.flag === 0) {
        message.success('上传成功');
        this.setState({
          uploadVisible: false,
          uploadFileList: []
        });
        router.push("/MetadataDefine")
      }
    }
  }

  hasCannotEdited() {
    return this.state.selectedRows.some(it => !it.canEdited);
  }

  Buttons = () => {
    return <Row >
      <Col span="14" className="btn_group_10">
        <Empower api="/frontMetadataInfoController/createMetadata">
          <Button type="primary"
            onClick={this.handleAddNew.bind(this)}
          >新建</Button>
        </Empower>
        <Empower api="/frontMetadataInfoController/createMetadata">
          <Button type="primary"
            onClick={this.handleAddNewAcq.bind(this)}>采集表字段
          </Button>
        </Empower>
        <Empower api="/frontMetadataInfoController/exportMetadata">
          <Button type="primary"
            
            /**
             * 选中超过1个则禁用
             * edited by steven leo on 2018.12.04
             */
            disabled={this.state.selectedRowKeys.length != 1}
            onClick={this.handleExport.bind(this)}
          >导出字段</Button>
        </Empower>
        <Empower api="/frontHistoryVersionController/getHistoryVersion">
          <Button type="primary"

            /**
             * 选中超过1个则禁用
             * edited by steven leo on 2018.12.04
             */
            disabled={this.state.selectedRowKeys.length != 1  || this.state.soureFreeze}
            onClick={this.showHistoryVersion.bind(this)}
          >历史版本</Button>
        </Empower>
        <Empower api="/frontMetadataInfoController/moveToRecycle">
          <Button type="primary"

            /**
             * 备注：当前版本开放了直采和非直采都可以删除的功能，
             * 如果要使用判断，可以使用下方注释的内容
             * edited by steven leo on 2018.12.04
             */
            disabled={!this.state.canDelete}
            // disabled={this.state.selectedRowKeys.length<1 || this.hasCannotEdited()|| this.state.soureFreeze }
            onClick={this.handleMoveToRecycle.bind(this)}
          >移入回收站</Button>
        </Empower>
      </Col>
      <Col span="10" style={{textAlign: 'right',fontSize:"16px"}}>
        {/*<Empower api="/frontMetadataInfoController/getRecycle">*/}
          <Link to="/MetadataDefine/drafts"
            style={{marginRight: "20px"}}
          >
            <Icon type="dropbox" />
            <span  style={{marginLeft: "5px"}}>草稿箱</span>
          </Link>
        {/*</Empower>*/}
        <Empower api="/frontMetadataInfoController/getRecycle">
          <Link to="/MetadataDefine/tableRecycle">
            <Icon type="delete" />
            <span style={{marginLeft: "10px"}}>回收站</span>
          </Link>
        </Empower>
      </Col>
    </Row>;
  }

  render() {
    const { location: { query }, metaDataDefine } = this.props;
    const metaType = query.metaType || '1';
    const { source,view } = metaDataDefine;

    return <div>
      {/*<Tabs activeKey={metaType} type="card" style={{margin:10}} onChange={this.handleChangeType.bind(this)}>
        <TabPane tab="事实表" key="1" >{this.Buttons()}</TabPane>
        <TabPane tab="聚合表" key="2">{this.Buttons()}</TabPane>
        <TabPane tab="查找表" key="3">{this.Buttons()}</TabPane>
        <TabPane tab="维度表" key="4">{this.Buttons()}</TabPane>
        <TabPane tab="宽表" key="5">{this.Buttons()}</TabPane>
        <TabPane tab="基础数据表" key="6">{this.Buttons()}</TabPane>
      </Tabs>*/}

      <header>{this.Buttons()}</header>

      <TableList
        showIndex
        rowKey="metaid"
        columns={this.columns}
        dataSource={source.list}
        rowSelection={{
          onChange: this.onChangeAllSelect.bind(this),
          selectedRowKeys: this.state.selectedRowKeys,
        }}
        pagination={{total: source.total}}
        className="padding_20_0"
      />

      {/* 上传样例 */}
      <Modal
        title="上传样例文件"
        visible={this.state.uploadVisible}
        onOk={() => this.setState({ uploadVisible: false, uploadFileList: [] })}
        onCancel={() => this.setState({ uploadVisible: false, uploadFileList: [] })}
        maskClosable={false}
        closable={false}
      >
        <Upload
          name="sourceFile"
          action={uploadExample}
          data={{metaid: metaDataDefine.view.metaid}}
          showUploadList
          fileList={this.state.uploadFileList}
          onChange={this.uploadStatusChange.bind(this)}
          accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        >
          <Button>
            <Icon type="upload" /> 上传样例
          </Button>
          <p style={{ color: '#999', marginTop: 5 }}>(仅支持Excel文件)</p>
        </Upload>
      </Modal>

      {/* 加载编辑器 */}
      {metaDataDefine.editorStep1Visible ? <EditorTableStep1 /> : null}
      {metaDataDefine.editorStep2Visible ? <EditorTableStep2 /> : null}

      {/* 加载查看窗口 */}
      {metaDataDefine.viewStep1Visible ? <ViewTableStep1 /> : null}
      {metaDataDefine.viewStep2Visible ? <ViewTableStep2 metaNameCn={view.metaNameCn ? view.metaNameCn: ""}/> : null}
      {metaDataDefine.viewAcquisitionVisible ? <Acquisition /> : null}

    </div>
  }
}

export default connect(({ metaDataDefine, metadataCommon,account }) => ({
  account,
  metaDataDefine,
  metadataCommon,
}))(AppPage);
