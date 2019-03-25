//一、环境
import React from "react";
import { connect } from 'dva';
import { Form,Input,Table } from 'antd';
import TableList from '../../../../components/TableList';//自定义表格
const FormItem = Form.Item;
const columns = [
  { title: '名称', dataIndex: 'name', key: 'name', width: '20%' },
  { title: '类型', dataIndex: 'type', key: 'type', width: '15%' },
  { title: '示例值', dataIndex: 'sample', key: 'sample', width: '30%' },
  { title: '描述', dataIndex: 'description', key: 'description', width: '40%'},
];

let i =0;

//二、渲染
class HttpClientInput extends React.Component {
  constructor(props){
    super(props);
  }
  getChildren(data){
    data.map(index=>{
      index.key = i++;
      if(index.typeFields){
        index.children = index.typeFields;
        return this.getChildren(index.typeFields)
      }
      return index;
    });

    return data;
  }
  render() {
    const data = this.getChildren(this.props.data);
    return (
      <TableList
        columns={columns}
        dataSource={data}
        pagination={false}
      />
    );
  }
}
//三、传参、调用：
const HttpInput = Form.create()(HttpClientInput);
export default connect()(HttpInput);
