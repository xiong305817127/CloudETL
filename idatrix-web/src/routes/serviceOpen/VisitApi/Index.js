import React from 'react';
import { connect } from 'dva';
import { Row, Col} from 'antd';
import { getServiceList } from '../../../services/service';
import Empower from '../../../components/Empower';
import TableList from '../../../components/TableList';

class ServiceTableVisitApi extends React.Component{
  //1.初始化状态：
  constructor(props){
    super(props);
    this.state ={
      dataSource:[],
    };
  }
  //2.页面加载前执行：
  componentWillMount() {
    getServiceList().then(res => {
      if (res.data && res.data.data) {
        this.setState({
          dataSource: res.data.data,
        });
      }
    });
  }
  columns = [
    {
      title: 'API列表',
      dataIndex: 'action',
      key: 'action',
      width: 100,
      render:(text, record) => (
        <Empower api="/service/detail" style={{color: '#333', cursor: 'default'}}>
          <a href={'#/service/ServiceTableVisitApi/api/' + record.id}>{record.serviceCode}</a>
        </Empower>
      )
    }, {
      title: '是否作废',
      dataIndex: 'obsoleted',
      key: 'obsoleted',
      width: 50,
      render:(text, record) => (
        <span>{record.obsoleted ? '是' : '否'}</span>
      )
    }, {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 50,
    }, {
      title: '描述',
      dataIndex: 'title',
      key: 'title',
      width: 100,
    }];
  //3.输出组件页面：
  render() {
    const data = this.state.dataSource;
    return(
      <Row style={{backgroundColor:'#fff',minHeight:700}}>
        {
          Object.keys(data).map((key) => {
            const item = data[key];
            return (
              <Col key={key} span={24}  style={{padding:'0px 20px'}}>
                <TableList
                  rowKey={record => record.id}
                  showIndex
                  dataSource={item}
                  columns={this.columns}
                  className="th-nowrap"
                  pagination={false}
                  title={() => key}
                />
              </Col>
            );
          })
        }
      </Row>
    )
  }
}

export default connect()(ServiceTableVisitApi);
