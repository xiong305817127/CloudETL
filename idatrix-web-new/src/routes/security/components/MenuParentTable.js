import  React  from 'react'

import { Table } from 'antd';

const columns = [{
  title: '菜单名称',
  dataIndex: 'name'
}, {
  title: '菜单图标',
  dataIndex: 'icon',
}, {
  title: '菜单路径',
  dataIndex: 'address',
}, {
  title: '显示序号',
  dataIndex: 'num',
}, {
  title: '菜单分类',
  dataIndex: 'type',
}, {
  title: '打开方式',
  dataIndex: 'mode',
}, {
  title: '是否显示',
  dataIndex: 'show',
}, {
  title: '菜单描述',
  dataIndex: 'describe',
}];

const data = [{
  key: '1',
  name: 'John',
  icon:'icon',
  address: 'xxxxxx',
  num: '1',
  type: '菜单',
  mode:'内嵌',
  show:'是',
  describe:'xxxxxxx'
}];

class MenuChildrenTable extends React.Component{

  //3.输出组件页面：
  render(){
    return(
      <div >
        <Table
          columns={columns}
          dataSource={data}
          bordered
          pagination={false}
        />
      </div>

    )
  }
}

export default MenuChildrenTable;
