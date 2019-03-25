import  React  from 'react'
import { Table} from 'antd';

const { Column} = Table;

//默认列表内容：1.2.3
const data = [{
  key: '1',
  name: '张三',
  address: '操作路径11111',
}, {
  key: '2',
  name: '李四',
  address: '操作路径22222',
}, {
  key: '3',
  name: '王五',
  address: '操作路径33333',
}];


class MenuChildrenModel extends React.Component{

  //3.输出组件页面：
  render(){
    return(
      <div >
        <Table dataSource={data} pagination={false}>
          <Column
            title="操作"
            dataIndex="name"
            key="name"
          />
          <Column
            title="操作路径"
            dataIndex="address"
            key="address"
          />

          <Column
            title="操作帮助"
            key="action"
            render={(text, record) => (
              <span>
                <a href="#">操作者：{record.name}</a>
                <span className="ant-divider" />
                <a href="#">编辑</a>
                <span className="ant-divider" />
                <a href="#" className="ant-dropdown-link">
                 删除
                </a>
              </span>
            )}
          />

        </Table>
      </div>

    )
  }
}

export default MenuChildrenModel;
