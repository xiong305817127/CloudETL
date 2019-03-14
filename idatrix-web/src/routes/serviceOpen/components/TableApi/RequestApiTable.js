import  React  from 'react'
import { Table} from 'antd';

const columns = [{
  title: '名称',
  dataIndex: 'name',
  key: 'name',
  width: 100,
}, {
  title: '类型',
  dataIndex: 'type',
  key: 'type',
  width: 100,
}, {
  title: '是否必须',
  dataIndex: 'required',
  key: 'required',
  render(text, record){return record.required ? '是' : '否';},
  width: 100,
}, {
  title: '示例值',
  dataIndex: 'sample',
  key: 'sample',
  width: 100,
}, {
  title: '更多限制',
  dataIndex: 'limited',
  key: 'limited',
  width: 100,
}, {
  title: '描述',
  dataIndex: 'description',
  key: 'description',
  width: 100,
}];

class RequestApiTable extends React.Component{
  //3.输出组件页面：
  render(){
    const data = (this.props.data || []).map((row, index) => {
      row.key = index;
      return row;
    });
    return(
      <div id="ServiceTableApi">
        <Table className="th-nowrap stripe-table" dataSource={data} columns={columns} pagination={false}/>
      </div>
    )
  }
}

export default RequestApiTable;
