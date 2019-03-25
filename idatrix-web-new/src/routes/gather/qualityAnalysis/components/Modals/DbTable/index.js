/**
 * Created by Administrator on 2017/3/13.
 */
import React from "react";
import { Row, Col, Button, Menu, Table, Select, Spin } from "antd";
import Modal from "components/Modal.js";
import { connect } from "dva";

const Option = Select.Option;

const DbTable = ({ analysisDbtable, dispatch }) => {
  const {
    visible,
    loading,
    fuc,
    tableFields,
    selectKeys,
    tableList,
    loading1,
    viewList,
    viewType,
    tables,
    owner,
    id
  } = analysisDbtable;

  const columns = [
    {
      title: "表字段名",
      dataIndex: "name",
      width: "100%"
    }
  ];

  const handleSure = () => {
    if (id !== 0 && id) {
      let tableInfo = {};
      tableList.forEach(index => {
        index.forEach(r => {
          if (r.id + "" === id) {
            tableInfo.table = r.name;
            tableInfo.tableId = r.id;
            tableInfo.tableType = r.type;
          }
        });
      });
      fuc(selectKeys, id, tableInfo);
      handleHide();
    } else {
      message.warn("请先选择表或视图！");
    }
  };

  const handleHide = () => {
    selectKeys.splice(0);
    dispatch({
      type: "analysisDbtable/hide"
    });
  };

  const handleChange = e => {
    let newTables = tableList[0];
		if(e === "view"){
			newTables = tableList[1];
		}

    dispatch({
      type: "analysisDbtable/show",
      payload: {
				viewType: e,
				tables:newTables
      }
    });
  };

  const handleClick = e => {
    dispatch({
			type: "analysisDbtable/queryTableFields",
      payload: {
				owner,
        id: e.key
      }
    });
  };
  const rowSelection = {
    onChange: selectedRowKeys => {
      dispatch({
        type: "analysisDbtable/show",
        payload: {
          selectKeys: selectedRowKeys
        }
      });
    }
  };

  return (
    <Modal
      visible={visible}
      title="选择字段"
      wrapClassName="vertical-center-modal out-model"
      okText="Create"
      footer={[
        <Button key="submit" type="primary" size="large" onClick={handleSure}>
          确定
        </Button>,
        <Button key="back" size="large" onClick={handleHide}>
          取消
        </Button>
      ]}
      onCancel={handleHide}
      maskClosable={false}
    >
      <Row style={{ marginBottom: "20px" }}>
        <Col
          span={5}
          style={{ height: "100%", lineHeight: "28px", textAlign: "right" }}
        >
          表类型：
        </Col>
        <Col span={16}>
          <Select
            value={viewType}
            onChange={handleChange}
            style={{ width: "100%" }}
            mode="vertical"
          >
            {viewList.map(index => {
              return (
                <Option key={index.value} value={index.value}>
                  {index.label}
                </Option>
              );
            })}
          </Select>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col
          span={10}
          style={{
            height: 300,
            overflowY: "scroll",
            overflowX: "hidden",
            border: "1px solid #e9e9e9",
            borderRadius: "4px"
          }}
        >
          <div>
            <Spin spinning={loading1}>
              <div style={{ width: 200, height: 300 }}>
                <Menu
                  onClick={handleClick}
                  style={{ width: 200 }}
                  mode="vertical"
                >
                  {tables.map(index => {
                    return (
                      <Menu.Item key={index.id}>{index.name}</Menu.Item>
                    );
                  })}
                </Menu>
              </div>
            </Spin>
          </div>
        </Col>
        <Col span={14}>
          <div id="dbtable">
            <Table
              loading={loading}
              bordered={false}
              size={"small"}
              rowSelection={rowSelection}
              scroll={{ y: 245 }}
              pagination={false}
              dataSource={tableFields}
              columns={columns}
            />
          </div>
        </Col>
      </Row>
    </Modal>
  );
};

export default connect(({ analysisDbtable }) => ({
  analysisDbtable
}))(DbTable);
