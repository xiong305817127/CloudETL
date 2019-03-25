import React from 'react'
import { Table, Form, Button,Select,Icon,Tooltip,DatePicker } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import Modal from 'components/Modal';
const Option = Select.Option;
import moment from 'moment';

const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY-MM';

class index extends React.Component {
   constructor(props) {
       super(props);
       this.state = {
           optionBor:[]
       };
   }
    //注册量详情表
    columnslist = [{
      dataIndex: 'resourceName',
      key: 'resourceName',
      title: '资源目录名称',
      width:'25%'
    },{
      title: '编码',
      dataIndex: 'resourceCode',
      key: 'resourceCode',
      width:'25%'
    },{
      title: '所属部门',
      dataIndex: 'deptCode',
      key: 'deptCode',
      width:'25%'
    },{
      title: '注册量',
      dataIndex: 'count',
      key: 'count',
      width:'25%'
    }];

  //资源使用频率
    columnslist1 = [{
         dataIndex: 'resourceName',
         key: 'resourceName',
         title: '资源目录名称',
         width:'25%'
      },{
         title: '编码',
         dataIndex: 'resourceCode',
         key: 'resourceCode',
         width:'25%'
      },{
         title: '所属部门',
         dataIndex: 'deptCode',
         key: 'deptCode',
         width:'25%'
      },{
         title: '查看量',
         dataIndex: 'count',
         key: 'count',
         width:'25%'
   }];
    //发布量
    columnslist2 = [{
      dataIndex: 'resourceName',
      key: 'resourceName',
      title: '资源目录名称',
      width:'25%'
    },{
      title: '编码',
      dataIndex: 'resourceCode',
      key: 'resourceCode',
      width:'25%'
    },{
      title: '所属部门',
      dataIndex: 'deptCode',
      key: 'deptCode',
      width:'25%'
    },{
      title: '发布量',
      dataIndex: 'count',
      key: 'count',
      width:'25%'
    }];
    columnslist3 = [{
      dataIndex: 'resourceName',
      key: 'resourceName',
      title: '资源目录名称',
      width:'25%'
    },{
      title: '编码',
      dataIndex: 'resourceCode',
      key: 'resourceCode',
      width:'25%'
    },{
      title: '所属部门',
      dataIndex: 'deptCode',
      key: 'deptCode',
      width:'25%'
    },{
      title: '订阅量',
      dataIndex: 'count',
      key: 'count',
      width:'25%'
    }];

   //接口调用次数
   columnsInfo = [{
      dataIndex: 'serviceName',
      key: 'serviceName',
      title: '接口名称',
      width:'50%'
    }, {
      title: '调用次数',
      dataIndex: 'count',
      key: 'count',
      width:'50%'
    }];
    //数据调用量
    columnsInfoCount = [{
      dataIndex: 'serviceName',
      key: 'serviceName',
      title: '接口名称',
      width:'50%'
    }, {
      title: '数据量',
      dataIndex: 'count',
      key: 'count',
      width:'50%'
    }];

    //上报任务数量
    columnsExit = [{
      title: '所属部门',
      dataIndex: 'deptName',
      key: 'deptName',
      width:'50%'
    },,{
      title: '上报次数',
      dataIndex: 'count',
      key: 'count',
      width:'50%'
    }];

    //上报数据量
    columnsExitCount = [{
      dataIndex: 'deptName',
      key: 'deptName',
      title: '作业名称',
      width:'50%'
    }, {
      title: '数据量',
      dataIndex: 'count',
      key: 'count',
      width:'50%'
    }];

     //交换作业量
     columnsExchange = [{
         title: '所属部门',
         dataIndex: 'deptName',
         key: 'deptName',
         width:'50%'
      },, {
         title: '交换次数',
         dataIndex: 'count',
         key: 'count',
         width:'50%'
      }];

     //交换数据量
     columnsExchangeCount = [{
         dataIndex: 'deptName',
         key: 'deptName',
         title: '接口名称',
         width:'50%'
      }, {
         title: '数据量',
         dataIndex: 'count',
         key: 'count',
         width:'50%'
      }];
    

    componentDidMount(){
      const {dispatch}=this.props;
      dispatch({ type: "DataOverviewModel/getCount"});
      dispatch({ type: "DataOverviewModel/getCountByNumberOfCalls"});
      dispatch({ type: "DataOverviewModel/getServerByTheAmountOfData"});
      dispatch({ type: "DataOverviewModel/getReptByNumberOfTasks"});
      dispatch({ type: "DataOverviewModel/getCountByTheAmountOfData"});
      dispatch({ type: "DataOverviewModel/getCountByNumberOfTasks"});
      dispatch({ type: "DataOverviewModel/getNumberOfTasks"});
      // dispatch({ type: "DataOverviewModel/getServicesList"});
      // dispatch({ type: "DataOverviewModel/getServicesListData"});
    }
  //注册量查询
  resourOnclick(){
      const {dispatch}=this.props;
      dispatch({ type: "DataOverviewModel/getresources", payload: { resourceType:1 }  });
  }

    //资源使用频率
    frequencyOnclick(){
      const {dispatch}=this.props;
      dispatch({ type: "DataOverviewModel/getresources", payload: { resourceType:4 }  });
  }

    //发布量
    releaseOnclick(){
      const {dispatch}=this.props;
      dispatch({ type: "DataOverviewModel/getresources", payload: { resourceType:3 }  });
  }

    //订阅量
    subscriptionOnclick(){
      const {dispatch}=this.props;
      dispatch({ type: "DataOverviewModel/getresources", payload: { resourceType:2 }  });
  }
    //接口调用次数
    expandedRowRenderServer = (record, index, indent, expanded) => {
         const {ServicesList} =this.props.DataOverviewModel;
         const columns = [
         { title: '通用时间', dataIndex: 'callTime', key: 'callTime' },
         { title: '客户端IP', dataIndex: 'ip', key: 'ip' },
         { title: '是否成功', dataIndex: 'success', key: 'success' },
         { title: '返回数据量', dataIndex: 'count', key: 'count' } ];
         return (
         <Table columns={columns}  dataSource={ServicesList ?ServicesList:[]}  pagination={false} />
         );
   };
   //接口调用次数 调用子组件接口
   onExpandServer=(expanded, record)=>{  const {dispatch}=this.props;dispatch({ type: "DataOverviewModel/getServicesList",payload:{serviceCode:record.serviceCode}})}

     //数据调用量
    RenderServer = (record, index, indent, expanded) => {
       console.log(record, index, indent, expanded,"record, index, indent, expanded");
      const {ServicesListData} =this.props.DataOverviewModel;
      const columns = [
         { title: '通用时间', dataIndex: 'callTime', key: 'callTime' },
         { title: '客户端IP', dataIndex: 'ip', key: 'ip' },
         { title: '是否成功', dataIndex: 'success', key: 'success' },
         { title: '返回数据量', dataIndex: 'count', key: 'count' } ];
      return (
      <Table columns={columns}  dataSource={ServicesListData?ServicesListData:[]}  pagination={false} />
      );
   };

   //数据调用量 调用子组件接口
   onExpandServerData=(expanded, record)=>{  const {dispatch}=this.props;dispatch({ type: "DataOverviewModel/getServicesListData",payload:{serviceCode:record.serviceCode}})}


  //上报任务数据量
   expandedRowRender = () => {
      const {ReportListData} =this.props.DataOverviewModel;
      const columns = [
      { title: '作业名称', dataIndex: 'taskName', key: 'taskName' },
      { title: '创建时间', dataIndex: 'createTime', key: 'createTime' } ];
      return (
      <Table columns={columns}  dataSource={ReportListData?ReportListData:[]}  pagination={false} />
      );
   };
 //上报任务数据量 调用子组件接口
 onExpandedExpanded=(expanded, record)=>{  const {dispatch}=this.props;dispatch({ type: "DataOverviewModel/getreportList",payload:{deptCode:record.deptCode}})}


     //上报数据量
   //   expandedRowRenderCount = () => {
   //    const {CountByTheAmountOfData} =this.props.DataOverviewModel;
   //    const columns = [
   //    { title: '作业名称', dataIndex: 'date', key: 'date' },
   //    { title: '创建事件', dataIndex: 'name', key: 'name' } ];
   //    return (
   //    <Table columns={columns}  dataSource={CountByTheAmountOfData}  pagination={false} />
   //    );
   // };

        //交换作业量
     expandedRowRenderTask = () => {
         const {ExchangeListData} =this.props.DataOverviewModel;
         const columns = [
         { title: '执行开始时间', dataIndex: 'startTime', key: 'startTime' },
         { title: '是否成功', dataIndex: 'status', key: 'status' },
         { title: '处理数据量', dataIndex: 'count', key: 'count' } ];
         return (
         <Table columns={columns}  dataSource={ExchangeListData}  pagination={false} />
         );
      };

       //上报任务数据量 调用子组件接口
 onExpandExpanded=(expanded, record)=>{  const {dispatch}=this.props;dispatch({ type: "DataOverviewModel/getExchangeList",payload:{deptId:record.deptId}})}



      //交换数据量
      // RenderTask = () => {
      //    const {NumberOfTasksData} =this.props.DataOverviewModel;
      //    const columns = [
      //    { title: '执行开始时间', dataIndex: 'date', key: 'date' },
      //    { title: '是否成功', dataIndex: 'name', key: 'name' },
      //    { title: '处理数据量', dataIndex: 'names', key: 'names' } ];
      //    return (
      //    <Table columns={columns}  dataSource={NumberOfTasksData}  pagination={false} />
      //    );
      // };

      onChange=(date, dateString)=>{
         const {dispatch}=this.props;
         dispatch({ type: "DataOverviewModel/getCount",payload:{startTime:dateString}});
         dispatch({ type: "DataOverviewModel/getCountByNumberOfCalls",payload:{startTime:dateString}});
         dispatch({ type: "DataOverviewModel/getServerByTheAmountOfData",payload:{startTime:dateString}});
         dispatch({ type: "DataOverviewModel/getReptByNumberOfTasks",payload:{startTime:dateString}});
         dispatch({ type: "DataOverviewModel/getCountByTheAmountOfData",payload:{startTime:dateString}});
         dispatch({ type: "DataOverviewModel/getCountByNumberOfTasks",payload:{startTime:dateString}});
         dispatch({ type: "DataOverviewModel/getNumberOfTasks",payload:{startTime:dateString}});
      }


   render(){
      const {countList,resourcesData,ServerNumberOfCallsData,ServerNumberOfCalls,ServerByTheAmountOfData,ServerByTheAmountOf,ReptByNumberOfTasksData,ReptByNumberOfTasks,
             CountByTheAmountOfData,CountByTheAmountOf,CountByNumberOfTasksData,CountByNumberOfTasks,NumberOfTasksData,NumberOfTasks} =this.props.DataOverviewModel;
           
      return(
         <div> 
             
               <div className={styles.leftTwo}>
                  <div className={styles.leftTitleSccess}>
                  <MonthPicker  format={dateFormat} defaultValue={moment()} onChange={this.onChange.bind(this)}/>
                  </div>
                    <div className={styles.bordColor}  onClick={this.resourOnclick.bind(this)}>
                         <div >
                              <Tooltip arrowPointAtCenter={true} trigger="click" placement="right"  overlayClassName="viweSpan" title={
                                 <Table className={styles.loginlist} bordered dataSource={resourcesData} pagination={false} columns={this.columnslist}/>
                              }>
                              <p className={styles.bordTitle}>注册量</p>
                              <p className={styles.bordTitleColor}>{countList.registerCount}</p>
                         </Tooltip>
                        </div>
                    </div>
                    <div className={styles.bordColor1} onClick={this.frequencyOnclick.bind(this)}>
                          <div>
                              <Tooltip style={{width:500}} trigger="click" placement="right" title={
                                 <span style={{width:500}}> <Table bordered dataSource={resourcesData} pagination={false} scroll={{y: 160}} columns={this.columnslist1} style={{width:500}}/></span>
                              }>
                              <p className={styles.bordTitle}>资源使用频率</p>
                              <p className={styles.bordTitleColor}>{countList.frequencyCount}</p>
                         </Tooltip>
                         </div>
                    </div>
                    <div className={styles.bordColor2} onClick={this.releaseOnclick.bind(this)}>
                        <div>
                              <Tooltip style={{width:500}} trigger="click" placement="right" title={
                                 <span style={{width:500}}> <Table bordered dataSource={resourcesData} pagination={false} scroll={{y: 160}} columns={this.columnslist2} style={{width:500}}/></span>
                              }>
                              <p className={styles.bordTitle}>发布量</p>
                              <p className={styles.bordTitleColor}>{countList.publicationCount}</p>
                           </Tooltip>
                         </div>
                    </div>
                    <div className={styles.bordColor3} onClick={this.subscriptionOnclick.bind(this)}>
                       <div>
                           <Tooltip style={{width:500}} trigger="click" placement="right" title={
                               <span style={{width:500}}> <Table bordered dataSource={resourcesData} pagination={false} scroll={{y: 160}} columns={this.columnslist3} style={{width:500}}/></span>
                           }>
                              <p className={styles.bordTitle}>订阅量</p>
                              <p className={styles.bordTitleColor}>{countList.subscriptionCount}</p>
                           </Tooltip>
                        </div>
                        
                    </div>

                   
               </div>
                {/* <Table bordered scroll={{y: 160}} className={styles.bordered} columns={this.columnsExit}  dataSource={ReptByNumberOfTasksData} pagination={false}/> */}
               <div className={styles.leftTwoLeft}>
                        <div className={styles.leftRile}>
                            <h3 className={styles.leftTitleImg}>接口调用次数 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="copy" /> {ServerNumberOfCalls.total}</p> </h3>
                            <Table className="components-table-demo-nested" columns={this.columnsInfo} expandedRowRender={this.expandedRowRenderServer}  rowKey="key" 
                                dataSource={ServerNumberOfCallsData}  scroll={{y: 160}} pagination={false} onExpand={this.onExpandServer}/>
                        </div>
                        <div className={styles.leftRile}>
                          <h3 className={styles.leftTitleImg}>数据调用量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="project" /> {ServerByTheAmountOf.total}</p> </h3>
                           <Table className="components-table-demo-nested" columns={this.columnsInfoCount} expandedRowRender={this.RenderServer} dataSource={ServerByTheAmountOfData} 
                               scroll={{y: 160}} pagination={false} onExpand={this.onExpandServerData} rowKey="key" />
                        </div>
                        <div className={styles.leftRile}>
                           <h3 className={styles.leftTitleImg}>上报任务数量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="file-text" /> {ReptByNumberOfTasks.total}</p> </h3>
                           <Table className="components-table-demo-nested" columns={this.columnsExit} expandedRowRender={this.expandedRowRender} dataSource={ReptByNumberOfTasksData} 
                                    scroll={{y: 160}} pagination={false} onExpand={this.onExpandedExpanded} rowKey="key"/>
                        </div>
                        <div className={styles.leftRile}>
                           <h3 className={styles.leftTitleImg}>上报数据量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="laptop" /> {CountByTheAmountOf.total}</p> </h3>  {/** expandedRowRender={this.expandedRowRenderCount} */}
                           <Table className="components-table-demo-nested" columns={this.columnsExitCount} dataSource={CountByTheAmountOfData}  scroll={{y: 160}} pagination={false}/>
                        </div>
                        <div className={styles.leftRile}>
                           <h3 className={styles.leftTitleImg}>交换作业量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="swap" /> {CountByNumberOfTasks.total}</p> </h3>
                           <Table className="components-table-demo-nested" columns={this.columnsExchange} expandedRowRender={this.expandedRowRenderTask} 
                               dataSource={CountByNumberOfTasksData}  scroll={{y: 160}} pagination={false} onExpand={this.onExpandExpanded} rowKey="key"/>
                        </div>
                        <div className={styles.leftRile}>
                        <h3 className={styles.leftTitleImg}>交换数据量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="bar-chart" /> {NumberOfTasks.total}</p> </h3> {/* expandedRowRender={this.RenderTask} */}
                           <Table className="components-table-demo-nested" columns={this.columnsExchangeCount} dataSource={NumberOfTasksData}  scroll={{y: 160}} pagination={false}/>
                        </div>
                        
               </div>
               
               {/* <Modal
                  title={serverModelList}
                  visible={visible}
                  width={600}
                  footer={null}
                  onCancel={this.handleCancel.bind(this)} >
               </Modal> */}
        
               
          </div>
      )
   }
}


export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
