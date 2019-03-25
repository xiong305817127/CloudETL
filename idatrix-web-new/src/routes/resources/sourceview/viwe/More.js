import React from 'react';
import { Form,Row,Col,Input,DatePicker,Button,Ico,Select,Cascader,Tag   } from 'antd';
import { connect } from 'dva';
import styles from '../index.less';
import TableList from 'components/TableList';
import { withRouter } from 'react-router';
import CheckView from '../../common/CheckView/index';
import Subscription from '../../common/Subscription/index';
import baseInfo from "config/baseInfo.config.js";
const FormItem = Form.Item;

class index extends React.Component {

  
   columns = [
    {
      title: '资源分类',
      dataIndex: 'catalogName',
      key: 'catalogName',
    }, {
      title: '资源代码',
      dataIndex: 'resourceCode',
      key: 'resourceCode',
    }, {
      title: '资源名称',
      dataIndex: 'resourceName',
      key: 'resourceName',
    }, {
      title: '提供方',
      dataIndex: 'deptName',
      key: 'deptName',
    }, {
      title: '提供方代码',
      dataIndex: 'deptCode',
      key: 'deptCode',
    }, {
      title: '数据量',
      dataIndex: 'dataCount',
      key: 'dataCount',
      render:(text)=>{
        return(
          <span>{text?text:0}</span>
        )
      }
    }, {
      title: '上架时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: '200px',
    },{ 
      title: '操作',
      key: 'x12',
      render: (text,record) => {
        let hoRoor = record.subscribeFlag===1?"订阅":"" || record.subscribeFlag===2?"已订阅":"";
        return (
        <div>
          <a onClick={()=>this.handleClick(record)}>
            查看
          </a>&nbsp;&nbsp;

          {/* 此处方法调用去掉this，因为此处不是一个类 */}
          {/* edited by steven leo on 2018/09/22 */}
          <a disabled={record.subscribeFlag === 0  || record.subscribeFlag===2}  onClick={()=>this.handleSubscription(record)}>
          {hoRoor}
          </a>
        </div>)
      }
    }];


  //查看
   handleClick=(record)=>{
    const {dispatch}=this.props;
    
    dispatch({
      type:"checkview/getEditResource",
       payload:{id:record.id,statue:"hide",type:"count"}
    })
  }

  //订阅
   handleSubscription=(record)=>{
    const {dispatch,location}=this.props;
      dispatch({
        type:"subscriptionModal/getSubDetail",
        name:record.resourceName,
        payload:{resourceId:record.id}
      })
      dispatch({
        type: "indexModel/save",
        payload: {
          queryList:location.query
        }
      });
	}
	
	//异步加载资源目录
	loadDataType = (selectedOptions) => {
		const { dispatch } = this.props;
		const { resourcesList } = this.props.indexModel;

    const targetOption = selectedOptions[selectedOptions.length - 1];
		targetOption.loading = true;

		return new Promise((resolve)=>{
			dispatch({
				type:"indexModel/getResourcesFolder",
				resolve,targetOption,resourcesList,
				payload:{ id:targetOption.id }
			})
		})
  }


  render(){
  const {form,dispatch,indexModel,location,router}=this.props;
    const {getFieldDecorator} = form;
    const {total,datasource,loading,resourcesList} = indexModel;
     const { query } = location;

    /**
     * 将数据转为list显示
     * 使用ul+li
     * @param {object} record  
     */
    const showExpanded=(record)=>{
       if(record.highlight === undefined || record.highlight.length === 0){    //判断record.highlight参数是否为空
         return;           //如果为空返回一个空
       }else{
         return (   
          <ul>   //使用数组结构用ui li标签换行
            {
              record.highlight.map(v=>{      //根据参数遍历
                  const tempArr = v?v.split("<em>"):"";  //判断有没有有em标签
                      return (
                          <div>
                          {
                            tempArr !== "" && 
                              tempArr.map(val=>{
                              if(val.indexOf("</em>") === -1){
                                return (
                                <span>{val}</span>
                                )
                              }else{
                                  const tempArr1 = val.split("</em>");
                                  const [first, ...rest] = tempArr1;
                                  return (
                                      <span>
                                      <Tag style={{display:"inline-block"}} color="magenta">{first}</Tag>
                                      <span>{rest ? rest.join(): ""}</span>
                                      </span>
                                  )
                              }
                          })
                          }
                          </div>
                )
              })
            }
          </ul>
       )
      }
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
         let val = values.catalogId;
        for(let index of Object.keys(values)){
            if(index === "catalogId" ){
               if(values[index] && values[index].length>0){
                  query["catalogId"] = val[val.length - 1];
                }else{
                    delete query["catalogId"];
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
  
    const displayRender=(label,value)=> {
        return label[label.length - 1];
      }
       /*选择部门*/
     const onChange = (e, label) => {
          if (!e) return;
          if (e.length > 0) {
            let args = [];
            let a1 = [];
            let a2 = [];
            for (let index of label) {
              /*args.push(index.id);*/
              a1.push(index.parentFullCode);
              a2.push(index.resourceEncode);
            }
            let arr =a1[a1.length - 1];
            let arr1 =a2[a2.length - 1];
            let join = arr+arr1;
            dispatch({
              type: "indexModel/save",
              payload: {
                catalogId: args[args.length - 1],
                queryList:query
              }
            });
          }
      }
  
  
    const {name,code,catalogId,deptName,deptCode,keyword} = query;
  
    
    return(
      <div className="margin_20">
        <Form className="btn_std_group">
          <Row gutter={20}>
              <Col span={8} >
                <FormItem label={"资源名称 "} {...formItemLayout}>
                  {getFieldDecorator("name",{
                    initialValue:name?name:"",
                  })(
                    <Input  />
                  )}
                </FormItem>
              </Col>
              <Col span={8} >
                <FormItem label={"资源代码 "}  {...formItemLayout}>
                  {getFieldDecorator("code",{
                    initialValue:code?code:"",
                  })(
                    <Input  />
                  )}
                </FormItem>
              </Col>
                <Col span={8} >
                <FormItem label={"资源分类 "} {...formItemLayout}>
                  {getFieldDecorator("catalogId",{
                    initialValue:catalogId?catalogId:"",
                  })(
                    <Cascader placeholder="请选择资源分类" loadData={this.loadDataType} displayRender={displayRender} options={resourcesList} onChange={onChange} style={{ width: '100%' }} />
                  )}
                </FormItem>
              </Col>
              <Col span={8} >
                <FormItem label={"提供方名称"}  {...formItemLayout}>
                  {getFieldDecorator("deptName",{
                    initialValue:deptName?deptName:"",
                  })(
                    <Input  />
                  )}
                </FormItem>
              </Col>
              <Col span={8} >
                <FormItem label={"提供方代码"}  {...formItemLayout}>
                  {getFieldDecorator("deptCode",{
                    initialValue:deptCode?deptCode:"",
                  })(
                    <Input  />
                  )}
                </FormItem>
              </Col>
              {
                baseInfo.premit && baseInfo.premit.includes("yunyang")?(
                    <Col span={8} >
                      <FormItem label={"全文搜索"}  {...formItemLayout}>
                        {getFieldDecorator("keyword",{
                          initialValue:keyword?keyword:"",
                        })(
                          <Input  />
                        )}
                      </FormItem>
                    </Col>
                ):null
              }
              
              
              <Col span={24} className="search_btn">
                  <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
              </Col>
            </Row>
          </Form>
          <div style={{marginTop: 20}}>
            <TableList 
              showIndex
              loading={loading}
              rowKey='__index'
              columns={this.columns}
    
              dataSource={datasource}
              pagination={{total: total}}
              expandedRowRender={showExpanded}
              defaultExpandAllRows={true}
            />
          </div>
        <CheckView />
        <Subscription />
      </div> 
    )
  }
}

export default connect(({ indexModel })=>({ indexModel }))(withRouter(Form.create()(index)));