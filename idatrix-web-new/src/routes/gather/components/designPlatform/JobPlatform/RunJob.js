import React from 'react'
import { connect } from 'dva'
import { Form,Input,Radio,Select,Checkbox,Row,Col,Button,Tabs } from 'antd'
import Modal from "components/Modal.js";
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;
import EditTable from '../../common/EditTable';


class Index extends React.Component{

  componentWillReceiveProps(nextProps){
    const { visible,params } = nextProps.runjob;
    if(visible === true){
      this.updateTable(params);
    }
  }

  updateTable(params){
    let args = [];
    let i = 0;

    if(this.refs.editTable){
      if(params ){
        for(let index of Object.keys(params)){
          args.push({
            key:i++,
            name:index,
            defaultValue:params[index],
            value: params[index]
          })
        }
      }
      this.refs.editTable.updateTable(args,i);
    }
  };

  initFuc(that){
    const { params } = this.props.runjob;
    let args = [];
    let i = 0;

    if(params ) {
      for (let index of Object.keys(params)) {
        args.push({
          key: i++,
          name: index,
          defaultValue: params[index],
          value: params[index]
        })
      }
    }
    that.updateTable(args,i);
  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  handleAdd1 = ()=>{
    const data = {
      "name": "",
      "value": ""
    };
    this.refs.editTable1.handleAdd(data);
  };

  handleDeleteFields1 = ()=>{
    this.refs.editTable1.handleDelete();
  };



  Columns = [
    {
      title: '参数',
      dataIndex: 'name',
      key: 'name',
      width:"150px",
      editable:false
    },{
      title: '默认值',
      dataIndex: 'defaultValue',
      key: 'defaultValue',
      width:"150px",
      editable:false,
    },{
      title: '值',
      dataIndex: 'value',
      key: 'value',
      editable:true,
    }
  ];

  Columns1 = [
    {
      title: '变量',
      dataIndex: 'name',
      key: 'name',
      width:"50%",
      editable:true
    },{
      title: '值',
      dataIndex: 'value',
      key: 'value',
      editable:true
    }
  ];

  /*格式化表格*/
  formatTable (obj){
    let newObj = {};
    for(let index of obj){
      newObj[index.name]  = index.value;
    }
    return newObj;
  }

   handleSubmit(e){

    const { form,runjob,dispatch } = this.props;

    const {viewId,actionName,selectedRows,runModel,dataSource,owner} = runjob;

    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      if(values.execLocal === "execLocal"){
        values.execLocal = true;
        values.execRemote = false;
      }else{
        values.execLocal = false;
        values.execRemote = true;
      }

      let sendFields = {};
      let sendFields1 = {};
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          sendFields = this.formatTable(this.refs.editTable.state.dataSource)
        }
      }

      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          sendFields1 = this.formatTable(this.refs.editTable1.state.dataSource)
        }

        dispatch({
          type:'runjob/show',
          payload:{
            dataSource:this.refs.editTable1.state.dataSource
          }
        });
      }else{
        if(dataSource.length>0){
          sendFields1 = this.formatTable(dataSource);
        }
      }

      values.params = sendFields;
      values.variables = sendFields1;

      if(runModel === "batch" && selectedRows.length>0){
        dispatch({
          type:'runjob/batchRun',
          payload:{
            selectedRows:selectedRows.filter(val=>val.owner === owner),
            visible:false,
            owner: owner,
            configuration:{
              execLocal:"execLocal",
              ...values
            }
          }
        });
      }else if(runModel === "viewRun" ){
        dispatch({
          type:"controljobplatform/executeJob",
          payload:{
            actionName:actionName,
            obj:{
              name:actionName,
              configuration:{
                execLocal:"execLocal",
                ...values
              }
            }
          }
        });
        this.handleHide();
      }else{
        dispatch({
          type: "jobspace/initStep"
        });
        dispatch({
          type:"jobdebug/executeJob",
          payload:{
            viewId:viewId,
            actionName:actionName,
            obj:{
              name:actionName,
              configuration:{
                execLocal:"execLocal",
                ...values
              }
            }
          }
        });
        this.handleHide();
      }
    });
  };

   handleHide (){
     const { dispatch } = this.props;

    dispatch({
      type:'runjob/hide',
      visible:false
    });
  };

  getItemsName(){
     const { items } = this.props.runjob;
      let args = [];

      console.log(items);

      for(let index of items){
         args.push(index.text);
      }
      return args;
  }

    render(){

      const { form,runjob,dispatch } = this.props;
      const {getFieldDecorator} = form;
      const {visible,serverList,model,runModel,dataSource,items} = runjob;

      const formItemLayout = {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 },
      };
      const formItemLayout1 = {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 },
      };
      const  formItemLayout3= {
        wrapperCol: { span: 24 },
      };
      const formItemLayout4= {
        labelCol: { span: 8 },
        wrapperCol: { span: 16},
      };

      const  changeModel = ()=>{

        switch (model) {
          case "execRemote":
            return (
              <div>
                <FormItem  label="远程机器"  {...formItemLayout1} >
                  {getFieldDecorator('remoteServer',{
                    initialValue:"",
                    rules: [{ required: true, message: '请选择远程机器' }]
                  })(
                    <Select placeholder="请选择远程机器">
                      {
                        serverList.map((index)=>
                          <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                        )
                      }
                    </Select>
                  )}
                </FormItem>
                <Row>
                  <Col span={12}>
                    <FormItem style={{marginBottom:"10px",marginLeft:"20px"}}  {...formItemLayout1}>
                      {getFieldDecorator('passExport',{
                        valuePropName: 'checked',
                        initialValue: false
                      })(
                        <Checkbox >将导出的文件发送到远程服务器</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem style={{marginBottom:"10px",marginLeft:"20px"}}  {...formItemLayout1}>
                      {getFieldDecorator('expandingRemoteJob',{
                        valuePropName: 'checked',
                        initialValue: false
                      })(
                        <Checkbox >Expand remote job</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
              </div>
            );
          default :
            return false;
        }
      };

      const onChange = (e)=>{
        dispatch({
          type:'runjob/changeModel',
          model:e.target.value
        });
      };

      return(
        <Modal
          title="执行调度"
          wrapClassName="vertical-center-modal"
          visible={visible}
          onCancel={this.handleHide.bind(this)}
          width={650}
          footer={[
            <Button key="submit" type="primary" size="large" onClick={this.handleSubmit.bind(this)}>运行</Button>,
            <Button key="back" size="large"  onClick={this.handleHide.bind(this)}>取消</Button>,
        ]}
        >
          <Form >
            {  /* <FormItem  label="执行方式"  {...formItemLayout} style={{marginBottom:"10px"}}>
             {getFieldDecorator('execLocal', {
             initialValue:model
             })(
             <RadioGroup  onChange={onChange} size="default">
             <RadioButton value="execLocal">本地执行</RadioButton>
             <RadioButton value="execRemote" disabled={true}>远程执行</RadioButton>
             </RadioGroup>
             )}
             </FormItem>
             {
             changeModel()
             }*/}

            <FormItem  label="引擎列表"  {...formItemLayout} >
              {getFieldDecorator('engineName',{
                initialValue:"Default-Local",
                rules: [{ required: true, message: '请选择执行引擎' }]
              })(
                <Select placeholder="请选择引擎名称" allowClear={true}>

                  {
                    serverList.map((index)=>{
                      if(index.type != "Clustered"){
                        return   <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                      }
                    })
                  }

                </Select>
              )}
            </FormItem>
            <Row gutter={20} style={{flexDirection:"row"}}>
              <Col span={2}>&nbsp;</Col>
              <Col span={10} >
                <FormItem   {...formItemLayout3} style={{marginBottom:"0px"}}>
                  {getFieldDecorator('clearingLog',{
                    valuePropName: 'checked',
                    initialValue: true
                  })(
                    <Checkbox >执行前清除日志</Checkbox>
                  )}
                </FormItem>
                <FormItem {...formItemLayout3} style={{marginBottom:"0px"}}>
                  {getFieldDecorator('safeMode',{
                    valuePropName: 'checked',
                    initialValue: false
                  })(
                    <Checkbox >启用安全模式</Checkbox>
                  )}
                </FormItem>
                <FormItem {...formItemLayout3} style={{marginBottom:"0px"}}>
                  {getFieldDecorator('gatherMetrics',{
                    valuePropName: 'checked',
                    initialValue: true
                  })(
                    <Checkbox >收集性能指标</Checkbox>
                  )}
                </FormItem>
                <FormItem {...formItemLayout3} style={{marginBottom:"0px"}}>
                  {getFieldDecorator('rebootAutoRun',{
                    valuePropName: 'checked',
                    initialValue: true
                  })(
                    <Checkbox >重启服务(运行中)后自动运行</Checkbox>
                  )}
                </FormItem>
              </Col>
              <Col span={10}>
                <Row>
                  <FormItem label="日志级别" {...formItemLayout4}  style={{width:"100%"}}>
                    {getFieldDecorator('logLevel',{
                      initialValue: "Basic"
                    })(
                      <Select  >
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
                </Row>
                {
                  runModel === "batch"?null:<Row>
                    <FormItem label="开始调度" {...formItemLayout4}  style={{width:"100%"}}>
                      {getFieldDecorator('startCopyName',{
                        initialValue:""
                      })(
                        <Select >
                          {
                            this.getItemsName().map(index=>{
                              return(
                                <Option value={index} key={index}>{index}</Option>
                              )
                            })
                          }
                        </Select>
                      )}
                    </FormItem>
                  </Row>
                }
              </Col>
            </Row>
            <Tabs  style={{margin:"20px 8% 0  8%"}}  type="card">
              <TabPane tab="参数" key="1">
                <Row style={{margin:"5px 0",width:"100%"}}  >
                  <Col span={12}>&nbsp;</Col>
                  <Col span={12}  style={{textAlign:"right"}}>
                    <Button  size={"small"}   onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                  </Col>
                </Row>
                <EditTable  initFuc={this.initFuc.bind(this)} extendDisabled={true}  rowSelection={true} columns={this.Columns} dataSource = {[]} tableStyle="editTableStyle5" size={"small"} scroll={{y: 300}} ref="editTable"   count={0}/>
              </TabPane>
              <TabPane tab="环境变量" key="2">
                <Row style={{margin:"5px 0",width:"100%"}}  >
                  <Col span={12}>
                    <ButtonGroup size={"small"} >
                      <Button    onClick={this.handleAdd1}>添加字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12}  style={{textAlign:"right"}}>
                    <Button  size={"small"}   onClick={this.handleDeleteFields1.bind(this)}>删除字段</Button>
                  </Col>
                </Row>
                <EditTable  extendDisabled={true}  rowSelection={true} columns={this.Columns1} dataSource = {dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 300}} ref="editTable1"   count={0}/>
              </TabPane>
            </Tabs>
          </Form >
        </Modal>
      )
    }
}


const RunJob = Form.create()(Index);

export default connect(({ runjob }) => ({
  runjob
}))(RunJob)
