/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import HadoopClusterList from './HadoopClusterList';
import HadoopClusterModel from './HadoopClusterModel';


const HadoopCluster = ({location,resourcecontent,cloudetlCommon})=>{

  const { view } = resourcecontent;
  const { isMetacube } = cloudetlCommon;


  const showModel = ()=>{

    if(view === "model"){
      return(
        <HadoopClusterModel  location={location} canEdit={!isMetacube}/>
      )
    }else{
      return(
        <HadoopClusterList location={location} canEdit={!isMetacube}/>
      )
    }
  };

  return(
    <div id="ResourceContent"  >
      <Line title="Hadoop集群" size={"small"} />
      {
        showModel()
      }
    </div>
  )
};

export default connect(({ resourcecontent,cloudetlCommon }) => ({
  resourcecontent,cloudetlCommon
}))(HadoopCluster);
