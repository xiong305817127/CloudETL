import React from 'react';
import { Table, Button, Row, Col, Icon, Breadcrumb, message } from 'antd';
import { getListTable, deleteListTable, fileDownloadApi } from 'services/analysis';
import Empower from 'components/Empower';
import {hashHistory} from "dva/router"
import Upload from 'components/Upload';
import downRequest from 'utils/downRequest';
import { downloadFile } from 'utils/utils';
import Modal from 'components/Modal';
import { API_BASE_ANALYSIS } from 'constants';
import filesize from 'filesize';
import NewListButtton from "../components/analysisTable/NewListButtton";
import ReNameButtton from "../components/analysisTable/ReNameButtton";

const confirm = Modal.confirm;
const columns =[
  {
    title: '类型',
    key: 'index',
    dataIndex: 'index',
    width: '5%',
    render: (text, record) => {
      if (record.file) {
        return <Icon type="file" />
      } else {
        return <Icon type="folder" style={{color: '#08c'}} />
      }
    },
  },{
    title: '名称',
    key: 'fileName',
    dataIndex: 'fileName',
    width: '40%',
    render: (text, record) => {
      const filePath = encodeURIComponent(record.filePath);
      if (record.file) {
        return (<span className="wordAll" title={text}>{text}</span>)
      } else {
        return (<Empower api="/hdfs/file/list" style={{color: '#333', cursor: 'default'}}>
          <a href={'#/ListManagementTable/MyDocumentsTable/' + filePath} className="wordAll" title={text}>{text}</a>
        </Empower>);
      }
    },
  },{
    title: '文件大小',
    key: 'fileLen',
    dataIndex: 'fileLen',
    width: '10%',
    render: (text, record) => <span>{record.file ? filesize(text) : ''}</span>
  },{
    title: '用户',
    key: 'owner',
    dataIndex: 'owner',
    width: '10%',
  },{
    title: '组名',
    key: 'groupName',
    dataIndex: 'groupName',
    width: '10%',
  },{
    title: '权限',
    key: 'permissions',
    dataIndex: 'permissions',
    width: '10%',
  },{
    title: '更新日期',
    key: 'modifiedTime',
    dataIndex: 'modifiedTime',
    width: '15%',
  }
];
class  MyDocumentsTable extends React.Component{
  // 1.初始化
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      selectedFile: {},
      selectedFilePath: '',
      selectedFileName: '',
      selectedFileIsFile: false,
      uploadShow: false,
      pathList: [],
      selectedRowKeys: [],
    };
  }
  componentWillMount() {
    const { path } = this.props.params;
    // console.log(111)
    // console.log(path)
    this.createBreadcrumb(path);
    this.loadList(path);
  }
  componentWillReceiveProps(nextProps) {
    const { path } = nextProps.params;
    this.createBreadcrumb(path);
    this.setState({
      selectedFilePath: '',
      selectedFileName: '',
      selectedFileIsFile: false,
      selectedRowKeys: [],
    });
    this.loadList(path);
  }
  // 创建面包屑
  createBreadcrumb(path) {
    const pathSplit = path.split('/');
    const pathList = [];
    pathSplit.reduce((path, item) => {
      path =`${path}/${item}`;
      pathList.push({
        title: item, path
      })
      return path;
    });
    this.setState({ pathList });
  }
  // 修改
  editSuccess() {
    const { path } = this.props.params;
    this.setState({
      selectedFilePath: '',
      selectedFileName: '',
      selectedFileIsFile: false,
      selectedRowKeys: [],
    });
    this.loadList(path);
    this.setState({
      selectedFilePath: '',
      selectedFileIsFile: '',
    });
  }
  // 2.加载列表
  loadList(filePath) {
    getListTable({ filePath }).then(({ data }) => {
			const { code } = data;
      if (code === "200") {
        const list = data.data || [];
        const folders = list.filter(item => !item.file);
        const files = list.filter(item => item.file);
        this.setState({
          dataSource: folders.concat(files),
        });
      }
    });
  }
  //上传文件状态改变
  uploadStatusChange(e){
    this.setState({ uploadFileList: e.fileList });
    const { response } = e.file;
    if (response) {
      if (response.code === "200") {
        message.success('上传成功');
        // this.editSuccess();
      } 
       else {
         message.error(response.msg);
       }
    }
  }
  DownloadClick() {
    // const url = window.location.href.replace(/\/#.*$/, '') + fileDownloadApi;
    const { fileName, filePath, fileLen } = this.state.selectedFile;
    downRequest(fileDownloadApi,{
			method:"POST",
			headers:{
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body:{
				fileName,
				filePath,
				fileLen
			}
    });

    this.setState({
      selectedFilePath: '',
      selectedFileIsFile: '',
    });
  }
  // 移入回收站:删除后立即刷新页面,对话框居中
  handleDeleteFields = ()=>{
    const deleteList = [this.state.selectedFilePath];
    const cbDeleteSuccess = this.editSuccess.bind(this);
    const content = this.state.selectedFileIsFile ? '确定要删除该文件吗？' : '确定要删除该目录吗？';
    confirm({
      content,
      onOk() {
        deleteListTable(deleteList).then(({ data })=>{
          if (data.code === "200") {
             message.success('删除成功');
            cbDeleteSuccess();
            this.setState({
              selectedFilePath: '',
              selectedFileIsFile: '',
            });
          } 
          
          // else {
          //   message.error(data.msg);
          // }
        });
      },
    });
  };
  // 选中操作
  handleSelect(selectedRowKeys, selectedRows) {
    const selectedFile = selectedRows[0];
    const selectedFilePath = selectedRows[0].filePath;
    const selectedFileName = selectedRows[0].fileName;
    const selectedFileIsFile = selectedRows[0].file;
    this.setState({ selectedRowKeys, selectedFile, selectedFilePath, selectedFileName, selectedFileIsFile })
  }

  //上传成功后刷新页面
  handleUpdateList(){
    const { path } = this.props.params;
    this.loadList(path);
    this.setState({ uploadVisible: false, uploadFileList: [] })
  }
  //点击取消返回
  goBack=()=>{
    hashHistory.goBack();
  }
  render() {
    const { dataSource, pathList } = this.state;
    const { path } = this.props.params;
    return(
      <div id="MyDocumentsTable"  style={{ margin: '0px 10px', backgroundColor:'#ffffff'}}>
        <Breadcrumb style={{ padding: '20px'}}>
          <Breadcrumb.Item><a href="#/ListManagementTable">目录管理</a></Breadcrumb.Item>
          {pathList.map((item, index) => {
            const path = encodeURIComponent(item.path);
            return (<Breadcrumb.Item key={index}>
              {index < pathList.length - 1 ? (
                <a href={`#/ListManagementTable/MyDocumentsTable/${path}`}>{item.title}</a>
              ) : (<span>{item.title}</span>)}
            </Breadcrumb.Item>);
          })}

        </Breadcrumb>
        
        {/*新建、修改、上传、下载、删除、回收站*/}
        <Row>
          <Col  style={{margin:'20px'}}>
            <Button style={{margin:"10px"}} type="primary" onClick={this.goBack}>返回</Button>
            {/*新建*/}
            <div style={{ display: 'inline-block' }}>
              <NewListButtton
                filePath={path}
                onSuccess={this.editSuccess.bind(this)}
              />
            </div>&nbsp;&nbsp;
            {/*重命名*/}
            <div style={{ display: 'inline-block' }}>
              <ReNameButtton
                oldFileName={this.state.selectedFileName}
                disabled={!this.state.selectedFilePath}
                filePath={this.state.selectedFilePath}
                onSuccess={this.editSuccess.bind(this)}
              />
            </div>&nbsp;&nbsp;
            {/*上传*/}
            <Empower api="/hdfs/file/upload">
              <Button type="primary" onClick={() => this.setState({ uploadVisible: true})}>上传文件</Button>
            </Empower>&nbsp;&nbsp;
            {/*下载*/}
            <Empower api="/hdfs/file/download">
              <Button  type='primary' disabled={!this.state.selectedFileIsFile} onClick={this.DownloadClick.bind(this)}>下载文件</Button>
            </Empower>&nbsp;&nbsp;
            {/*删除*/}
            <Empower api="/hdfs/file/delete">
              <Button  type='primary' disabled={!this.state.selectedFilePath} onClick={this.handleDeleteFields.bind(this)}>删除</Button>
            </Empower>
          {/*<Col span={14}></Col>
          <Col span={2}><a  href="#RecycleBinTable">回收站</a></Col>*/}
          </Col>

          <Col  style={{margin:'20px',minheight:'650px'}} >
          <Table
            rowKey={record => record.filePath}
            dateFormat
            pagination={false}
            columns={columns}
            dataSource={dataSource}
            rowSelection={{
              type: 'radio',
              onChange: (selectedRowKeys, selectedRows) => this.handleSelect(selectedRowKeys, selectedRows),
              selectedRowKeys: this.state.selectedRowKeys,
            }}
            className="stripe-table"
          />
          </Col>
        </Row>

        <Modal
          title="上传文件"
          visible={this.state.uploadVisible}
          onOk={this.handleUpdateList.bind(this)}
          onCancel={() => this.setState({ uploadVisible: false, uploadFileList: [] })}
          maskClosable={false}
          closable={false}
        >
          <Upload
            name="file"
            action={`${API_BASE_ANALYSIS}/hdfs/file/upload`}
            data={{filePath: path}}
            showUploadList={true}
            fileList={this.state.uploadFileList}
            onChange={this.uploadStatusChange.bind(this)}
          >
            <Button>
              <Icon type="upload" /> 选择文件
            </Button>
          </Upload>
        </Modal>

      </div>
    )
  }
}
export default MyDocumentsTable;
