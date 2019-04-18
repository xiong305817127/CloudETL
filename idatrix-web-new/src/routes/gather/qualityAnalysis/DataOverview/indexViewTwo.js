import React from 'react'
import { Table, Popover, Button,Select,Icon,Tooltip,DatePicker } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import Modal from 'components/Modal';
const Option = Select.Option;
import moment from 'moment';
import { select } from 'redux-saga/effects';

const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY-MM';

class index extends React.Component {
   constructor(props) {
       super(props);
       this.state = {
           optionBor:[],
           status:"",
           visible: false,
           visible1: false,
           visible2: false,
           visible3: false
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
    expandedRowRenderServer = [
         { title: '通用时间', dataIndex: 'callTime', key: 'callTime',width:"30%" },
         { title: '客户端IP', dataIndex: 'ip', key: 'ip',width:"30%" },
         { title: '是否成功', dataIndex: 'success', key: 'success',width:"20%" },
         { title: '返回数据量', dataIndex: 'count', key: 'count',width:"20%" } 
        ];

   expandedRowRenderServerBind=(res)=>{
      const {dispatch}=this.props;
      dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
      dispatch({ type: "DataOverviewModel/getServicesList",payload:{serviceCode:res.serviceCode}})
      this.setState({
        status:"expandedRowRenderServerBind"
      })
   }

     //数据调用量
    RenderServer = [
         { title: '通用时间', dataIndex: 'callTime', key: 'callTime',width:"30%" },
         { title: '客户端IP', dataIndex: 'ip', key: 'ip',width:"30%" },
         { title: '是否成功', dataIndex: 'success', key: 'success',width:"20%" },
         { title: '返回数据量', dataIndex: 'count', key: 'count',width:"20%" }
    ];
    RenderServerBind=(res)=>{
        const {dispatch}=this.props;
        dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
        dispatch({ type: "DataOverviewModel/getServicesListData",payload:{serviceCode:res.serviceCode}})
        this.setState({
          status:"RenderServerBind"
        })
    }

  //上报任务数据量
  expandedRowRender=[
    { title: '作业名称', dataIndex: 'taskName', key: 'taskName' },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime' }
   ];
    expandedRowRenderBind=(res)=>{
      const {dispatch}=this.props;
        dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
        dispatch({ type: "DataOverviewModel/getreportList",payload:{deptCode:res.deptCode}});
        this.setState({
          status:"expandedRowRenderBind"
        })
    }

        //交换作业量
     expandedRowRenderTask =[
         { title: '执行开始时间', dataIndex: 'startTime', key: 'startTime',width:"40%" },
         { title: '是否成功', dataIndex: 'status', key: 'status',width:"30%" },
         { title: '处理数据量', dataIndex: 'count', key: 'count',width:"30%" } ];

     expandedRowRenderTaskBind=(record)=>{
          const {dispatch}=this.props;
          dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
          dispatch({ type: "DataOverviewModel/getExchangeList",payload:{deptId:record.deptId}})
          this.setState({
            status:"expandedRowRenderTaskBind"
          })
      }


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

      handleCancel=(e)=>{
        const {dispatch}=this.props;
        dispatch({ type: "DataOverviewModel/save",payload:{visible:false}});
      }

      handleVisibleChange = (visible) => { this.setState({ visible });}
      handleVisibleChange1 = (visible1) => { this.setState({ visible1 });}
      handleVisibleChange2 = (visible2) => { this.setState({ visible2 });}
      handleVisibleChange3 = (visible3) => { this.setState({ visible3 });}


   render(){
      const {countList,resourcesData,ServerNumberOfCallsData,ServerNumberOfCalls,ServerByTheAmountOfData,ServerByTheAmountOf,ReptByNumberOfTasksData,ReptByNumberOfTasks,
             CountByTheAmountOfData,CountByTheAmountOf,CountByNumberOfTasksData,CountByNumberOfTasks,NumberOfTasksData,NumberOfTasks,visible,ServicesListData,ExchangeListData,
             ReportListData,ServicesList } =this.props.DataOverviewModel;
      return(
         <div> 
             
               <div className={styles.leftTwo}>
                  <div className={styles.leftTitleSccess}>
                  <MonthPicker  format={dateFormat} defaultValue={moment()} onChange={this.onChange.bind(this)}/>
                  </div>
                    <div className={styles.bordColor}  onClick={this.resourOnclick.bind(this)}>
                         <div >
                           <Popover
                              content={<Table bordered dataSource={resourcesData} pagination={false} columns={this.columnslist}/>}
                              title="详情"
                              trigger="click"
                              visible={this.state.visible}
                              onVisibleChange={this.handleVisibleChange.bind(this)}
                            >
                              <p className={styles.bordTitle}>注册量</p>
                              <p className={styles.bordTitleColor}>{countList.registerCount}</p>
                            </Popover>     
                        </div>
                    </div>
                    <div className={styles.bordColor1} onClick={this.frequencyOnclick.bind(this)}>
                          <div>
                          <Popover
                              content={<Table bordered dataSource={resourcesData} pagination={false} scroll={{y: 160}} columns={this.columnslist1} style={{width:500}}/>}
                              title="资源使用频率详情"
                              trigger="click"
                              visible={this.state.visible1}
                              onVisibleChange={this.handleVisibleChange1.bind(this)}
                            >
                              <p className={styles.bordTitle}>资源使用频率</p>
                              <p className={styles.bordTitleColor}>{countList.frequencyCount}</p>
                            </Popover>     
                         </div>
                    </div>
                    <div className={styles.bordColor2} onClick={this.releaseOnclick.bind(this)}>
                        <div>
                          <Popover
                              content={<Table bordered dataSource={resourcesData} pagination={false} scroll={{y: 160}} columns={this.columnslist2} style={{width:500}}/>}
                              title="发布量详情"
                              trigger="click"
                              visible={this.state.visible2}
                              onVisibleChange={this.handleVisibleChange2.bind(this)}
                            >
                               <p className={styles.bordTitle}>发布量</p>
                               <p className={styles.bordTitleColor}>{countList.publicationCount}</p>
                            </Popover>     
                         </div>
                    </div>
                    <div className={styles.bordColor3} onClick={this.subscriptionOnclick.bind(this)}>
                       <div>
                       <Popover
                              content={<Table bordered dataSource={resourcesData} pagination={false} scroll={{y: 160}} columns={this.columnslist3} style={{width:500}}/>}
                              title="订阅量详情"
                              trigger="click"
                              visible={this.state.visible3}
                              onVisibleChange={this.handleVisibleChange3.bind(this)}
                            >
                               <p className={styles.bordTitle}>订阅量</p>
                               <p className={styles.bordTitleColor}>{countList.subscriptionCount}</p>
                            </Popover>     
                          
                        </div>
                        
                    </div>

                   
               </div>
                {/* <Table bordered scroll={{y: 160}} className={styles.bordered} columns={this.columnsExit}  dataSource={ReptByNumberOfTasksData} pagination={false}/> */}
               <div className={styles.leftTwoLeft}>
                        <div className={styles.leftRile}>
                            <h3 className={styles.leftTitleImg}>接口调用次数 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="copy" /> {ServerNumberOfCalls.total}</p> </h3>
                            <Table className="components-table-demo-nested" columns={this.columnsInfo}  onRowClick={(record)=>{this.expandedRowRenderServerBind(record)}} rowKey="key" 
                                dataSource={ServerNumberOfCallsData}  scroll={{y: 160}} pagination={false} onExpand={this.onExpandServer}/>
                        </div>
                        <div className={styles.leftRile}>
                          <h3 className={styles.leftTitleImg}>数据调用量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="project" /> {ServerByTheAmountOf.total}</p> </h3>
                           <Table className="components-table-demo-nested" columns={this.columnsInfoCount} onRowClick={(record)=>{this.RenderServerBind(record)}} dataSource={ServerByTheAmountOfData} 
                               scroll={{y: 160}} pagination={false} onExpand={this.onExpandServerData} rowKey="key" />
                        </div>
                        <div className={styles.leftRile}>
                           <h3 className={styles.leftTitleImg}>上报任务数量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="file-text" /> {ReptByNumberOfTasks.total}</p> </h3>
                           <Table className="components-table-demo-nested" columns={this.columnsExit} dataSource={ReptByNumberOfTasksData} 
                                    scroll={{y: 160}} pagination={false} onExpand={this.onExpandedExpanded} rowKey="key"  onRowClick={(record)=>{this.expandedRowRenderBind(record)}}/> 
                                    {/* onRowClick={(record)=>this.expandedRowRenderBind(record)}  */}
                        </div>
                        <div className={styles.leftRile}>
                           <h3 className={styles.leftTitleImg}>上报数据量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="laptop" /> {CountByTheAmountOf.total}</p> </h3>  {/** expandedRowRender={this.expandedRowRenderCount} */}
                           <Table className="components-table-demo-nested" columns={this.columnsExitCount} dataSource={CountByTheAmountOfData}  scroll={{y: 160}} pagination={false}/>
                        </div>
                        <div className={styles.leftRile}>
                           <h3 className={styles.leftTitleImg}>交换作业量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="swap" /> {CountByNumberOfTasks.total}</p> </h3>
                           <Table className="components-table-demo-nested" columns={this.columnsExchange}  onRowClick={(record)=>{this.expandedRowRenderTaskBind(record)}}
                               dataSource={CountByNumberOfTasksData}  scroll={{y: 160}} pagination={false} onExpand={this.onExpandExpanded} rowKey="key"/>
                        </div>
                        <div className={styles.leftRile}>
                        <h3 className={styles.leftTitleImg}>交换数据量 &nbsp;&nbsp;<p className={styles.leftColor}><Icon type="bar-chart" /> {NumberOfTasks.total}</p> </h3> {/* expandedRowRender={this.RenderTask} */}
                           <Table className="components-table-demo-nested" columns={this.columnsExchangeCount} dataSource={NumberOfTasksData}  scroll={{y: 160}} pagination={false}/>
                        </div>
                        
               </div>
               
                <Modal
                  title={"查看详情"}
                  visible={visible}
                  width={600}
                  footer={null}
                  onCancel={this.handleCancel.bind(this)} >
                  {/* 接口调用次数 */}
                   { this.state.status === "expandedRowRenderServerBind"?(
                      <Table rowKey="key" columns={this.expandedRowRenderServer} dataSource={ServicesList ?ServicesList :[]} scroll={{y: 300}} pagination={false} />
                    ):null }
                  {/* 数据调用量 */}
                   { this.state.status === "RenderServerBind"?(
                       <Table rowKey="key" columns={this.RenderServer} dataSource={ServicesListData?ServicesListData:[]} scroll={{y: 300}} pagination={false} />
                    ):null }
                     {/* 上报任务数据量 */}
                   { this.state.status === "expandedRowRenderBind"?(
                     <Table rowKey="key" columns={this.expandedRowRender} dataSource={ReportListData?ReportListData:[]} scroll={{y: 300}} pagination={false} />
                    ):null }
                    {/* 交换作业量 */}
                   { this.state.status === "expandedRowRenderTaskBind"?(
                    <Table rowKey="key" columns={this.expandedRowRenderTask} dataSource={ExchangeListData?ExchangeListData:[]} scroll={{y: 300}} pagination={false} />
                    ):null }
                  
                 
                  
               </Modal> 
        
               
          </div>
      )
   }
}


export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
