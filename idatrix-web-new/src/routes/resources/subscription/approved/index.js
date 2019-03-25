import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button,Ico,Select  } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter } from 'react-router';
import moment from "moment";

const { RangePicker } = DatePicker;
const FormItem = Form.Item;
const Option = Select.Option;

const index = ({form,dispatch,subscriptionApprovedModel,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,loading} = subscriptionApprovedModel;
   const { query } = location;

  console.log(total,"总数");
  console.log(datasource,"数据数组");

   const columns = [
    {
      title: '资源代码',
      key: 'code',
      dataIndex: 'code',
      width:"10%"
    }, {
      title: '资源名称',
      key: 'name',
      dataIndex: 'name',
       width:"10%"
    }, {
      title: '订阅方',
      key: 'deptName',
      dataIndex: 'deptName',
      width:"8%"
    }, {
      title: '订阅编号',
      key: 'subNo',
      dataIndex: 'subNo',
       width:"10%"
    },{
      title: '申请人',
      key: 'subscribeUserName',
      dataIndex: 'subscribeUserName',
      width:"8%",
    },
    {
      title: '申请时间',
      key: 'applyDate',
      dataIndex: 'applyDate',
      width:"150px",
    },
    {
      title: '审批动作',
      key: 'subscribeStatus',
      dataIndex: 'subscribeStatus',
       width:"8%",
      
    },{
      title: '审批时间',
      key: 'approveTime',
      dataIndex: 'approveTime',
      width:"150px",
    },{
      title: '审批意见',
      key: 'suggestion',
      dataIndex: 'suggestion'
    }
  ];

  //查询
  const handleSearch = ()=>{
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      for(let index of Object.keys(values)){
        if(index === "date"){
            if(values[index] && values[index].length>0){
                query["approveStartTime"] = values[index][0].format('YYYY-MM-DD');
                query["approveEndTime"] = values[index][1].format('YYYY-MM-DD');
            }else{
                delete query["approveStartTime"];
                delete query["approveEndTime"];
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


/*
approveStartTime: 20121222审批开始时间
approveEndTime: 20121222 审批结束时间

*/

  const disabledDate = (current)=> {
    // can not select days before today and today
    return !(current && current.valueOf() < Date.now());
  }


  const {name,subStatus,subDeptName,approveStartTime,approveEndTime} = query;

  const dateFormat = "YYYY-MM-DD";

  return(
    <div style={{margin:20}}>
      <Form className="btn_std_group">
        <Row gutter={20} >
          <Col span={8} style={{ display:'block'}}>
            <FormItem label={"资源名称"} {...formItemLayout}>
              {getFieldDecorator("name",{
                initialValue:name?name:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8} style={{ display:'block'}}>
            <FormItem label={"审批动作"}  {...formItemLayout}>
              {getFieldDecorator("subStatus",{
                initialValue:subStatus?subStatus:"",
              })(
                <Select>
                    <Option value="" key="all">全部</Option>
                    <Option value="wait_approve" key="wait_approve">待审核</Option>
                    <Option value="success" key="success">订阅成功</Option>
                    <Option value="failed" key="failed">已拒绝</Option>
                </Select>
              )}
            </FormItem>
          </Col>
          <Col span={8} style={{ display:'block'}}>
            <FormItem label={"订阅方名称"}  {...formItemLayout}>
              {getFieldDecorator("subDeptName",{
                initialValue:subDeptName?subDeptName:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>

          <Col span={8} style={{ display:'block'}}>
            <FormItem label={"审批时间"}  {...formItemLayout}>
              {getFieldDecorator("date",{
                initialValue:approveStartTime && approveEndTime?[moment(approveStartTime, dateFormat), moment(approveEndTime, dateFormat)]:[],
              })(
                 <RangePicker disabledDate={disabledDate} format={dateFormat} />
              )}
            </FormItem>
          </Col>
          <Col span={8}></Col>
          <Col span={8} className="search_btn">
            <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
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
          />
        </div>
    </div> 
  )
}

export default connect(({ subscriptionApprovedModel })=>({ subscriptionApprovedModel }))(withRouter(Form.create()(index)));