/**
 * Created by Administrator on 2017/6/30.
 */
import React from 'react';
import { Layout, Row, Col, Dropdown, Menu } from 'antd';
import { connect } from 'dva';
import styles from './index.less'

import Workspace from './components/workspace/'
//import Workheader from './TransPlatform/Workheader'
import Worktools from './components/worktools/'
//import Style from './Workview.css'
import RunAnalysis from "../components/Modals/RunAnalysis/";
import NewAnalysis from "../components/Modals/NewAnalysis/";

const { Content, Sider,Footer } = Layout;

let Timer = null;
const DesignPlatform = ({ dispatch, designSpace }) => {

    const { view, taskList } = designSpace;

    //打开分析任务
    const handleMenuClick = (e) => {
        console.log(e, "点击");
        dispatch({
            type: "designSpace/openAnalysis",
            payload: { name: e.key }
        })
    }

    //获取菜单
    const menu = (
        <Menu className={styles.DropMenu} onClick={handleMenuClick}>
            {
                taskList.map(index => (
                    <Menu.Item key={index} >{index}</Menu.Item>
                ))
            }
        </Menu>
    );

    // const viewHeader = transheader;

    // const { activeKey } = viewHeader;
    // const item = copyTransItme;

    // const { view } = designSpace;

    // const getTransList = () => {
    //     dispatch({
    //         type: "designplatform/queryTransList",
    //         payload: {}
    //     })
    // };

    // const getList = () => {
    //     return getTransList();
    // };

    // const changeStatus = (name) => {
    //     return changeItem(name);
    // };

    // const changeItem = (name) => {
    //     if (Timer) {
    //         clearTimeout(Timer);
    //     }
    //     Timer = setTimeout(() => {
    //         dispatch({
    //             type: "designplatform/changeStatus",
    //             payload: {
    //                 status: name
    //             }
    //         });
    //         if (name === "job") {
    //             dispatch({
    //                 type: "transheader/changeModel",
    //                 payload: {
    //                     shouldUpdate: true
    //                 }
    //             });
    //             dispatch({
    //                 type: "transdebug/cleanDebug"
    //             });
    //         } else {
    //             dispatch({
    //                 type: "jobheader/changeModel",
    //                 payload: {
    //                     shouldUpdate: true
    //                 }
    //             });
    //             dispatch({
    //                 type: "jobdebug/cleanDebug"
    //             });
    //         }
    //     }, 100);
    // };

    // const handleClick = (name) => {
    //     dispatch({
    //         type: "transheader/openFile",
    //         payload: {
    //             activeKey: name
    //         }
    //     });
    //     dispatch({
    //         type: "designplatform/changeStatus",
    //         payload: {
    //             status: "trans"
    //         }
    //     });
    // };

    // const handleNewTask = () => {
    //     dispatch({
    //         type: 'newtrans/getNewModel',
    //         payload: {
    //             status: status
    //         }
    //     })
    // };

    // const handlePasteClick = () => {
    //     let obj = {};
    //     let newItem = { ...item };

    //     console.log(newItem);

    //     if (status === "trans") {
    //         obj = {
    //             "fromStepName": item.text,
    //             "fromTransName": item.viewName,
    //             "toStepName": "",
    //             "toTransName": activeKey
    //         };
    //         dispatch({
    //             type: "transspace/copyItem",
    //             payload: {
    //                 item: newItem,
    //                 obj: obj
    //             }
    //         })
    //     } else {
    //         obj = {
    //             "fromEntryName": item.text,
    //             "fromJobName": item.viewName,
    //             "toEntryName": "",
    //             "toJobName": activeKey
    //         };
    //         dispatch({
    //             type: "jobspace/copyItem",
    //             payload: {
    //                 item: newItem,
    //                 obj: obj
    //             }
    //         })
    //     }
    // };

    //新建分析
    const handleNewTask = () => {
        dispatch({  type:"newAnalysis/getNewModel" })
    }

    return (
        <Layout className={styles.qualitySpace}>
            <Sider className={styles.silder} width={215}>
                <Worktools />
            </Sider>
            <Content className={styles.Content}>
                <div className={styles.fucBtn} style={{ top: view ? "70px" : "30px",zIndex:1 }} type="flex" >
                    <div onClick={handleNewTask}>
                        <span>新建分析</span>
                    </div>
                    <Dropdown overlay={menu} className={styles.Dropdown} >
                        <div>
                            <span> 全部任务</span>
                        </div>
                    </Dropdown>
                </div>
                {
                    view ? (
                        <Workspace />
                    ) : (
                            <div className={styles.welcome}>
                                <div >欢迎使用数据质量分析系统</div>
                                <p>请新建或打开分析任务</p>
                            </div>
                        )
                }
            </Content>
            {/* 执行分析弹框 */}
            <RunAnalysis />
            {/* 新建分析弹框 */}
            <NewAnalysis />
        </Layout>
    )
}

export default connect(({ designSpace }) => ({
    designSpace
}))(DesignPlatform);


/**
 *  {
                    view?(
                    <Content className={Style.mainCont}>
                        <Workheader viewStatus={viewStatus} />
                        <Workspace viewStatus={viewStatus} />
                    </Content>):(
                    <Content className={Style.welcome}>
                        <Welcome viewStatus={viewStatus} />
                    </Content>)
                }
 */