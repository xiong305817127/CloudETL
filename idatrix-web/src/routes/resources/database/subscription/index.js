/*alisa
 2018-09-26
 系统管理的订阅关系管理
 */
import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button,Ico,Select  } from 'antd';
import { connect } from 'dva';
import styles from '../index.less';
import TableList from 'components/TableList';
import { withRouter,hashHistory } from 'react-router';
import Modal from 'components/Modal';
import { shareMethodArgs, } from '../../constants.js';
import moment from "moment";
import CheckView from '../../common/CheckView/index';
const FormItem = Form.Item;
const Optino = Select.Option;
const { RangePicker } = DatePicker;
const index = ({form,dispatch,subscriptionModel,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,loading,selectedRows} = subscriptionModel;
   const { query } = location;

   const columns = [
    {
      title: '资源代码',
      key: 'code',
      dataIndex: 'code',
      width:"12%"
    }, {
      title: '订阅编号',
      key: 'subNo',
      dataIndex: 'subNo',
       width:"10%"
    }, {
      title: '资源名称',
      key: 'name',
      dataIndex: 'name',
       width:"12%"
    }, {
      title: '提供方',
      key: 'deptName',
      dataIndex: 'deptName',
      width:"8%",
    },
    ,{
      title: '申请日期',
      key: 'applyDate',
      dataIndex: 'applyDate',
      width:"15%",
    },
    {
      title: '订阅状态',
      key: 'subscribeStatus',
      dataIndex: 'subscribeStatus',
      width:"6%",
    },
    {
      title: '交换方式',
      key: 'shareMethod',
      dataIndex: 'shareMethod',
      width:"10%",
      render:(text)=>{	return (<span>{shareMethodArgs[text].title}</span>) } },
    {
      title: '截止日期',
      key: 'endTime',
      dataIndex: 'endTime',
      width:"15%",
    },
   {
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '160px',
      render: (text, record, index) => {
          if(record.subscribeStatus === "订阅成功"){
          		return(
                <div>
      	        	<a style={{fontSize: 14}} onClick={()=>{ handleCheckView(record) }} >订阅详情</a>&nbsp;&nbsp;
      		        <a style={{fontSize: 14}} onClick={()=>{ handleOpraterNo(record) }} >终止订阅</a>
      	        </div>
          			);
        	}else if(record.subscribeStatus === "订阅终止"){
          		 return(
                    <div>
          	        	<a style={{fontSize: 14}} onClick={()=>{ handleCheckView(record) }} >订阅详情</a>&nbsp;&nbsp;
          		        <a style={{fontSize: 14}} onClick={()=>{ handleOprateryes(record) }} >恢复订阅</a>
          	        </div>
          		 );
        	}else{
          		 return(
                     <div>
                      	<a style={{fontSize: 14}}  onClick={()=>{ handleCheckView(record) }} >订阅详情</a>&nbsp;&nbsp;
    	               </div>
          		 );
        	}
        }
      }
  ];

  //订阅详情
  const handleCheckView = (record)=>{
   hashHistory.push(`/resources/database/DetailsList/${record.id}`)
  }
  //恢复订阅
  const handleOprateryes=(record)=>{
     dispatch({type:"subscriptionModel/getresume",payload:{id:record.id}})
  }
  //终止订阅
  const handleOpraterNo=(record)=>{
  	dispatch({type:"subscriptionModel/getstop",payload:{id:record.id}})
  }

  //查询
  const handleSearch = ()=>{
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      for(let index of Object.keys(query)){
        delete query[index]
      };

       for(let index of Object.keys(values)){
        if(index === "date"){
            if(values[index] && values[index].length>0){
                query["applyStartTime"] = values[index][0].format('YYYY-MM-DD');
                query["applyEndTime"] = values[index][1].format('YYYY-MM-DD');
            }else{
                delete query["applyStartTime"];
                delete query["applyEndTime"];
            }
        }else{
            if(values[index]){
                query[index] = values[index]
            }else{
              delete query[index]
            }
        }
      }
      query.page = 1;
      router.push({...location,query})
    })
  }

  const formItemLayout = {
    labelCol: {span:6},
    wrapperCol:{span:16},
  }

  const formItemLayout1 = {
    labelCol: {span:3},
    wrapperCol:{span:18},
  }

//日期格式
 const dateFormat = "YYYY-MM-DD";
//查询参数
 const {name,code,shareMethod,applyStartTime,applyEndTime,deptName,subStatus } = query;

  return(
    <div className={styles.mysource}>
      <Form className="btn_std_group">
        <Row gutter={20} >
          <Col span={8}>
            <FormItem label={"资源名称"} {...formItemLayout}>
              {getFieldDecorator("name",{
                initialValue:name?name:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={"资源代码"}  {...formItemLayout}>
              {getFieldDecorator("code",{
                initialValue:code?code:"",
              })(
               <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={"交换方式"}  {...formItemLayout}>
              {getFieldDecorator("shareMethod",{
                initialValue:shareMethod?shareMethod:"",
              })(
               <Select>
                    <Option value="" key="all">全部</Option>
                    <Option value="db" key="1">共享平台-数据库</Option>
                    <Option value="file" key="2">共享平台-文件下载</Option>
                    <Option value="service" key="3">共享平台-服务</Option>
                </Select>
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={"提供方名称"}  {...formItemLayout}>
              {getFieldDecorator("deptName",{
                initialValue:deptName?deptName:"",
              })(
               <Input  />
              )}
            </FormItem>
          </Col>
         
         <Col span={8}>
             <FormItem label={"申请日期"}  {...formItemLayout}>
              {getFieldDecorator("date",{
                initialValue:applyStartTime && applyEndTime?[moment(applyStartTime, dateFormat), moment(applyEndTime, dateFormat)]:[],
              })(
                 <RangePicker format={dateFormat} disabledDate={curr=>!(curr && curr < moment().endOf("day"))} />
              )}
            </FormItem>
          </Col>
           <Col span={8}>
            <FormItem label={"订阅状态"}  {...formItemLayout}>
              {getFieldDecorator("subStatus",{
                initialValue:subStatus?subStatus:"",
              })(
                <Select>
                    <Option value="" key="all">全部</Option>
                    <Option value="wait_approve" key="wait_approve">待审核</Option>
                    <Option value="success" key="success">订阅成功</Option>
                    <Option value="failed" key="failed">已拒绝</Option>
                    <Option value="stop" key="failed">订阅终止</Option>
                </Select>
              )}
            </FormItem>
          </Col>
           <Col span={24} className="search_btn">
            <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
          </Col>
        </Row>
        </Form>
        <div style={{paddingTop: 20}}>
          <TableList 
            showIndex
            loading={loading}
            rowKey='__index'
            columns={columns}
            dataSource={datasource}
            pagination={{total: total}}
            
          />
        </div>

         <CheckView />
    </div> 
  )
}

export default connect(({ subscriptionModel })=>({ subscriptionModel }))(withRouter(Form.create()(index)));