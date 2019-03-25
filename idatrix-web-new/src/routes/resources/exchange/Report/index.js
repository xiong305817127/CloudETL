import { connect } from 'dva';
import ReactDOM from 'react-dom';
import ReactEcharts from 'echarts-for-react';
import { withRouter,hashHistory } from 'react-router';
import {Row,Col,Icon,Button,Form,Input,Select,message,Tooltip,Popconfirm,Cascader,DatePicker,Card } from 'antd';
import { API_BASE_CATALOG } from 'constants';
import { uploadFile, convertArrayToTree,downloadFile } from 'utils/utils';
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
import TableList from "components/TableList";
import { strEnc, strDec } from 'utils/EncryptUtil';
import Modal from 'components/Modal';
import ControlJobPlatform from '../../../gather/components/taskCenter/controlPlatform/ControlJobPlatform';
import RunJob from '../../../gather/components/designPlatform/JobPlatform/RunJob';
import { getStatisticsReact,getRunningReact,getOverviewTask,getHistory } from  'services/DirectoryOverview';
import { Cookies } from 'react-cookie';

const cookie = new Cookies(); 
const formItemLayout1 = {
    labelCol: {span:6},
    wrapperCol:{span:17},
  }

const CustomizedForm = Form.create({
  onFieldsChange(props, changedFields) {
    props.onChange(changedFields);
  },
   
  onValuesChange(_, values) {
    //console.log(values);
  },
})((props) => {
  const { handleSearch} = props;
  const { getFieldDecorator } = props.form;
  return (
       <Row gutter={20}>
          <Col span={8}>
             <FormItem label="作业名称" {...formItemLayout1}>
              {getFieldDecorator("taskName",{
              })(
                <Input/>
              )}
            </FormItem>
         </Col>
          <Col span={8} >
            <FormItem label={"资源代码"} {...formItemLayout1} >
              {getFieldDecorator("code",{
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={"作业状态"} {...formItemLayout1} >
              {getFieldDecorator("taskStatus",{
              })(
                 <Select allowClear={true} style={{ width: "100%" }}>
                    <Option value="">全部</Option>
                    <Option value="WAIT_IMPORT">等待入库</Option>
                    <Option value="IMPORTING">入库中</Option>
                    <Option value="IMPORT_COMPLETE">已入库</Option>
                    <Option value="IMPORT_ERROR">入库失败</Option>
                    <Option value="STOP_IMPORT">终止入库</Option>
                </Select>
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={"提供方"} {...formItemLayout1} >
              {getFieldDecorator("provideDept",{
              })(
                <Input  />
              )}
            </FormItem>
         </Col>
          <Col span={8}>
            <FormItem label={"订阅方"} {...formItemLayout1} >
              {getFieldDecorator("subscribeDept",{
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8} className="search_btn">
              <Button type="primary" onClick={handleSearch}> 查询</Button>
          </Col>
     </Row>
  );
});

class reportlist extends React.Component {
   constructor(props){
    super(props);
         this.state = {
           fields: {
            taskName: { value: '',},
            code: { value: '', },
            taskStatus: { value: '', },
            provideDept: { value: '', },
            subscribeDept: { value: '', },
          },
            pagination:{
              page:1,
              pageSize:10
            },
           monthNamelist:"",
           dateCountlist:"",
           count:"",
           exCount:"",
	        total:0,
	        option :{
             tooltip: {
                  trigger: 'axis'
              },
              xAxis: {
                  type: 'category',
                  data: []
              },
              yAxis: [{
                  name : '数据量（个）',
                  type: 'value',
                  splitLine: {
                    show: false
                }
              },{
                  name : '作业数量（条）',
                  type: 'value',
                  min: 0,
                  max: 250,
                  splitLine: {
                    show: false
                }
              }],
            grid: {
                left: '3%',
                right: '2%',
                bottom: '3%',
                top: "15%",
                show: true,
                containLabel: true,
                borderWidth: 0,
                borderColor: "#4d4d4d"
              },
             series: [{
                          name:'数据量',
                          data: [],
                          type: 'bar',
                          smooth: true,
                          
                         itemStyle : {
                          normal : {
                              color:'#56c9e9'
                          },
                            areaStyle: {
                              color:'#56c9e9'
                           }
                         },
                      },{
                         name:'纵轴作业数量',
                          data:[],
                          type: 'line',
                           smooth: true,
                         
                         itemStyle : {
                          normal : {
                              color:'#85e0a4'
                          },
                           areaStyle: {
                              color:'skyblue'
                           }
                         },
                             
                      }]
          }
      }   
  }


  columns = [
   {
      title: '作业名称',
      dataIndex: 'taskName',
      key: 'taskName',
    }, {
      title: '作业编号',
      dataIndex: 'subscribeId',
      key: 'subscribeId',
    }, {
      title: '资源代码',
      dataIndex: 'code',
      key: 'code',
    }, {
      title: '资源名称',
      dataIndex: 'name',
      key: 'name',
    }, {
      title: '订阅方',
      dataIndex: 'subscribeDept',
      key: 'subscribeDept',
    }, {
      title: '提供方',
      dataIndex: 'provideDept',
      key: 'provideDept',
    }, {
      title: '截止日期',
      dataIndex: 'endTime',
      key: 'endTime',
    }, {
      title: '作业类型',
      dataIndex: 'taskType',
      key: 'taskType',
       render:(text)=>{
        const alRoot= text ==="DB"?"数据库":"文件类型";
        return(
          <span>{alRoot}</span>
        )
      }
    }, {
      title: '最近执行时间',
      dataIndex: 'lastRunTime',
      key: 'lastRunTime',
    }, {
      title: '数据量',
      dataIndex: 'dataCount',
      key: 'dataCount',
    }, {
      title: '作业状态',
      dataIndex: 'taskStatus',
      key: 'taskStatus',
      render:(text)=>{
         const alRoots= text ==="WAIT_IMPORT"?"等待入库":""||text ==="IMPORTING"?"入库中":""||
            text ==="IMPORT_COMPLETE"?"已入库":""||text ==="IMPORT_ERROR"?"入库失败":""||text ==="STOP_IMPORT"?"终止入库":"";
        return(
          <span>{alRoots}</span>
        )
      }
    }, {
      title: '操作',
      dataIndex: 'x',
      key: 'x',
       render: (text,record,index) => {
        if(record.taskStatus === "WAIT_IMPORT" || record.taskStatus === "IMPORT_ERROR"){
            return (<div></div>)
        }else{
           return (<div>
             <a>
              <Tooltip title="监控" >
                  <Icon style={{fontSize:"16px",cursor:"pointer"}} onClick={(e)=>{this.handleControl(record.subscribeId,record.creator)}} type="eye" />&nbsp;&nbsp;&nbsp;&nbsp;
              </Tooltip>
            </a>
             <a onClick={()=>{this.HideMore(record.taskName)}}>
              <Tooltip title="交换历史" >
                  交换历史&nbsp;&nbsp;
              </Tooltip>
            </a>
          </div>)
        }
      }
    }];

      columns1 = [
       {
          title: '上报任务',
          dataIndex: 'taskName',
          key: 'taskName',
        }, {
          title: '开始时间',
          dataIndex: 'startTime',
          key: 'startTime',
        }];

       columns2 = [
       {
          title: '交换任务',
          dataIndex: 'etlRunningId',
          key: 'etlRunningId',
          width:"20%"
        }, {
          title: '状态',
          dataIndex: 'status',
          key: 'status',
          width:"15%"
        }, {
          title: '开始时间',
          dataIndex: 'startTime',
          key: 'startTime',
          width:"28%"
        }, {
          title: '结束时间',
          dataIndex: 'endTime',
          key: 'endTime',
          width:"28%"
        }, {
          title: '数据量',
          dataIndex: 'dataCount',
          key: 'dataCount',
          width:"10%"
        }];

   componentWillReceiveProps(nextProps){
        const {pagination,data,loading,total} = nextProps.reportModel;
        console.log(nextProps.reportModel,"nextProps.reportModel");
        const { dispatch } = this.props;
        this.setState({pagination,data,loading,total})
    }


	 componentDidMount() {
     this.handleSearch();
     this.require();
     
	 }
	 
     /*查询接口*/
	    require(){
          const { dispatch } = this.props;
        
           getStatisticsReact().then((res)=>{
              const {code,data } = res.data;
               if(code === "200"){
                 if(data){
                     let seriesa=[];
                    let xAxisa =[];
                    let xAxisTwo=[];
                    for(let key of data.describes){
                       seriesa.push(key.month);
                       xAxisa.push(key.dateCount);
                       xAxisTwo.push(key.taskCount);
                    }
                    let series = [{data:[...xAxisa],type:'bar'},{data:[...xAxisTwo],type:'line'}];
                    let xAxis = {data:[...seriesa]};
                     const option = {...this.state.option,series,xAxis};
                    this.setState({
                      monthNamelist:series,
                      dateCountlist:xAxis,
                      count:res.data.data.count,
                       /*...this.state,*/
                      option:option,
                    })
                  }
               }
            })

            getRunningReact().then((res)=>{
               const {code,data } = res.data;
               if(code === "200"){
                  dispatch({
                    type:"reportModel/setMetaId",
                    payload:{
                         dataList:data.exchangTaskInfo,
                         exCount:data.count,
                     }
                 })
               }
            })
      }

       /*查询列表接口*/
      handleSearch= ()=>{
        
         const { dispatch } = this.props;
          const fields = this.state.fields;
          const pager = this.state.pagination;
          const { query } = this.props.location;
         this.props.form.validateFields((err, values) => {
              let arr={};
                 arr.taskName = fields.taskName.value;
                 arr.code = fields.code.value;
                 arr.provideDept = fields.provideDept.value;
                 arr.taskStatus = fields.taskStatus.value;
                 arr.subscribeDept = fields.subscribeDept.value;
                 arr.page=1;
                 arr.pageSize=query.pageSize?query.pageSize:10;
             getOverviewTask(arr,{
                page: 1,
                pageSize: query.pageSize || pager.pageSize,
              }).then((res)=>{
                   const {code,data,total,message } = res.data;
                   console.log( data.results," data.results");
                     if(code === "200"){
                       pager.total = data.total;
                          data.results.map( (row, index) => {
                           /* row.key = row.id;*/
                            row.index = pager.pageSize * (pager.pager - 1) + index + 1;
                            return row;
                          });
                          pager.taskName=fields.taskName.value,
                          pager.code =fields.code.value,
                          pager.provideDept =fields.provideDept.value,
                          pager.taskStatus=fields.taskStatus.value,
                          pager.subscribeDept=fields.subscribeDept.value,
                        dispatch({
                            type:"reportModel/setMetaId",
                            payload:{
                                 data:data.results,
                                 pagination: pager,
                                 loading:false,
                             }
                         })
                         this.setState({pagination: pager,data:data.results})
                     }else{
                        dispatch({
                            type:"reportModel/setMetaId",
                            payload:{
                                 /*pagination: pager,*/
                                 loading:false,
                             }
                         })
                          this.setState({data:[]})
                  }
                });
            })
       
      }
      
   /*打开监控*/
    handleControl = (name,creator)=>{
      const { dispatch } = this.props;
      const { username, renterId } = this.props.account;
      console.log(creator,username, renterId ,"realName, renterId");
      let ower = strEnc(creator, username, ""+renterId, "GBXVDHSKENCNJDUSBZACXBLMKDICDHJNBC");
      cookie.set('resource_owner', ower, { Path: '/T' });
      cookie.set(creator, username);

      dispatch({
        type:"controljobplatform/openJobs",
        payload:{
          visible:true,
          transName:name
          
        }
      });
  };

  handHide(){
         const { dispatch } = this.props;
         dispatch({
              type:"reportModel/hideModel",
              payload:{
                   visibles:false
               }
           })
      }
      handShow(){
         const { dispatch } = this.props;
         dispatch({
              type:"reportModel/hideModel",
              payload:{
                   visibles:false
               }
           })
     }
     HideMore(taskId){
       const { dispatch } = this.props;
       let arr ={};
       arr.taskId = taskId;
       getHistory(arr).then((res)=>{
        const {code,data,total,message } = res.data;
         if(code === "200"){
             dispatch({
              type:"reportModel/showModel",
              payload:{
                   datalistTo:data,
                   visibles:true
               }
           })
         }
       })
         
     }
    
        /*获取搜索框的值*/
  handleFormChange = (changedFields) => {
    this.setState(({ fields }) => ({
      fields: { ...fields, ...changedFields },
    }));
  }
  render() {
     const { getFieldDecorator } = this.props.form;
     const { text,visibles,dataList,exCount,datalistTo}= this.props.reportModel;
     console.log(this.props.reportModel,"Look");
     const { option,count,pagination,data } = this.state;
     const fields = this.state.fields;
      const formItemLayout = {
        labelCol: {span:6},
        wrapperCol:{span:12},
      };
    return (
      <div style={{margin:20}}>
          <Row gutter={20}>
            <Col span={17}>
              <Card
                  className="resetCardTitle"
                  title="作业统计"
                extra={<span className="bold_b">作业总量{count}</span>}
              >
                <ReactEcharts
                  option={option}
                  style={{height: '260px', width: '98%'}}
                  notMerge={false}
                  lazyUpdate={true}
                  theme={"theme_name"}
                  onChartReady={this.onChartReadyCallback}
                  onEvents={this.EventsDict}
                />
              </Card>
            </Col>
            <Col span={7}>
              <Card
                  className="resetCardTitle"
                  title={"正在进行的作业（最新5条）"}
                  extra={<span className="bold_b">进行中:{exCount}条</span>}
                >
                <div style={{height:260}}>
                  <TableList
                    showIndex
                    onRowClick={()=>{return false}}
                    columns={this.columns1}
                    dataSource={dataList}
                    className="th-nowrap"
                    pagination={false}
                  />
                </div>
              </Card>
            </Col>
          </Row>
          <div className="btn_std_group">
            <Form className="ant-advanced-search-form" >
              <CustomizedForm {...fields} onChange={this.handleFormChange} handleSearch={this.handleSearch}/>
              <Modal
                title="交换历史"
                visible={visibles}
                width={"60%"}
                footer={[
                      <Button size="large" onClick={this.handHide.bind(this)}>取消</Button>,
                      <Button type="primary" size="large" onClick={this.handShow.bind(this)}>确定</Button>,
                      ]}
                  onCancel={this.handHide.bind(this)} >
                    <TableList
                        showIndex
                        onRowClick={()=>{return false}}
                        style={{marginTop: 20}}
                        columns={this.columns2}
                        dataSource={datalistTo}
                        className="th-nowrap"
                        pagination={false}
                        scroll={{y: 500}}
                      />
              </Modal>
            </Form>
          </div>
          
               <div>
                     <TableList
                       showIndex
                       onRowClick={()=>{return false}}
                       style={{marginTop: 20}}
                       columns={this.columns}
                       dataSource={data}
                       className="th-nowrap"
                       pagination={pagination}
                     />
                  </div>

                  <ControlJobPlatform />
                  <RunJob/>
      </div>
      
    );
  }
}
const reportlistForm = Form.create()(reportlist);
export default connect(({reportModel,resourcesCommon,account})=>({
  reportModel,resourcesCommon,account
}))(reportlistForm);