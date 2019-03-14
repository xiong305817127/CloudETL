/**
 * Created by Administrator on 2017/8/21.
 */
import React from 'react'
import { Tabs, Button, message } from 'antd';
import { connect } from "dva"
import Style from './Debug.css'
import { runStatus, delayTime, stopStatus, pauseStatus, spaceTime, errorStatus } from '../../../../constant';
import LogInfo from './LogInfo'
import StepInfo from './StepInfo'
import PreView from './PreView'
import DebugDetail from './DebugDetail'
import PreViewList from "./PreViewList"

const TabPane = Tabs.TabPane;

let Timer = null;
let Timer1 = null;
let Timer2 = null;
const TransDebug = ({ transdebug, infolog, dispatch , transheader}) => {

  const { getTrans_exec_id, getTrans_status, getTransExecInfo } = transheader.methods; 
  const { visible, model, transName, viewId } = transdebug;
  const { DebugPreviewDataList } = infolog;

  const sendTransEvent = ({ executionId, status, name, viewId }) => {

    getTransExecInfo({ executionId: executionId }).then((res) => {
      const { code, data } = res.data;
      if (code === "200") {
        const { ExecLog, StepMeasure, StepStatus } = data;

        dispatch({
          type: "transspace/updateStepStatus",
          payload: {
            stepInfo: StepStatus,
            transName: name,
            actionType: "updateStep"
          }
        });
        if (ExecLog.log && ExecLog.log.trim()) {
          dispatch({
            type: "infolog/printLog",
            infoLog: ExecLog,
            executionId: executionId,
            transName: name,
            viewId: viewId
          });
        }
        dispatch({
          type: "infostep/printStep",
          stepMeasure: StepMeasure,
          executionId: executionId,
          transName: name,
          viewId: viewId
        })
      } else {
        if (Timer2) {
          clearTimeout(Timer2);
        }
      }
    });
    dispatch({
      type: "transspace/updateStepStatus",
      payload: {
        transName: name,
        transStatus: status,
        actionType: "updateStatus"
      }
    });
  };

  const getTransInfo = (name) => {
    getTrans_exec_id({name:name}).then((res) => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (executionId) {
          timeTest(sendTransEvent, spaceTime, { name: name, executionId: executionId, viewId: viewId });
        } else {
          message.error("执行ID获取异常，执行失败！");
        }
      }
    })
  };

  const timeTest = (func, w, obj) => {
    var interv = () => {
      getTrans_status({name: obj.name}).then((res) => {
        const { code, data } = res.data;
        const { status } = data;
        console.log(status,"循环请求");
        console.log(runStatus,"循环请求");
        if (code === "200" && runStatus.has(status)) {
          console.log(status,"循环请求");

          Timer2 = setTimeout(interv, w);
          try {
            func({ ...obj, status });
          }
          catch (e) {
            throw e.toString();
          }
        } else {
          if(Timer2){
            clearTimeout(Timer2);
            Timer2 = null;
          }
          dispatch({
            type: "transspace/updateStepStatus",
            payload: {
              transName: obj.name,
              transStatus: status,
              actionType: "updateStatus"
            }
          });

          getTransExecInfo({
            executionId: obj.executionId
          }).then((res) => {
            const { code, data } = res.data;
            if (code === "200") {
              const { ExecLog, StepMeasure, StepStatus, DebugPreviewData } = data;
              if (ExecLog.log && ExecLog.log.trim()) {
                dispatch({
                  type: "infolog/printLog",
                  infoLog: ExecLog,
                  executionId: obj.executionId,
                  transName: obj.name,
                  viewId: viewId
                });
              }
              dispatch({
                type: "infostep/printStep",
                stepMeasure: StepMeasure,
                executionId: obj.executionId,
                transName: obj.name,
                viewId: viewId
              });

              if (DebugPreviewData && [...Object.keys(DebugPreviewData)].length > 0) {
                dispatch({
                  type: "infolog/preview",
                  DebugPreviewData: DebugPreviewData,
                  executionId:obj.executionId
                });
              }

              if (stopStatus.has(status)) {
                dispatch({
                  type: "transspace/updateStepStatus",
                  payload: {
                    actionType: "updateResult",
                    status: "stopStyle",
                    stepInfo: StepStatus,
                    transName: obj.name
                  }
                });
              } else if (errorStatus.has(status)) {
                dispatch({
                  type: "transspace/updateStepStatus",
                  payload: {
                    actionType: "updateResult",
                    status: "errorStyle",
                    stepInfo: StepStatus,
                    transName: obj.name
                  }
                });
              } else if (pauseStatus.has(status)) {
                return;
              } else {
                dispatch({
                  type: "transspace/updateStepStatus",
                  payload: {
                    actionType: "updateResult",
                    status: "success",
                    stepInfo: StepStatus,
                    transName: obj.name
                  }
                });
              }
            }
          });
        }
      })
    };
    setTimeout(interv, w);
  };


  const clearLog = () => {
    dispatch({
      type: "infolog/clean"
    });
  };


  if (model === "openDebug" && visible === "block") {
    dispatch({ type: "transdebug/save",payload:{ model:"init" }});
    getTransInfo(transName);
  } else if (model === "pauseDebug" && visible === "block") {
    dispatch({ type: "transdebug/save",payload:{ model:"init" }});
    getTransInfo(transName);
  } else if (model === "cleanDebug") {
    if (Timer) {
      clearTimeout(Timer);
    }
    if (Timer1) {
      clearTimeout(Timer1);
    }
    if (Timer2) {
      clearTimeout(Timer2);
    }
    dispatch({ type: "transdebug/save",payload:{ model:"init" }});
    Timer = setTimeout(() => {
      dispatch({ type: "infolog/cleanLog"});
    }, delayTime);
    Timer1 = setTimeout(() => {
      dispatch({
        type: "infostep/cleanStep"
      })
    }, delayTime);
  };

  const callback = (key) => {
    return false;
  };

  const setDetailShow = () => {
    dispatch({
      type: "debugdetail/changeView",
      visible: true
    })
  };




  const operations = () => {
    return (
      <div className={Style.operations}>
        <Button.Group size={"small"}>
          <Button onClick={clearLog}>清除执行日志</Button>
          <Button onClick={setDetailShow}>查看详情</Button>
        </Button.Group>
      </div>
    )
  };


  return (
    <div className={Style.debug} style={{ display: visible }} id="debugTabs"  >
      <Tabs onChange={(key) => { callback(key) }} animated={false} type="card" tabBarExtraContent={operations()}>
        <TabPane tab="日志" key="infoLog">
          <LogInfo styleClass="tabDiv" />
        </TabPane>
        <TabPane tab="步骤度量" key="stepMeasure">
          <StepInfo styleClass="tabDiv" />
        </TabPane>
        {
          DebugPreviewDataList.size > 0 ? (
            <TabPane tab="预览数据" key="predata">
              <PreViewList data={DebugPreviewDataList} />
            </TabPane>
          ) : null
        }
      </Tabs>
      <DebugDetail />
      <PreView />
    </div>
  )
};

export default connect(({ transdebug, infolog , transheader }) => ({
  transdebug, infolog, transheader
}))(TransDebug)
