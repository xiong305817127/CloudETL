/**
 * Created by Administrator on 2017/3/13.
 */
import { Button, Modal,Form} from 'antd';
import { connect } from 'dva';
import { getTrans_list } from  '../../../../../../services/gather';
import { getJob_list } from  '../../../../../../services/gather1';

let Timer = null;

const Model = ({ model,dispatch})=>{

  const { visible } = model;
  console.log(visible);


  const timeTest = (func, w)=>{
      var interv = ()=>{
        getTrans_list({isOnlyName:true}).then((res)=>{
          const {code} = res.data;
          if(code === "200"){
            console.log(res.data);
            setTimeout(interv, w);
            try{
              func.call(null);
            }
            catch(e){
              throw e.toString();
            }
          }
        });
      };
      setTimeout(interv, w);
  };


  timeTest(()=>{
      console.count("执行次数");
  },3000);

  const setModelHide = ()=>{
    dispatch({
      type:'items/hide',
      visible:false,
    })
  }

  const handleCancel = ()=>{
    dispatch({
      type:'items/hide',
      visible:false,
    })
  }

  return(
    <Modal
      visible={visible}
      title="温馨提醒"
      wrapClassName="vertical-center-modal"
      okText="Create"
      style={{zIndex:50}}
      maskClosable={false}
      footer={[
                  <Button key="submit" type="primary" size="large"  onClick={()=>{setModelHide()}}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={()=>{handleCancel();}}>取消</Button>,
                ]}
    >
      <p>组件正在开发中。。。</p>
    </Modal>
  )
}


const ModelTest = Form.create()(Model);
export default connect()(ModelTest);
