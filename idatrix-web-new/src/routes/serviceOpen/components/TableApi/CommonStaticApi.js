import  React  from 'react'
import { Table} from 'antd';
const columns = [{
  title: '环境',
  dataIndex: 'name',
  key: 'name',
  width: 100,//静态数据可以定宽度
}, {
  title: 'HTTP请求路径',
  dataIndex: 'http',
  key: 'http',
  width: 150,
}, {
  title: 'HTTPS请求路径',
  dataIndex: 'https',
  key: 'https',
  width: 150,
}];
//静态数据改为动态数据：
const staticData= [{
  key: '1',
  name: '正式环境',
  http: 'http://<ip>:<port>/<context>/router',
  https: 'https://eco.taobao.com/router/rest'
}, {
  key: '2',
  name: '沙箱环境',
  http: 'http://<ip>:<port>/<context>/router',
  https: 'https://eco.taobao.com/router/rest'
}];
class CommonStaticApi extends React.Component{
  constructor(props){
    super(props);
    this.state ={
      dataSource: {}
    }
  }
  //3.输出组件页面：
  render(){
    const data = this.props.data;
    const dataSource = [];
    dataSource.push({
      key:1,
      name: '正式环境',
      http: data['http.production.address'],
      https: data['https.production.address'],
    });
    dataSource.push({
      key:2,
      name: '沙箱环境',
      http: data['http.sandbox.address'],
      https: data['https.sandbox.address'],
    });
    return(
      <div id="ServiceTableApi">
        <h3 style={{marginBottom:20}}>a.请求地址：</h3>
        <Table className="th-nowrap stripe-table" dataSource={dataSource} columns={columns} pagination={false} />
      </div>
    )
  }
}

export default CommonStaticApi;

