import React from 'react';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import { Transfer,Modal,Button,Form,Card,Row,Col,Checkbox,Input,Select,message } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import Style from '../ResourceContent.css';
import Empower from '../../../../../components/Empower';
import { save_hadoop,checkHadoop_name } from '../../../../../services/gather';
import { strEnc,strDec } from 'utils/EncryptUtil';

let Timer;
class HadoopOutputDialog extends React.Component {
  state = {
    mockData: [],
    targetKeys: []
  };

  setModelHide(){
    const { dispatch } = this.props;
    this.setState({
      mockData: [],
      targetKeys: []
    });
    dispatch({
      type:"excelinputmodel/hide",
      visible:false,
    })
  }

  formItemLayout = {
    labelCol: { span: 3 },
    wrapperCol: { span: 14 },
  };
  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };

  formItemLayout2 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 12 },
  }

  getName(name){
    if(name === "hdfs"){
      return "HDFS";
    }else if(name === "wasb"){
      return "WASB";
    }else{
      return "MapR"
    }
  }

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return false;
      }

      if(values.password && values.password.length>0){
        values.password = strEnc(values.password,values.name,values.hostname,values.port);
      }

      save_hadoop(values).then((res)=>{
        const { code } = res.data;
        if( code === "200"){
          message.success("保存成功");
          this.handleCancel();
        }
      })
    });
  };
  handleConfirm = (rule, value, callback) => {
    const { config } = this.props.resourcecontent;
    if(value &&  value.trim() && value === config.name){
      callback();
    }else{
      if(value && value.trim()){
        if(Timer){
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(()=>{
          checkHadoop_name(value).then(( res)=>{
            const { code,data } = res.data;
            if(code === "200"){
              const {result} = data;
              if(result === true){
                callback(true)
              }else{
                callback()
              }
            }
          });
        },300);
      }else{
        callback()
      }
    }
  };


  handleCancel(){
    const form = this.props.form;
    form.resetFields();
    const { location,router } = this.props;
    router.push({...location,query:{}});
  }

  render() {
    const { config } = this.props.resourcecontent;
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { canEdit } = this.props;

    const setDisabled = ()=>{
      if(getFieldValue("storage") === undefined){
        return config.storage;
      }else{
        if(getFieldValue("storage")){
          return getFieldValue("storage");
        }
      }
    };


    return (
      <div className={Style.ServerCenter} style={{paddingLeft:"20px",paddingTop:"20px"}}>
      <div className={Style.divTabs}>
        <Form onSubmit={this.handleSubmit}>
          <div className={Style.HostServer}>
            <FormItem label="集群名称"   {...this.formItemLayout2}>
              {getFieldDecorator('name', {
                initialValue:config.name,
                rules: [{ required: true, message: '请输入集群名称' },
                  {validator:this.handleConfirm,message: '集群名称已存在，请更改!' }]
              })(
                <Input  disabled={config.name?true:false}/>
              )}
            </FormItem>
            <FormItem label="存储"  style={{marginBottom:"8px"}} {...this.formItemLayout2}>
              {getFieldDecorator('storage', {
                initialValue:config.storage?config.storage:"hdfs"
              })(
                <Select >
                  <Option value="hdfs">HDFS</Option>
                  <Option value="wasb">WASB</Option>
                  <Option value="maprfs">MapR</Option>
                </Select>
              )}
            </FormItem>
            <FormItem
              {...this.formItemLayout}
              label={this.getName( setDisabled())}
              style={{marginBottom:0}}
            >
            </FormItem>
            <Row  style={{padding:"10px 0"}}>
              <Col span={12}>
                <FormItem  label="主机名称" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('hostname', {
                    initialValue:config.hostname
                  })(
                    <Input  disabled={setDisabled() === "mapr"?true:false}/>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem  label="端口"  {...this.formItemLayout1}>
                  {getFieldDecorator('port', {
                    initialValue:config.port,
                    rules:[{ required: false,pattern:/^([1-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/, message: '请输入正确的端口号' }]
                  })(
                    <Input  disabled={setDisabled() === "mapr"?true:false} />
                  )}
                </FormItem>
              </Col>
            </Row>

            <Row >
              <Col span={12}>
                <FormItem  label="用户名" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('username', {
                    initialValue:config.username
                  })(
                    <Input  disabled={setDisabled() === "mapr"?true:false} />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem  label="密码" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                  {getFieldDecorator('password', {
                    initialValue:config.password?strDec(config.password,config.name,config.hostname,config.port):""
                  })(
                    <Input   disabled={setDisabled() === "mapr"?true:false}/>
                  )}
                </FormItem>
              </Col>
            </Row>
            <FormItem
              {...this.formItemLayout}
              label="JobTracker"
              style={{marginBottom:0}}
            >
            </FormItem>
            <Row  >
              <Col span={12}>
                <FormItem  label="主机名称" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('jobTracker.hostname', {
                    initialValue:config.jobTracker?config.jobTracker.hostname:""
                  })(
                    <Input  disabled={setDisabled() === "mapr"?true:false}/>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem  label="端口号" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                  {getFieldDecorator('jobTracker.port', {
                    initialValue:config.jobTracker?config.jobTracker.port:"",
                    rules:[{ required: false,pattern:/^([1-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/, message: '请输入正确的端口号' }]
                  })(
                    <Input  disabled={setDisabled() === "mapr"?true:false}/>
                  )}
                </FormItem>
              </Col>
            </Row>
            <FormItem
              {...this.formItemLayout}
              label="ZooKeeper"
              style={{marginBottom:0}}
            >
            </FormItem>
            <Row >
              <Col span={12}>
                <FormItem  label="主机名称" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                  {getFieldDecorator('zooKeeper.hostname', {
                    initialValue:config.zooKeeper?config.zooKeeper.hostname:""
                  })(
                    <Input />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem  label="端口号" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                  {getFieldDecorator('zooKeeper.port', {
                    initialValue:config.zooKeeper?config.zooKeeper.port:"",
                    rules:[{ required: false,pattern:/^([1-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/, message: '请输入正确的端口号' }]
                  })(
                    <Input />
                  )}
                </FormItem>
              </Col>
            </Row>
            <FormItem
              {...this.formItemLayout}
              label="Oozie"
              style={{marginBottom:0}}
            >
            </FormItem>
            <FormItem  label="URL" {...this.formItemLayout2}>
              {getFieldDecorator('url', {
                initialValue:config.url
              })(
                <Input />
              )}
            </FormItem>
            <Row className={Style.BottomRow} style={{bottom:"-27px"}}>
              <Col span={12}  style={{textAlign:"right"}}>
                <Empower api={canEdit?"/cloud/editHadoop.do":""} >
                  <Button type="primary" htmlType="submit" >
                    保存
                  </Button>
                </Empower>
              </Col>
              <Col span={12} style={{textAlign:"center"}}>
                <Button  onClick={this.handleCancel.bind(this)}>取消</Button>
              </Col>
            </Row>
          </div>
        </Form>
        </div>
      </div>
    );
  }
}

const HadoopClusterModel = Form.create()(HadoopOutputDialog);

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(HadoopClusterModel));
