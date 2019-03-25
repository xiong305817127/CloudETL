/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import ServerList from './ServerList';
import ServerModel from './ServerModel';


const Server = ({location,resourcecontent,cloudetlCommon})=>{

  const { view } = resourcecontent;
  const { isMetacube } = cloudetlCommon;

  const showModel = ()=>{

    if(view === "model"){
      return(
        <ServerModel  location={location} canEdit={!isMetacube}/>
      )
    }else{
      return(
        <ServerList location={location} canEdit={!isMetacube} />
      )
    }
  };

  return(
    <div id="ResourceContent"  >
      <Line title="服务器" size={"small"} />
      {
        showModel()
      }
    </div>
  )
};

export default connect(({ resourcecontent,cloudetlCommon }) => ({
  resourcecontent,cloudetlCommon
}))(Server);
