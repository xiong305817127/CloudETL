import React, { Component } from "react";
import { connect } from "dva";
import { withRouter } from "react-router";
import { API_BASE_GATHER } from "constants";
import {
  Input,
  Row,
  Col,
  Button,
  Select,
  Layout,
  Spin,
  Modal,
  Icon,
  Pagination,
  Table,
  Tooltip,
  message,
  Collapse,
  Tabs
} from "antd";
import Empower from "../../../../components/Empower";
import {
  initStatus,
  runStatus,
  pauseStatus,
  stopStatus,
  finishStatus,
  errorStatus,
  statusType,
  statusType1,
  transPageSize
} from "../../constant";
import ControlTransPlatform from "./controlPlatform/ControlTransPlatform";
const confirm = Modal.confirm;
const { Header, Content, Footer } = Layout;
const InputGroup = Input.Group;
const Option = Select.Option;
const ButtonGroup = Button.Group;
const Panel = Collapse.Panel;
const TabPane = Tabs.TabPane;

import EchartsView from "./eChartsView/EchartsView";
import Style from "./AppContent.css";
import Line from "../common/Line";
import { setTimer } from "../../method";

let Timer = null;

const TransCenter = props => {
  const { taskcontent, dispatch, router, location } = props;
  const {
    loading,
    taskList,
    btn1,
    btn2,
    model,
    selectedRows,
    transHistory,
    isMap,
    taskListMap,
    taskListMapSelf,
    total_trans,
    self_total_trans,
    defaultkeyTaskOpenKey,
    self,
    groupList,
    group
  } = taskcontent;

  const { query } = location;

  if (Timer) {
    clearTimeout(Timer);
    Timer = null;
  }

  if (loading) {
    Timer = setTimeout(() => {
      if (loading) {
        dispatch({
          type: "taskcontent/showLoading",
          loading: false
        });
      }
    }, 5000);
  }

  const handleClick = type => {
    if (type === "btn1") {
      const { query } = location;
      query.page = query.page ? query.page : 1;
      query.pageSize = 8;
      router.push(location);
    } else {
      const { query } = location;
      query.page = query.page ? query.page : 1;
      query.pageSize = 8;
      router.push(location);
    }

    dispatch({
      type: "taskcontent/changeBtn",
      click: type
    });
  };

  const handleOpen = (name, owner) => {
    setTimer("", 300, () => {
      dispatch({
        type: "transheader/openFile",
        payload: {
          activeKey: name,
          owner
        }
      });
      dispatch({
        type: "designplatform/changeStatus",
        payload: {
          status: "trans"
        }
      });
      window.href = "/gather/designplatform";
    });
  };

  const handlOpenHistory = (e, name, owner) => {
    e.preventDefault();
    dispatch({
      type: "taskdetails/queryTransHistory",
      payload: {
        name,
        owner
      }
    });
  };

  const selectGroup = owner => value => {
    dispatch({
      type: "taskcontent/queryTransList",
      payload: {
        status: "init",
        obj: {
          pageSize: 8,
          page: 1,
          owner: owner,
          isMap: true,
          self: self,
          group: value
        }
      }
    });
  };

  const handleNewTask = () => {
    dispatch({
      type: "newtrans/getNewModel",
      payload: {
        status: "trans"
      }
    });
  };

  const handleUpdateTask = () => {
    dispatch({
      type: "taskcontent/queryTransList",
      payload: {
        status: "refresh",
        obj: {
          page: query.page ? query.page : 1,
          pageSize: query.pageSize ? query.pageSize : transPageSize,
          search: query.keyword ? decodeURIComponent(query.keyword) : "",
          searchType: query.searchType ? query.searchType : ""
        }
      }
    });
  };

  const handleChange = e => {
    setTimer(e.target.value, 1500, value => {
      if (value) {
        query.keyword = encodeURIComponent(value);
        query.page = 1;
      } else {
        delete query.keyword;
      }

      router.push(location);
    });
  };

  const onSelectChange = e => {
    setTimer(e, 800, value => {
      if (value) {
        query.searchType = encodeURIComponent(value);
        query.page = 1;
      } else {
        delete query.searchType;
      }
      router.push(location);
    });
  };

  const showList = status => {
    if (runStatus.has(status) || pauseStatus.has(status)) {
      return (
        <span
          type="loading"
          spin={true}
          className={Style.statusCommon + " runStatusBg"}
        >
          {statusType.get(status)}
        </span>
      );
    } else if (errorStatus.has(status)) {
      return (
        <span className={Style.statusCommon + " errorStatusBg"}>
          {statusType.get(status)}
        </span>
      );
    } else if (stopStatus.has(status)) {
      return (
        <span className={Style.statusCommon + " stopStatusBg"}>
          {statusType.get(status)}
        </span>
      );
    } else {
      return (
        <span className={Style.statusCommon + " initStatusBg"}>
          {statusType.get(status)}
        </span>
      );
    }
  };

  const getTextColor = (status, name) => {
    if (runStatus.has(status) || pauseStatus.has(status)) {
      return (
        <div>
          <div>
            {" "}
            <span className={Style.circle + " runStatusBg"}>&nbsp;</span>
            {name}
          </div>
        </div>
      );
    } else if (errorStatus.has(status)) {
      return (
        <div>
          <div>
            {" "}
            <span className={Style.circle + " errorStatusBg"}>&nbsp;</span>
            {name}
          </div>
        </div>
      );
    } else if (stopStatus.has(status)) {
      return (
        <div>
          <div>
            <span className={Style.circle + " stopStatusBg"}>&nbsp;</span>
            {name}
          </div>
        </div>
      );
    } else {
      return (
        <div>
          <div>
            <span className={Style.circle + " initStatusBg"}>&nbsp;</span>
            {name}
          </div>
        </div>
      );
    }
  };

  const columns = [
    { title: "转换名称", dataIndex: "name", key: "name" },
    {
      title: "执行状态",
      dataIndex: "status",
      key: "status",
      render: record => showList(record)
    },
    { title: "已运行时间", dataIndex: "execTime", key: "execTime" },
    { title: "最后执行时间", dataIndex: "lastExecTime", key: "lastExecTime" },
    { title: "最后修改时间", dataIndex: "modifiedTime", key: "modifiedTime" },
    {
      title: "操作",
      dataIndex: "",
      key: "x",
      render: record => (
        <div>
          <Tooltip placement="top" title="查看执行历史">
            <Icon
              style={{ fontSize: "16px", cursor: "pointer" }}
              onClick={e => {
                handlOpenHistory(e, record.name, record.owner);
              }}
              type="file-text"
            />
          </Tooltip>
          <Tooltip
            placement="top"
            title="编辑转换"
            style={{ marginLeft: "10px" }}
          >
            <Icon
              style={{
                fontSize: "16px",
                cursor: "pointer",
                marginLeft: "10px"
              }}
              onClick={e => {
                handleOpen(record.name, record.owner);
              }}
              type="edit"
            />
          </Tooltip>
        </div>
      )
    }
  ];

  const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      dispatch({
        type: "taskcontent/showLoading",
        payload: {
          selectedRows: selectedRows
        }
      });
    }
  };

  const handleBatchExcute = owner => {
    return () => {
      if (selectedRows.length === 0) {
        message.info("请先勾选需要执行的转换！");
      } else {
        dispatch({
          type: "runtrans/queryBatchList",
          payload: {
            selectedRows: selectedRows,
            runModel: "batch",
            visible: true,
            owner
          }
        });
      }
    };
  };

  const handleBatchStop = owner => {
    return () => {
      if (selectedRows.length === 0) {
        message.info("请先勾选需要停止的转换！");
      } else {
        confirm({
          title: "确定停止这些转换吗?",
          onOk() {
            dispatch({
              type: "taskcontent/queryBatchStop",
              payload: {
                selectedRows: selectedRows,
                owner
              }
            });
          }
        });
      }
    };
  };

  const handleUpload = () => {
    dispatch({
      type: "uploadfile/showModal",
      payload: {
        model: "ktr",
        filterType: "ktr",
        visible: true,
        title: "外部KTR文件上传"
      }
    });
  };

  const handleUpload1 = name => {
    dispatch({
      type: "uploadfile/showModal",
      payload: {
        model: `mp4::转换_${name}`,
        visible: true,
        title: "转换视频文件上传",
        value: true,
        disabled: true
      }
    });
  };

  const handleControl = (e, name, owner) => {
    e.preventDefault();
    dispatch({
      type: "controltransplatform/openTrans",
      payload: {
        visible: true,
        transName: name,
        owner: owner
      }
    });
  };
  const ShowItem = ({ index }) => {
    return (
      <Col span={8} lg={8} xl={6} key={index.name}>
        <Layout className={Style.cardSpan + " grayBorder"}>
          <Header className={Style.cardHeader}>
            <Row>
              <Col
                span={14}
                style={{
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis"
                }}
                onClick={() => {
                  handleOpen(index.name, index.owner);
                }}
              >
                {getTextColor(index.status, index.name)}
              </Col>
              <Col
                span={10}
                style={{
                  textAlign: "right",
                  paddingTop: "2px"
                }}
              >
                <Row>
                  <Col span={4}>
                    <Tooltip placement="top" title="上传视频">
                      <Icon
                        style={{ fontSize: "16px", cursor: "pointer" }}
                        onClick={e => {
                          handleUpload1(index.name);
                        }}
                        type="upload"
                      />
                    </Tooltip>
                  </Col>
                  <Col span={5}>
                    <Tooltip placement="top" title="下载视频">
                      <a  href={`${API_BASE_GATHER}/cloud/downloadFile.do?type=mp4&path=转换_${index.name}`} >
                        <Icon
                          style={{ fontSize: "16px", cursor: "pointer",color:"rgba(0,0,0,0.65)" }}
                          type="download"
                        />
                      </a>
                    </Tooltip>
                  </Col>
                  <Col span={5}>
                    <Tooltip placement="top" title="监控">
                      <Icon
                        style={{
                          fontSize: "16px",
                          cursor: "pointer"
                        }}
                        onClick={e => {
                          handleControl(e, index.name, index.owner);
                        }}
                        type="eye"
                      />
                    </Tooltip>
                  </Col>
                  <Col span={5}>
                    <Tooltip placement="top" title="查看执行历史">
                      <Icon
                        style={{
                          fontSize: "16px",
                          cursor: "pointer"
                        }}
                        onClick={e => {
                          handlOpenHistory(e, index.name, index.owner);
                        }}
                        type="file-text"
                      />
                    </Tooltip>
                  </Col>
                  <Col span={5}>
                    <Tooltip
                      placement="top"
                      title="编辑转换"
                      style={{ marginLeft: "10px" }}
                    >
                      <Icon
                        style={{
                          fontSize: "16px",
                          cursor: "pointer",
                          marginLeft: "10px"
                        }}
                        onClick={e => {
                          handleOpen(index.name, index.owner);
                        }}
                        type="edit"
                      />
                    </Tooltip>
                  </Col>
                </Row>
              </Col>
            </Row>
          </Header>

          <Content className={Style.cardContent}>
            <EchartsView name={index.name} owner={index.owner} type="trans" />
          </Content>
          <Footer className={Style.cardFooter}>
            <Row>
              <Col span={7}>
                服务器数{" "}
                <span className={Style.span + " blueColor"}>
                  {index.servers}
                </span>
              </Col>
              <Col span={7}>
                集群数{" "}
                <span className={Style.span + " blueColor"}>
                  {index.clusters}
                </span>
              </Col>
              <Col span={10} className={Style.ClickTime}>
                已运行{index.execTime}
              </Col>
            </Row>
          </Footer>
        </Layout>
      </Col>
    );
  };
  const changeOwnerPage = owner => {
    return (page, pageSize) => {
      dispatch({
        type: "taskcontent/queryTransList",
        payload: {
          status: "init",
          obj: {
            pageSize: pageSize,
            page: page,
            owner: owner,
            isMap: true,
            self: self
          }
        }
      });
    };
  };

  const showModel = () => {
    return (
      <div>
        <Row className={Style.cardRow} gutter={20} type="flex" justify="start">
          {!isMap &&
            (taskList.length > 0 ? (
              taskList.map((index, key) => <ShowItem key={key} index={index} />)
            ) : (
              <div>暂无转换任务</div>
            ))}

          {isMap && (
            <div style={{ padding: "0 16px", width: "100%" }}>
              <Collapse
                defaultActiveKey={[defaultkeyTaskOpenKey]}
                style={{ width: "100%" }}
              >
                {Object.entries(self ? taskListMapSelf : taskListMap).map(
                  (val, index) => {
                    return (
                      <Panel
                        disabled={val[1].rows.length == 0}
                        header={`用户：${val[0]} （转换任务：${
                          val[1].total
                        }个）`}
                        key={val[0]}
                      >
                        {self && (
                          <p>
                            分组：
                            <Select
                              value={group}
                              style={{ width: "200px" }}
                              onChange={selectGroup(val[0])}
                            >
                              <Option value="all">全部</Option>

                              {groupList.map(val => {
                                return (
                                  <Option key={val} value={val}>
                                    {val}
                                  </Option>
                                );
                              })}
                            </Select>
                          </p>
                        )}
                        {btn1 === "btn1Click" ? null : (
                          <p style={{ padding: "0 16px", textAlign: "right" }}>
                            <span>
                              <Button
                                style={{ height: "35px", marginRight: "16px" }}
                                onClick={handleBatchExcute(val[0])}
                              >
                                批量执行
                              </Button>
                              <Button
                                style={{ height: "35px" }}
                                onClick={handleBatchStop(val[0])}
                              >
                                批量停止
                              </Button>
                            </span>
                          </p>
                        )}
                        {btn1 !== "btn1Click" && (
                          <Table
                            columns={columns}
                            pagination={false}
                            expandedRowRender={record =>
                              record.description ? (
                                <p style={{ margin: 0 }}>
                                  转换描述：{record.description}
                                </p>
                              ) : (
                                <p style={{ margin: 0 }}>暂无描述</p>
                              )
                            }
                            dataSource={val[1].rows}
                            rowSelection={rowSelection}
                          />
                        )}

                        {btn1 === "btn1Click" &&
                          val[1].rows.map((value, key) => {
                            return <ShowItem index={value} key={key} />;
                          })}
                        <div style={{ clear: "both" }} />
                        <div
                          style={{
                            textAlign: "right",
                            background: "#f0f0f0",
                            padding: "8px"
                          }}
                        >
                          <Pagination
                            size="small"
                            onChange={changeOwnerPage(val[0])}
                            total={val[1].total}
                            pageSize={8}
                            current={val[1].page}
                            showTotal={(total, range) =>
                              `当前显示${range[0]}-${
                                range[1]
                              } / ${total} 条数据`
                            }
                          />
                        </div>
                      </Panel>
                    );
                  }
                )}
              </Collapse>
            </div>
          )}
        </Row>
      </div>
    );
  };

  let initObj = query;

  /*if(Object.keys(query).length === 0 && btn2 === "btn2Click"){
      initObj = Object.assign(query,transHistory);
    }
  
    console.log(initObj,"初始数据");*/

  return (
    <div id="AppContent">
      <Row>
        <Col span={14}>
          <Row>
            <Col span={14}>
              <InputGroup compact>
                <Input
                  placeholder="请按转换名称搜索"
                  className={Style.Input}
                  defaultValue={
                    initObj.keyword ? decodeURIComponent(initObj.keyword) : ""
                  }
                  onChange={handleChange}
                />
                <Button size="large" className={Style.searchBtn}>
                  &nbsp;
                </Button>
              </InputGroup>
            </Col>
            <Col span={6} xl={4}>
              <InputGroup compact>
                <Button
                  onClick={() => {
                    handleClick("btn1");
                  }}
                  className={"changeBtn " + Style[btn1]}
                >
                  &nbsp;
                </Button>
                <Button
                  onClick={() => {
                    handleClick("btn2");
                  }}
                  className={"changeBtn " + Style[btn2]}
                >
                  &nbsp;
                </Button>
              </InputGroup>
            </Col>
            <Col span={4} id="AppContentSelect">
              <Select
                className={Style.statusSelect}
                value={
                  initObj.searchType
                    ? decodeURIComponent(initObj.searchType)
                    : "全部状态"
                }
                allowClear
                onChange={onSelectChange}
              >
                <Option key={"全部状态"} value={"全部状态"}>
                  全部状态
                </Option>
                <Option key={"等待执行"} value={"等待执行"}>
                  等待执行
                </Option>
                <Option key={"执行中"} value={"执行中"}>
                  执行中
                </Option>
                <Option key={"告警状态"} value={"告警状态"}>
                  告警状态
                </Option>
                <Option key={"执行故障"} value={"执行故障"}>
                  执行故障
                </Option>
              </Select>
            </Col>
          </Row>
        </Col>
        <Col span={10} style={{ textAlign: "right" }}>
          <ButtonGroup>
            <Button
              style={{ height: "35px" }}
              icon="upload"
              onClick={handleUpload}
            >
              上传
            </Button>
            <Empower api="/trans/newTrans.do">
              <Button
                style={{ height: "35px" }}
                icon="plus"
                onClick={handleNewTask}
              >
                新建
              </Button>
            </Empower>
            <Button
              style={{ height: "35px" }}
              icon="sync"
              onClick={handleUpdateTask}
            >
              刷新
            </Button>
          </ButtonGroup>

          <InputGroup style={{ textAlign: "right", display: "none" }} compact>
            <Col span={20}>&nbsp;</Col>
            <Col span={2}>
              <Empower api="/trans/newTrans.do">
                <Button
                  className={Style.rightBtn + " " + Style.addBtn}
                  onClick={handleNewTask}
                >
                  &nbsp;
                </Button>
              </Empower>
            </Col>
            <Col span={2}>
              <Button
                className={Style.rightBtn + " " + Style.refresh}
                onClick={handleUpdateTask}
              >
                &nbsp;
              </Button>
            </Col>
          </InputGroup>
        </Col>
      </Row>
      {/* <Line title="转换任务" /> */}
      <div
        style={{
          padding: "16px 16px 0 16px",
          textAlign: "right",
          fontWeight: "bold"
        }}
      >
        总转换数：{self ? self_total_trans : total_trans}
      </div>
      <Spin tip="加载中..." spinning={loading}>
        <div className={Style.divContent}>
          {model === "trans" && !loading ? showModel() : null}
        </div>
      </Spin>
      <ControlTransPlatform />
    </div>
  );
};

export default withRouter(
  connect(({ taskcontent }) => ({
    taskcontent
  }))(TransCenter)
);
