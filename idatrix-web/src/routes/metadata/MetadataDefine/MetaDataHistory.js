/**
 * 数据表类历史版本
 * @model  ./metaDataHistory.model.js
 */
import React from "react";
import { connect } from "dva";
import { Link } from "react-router";
import { Button, Row, Col, message, List, Icon } from "antd";
import Empower from "components/Empower";
import TableList from "components/TableList";
import { deleteHistoryVersion } from "services/metadataDefine";
import { getLabelByTreeValue } from "utils/metadataTools";
import { deepCopy } from "utils/utils";
import ViewTableStep1 from "./components/ViewTableStep1";
import ViewTableStep2 from "./components/ViewTableStep2";
import Modal from "components/Modal";

import Style from "./style.css";

const dictionary = {
  publicStats: [
    "状态",
    text => {
      return text == 1 ? "授权公开" : "未公开";
    }
  ],
  remark: ["备注"],
  owner: ["所有者"],
  creator: ["创建者"],
  dbDatabasename: ["数据库"],
  dsType: [
    "类型",
    text => {
      return {
        "2": "Oracle",
        "3": "MySQL",
        "4": "Hive",
        "5": "Hbase",
        "14": "DM",
        "8": "PostgreSql"
      }[text];
    }
  ],
  dept: [
    "部门",
    (text, departmentsOptions) => {
      return getLabelByTreeValue(text, departmentsOptions);
    }
  ],
  metaNameEn: ["英文名"],
  metaNameCn: ["中文名"]
};

class AppPage extends React.Component {
  state = {
    selectedRows: [],
    selectedRowKeys: [],
    metaNameCn: ""
  };

  columns = [
    {
      title: "表中文名称",
      dataIndex: "metaNameCn",
      key: "metaNameCn"
    },
    {
      title: "表英文名称",
      dataIndex: "metaNameEn",
      key: "metaNameEn"
    },
    {
      title: "版本",
      dataIndex: "version",
      key: "version"
    },
    {
      title: "所属组织",
      dataIndex: "dept",
      key: "dept",
      render: text => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text, departmentsOptions);
      }
    },
    {
      title: "拥有者",
      dataIndex: "owner",
      key: "owner"
    },
    {
      title: "创建日期",
      dataIndex: "createTime",
      key: "createTime"
    },
    {
      title: "备注",
      dataIndex: "remark",
      key: "remark",
      render: text => (
        <div className="word25" title={text}>
          {text}
        </div>
      )
    },
    {
      title: "查看版本字段",
      render: (text, record) => (
        <Button size="small" onClick={this.handleView(record)}>
          <Icon type="bars"/>
          查看字段
        </Button>
      )
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    const { query } = this.props.routing.locationBeforeTransitions;
    dispatch({ type: "metadataCommon/getDepartments" });
    dispatch({
      type: "metaDataDefine/getMetaTableBaseInfoByMetaId",
      id: query.ids
    });
  }

  // 刷新列表
  reloadList() {
    const {
      dispatch,
      location: { query }
    } = this.props;
    dispatch({ type: "metaDataHistory/getList", payload: query });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 永久删除
  handleDelete() {
    Modal.confirm({
      content: "删除后将无法找回，确认要执行此操作吗？",
      onOk: async () => {
        const query = { ids: this.state.selectedRowKeys.join(",") };
        const { data } = await deleteHistoryVersion(query);
        if (data && data.code === "200") {
          message.success("已删除");
          this.reloadList();
        }
      }
    });
  }

  // 查看
  handleView(record) {
    return () => {
      const { dispatch } = this.props;
      dispatch({ type: "metaDataDefine/showView", step: 2 });
      dispatch({
        type: "metaDataDefine/getVersionDetails",
        metaid: record.metaid,
        version: record.version
      });
      this.setState({
        metaNameCn: record.metaNameCn
      })
    };
  }

  render() {
    const { metaDataHistory, metaDataDefine } = this.props;
    const { list, total } = metaDataHistory;
    const { historyData } = metaDataDefine;

    return (
      <div style={{ padding: 20, backgroundColor: "#fff" }}>
        <div className={Style["btns-wrap"]}>
          <Button>
            <Link to="/MetadataDefine">返回</Link>
          </Button>
          <Empower api="/frontMetadataInfoController/batchToDelete">
            <Button
              type="primary"
              disabled={this.state.selectedRowKeys.length < 1}
              onClick={this.handleDelete.bind(this)}
            >
              删除
            </Button>
          </Empower>
        </div>

        <Row gutter={16}>
          <Col span={5}>
            <div style={{ paddingTop: "20px" }}>
              <div className="padding_16" style={{ paddingLeft: "0px" }}>
                数据信息：
              </div>
              <List
                size="small"
                bordered
                dataSource={Object.entries(historyData)}
                renderItem={item => {
                  const { departmentsOptions } = this.props.metadataCommon;
                  if (dictionary[item[0]]) {
                    if (dictionary[item[0]][1]) {
                      return (
                        <List.Item>
                          {dictionary[item[0]][0]}：
                          {dictionary[item[0]][1](item[1], departmentsOptions)}
                        </List.Item>
                      );
                    } else {
                      return (
                        <List.Item>
                          {dictionary[item[0]][0]}：{item[1]}
                        </List.Item>
                      );
                    }
                  } else {
                    return <span />;
                  }
                }}
              />
            </div>
          </Col>
          <Col span={19}>
            <div style={{ paddingTop: "20px" }}>
              <div className="padding_16" style={{ paddingLeft: "0px" }}>
                历史版本：
              </div>
            </div>
            <TableList
              showIndex
              rowKey="id"
              columns={this.columns}
              dataSource={list}
              rowSelection={{
                onChange: this.onChangeAllSelect.bind(this),
                selectedRowKeys: this.state.selectedRowKeys
              }}
              pagination={{ total }}
            />
          </Col>
        </Row>

        {/* 加载查看窗口 */}
        {metaDataDefine.viewStep1Visible ? <ViewTableStep1 /> : null}
        {metaDataDefine.viewStep2Visible ? <ViewTableStep2  metaNameCn={this.state.metaNameCn}/> : null}
      </div>
    );
  }
}

export default connect(
  ({ metaDataHistory, metaDataDefine, metadataCommon, routing }) => ({
    metaDataHistory,
    metaDataDefine,
    metadataCommon,
    routing
  })
)(AppPage);
