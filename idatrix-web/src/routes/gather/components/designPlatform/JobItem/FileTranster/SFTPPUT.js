import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig } from '../../../../constant';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const Option = Select.Option;


class SFTPPUT extends React.Component {

  constructor(){
     super();
     this.state = {
        path:""
     }
  }

  componentDidMount(){
    const { getDataStore } = this.props.model;
    let obj1 = {};
    obj1.type = "data";
    obj1.path = "";
    getDataStore(obj1,data=>{
      const { path } = data;
      this.setState({
        path:path
      })
    })
  }

  hideModal = () => {
    const { dispatch,form} = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
    form.resetFields();
  };

  handleCreate = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      const {panel,description,transname,key,saveEntry,text} = this.props.model;

      let obj = {};

      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.parallel= values.parallel;
      obj.entryParams = {
        ...values
      };

      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };


  handleChange1(e){
      const {setFieldsValue} = this.props.form;
      if(e.target.checked){
        setFieldsValue({copypreviousfiles:false});
      }
  };
  handleChange2(e){
    const {setFieldsValue} = this.props.form;
    if(e.target.checked){
      setFieldsValue({copyprevious:false});
    }
  };


  handleCheck = (e)=>{
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(err){
        message.error("请先完善必选项！");
        return false;
      }
      const { getJobDetails,transname,text } = this.props.model;
      let obj = {};
      obj.jobName = transname;
      obj.entryName = text;
      obj.detailType = "SFTP";
      obj.detailParam = {
        flag:"test",
        checkFolder:false,
        ...values
      };

      getJobDetails(obj,data=>{
      });
    })
  };


  handleCheckFolder = (e)=>{
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(err){
        message.error("请先完善必选项！");
        return false;
      }
      const { getFieldValue } = this.props.form;
      if(!getFieldValue("sftpDirectory") && !getFieldValue("sftpDirectory").trim()){
        message.error("请先完善必选项！");
        return false
      }

      const { getJobDetails,transname,text } = this.props.model;

      let obj = {};
      obj.jobName = transname;
      obj.entryName = text;
      obj.detailType = "SFTP";
      obj.detailParam = {
        flag:"test",
        checkFolder:true,
        Remotefoldername:getFieldValue("sftpDirectory"),
        ...values
      };

      getJobDetails(obj,data=>{
      });
    })
  };

  /*文件模板*/
  getFieldList(name,model){
    const {dispatch} = this.props;
    const {getFieldsValue,getFieldValue} = this.props.form;
    const {panel,formatFolder} = this.props.model;

   let infoObj = getFieldsValue(["serverName","serverPort","userName","password"]);

    let obj = treeViewConfig.get(panel)[name];
		let updateModel = "";
		let path = "";
    if(model === "key"){
			path = formatFolder(getFieldValue("keyfilename"));
      updateModel = this.setFolder1.bind(this); 
    }else if(model === "local"){
			path = formatFolder(getFieldValue("localDirectory"));
      updateModel = this.setFolder2.bind(this);
    }else if(model === "sftp"){
			path = formatFolder(getFieldValue("sftpDirectory"));
      updateModel = this.setFolder3.bind(this);
    }else{
			path = formatFolder(getFieldValue("destinationfolder"));
      updateModel = this.setFolder.bind(this);
    }

    let viewPath = "";

    if(name === "list" && model === "sftp"){
      path = `${infoObj.serverName}::${infoObj.serverPort}::${infoObj.userName}::${infoObj.password}::${path}`;
      if(!infoObj.serverName || !infoObj.serverPort || !infoObj.userName || !infoObj.password){
          message.error("请先完善服务器设置！");
          return false;
      }
    }else{
			if(path.substr(0,1) !== "/"){
				path = `${this.state.path}${path}`
			}
			viewPath = path;
    }

    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
          ...obj.obj,
          path
        },
        prefixStr:`${infoObj.serverName}::${infoObj.serverPort}::${infoObj.userName}::${infoObj.password}::`,
        viewPath:viewPath,
        updateModel:updateModel
      }
    })
  };

  /*设置文件名*/
  setFolder(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
          "destinationfolder":str
      })
    }
  };


  /*设置文件名*/
  setFolder1(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
        "keyfilename":str
      })
    }
  };

  /*设置文件名*/
  setFolder2(str){
    const { setFieldsValue } = this.props.form;
    if(str){
      setFieldsValue({
        "localDirectory":str
      })
    }
  };


  /*设置文件名*/
  setFolder3(str){
    const { setFieldsValue } = this.props.form;

    setFieldsValue({
      "sftpDirectory":str
    })
  };


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;
    const { path } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 },
    };

    const setDisabled = ()=>{
      if(getFieldValue("usekeyfilename") === undefined ){
        return config.usekeyfilename;
      }else{
        if(getFieldValue("usekeyfilename")){
          return getFieldValue("usekeyfilename");
        }else {
          return false;
        }
      }
    };

    const setDisabled1 = ()=>{
      if(getFieldValue("copyprevious") === undefined){
        return config.copyprevious || config.copypreviousfiles;
      }else{
        if(getFieldValue("copyprevious") || getFieldValue("copypreviousfiles")){
          return getFieldValue("copyprevious") || getFieldValue("copypreviousfiles");
        }else {
          return false;
        }
      }
    };

    const setDisabled2 = ()=>{
      if(getFieldValue("afterFTPS") === undefined ){
        return config.afterFTPS+"";
      }else{
        if(getFieldValue("afterFTPS")){
          return getFieldValue("afterFTPS");
        }
      }
    };

    return (
       <Modal
        visible={visible}
        title="SFTP 上传"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={750}
        onCancel={this.hideModal}
        footer={[
                   <Button  key="test" size="large"  onClick={this.handleCheck} style={{float:"left"}} >
                    测试连接
                  </Button>,
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="作业项名称"  {...formItemLayout} >
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
              <Input  />
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
				<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
        <Tabs type="card">
          <TabPane tab="一般" key="1">
            <FormItem
              {...formItemLayout}
              label="服务器设置"
              style={{marginBottom:"8px"}}
            >
            </FormItem>
            <FormItem label="SFTP服务器名称/IP"  {...formItemLayout} >
              {getFieldDecorator('serverName', {
                initialValue: config.serverName,
                rules: [{ required: true, message: '请输入服务器名称或IP名称' }]
              })(
                <Input />
              )}
            </FormItem>
            <FormItem label="端口"  {...formItemLayout} >
              {getFieldDecorator('serverPort', {
                initialValue: config.serverPort,
                 rules: [{ required: true, message: '请输入端口号' },
                        {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message: '请输入正确的端口号' }
                 ]
              })(
                <Input />
              )}
            </FormItem>
            <FormItem label="用户名"  {...formItemLayout} >
              {getFieldDecorator('userName', {
                initialValue: config.userName,
                rules: [{ required: true, message: '请输入用户名' }]
              })(
                <Input />
              )}
            </FormItem>
            <FormItem label="密码"  {...formItemLayout} >
              {getFieldDecorator('password', {
                initialValue: config.password,
                rules: [{ required: true, message: '请输入密码' }]
              })(
                <Input type="password" />
              )}
            </FormItem>
            <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
			            {getFieldDecorator('includeSubFolders', {
                    valuePropName: 'checked',
			              initialValue: config.includeSubFolders
			            })(
			               <Checkbox>是否包括子目录</Checkbox>
			            )}
            </FormItem>
            <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
              {getFieldDecorator('usekeyfilename', {
                valuePropName: 'checked',
                initialValue: config.usekeyfilename
              })(
                <Checkbox>使用私钥文件</Checkbox>
              )}
            </FormItem>
            <FormItem label="私钥文件"  {...formItemLayout1} style={{marginBottom:"8px"}}>
              {getFieldDecorator('keyfilename', {
                initialValue: config.keyfilename,
              })(
                <Input disabled={!setDisabled()} />
              )}
              <Button disabled={!setDisabled()}  onClick={()=>{this.getFieldList("model","key")}}>浏览</Button>
            </FormItem>
            <FormItem label="密钥"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('keyfilepass', {
                initialValue: config.keyfilepass,
              })(
                <Input disabled={!setDisabled()}/>
              )}
            </FormItem>
            <FormItem label="代理类型"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('proxyType', {
                initialValue: config.proxyType,
              })(
                <Select style={{ width: 285 }} >
                  <Option value="1">SOCKS5</Option>
                  <Option value="2">Http</Option>
                </Select>
              )}
            </FormItem>
            <FormItem label="代理主机"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('proxyHost', {
                initialValue: config.proxyHost,
              })(
                <Input />
              )}
            </FormItem>
            <FormItem label="代理端口"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('proxyPort', {
                initialValue: config.proxyPort,
              })(
                <Input />
              )}
            </FormItem>
            <FormItem label="代理用户名"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('proxyUsername', {
                initialValue: config.proxyUsername,
              })(
                <Input />
              )}
            </FormItem>
            <FormItem label="代理密码"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('proxyPassword', {
                initialValue: config.proxyPassword,
              })(
                <Input type="password"/>
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label="其余设置"
              style={{marginBottom:"8px"}}
            >
            </FormItem>
            <FormItem label="压缩"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('compression', {
                initialValue: config.compression,
              })(
                <Select style={{ width: 285 }}>
                  <Option value="none">none</Option>
                  <Option value="zlib">zlib</Option>
                </Select>
              )}
            </FormItem>

          </TabPane>
		    <TabPane tab="文件" key="2">
          <FormItem
            {...formItemLayout}
            label="源(本地)文件"
            style={{marginBottom:"8px"}}
          >
          </FormItem>
          <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
            {getFieldDecorator('copyprevious', {
              valuePropName: 'checked',
              initialValue: config.copyprevious
            })(
               <Checkbox  onChange ={this.handleChange1.bind(this)}>将上一个作业项结果做为参数</Checkbox>
            )}
         </FormItem>
         <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
            {getFieldDecorator('copypreviousfiles', {
              valuePropName: 'checked',
              initialValue: config.copypreviousfiles
            })(
               <Checkbox onChange ={this.handleChange2.bind(this)} >Copy previous result files to</Checkbox>
            )}
         </FormItem>
          <FormItem label="本地目录"  {...formItemLayout1} style={{marginBottom:"8px"}}>
            {getFieldDecorator('localDirectory', {
              initialValue: config.localDirectory,
            })(
            <Input disabled={setDisabled1()} />
            )}
            <Button disabled={setDisabled1()} onClick={()=>{this.getFieldList("remote","local")}}>浏览 </Button>
          </FormItem>
           <FormItem label="通配符(正则表达式)"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('wildcard', {
                initialValue: config.wildcard
              })(
                 <Input disabled={setDisabled1()}/>
              )}
          </FormItem>
          <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
              {getFieldDecorator('successWhenNoFile', {
                valuePropName: 'checked',
                initialValue: config.successWhenNoFile
              })(
                 <Checkbox>当本地没有文件时运行成功</Checkbox>
              )}
               </FormItem>
                <FormItem label="SFTP上传后"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('afterFTPS', {
                initialValue: config.afterFTPS+"",
              })(
                <Select style={{ width: 285 }}>
                  <Option value="0">什么也不做</Option>
                  <Option value="1">删除文件</Option>
                  <Option value="2">文件移动到</Option>
                </Select>
              )}
          </FormItem>
          <FormItem label="目标文件夹"  {...formItemLayout} style={{marginBottom:"8px"}}>
              {getFieldDecorator('destinationfolder', {
                initialValue: config.destinationfolder,
              })(
               <Input  disabled={setDisabled2() === "2"?false:true}/>
              )}
              <Button disabled={setDisabled2() === "2"?false:true} onClick={()=>{this.getFieldList("remote")}}>浏览 </Button>
          </FormItem>
          <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
              {getFieldDecorator('createDestinationFolder', {
                valuePropName: 'checked',
                initialValue: config.createDestinationFolder
              })(
                 <Checkbox disabled={setDisabled2() === "2"?false:true}>创建目标文件</Checkbox>
              )}
          </FormItem>
           <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                {getFieldDecorator('addFilenameResut', {
                  valuePropName: 'checked',
                  initialValue: config.addFilenameResut
                })(
                  <Checkbox disabled={setDisabled2() === "0"?false:true}>添加文件名到结果文件列表</Checkbox>
                )}
           </FormItem>
          <FormItem
            {...formItemLayout}
            label="目标(远程)文件夹"
            style={{marginBottom:"8px"}}
          >
          </FormItem>
            <FormItem label="远程目录"  {...formItemLayout1} style={{marginBottom:"8px"}}>
                 {getFieldDecorator('sftpDirectory', {
                    initialValue: config.sftpDirectory
                  })(
                    <Input />
                    )}
                <Button onClick={()=>{this.getFieldList("list","sftp")}}>浏览</Button>
                <Button onClick={this.handleCheckFolder.bind(this)}>测试</Button>
            </FormItem>
             <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                {getFieldDecorator('createRemoteFolder', {
                  valuePropName: 'checked',
                  initialValue: config.createRemoteFolder
                })(
                   <Checkbox>创建文件夹</Checkbox>
                )}
           </FormItem>
		    </TabPane>
		  </Tabs>

        </Form>
      </Modal>
    );
  }
}
const SftpputForm = Form.create()(SFTPPUT);
export default connect()(SftpputForm);
