import React from 'react';
import { Table,Button,Row,Col,Input} from 'antd';
const Search = Input.Search;
//定义静态数据：
const columns =[
  {
    title: '序号',
    key: 'id',
    dataIndex: 'id',
    width: '5%'
  },{
    title: '名称',
    key: 'fileName',
    dataIndex: 'fileName',
    width: '45%'
  },{
    title: '文件大小',
    key: 'fileLen',
    dataIndex: 'fileLen',
    width: '10%'
  },{
    title: '用户',
    key: 'owner',
    dataIndex: 'owner',
    width: '10%'
  },{
    title: '组名',
    key: 'groupName',
    dataIndex: 'groupName',
    width: '10%'
  },{
    title: '权限',
    key: 'permissions',
    dataIndex: 'permissions',
    width: '10%'
  },{
    title: '更新日期',
    key: 'modifiedTime',
    dataIndex: 'modifiedTime',
    width: '10%'
  }
];
const data = [];
for (let i = 1; i <= 3; i++) {
  data.push({
    key: i,
    id: `${i}`,
    "fileName": "国库科", //文件或目录名称
    "filePath": "hdfs://yscluster/财政局/国库科", //路径
    "fileLen": 0, //文件大小
    "modifiedTime": "2017-06-15 10:39:18.73", //修改时间
    "accessTime": "1970-01-01 08:00:00.0", //访问时间
    "replicates": 0, //文件副本数
    "owner": "admin", //所有者
    "groupName": "hadoop", //所属组名
    "permissions": "rwxr-xr-x", //权限
    "file": false //是否是文件
  });
}
class  RecycleBinTable extends React.Component{
  //1.初始化
  constructor(props){
    super(props);
    this.state ={
      dataSource: [],
    };
  }
  //2.下载、删除

  render(){
    return(
      <div>
        {/*下载、删除*/}
        <Row >
          <Col span={12} offset={8}>
            <Search
              placeholder="输入名称"
              style={{ width: '500px',height:'40px' }}
              onSearch={value => console.log('点击了搜索')}
            />
          </Col>
        </Row >
        <Row style={{margin:'20px'}}>
          <Col span={20}></Col>
          <Col span={2}><Button >下载</Button></Col>
          <Col span={2}><Button >删除</Button></Col>
        </Row>
        <Row style={{height:'700px'}}>
          <Col span={24}>
            <Table
              rowKey="id"
              className="stripe-table"
              columns={columns}
              dataSource={data}
              pagination={false}
              style={{margin:'auto 20px'}}
            />
          </Col>
        </Row>
      </div>
    )
  }
}

export default RecycleBinTable;
