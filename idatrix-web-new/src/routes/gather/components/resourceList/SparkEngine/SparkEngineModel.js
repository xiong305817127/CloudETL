import React from 'react';
import { connect } from 'dva';
import { Transfer,Modal,Button,Form,Card,Row,Col,Checkbox,Input,Select,message } from 'antd';
import Style from '../ResourceContent.css'
import { getsaveCluster,check_SparkName } from '../../../../../services/gather';
import {withRouter} from 'react-router';
import Empower from '../../../../../components/Empower';

const FormItem = Form.Item;
const { TextArea } = Input;
let Timer;
class SparkDialog extends React.Component {
  state = {
    mockData: [],
    targetKeys: [],
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

  formItemLayout2 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 12 },
  }


  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return false;
      }
      getsaveCluster(values).then((res)=>{
        const { code } = res.data;
        if( code === "200"){
          message.success("保存成功");
          this.handleCancel();
        }
      })
    });
  };
  handleConfirm = (rule, value, callback) => {

    const { config } = this.props;
    if(value &&  value.trim() && value === config.name){
      callback();
    }else{
      if(value && value.trim()){
        if(Timer){
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(()=>{
          check_SparkName(value).then(( res)=>{
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
  }


  handleCancel(){
    const form = this.props.form;
    form.resetFields();
    const { location,router } = this.props;
    router.push({...location,query:{}});
  };

  render() {
    const { config } = this.props.resourcecontent;
    const { getFieldDecorator } = this.props.form;


    return (
      <div className={Style.ServerCenter} style={{paddingLeft:"20px",paddingTop:"20px"}}>
      <div className={Style.divTabs}>
        <Form onSubmit={this.handleSubmit}>


            <FormItem label="名称"   {...this.formItemLayout2}>
              {getFieldDecorator('name', {
                initialValue:config.name,
                rules: [{ required: true, message: '请输入引擎名称' }]
              })(
                <Input  disabled={config.name?true:false}/>
              )}
            </FormItem>

            <FormItem  label="路径" {...this.formItemLayout2}>
              {getFieldDecorator('url', {
                initialValue:config.url,
              })(
                <Input />
              )}
            </FormItem>

            <FormItem label="描述"   {...this.formItemLayout2}>
              {getFieldDecorator('desc', {
                initialValue:config.description,
              })(
                <TextArea />
              )}
            </FormItem>
        </Form>

        </div>
            <Row className={Style.BottomRow}>
                  <Col span={12}  style={{textAlign:"right"}} >
                    <Empower api="/cloud/editSpark.do" >
                      <Button type="primary" disabled={true} htmlType="submit" >
                        保存
                      </Button>
                    </Empower>
                  </Col>
                  <Col span={12} style={{textAlign:"center"}}>
                    <Button  onClick={this.handleCancel.bind(this)}>取消</Button>
                  </Col>
              </Row>
      </div>
    );
  }
}

const SparkEngineModel = Form.create()(SparkDialog);

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(SparkEngineModel));
