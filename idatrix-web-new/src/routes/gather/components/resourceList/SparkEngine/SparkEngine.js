/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import SparkEngineList from './SparkEngineList';
import SparkEngineModel from './SparkEngineModel';


const SparkEngine = ({location,resourcecontent})=>{

  const { view } = resourcecontent;


  const showModel = ()=>{

    if(view === "model"){
      return(
        <SparkEngineModel  location={location} />
      )
    }else{
      return(
        <SparkEngineList location={location} />
      )
    }
  };

  return(
    <div id="ResourceContent"  >
      <Line title="Spark引擎" size={"small"} />
      {
        showModel()
      }
    </div>
  )
};

export default connect(({ resourcecontent }) => ({
  resourcecontent
}))(SparkEngine);
