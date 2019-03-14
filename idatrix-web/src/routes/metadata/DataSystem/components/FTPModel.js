import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input, Radio,Select,Col,Row,Cascader,message } from 'antd';
const FormItem = Form.Item;
const TextArea = Input;
import { insert_ftp_table_fields,update_ftp_table_fields,get_front_pos,get_platform_HDFStree,get_ftp_table_fiele_get } from '../../../../services/metadata'
import Style from './DatabaseModel.css'
import Modal from 'components/Modal';
import { submitDecorator } from 'utils/decorator';
let Timer;
@submitDecorator
class Model extends React.Component{

  constructor(){
    super();

    this.state = {
      radioValue:"1",
      data:[],
      FESId: '',
      hdfspath:''
    }

    this.get_pos_list = this.get_pos_list.bind(this)
  }


  get_pos_list(){

    console.log("触发？？？")
    get_front_pos().then(( res)=>{
      // console.log(res.data.data.rows,"ddddd");
        const data = res && res.data && res.data.data && res.data.data.rows || [];
        this.setState({ data });
      });
  }

  componentWillReceiveProps(nextProps) {
    const { dispatch, account } = this.props;
    if (nextProps.ftpmodel.visible && !this.props.ftpmodel.visible) {
      dispatch({ type: 'metadataCommon/getUsers' });
      this.setState({FESId: ''});
    }
  }
   /*检测同一个前置机下面不能有多个用户名名称*/
  handleUserNameScarch = (rule,value, callback) => {
 console.log(hdfspath.value,"hdfspath");
    console.log(this.props,"/////",this.state.hdfspath);

    const { info } = this.props.ftpmodel;
    if(value && value !== info.ftpUser ||  value !== info.hdfspath){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.ftpUser = value;
        obj.hdfspath = hdfspath.value;
        get_ftp_table_fiele_get(obj).then(( res)=>{
           const { data } = res.data;
          if(res.data.data.rows.length === 0){
            callback()
          }else{
            callback(true);
            message.error("当前用户名已创建，请重新选择");
          }

        });
      },300);
    }else{
      callback()
    }
  };

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return
      }
      this.props.disableSubmit();
      const { dispatch, account } = this.props;
      const { model } = this.props.ftpmodel;
      values.status = 0;
      values.renterId = account.renterId;
      if(model === "newmodel"){
        insert_ftp_table_fields(values).then((res)=>{
          this.props.enableSubmit();
          console.log(res,"res");
          if(res.data.code === "200"){
            message.success("新增成功");
          }else if(res.data.code === '700'){
               message.error("新增失败,请重新选择用户名和前置机");
          }else{
            message.error(res.data.msg);
          }
          dispatch({
            type:'datasystemsegistration/changeView',
            payload:{
              actionKey:"updatemodel"
            }
          })
        })
      }else if(model === "editmodel"){
        const { info } = this.props.ftpmodel;
        values.id = info.id;
        let serverName = info.serverName;
        if(serverName === values.serverId){
          values.serverId = info.frontEndServer.id;
        }
        update_ftp_table_fields(values).then((res)=>{
          this.props.enableSubmit();
          if(res.data.code === "200"){
               dispatch({
                type:'datasystemsegistration/changeView',
                payload:{
                  actionKey:"updatemodel"
                }
              })
          }else{
            message.error("修改失败");
          }

        })
      }
       this.props.enableSubmit();
      this.hideModel();
    });
  }

  hideModel(){
      const {dispatch,form} = this.props;
      dispatch({
        type:"ftpmodel/hide",
        visible:false
      })
     
      form.resetFields();
  }

  formItemLayout1 = {
    labelCol: { span: 4},
    wrapperCol: { span: 16 },
  };
  formItemLayout2 = {
    labelCol: { span: 10},
    wrapperCol: { span: 14 },
  };
  formItemLayout5 = {
    labelCol: { span: 6},
    wrapperCol: { span: 16 },
  };
  formItemLayout3 = {
    labelCol: { span: 5},
    wrapperCol: { span:19},
  };

  formItemLayout4 = {
    labelCol: { span: 6},
    wrapperCol: { span:17},
  };

  handleRadioChange(e){
    this.setState({
      radioValue:e.target.value
    })
  }

  // 切换前置机
  handleChangeFES(val) {
    this.setState({ FESId: val });

  }

  render(){
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { visible,info } = this.props.ftpmodel;
    const { account, metadataCommon } = this.props;
    const FESId = this.state.FESId ? this.state.FESId : info && info.frontEndServer && info.frontEndServer.id || '';
    const ftpUser = getFieldValue('ftpUser');
    const ftpUserId = (metadataCommon.usersOptions.find(it => it.label === ftpUser) || {}).value || account.id;
    let hdfspath = info.hdfspath ? info.hdfspath : `/${account.renterId}/SFTP/${FESId}/${ftpUserId}`;
    return(
      <Modal
        visible={visible}
        title="前置机SFTP用户基本信息"
        wrapClassName="vertical-center-modal DatabaseModel"
        width={600}
        maskClosable={false}
        footer={[
          <Button key="back" size="large" onClick={()=>{this.hideModel()}}>取消</Button>,
          <Button key="submit" type="primary" size="large"  onClick={this.handleSubmit} loading={this.props.submitLoading}>确定</Button>,
          ]}
        onCancel={()=>{this.hideModel()}}>`
        <Form onSubmit={this.handleSubmit} >
         <FormItem   label="所在前置机: "  {...this.formItemLayout4} >
            {getFieldDecorator('serverId', {
              initialValue:info.serverName,
              rules: [{ required: true, message: '请输入所在前置机名称' }]
            })(
              <Select placeholder="请选择所在前置机" onChange={this.handleChangeFES.bind(this)} autoFocus onFocus={this.get_pos_list}>
                {
                  this.state.data.map((index)=>
                    <Select.Option key={index.id} value={index.id+""}>{index.serverName}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <Row>
            <Col span={24}>
              <FormItem   label="用户名: "  {...this.formItemLayout4} >
                {getFieldDecorator('ftpUser', {
                  initialValue:info.ftpUser,
                  rules: [{ required: true, message: '请输入用户名' }]})(
                  <Select disabled={!!info.id || this.state.FESId == ""}>
                    {metadataCommon.usersOptions.map(option => (
                      <Select.Option key={option.label}>{option.label}</Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            {/*<Col span={12}>
             <FormItem   label="密码: "  {...this.formItemLayout5} >
             {getFieldDecorator('ftpPassword', {
             initialValue:info.ftpPassword,
             rules: [{ required: true, message: '请输入密码' }]
             })(
             <Input disabled={this.state.FESId == ""} type="password"/>
             )}
             </FormItem>
             </Col>*/}
          </Row>

          {/*<FormItem   label="一级目录: "  {...this.formItemLayout4} >
            {getFieldDecorator('oneLevel', {
              // initialValue:info.oneLevel,
              initialValue:info.hdfsFileDirectory ? info.hdfsFileDirectory.allPathname : '',
              rules: [{ required: true, message: '请输入一级目录' }]
            })(
              <Input readOnly />
            )}
          </FormItem>*/}
          <FormItem   label="对应的HDFS路径: "  {...this.formItemLayout4} >
            {getFieldDecorator('hdfspath', {
              initialValue:hdfspath,
              rules: [{ required: true, message: '请选择HDFS路径' }]
            })(
              <Input disabled maxLength="255"/>
            )}
          </FormItem>
          <FormItem  label="备注：" {...this.formItemLayout4} style={{marginBottom:"8px"}} >
            {getFieldDecorator('remark',{
              initialValue: info.remark,
            })(<TextArea  maxLength="200" rows={4} spellCheck={false}/>)}
          </FormItem>
        </Form>
      </Modal>
    )
  }
}
const FTPModel = Form.create()(Model);
export default connect(({ ftpmodel, account, metadataCommon }) => ({
  ftpmodel,
  account,
  metadataCommon,
}))(FTPModel)
