/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import DataSystemList from './DataSystemList';
import DataSystemModel from './DataSystemModel';


const DataSystem = ({location,resourcecontent,cloudetlCommon})=>{

    const { view } = resourcecontent;
    const { isMetacube } = cloudetlCommon;
    
    const showModel = ()=>{

        if(view === "model"){
             return(
               <DataSystemModel  location={location}  canEdit={!isMetacube} />
             )
        }else{
           return(
              <DataSystemList location={location} canEdit={!isMetacube}/>
           )
        }
    };

    return(
      <div id="ResourceContent"  >
          <Line title="数据系统" size={"small"} />
          {
              showModel()
          }
      </div>
    )
};

export default connect(({ resourcecontent,cloudetlCommon }) => ({
  resourcecontent,cloudetlCommon
}))(DataSystem);
