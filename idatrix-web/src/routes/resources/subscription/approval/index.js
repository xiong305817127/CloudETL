import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter,hashHistory } from 'react-router';
import moment from "moment";

const FormItem = Form.Item;
const { RangePicker } = DatePicker;
const index = ({form,dispatch,subscriptionApproval,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,selectedRowKeys,loading,selectedRows} = subscriptionApproval;
   const { query } = location;

  console.log(total,"总数");
  console.log(datasource,"数据数组");

   const columns = [
    {
      title: '订阅编号',
      key: 'subNo',
      dataIndex: 'subNo',
      width:"12%"
    }, {
      title: '资源代码',
      key: 'code',
      dataIndex: 'code',
       width:"12%"
    }, {
      title: '资源名称',
      key: 'name',
      dataIndex: 'name',
      width:"20%",
    },{
      title: '提供方',
      key: 'deptName',
      dataIndex: 'deptName',
      width:"8%"
    }, {
      title: '订阅方',
      key: 'subscribeDeptName',
      dataIndex: 'subscribeDeptName',
      width:"8%"
    },
    {
      title: '申请人',
      key: 'subscribeUserName',
      dataIndex: 'subscribeUserName',
      width:"10%",
    },
    {
      title: '申请日期',
      key: 'applyDate',
      dataIndex: 'applyDate',
      width:"150px",
    },
   {
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '160px',
      render: (text, record) => (
        <div>
          {/* <a style={{fontSize: 14}}
              onClick={()=>{ handleCheckViewClick(record) }}
          >订阅详情</a>&nbsp;&nbsp; */}
        	<a style={{fontSize: 14}}
	            onClick={()=>{ handleCheckView(record) }}
	        >审批</a>
        </div>
      )},
  ];

    //订阅详情
  const handleCheckViewClick = (record)=>{
    hashHistory.push(`/resources/subscription/approvalInst/${record.id}`);
    dispatch({type:"subscriptionDetailModel/save",payload:{status:"approw"}})
  }

  //审批界面
  const handleCheckView = (record)=>{
    hashHistory.push(`/resources/subscription/approval/${record.id}`);
    dispatch({type:"subscriptionDetailModel/save",payload:{status:"approval"}})
  }

  //查询
  const handleSearch = ()=>{
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
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
    labelCol: {span:8},
    wrapperCol:{span:16},
  }

  //删除数据
  const handleBatchAgree = ()=>{
    if(selectedRows.length === 0) return;
    
    let args = [];
    for(let index of selectedRows){
        args.push(index.id)
    }

    dispatch({type:"subscriptionApproval/getBatchProcess",payload:{ids:args.join()}})
  };

  const onChangeAllSelect = (e,record)=>{
    dispatch({type:"subscriptionApproval/save",payload:{selectedRowKeys:e,selectedRows:record}})
  };
 const dateFormat = "YYYY-MM-DD";

  const {name,code,subDeptName,applyStartTime,applyEndTime } = query;

  return(
    <div style={{margin: 20}}>
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
            <FormItem label={"提供方"}  {...formItemLayout}>
              {getFieldDecorator("subDeptName",{
                initialValue:subDeptName?subDeptName:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
         <Col span={8}>
             <FormItem label={"申请时间"}  {...formItemLayout}>
              {getFieldDecorator("date",{
                initialValue:applyStartTime && applyEndTime?[moment(applyStartTime, dateFormat), moment(applyEndTime, dateFormat)]:[],
              })(
                 <RangePicker 
                  format={dateFormat}
                  disabledDate={curr=>!(curr && curr < moment().endOf("day"))} 
                  />
              )}
            </FormItem>
          </Col>
          <Col span={8}></Col>
          <Col span={8} className="search_btn">
            <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
            <Button type="primary" htmlType="submit" disabled={selectedRows.length === 0?true:false} onClick={handleBatchAgree}  style={{ marginLeft: 8 }}>批量审批</Button>
          </Col>
        </Row>
        </Form>
        <div style={{marginTop: 20}}>
          <TableList 
            showIndex
            loading={loading}
            rowKey='__index'
            columns={columns}
            dataSource={datasource}
            pagination={{total: total}}
            rowSelection={{
              onChange:(e,record)=>{onChangeAllSelect(e,record)},
              selectedRowKeys
            }}
          />
        </div>
    </div> 
  )
}

export default connect(({ subscriptionApproval,subscriptionDetailModel })=>({ subscriptionApproval,subscriptionDetailModel }))(withRouter(Form.create()(index)));