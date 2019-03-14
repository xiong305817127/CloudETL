import React from "react"
import { Tabs, Button } from 'antd';
import Modal from "components/Modal";
import { connect } from "dva";
import Empower from 'components/Empower';
import styles from "./index.less";
import SpaceContent from "./components/SpaceContent/";
import WorkFooter from "../workfooter";

const TabPane = Tabs.TabPane;

const index = ({ designSpace, dispatch }) => {

    const { activeArgs,name,removeKey,modelVisible } = designSpace;

    //切换Tabs
    const onChange = (e) => {
        if (!e) return;
        dispatch({ type: "designSpace/openAnalysis", payload: { name: e } })
    }

    //点击关闭按钮
    const onEdit = (e) => {
        if (!e) return;
        dispatch({
            type: 'designSpace/save',
            payload: {
                modelVisible: true,
                removeKey: e
            }
        })
    }

    //删除
    const handleDelete = () => {
			Modal.confirm({
				title: '删除任务',
				content: `确定删除${removeKey}？`,
				zIndex:1020,
				okText: '确认',
				cancelText: '取消',
				onOk:()=>{
					dispatch({  type: 'designSpace/deleteTabs' });
				}	
			});
    };

    //关闭
    const handleModelOk = () => {
        dispatch({  type: 'designSpace/closeTabs' })
    };

    //取消
    const handleCancel = () => {
        dispatch({ 
            type: 'designSpace/save',
            payload: { modelVisible: false, removeKey: "" }
        })
    };

    //是否可以删除
    const canDelete = removeKey.indexOf("SUB_") !== -1;

    return (
        <div className={styles.workspace}>
            <div className={styles.spaceheader}>
                <Tabs
                    hideAdd
                    onChange={onChange}
                    activeKey={name}
                    type="editable-card"
                    onEdit={onEdit}
                >
                    {activeArgs.map(n => <TabPane tab={n} key={n}></TabPane>)}
                </Tabs>
            </div>
            <div className={styles.spacecontent}>
                <SpaceContent />
            </div>
            <div className={styles.spacefooter}>
                <WorkFooter />
            </div>
            <Modal title="提示信息" visible={modelVisible}
                wrapClassName="vertical-center-modal"
                height={250}
                onCancel={handleCancel}
                footer={[
                    <Empower key="back1" api="/trans/deleteTrans.do" >
                        <Button key="back" disabled={canDelete} style={{ float: "left" }} size="large" onClick={handleDelete}>删除</Button>
                    </Empower>,
                    <Button key="submit" type="primary" size="large" onClick={handleModelOk}>关闭</Button>,
                    <Button key="close" size="large" onClick={handleCancel}>取消</Button>
                ]}
            >
                <p style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{canDelete ? `确定要关闭${removeKey}` : `请选择 关闭 或 删除 ${removeKey}`} </p>
            </Modal>
        </div>
    )
}

export default connect(({
    designSpace
}) => ({ designSpace }))(index);