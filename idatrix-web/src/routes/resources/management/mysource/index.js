import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button,Tooltip,Icon,Select } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter,hashHistory } from 'react-router';
import Modal from 'components/Modal';
import CheckView from '../../common/CheckView/index';
import { canSubmitArgs } from '../../constants';
import FileUpload from 'components/FileUpload/FileUpload.js';
import { API_BASE_CATALOG } from '../../../../constants';
import { sureConfirm,downloadFile } from "utils/utils";
const FormItem = Form.Item;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

const index = ({form,dispatch,MySourceModel,location,router})=>{

  const {getFieldDecorator} = form;
  const {total,datasource,selectedRowKeys,canSubmit,resourceName,CheckHistoryShow,HistoryChangeShow,datasource1,datasource2,loading,selectedRows,InsterShow} = MySourceModel;
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
      width:"10%",
      render:(text,record)=>{
        return `${text}(${record.deptCode})`
      }
    }, {
      title: '状态',
      key: 'status',
      dataIndex: 'status',
       width:"10%",
    }, {
      title: '审批人',
      key: 'approverName',
      dataIndex: 'approverName',
       width:"10%",
       render:(text,record)=>{
          return (<span>{text?text:""}</span>)
       }
    },{
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '200px',
      render: (text, record) => (
        <div>
          <a style={{fontSize: 14}}
            onClick={()=>{ handleCheckView(record) }}
          >查看</a>
          {
            canSubmitArgs.includes(record.status)?(
               <a style={{fontSize: 14,marginLeft:"10px"}}
                onClick={()=>{ handleEditView(record) }}
              >编辑</a> 
            ):null
          }
          <a style={{fontSize: 14,marginLeft:"10px"}}
            onClick={()=>{handleHistoryChange(record)}}
          >变更历史</a>
          <a style={{fontSize: 14,marginLeft:"10px"}}
            onClick={()=>{handleCheckHistory(record)}}
          >审批历史</a>
        </div>
      )},
  ];


   const columns1 = [
    {
      title: '序号',
      key: 'key',
      dataIndex: 'key',
      width:"5%"
    },
    {
      title: '操作',
      key: 'actionName',
      dataIndex: 'actionName',
      width:"20%"
    }, {
      title: '操作人',
      key: 'operator',
      dataIndex: 'operator',
       width:"20%"
    }, {
      title: '操作时间',
      key: 'operatorTime',
      dataIndex: 'operatorTime',
      width:"50%"
    },
  ];

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
      width:"12%"
    }, {
      title: '审批后状态',
      key: 'nextStatus',
      dataIndex: 'nextStatus',
       width:"12%"
    }, {
      title: '审批人',
      key: 'approverName',
      dataIndex: 'approverName',
      width:"12%"
    }, {
      title: '审批时间',
      key: 'approveTime',
      dataIndex: 'approveTime',
      width:"20%"
    }, {
      title: '审批动作',
      key: 'approveAction',
      dataIndex: 'approveAction',
      width:"12%",
      render:(text)=>(
        <span>{text==="agree"?"同意":"不同意"}</span>
      )
    },
    {
      title: '审批意见',
      key: 'suggestion',
      dataIndex: 'suggestion',
      width:"27%"
    },
  ];

  //查看按钮
  const handleCheckView = (record)=>{
    dispatch({
      type:"checkview/getEditResource",
      payload:{id:record.id}
    })
  }

  //查看按钮
  const handleEditView = (record)=>{
    hashHistory.push("/resources/management/mysource/edit?id="+record.id);
  }

  //变更历史
  const handleHistoryChange = (record)=>{
    dispatch({
      type:"MySourceModel/getHistory",
      payload:{
        id:record.id
      },
      resourceName:record.resourceName
    })
  }

  const handleHistoryClick = ()=>{
    dispatch({
      type:"MySourceModel/save",
      payload:{
        HistoryChangeShow:false
      }
    })
  }


  //审批历史
  const handleCheckHistory = (record)=>{
     dispatch({
        type:"MySourceModel/getCheck",
        payload:{
           id:record.id
        },
        resourceName:record.resourceName
      })
  }

  const handleCheckClick = ()=>{
    dispatch({
      type:"MySourceModel/save",
      payload:{
        CheckHistoryShow:false
      }
    })
  }


  //新增按钮
  const handleClick = ()=>{
    hashHistory.push("/resources/management/mysource/new");
   /* dispatch({
      type:"checkview/getEditResource",
      payload:{
        InsterShow:true
      }
    })
*/
  };

  const handleSearch = ()=>{

    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      for(let index of Object.keys(values)){
        if(values[index]){
            if(index === "status" && values[index] === "all"){
                delete query[index]
            }else{
                 query[index] = values[index]
            }
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
  const handleDelete = ()=>{
    if(selectedRows.length === 0) return;
    
    sureConfirm({
      title: '确定删除页面选中数据吗?',
    },(bool)=>{
      if(bool){
        let args = [];
        for(let index of selectedRows){
            args.push(index.id)
        }
        dispatch({type:"MySourceModel/deleteInfo",payload:{id:args.join()}})
      }
    })
  };

  const onChangeAllSelect = (e,record)=>{
    let canSubmit = true;

    if(record.length>0){
      for(let index of record){
        if(!canSubmitArgs.includes(index.status)){
            canSubmit = false;
        }
      }
    }else{
      canSubmit = false;
    }
    dispatch({type:"MySourceModel/save",payload:{selectedRowKeys:e,selectedRows:record,canSubmit}})
  };

  //提交注册
  const handleRegister = ()=>{
    if(selectedRows.length === 0) return;
    
    sureConfirm({
      title: '确定提交审核页面选中数据吗?',
    },(bool)=>{
      if(bool){
        let args = [];
        for(let index of selectedRows){
            args.push(index.id)
        }
        dispatch({type:"MySourceModel/getSubmitInfo",payload:{ids:args.join()}})
      }
    });
  };

  const {name,code,deptName,deptCode,status} = query;

  const fileUploadProps = {
    fileName:"file",
    uploadUrl: `${API_BASE_CATALOG}/resource/batchImport`,
    multiple:false,
    uploadBtn:(fuc)=>{
        return (
          <Button type="primary" onClick={fuc} style={{ marginLeft: 8 }} >批量新增</Button>
        )
    },
    title:"批量新增",
    handleCallback:(fileList)=>{
      const { status,response } = fileList[0];
      console.log(fileList[0],"fileList[0]=====");
      if(status === "done" && response.code === "200" ){
          dispatch({
            type:"MySourceModel/fileResourceImport",
            payload:{
              fileName:response.data.fileName
            }
          })
      }

    }
  }

  const downloadExcel = () => {
    downloadFile('files/excel-template/政务信息资源批量导入模板.xlsx');
  };

  return(
    <div style={{ margin: 20 }}>
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
            <FormItem label={"资源代码"}  {...formItemLayout}>
              {getFieldDecorator("code",{
                initialValue:code?code:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8} style={{ display:'block'}}>
            <FormItem label={"状态"}  {...formItemLayout}>
              {getFieldDecorator("status",{
                initialValue:status?status:"all",
              })(
                <Select>
                    <Option value="all" key="all">全部</Option>
                    <Option value="1" key="draft">草稿</Option>
                    <Option value="3" key="wait_update">退回修改</Option>
                    <Option value="4" key="wait_reg_approve">待注册审批</Option>
                    <Option value="6" key="wait_pub_approve">待发布审批</Option>
                    <Option value="7" key="pub_success">已发布</Option>
                    <Option value="8" key="recall">下架</Option>
                </Select>
              )}
            </FormItem>
          </Col>
          <Col span={8} style={{ display:'block'}}>
            <FormItem label={"提供方名称"}  {...formItemLayout}>
              {getFieldDecorator("deptName",{
                initialValue:deptName?deptName:"",
              })(
                <Input  />
              )}
            </FormItem>
          </Col>
          <Col span={8} style={{ display:'block'}}>
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
          <Col span={24} className="search_btn">
            <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
            <Button type="primary" htmlType="submit" onClick={handleClick}  style={{ marginLeft: 8 }}>新增</Button>
            <FileUpload {...fileUploadProps}  />
            <Button type="primary" htmlType="submit" onClick={downloadExcel} style={{ marginLeft: 8 }}>下载模板</Button>
            <Button type="primary" htmlType="submit" disabled={!canSubmit} onClick={handleDelete} style={{ marginLeft: 8 }}>删除</Button>
            <Button type="primary" htmlType="submit" disabled={!canSubmit} onClick={handleRegister} style={{ marginLeft: 8 }}>提交审核</Button>
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

        <Modal
          visible={HistoryChangeShow}
          title={`变更历史(${resourceName})`}
          onOk={handleHistoryClick}
          onCancel={handleHistoryClick}
          footer={[<Button type="primary" key="historyModal" onClick={handleHistoryClick}>确定</Button>]}
          width={600}
        >
          <TableList
            rowKey='__index'
            columns={columns1}
            dataSource={datasource1}
            pagination={false}
            scroll={{ y: 300 }}
          />
        </Modal>
        <Modal
          visible={CheckHistoryShow}
          title={`审批历史(${resourceName})`}
          onOk={handleCheckClick}
          onCancel={handleCheckClick}
          footer={[<Button type="primary" key="checkModal"  onClick={handleCheckClick}>确定</Button>]}
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
        <CheckView  canDownload={true} />
    </div> 
  )
}

export default connect(({ MySourceModel })=>({ MySourceModel }))(withRouter(Form.create()(index)));