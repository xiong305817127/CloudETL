// 数据表格预览组件
import React from 'react';
import Modal from 'components/Modal';
import PropTypes from 'prop-types';
import TableList from '../../../components/TableList';

const columns = [
  {
    title: '字段名称',
    dataIndex: 'colName',
  }, {
    title: '字段描述',
    dataIndex: 'description',
  },
];

class ViewTable extends React.Component {

  render() {
    return (<Modal
      title={(this.props.tableName || '') + ' 表字段列表'}
      visible={this.props.visible}
      onOk={this.props.onClose}
      onCancel={this.props.onClose}
    >
      <TableList
        rowKey="id"
        columns={columns}
        dataSource={this.props.data}
        pagination={false}
        bordered
      />
    </Modal>);
  }
}

ViewTable.propTypes = {
  data: PropTypes.array.isRequired,
  tableName: PropTypes.string,
  visible: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ViewTable;
