/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import ClusterList from './ClusterList';
import ClusterModel from './ClusterModel';


const Cluster = ({location,resourcecontent})=>{

  const { view } = resourcecontent;


  const showModel = ()=>{

    if(view === "model"){
      return(
        <ClusterModel  location={location} />
      )
    }else{
      return(
        <ClusterList location={location} />
      )
    }
  };

  return(
    <div id="ResourceContent"  >
      <Line title="服务器集群" size={"small"} />
      {
        showModel()
      }
    </div>
  )
};

export default connect(({ resourcecontent }) => ({
  resourcecontent
}))(Cluster);
