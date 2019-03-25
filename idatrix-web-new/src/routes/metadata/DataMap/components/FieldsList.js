import { Table } from 'antd';

const columns = [{
  title: '字段中文名',
  dataIndex: 'metaNameCn',
}, {
  title: '字段英文名',
  dataIndex: 'metaNameEn',
}, {
  title: '字段类型',
  dataIndex: 'metaType',
}, {
  title: '长度',
  dataIndex: 'length',
}, {
  title: '字段描述',
  dataIndex: 'desc',
}];

export default ({ dataSource }) => {
  const list = dataSource.map(({ extra, guid }) => ({
    metaNameCn: extra.column_name_cn,
    metaNameEn: guid.field,
    metaType: extra.type,
    length: extra.length,
    desc: extra.desc,
  }));
  return (<Table
    columns={columns}
    className="stripe-table"
    dataSource={list}
  />);
};
