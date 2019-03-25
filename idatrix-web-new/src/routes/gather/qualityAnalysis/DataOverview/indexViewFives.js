import React from 'react'
import { Table, Form, Button,Select } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import ReactEcharts from 'echarts-for-react';
import { withRouter,hashHistory } from 'react-router';
const Option = Select.Option;
class index extends React.Component {
     componentDidMount(){
          const {dispatch}=this.props;
          dispatch({ type: "DataOverviewModel/getDetail"});
          dispatch({ type: "DataOverviewModel/getSchemaInfo"});
          
      }
     render(){
          const {StatisticsUserOptian} =this.props.DataOverviewModel;
          
          return (
               <div className={styles.fivesBang}> 
                     <div className={styles.fivesBangLeft}>
                          <div className={styles.FivesBordTitle}>
                              <p className={styles.bordTitle}>资源使用频率</p>
                              <p className={styles.bordTitleColor}>{StatisticsUserOptian.avgTime?StatisticsUserOptian.avgTime:"0"}</p>
                         </div> 
                    </div> 
                    <div className={styles.fivesBangRight}>
                         <div className={styles.fivesLeft}>
                             <p className={styles.fivesPon}>活跃用户</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.weekActiveUser?StatisticsUserOptian.weekActiveUser:"0"}</h1>
                                  <p>本周内登录次数在3次以上的用户数</p>
                             </div>
                         </div>
                         <div className={styles.fivesLeft}>
                             <p className={styles.fivesPon}>活跃用户</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.weekUsed?StatisticsUserOptian.weekUsed:"0"}</h1>
                                  <p>本周内登录次数在10次以上的用户数</p>
                             </div>
                         </div>
                         <div className={styles.fivesLeft}>
                             <p className={styles.fivesPon}>活跃用户</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.monthActiveUser?StatisticsUserOptian.monthActiveUser:"0"}</h1>
                                  <p>本周内使用平台的用户数</p>
                             </div>
                         </div>
                         <div className={styles.fivesLeft}>
                             <p className={styles.fivesPon}>活跃用户</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.monthUsed?StatisticsUserOptian.monthUsed:"0"}</h1>
                                  <p>本月内使用平台的用户数</p>
                             </div>
                         </div>
     
     
                         <div className={styles.fivesLeftBottom}>
                             <p className={styles.fivesPon}>新增模型</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.dayNewModel?StatisticsUserOptian.dayNewModel:"0"}</h1>
                                  <p>本日模型新建数量</p>
                             </div>
                         </div>
                         <div className={styles.fivesLeftBottom}>
                             <p className={styles.fivesPon}>新增模型</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.monthNewModel?StatisticsUserOptian.monthNewModel:"0"}</h1>
                                  <p>本月模型新建数量</p>
                             </div>
                         </div>
                         <div className={styles.fivesLeftBottom}>
                             <p className={styles.fivesPon}>新增模型</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.dayChainModel?StatisticsUserOptian.dayChainModel:"0"}</h1>
                                  <p>本日模型环比增加</p>
                             </div>
                         </div>
                         <div className={styles.fivesLeftBottom}>
                             <p className={styles.fivesPon}>新增模型</p>
                             <div className={styles.fivesPonStyle}>
                                  <h1 style={{color:"#36bbf2", marginBottom: "16%"}}>{StatisticsUserOptian.monthChainModel?StatisticsUserOptian.monthChainModel:"0"}</h1>
                                  <p>本月模型环比增加</p>
                             </div>
                         </div>
                    </div>
                         
         
               </div>
        )
     }
}



export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
