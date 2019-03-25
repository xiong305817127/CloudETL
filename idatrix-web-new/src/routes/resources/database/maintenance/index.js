import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button,Select} from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter } from 'react-router';
import CheckView from '../../common/CheckView/index'
import {canPubArgs,canRecallArgs} from '../../constants'

const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

const index = ({form,dispatch,maintenanceModel,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,selectedRowKeys,loading,selectedRows,canStatus} = maintenanceModel;
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
       width:"18%"
    }, {
      title: '提供方',
      key: 'deptName',
      dataIndex: 'deptName',
      width:"15%",
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
      title: '更新时间',
      key: 'updateTime',
      dataIndex: 'updateTime',
       width:"15%"
    },{
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '100px',
      render: (text, record) => (
        <div>
          <a style={{fontSize: 14}}
            onClick={()=>{ handleCheckView(record) }}
          >查看</a>
        </div>
      )},
  ];

  //查看按钮
  const handleCheckView = (record)=>{ 
	    dispatch({
	      type:"checkview/getEditResource",
	      payload:{id:record.id}
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
    console.log(router,"{...location,query}",{...location.query.page,query},"query",query);
  }

  //发布
  const handleGrounding = ()=>{
    if(selectedRows.length === 0) return;
    
    let args = [];
    for(let index of selectedRows){
        args.push(index.id)
    }

    dispatch({type:"maintenanceModel/getPub",payload:{ids:args.join()}})
  }

  //下架
  const handleDown = ()=>{
    if(selectedRows.length === 0) return;
    
    let args = [];
    for(let index of selectedRows){
        args.push(index.id)
    }

    dispatch({type:"maintenanceModel/getRecall",payload:{ids:args.join()}})
  }

    //退回修改
  const handleBatchAgree = ()=>{
    if(selectedRows.length === 0) return;
    
    let args = [];
    for(let index of selectedRows){
        args.push(index.id)
    }

    dispatch({type:"maintenanceModel/getBack",payload:{ids:args.join()}})
  };

  const formItemLayout = {
    labelCol: {span:6},
    wrapperCol:{span:16},
  }

  const formItemLayout1 = {
    labelCol: {span:3},
    wrapperCol:{span:18},
  }

  const onChangeAllSelect = (e,record)=>{

    let canStatus = "none";

    console.log(e);
    console.log(record);

    if(record.length>0){
       let boo1 = record.every(index=>canPubArgs.includes(index.status));
       let boo2 = record.every(index=>canRecallArgs.includes(index.status));
       if(boo1){
          canStatus = "pub";
       }
       if(boo2){
          canStatus = "recall"
       }
    }
    dispatch({type:"maintenanceModel/save",payload:{canStatus,selectedRowKeys:e,selectedRows:record}})
  };


  const {name,code,deptName,status} = query;

  return(
    <div style={{padding: 20}}>
      <Form className="btn_std_group">
        <Row gutter={20}>
          <Col span={9} style={{ display:'block'}}>
            <FormItem label={"资源名称"} {...formItemLayout}>
              {getFieldDecorator("name",{
                initialValue:name?name:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={9} style={{ display:'block'}}>
            <FormItem label={"资源代码"}  {...formItemLayout}>
              {getFieldDecorator("code",{
                initialValue:code?code:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={9} style={{ display:'block'}}>
            <FormItem label={"提供方名称"}  {...formItemLayout}>
              {getFieldDecorator("deptName",{
                initialValue:deptName?deptName:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={9} style={{ display:'block'}}>
            <FormItem label={"状态"}  {...formItemLayout}>
              {getFieldDecorator("status",{
                initialValue:status?status:"all",
              })(
              <Select>
		            <Option value="all">全部</Option>
		            <Option value="pub">已发布</Option>
		            <Option value="recall">下架</Option>
		          </Select>
              )}
            </FormItem>
          </Col>
          <Col span={18} style={{ display:'none'}} >
             <FormItem
              label="注册时间"
               {...formItemLayout1}
            >
              {getFieldDecorator('time')(
                <RangePicker showTime format="YYYY-MM-DD HH:mm:ss"  />
              )}
            </FormItem>
          </Col>
          <Col span={24} className="search_btn" >
            <Button type="primary" onClick={handleSearch}>查询</Button>
            <Button type="primary" disabled={canStatus==="pub"?false:true} onClick={handleGrounding} style={{ marginLeft: 8 }}>发布</Button>
            <Button type="primary" disabled={canStatus==="recall"?false:true} onClick={handleDown} style={{ marginLeft: 8 }}>下架</Button>
            <Button type="primary" disabled={canStatus==="pub"?false:true} onClick={handleBatchAgree}  style={{ marginLeft: 8 }}>退回修改</Button>
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

export default connect(({ maintenanceModel })=>({ maintenanceModel }))(withRouter(Form.create()(index)));