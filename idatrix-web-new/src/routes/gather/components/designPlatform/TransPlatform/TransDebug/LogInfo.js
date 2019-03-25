/**
 * Created by Administrator on 2017/4/26.
 */
import React from 'react';
import { connect } from 'dva';

class LogInfo extends React.Component{


   render(){

     const { log_list } = this.props.infolog;


      return(
        <div className={this.props.styleClass} style={{fontSize:"12px",paddingBottom:"20px"}} >
          {
            log_list.map((index)=>{
                  return(
                      <pre style={{ whiteSpace:"pre-wrap",marginBottom:0}} key={index.key}>{index.log}</pre>
                  )
            })
          }
        </div>
      )
   }
}


export default connect(({ infolog }) => ({
  infolog
}))(LogInfo)
