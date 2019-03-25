/**
 * Created by Administrator on 2017/3/13.
 */
import Modal from 'components/Modal';
import { connect } from 'dva'

const DeleteTip = ({dispatch,deletemodel})=>{

    const { visible,id,text,tipText } = deletemodel;

  const setModal1Hide = ()=>{
    dispatch({
      type:'deletemodel/hide',
      visible:false
    })
  };


  const deleteItem = ()=>{
      dispatch({
        type:"flowspace/deleteItem",
        id:id
      });
      setModal1Hide();
    };

    return(
      <Modal
        title="提醒框"
        wrapClassName="vertical-center-modal"
        visible={visible}
        onOk={() => deleteItem(id,text)}
        onCancel={() => setModal1Hide(false)}
      >
        <p>{tipText} ?</p>
      </Modal>
    )
}

export default connect(({ deletemodel }) => ({
  deletemodel
}))(DeleteTip)
