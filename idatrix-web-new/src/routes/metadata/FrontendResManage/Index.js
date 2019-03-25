/**
 * Created by Administrator on 2017/8/22.
 */
import { connect } from 'dva';
import  RadioView  from '../common/RadioView';
import FrontEnd from './components/FrontEnd';
import Platform from './components/Platform';

const FrontendResManage = ({location, frontendfesmanage})=>{

  const { model } = frontendfesmanage;

  const option = {
    type:"DSRegister",
    title:"数据系统注册",
    defaultValue:"1",
    radioArgs:[
      {value:"1",name:"前置机"},
      {value:"2",name:"平台"}
    ],
    location:location
  };

  const showView = ()=>{
    if(model === '1'){
      return (
        <FrontEnd location={location} frontendfesmanage={frontendfesmanage} />
      )
    }else{
      return (
        <Platform location={location} frontendfesmanage={frontendfesmanage} />
      )
    }
  };

  return(
    <div>
      <RadioView  option={option} />
      <div>
        {
          showView()
        }
      </div>
    </div>
  )
};

export default connect(({ frontendfesmanage }) => ({
  frontendfesmanage
}))(FrontendResManage);
