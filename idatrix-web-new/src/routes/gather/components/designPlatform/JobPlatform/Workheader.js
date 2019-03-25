/**
 * Created by Administrator on 2017/8/29.
 */
import React from "react"
import { Tabs, Button,Modal } from 'antd';
import { connect } from "dva";
import Style from './Workheader.css';
import { runStatus,delayTime } from '../../../constant';
import Empower from '../../../../../components/Empower';
import { getOpen_job } from '../../../../../services/gather1'

let Timer = null;
let Timer1 = null;
let Timer2 = null;
const TabPane = Tabs.TabPane;

const Workheader = ({ jobheader, dispatch, viewStatus }) => {
  const {
    activeArgs,
    activeKey,
    modelVisible,
    shouldUpdate,
    newFile,
    removeKey,
    hasTask
  } = jobheader;

  const { getJob_status } = jobheader.methods;
  //暂时缺少 初始化workspace状态
  const openFile = key => {
    if (Timer) {
      clearTimeout(Timer);
    }
    //直接清空日志
    dispatch({
      type: "jobdebug/cleanDebug"
    });
    Timer = setTimeout(() => {
      const backUrl = window.location.hash.replace(/[?&]_k=.+$/, "");
      if (viewStatus === "job" && backUrl === "#/gather/designplatform") {
        getOpen_job({ ...activeArgs.get(key), name: key }).then(res => {
          const { code, data } = res.data;
          if (code === "200") {
            const { info, entryList, hopList } = data;
            dispatch({
              type: "jobspace/openFile",
              name: key,
              info: info,
              itemsList: entryList,
              linesList: hopList,
              viewId: activeArgs.get(key).viewId
            });
            getJob_status({name:key}).then(res => {
              const { code, data } = res.data;
              if (code === "200") {
								const { status } = data;
								dispatch({
									type: "jobspace/updateData",
									payload:{
										btnStatus:status
									}
								});
                if (runStatus.has(status)) {
                  dispatch({
                    type: "jobdebug/openDebug",
                    payload: {
                      viewId: activeArgs.get(key).viewId,
                      visible: "block",
                      transName: key
                    }
                  });
                } else {
                  dispatch({
                    type: "jobdebug/changeTabs",
                    viewId: activeArgs.get(key).viewId,
                    transName: key
                  });
                }
              }
            });
          }
        });
        dispatch({
          type: "jobheader/changeModel",
          payload: {
            shouldUpdate: false
          }
        });
      }
    }, delayTime);
  };

  //点击tabs切换状态
  const onChange = key => {
    console.log(Timer2);

    if (Timer2) {
      clearTimeout(Timer2);
      Timer2 = null;
    }

    //直接清空日志
    dispatch({
      type: "jobdebug/cleanDebug"
    });

    dispatch({
      type: "jobheader/changeModel",
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
    }
    Timer1 = setTimeout(() => {
      dispatch({
        type: "jobspace/openFile",
        name: activeKey,
        viewId: activeArgs.get(activeKey),
        btnStatus: "Waiting"
      });
      dispatch({
        type: "jobdebug/changeTabs",
        viewId: activeArgs.get(activeKey),
        transName: activeKey
      });
      dispatch({
        type: "jobheader/changeModel",
        payload: {
          newFile: false
        }
      });
    }, delayTime);
  };

  //是否需要更新
  if (shouldUpdate) {
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
        type: "jobheader/changeModel",
        payload: {
          model: "welcome"
        }
      });
    }, delayTime);
  }

  //显示删除弹框
  const onEdit = (targetKey, action) => {
    if(action === "remove"){
      dispatch({
        type: "jobheader/changeModel",
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
			title: '删除调度',
			content: `确定删除${removeKey}？`,
			zIndex:1020,
			okText: '确认',
			cancelText: '取消',
			onOk:()=>{
				dispatch({
					type:'jobheader/deleteTrans',
					payload:{
						removeKey:removeKey
					}
				});
				//直接清空日志
				dispatch({
					type:"jobdebug/cleanDebug"
				});
			}
		});
  };

  //关闭
  const handleModelOk = () => {
    dispatch({
      type: "jobheader/closeModel",
      payload: {
        removeKey: removeKey
      }
    });
    dispatch({
      type: "jobdebug/cleanDebug"
    });
  };

  //取消
  const handleCancel = () => {
    dispatch({
      type: "jobheader/changeModel",
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
          <Empower key="back1" api="/job/deleteJob.do">
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
            ? `确定要 关闭${removeKey}`
            : `请选择 关闭 或 删除 ${removeKey}`}
        </p>
      </Modal>
    </div>
  );
};

export default connect(({ jobheader }) => ({
  jobheader
}))(Workheader);
