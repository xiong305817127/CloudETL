import React from 'react'
import { Table, Form, Button,Select,DatePicker,Tabs } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import ReactEcharts from 'echarts-for-react';
const Option = Select.Option;
const TabPane = Tabs.TabPane;

import moment from 'moment';

const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY/MM/DD';


class index extends React.Component {
     constructor(props) {
          super(props);
          this.state = {
               schemaInfo:"SchemaClickDetails"  //默认数据模型的组件
          };
      }

     columns = [{
          dataIndex: 'name',
          key: 'name',
          title:'模型',
          width:"50%"
        }, {
          title: '点击页面',
          dataIndex: 'clickCount',
          key: 'clickCount',
        }]

        componentDidMount(){
          const {dispatch}=this.props;
          dispatch({ type: "DataOverviewModel/getSchemaclick",payload:{date:"2019/03/01"}});
        }

        //数据模型点击量
        SchemaClickDetails=()=>{
           this.setState({
               schemaInfo:"SchemaClickDetails" 
           })
        }
        //报表分析点击量
        statementClickDetails=()=>{
          this.setState({
               schemaInfo:"statementClickDetails" 
           })
        }
        //仪表盘点击量
        dashBoardClickDetails=()=>{
          this.setState({
               schemaInfo:"dashBoardClickDetails" 
           })
       }
      //日期查询
      onChange=(date, dateString)=>{
          const {dispatch}=this.props;
          dispatch({ type: "DataOverviewModel/getSchemaclick",payload:{date:dateString}});
      }

     render(){
          const {SchemaclickInfo,SchemaclickData} =this.props.DataOverviewModel;
          const {schemaInfo } =this.state;
          let list =SchemaclickInfo.schemaClickDetails?SchemaclickInfo.schemaClickDetails:{};
          let listStatement =SchemaclickInfo.statementClickDetails?SchemaclickInfo.statementClickDetails:{};  
          let listDashBoard =SchemaclickInfo.dashBoardClickDetails?SchemaclickInfo.dashBoardClickDetails:{};
         
          console.log(list," &&", listStatement==={} ,"&&", listDashBoard,"schemaInfo====");
          return (
               <div> 
                  
                    <div className={styles.leftTwo}>
                       <div className={styles.leftTitleSccess}>
                          <MonthPicker  format={dateFormat} defaultValue={moment('2019/03/01', dateFormat)} onChange={this.onChange.bind(this)}/>
                       </div>
                         <div className={styles.bordColorThere}>
                             <a>
                                <p className={styles.bordTitle1}>BI总使用量</p>
                                <p className={styles.bordTitleColor1}>{SchemaclickInfo.biSumClickCount?SchemaclickInfo.biSumClickCount:"0"}</p>
                             </a>
                         </div>
                         <div className={styles.bordColorCol} onClick={this.SchemaClickDetails.bind(this)}>
                             <a>
                                <p className={styles.bordTitle1}>数据模型点击量</p>
                                <p className={styles.bordTitleColor1}>{SchemaclickInfo.schemaSumClickCount?SchemaclickInfo.schemaSumClickCount:"0"}</p>
                             </a>
                         </div>
                         <div className={styles.bordColorCol1} onClick={this.statementClickDetails.bind(this)}>
                              <a>
                                 <p className={styles.bordTitle} style={{fontSize:"16"}}>报表分析点击量</p>
                                 <p className={styles.bordTitleColor}>{SchemaclickInfo.statementSumClickCount?SchemaclickInfo.statementSumClickCount:"0"}</p>
                              </a>
                         </div>
                         <div className={styles.bordColorCol2} onClick={this.dashBoardClickDetails.bind(this)}>
                              <a>
                                 <p className={styles.bordTitle}>仪表盘点击量</p>
                                 <p className={styles.bordTitleColor}>{SchemaclickInfo.dashBoardSumClickCount?SchemaclickInfo.dashBoardSumClickCount:"0"}</p>
                              </a>
                         </div>
                         
                    </div>
             

                    
                    {schemaInfo === "SchemaClickDetails" ?(
                          <div className={styles.leftTwoLeft}>
                              <h3>数据模型</h3>
                              {
                                   JSON.stringify(list) == "{}"?(
                                        <h2>暂无数据</h2>  
                                   ):(
                                        <Tabs tabPosition="left">
                                        {Object.keys(list).map((key, index)=>{
                                             return(
                                             <TabPane tab={key} key={index}><Table rowKey="key" bordered scroll={{x:200,y:500}} className={styles.bordered} columns={this.columns} dataSource={list[key]} pagination={false}/> </TabPane>
                                             )  })
                                                  }
                                        </Tabs>
                                   )
                              }
                              
                         </div>
                    ):null}
 
                    {schemaInfo === "statementClickDetails" ?(
                         
                          <div className={styles.leftTwoLeft}>
                            <h3>报表分析</h3>
                            {
                                JSON.stringify(listStatement) == "{}"?(
                                   <h2>暂无数据 </h2>  
                                ):(
                                   <Tabs tabPosition="left">
                                        {Object.keys(listStatement).map((key, index)=>{return (
                                             <TabPane tab={key} key={index}><Table rowKey="key" bordered className={styles.bordered} scroll={{x:200,y:500}} columns={this.columns} dataSource={listStatement[key]} pagination={false}/></TabPane>
                                        ) })  }
                                   </Tabs>
                                ) 
                            }
                            
                         </div>
                    ):null}

                    {schemaInfo === "dashBoardClickDetails" ?(
                          <div className={styles.leftTwoLeft}>
                            <h3>仪表盘</h3>
                            {
                                 JSON.stringify(listDashBoard) == "{}"?(
                                   <h2>暂无数据 </h2>  
                                 ):(
                                   <Tabs tabPosition="left">
                                        {Object.keys(listDashBoard).map((key, index)=>{ return ( 
                                             <TabPane tab={key} key={index}> <Table rowKey="key" bordered className={styles.bordered} scroll={{x:200,y:500}} columns={this.columns} dataSource={listDashBoard[key]} pagination={false}/> </TabPane>
                                   )  })  }
                                   </Tabs>
                                 )
                            }
                           
                         </div>
                    ):null}
                    
             
                    
               </div>
        )
     }
}


export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
