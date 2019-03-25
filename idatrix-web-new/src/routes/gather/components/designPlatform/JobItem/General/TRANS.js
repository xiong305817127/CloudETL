import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Tabs,Select,Checkbox,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
import EditTable from '../../../common/EditTable';

class JOB extends React.Component {
 constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
     const { argument,parameters} = props.model.config;
      let data = [];
      let data1 = [];
        let count = 0;
      if(argument){
          for(let index of argument){
           data.push({
            "key":count,
            "argument":index
          });
          count++;
       }
      }
     let counts = 0;
      if(parameters){
        for(let index of parameters){

          data1.push({
            "key":counts,
            ...index
          });
          counts++;
        }
      }

      this.state = {
        dataSource: data,
        dataSource1: data1,
        serverList:[],
        TransList:[]
      }
    }
  };

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getEngineList,transname,text,getTransList } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getEngineList(data => {
      this.setState({
        serverList:data
      })
    });
    getTransList(data =>{
      this.setState({
        TransList:data
      })
    });
  };

 hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

   handleCreate(e){
    e.preventDefault();
      const {panel,description,transname,key,saveEntry,text,formatTable,config} = this.props.model;
      const { parameters,argument } = config;

    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
           let args =["argument"];
           let args1 =[];
          args1 = formatTable(this.refs.editTable.state.dataSource,args);
          for(let index of args1){
            sendFields.push(index["argument"])
          }
        }
      }else{
        if(sendFields){
          sendFields = argument
        }
      }
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let args = [ "parameters", "parameterFieldNames", "parameterValues"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,args);
          console.log(sendFields1,"sendFields");
        }
      }else{
        if(parameters){
          sendFields1 = parameters
        }
      }

      let obj = {};
      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.parallel= values.parallel;
      obj.entryParams = {
        ...values,
        argument:sendFields,
        parameters:sendFields1
      };


      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };
  dataSource = [{key:1, name:"",num:""},{key:2,name:"",num:""},{key:3,name:"",num:""},{key:4,name:"", num:""},{key:5,name:"",num:""}]
  columns = [{title: '值',dataIndex: 'argument',editable:true,width:"100%"}];

  dataSource1 = [{key:1, name:"",num:""},{key:2,name:"",num:""},{key:3,name:"",num:""},{key:4,name:"", num:""},{key:5,name:"",num:""}]
  columns1 = [{title: '命名参数',dataIndex: 'parameters',editable:true,width:"33%"},
                {title: '流列名',dataIndex: 'parameterFieldNames',editable:true,width:"33%"},
                {title: '值',dataIndex: 'parameterValues',editable:true,width:"33%"}];

 handleAdd(){
    const data = {
      "argument": ""
    };
    this.refs.editTable.handleAdd(data);
  }

  handleDeleteFields(){
    this.refs.editTable.handleDelete();
  }
  handleAdd1(){
    const data = {
      "parameters": "",
      "parameterFieldNames": "",
      "parameterValues": "",
    }
    this.refs.editTable1.handleAdd(data);
  }

  handleDeleteFields1(){
    this.refs.editTable1.handleDelete();
  }

 handleFocus(){
    const { getJobDetails,transname,text,panel } = this.props.model;
    const { getFieldValue } = this.props.form;
    if(getFieldValue("transname")){
          let obj = {};
           obj.jobName = transname;
           obj.entryName = text;
           obj.detailType = panel;
            obj.detailParam = {
              flag:"getParameters",
              transName:getFieldValue("transname")
            };
            getJobDetails(obj,data =>{
                let args = [];
                let count = 0;
                for(let index of data){
                    count++;
                   args.push({
                     "key":count,
                     "parameters": index[0],
                     "parameterFieldNames": index[1],
                     "parameterValues": index[2]
                   })
                }
              this.refs.editTable1.updateTable(args,count);
            })
        }
   }


  render() {
   const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;
     const { serverList,TransList } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };
  const setDisabled = ()=>{
      if(getFieldValue("setLogfile") === undefined){
        return config.setLogfile;
         //config.schedulerType
      }else{
        if(getFieldValue("setLogfile")){
          return getFieldValue("setLogfile");   //getFieldValue("schedulerType")
        }else{
          return false;
        }
      }
    };

    return (
       <Modal
        visible={visible}
        title="转换"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width="50%"
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >

          <FormItem label="作业项名称"  {...formItemLayout}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
               <Input />
            )}
          </FormItem>
         {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'13rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
           <FormItem label="转换"  {...formItemLayout}>
            {getFieldDecorator('transname', {
              initialValue: config.transname
            })(
               <Select>
                    { TransList.map(index=>{
                              return(
                                <Option key={index} value={index}>{index}</Option>
                              )
                         })
                      }
                </Select>
            )}

          </FormItem>

        <Tabs type="card" style={{margin:"0 5%"}}>
          <TabPane tab="选项" key="1">

                    <FormItem label="运行配置"  {...formItemLayout} >
                            {getFieldDecorator('runConfiguration', {
                              initialValue: config.runConfiguration,
                                rules: [{ whitespace:true, required: true, message: '请选择运行配置' }]
                            })(
                          <Select style={{ width: 200 }} >
                             { serverList.map(index=>{
                                    return(
                                      <Option key={index.name} value={index.name}>{index.name}</Option>
                                    )
                               })
                            }
                          </Select>
                        )}
                    </FormItem>

                     <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"23%"}}>
                        {getFieldDecorator('execPerRow', {
                        valuePropName: 'checked',
                          initialValue: config.execPerRow
                        })(
                           <Checkbox>执行每一个输入行</Checkbox>
                        )}
                     </FormItem>
                     <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"23%"}}>
                        {getFieldDecorator('clearResultRows', {
                        	valuePropName: 'checked',
                          initialValue: config.clearResultRows,
                        })(
                           <Checkbox>在执行前清除结果行列表？</Checkbox>
                        )}
                     </FormItem>
                     <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px", marginLeft:"23%"}}>
                        {getFieldDecorator('clearResultFiles', {
                        	valuePropName: 'checked',
                          initialValue: config.clearResultFiles,
                        })(
                           <Checkbox>在执行前清除结果文件列表？</Checkbox>
                        )}
                     </FormItem>
                      <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"23%"}}>
                        {getFieldDecorator('waitingToFinish', {
                        	valuePropName: 'checked',
                          initialValue: config.waitingToFinish,
                        })(
                           <Checkbox>等待远程转换执行结束</Checkbox>
                        )}
                     </FormItem>
                      <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"23%"}}>
                        {getFieldDecorator('followingAbortRemotely', {
                        	valuePropName: 'checked',
                          initialValue: config.followingAbortRemotely,
                        })(
                           <Checkbox>本地转换终止时远程转换也通知终止</Checkbox>
                        )}
                     </FormItem>

          </TabPane>
          <TabPane tab="设置日志" key="2">
                <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px", marginLeft: "25%"}}>
                    {getFieldDecorator('setLogfile', {
                    	valuePropName: 'checked',
                       initialValue: config.setLogfile,
                     })(
                      <Checkbox>指定日志文件</Checkbox>
                     )}
                </FormItem>
                <FormItem label="日志文件名"  {...formItemLayout} style={{marginBottom:"8px"}}>
                   {getFieldDecorator('logfile', {
                      initialValue: config.logfile,
                    })(
                      <Input disabled={setDisabled() ===false ? true:false}/>
                     )}
                </FormItem>
                 <FormItem label="日志文件后缀名"  {...formItemLayout} style={{marginBottom:"8px"}}>
                   {getFieldDecorator('logext', {
                      initialValue: config.logext,
                    })(
                      <Input  disabled={setDisabled() ===false ? true:false}/>
                     )}
                </FormItem>
                <FormItem label="日志级别"  {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('logFileLevel', {
                    initialValue: config.logFileLevel
                  })(
                    <Select style={{ width: 285 }} disabled={setDisabled() ===false ? true:false}>
                      <Option value="Nothing">没有日志</Option>
                      <Option value="Error">错误日志</Option>
                      <Option value="Minimal">最小日志</Option>
                      <Option value="Basic">基本日志</Option>
                      <Option value="Detailed">详细日志</Option>
                      <Option value="Debug">调试</Option>
                      <Option value="Rowlevel">行级日志(非常详细)</Option>
                    </Select>
                  )}
              </FormItem>
               <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft: "25%"}}>
                    {getFieldDecorator('setAppendLogfile', {
                    	valuePropName: 'checked',
                       initialValue: config.setAppendLogfile
                     })(
                      <Checkbox disabled={setDisabled() ===false ? true:false}>添加到日志文件尾</Checkbox>
                     )}
                </FormItem>
                 <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px", marginLeft: "25%"}}>
                    {getFieldDecorator('createParentFolder', {
                    	valuePropName: 'checked',
                       initialValue: config.createParentFolder,
                     })(
                      <Checkbox disabled={setDisabled() ===false ? true:false}>创建父文件夹</Checkbox>
                     )}
                </FormItem>
                 <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft: "25%"}}>
                    {getFieldDecorator('addDate', {
                    	valuePropName: 'checked',
                       initialValue: config.addDate,
                     })(
                      <Checkbox disabled={setDisabled() ===false ? true:false}>日志文件包含日期？</Checkbox>
                     )}
                </FormItem>
                 <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft: "25%"}}>
                    {getFieldDecorator('addTime', {
                    	valuePropName: 'checked',
                       initialValue: config.addTime,
                     })(
                      <Checkbox disabled={setDisabled() ===false ? true:false}>日志文件包含时间？</Checkbox>
                     )}
                </FormItem>
          </TabPane>
          <TabPane tab="参数" key="3">
              <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px"}}>
                {getFieldDecorator('argFromPrevious', {
                  valuePropName: 'checked',
                  initialValue: config.argFromPrevious
                })(
                  <Checkbox>复制上一步结果到位置参数</Checkbox>
                )}
              </FormItem>
               <Row style={{margin:"5px 0",width:"100%"}}  >
                  <Col span={12}>
                    <Button size={"small"} onClick={this.handleAdd.bind(this)}>添加参数</Button>
                  </Col>
                  <Col span={12}>
                  <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields.bind(this)} >删除参数</Button>
                  </Col>
              </Row>
               <EditTable columns={this.columns} dataSource = {this.state.dataSource} size={"small"}  ref="editTable"  count={6}/>
          </TabPane>
          <TabPane tab="命名参数" key="4">
                    <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px"}}>
                        {getFieldDecorator('paramsFromPrevious', {
                          valuePropName: 'checked',
                          initialValue: config.paramsFromPrevious
                        })(
                           <Checkbox>复制上一步结果到命名参数</Checkbox>
                        )}
                     </FormItem>
                     <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px"}}>
                        {getFieldDecorator('passingAllParameters', {
                          valuePropName: 'checked',
                          initialValue: config.passingAllParameters
                        })(
                           <Checkbox>将所有参数值下发到子作业</Checkbox>
                        )}
                     </FormItem>
                      <Row style={{margin:"5px 0",width:"100%"}}  >
                          <Col span={12}>
                              <Button size={"small"} onClick={this.handleAdd1.bind(this)}>添加参数</Button>
                              <Button size={"small"} onClick={this.handleFocus.bind(this)}>获取参数</Button>
                          </Col>
                          <Col span={12}>
                             <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields1.bind(this)} >删除参数</Button>
                          </Col>
                      </Row>
               <EditTable columns={this.columns1} dataSource={this.state.dataSource1} tableStyle="editTableStyle5" rowSelection={true} size={"small"}  ref="editTable1"  count={6}/>
          </TabPane>
        </Tabs>

        </Form>
      </Modal>
    );
  }
}
const JobForm = Form.create()(JOB);
export default connect()(JobForm);
