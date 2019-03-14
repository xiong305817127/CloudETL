/**
 * 元数据定义 查看窗口 第二步
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Table, Input, Select, message } from 'antd';
import dbDataType from 'config/dbDataType.config';
import dbTypeValue from 'config/dbTypeValue.config';
import Modal from 'components/Modal';

const getIndexByKey = (columns, key) => {
  let index = -1;
  columns.some((item, i) => {
    if (item.dataIndex === key) {
      index = i;
      return true;
    }
  });
  return index;
};

class View extends React.Component {

  makeColumns = () => {
    const { view } = this.props.metaDataDefine;
    const columns = [
      {
        title: '列族',
        dataIndex: 'colFamily',
        key: 'colFamily',
        width:"5%",
      },
      {
        title: '字段名称',
        dataIndex: 'colName',
        key: 'colName',
        width:"10%",
      },
      {
        title: '字段描述',
        dataIndex: 'description',
        key: 'description',
        width:"10%",
      }, {
        title: '分区顺序',
        dataIndex: 'sequence',
        width:"5%",
      }, {
        title: '数据类型',
        dataIndex: 'dataType',
        key: 'dataType',
        width:"8%",
      }, {
        title: '长度',
        dataIndex: 'length',
        key: 'length',
        width:"7%",
      },{
        title: '精度',
        dataIndex: 'precision',
        key: 'precision',
        width:'7%',
      },{
        title: '是否主键',
        dataIndex: 'isPk',
        key: 'isPk',
        width:"7%",
        render:(text,record)=> text == 1 ? '是' : '否',
      }, /*{
        title: '是否维度',
        dataIndex: 'isDemension',
        key: 'isDemension',
        width:'7%',
        render:(text,record)=> text == 1 ? '是' : '否',
      }, {
        title: '是否度量',
        dataIndex: 'isMetric',
        key: 'isMetric',
        width:'6%',
        render:(text,record)=> text == 1 ? '是' : '否',
      },*/ {
        title: '是否允许为空',
        dataIndex: 'isNull',
        key: 'isNull',
        width:"6%",
        render:(text,record)=> text == 1 ? '是' : '否',
      }/*, {
        title: '安全属性',
        dataIndex: 'secAttribute',
        key: 'secAttribute',
        width:'6%',
      }, {
        title: '引用的数据标准',
        dataIndex: 'standard',
        key: 'standard',
        width:'10%',
      }, {
        title: '索引类型',
        dataIndex: 'indexType',
        key: 'indexType',
        width:'6%',
        render:(text,record)=>{
          if (text == 1) {
            return 'Normal';
          } else if (text == 2) {
            return 'Unique'
          } else if (text == 3) {
            return 'Full Text'
          }
          return '';
        }
      }, {
        title: '索引方法',
        dataIndex: 'indexId',
        key: 'indexId',
        width:'6%',
        render:(text,record)=>{
          if (text == 1) {
            return 'BTREE';
          } else if (text == 2) {
            return 'HASH'
          }
          return '';
        }
      }, {
        title: '分桶定义',
        dataIndex: 'bucketing',
        width:'5%',
      }*/
    ];

    // 如果不是mysql
    if (Number(view.dsType) !== dbTypeValue.mysql) {
      columns.splice(getIndexByKey(columns, 'indexType'), 1); // 删除索引类型
      columns.splice(getIndexByKey(columns, 'indexId'), 1); // 删除索引方法
    }
    // 如果不是hive
    if (Number(view.dsType) !== dbTypeValue.hive) {
      columns.splice(getIndexByKey(columns, 'sequence'), 1); // 删除分区顺序
      columns.splice(getIndexByKey(columns, 'bucketing'), 1); // 删除分桶定义
    }
    // 如果不是hbase
    if (Number(view.dsType) !== dbTypeValue.hbase) {
      columns.splice(getIndexByKey(columns, 'colFamily'), 1); // 删除列族
    }
    // 如果是hive
    if (Number(view.dsType) === dbTypeValue.hive) {
      columns.splice(getIndexByKey(columns, 'isPk'), 1); // 删除主键属性
      // 以下两条由于目前未实现，暂时隐藏
      columns.splice(getIndexByKey(columns, 'sequence'), 1); // 暂时隐藏
      columns.splice(getIndexByKey(columns, 'bucketing'), 1); // 暂时隐藏
    }

    return columns;
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/hideAllView' });
  }

  // 返回基本信息
  handleLastStep() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/showView', step: 1});
  }

  render() {
    const { metaDataDefine, metadataCommon , metaNameCn } = this.props;
    const { viewFields } = metaDataDefine;
    console.log(metaDataDefine,"表结构定义");

    return <Modal
      title={`表结构定义(${metaNameCn})`}
      visible={metaDataDefine.viewStep2Visible}
      onOk={this.handleCancel.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      okText="关闭"
      width={1200}
    >
      <Table
        showIndex
        rowKey="key"
        columns={this.makeColumns()}
        dataSource={viewFields.filter(row => row.status !== 2)}
        pagination={false}
        style={{marginTop:'20px'}}
        className="stripe-table th-nowrap"
      />

    </Modal>
  }
}

export default connect(({ metaDataDefine, metadataCommon }) => ({
  metaDataDefine,
  metadataCommon,
}))(View);
