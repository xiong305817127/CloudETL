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
      if(argument){
        let count = 0;
        for(let index of argument){
         data.push({
          "key":count,
          "argument":index
        });
        count++;
     };
    }
      if(parameters){
        let count = 0;
        for(let index of parameters){

          data1.push({
            "key":count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource: data,
        dataSource1: data1,
        updateStatus:"isUpdate",
        serverList:[],
        jobList:[],
        detailsList:[]
      }
    }
  };
  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getServerList,transname,text,getJobList } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getServerList(data => {
      this.setState({
        serverList:data
      })
    });
    getJobList(data =>{
      this.setState({
        jobList:data
      })
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
      if(values.paramsFromPrevious === "Local"){
        values.paramsFromPrevious = true;
      }else{
        values.paramsFromPrevious = false;
      }
      let sendFields = [];
      let sendFields1 = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
           let args =["argument"];
           let args1 =[];
            args1 = formatTable(this.refs.editTable.state.dataSource,args);
          for(let index of args1){
            sendFields.push(index["argument"]);
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

  columns = [{title: '参数',dataIndex: 'argument',key:'argument',editable:true,width:"100%"}];
  columns1 = [{title: '命名参数',dataIndex: 'parameters',key:'parameters',editable:true,width:"33%"},
                {title: '流列名',dataIndex: 'parameterFieldNames',key:'parameterFieldNames',editable:true,width:"33%"},
                {title: '值',dataIndex: 'parameterValues',key:'parameterValues',editable:true,width:"33%"}];

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
      "parameterValues": ""
    };
    this.refs.editTable1.handleAdd(data);
  }
  handleDeleteFields1(){
    this.refs.editTable1.handleDelete();
  }

  handleFocus(){
  const { getJobDetails,transname,text,panel } = this.props.model;
    const { getFieldValue } = this.props.form;

    if(getFieldValue("jobname")){
          let obj = {};
           obj.jobName = transname;
           obj.entryName = text;
           obj.detailType = panel;
            obj.detailParam = {
              flag:"getParameters",
              jobName:getFieldValue("jobname")
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
                     "parameterValues": index[2],
                   })
                }
              this.refs.editTable1.updateTable(args,count);
            })
     }
  }
    formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    formItemLayout1 = {
      labelCol: { span: 5 },
      wrapperCol: { span: 12 },
    };

      showModel(status){
            if(status === "Local"){
                  return(
                      <div style={{textAlign:"center",lineHeight:"10"}}>此调度将在云化数据集成系统服务器上运行。</div>
                  )
            }else{
             const { getFieldDecorator } = this.props.form;
             const { config } = this.props.model;
             const { serverList } = this.state;
                return(
                  <div>
                    <div style={{margintop:"4%"}}>
                        <FormItem label="服务器"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
                            {getFieldDecorator('remoteSlaveServerName', {
                              initialValue: config.remoteSlaveServerName,
                            })(
                           <Select >
                             { serverList.map(index=>{
                                    return(
                                      <Option key={index.name}>{index.name}</Option>
                                    )
                               })
                            }
                          </Select>
                        )}
                    </FormItem>
                     <FormItem label=""  {...this.formItemLayout} style={{marginBottom:"8px",marginLeft:"20%"}}>
                        {getFieldDecorator('waitingToFinish', {
                           valuePropName: 'checked',
                          initialValue: config.waitingToFinish,
                        })(
                           <Checkbox>等待远程作业结束</Checkbox>
                        )}
                     </FormItem>
                     <FormItem label=""  {...this.formItemLayout} style={{marginBottom:"8px",marginLeft:"20%"}}>
                        {getFieldDecorator('passingExport', {
                           valuePropName: 'checked',
                          initialValue: config.passingExport,
                        })(
                           <Checkbox>将作业执行结果发送到服务器</Checkbox>
                        )}
                     </FormItem>
                      <FormItem label=""  {...this.formItemLayout} style={{marginBottom:"8px",marginLeft:"20%"}}>
                        {getFieldDecorator('expandingRemoteJob', {
                           valuePropName: 'checked',
                          initialValue: config.expandingRemoteJob,
                        })(
                           <Checkbox>启用对子作业和转换的监视</Checkbox>
                        )}
                     </FormItem>
                      <FormItem label=""  {...this.formItemLayout} style={{marginBottom:"8px",marginLeft:"20%"}}>
                        {getFieldDecorator('followingAbortRemotely', {
                           valuePropName: 'checked',
                          initialValue: config.followingAbortRemotely,
                        })(
                          <Checkbox>本地作业终止则远程作业也终止</Checkbox>
                        )}
                     </FormItem>
                 </div>
                  </div>
                )
            }
        }

 onChange = (e) => {
    this.setState({
     value:e.target.value
    });
  };

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckJobName,transname,nextStepNames,parallel } = this.props.model;

     const {jobList } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };


    const setDisabled = ()=>{
      if(getFieldValue("setLogfile") === undefined){
        return config.setLogfile;
      }else{
        if(getFieldValue("setLogfile")){
          return getFieldValue("setLogfile");
        }else{
          return false;
        }
      }
    };

    return (
       <Modal
        visible={visible}
        title="Job"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={600}
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}>
        <Form >
          <FormItem label="作业名称"  {...formItemLayout} >
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业名称' },
                {validator:handleCheckJobName,message: '作业名称已存在，请更改!' }]
            })(
               <Input min={0}  />
            )}
          </FormItem>
           {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'11rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
           <FormItem label="调度名称"  {...formItemLayout} >
            {getFieldDecorator('jobname', {
              initialValue: config.jobname
            })(
                <Select >
                    { jobList.map(index=>{
                            if(index !== transname){
                                return(
                                    <Option key={index}>{index}</Option>
                                )
                            }
                         })
                      }
                </Select>
            )}
        </FormItem>
        <Tabs type="card"  style={{margin:"0 5%"}}>
          <TabPane tab="选项" key="1">

            <FormItem   {...this.formItemLayout}>
              {getFieldDecorator('select', {
                initialValue:config.remoteSlaveServerName?"Server":"Local",
                onChange:this.onChange.bind(this)
              })(
                <Radio.Group   style={{ marginBottom: 8,marginTop:15,marginLeft:"35%"}}>
                  <Radio.Button value="Local">本地</Radio.Button>
                  <Radio.Button value="Server">服务器</Radio.Button>
                </Radio.Group>
              )}
            </FormItem>
              {
                this.showModel(getFieldValue("select"))
              }
              <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"20%"}}>
                        {getFieldDecorator('execPerRow', {
                           valuePropName: 'checked',
                          initialValue: config.execPerRow,
                        })(
                          <Checkbox>执行每一个输入行</Checkbox>
                        )}
                     </FormItem>
          </TabPane>
          <TabPane tab="设置日志" key="2">
                <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px", marginLeft:"25%"}}>
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
                      <Input disabled={setDisabled() ===false ? true:false}/>
                     )}
                </FormItem>
                <FormItem label="日志级别"  {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('logFileLevel', {
                    initialValue: config.logFileLevel,
                  })(
                    <Select style={{ width: 285 }} disabled={setDisabled() ===false ? true:false}>
                      <Option value="Nothing">没有日志</Option>
                      <Option value="Error">错误日志</Option>
                      <Option value="Minimal">最小日志</Option>
                      <Option value="Basic">基本日志</Option>
                      <Option value="Detailed">详细日志</Option>
                      <Option value="Debug">调试日志</Option>
                      <Option value="Rowlevel">行级日志(非常详细)</Option>
                    </Select>
                  )}
              </FormItem>
               <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('setAppendLogfile', {
                      valuePropName: 'checked',
                       initialValue: config.setAppendLogfile,
                     })(
                      <Checkbox disabled={setDisabled() ===false ? true:false}>添加到日志文件尾</Checkbox>
                     )}
                </FormItem>
                 <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('createParentFolder', {
                      valuePropName: 'checked',
                       initialValue: config.createParentFolder
                     })(
                      <Checkbox disabled={setDisabled() === false ? true:false}>创建父文件夹</Checkbox>
                     )}
                </FormItem>
                 <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                    {getFieldDecorator('addDate', {
                      valuePropName: 'checked',
                       initialValue: config.addDate,
                     })(
                      <Checkbox disabled={setDisabled() ===false ? true:false}>日志文件包含日期？</Checkbox>
                     )}
                </FormItem>
                 <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
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
                  initialValue: config.argFromPrevious,
                })(
                  <Checkbox>复制上一步结果到位置参数</Checkbox>
                )}
              </FormItem>
                <Row style={{margin:"5px 0",width:"100%"}}  >
                  <Col span={12}>
                    <Button size={"small"} onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  </Col>
                  <Col span={12}>
                   <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                  </Col>
                </Row>
               <EditTable columns={this.columns} rowSelection={true} dataSource = {this.state.dataSource} size={"small"} tableStyle="editTableStyle5" ref="editTable"  count={6}/>
          </TabPane>
          <TabPane tab="命名参数" key="4">
                    <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px"}}>
                        {getFieldDecorator('paramsFromPrevious', {
                          valuePropName: 'checked',
                          initialValue: config.paramsFromPrevious,
                        })(
                           <Checkbox>复制上一步结果到命名参数</Checkbox>
                        )}
                     </FormItem>
                     <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px"}}>
                        {getFieldDecorator('passingAllParameters', {
                          valuePropName: 'checked',
                          initialValue: config.passingAllParameters,
                        })(
                           <Checkbox>将所有参数值下发到子作业</Checkbox>
                        )}
                     </FormItem>

                     <Row style={{margin:"5px 0",width:"100%"}}  >
                        <Col span={12}>
                          <Button size={"small"} onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                          <Button size={"small"} onClick={this.handleFocus.bind(this)}>获取字段</Button>
                        </Col>
                        <Col span={12}>
                          <Button style={{float:"right"}}  size={"small"} onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
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
