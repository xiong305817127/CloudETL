/**
 * Created by Administrator on 2017/8/21.
 */
import React from 'react'
import { Tabs, Button, Table } from 'antd';
import { connect } from "dva"
import styles from './index.less'
import Modal from "components/Modal"
//import DebugDetail from './DebugDetail'

const TabPane = Tabs.TabPane;

const TransDebug = ({ analysisInfo, dispatch }) => {

    const { showInfo, logList, StepMeasure,visible } = analysisInfo;

    //清除日志
    const clearLog = () => {
        dispatch({
            type: "analysisInfo/save",
            payload: { logList: [] }
        })
    }

    //关闭详情
    const handleCancel = ()=>{
        dispatch({
            type: "analysisInfo/save",
            payload: { visible: false }
        })
    }

    //展现详情
    const showDetailLog = ()=>{
        dispatch({
            type: "analysisInfo/save",
            payload: { visible: true }
        })
    }

    const operations = () => {
        return (
            <div className={styles.operations}>
                <Button.Group size={"small"}>
                    <Button onClick={clearLog}>清除执行日志</Button>
                    <Button onClick={showDetailLog}>查看详情</Button>
                </Button.Group>
            </div>
        )
    };

    const columns = [
        {
            title: '#',
            dataIndex: 'key',
        },
        {
            title: '步骤名称',
            dataIndex: 'stepName',
        },
        {
            title: '复制的记录行数',
            dataIndex: 'copy',
        },
        {
            title: '读',
            dataIndex: 'linesRead',
        },
        {
            title: '写',
            dataIndex: 'linesWritten',
        },
        {
            title: '输入',
            dataIndex: 'linesInput',
        },
        {
            title: '输出',
            dataIndex: 'linesOutput',
        },
        {
            title: '更新',
            dataIndex: 'linesUpdated',
        },
        {
            title: '拒绝',
            dataIndex: 'linesRejected',
        },
        {
            title: '错误',
            dataIndex: 'errors',
        },
        {
            title: '激活',
            dataIndex: 'statusDescription',
        }, {
            title: '时间',
            dataIndex: 'seconds',
        }, {
            title: '速度(条记录/秒)',
            dataIndex: 'speed',
        }, {
            title: 'Pri/in/out',
            dataIndex: 'priority',
        }
    ];


    return (
        <div className={styles.debug} style={{ display: showInfo ? "block" : "none" }} >
            <Tabs animated={false} type="card" tabBarExtraContent={operations()}>
                <TabPane tab="日志" key="infoLog" >
                    <div className={styles.infoLog} style={{ fontSize: "12px" }} >
                        {
                            logList.map((index) => {
                                return (
                                    <pre style={{ whiteSpace: "pre-wrap", marginBottom: 0 }} key={index.key}>{index.log}</pre>
                                )
                            })
                        }
                    </div>
                </TabPane>
                <TabPane tab="步骤度量" key="stepMeasure">
                    <div className={styles.stepMeasure}>
                        <Table columns={columns} size={"small"} dataSource={StepMeasure} bordered pagination={false} />
                    </div>
                </TabPane>
            </Tabs>
            <Modal
                visible={visible}
                title="执行信息"
                wrapClassName="vertical-center-modal"
                onCancel={handleCancel}
                maskClosable={false}
                width={1000}
                footer={null}
            >
                <Tabs  animated={false} type="card"  className={styles.logModal}>
                    <TabPane tab="日志" key="infoLog" >
                        <div className={styles.modalInfoLog} style={{ fontSize: "12px" }} >
                            {
                                logList.map((index) => {
                                    return (
                                        <pre style={{ whiteSpace: "pre-wrap", marginBottom: 0 }} key={index.key}>{index.log}</pre>
                                    )
                                })
                            }
                        </div>
                    </TabPane>
                    <TabPane tab="步骤度量" key="stepMeasure">
                        <div className={styles.modalStepMeasure}>
                            <Table columns={columns} size={"small"} dataSource={StepMeasure} bordered pagination={false} />
                        </div>
                    </TabPane>
                </Tabs>
            </Modal>
            {/* <DebugDetail /> */}
        </div>
    )
};

export default connect(({ analysisInfo }) => ({
    analysisInfo
}))(TransDebug);