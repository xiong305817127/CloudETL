/**
 * Created by Administrator on 2017/8/29.
 */
import React from "react";
import { Tabs, Button, Modal, message } from "antd";
import { connect } from "dva";
const TabPane = Tabs.TabPane;
import Style from "./Workheader.css";
import { runStatus, delayTime, pauseStatus } from "../../../constant";
import Empower from "../../../../../components/Empower";
import { getOpen_trans } from "../../../../../services/gather";

let Timer = null;
let Timer1 = null;
let Timer2 = null;

const Workheader = ({ transheader, dispatch, viewStatus }) => {
  const {
    activeArgs,
    activeKey,
    modelVisible,
    shouldUpdate,
    newFile,
    removeKey,
    hasTask
  } = transheader;
  const { getTrans_status } = transheader.methods;
  //暂时缺少 初始化workspace状态
  const openFile = key => {
    if (Timer) {
      clearTimeout(Timer);
    }
    //直接清空日志
    dispatch({
      type: "transdebug/cleanDebug"
    });

    Timer = setTimeout(() => {
      const backUrl = window.location.hash.replace(/[?&]_k=.+$/, "");
      if (viewStatus === "trans" && backUrl === "#/gather/designplatform") {
        getOpen_trans({ ...activeArgs.get(key), name: key }).then(res => {
          const { code, data } = res.data;
          if (code === "200") {
            const { info, stepList, hopList } = data;
            dispatch({
              type: "transspace/openFile",
              name: key,
              info: info,
              itemsList: stepList,
              linesList: hopList,
              viewId: activeArgs.get(key).viewId
            });
            getTrans_status({name:key}).then(res => {
              const { code, data } = res.data;
              if (code === "200") {
								const { status } = data;
								dispatch({
									type: "transspace/updateData",
									payload:{
										btnStatus:status
									}
								});
               
                if (runStatus.has(status)) {
                  dispatch({
                    type: "transdebug/openDebug",
                    payload: {
                      viewId: activeArgs.get(key).viewId,
                      visible: "block",
                      transName: key
                    }
                  });
                } else if (pauseStatus.has(status)) {
                  dispatch({
                    type: "transspace/updateStepStatus",
                    payload: {
                      transName: key,
                      actionType: "updateStatus",
                      transStatus: status
                    }
                  });
                } else {
                  dispatch({
                    type: "transdebug/changeTabs",
                    viewId: activeArgs.get(key).viewId,
                    transName: key
                  });
                }
              }
            });
          }
        });

        // [待处理]不知道这是什么鬼？
        // dispatch({
        //   type: "transheader/changeModel",
        //   payload: {
        //     shouldUpdate: false
        //   }
        // });
      }
    }, delayTime);
  };

  //点击tabs切换状态
  const onChange = key => {
    if (key === "") {
      return;
    }
    if (Timer2) {
      clearTimeout(Timer2);
      Timer2 = null;
      return;
    }

    console.log(Timer2);
    //直接清空日志
    dispatch({
      type: "transdebug/cleanDebug"
    });

    dispatch({
      type: "transheader/changeModel",
      payload: {
        activeKey: key
      }
    });
    openFile(key);
  };

  //初始化新建任务
  const initNewFile = () => {
    if (Timer1) {
      clearTimeout(Timer1);
      Timer1 = null;
    }
    Timer1 = setTimeout(() => {
      dispatch({
        type: "transspace/openFile",
        name: activeKey,
        viewId: activeArgs.get(activeKey),
        btnStatus: "Waiting"
      });
      dispatch({
        type: "transdebug/changeTabs",
        viewId: activeArgs.get(activeKey),
        transName: activeKey
      });
      dispatch({
        type: "transheader/changeModel",
        payload: {
          newFile: false
        }
      });
    }, delayTime);
  };

  //是否需要更新
  if (shouldUpdate && activeKey != "") {
    openFile(activeKey);
  }

  //新建后初始化
  if (newFile) {
    initNewFile();
  }
  //无任务时，切换
  if (!hasTask) {
    setTimeout(() => {
      dispatch({
        type: "transheader/changeModel",
        payload: {
          model: "welcome"
        }
      });
    }, delayTime);
  }

  //显示删除弹框
  const onEdit = (targetKey, action) => {
    if (action === "remove") {
      dispatch({
        type: "transheader/changeModel",
        payload: {
          modelVisible: true,
          removeKey: targetKey
        }
      });
    }
  };

  //删除
  const handleDelete = ()=>{
		Modal.confirm({
			title: '删除转换',
			content: `确定删除${removeKey}？`,
			zIndex:1020,
			okText: '确认',
			cancelText: '取消',
			onOk:()=>{
				dispatch({
					type:'transheader/deleteTrans',
					payload:{
						removeKey:removeKey
					}
				});
				//直接清空日志
				dispatch({
					type:"transdebug/cleanDebug"
				});
			}	
		})
  };

  //关闭
  const handleModelOk = () => {
    dispatch({
      type: "transheader/closeModel",
      payload: {
        removeKey: removeKey,
        shouldUpdate: true
      }
    });
    //直接清空日志
    dispatch({
      type: "transdebug/cleanDebug"
    });
  };

  //取消
  const handleCancel = () => {
    dispatch({
      type: "transheader/changeModel",
      payload: {
        modelVisible: false
      }
    });
  };

  const canDelete = removeKey.indexOf("SUB_") !== -1;

  return (
    <div id="Workheader" className={Style.Workheader}>
      <Tabs
        hideAdd
        onChange={onChange}
        activeKey={activeKey}
        type="editable-card"
        onEdit={onEdit}
      >
        {[...activeArgs.keys()].map(name => (
          <TabPane
            tab={name + "(" + activeArgs.get(name).owner + ")"}
            key={name}
          />
        ))}
      </Tabs>
      <Modal
        title="提示信息"
        visible={modelVisible}
        wrapClassName="vertical-center-modal"
        height={250}
        onCancel={handleCancel}
        footer={[
          <Empower key="back1" api="/trans/deleteTrans.do">
            <Button
              key="back"
              disabled={canDelete}
              style={{ float: "left" }}
              size="large"
              onClick={handleDelete}
            >
              删除
            </Button>
          </Empower>,
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={handleModelOk}
          >
            关闭
          </Button>,
          <Button key="close" size="large" onClick={handleCancel}>
            取消
          </Button>
        ]}
      >
        <p
          style={{
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap"
          }}
        >
          {canDelete
            ? `确定要关闭${removeKey}`
            : `请选择 关闭 或 删除 ${removeKey}`}{" "}
        </p>
      </Modal>
    </div>
  );
};

export default connect(({ transheader }) => ({
  transheader
}))(Workheader);
