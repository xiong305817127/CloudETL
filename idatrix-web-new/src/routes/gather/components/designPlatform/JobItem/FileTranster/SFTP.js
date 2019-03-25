import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,message} from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig } from '../../../../constant';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const Option = Select.Option;


class SFTP extends React.Component {

    constructor(){
        super();
         this.state = {
             path:""
         }
    };

    componentDidMount(){
        const { getDataStore,getSftpList } = this.props.model;
        let obj1 = {};
        obj1.type = "data";
        obj1.path = "";
        getDataStore(obj1,data=>{
            const { path } = data;
            this.setState({
                path:path
            })
        });
       /* getSftpList(data=>{
            console.log(data);
        })*/
		}
		
  hideModal = () => {
    const { dispatch,form } = this.props;
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
      obj.entryParams = {...values};

      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };

  handleCheck = (e)=>{
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(err){
          return false;
      }
       const { getJobDetails,transname,text,panel } = this.props.model;
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
      if(!getFieldValue("sftpDirectory") || !getFieldValue("sftpDirectory").trim()){
        message.error("远程目录不能为空！");
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
			const { getFieldValue } = this.props.form;
			const {panel,formatFolder} = this.props.model;
      let obj = treeViewConfig.get(panel)[name];

      //设置目标文件
			let updateModel = this.setFolder.bind(this);
			let path = formatFolder(getFieldValue("targetDirectory"));
			let viewPath = "";
      if(model === "key"){
         //秘钥文件
				 updateModel = this.setFolder2.bind(this);
				 path = formatFolder(getFieldValue("keyfilename"));
      }
			
			if(path.substr(0,1) !== "/"){
				path = `${this.state.path}${path}`
			}
			viewPath = path;

      dispatch({
          type:"treeview/showTreeModel",
          payload:{
            ...obj,
            obj:{
							...obj.obj,
							path:path
            },
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
          "targetDirectory":str
        })
    }
  };

    /*设置文件名*/
  setFolder2(str){
      const { setFieldsValue } = this.props.form;
      if(str){
        setFieldsValue({
            "keyfilename":str
        })
      }
  };

  /*文件模板*/
  getFieldList1(name){
    const {dispatch} = this.props;
    const { getFieldsValue } = this.props.form;
    const {panel} = this.props.model;

      let infoObj = getFieldsValue(["serverName","serverPort","userName","password"]);

    let obj = treeViewConfig.get(panel)[name];
    let updateModel = this.setFolder1.bind(this);

    let type = "sftp";
    let viewPath = "";

      obj.obj.path = `${infoObj.serverName}::${infoObj.serverPort}::${infoObj.userName}::${infoObj.password}::/`;

      if(!infoObj.serverName || !infoObj.serverPort || !infoObj.userName || !infoObj.password){
          message.error("请先完善服务器设置！");
          return false;
      }
    dispatch({
      type:"treeview/showTreeModel",
      payload:{
        ...obj,
        obj:{
          ...obj.obj,
          type:type
        },
        viewPath:viewPath,
        prefixStr:`${infoObj.serverName}::${infoObj.serverPort}::${infoObj.userName}::${infoObj.password}::`,
        updateModel:updateModel
      }
    })
  };

  /*设置文件名*/
  setFolder1(str){
    const { setFieldsValue } = this.props.form;

      setFieldsValue({
          "sftpDirectory":str
      });
  };

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;
    const { path } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 }
    };

     const formItemLayout2 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 10 }
    };
      const formItemLayout3 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 }
    };
    const setDisabled = ()=>{
     if(getFieldValue("usekeyfilename") === undefined){
        return config.usekeyfilename;
      }else{
        if(getFieldValue("usekeyfilename")){
          return getFieldValue("usekeyfilename");
        }else {
          return false;
        }
      }
    }

    const setDisabled1 = ()=>{
     if(getFieldValue("copyprevious") === undefined){
        return config.copyprevious;
      }else{
        if(getFieldValue("copyprevious")){
          return getFieldValue("copyprevious");
        }else {
          return false;
        }
      }
    }
    return (
       <Modal
        visible={visible}
        title="SFTP 下载"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={600}
        onCancel={this.hideModal}
        footer={[
                   <Button  key="test" size="large" onClick={this.handleCheck} style={{float:"left"}} >
                    测试连接
                  </Button>,
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="作业项名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
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
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'10em'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
        <p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
        <Tabs type="card" style={{margin:"20px 10px 0 10px"}}>
		    <TabPane tab="一般" key="1">
          <FormItem
            {...formItemLayout}
            label="服务器设置"
            style={{marginBottom:"8px"}}
          >
          </FormItem>
          <FormItem label="SFTP服务器名称/IP"  {...formItemLayout3} >
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
                          {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message: '请输入正确的端口号' }]

			          })(
			            <Input/>
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
			              initialValue: config.compression+"",
			            })(
			              <Select style={{ width: 285 }}>
			                <Option value="1">none</Option>
			                <Option value="2">zlib</Option>
			              </Select>
			            )}
			        </FormItem>

		    </TabPane>
		    <TabPane tab="文件" key="2">
            <FormItem
              {...formItemLayout}
              label="源文件"
              style={{marginBottom:"8px"}}
            >
            </FormItem>
              <FormItem  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                  {getFieldDecorator('copyprevious', {
                    valuePropName: 'checked',
                    initialValue: config.copyprevious,
                  })(
                     <Checkbox>复制上一个作业项结果做为参</Checkbox>
                  )}
             </FormItem>
             <FormItem label="远程目录"  {...formItemLayout2} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('sftpDirectory', {
                    initialValue: config.sftpDirectory,
                  })(
                     <Input  />
                  )}
                  <Button  onClick={()=>{this.getFieldList1("list")}}>浏览</Button>
                  <Button  onClick={this.handleCheckFolder.bind(this)}> 测试 </Button>
             </FormItem>
             <FormItem label="通配符(正则表达式)"  {...formItemLayout} style={{marginBottom:"8px"}}>
                {getFieldDecorator('wildcard', {
                  initialValue: config.wildcard
                })(
                   <Input disabled={setDisabled1()}/>
                )}
             </FormItem>
              <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
                {getFieldDecorator('remove', {
                    valuePropName: 'checked',
                    initialValue: config.remove
                })(
                   <Checkbox>获取后删除服务器文件</Checkbox>
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
                label="目标文件"
                style={{marginBottom:"8px"}}
              >
              </FormItem>
              <FormItem label="目标目录"  {...formItemLayout} style={{marginBottom:"8px"}}>
			            {getFieldDecorator('targetDirectory', {
			              initialValue: config.targetDirectory,
			            })(
			             <Input />
			            )}
			            <Button  onClick={()=>{this.getFieldList("remote")}}>浏览</Button>
			        </FormItem>
			        <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
			            {getFieldDecorator('createtargetfolder', {
                    valuePropName: 'checked',
			              initialValue: config.createtargetfolder
			            })(
			               <Checkbox>创建目标文件</Checkbox>
			            )}
              </FormItem>
          <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px",marginLeft:"25%"}}>
              {getFieldDecorator('isaddresult', {
                valuePropName: 'checked',
                initialValue: config.isaddresult
              })(
                 <Checkbox>添加文件名到结果</Checkbox>
              )}
          </FormItem>
		    </TabPane>
		  </Tabs>

        </Form>
      </Modal>
    );
  }
}
const SftpForm = Form.create()(SFTP);
export default connect()(SftpForm);
