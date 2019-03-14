import React from "react";

// 引入工具
import { connect } from "dva";
import { withRouter } from "react-router";
import { setTimer } from "../../method";

// 引入组件
import {
  Input,
  Row,
  Col,
  Button,
  Select,
  Layout,
  Spin,
  Icon,
  Pagination,
  Table,
  Tooltip,
  message,
  Modal,
  Collapse
} from "antd";
import Empower from "../../../../components/Empower";
import {
  runStatus,
  pauseStatus,
  stopStatus,
  errorStatus,
  statusType,
  jobPageSize
} from "../../constant";
import ControlJobPlatform from "./controlPlatform/ControlJobPlatform";
import Line from "../common/Line";
import EchartsView from "./eChartsView/EchartsView";
const { Header, Content, Footer } = Layout;
const InputGroup = Input.Group;
const ButtonGroup = Button.Group;
const Option = Select.Option;
const Panel = Collapse.Panel;

// 引入样式
import Style from "./AppContent.css";

const confirm = Modal.confirm;

let Timer = null;

const JobsCenter = ({ taskcontent, dispatch, location, router }) => {
  const {
    loading,
    taskList,
    btn1,
    btn2,
    total,
    model,
    selectedRows,
    runList,
    isMap,
    taskListMap,
    total_job,
    self,
    taskListMapSelf,
    self_total_job,
    defaultkeyTaskOpenKey,
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
    console.log(type);

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
        type: "jobheader/openFile",
        payload: {
          activeKey: name,
          owner
        }
      });
      dispatch({
        type: "designplatform/changeStatus",
        payload: {
          status: "job"
        }
      });

      window.href = "/gather/designplatform";
    });
  };

  const handlOpenHistory = (e, name, owner) => {
    e.preventDefault();
    e.stopPropagation();
    dispatch({
      type: "taskdetails/queryJobHistory",
      payload: {
        name: name,
        owner
      }
    });
  };
  const selectGroup = owner => value => {
    dispatch({
      type: "taskcontent/queryJobList",
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
        status: "job"
      }
    });
  };

  const handleUpdateTask = () => {
    dispatch({
      type: "taskcontent/queryJobList",
      payload: {
        status: "refresh",
        obj: {
          page: query.page ? query.page : 1,
          pageSize: query.pageSize ? query.pageSize : jobPageSize,
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

  const handlePageChange = e => {
    const { query } = location;
    query.page = e;
    query.pageSize = query.pageSize ? query.pageSize : jobPageSize;
    router.push(location);
  };

  const showList = status => {
    if (runStatus.has(status) || pauseStatus.has(status)) {
      return (
        <Icon
          type="loading"
          spin={true}
          className={Style.statusCommon + " runStatusBg"}
        >
          {statusType.get(status)}
        </Icon>
      );
    } else if (errorStatus.has(status)) {
      return (
        <Icon className={Style.statusCommon + " errorStatusBg"}>
          {statusType.get(status)}
        </Icon>
      );
    } else if (stopStatus.has(status)) {
      return (
        <Icon className={Style.statusCommon + " stopStatusBg"}>
          {statusType.get(status)}
        </Icon>
      );
    } else {
      return (
        <Icon className={Style.statusCommon + " initStatusBg"}>
          {statusType.get(status)}
        </Icon>
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
                handlOpenHistory(e, record.name);
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
      console.log(selectedRows);
      dispatch({
        type: "taskcontent/showLoading",
        payload: {
          selectedRows: selectedRows
        }
      });
    },
    getCheckboxProps: record => ({
      disabled: runList.includes(record.name) // Column configuration not to be checked
    })
  };

  const handleControl = (e, name, owner) => {
    e.preventDefault();
    dispatch({
      type: "controljobplatform/openJobs",
      payload: {
        visible: true,
        transName: name,
        owner
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
                span={16}
                style={{
                  width: "70%",
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
                span={8}
                style={{
                  width: "30%",
                  textAlign: "right",
                  paddingTop: "2px"
                }}
              >
                <Row>
                  <Col span={8}>
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
                  <Col span={8}>
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
                  <Col span={8}>
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
            <EchartsView name={index.name} owner={index.owner} type="job" />
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
        type: "taskcontent/queryJobList",
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
  const handleBatchExcute = owner => {
    return () => {
      if (selectedRows.length === 0) {
        message.info("请先勾选需要执行的调度！");
      } else {
        dispatch({
          type: "runjob/queryBatchList",
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

  const handleUpload = () => {
    dispatch({
      type: "uploadfile/showModal",
      payload: {
        model: "kjb",
        filterType: "kjb",
        visible: true,
        title: "外部KJB文件上传"
      }
    });
  };

  const handleBatchStop = owner => {
    return () => {
      if (selectedRows.length === 0) {
        message.info("请先勾选需要停止的调度！");
      } else {
        confirm({
          title: "确定停止这些调度吗?",
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

  const showModel = () => {
    return (
      <div>
        <Row className={Style.cardRow} gutter={20} type="flex" justify="start">
          {!isMap &&
            (taskList.length > 0 ? (
              taskList.map(index => <ShowItem index={index} />)
            ) : (
              <div>暂无调度任务</div>
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
                        header={`用户：${val[0]} （调度任务：${
                          val[1].total
                        }个）`}
                        key={val[0]}
                      >
                        {self && (
                          <p>
                            分组：
                            <Select
                              style={{ width: "200px" }}
                              defaultValue={group}
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
                          <p style={{padding: "0 16px", textAlign: "right"}}>
                            <span>
                              <Button
                                style={{ height: "35px",marginRight:"16px" }}
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
                          val[1].rows.map(value => {
                            return <ShowItem index={value} />;
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

  return (
    <div id="AppContent">
      <Row>
        <Col span={14}>
          <Row>
            <Col span={14}>
              <InputGroup compact>
                <Input
                  placeholder="请按调度名称搜索"
                  size="large"
                  defaultValue={
                    query.keyword ? decodeURIComponent(query.keyword) : ""
                  }
                  className={Style.Input}
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
                  size="large"
                  className={"changeBtn " + Style[btn1]}
                >
                  &nbsp;
                </Button>
                <Button
                  onClick={() => {
                    handleClick("btn2");
                  }}
                  size="large"
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
                  query.searchType
                    ? decodeURIComponent(query.searchType)
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
              <Empower api="/job/newJob.do">
                <Button
                  size="large"
                  className={Style.rightBtn + " " + Style.addBtn}
                  onClick={handleNewTask}
                >
                  &nbsp;
                </Button>
              </Empower>
            </Col>
            <Col span={2}>
              <Button
                size="large"
                className={Style.rightBtn + " " + Style.refresh}
                onClick={handleUpdateTask}
              >
                &nbsp;
              </Button>
            </Col>
          </InputGroup>
        </Col>
      </Row>
      {/* <Line title="调度任务" /> */}
      <div
        style={{
          padding: "16px 16px 0 16px",
          textAlign: "right",
          fontWeight: "bold"
        }}
      >
        总调度数：{self ? self_total_job : total_job}
      </div>
      <Spin tip="加载中..." spinning={loading}>
        <div className={Style.divContent}>
          {model === "job" && !loading ? showModel() : null}
        </div>
      </Spin>
      <ControlJobPlatform />
    </div>
  );
};

export default withRouter(
  connect(({ taskcontent }) => ({
    taskcontent
  }))(JobsCenter)
);
