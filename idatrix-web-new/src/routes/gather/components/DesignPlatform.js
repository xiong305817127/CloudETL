/**
 * Created by Administrator on 2017/6/30.
 */
import React from "react";
import { Layout, Button } from "antd";
import { connect } from "dva";
import JobPlatform from "./designPlatform/JobPlatform";
import TransPlatform from "./designPlatform/TransPlatform";
import Style from "./DesignPlatform.css";
import ChangePlatform from "./designPlatform/workview/ChangePlatform";
import NewTask from "./designPlatform/workview/NewTask";
import DropList from "./designPlatform/workview/DropList";
import DomShow from "./designPlatform/DomShow";
import Empower from "../../../components/Empower";

let Timer = null;

const DesignPlatform = ({
  dispatch,
  designplatform,
  transheader,
  jobheader
}) => {
  const {
    status,
    transList,
    jobList,
    copyTransItme,
    copyJobItme
  } = designplatform;
  const viewHeader = status === "trans" ? transheader : jobheader;

  const { model, activeKey } = viewHeader;
  const item = status === "trans" ? copyTransItme : copyJobItme;

  const showModel = () => {
    if (status === "trans") {
      return (
        <TransPlatform
          viewStatus={status}
          model={model}
          owner={transheader.owner}
        />
      );
    } else{
      return (
        <JobPlatform
          viewStatus={status}
          model={model}
          owner={jobheader.owner}
        />
      );
    }
  };

  const getTransList = () => {
    dispatch({
      type: "designplatform/queryTransList",
      payload: {
        isMap: true
      }
    });
  };
  const getJobList = () => {
    dispatch({
      type: "designplatform/queryJobList",
      payload: {
        isMap: true
      }
    });
  };

  const getList = () => {
    if (status === "trans") {
      return getTransList();
    } else {
      return getJobList();
    }
  };

  const changeStatus = name => {
    return changeItem(name);
  };

  const changeItem = name => {
    if (Timer) {
      clearTimeout(Timer);
    }
    Timer = setTimeout(() => {
      dispatch({
        type: "designplatform/changeStatus",
        payload: {
          status: name
        }
      });
      if (name === "job") {
        dispatch({
          type: "transheader/changeModel",
          payload: {
            shouldUpdate: true
          }
        });
        dispatch({
          type: "transdebug/cleanDebug"
        });
      } else {
        dispatch({
          type: "jobheader/changeModel",
          payload: {
            shouldUpdate: true
          }
        });
        dispatch({
          type: "jobdebug/cleanDebug"
        });
      }
    }, 100);
  };

  const handleClick = str => {
    const data = JSON.parse(str);
    if (status === "trans") {
      dispatch({
        type: "designplatform/changeStatus",
        payload: {
          status: "trans"
        }
      });
      dispatch({
        type: "transheader/openFile",
        payload: {
          activeKey: data.name,
          owner: data.owner
        }
      });
    } else {
      dispatch({
        type: "designplatform/changeStatus",
        payload: {
          status: "job"
        }
      });
      
      dispatch({
        type: "jobheader/openFile",
        payload: {
          activeKey: data.name,
          owner: data.owner
        }
      });
    }
  };

  const handleNewTask = () => {
    dispatch({
      type: "newtrans/getNewModel",
      payload: {
        status: status
      }
    });
  };

  const handlePasteClick = () => {
    let obj = {};
    let newItem = { ...item };

    if (status === "trans") {
      obj = {
        fromStepName: item.text,
        fromTransName: item.viewName,
        toStepName: "",
        fromOwner: item.fromOwner,
        toTransName: activeKey
      };
      dispatch({
        type: "transspace/copyItem",
        payload: {
          item: newItem,
          obj: obj
        }
      });
    } else {
      obj = {
        fromEntryName: item.text,
        fromJobName: item.viewName,
        toEntryName: "",
        fromOwner: item.fromOwner,
        toJobName: activeKey
      };
      dispatch({
        type: "jobspace/copyItem",
        payload: {
          item: newItem,
          obj: obj
        }
      });
    }
  };

  return (
    <Layout id="DesignPlaform">
      {showModel()}
      <Empower
        api={status === "trans" ? "/trans/newTrans.do" : "/job/newJob.do"}
        disable-type="hide"
      >
        <div id="space_new">
          <NewTask
            text={status === "trans" ? "转换" : "调度"}
            handleNewTask={handleNewTask}
          />
        </div>
      </Empower>
      <div id="space_task">
        {status === "trans" && (
          <DropList
            getList={getList}
            taskList={status === "trans" ? transList : jobList}
            handleClick={handleClick}
          />
        )}

        {status !== "trans" && (
          <DropList
            getList={getList}
            taskList={status === "trans" ? transList : jobList}
            handleClick={handleClick}
          />
        )}
      </div>
      <div id="welcome_change">
        <ChangePlatform status={status} changeStatus={changeStatus} />
      </div>
      {model === "view" ? (
        <div id="paste_btn">
          <Button disabled={item ? false : true} onClick={handlePasteClick}>
            粘贴
          </Button>
        </div>
      ) : null}
      <DomShow
        owner={status == "trans" ? transheader.owner : jobheader.owner}
      />
    </Layout>
  );
};

export default connect(({ designplatform, transheader, jobheader }) => ({
  designplatform,
  transheader,
  jobheader
}))(DesignPlatform);
