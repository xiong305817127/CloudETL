import React from 'react';
import { Form,Row,Col,Input,Button,Select,Switch,Popconfirm } from 'antd';
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter,hashHistory } from 'react-router';
import Modal from 'components/Modal';
const FormItem = Form.Item;
const { TextArea } = Input;
const Option = Select.Option;
import Style from './index.less';
import Empower from 'components/Empower';

const index = ({form,dispatch,dataDictionModel,location,router,account})=>{
	const {getFieldDecorator,setFieldsValue} = form;
	const {total,datasource,loading,visibleShow,text,chenckTrue,listType} = dataDictionModel;
	const { renterId } = account;
	 const { query } = location;
	 
   const columns = [
    {
      title: '字典名称',
      key: 'dictName',
      dataIndex: 'dictName',
      width:"17%",
    }, {
      title: '创建人',
      key: 'creator',
      dataIndex: 'creator',
      width:"10%",
    }, {
      title: '创建日期',
      key: 'addTime',
      dataIndex: 'addTime',
       width:"15%"
    }, {
      title: '公有/私有',
      key: 'share',
      dataIndex: 'share',
			width:"7%",
			render:(record)=>{
        return(
          <span>{record===true?"公有":"私有"}</span>
        )
      }
    },{
      title: '修改日期',
      key: 'updateTime',
      dataIndex: 'updateTime',
      width:"15%",
    },
    ,{
      title: '生效日期',
      key: 'activeTime',
      dataIndex: 'activeTime',
      width:"15%",
    },
    {
      title: '操作人',
      key: 'modifier',
      dataIndex: 'modifier',
      width:"10%",
    },
   {
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '160px',
      render: (text, record, index) => {
				/**
				 * if判断看是否是什么状态去展示什么内容
				 * status状态为0的时候是未生效状态
				 * status状态为1的时候是生效状态
				 * status状态为2的时候是待更新状态
				 */
      	if(record.status === 0){
             return( <div>
											<div><a style={{fontSize: 14}}  onClick={()=>{ handleCheckView(record) }} >编辑</a>&nbsp;&nbsp;</div>	
											<Empower api="/dictDataList/No.do" disabled={renterId !== parseInt(record.renterId)}>
											<div>	<a style={{fontSize: 14}} disabled={renterId !== parseInt(record.renterId)}  onClick={()=>{ handleCheckStust(record) }} >未生效</a>&nbsp;&nbsp;</div>
											</Empower>
										</div> );
      	}else if(record.status === 1){
	         return( <div>
											<div><a style={{fontSize: 14}}  onClick={()=>{ handleCheckView(record) }} >编辑</a>&nbsp;&nbsp;</div>	
											<Empower api="/dictDataList/yes.do" disabled={renterId !== parseInt(record.renterId)}>
												<div><a style={{fontSize: 14}} disabled={renterId !== parseInt(record.renterId)} onClick={()=>{ handleCheckStust(record) }} >生效</a>&nbsp;&nbsp;</div>
											</Empower>
	               </div> );
      	}else{
           return( <div>
											<div><a style={{fontSize: 14}}  onClick={()=>{ handleCheckView(record) }} >编辑</a>&nbsp;&nbsp;</div>	
											<Empower api="/dictDataList/inst.do"  disabled={renterId !== parseInt(record.renterId)}>
												<div><a style={{fontSize: 14}} disabled={renterId !== parseInt(record.renterId)} onClick={()=>{ handleCheckStust(record) }} >待更新</a>&nbsp;&nbsp;</div>
											</Empower>
	               </div>);
      	  }
        }
     }];
		
		 /**
			* 点击生效状态所触发的方法事件
			* 需要传入id，status，
			* 所有的方法都需写在model里面，方便统一调用
			*/
    const handleCheckStust=(record)=>{
     	let arr={};
     	arr.id = record.id;
     	arr.status = record.status;
        dispatch({type:"dataDictionModel/GetdictDatastatus",payload:{...arr}});
     }

   /**
		* 按条件查询内容
		* 根据query中的路由选项去获取内容显示 
		* query.page = 1;查询结果默认就为第一页，
		* 如果为其他页数，
		* 查询不到所在的页数就会影响当前用户查询的内容
		*/
   const getClickList=()=>{
      form.validateFields((err, values) => {
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
   /**
		* 所有的方法都需写在model里面，方便统一调用
		* 点击编辑跳转页面,c传入字典名称dictName
		* 并查询字典列表接口查询展示
	 */
		const handleCheckView=(record)=>{
				hashHistory.push(`/gather/dataDictionary/edit/${record.id}`);
				dispatch({type:"dataDictionModel/getConfigId",payload:{diceName:record.dictName}});
		}
	/**
	 *输入框样式调整 
	 */
  const formItemLayout = {
    labelCol: {span:6},
    wrapperCol:{span:18},
  }
   //点击新建数据系统字典===弹出框
  const visibleShowClick = ()=>{
  	 dispatch({type:"dataDictionModel/save",payload:{visibleShow:true}})
  }
  //点击关闭新建数据系统字典===弹出框
  const visibleHideClick = ()=>{
  	dispatch({type:"dataDictionModel/save",payload:{visibleShow:false}})
  }
   /**
		* 新建只需要提交数据字典名称和描述
		* @param {点击提交新建系统字典内容} e 
		* 刷新列表
		*/
  const visibleSibmitClick =(e)=>{
		console.log(chenckTrue,"chenckTrue");
    e.preventDefault();
    form.validateFields((err, values) => {
	     if (!err) {
	      }
	      let arr={};
	      arr.dictDesc = values.dictDesc;
				arr.dictName = values.dictName;
				arr.share = chenckTrue;
	      dispatch({ type: "dataDictionModel/GetSibmitdictNew", payload: { ...arr }})  
	  })
  }
		/** 新建下拉获取列表
		 * 根据下拉的值获取字典名称和描述
		*/
  const handleChangeBase=(e,label)=>{
     dispatch({type:"dataDictionModel/getdictNewlist",payload:{id:e}})
      setFieldsValue({ dictName:label.props.children})
      setFieldsValue({dictDesc:label.props.value })
	}
	/**
	 * 新建时设置是否为公有还是私有
	 */
	const onChange=(checked)=> {
		dispatch({type:"dataDictionModel/getConfigId",payload:{chenckTrue:checked}})
	}

 const {dictNameValue } = query;
    return(
        <div style={{padding: "20px"}}>
           <Form className={Style.StyleTitle}>
              <Row gutter={24}>
									<Col span={12} offset={1}>
										<FormItem label={"字典名称"} {...formItemLayout}>
											{getFieldDecorator("dictNameValue",{
												initialValue:dictNameValue?dictNameValue:"",
											})(
												<Input />
											)}
										</FormItem>
									</Col>
									<Col span={3} offset={4} className="form-btn-style" style={{marginBottom:15}}>
										<Button style={{float:"right"}} type="primary" htmlType="submit" onClick={getClickList}>查询</Button>
									</Col>
									
									<Col span={2} className="form-btn-style" style={{marginBottom:15}}>
										<Empower api="/analysis/dict.do" >
												<Button style={{float:"right"}} type="primary" htmlType="submit" onClick={visibleShowClick}>新建</Button>
										</Empower>
									</Col>
							
		       </Row>

		       <Modal
		          visible={visibleShow}
		          title="新建数据字典"
		          onOk={visibleSibmitClick}
		          onCancel={visibleHideClick}
		          footer={[<Button type="primary" key="visibleSibmitClick" onClick={visibleSibmitClick}>确定</Button>,
		                   <Button type="primary" onClick={visibleHideClick}>取消</Button>]}
		          width={600}
		        >
		          <div>
							<Row gutter={24}>
			              <Col style={{ display:'block'}}>
			                <FormItem label={"设置权限公有/私有"} {...formItemLayout}>
			                  
			                    <Switch onChange={onChange} checkedChildren="公有" unCheckedChildren="私有"/>
			                 
			                </FormItem>
			              </Col>
			         </Row>
			         
               <Row gutter={24}>
			              <Col style={{ display:'block'}}>
			                <FormItem label={"字典名称"} {...formItemLayout}>
			                  {getFieldDecorator("dictName",{
			                  	initialValue:text.stdVal1,
			                     rules: [{
			                          required: true, message: '请输入字典名称!',
			                        }],
			                  })(
			                    <Input  placeholder="请输入字典名称"/>
			                  )}
			                </FormItem>
			              </Col>
			         </Row>
			         
							 {/**
							   <Row gutter={24}>
			              <Col style={{ display:'block'}}>
			                <FormItem label={"复制字典名称"} {...formItemLayout}>
			                  {getFieldDecorator("stdVal1",{
			                  	initialValue:text.stdVal1,
			                     })(
			                       <Select allowClear placeholder="请选择需要复制的字典名称" onChange={handleChangeBase} style={{ width: '100%' }}>
					                  {
					                    datasource.map((index) =>
					                      <Option key={index.id} value={index.dictName+"",index.id+"",index.dictDesc+""}>{index.dictName}</Option>)
					                  }
					                </Select>
			                  )}
			                </FormItem>
			              </Col>
			         </Row>
							*/}
			         <Row gutter={24}>
			              <Col style={{ display:'block'}}>
			                <FormItem label={"描述"} {...formItemLayout}>
			                  {getFieldDecorator("dictDesc",{
			                    initialValue:text.dictDesc,
			                  })(
			                    <TextArea rows={8} placeholder="请输入数据字典的详细描述" />
			                  )}
			                </FormItem>
			              </Col>
			         </Row>
			       
		          </div>
		        </Modal>
           </Form>

            <TableList 
	          showIndex
	          loading={loading}
	          rowKey='__index'
	          columns={columns}
	          dataSource={datasource}
	          pagination={{total: total}}
	           />
 
        </div>
    )
}

export default connect(({ dataDictionModel,DataDictionaryEditModel,account })=>({ dataDictionModel,DataDictionaryEditModel,account }))(withRouter(Form.create()(index)));