/**
 * Created by Administrator on 2017/8/24.
 */
import  React  from 'react';
import { Layout,Button,Icon,Popconfirm, messag,Tooltip} from  'antd';
import { connect } from 'dva';
import TableList from "../../../../components/TableList";

class Platform extends React.Component{

  // 平台侧
  columns  = [
    {
      title: '组件名称',
      dataIndex: 'serverName',
      key: 'serverName'
    }, {
      title: '连接地址',
      dataIndex: 'connUrl',
      key: 'connUrl',
    }
  ];

  render(){
    const { platformServerInfo } = this.props.frontendfesmanage;
    return(
      <section style={{backgroundColor: '#fff'}} className="padding_20">
          <TableList
            rowKey="serviceId"
            showIndex
            onRowClick={(record)=>{console.log(record)}}
            columns={this.columns}
            dataSource={platformServerInfo.rows}
            className="th-nowrap"
            pagination={{total: platformServerInfo.total}}
          />
        </section>
    )
  }
}

export default connect(({ frontendfesmanage }) => ({
  frontendfesmanage,
}))(Platform);
