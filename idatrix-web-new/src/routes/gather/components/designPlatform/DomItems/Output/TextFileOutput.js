import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Tabs,Row,Col,Radio } from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig } from '../../../../constant';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';

let Timer;

class TextFileOutputDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { fields } = props.model.config;
      let data = [];
      let count = 0;
      if(fields){

        for(let index of fields){
          data.push({
            key:count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        InputData:[],
        path:""
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,getDataStore } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      })
    });
    let obj1 = {};
    obj1.type = "output";
    obj1.path = "";
    getDataStore(obj1,data=>{
      const { path } = data;
      this.setState({
        path:path
      })
    })
  };

  initFuc(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");
    that.updateOptions({
      name:options
    });
  };

  optionGroups = [
    <Option key="yyyy/MM/dd HH:mm:ss.SSS" value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Option>,
    <Option  key="yyyy/MM/dd HH:mm:ss.SSS XXX" value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Option>,
    <Option key="yyyy/MM/dd HH:mm:ss" value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>,
    <Option key="yyyy/MM/dd HH:mm:ss XXX" value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Option>,
    <Option key="yyyyMMddHHmmss" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Option>,
    <Option key="yyyy/MM/dd" value="yyyy/MM/dd">yyyy/MM/dd</Option>,
    <Option key="yyyy-MM-dd" value="yyyy-MM-dd">yyyy-MM-dd</Option>,
    <Option key="yyyy-MM-dd HH:mm:ss" value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Option>,
    <Option key="yyyy-MM-dd HH:mm:ss XXX" value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Option>,
    <Option key="yyyyMMdd" value="yyyyMMdd">yyyyMMdd</Option>,
    <Option key="MM/dd/yyyy" value="MM/dd/yyyy">MM/dd/yyyy</Option>,
    <Option key="MM/dd/yyyy HH:mm:ss" value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Option>,
    <Option key="MM-dd-yyyy" value="MM-dd-yyyy">MM-dd-yyyy</Option>,
    <Option key="MM-dd-yyyy HH:mm:ss" value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Option>,
    <Option key="MM/dd/yy" value="MM/dd/yy">MM/dd/yy</Option>,
    <Option key="MM-dd-yy" value="MM-dd-yy">MM-dd-yy</Option>,
    <Option key="dd/MM/yyyy" value="dd/MM/yyyy">dd/MM/yyyy</Option>,
    <Option key="dd-MM-yyyy" value="dd-MM-yyyy">dd-MM-yyyy</Option>,
    <Option key="yyyy-MM-dd'T'HH:mm:ss.SSSXXX" value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Option>,
];

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  formItemLayout2 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 12 }
  };
  formItemLayout = {
    wrapperCol: { span:18}
  };

  columns = [
    {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width:"12%",
    selectable:true
  }, {
    title: '类型',
    dataIndex: 'type',
    width:"13%",
    key: 'type',
    selectable:true,
    selectArgs:[<Select.Option key="Number" value="Number">Number</Select.Option>,
      <Select.Option key="Date" value="Date">Date</Select.Option>,
      <Select.Option key="String" value="String">String</Select.Option>,
      <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
      <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
      <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
      <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
      <Select.Option key="Timestamp" value="Timestamp">Timestamp</Select.Option>,
      <Select.Option key="Internet Address" value="Internet Address">Internet Address</Select.Option>
    ]

  }, {
    title: '格式',
    dataIndex: 'format',
    key: 'format',
    width:"10%",
    editable:true,
  }, {
    title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"8%",
    editable:true,
  }, {
    title: '精度',
    dataIndex: 'precision',
    key: 'precision',
    width:"8%",
    editable:true,
  }, {
      title: '货币',
      dataIndex: 'currencyType',
      key: 'currencyType',
      width:"8%",
      editable:true,
    },{
    title: '小数',
    dataIndex: 'decimal',
    key: 'decimal',
    width:"8%",
    editable:true,
  },{
    title: '分组',
    dataIndex: 'group',
    key: 'group',
    width:"8%",
    editable:true,
  },
    {
      title: '去除字符串方式',
      dataIndex: 'trimType',
      key: 'trimType',
      width:"13%",
      selectable:true,
      selectArgs:[<Select.Option key="none" value="none">不去掉空格</Select.Option>,
        <Select.Option key="left" value="left">去掉左空格</Select.Option>,
        <Select.Option key="right" value="right">去掉右空格</Select.Option>,
        <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>,
      ]
    },
    {
      title: 'null',
      dataIndex: 'nullif',
      key: 'nullif',
      editable:true,
    },
  ];

  handleAdd = ()=>{
    const data = {
      name:"",
      type:"",
      length:"",
      format:"",
      precision:""
    };
    this.refs.editTable.handleAdd(data);
  };

  handleGetFields = ()=>{
    const { InputData } = this.state;
    let args = [];
    let count = 0;
    for(let index of InputData){
      args.push({
        key:count,
        currencyType:index.currencySymbol,
        decimal:index.decimalSymbol,
        group:index.groupingSymbol,
        ...index
      });
      count++;
    }

    this.refs.editTable.updateTable(args,count);
  };

  setModelHide (){
    const {  dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  }

  handleFormSubmit(){
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,config,text,formatTable } = this.props.model;
    const { fields } = config;


    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let arg = [ "name", "type", "format", "currencyType", "decimal", "group", "nullif", "trimType", "length", "precision"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
        }
      }else{
        if(fields){
          sendFields = fields
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text?"":values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "fileName":values.fileName.trim(),
        "isCommand": values.isCommand,
        "servletOutput": values.servletOutput,
        "createParentFolder": values.createParentFolder,
        "doNotOpenNewFileInit":values.doNotOpenNewFileInit,
        "fileNameInField": values.fileNameInField,
        "fileNameField": values.fileNameField,
        "extention": values.extention,
        "stepNrInFilename": values.stepNrInFilename,
        "haspartno": values.haspartno, // partNrInFilename
        "addDate": values.addDate,
        "addTime": values.addTime,
        "specifyFormat": values.specifyFormat,
        "dateTimeFormat": values.dateTimeFormat,
        "addToResultFilenames": values.addToResultFilenames,
        "append": values.append,
        "separator": values.separator,
        "enclosure": values.enclosure,
        "enclosureForced": values.enclosureForced,
        "enclosureFixDisabled": values.enclosureFixDisabled,
        "header": values.header,
        "footer": values.footer,
        "format": values.format,
        "compression": values.compression,
        "encoding": values.encoding,
        "pad": values.pad,
        "fastDump": values.fastDump,
        "splitevery": values.splitevery,
        "endedLine": values.endedLine,
        "fields": sendFields
      }
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  }

  getFieldList(){
		const {dispatch} = this.props;
		const { getFieldValue } = this.props.form;
		const {panel,formatFolder} = this.props.model;
		let path = formatFolder(getFieldValue("fileName"));
    let viewPath = "";
		let obj = treeViewConfig.get(panel)["list"];
		
		if(path.substr(0,1) !== "/"){
			path = `${this.state.path}${path}`
		}
    viewPath = path;
    let updateModel = this.setFolder.bind(this);

    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
					...obj.obj,
					path
        },
        viewPath:viewPath,
        updateModel:updateModel
      }
    })
  };

  /*设置文件名*/
  setFolder(str){
    if(!str) return false;

    const { setFieldsValue } = this.props.form;
      setFieldsValue({
        "fileName":str
      })

  };

  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { visible,config,text,handleCheckName,isMetacube } = this.props.model;
    const { path } = this.state;

    const setDisabled = ()=>{
      if(getFieldValue("isCommand") === undefined){
        return config.isCommand;
      }else{
        if(getFieldValue("isCommand")){
          return getFieldValue("isCommand");
        }else {
          return false;
        }
      }
    };

    const setDisabled1 = ()=>{

      if(getFieldValue("servletOutput") === undefined){
        return config.servletOutput;
      }else{
        if(getFieldValue("servletOutput")){
          return getFieldValue("servletOutput");
        }else {
          return false;
        }
      }
    };

    const setDisabled2 = ()=>{

      if(getFieldValue("fileNameInField") === undefined){
        return config.fileNameInField;
      }else{
        if(getFieldValue("fileNameInField")){
          return getFieldValue("fileNameInField");
        }else {
          return false;
        }
      }
    };

    const setDisabled3 = ()=>{

      if(getFieldValue("specifyFormat") === undefined){
        return config.specifyFormat;
      }else{
        if(getFieldValue("specifyFormat")){
          return getFieldValue("specifyFormat");
        }else {
          return false;
        }
      }
    };



    return (

      <Modal
        visible={visible}
        title="文本文件输出"
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
            <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
            <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
        maskClosable={false}
        onCancel ={this.setModelHide.bind(this)}
      >
        <Form >
          <FormItem label="步骤名称"  {...this.formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <div style={{margin:"0 5%"}}>
            <Tabs type="card">
              <TabPane tab="文件" key="1">
								<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                <FormItem label="文件名称"  {...this.formItemLayout2}>
                  {getFieldDecorator('fileName', {
                    initialValue: config.fileName
                  })(
                    <Input disabled={setDisabled1() || setDisabled2()}/>
                  )}
                  <Button disabled={setDisabled1() || setDisabled2() } onClick={()=>{this.getFieldList()}}>浏览</Button>
                </FormItem>
                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('isCommand', {
                        valuePropName: 'checked',
                        initialValue:config.isCommand
                      })(
                        <Checkbox  disabled={setDisabled1() } >结果输送至命令行或脚本</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('servletOutput', {
                        valuePropName: 'checked',
                        initialValue:config.servletOutput
                      })(
                        <Checkbox >输出传递到servlet</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('createParentFolder', {
                        valuePropName: 'checked',
                        initialValue:config.createParentFolder
                      })(
                        <Checkbox disabled={setDisabled() || setDisabled1() || isMetacube }>创建父目录</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('doNotOpenNewFileInit', {
                        valuePropName: 'checked',
                        initialValue:config.doNotOpenNewFileInit
                      })(
                        <Checkbox disabled={setDisabled1() || setDisabled2()}>启动时不创建文件</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row >
                  <Col span={12}>
                    <FormItem  style={{marginLeft:"20%",marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('fileNameInField', {
                        valuePropName: 'checked',
                        initialValue:config.fileNameInField
                      })(
                        <Checkbox  >从字段中获取文件名(全路径)</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem label="文件名字段"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('fileNameField', {
                    initialValue:config.fileNameField
                  })(
                    <Select  disabled={!setDisabled2() || setDisabled1()}>
                      {
                        this.state.InputData.map((index)=>
                          <Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>
                        )
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="扩展名"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('extention', {
                    initialValue:config.extention
                  })(
                   <Input disabled={setDisabled1()} />
                  )}
                </FormItem>
                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('stepNrInFilename', {
                        valuePropName: 'checked',
                        initialValue:config.stepNrInFilename
                      })(
                        <Checkbox disabled={setDisabled1() || setDisabled2() }>文件名里包含步骤数？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('haspartno', {
                        valuePropName: 'checked',
                        initialValue:config.haspartno
                      })(
                        <Checkbox disabled={setDisabled1()|| setDisabled2()}>文件名里包含数据分区号？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('addDate', {
                        valuePropName: 'checked',
                        initialValue:config.addDate
                      })(
                        <Checkbox disabled={setDisabled1()|| setDisabled2() || setDisabled3()}>文件名里包含日期？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('addTime', {
                        valuePropName: 'checked',
                        initialValue:config.addTime
                      })(
                        <Checkbox disabled={setDisabled1()|| setDisabled2() || setDisabled3()}>文件名里包含时间？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('specifyFormat', {
                        valuePropName: 'checked',
                        initialValue:config.specifyFormat
                      })(
                        <Checkbox  disabled={setDisabled1()|| setDisabled2()}>指定日期时间格式？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('addToResultFilenames', {
                        valuePropName: 'checked',
                        initialValue:config.addToResultFilenames
                      })(
                        <Checkbox disabled={setDisabled1()}>结果中添加文件名？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem label="日期时间格式"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('dateTimeFormat', {
                    initialValue:config.dateTimeFormat
                  })(
                    <Select disabled={setDisabled2() || setDisabled1() || !setDisabled3()}>
                      {
                        this.optionGroups
                      }
                    </Select>
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="内容" key="2">

                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"10px"}} {...this.formItemLayout}>
                      {getFieldDecorator('append', {
                        valuePropName: 'checked',
                        initialValue:config.append
                      })(
                        <Checkbox disabled={setDisabled1()}>追加方式</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>

                <FormItem label="分隔符"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('separator', {
                    initialValue:config.separator
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="封闭符"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('enclosure', {
                    initialValue:config.enclosure
                  })(
                    <Input />
                  )}
                </FormItem>
                <Row  style={{marginLeft:"10%"}}>
                   <Col span={12}>
                     <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                       {getFieldDecorator('enclosureForced', {
                         valuePropName: 'checked',
                         initialValue:config.enclosureForced
                       })(
                         <Checkbox >强制在字段周围加封闭符？</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                   <Col span={12}>
                     <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                       {getFieldDecorator('enclosureFixDisabled', {
                         valuePropName: 'checked',
                         initialValue:config.enclosureFixDisabled
                       })(
                         <Checkbox >禁用封闭符修复？</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                </Row>
                <Row style={{marginLeft:"10%"}}>
                   <Col span={12}>
                     <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                       {getFieldDecorator('header', {
                         valuePropName: 'checked',
                         initialValue:config.header
                       })(
                         <Checkbox >头部？</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                   <Col span={12}>
                     <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                       {getFieldDecorator('footer', {
                         valuePropName: 'checked',
                         initialValue:config.footer
                       })(
                         <Checkbox >尾部？</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                </Row>




                <FormItem label="格式"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('format', {
                    initialValue:config.format
                  })(
                    <Select>
                      <Option value="DOS">CR+LF terminated (Windows, DOS)</Option>
                      <Option value="UNIX">LF terminated (Unix)</Option>
                      <Option value="CR">CR terminated</Option>
                      <Option value="None">No new-line terminated</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem label="压缩"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('compression', {
                    initialValue:config.compression
                  })(
                    <Select>
                      <Option value="None">None</Option>
                      <Option value="Zip">Zip</Option>
                      <Option value="GZip">GZip</Option>
                      <Option value="Snappy">Snappy</Option>
                      <Option value="Hadoop-snappy">Hadoop-snappy</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem label="编码"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('encoding', {
                    initialValue:config.encoding?config.encoding:"GBK"
                  })(
                    <Select>
                      <Option value="GBK">GBK</Option>
                      <Option value="ISO-8859-1">ISO-8859-1</Option>
                      <Option value="GB2312">GB2312</Option>
                      <Option value="UTF-8">UTF-8</Option>
                      <Option value="Big5">Big5</Option>
                    </Select>
                  )}
                </FormItem>
                <Row style={{marginLeft:"10%"}}>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator('pad', {
                        valuePropName: 'checked',
                        initialValue:config.pad
                      })(
                        <Checkbox >字段右填充或裁剪？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem  style={{marginBottom:"0px"}} {...this.formItemLayout}>
                      {getFieldDecorator("fastDump", {
                        valuePropName: 'checked',
                        initialValue:config.fastDump
                      })(
                        <Checkbox >快速数据存储(无格式)？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>


                <FormItem label="分拆...每一行"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('splitevery', {
                    initialValue:config.splitevery
                  })(
                    <Input disabled={setDisabled1()|| setDisabled2()}/>
                  )}
                </FormItem>
                <FormItem label="添加文件结束行"   style={{marginBottom:"8px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('endedLine', {
                    initialValue:config.endedLine
                  })(
                    <Input disabled={setDisabled2()} />
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="字段" key="3">
                <div >
                  <Row style={{margin:"5px 0",width:"100%"}}  >
                    <Col span={12}  >
                      <ButtonGroup size={"small"}>
                        <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                        <Button onClick={()=>{this.handleGetFields()}}>获取字段</Button>
                      </ButtonGroup>
                    </Col>
                    <Col span={12}>
                      <Button  style={{float:"right"}} size={"small"}  onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                    </Col>
                  </Row>
                  <EditTable   initFuc={this.initFuc.bind(this)} columns={this.columns} rowSelection={true} dataSource = {this.state.dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140,x:1000}} ref="editTable"   count={4}/>
                </div>
              </TabPane>
            </Tabs>
          </div>

        </Form>
      </Modal>
    );
  }
}
const TextFileOutput = Form.create()(TextFileOutputDialog);

export default connect()(TextFileOutput);
