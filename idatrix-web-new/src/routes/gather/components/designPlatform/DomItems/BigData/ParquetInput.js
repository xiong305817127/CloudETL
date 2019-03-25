import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig,selectType } from '../../../../constant';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const Option = Select.Option;
import EditTable from '../../../common/EditTable';

class ParquetInput extends React.Component {
  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { inputFields } = props.model.config;
      let data = [];
      let count = 0;
      if(inputFields){
        for(let index of inputFields){
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
        path:"",
      }
    }
  };

  componentDidMount(){
    this.Request();
  }


  Request(){
    const { getHadoopServer,getDataStore,getInputSelect } = this.props.model;
    const { dataSource } = this.state;
    getHadoopServer(data =>{
        this.setState({
          hodoopList:data
        });
    });
    getDataStore({type:"data",path:""},data=>{
      const { path } = data;
      this.setState({
        path:path
      });
    });
    if(this.refs.editTable){
        let options = getInputSelect(dataSource,"name");
		this.refs.editTable.updateOptions({
		  name:options
		});
    }
  }


  /*表格1*/
  Columns = [{
    title: '变量',
    dataIndex: 'path',
    key: 'path',
    editable:true,
    width:"32%"
  },{
    title: '字段名',
    dataIndex: 'name',
    key: 'name',
    width:"32%",
    selectable:true
  },{
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    selectable:true,
    selectArgs:selectType.get("type")
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
          let args = ["path", "name", "type"];
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

      delete values.text;

      obj.config = {
        inputFile:{...values},
        inputFields:sendFields
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

    console.log(obj);

    let str = getFieldValue("sourceConfigurationName");
    let str1 = formatFolder(getFieldValue("fileName"));
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
      "fileName":str
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
      const { getDetails,transname,text,panel,getInputSelect } = this.props.model;
      const { getFieldValue } = this.props.form;

       let sourceConfigurationName = getFieldValue("sourceConfigurationName");
       let fileName = getFieldValue("fileName");
       
       if(sourceConfigurationName && fileName){
   		 getDetails({
	      	transName:transname,
	      	stepName:text,
	      	detailType:panel,
	      	detailParam:{
	      		flag:"getFields",fileName,
	      		sourceConfigurationName
	      	}
	      },data=>{
	      	let args = [];
      		let count = 0;
      		let options = getInputSelect(data,"fieldName");
      		for(let index of data){
      			args.push({
      				key:count++,
      				name:index.fieldName,
      				path:index.fieldName,
      				type:index.type
      			})
      		}
      		this.refs.editTable.updateTable(args,count);
	        this.refs.editTable.updateOptions({
	          name:options
	        });
	      })
       }	

     


    /*  let args = [];
      let count = 0;
      const { InputData } = this.state;

      for(let index of fieldsList){
        args.push({
          key:count,
          columnName:index.name,
          streamName:""
        });
        count++;
      }

      let sameArgs = get_Similarity(args,InputData,"columnName","streamName");

      console.log(sameArgs,"相似的数组");

      this.refs.editTable.updateTable(sameArgs,count);*/
	}

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    const { hodoopList,path } = this.state;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };

    const formItemLayout1 = {
      labelCol: { span: 5 },
      wrapperCol: { span: 14 }
    };

    return (
      <Modal
        visible={visible}
        title="Parquet输入"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="转换名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
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
                initialValue:config.inputFile.sourceConfigurationName
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
	            {getFieldDecorator('fileName', {
	              initialValue: config.inputFile.fileName
	            })(
	              <Input />
	            )}
	            <Button onClick={()=>{this.getFieldList()}}>浏览</Button>
          	</FormItem>
          	<Row>
	            <Col span={12}>
	              <p style={{marginLeft:"5px"}}>字段列表：</p>
	            </Col>
	            <Col span={12}>
	              <ButtonGroup size={"small"} style={{float:"right"}} >
	                <Button   onClick={this.handleAdd}>添加字段</Button>
                    <Button   onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                    <Button   onClick={this.getFields.bind(this)} >获取字段</Button>
	              </ButtonGroup>
	            </Col>
          	</Row>
          	<EditTable columns={this.Columns} dataSource = {this.state.dataSource} count={1} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}}  rowSelection={true} ref="editTable" />
        </Form>
      </Modal>
    );
  }
}

export default connect()(Form.create()(ParquetInput));
