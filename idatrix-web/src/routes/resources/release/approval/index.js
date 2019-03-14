import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter } from 'react-router';
import CheckView from '../../common/CheckView/index';
import { sureConfirm } from "utils/utils";

const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

const index = ({form,dispatch,releaseApproval,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,selectedRowKeys,loading,selectedRows} = releaseApproval;
   const { query } = location;

  console.log(total,"总数");
  console.log(datasource,"数据数组");

   const columns = [
    {
      title: '资源代码',
      key: 'resourceCode',
      dataIndex: 'resourceCode',
      width:"15%"
    }, {
      title: '资源名称',
      key: 'resourceName',
      dataIndex: 'resourceName',
       width:"20%"
    }, {
      title: '提供方',
      key: 'deptName',
      dataIndex: 'deptName',
      width:"20%",
      render:(text,record)=>{
        return `${text}(${record.deptCode})`
      }
    },
    {
      title: '创建人',
      key: 'creator',
      dataIndex: 'creator',
      width:"10%"
    }, 
    {
      title: '状态',
      key: 'status',
      dataIndex: 'status',
       width:"10%"
    },{
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '200px',
      render: (text, record) => (
        <div>
          <a style={{fontSize: 14}}
            onClick={()=>{ handleCheckView(record) }}
          >审批</a>
        </div>
      )},
  ];


  //查看按钮
  const handleCheckView = (record)=>{
    dispatch({
      type:"checkview/getEditResource",
      payload:{id:record.id},
      actionType:"check"
    })
  }

  //查询
  const handleSearch = ()=>{
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      for(let index of Object.keys(values)){
        if(values[index]){
            query[index] = values[index]
        }else{
          delete query[index]
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

  //删除数据
  const handleBatchAgree = ()=>{
    if(selectedRows.length === 0) return;

    sureConfirm({
      title:"确定发布所选项吗？"
    },(bool)=>{
      if(bool){
        let args = [];
        for(let index of selectedRows){
            args.push(index.id)
        }
        dispatch({type:"releaseApproval/getBatchProcess",payload:{ids:args.join()}})
      }
    })
  };

  const onChangeAllSelect = (e,record)=>{
    dispatch({type:"releaseApproval/save",payload:{selectedRowKeys:e,selectedRows:record}})
  };


  const {name,code,deptName,deptCode} = query;

  return(
    <div style={{margin:20}}>
      <Form className="btn_std_group">
        <Row gutter={20}>
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
            <FormItem label={"提供方名称"}  {...formItemLayout}>
              {getFieldDecorator("deptName",{
                initialValue:deptName?deptName:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem label={"提供方代码"}  {...formItemLayout}>
              {getFieldDecorator("deptCode",{
                initialValue:deptCode?deptCode:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          {/* <Col span={18} style={{ display:'none'}} >
             <FormItem
              label="注册时间"
               {...formItemLayout1}
            >
              {getFieldDecorator('time')(
                <RangePicker showTime format="YYYY-MM-DD HH:mm:ss"  />
              )}
            </FormItem>
          </Col> */}
          <Col span={24} className="search_btn">
            <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
            <Button type="primary" htmlType="submit" disabled={selectedRows.length === 0?true:false} onClick={handleBatchAgree}  style={{ marginLeft: 8 }}>批量审批</Button>
          </Col>
        </Row>
        </Form>
        <div style={{marginTop:20}}>
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
        <CheckView />
    </div> 
  )
}

export default connect(({ releaseApproval })=>({ releaseApproval }))(withRouter(Form.create()(index)));