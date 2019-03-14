import React from 'react';
import { Table } from 'antd';
import { listSearchTable } from '../../../services/analysis';
import Empower from '../../../components/Empower';
import Search from '../../../components/Search';
// 定义静态数据：
const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 10,
  }, {
    title: '文件目录名称',
    dataIndex: 'path',
    width: '50%',  //a 标签的 className="word25" 
    render: (text, record) => <Empower api="/hdfs/file/list" style={{color: '#333', cursor: 'default'}}>
      <a href={'#/ListManagementTable/MyDocumentsTable/' + encodeURIComponent(record.path)} title={text}>{text}</a>
    </Empower>,
  // }, {
  //   title: '建立日期',
  //   dataIndex: 'newTime',
  //   width:'150px',
  }, {
    title: '所属组织机构',
    dataIndex: 'organization',
    width: '20%',
  // }, {
  //   title: '所属行业',
  //   key: 'industry',
  //   dataIndex: 'industry',
  //   width:'150px',
  // }, {
  //   title: '主题',
  //   dataIndex: 'theme',
  //   width:'150px',
  // }, {
  //   title: '标签',
  //   key: 'label',
  //   dataIndex: 'label',
  //   width:'150px',
  }, {
    title: '修改日期',
    dataIndex: 'createtime',
    width: '20%',
    render: (text) => (<span>
      {text}
    </span>)
  }
];

class ListManagementTable extends React.Component{
  //1.初始化
  constructor(props){
    super(props);
    this.state ={
      dataSource: [],
    }
  };
  //2.加载列表
  componentWillMount() {
    this.loadList();
  };
  //3.获取搜索值匹配的列表值
  loadList(keyword) {
    const obj = {};
    if (keyword){
      // console.log("搜索：",keyword)
    }
    obj.keyword = keyword;
    listSearchTable(obj).then(res => {
			const { code } = res.data;
      if (code === "200" ){
        this.setState({
          dataSource: res.data.data,
        });
      }
    });
  };
  //4.点击搜索：
  onSearch(val) {
    this.loadList(val);
  };

  render(){
    const dataSource = this.state.dataSource.map((item, index) => {
      item.index = index + 1;
      return item;
    });
    return(
      <div style={{backgroundColor: '#fff',width:"100%"}}>
        {/*搜索：*/}
        <header style={{padding:20,}}>
          <Search
            placeholder="输入名称"
            style={{ width: '500px', height:'40px' }}
            onSearch={value => this.onSearch(value)}
          />
        </header>
        {/*渲染：增删改查其他功能在第二层表格列表：MyDocumentsTable*/}
        <Table style={{padding:20}}
          rowKey={record => record.id}
          dateFormat
          className="stripe-table"
          columns={columns}
          dataSource={dataSource}
          pagination={false}
        />
      </div>
    )}
}

export default ListManagementTable;
