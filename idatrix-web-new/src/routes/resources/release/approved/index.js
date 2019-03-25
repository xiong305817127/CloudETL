import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter } from 'react-router';
import Modal from 'components/Modal';
import CheckView from '../../common/CheckView/index'

const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;

const index = ({form,dispatch,releaseApprovedModel,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,CheckHistoryShow,loading,datasource2,selectName} = releaseApprovedModel;
   const { query } = location;

  console.log(total,"总数");
  console.log(datasource,"数据数组");

   const columns = [
    {
      title: '资源代码',
      key: 'resourceCode',
      dataIndex: 'resourceCode',
      width:"12%"
    }, {
      title: '资源名称',
      key: 'resourceName',
      dataIndex: 'resourceName',
       width:"18%"
    }, {
      title: '提供方',
      key: 'deptName',
      dataIndex: 'deptName',
      width:"18%",
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
      title: '审批时间',
      key: 'approveTime',
      dataIndex: 'approveTime',
       width:"15%"
    },{
      title: '审批意见',
      key: 'approveAction',
      dataIndex: 'approveAction',
       width:"10%",
       render:(text)=>{
       		return text === 'agree'?"同意":"不同意"
       }
    },{
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      render: (text, record) => (
        <div>
          <a style={{fontSize: 14}}
            onClick={()=>{ handleCheckView(record) }}
          >查看</a>&nbsp;&nbsp;
          <a style={{fontSize: 14}}
            onClick={()=>{ handleCheckHistory(record) }}
          >审批历史</a>
        </div>
      )},
  ];

  	//查看modal
   const columns2 = [
    {
      title: '序号',
      key: 'key',
      dataIndex: 'key',
      width:"5%"
    },
    {
      title: '资源状态',
      key: 'currentStatus',
      dataIndex: 'currentStatus',
      width:"14%"
    }, {
      title: '审批后状态',
      key: 'nextStatus',
      dataIndex: 'nextStatus',
       width:"14%"
    }, {
      title: '审批人',
      key: 'approverName',
      dataIndex: 'approverName',
      width:"15%"
    }, {
      title: '审批时间',
      key: 'approveTime',
      dataIndex: 'approveTime',
      width:"18%"
    }, {
      title: '审批动作',
      key: 'approveAction',
      dataIndex: 'approveAction',
      width:"10%",
       render:(text)=>{
       		return text === 'agree'?"同意":"不同意"
       }
    },
    {
      title: '审批意见',
      key: 'suggestion',
      dataIndex: 'suggestion'
    },
  ];


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

  //查看按钮
  const handleCheckView = (record)=>{
    dispatch({
      type:"checkview/getEditResource",
      payload:{id:record.id}
    })
  }


  //审批历史modal
  const handleCheckHistory = (record)=>{
    dispatch({
    	type:"releaseApprovedModel/getCheck",
    	payload:{id:record.id},
      selectName:record.resourceName
 	  })
  }

  const handleCheckHide = ()=>{
    dispatch({
      type:"releaseApprovedModel/save",
      payload:{
        CheckHistoryShow:false
      }
    })
  }



  const {name,code,deptName,deptCode} = query;

  return(
    <div style={{margin: 20}}>
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
          {/* <Col span={8} style={{ display:'none'}} >
             <FormItem
              label="注册时间"
               {...formItemLayout1}
            >
              {getFieldDecorator('time')(
                <RangePicker showTime format="YYYY-MM-DD HH:mm:ss"  />
              )}
            </FormItem>
          </Col> */}
          <Col span={16} className="search_btn">
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

         <Modal
          visible={CheckHistoryShow}
          title={`审批历史(${selectName})`}
          onOk={handleCheckHide}
          onCancel={handleCheckHide}
          footer={[<Button type="primary" key="checkModal"  onClick={handleCheckHide}>确定</Button>]}
          width={1000}
        >
           <TableList 
            rowKey='__index'
            columns={columns2}
            dataSource={datasource2}
             pagination={false}
             scroll={{ y: 300 }}
          />
          </Modal>
          <CheckView />
    </div> 
  )
}

export default connect(({ releaseApprovedModel })=>({ releaseApprovedModel }))(withRouter(Form.create()(index)));