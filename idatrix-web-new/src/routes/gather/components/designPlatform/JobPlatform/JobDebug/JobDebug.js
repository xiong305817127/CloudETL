/**
 * Created by Administrator on 2017/8/21.
 */
import React from "react";
import { Tabs, Button, message } from "antd";
import { connect } from "dva";
import Style from "./Debug.css";
import {
  runStatus,
  delayTime,
  stopStatus,
  pauseStatus,
  spaceTime,
  errorStatus
} from "../../../../constant";
const TabPane = Tabs.TabPane;
import LogInfo from "./LogInfo";
import StepInfo from "./StepInfo";
import DebugDetail from "./DebugDetail";

let Timer = null;
let Timer1 = null;
let Timer2 = null;
const JobDebug = ({ jobheader, jobdebug, dispatch }) => {
  const { visible, model, transName, viewId } = jobdebug;
  const { methods } = jobheader;
  const { getJob_exec_id, getJob_status, getJobExecInfo } = methods;
  const sendTransEvent = ({ executionId, status, name, viewId }) => {
    getJobExecInfo({
      executionId: executionId
    }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { EntryMeasure, EntryStatus, ExecLog } = data;
        dispatch({
          type: "jobspace/updateStepStatus",
          payload: {
            stepInfo: EntryStatus,
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
          stepMeasure: EntryMeasure[0] ? EntryMeasure[0].childEntryMeasure : [],
          executionId: executionId,
          transName: name,
          viewId: viewId
        });
      } else {
        if (Timer2) {
          clearTimeout(Timer2);
        }
      }
    });

    dispatch({
      type: "jobspace/updateStepStatus",
      payload: {
        transName: name,
        transStatus: status,
        actionType: "updateStatus"
      }
    });
  };

  const getTransInfo = name => {
    getJob_exec_id({name}).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (executionId) {
          timeTest(sendTransEvent, spaceTime, {
            name: name,
            executionId: executionId,
            viewId: viewId
          });
        }
      }
    });
  };

  const timeTest = (func, w, obj) => {
    var interv = () => {
      getJob_status({name:obj.name}).then(res => {
        const { code, data } = res.data;
        const { status } = data;

        if (code === "200" && runStatus.has(status)) {
          Timer2 = setTimeout(interv, w);
          try {
            func({ ...obj, status });
          } catch (e) {
            throw e.toString();
          }
        } else {
          dispatch({
            type: "jobspace/updateStepStatus",
            payload: {
              transName: obj.name,
              transStatus: status,
              actionType: "updateStatus"
            }
          });
          getJobExecInfo({
            executionId: obj.executionId
          }).then(res => {
            const { code, data } = res.data;
            if (code === "200") {
              const { EntryMeasure, EntryStatus, ExecLog } = data;
              if (ExecLog.log && ExecLog.log.trim()) {
                dispatch({
                  type: "infolog/printLog",
                  infoLog: ExecLog,
                  executionId: obj.executionId,
                  transName: name,
                  viewId: viewId
                });
              }
              dispatch({
                type: "infostep/printStep",
                stepMeasure: EntryMeasure[0]
                  ? EntryMeasure[0].childEntryMeasure
                  : [],
                executionId: obj.executionId,
                transName: name,
                viewId: viewId
              });
              if (stopStatus.has(status)) {
                dispatch({
                  type: "jobspace/updateStepStatus",
                  payload: {
                    actionType: "updateResult",
                    status: "stopStyle",
                    stepInfo: EntryStatus,
                    transName: obj.name
                  }
                });
              } else if (errorStatus.has(status)) {
                dispatch({
                  type: "jobspace/updateStepStatus",
                  payload: {
                    actionType: "updateResult",
                    status: "errorStyle",
                    stepInfo: EntryStatus,
                    transName: obj.name
                  }
                });
              } else if (pauseStatus.has(status)) {
                return;
              } else {
                dispatch({
                  type: "jobspace/updateStepStatus",
                  payload: {
                    actionType: "updateResult",
                    status: "success",
                    stepInfo: EntryStatus,
                    transName: obj.name
                  }
                });
              }
            }
          });
        }
      });
    };
    setTimeout(interv, w);
  };

  const clearLog = () => {
    dispatch({
      type: "infolog/clean"
    });
  };
  const cleanStep = () => {
    dispatch({
      type: "infostep/cleanStep"
    });
  };

  if (model === "openDebug" && visible === "block") {
    dispatch({ type: "jobdebug/save", payload: { model: "init" } });
    cleanStep();
    clearLog();
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

    dispatch({ type: "jobdebug/save", payload: { model: "init" } });
    Timer = setTimeout(() => {
      dispatch({
        type: "infolog/cleanLog"
      });
    }, delayTime);
    Timer1 = setTimeout(() => {
      dispatch({
        type: "infostep/cleanStep"
      });
    }, delayTime);
  }

  const callback = key => {
    return false;
  };

  const setDetailShow = () => {
    dispatch({
      type: "debugdetail/changeView",
      visible: true
    });
  };

  const operations = () => {
    return (
      <div className={Style.operations}>
        <Button.Group size={"small"}>
          <Button onClick={clearLog}>清除执行日志</Button>
          <Button onClick={setDetailShow}>查看详情</Button>
        </Button.Group>
      </div>
    );
  };

  return (
    <div className={Style.debug} style={{ display: visible }} id="debugTabs">
      <Tabs
        onChange={key => {
          callback(key);
        }}
        animated={false}
        type="card"
        tabBarExtraContent={operations()}
      >
        <TabPane tab="日志" key="infoLog">
          <LogInfo styleClass="tabDiv" />
        </TabPane>
        <TabPane tab="步骤度量" key="stepMeasure">
          <StepInfo styleClass="tabDiv" />
        </TabPane>
      </Tabs>
      <DebugDetail />
    </div>
  );
};

export default connect(({ jobdebug, jobheader }) => ({
  jobdebug,
  jobheader
}))(JobDebug);
