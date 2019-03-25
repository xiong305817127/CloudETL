/**
 * 从现有表导入字段
 */
import React from 'react';
import { connect } from 'dva';
import { Row, Col, Tabs, Table, message } from 'antd';
import { getFieldsById } from 'services/metadataDefine';
import { deepCopy } from 'utils/utils';
import Modal from 'components/Modal';

import ExistingTables from './ExistingTables';

import Style from '../style.css';

const TabPane = Tabs.TabPane;

class Import extends React.Component {
  state = {
    panes: [],
    activeKey: '',
    selectedRows1: [],
    selectedRowKeys1: [],
    selectedRows2: [],
    selectedRowKeys2: [],
    dataSource1: [],
    dataSource2: [],
    tablesVisible: true,
  };

  // 现有表全选操作
  onChangeAllSelect1(selectedRowKeys1, selectedRows1) {
    const { dataSource2, selectedRowKeys2, selectedRows2 } = this.state;
    selectedRows1.map(row => {
      console.log(row)
      if (!dataSource2.some(it => it.id === row.id)) {
        dataSource2.push(row);
        selectedRows2.push(row);
        selectedRowKeys2.push(row.id);
      }
    });
    this.setState({ selectedRowKeys1, selectedRows1, dataSource2, selectedRows2, selectedRowKeys2 });
  }

  // 新定义表全选操作
  onChangeAllSelect2(selectedRowKeys2, selectedRows2) {
    this.setState({ selectedRowKeys2, selectedRows2, dataSource2: selectedRows2 });
  }

  handleSubmit() {
    const fields = [];
    this.state.selectedRows2.forEach((it) => {
      // const f = deepCopy(it);
      // delete f.id;
      // delete f.length;
      // delete f.dataType;
      const f = {
        colName: it.colName,
        description: it.description,
        dataType:it.dataType.toLowerCase(),
        dsType: it.dsType,
        /**
         * add `length` from `it`
         * fixed by Steven Leo
         */
        length: it.length
      };
      fields.push(f);
    });

    this.props.onClose();
    this.props.onOk(fields);
  }

  handleCancel() {
    this.props.onClose();
  }

  // 切换标签
  handleChangeTab = async (activeKey) => {
    this.setState({ activeKey });
    const query = { metaid: activeKey };
    const { data } = await getFieldsById(query);
    const dataSource1 = data && data.data || [];
    this.setState({ dataSource1, selectedRows1: [], selectedRowKeys1: [] });
  };

  // 编辑标签
  handleEditTab = (targetKey, action) => {
    if (action === 'add') {
      this.handleAddTab();
    } else if (action === 'remove') {
      this.handleRemoveTab(targetKey);
    }
  };

  // 新增标签
  handleAddTab = () => {
    this.setState({ tablesVisible: true });
  };

  // 删除标签
  handleRemoveTab = (targetKey) => {
    if (this.state.panes.length <= 1) return;
    const panes = this.state.panes.filter(pane => pane.key !== targetKey);
    let activeKey = this.state.activeKey;
    if (activeKey === targetKey) {
      activeKey = panes[panes.length - 1].key;
    }
    this.setState({ panes, activeKey });
  };

  // 选择表
  handleChooseTable = (tables) => {
    const { panes } = this.state;
    tables.forEach((item) => {
      if (!panes.some(p => p.key === item.metaid)) {
        panes.push({
          key: item.metaid,
          title: item.metaName,
          dsType: item.dsType
        });
      }
    });
    this.setState({ panes});
    if (tables.length > 0) {
      this.handleChangeTab(tables[0].metaid);
    }
  }

  // 现有表列
  columns1 = [{
    title: '字段名称',
    width: 200,
    dataIndex: 'colName',
  }, {
    title: '字段描述',
    dataIndex: 'description',
  }];

  // 新定义表列
  columns2 = [{
    title: '字段名称',
    width: 200,
    dataIndex: 'colName',
  }, {
    title: '字段描述',
    dataIndex: 'description',
   /*}, {
     title: '数据类型',
     dataIndex: 'dataType',*/
  // }, {
  //   title: '长度',
  //   dataIndex: 'length',
  }];

  render() {

    return (<Modal
      title="请点击+号选择现有元数据表，并把需要的字段添加到新表的元数据定义中"
      visible
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      closable={false}
      okText="保存"
      width={1200}
      zIndex={1010}
    >
      <Row>
        <Col span="9">
          <header className={Style['import-header']}>现有表的字段列表（勾选字段后添加到新表中）</header>
          <Tabs
            onChange={this.handleChangeTab}
            activeKey={this.state.activeKey}
            type="editable-card"
            onEdit={this.handleEditTab}
            tabBarStyle={{ marginBottom: 0 }}
          >
            {this.state.panes.map(pane => (
              <TabPane tab={pane.title} key={pane.key} closable={pane.closable}>
                <Table
                  showIndex
                  rowKey="id"
                  columns={this.columns1}
                  dataSource={
                    this.state.dataSource1
                    ? 
                    this.state.dataSource1.map(val=>({
                      ...val,
                      dsType: pane.dsType
                    }))
                    :[]
                  }
                  rowSelection={{
                    onChange: this.onChangeAllSelect1.bind(this),
                    selectedRowKeys: this.state.selectedRowKeys1,
                  }}
                  pagination={false}
                  style={{ borderTop: 0 }}
                  className={Style['import-table']}
                  scroll={{ y: 400 }}
                  size="small"
                />
              </TabPane>
            ))}
          </Tabs>
        </Col>
        <Col span="15" style={{ paddingLeft: 20 }}>
          <header className={Style['import-header']}>新定义表的字段列表（不勾选则去除字段）</header>
          <Table
            showIndex
            rowKey="id"
            columns={this.columns2}
            dataSource={this.state.dataSource2}
            rowSelection={{
              onChange: this.onChangeAllSelect2.bind(this),
              selectedRowKeys: this.state.selectedRowKeys2,
            }}
            pagination={false}
            style={{ marginTop: 31 }}
            className={Style['import-table']}
            scroll={{ y: 400 }}
            size="small"
          />
        </Col>
      </Row>

      {this.state.tablesVisible ? (
        <ExistingTables 
        onClose={() => { this.setState({ tablesVisible: false }); }} 
        onOk={this.handleChooseTable} />
      ) : null}
    </Modal>);
  }
}

export default connect()(Import);
