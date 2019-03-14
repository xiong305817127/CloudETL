import React from 'react';
import { Form,Row,Col,Input,Button,Icon,Upload,message,Card,Popconfirm,Tooltip  } from 'antd';
import { connect } from 'dva';
import { withRouter,hashHistory } from 'react-router';
import { analysisCsvFile} from 'services/gather1';
import TableList from 'components/TableList';
import {API_BASE_QUALITY} from 'constants';
import {downloadFile,deepCopy } from 'utils/utils';
import Empower from 'components/Empower';
const FormItem = Form.Item;
/*const { TextArea } = Input;*/

class NewIndex extends React.Component{
  constructor(){
    super();
  }

  state = {
     fileList:[],  //导入数据的存放
     disabledlist:[],//禁用输入框选项
     disabled:"hide",  //禁用输入框选项
     selectedRowKeys:[],  //表格复选框值
     selectedRow:[],
     dicid:[],  //导入的数据
     filstArr:[],
     fileStuat:"hide",
  }

  columns= [{
      title: '标准值',
      key: 'stdVal1',
      dataIndex: 'stdVal1',
      width:"10%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`stdVal1.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`stdVal1`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值1',
      key: 'simVal2',
      dataIndex: 'simVal2',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal2.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1}  onChange={value => { this.modifyField(`simVal2`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值2',
      key: 'simVal3',
      dataIndex: 'simVal3',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal3.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1}  onChange={value => { this.modifyField(`simVal3`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值3',
      key: 'simVal4',
      dataIndex: 'simVal4',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal4.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal4`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值4',
      key: 'simVal5',
      dataIndex: 'simVal5',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal5.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal5`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值5',
      key: 'simVal6',
      dataIndex: 'simVal6',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal6.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal6`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值6',
      key: 'simVal7',
      dataIndex: 'simVal7',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal7.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal7`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值7',
      key: 'simVal8',
      dataIndex: 'simVal8',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal8.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal8`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值8',
      key: 'simVal9',
      dataIndex: 'simVal9',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal9.${record.id}`, {
              initialValue: text,
            })(
              <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal9`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }, {
      title: '参考值9',
      key: 'simVal10',
      dataIndex: 'simVal10',
      width:"9%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`simVal10.${record.id}`, {
              initialValue: text,
            })(
             <Input disabled={this.state.disabledlist.indexOf(record.id) == -1} onChange={value => { this.modifyField(`simVal10`, value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    },{
      title: '操作',
      key: 'oprater',
      dataIndex: 'oprater',
      width: '10%',
      render: (text, record, index) => {
        const { renterId } = this.props.account;
        const {dictdata} = this.props.DataDictionaryEditModel;
        console.log(renterId,"renterId",dictdata);
          return(
               <Row>
                 <Empower api="/dictDataList/diceAdd.do"  disabled={renterId !== dictdata}>
                  <a> <Col span={7} onClick={this.handleCheckView(record.id)}>编辑</Col></a>
                  </Empower>
                  <Empower api="/dictDataList/diceAdd.do" disabled={this.state.disabledlist.indexOf(record.id) == -1||renterId !== dictdata}>
                  <a disabled={this.state.disabledlist.indexOf(record.id) == -1||renterId !== dictdata}> 
                     <Col span={7} onClick={()=>{ this.handleChecksibmit(record.id)}} >保存</Col>
                  </a>
                  </Empower>
                  <Empower api="/dictDataList/delect.do" disabled={renterId !== dictdata}>
                    <Popconfirm title="确认删除？" 
                     onConfirm={() => { this.handleCheckDelect(record) }}  onCancel={() => { this.cancel }} okText="确认" cancelText="取消">
                      <a>
                        <Tooltip title="删除" >
                          删除&nbsp;&nbsp;&nbsp;&nbsp;
                      </Tooltip>
                      </a>
                    </Popconfirm>
                    </Empower>
               </Row>
               
           );
        }
      }
    ]
   confirm=(e)=> {
      console.log(e);
      message.success('Click on Yes');
    }
    
   cancel=(e)=> {
      console.log(e);
      message.error('Click on No');
    }
    

    /**
     * 删除单行数据
     */
    handleCheckDelect=(did)=>{
      const {dispatch} = this.props;
      const {id} = this.props.DataDictionaryEditModel;
      dispatch({ type: "DataDictionaryEditModel/deletedictData", payload: {ids:[did.id],dictId:id}})  
    }


      // 修改输入框字段
  modifyField = (keyOfCol, e, record)=> {
     const { dispatch } = this.props;
     const { datasource } = this.props.DataDictionaryEditModel;
     /**
      * 校验选择的id与数据中的某一个id是否相同
      * 如果相同则修改当前输入的值
      */
    const data = deepCopy(datasource).map(row => {
      if (row.key === record.key) {
        row[keyOfCol] = e.target.value;
      }
      return row;
    });
     dispatch({ type: "DataDictionaryEditModel/setMetaId", payload: { arrlist:datasource} });
  }

     /**
      * 点击编辑字段
      * 校验为-1输入框不可输入
      * 或者为hide不可输入
      */
    handleCheckView=(redid)=>{
      return ()=>{
        this.setState({
           disabledlist: Array.from((new Set(this.state.disabledlist)).add(redid))
        })
      }
    }



    /**
     * 点击保存字段接口
     * 把输入框的值都拼装成一个字符串
     * 并且去掉为null空字符的数据
     */
    handleChecksibmit=(rid)=>{
       const {form,dispatch,DataDictionaryEditModel,dataDictionModel} = this.props;
       const { diceName } = dataDictionModel;
       const {id} = DataDictionaryEditModel;
       form.validateFields((err, values) => {
        console.log(values.stdVal1,"values",diceName);
          if (!err) {
          }
          for(let index in values){
             let list = values.simVal2[rid]+","+
                        values.simVal3[rid]+","+
                        values.simVal4[rid]+","+
                        values.simVal5[rid]+","+
                        values.simVal6[rid]+","+
                        values.simVal7[rid]+","+
                        values.simVal8[rid]+","+
                        values.simVal9[rid]+","+
                        values.simVal10[rid];
                      var arrNull = new RegExp(null,'g');  //去除表格空数据
                      var str = list.replace(arrNull,"")
                       let arr ={};
                       arr.id=rid;
                       arr.dictId=id;
                       arr.stdVal1= values.stdVal1[rid]; 
                       arr.valueArr=str;
                       //点击保存调取接口
                      dispatch({ type: "DataDictionaryEditModel/GetdictData", payload: { ...arr }})  
                       this.setState({
                             disabledlist: Array.from((new Set(this.state.disabledlist = "-1")))
                          })
              }
       })
       
    }

  render(){
    const {form,dispatch,DataDictionaryEditModel,location,router,dataDictionModel} = this.props;
    const {filstArr,dicid,fileStuat}=this.state;
    const {getFieldDecorator} = form;
    const {total,datasource,loading,id,	errorMessage, successMessage,dictdata} = DataDictionaryEditModel;
    const { diceName } = dataDictionModel;
    const { renterId } = this.props.account;
   const { query } = location;

   /**
    * 单独保存修改字典名称
    *  保存成功跳转到首页
    * */
   const goGetupdate=(e)=>{
        e.preventDefault();
        form.validateFields((err, values) => {
          if (!err) {
            let arr ={};
               arr.id=id;
               arr.dictName=values.dictNamevalue;
               //点击保存调取接口
            dispatch({ type: "DataDictionaryEditModel/Getupdate", payload: { ...arr }})  
          }
      });
   }

   /**
    * 删除数据
    */
   const goGetselect=(e)=>{
    dispatch({ type: "DataDictionaryEditModel/deletedictData", payload: {ids:filstArr,dictId:id}})  
      this.setState({
        fileStuat:"hide"
      })
   }

  const formItemLayout = {
    labelCol: {span:6},
    wrapperCol:{span:10},
  }
  const formItemLayout1 = {
    labelCol: {span:10},
    wrapperCol:{span:13},
  }

   const formItemLayout2 = {
    labelCol: {span:2},
    wrapperCol:{span:6},
  }

   const formItemLayout3 = {
    labelCol: {span:2},
    wrapperCol:{span:6},
  }
  //点击取消返回
  const goBack=()=>{
     hashHistory.goBack();
  }

  //点击导出文档
 const download=(e)=>{
     downloadFile('files/excel-template/数据字典导出文档.xlsx');
     /* downloadFile(`${API_BASE_QUALITY}/analysis/analysisCsvFile/`+id+".do");*/
 }
   /**
    * 点击导入模板 
    */
    const onChange=(info)=> {
      let arge=[];
      let arge1=[];
        let fileList = info.fileList;
            fileList = fileList.slice(-2);
            fileList = fileList.map((file) => {
              if (file.response) {
                file.url = file.response.url;
              }
              return file;
            });
            fileList = fileList.filter((file) => {
              if (file.response) {
                return file.response.status === 'success';
              }
              return true;
            });

            this.setState({ fileList });
            console.log(info.file.response,"info.file.response");
           let filst =info.file.response.data===null?info.file.response:info.file.response.data.successIds;
             for(let index of filst){
              arge.push({
                id:index
              })
              var newstr=JSON.stringify(index);
              for(let key in arge){
                arge1.push(newstr[key]);
              }
             }
            //判断success为ture就是导入成功
            //并且刷新表单
        if(info.fileList[0].response.success=== true){
            if (info.file.status === 'done') {
              message.success(info.fileList[0].response.message);
              this.setState({
                  fileStuat:"show",
              })
            } else if (info.file.status === 'error') {
              message.error("上传失败");
            }
          }
        // }else{
        //    message.error(info.file.response.message);
        // }
        dispatch({ type: "DataDictionaryEditModel/save", payload: { 
                     errorMessage:info.file.response.data.errorMessage,
                     successMessage:info.file.response.data.successMessage, }})  
        hashHistory.push(`/gather/dataDictionary/edit/${id}`);

        this.setState({
          dicid:arge,
          filstArr:filst
       })
      }

      const arr=(record,text)=>{
        return dicid.some(val=>val.id === record.id) ? "editable-row-test": "editable-row"; 
      }
      const goGetinstart=(text,record)=>{
        this.setState({
          dicid:[],
          fileStuat:"hide"
       })
        hashHistory.push(`/gather/dataDictionary/edit/`+id);
      }
  
  //点击导入文档
  //调取接口，传入参数
  const props = {
      action: API_BASE_QUALITY+'/analysis/dictData.do',
       data:{  dictId:id, },
       onChange: onChange,
    };
 //按条件查询内容
   const getClickList=()=>{
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

 const {value,url } = query;
  return(
      <div style={{padding: "20px"}}>

       <Row gutter={24}>
              <Col span={3} style={{ display:'block'}}>
                  <FormItem {...formItemLayout2}>
                  <Empower api="/dictDataList/fileUrl.do" disabled={renterId !== dictdata}>
                      <Upload {...props} fileList={this.state.fileList} >
                            <Button disabled={renterId !== dictdata}>
                              <Icon type="upload" /> 导入
                            </Button>
                          </Upload>
                    </Empower>
                    </FormItem>
                 
              </Col>
              <Col span={2} style={{ display:'block'}}>
                  <FormItem {...formItemLayout2}>
                      <Button type="primary" onClick={download}>下载模板</Button>
                  </FormItem>
              </Col>
             
         </Row>
        
<Card style={{border: "1px solid #e8e8e8"}}>
   <Form >  
    {
          fileStuat ==="show" ? (
            <div style={{border: '1px #ccc solid',marginBottom:'19px'}}>
                 <p style={{marginLeft:'5%'}}>{successMessage}</p>
                 <p style={{marginLeft:'5%'}}>{errorMessage}</p>
                 <Button style={{marginTop:"-10px",marginLeft:'5%',marginBottom:'5px'}} type="primary" onClick={goGetinstart}>保存数据</Button>
                 <Button style={{marginTop:"-10px",marginLeft:'5%',marginBottom:'5px'}} type="primary" onClick={goGetselect}>移除数据</Button>
            </div>
          ):null
     }
   
       <Row>
              <Col span={6} offset={10} style={{ display:'block',marginTop:"-11px",marginBottom:"-11px"}}>
                <FormItem label={"字典名称"} {...formItemLayout}>
                  {getFieldDecorator("dictNamevalue",{
                    initialValue:diceName?diceName:"",
                  })(
                    <Input  disabled={renterId !== dictdata}/>
                  )}
                </FormItem>
              </Col>
              <Empower api="/dictDataList/diceBaocun.do" disabled={renterId !== dictdata}>
                <Button  style={{float:"right",marginTop:"-10px"}} type="primary" onClick={goGetupdate}>保存字典名称</Button>
              </Empower>
        </Row>
   </Form>

          <TableList 
            showIndex
            loading={loading}
            rowKey='__index'
            columns={this.columns}
            dataSource={datasource}
            pagination={{total: total}}
            rowClassName={arr}
             />
     
    </Card >
        <Button style={{float:"right",margin:"10px"}} type="primary" onClick={goBack}>返回</Button>
       
       
 </div>
  )
  }
}


export default connect(({ DataDictionaryEditModel,dataDictionModel,account })=>({ DataDictionaryEditModel,dataDictionModel,account }))(withRouter(Form.create()(NewIndex)));