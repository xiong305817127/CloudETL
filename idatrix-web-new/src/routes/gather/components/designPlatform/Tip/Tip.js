/**
 * Created by Administrator on 2017/3/13.
 */
import Modal from "components/Modal.js";
import { connect } from 'dva'

const Tip = ({dispatch,tip})=>{

    const { status,visible,id,text,tipText,transname } = tip;

  const setModal1Hide = ()=>{
    dispatch({
      type:'tip/hide',
      visible:false
    })
  };


  const deleteItem = ()=>{
      console.log(status);
        if(status === "trans"){
          dispatch({
             type:"transspace/deleteStep",
              payload:{
                  obj:{
                    "transName":transname,
                    "stepName": text
                  },
                id:id
              }
          })
        }else{
          dispatch({
            type:"jobspace/deleteStep",
            payload:{
              obj:{
                "jobName":transname,
                "entryName": text
              },
              id:id
            }
          })
        }
        setModal1Hide();
    };

    return(
      <Modal
        title="删除节点"
        wrapClassName="vertical-center-modal"
        visible={visible}
        onOk={() => deleteItem(id,text)}
        onCancel={() => setModal1Hide(false)}
      >
        <p>{tipText}</p>
      </Modal>
    )
}

export default connect(({ tip }) => ({
  tip
}))(Tip)
