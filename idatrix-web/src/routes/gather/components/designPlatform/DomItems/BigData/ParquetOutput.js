import React from "react";
import { connect } from 'dva';
import { Button, Form, Input,Select,Tabs,Checkbox,Row,Col,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig,selectType } from '../../../../constant';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const Option = Select.Option;
import EditTable from '../../../common/EditTable';

class ParquetOutput extends React.Component {
  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { outputFields } = props.model.config;
      let data = [];
      let count = 0;
      if(outputFields){
        for(let index of outputFields){
          data.push({
            key:count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        hodoopList:[],
        path:""
      }
    }
  };

  componentDidMount(){
    this.Request();
  }


  Request(){
    const { getHadoopServer,getDataStore,transname,text,getInputFields,getInputSelect } = this.props.model;
    const { dataSource } = this.state;
    getHadoopServer(data =>{
        this.setState({
          hodoopList:data
        });
    });
    getInputFields({transname:transname,stepname:text}, data => {
      this.setState({
        InputData:data
      });
      if(this.refs.editTable){
        let options = getInputSelect(data,"name");
        this.refs.editTable.updateOptions({
          name:options
        });
      }
    });
    getDataStore({type:"data",path:""},data=>{
      const { path } = data;
      this.setState({
        path:path
      });
    });
  }


  /*表格1*/
  Columns = [{
    title: '变量',
    dataIndex: 'path',
    key: 'path',
    editable:true,
    width:"20%"
  },{
    title: '字段名',
    dataIndex: 'name',
    key: 'name',
    width:"20%",
    selectable:true
  },{
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width:"18%",
    selectable:true,
    selectArgs:selectType.get("type")
  },
  {
    title: '默认值',
    dataIndex: 'ifNullValue',
    key: 'ifNullValue',
    editable:true,
     width:"20%",
  },
  {
    title: '允许为空',
    dataIndex: 'nullable',
    key: 'nullable',
    selectable:true,
    selectArgs:[
    	<Select.Option key={true} value="true" >是</Select.Option>,
    	<Select.Option key={false} value="false" >否</Select.Option>
    ]
  }
  ];

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      };
      const { inputFields } = this.props.model.config;
      const { formatTable} = this.props.model;
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = ["path", "name", "type", "nullable","ifNullValue"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(inputFields){
          sendFields = inputFields
        }
      }

      const {panel,description,transname,key,saveStep,text} = this.props.model;

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;

      obj.config = {
        ...values,
        outputFields:sendFields
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };

  /*文件模板*/
  getFieldList(){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;
    let obj = treeViewConfig.get(panel)["model"];

    let str = getFieldValue("sourceConfigurationName");
    let str1 = formatFolder(getFieldValue("filename"));
    let updateModel = this.setFolder.bind(this);

    let viewPath = "";
    let needUpFolder = true;

    if(str && str.trim()){
      let obj1 = {};
      let str2 = "";

      if(str === "local"){
				let type = "input";
				if(str1.substr(0,1) !== "/"){
					str1 = `${this.state.path}${str1}`
				}
				viewPath = str1;
        obj1 = {
          type:type,
          path:str1,
          depth:1
        }
      }else{
        obj1 = {
          type:"hdfs",
          path:str+"::"+ (str1?str1:"/"),
          depth:1
        };
        str2 = str+"::";
      };

      dispatch({
        type:"treeview/showTreeModel",
        payload:{
          ...obj,
          obj:obj1,
          needUpFolder:needUpFolder,
          updateModel:updateModel,
          prefixStr:str2,
          viewPath:viewPath
        }
      })

    }else{
        message.error("请先选择路径！");
    }
  };

  /*设置文件名*/
  setFolder(str){
    if(!str){
      return;
    }
    const { setFieldsValue } = this.props.form;

    setFieldsValue({
      "filename":str
    });
  };

  	//添加字段
   	handleAdd = ()=>{
		const data = {
	  		tableIndex: "",
	  		flowIndex: ""
		};
		this.refs.editTable.handleAdd(data);
  	};

	//删除字段
	handleDeleteFields = ()=>{
	  	this.refs.editTable.handleDelete();
	};

	//获取字段
	getFields(){
      let args = [];
      let count = 0;
      const { InputData } = this.state;
      for(let index of InputData){
        args.push({
           key:count++,
           path:index.name,
           name: index.name,
           type: "",
           nullable:false,
           ifNullValue:""
        });
      }
      this.refs.editTable.updateTable(args,count);
	}

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    const { hodoopList,path } = this.state;

    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 14 }
    };

    const formItemLayout1 = {
      labelCol: { span: 5 },
      wrapperCol: { span: 14 }
    };

    const formItemLayout2 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };

     const setDisabled = ()=>{
      if(getFieldValue("specify") === undefined){
        return config.dateTimeFormat?true:false;
      }else{
        if(getFieldValue("specify")){
          return getFieldValue("specify");
        }else {
          return false;
        }
      }
    }

    const setDisabled1 = ()=>{
      if(getFieldValue("enableDictionary") === undefined){
        return !config.enableDictionary;
      }else{
        if(getFieldValue("enableDictionary")){
          return !getFieldValue("enableDictionary");
        }else {
          return true;
        }
      }
    }

    return (
      <Modal
        visible={visible}
        title="Parquet输出"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        width={700}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="转换名称"  {...formItemLayout2} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称！' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input  />
            )}
          </FormItem>
					<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
          <FormItem label="路径"  {...formItemLayout1} style={{marginBottom:"8px"}}>
              {getFieldDecorator('sourceConfigurationName', {
                initialValue:config.sourceConfigurationName
              })(
                <Select  >
                  <Option key="local" value="local">Local</Option>
                  { hodoopList.map(index=>{
                    return(
                      <Option key={index.name}>{index.name}</Option>
                    )
                  })
                  }
                </Select>
              )}
            </FormItem>
          	<FormItem label="目录/文件名"  {...formItemLayout1} style={{marginBottom:"8px"}}>
	            {getFieldDecorator('filename', {
	              initialValue: config.filename
	            })(
	              <Input />
	            )}
	            <Button onClick={()=>{this.getFieldList()}}>浏览</Button>
          	</FormItem>
          	 <FormItem  style={{marginBottom:"0px",marginLeft:"78px"}} {...formItemLayout1}>
	              {getFieldDecorator('overrideOutput', {
	                valuePropName: 'checked',
	                initialValue:config.overrideOutput,
	              })(
	                <Checkbox>覆盖现有的输出文件</Checkbox>
	              )}
            </FormItem>
            <Tabs type="card">
            	<TabPane tab="字段" key="1">
            		<Row>
			            <Col span={12}>
			              <p style={{marginLeft:"5px"}}>字段列表：</p>
			            </Col>
			            <Col span={12}>
			              <ButtonGroup size={"small"} style={{float:"right"}} >
			                <Button   onClick={this.handleAdd}>添加字段</Button>
		                    <Button   onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
		                    <Button  size={"small"} onClick={this.getFields.bind(this)} >获取字段</Button>
			              </ButtonGroup>
			            </Col>
		          	</Row>
		          	<EditTable columns={this.Columns} dataSource = {this.state.dataSource} count={1} size={"small"} scroll={{y: 140}}  rowSelection={true} ref="editTable" />
            	</TabPane>
            	<TabPane tab="选项" key="2">
            		<Row>
            			<Col span={12} >
				            <FormItem label="压缩类型"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('compressionType', {
				                initialValue:config.compressionType?config.compressionType:"NONE"
				              })(
				                <Select  >
				                  <Option key="NONE" value="NONE">None</Option>
				                  <Option key="SNAPPY" value="SNAPPY">Snappy</Option>
				                  <Option key="GZIP" value="GZIP">GZIP</Option>
				                </Select>
				              )}
				            </FormItem>
            			</Col>
            			<Col span={12} >
				            <FormItem label="版本"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('parquetVersion', {
				                initialValue:config.parquetVersion?config.parquetVersion:"PARQUET_1"
				              })(
				                <Select  >
				                  <Option key="PARQUET_1" value="PARQUET_1">Parquet 1.0</Option>
				                  <Option key="PARQUET_2" value="PARQUET_2">Parquet 2.0</Option>
				                </Select>
				              )}
				            </FormItem>
            			</Col>
            		</Row>
            		<Row>
            			<Col span={12} >
				            <FormItem label="行簇大小(MB)"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('rowGroupSize', {
				                initialValue:config.rowGroupSize
				              })(
				                <Input />
				              )}
				            </FormItem>
            			</Col>
            			<Col span={12}>
            			 	<FormItem label="数据页大小(KB)"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('dataPageSize', {
				                initialValue:config.dataPageSize
				              })(
				                <Input />
				              )}
				            </FormItem>
            			</Col>
            		</Row>
            		<Row>
            			<Col span={12} >
            				<FormItem label="扩展名"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('extension', {
				                initialValue:config.extension
				              })(
				                <Input />
				              )}
				            </FormItem>
            			</Col>
            		</Row>
            		<Row>
            			<Col span={12} >
            				<FormItem  style={{marginBottom:"0px",marginLeft:"78px"}} {...formItemLayout}>
				              {getFieldDecorator('dateInFilename', {
				                valuePropName: 'checked',
				                initialValue:config.dateInFilename,
				              })(
				                <Checkbox>文件名包含日期</Checkbox>
				              )}
				            </FormItem>
            			</Col>
            			<Col span={12} >
            				<FormItem  style={{marginBottom:"0px",marginLeft:"78px"}} {...formItemLayout}>
				              {getFieldDecorator('timeInFilename', {
				                valuePropName: 'checked',
				                initialValue:config.timeInFilename,
				              })(
				                <Checkbox>文件名包含时间</Checkbox>
				              )}
				         	</FormItem>
            			</Col>
            		</Row>
            		<Row>
            			<Col span={12} >
            				<FormItem  style={{marginBottom:"0px",marginLeft:"78px"}} {...formItemLayout}>
				              {getFieldDecorator('specify', {
				                valuePropName: 'checked',
				                initialValue:config.dateTimeFormat?true:false,
				              })(
				                <Checkbox>指定的日期时间格式</Checkbox>
				              )}
				            </FormItem>
            			</Col>
            			<Col span={12} >
            				<FormItem label="格式"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('dateTimeFormat', {
				                initialValue:config.dateTimeFormat
				              })(
				                <Select disabled={!setDisabled()} >
				                  <Option key="yyyyMMddHHmmss" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Option>
				                  <Option key="yyyy-MM-dd" value="yyyy-MM-dd">yyyy-MM-dd</Option>
				                  <Option key="yyyyMMdd" value="yyyyMMdd">yyyyMMdd</Option>
				                  <Option key="MM-dd-yyyy" value="MM-dd-yyyy">MM-dd-yyyy</Option>
				                  <Option key="MM-dd-yy" value="MM-dd-yy">MM-dd-yy</Option>
				                  <Option key="dd-MM-yyyy" value="dd-MM-yyyy">dd-MM-yyyy</Option>
				                </Select>
				              )}
				            </FormItem>
            			</Col>
            		</Row>
            		<Row>
            			<Col span={12} >
            				<FormItem  style={{marginBottom:"0px",marginLeft:"78px"}} {...formItemLayout}>
				              {getFieldDecorator('enableDictionary', {
				                valuePropName: 'checked',
				                initialValue:config.enableDictionary,
				              })(
				                <Checkbox>字典编码</Checkbox>
				              )}
				            </FormItem>
            			</Col>
            			<Col span={12} >
            				<FormItem label="页面大小"  {...formItemLayout} style={{marginBottom:"8px"}}>
				              {getFieldDecorator('dictPageSize', {
				                initialValue:config.dictPageSize
				              })(
				                <Input disabled={setDisabled1()} />
				              )}
				            </FormItem>
            			</Col>
            		</Row>
            	</TabPane>
            </Tabs>
          	</Form>
      </Modal>
    );
  }
}

export default connect()(Form.create()(ParquetOutput));
