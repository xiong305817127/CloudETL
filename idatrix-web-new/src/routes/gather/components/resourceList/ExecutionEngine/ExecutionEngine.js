/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import ExecutionEngineList from './ExecutionEngineList';
import ExecutionEngineModel from './ExecutionEngineModel';


const ExecutionEngine = ({location,resourcecontent})=>{

  const { view } = resourcecontent;


  const showModel = ()=>{

    if(view === "model"){
      return(
        <ExecutionEngineModel  location={location} />
      )
    }else{
      return(
        <ExecutionEngineList location={location} />
      )
    }
  };

  return(
    <div id="ResourceContent"  >
      <Line title="执行引擎" size={"small"} />
      {
        showModel()
      }
    </div>
  )
};

export default connect(({ resourcecontent }) => ({
  resourcecontent
}))(ExecutionEngine);
