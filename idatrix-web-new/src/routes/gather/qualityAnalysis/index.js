/**
 * 质量分析首页展示并更新状态
 * @author pwj 2018/09/27
 */
import { connect } from "dva";
import React from 'react';
import { withRouter } from 'react-router';
import { hashHistory } from "dva/router";
import { Input, Row, Col, Button, Modal, Icon, Tooltip, message } from "antd";
import Empower from 'components/Empower';
import TableList from 'components/TableList';
import styles from "./index.less";
import { initStatus, runStatus, pauseStatus, stopStatus, finishStatus, errorStatus } from './constant';
import RunAnalysis from "./components/Modals/RunAnalysis/";
import NewAnalysis from "./components/Modals/NewAnalysis/";
import { routerRedux } from "dva/router";
import RunLog from "./components/Modals/RunLog";

const confirm = Modal.confirm;
const ButtonGroup = Button.Group;
const Search = Input.Search;


const index = ({ dispatch, qualityAnalysis, location, router }) => {

    const { analysisList, loading, total, selectedRows } = qualityAnalysis;

    //勾选项
    const rowSelection = {
        onChange: (selectedRowKeys, selectedRows) => {
            dispatch({
                type: 'qualityAnalysis/save',
                payload: {
                    selectedRows: selectedRows
                }
            });
        }
    };

    //打开分析任务
    const handleOpen = (name) => {
        dispatch({ type: "designSpace/save", payload: { name } });
        dispatch(routerRedux.push("/gather/qualityAnalysis/designSpace"));
    }

    const buttonForReport = ({ target }) => {
        hashHistory.push(target.getAttribute("data-url"))
    }

    const showList = (status) => {
        if (runStatus.has(status) || pauseStatus.has(status)) {
            return (
                <span className={styles.statusCommon + " runStatusBg"} >{runStatus.get(status) || pauseStatus.get(status)}</span>
            )
        } else if (errorStatus.has(status)) {
            return (
                <span className={styles.statusCommon + " errorStatusBg"} >{errorStatus.get(status)}</span>
            )
        } else if (stopStatus.has(status)) {
            return (
                <span className={styles.statusCommon + " stopStatusBg"} >{stopStatus.get(status)}</span>
            )
        } else {
            return (
                <span className={styles.statusCommon + " initStatusBg"} >{initStatus.get(status) || finishStatus.get(status)}</span>
            )
        }
    };

    const columns = [
        { title: '场景名称', dataIndex: 'name', key: 'name' },
        { title: '执行状态', dataIndex: 'status', key: 'status', render: (record) => showList(record) },

        // 此处无法显示正确的数据，暂时隐藏
        // edited by steven leo on 2018/10/16
        // { title: '执行次数', dataIndex: 'servers', key: 'servers' },
        { title: '已运行时间', dataIndex: 'execTime', key: 'execTime'},
        { title: '最后执行时间', dataIndex: 'lastExecTime', key: 'lastExecTime' },
        { title: '最后修改时间', dataIndex: 'modifiedTime', key: 'modifiedTime'},
        {
            title: '操作', dataIndex: '', key: 'x', render: (record) =>
                <div>
                    <Tooltip placement="top" title="分析报告列表">
                        <Icon style={{ fontSize: "16px", cursor: "pointer", marginLeft: "10px" }} data-url={`/gather/qualityAnalysis/report?name=${encodeURIComponent(record.name)}`} onClick={buttonForReport} type="search" />
                    </Tooltip>
                    <Tooltip placement="top" title="查看执行历史">
                        <Icon style={{ fontSize: "16px", cursor: "pointer", marginLeft: "10px" }} onClick={(e) => { handlOpenHistory(e, record.name) }} type="file-text" />
                    </Tooltip>
                    <Tooltip placement="top" title="编辑转换" style={{ marginLeft: "10px" }}>
                        <Icon style={{ fontSize: "16px", cursor: "pointer", marginLeft: "10px" }} onClick={(e) => { handleOpen(record.name) }} type="edit" />
                    </Tooltip>
                </div>
        },
    ];

    const handlOpenHistory = (e,name) => {
        dispatch({
            type: "analysisDetails/queryTransHistory",
            payload: { name }
        });
    }


    //点击搜索查询
    const handleSearch = (value) => {
        const { query } = location;
        if (value) {
            query.search = encodeURIComponent(value);
            query.page = 1;
        } else {
            delete query.search
        }
        router.push(location);
    }

    //批量执行
    const handleBatchExcute = () => {
        if (selectedRows.length === 0) {
            message.info("请先勾选需要执行的分析！");
        } else {
            dispatch({
                type: "runAnalysis/openRunAnalysis",
                payload: {
                    runType: "batch",
                    visible: true
                }
            })
        }
    }

    //批量停止
    const handleBatchStop = () => {
        if (selectedRows.length === 0) {
            message.info("请先勾选需要停止的分析任务！");
        } else {
            confirm({
                title: '确定停止这些分析任务吗?',
                onOk() {
                    dispatch({ type: "qualityAnalysis/queryBatchStop" })
                }
            });
        }
    }

    //新建分析
    const handleNewTask = () => {
			console.log("新建分析");
        dispatch({
            type: "newAnalysis/getNewModel",
        })
    }

    //刷新列表即状态
    const handleUpdateTask = () => {
        router.push(location);
    }

    return (
        <div className={styles.qualityAnalysis}>
            <Row className={styles.rowHeader}>
                <Col span={10}>
                    <Search
                        placeholder="请输入要搜索的场景名称"
                        onSearch={handleSearch}
                        enterButton
                    />
                </Col>
                <Col span={14} style={{ textAlign: "right" }}>
                    <ButtonGroup size={"large"}>
                        <Button style={{ height: "35px" }} onClick={handleBatchExcute}>批量执行</Button>
                        <Button style={{ height: "35px" }} onClick={handleBatchStop}>批量停止</Button>
                        <Empower api="/trans/newTrans.do" >
                            <Button style={{ height: "35px" }} icon="plus" onClick={handleNewTask}>新建</Button>
                        </Empower>
                        <Button style={{ height: "35px" }} icon="sync" onClick={handleUpdateTask} >刷新</Button>
                    </ButtonGroup>
                </Col>
            </Row>
            <TableList
                rowKey="name"
                className={styles.analySisTable}
                columns={columns}
                pagination={{ total }}
                dataSource={analysisList}
                loading={loading}
                rowSelection={rowSelection}
            />
            {/* 执行分析弹框 */}
            <RunAnalysis />
            {/* 新建分析弹框 */}
            <NewAnalysis />
            {/* 执行日志 */}
            <RunLog />
        </div>
    )
}

export default withRouter(connect(({ qualityAnalysis }) => ({
    qualityAnalysis
}))(index));