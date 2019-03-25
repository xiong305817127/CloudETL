import  React  from 'react'
import { Table} from 'antd';

const columns = [
  {
  title: '名称',
  dataIndex: 'name',
  key: 'name',
}, {
  title: '类型',
  dataIndex: 'type',
  key: 'type',
}, {
  title: '是否必须',
  dataIndex: 'must',
  key: 'must',
}, {
  title: '描述',
  dataIndex: 'description',
  key: 'description',
  render:(text) => (<div className="word25" title={text}>{text}</div>)
}
];

class CommonStaticApiRequest extends React.Component{

  state = {
    dataSource: [{
      key: '1',
      name: 'method',
      type: 'string',
      must: '是',
      description: 'API接口名称'
    }, {
      key: '2',
      name: 'app_key',
      type: 'string',
      must: '是',
      description: '分配给应用的AppKey'
    }, {
      key: '3',
      name: 'app_secret',
      type: 'string',
      must: '是',
      description: '分配给应用的AppSecret'
    }, /*{
      key: '4',
      name: 'sign_method',
      type: 'string',
      must: '是',
      description: 'xxxxxxxxxxxxxxx'
    },*/ {
      key: '5',
      name: 'sign',
      type: 'boolean',
      must: '',//动态数据
      description: 'API输入参数签名结果'
    }, {
      key: '6',
      name: 'session',
      type: 'boolean',
      must: '',//动态数据
      description: '用户登录授权成功后，颁发给应用的授权信息，详细介绍请点击这里。当此API的标签上注明：“需要授权”，则此参数必传；“不需要授权”，则此参数不需要传；“可选授权”，则此参数为可选。'
    }]
  }

  componentWillMount() {
    this.mergeDataSource(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.mergeDataSource(nextProps);
  }

  mergeDataSource(props) {
    const { data } = props;
    const dataSource = this.state.dataSource.map(item => {
      switch (item.name) {
        case 'session':
          item.must = data.needInSession ? '是' : '否';
        break;
        case 'sign':
          item.must = data.ignoreSign ? '否' : '是';
        break;
      }
      return item;
    });
    this.setState({ dataSource });
  }

  //3.输出组件页面：
  render() {
    return(
      <div id="ServiceTableApi">
        <h3 style={{margin:'20px 0px'}}>b.公共请求参数：</h3>
        <Table className=" th-nowrap stripe-table" dataSource={this.state.dataSource} columns={columns} pagination={false}/>
      </div>
    )
  }
}

export default CommonStaticApiRequest;

