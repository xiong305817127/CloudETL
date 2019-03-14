import React from 'react';
import { connect } from 'dva';
import { Transfer,Button,Form,Card,Row,Col,Checkbox,Input } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;


class HadoopOutputDialog extends React.Component {
  state = {
    mockData: [],
    targetKeys: [],
  }

  componentWillReceiveProps(nextProps){

  }


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
  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };
  formItemLayout2 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 16 },
  }
  formItemLayout = {
    wrapperCol: { span:18},
  };

  render() {

    const { visible } = this.props.hadoopoutputmodel;
    const { getFieldDecorator } = this.props.form;

    return (
      <Modal
        title="Hadoop cluster"
        wrapClassName="vertical-center-modal  out-model"
        visible={visible}
        width={750}
        footer={[
            <Button key="submit" type="primary" size="large" >确定</Button>,
            <Button key="back" size="large" >取消</Button>,
        ]}
        maskClosable={false}
      >
        <FormItem label="Cluster Name" style={{marginBottom:"8px"}}  {...this.formItemLayout1}>
          {getFieldDecorator('text', {
            initialValue:"",
            rules: [{ whitespace:true, required: true, message: '请输入步骤名称' }]
          })(
            <Input />
          )}
        </FormItem>
        <FormItem  style={{marginBottom:"0px",marginLeft:"15%"}} {...this.formItemLayout}>
          {getFieldDecorator('includeRowNumber', {
            valuePropName: 'checked',
            initialValue:true
          })(
            <Checkbox >Use MapR client</Checkbox>
          )}
        </FormItem>
        <Card title="HDFS"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
          <Row  style={{padding:"10px 0"}}>
            <Col span={14}>
              <FormItem  label="Hostname" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                {getFieldDecorator('Hostname', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={10}>
              <FormItem  label="Port" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                {getFieldDecorator('Port', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row  style={{padding:"10px 0"}}>
            <Col span={14}>
              <FormItem  label="Username" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                {getFieldDecorator('Username', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={10}>
              <FormItem  label="Password" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                {getFieldDecorator('Password', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
          </Row>
        </Card>
        <Card title="JobTracker"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
          <Row  style={{padding:"10px 0"}}>
            <Col span={14}>
              <FormItem  label="Hostname" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                {getFieldDecorator('Hostname1', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={10}>
              <FormItem  label="Port" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                {getFieldDecorator('Port1', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
          </Row>
        </Card>
        <Card title="ZooKeeper"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
          <Row  style={{padding:"10px 0"}}>
            <Col span={14}>
              <FormItem  label="Hostname" style={{marginBottom:"0px"}} {...this.formItemLayout1}>
                {getFieldDecorator('Hostname2', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={10}>
              <FormItem  label="Port" style={{marginBottom:"0px"}}  {...this.formItemLayout1}>
                {getFieldDecorator('Port2', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
          </Row>
        </Card>
        <Card title="Oozie"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
          <Row  style={{padding:"10px 0"}}>
            <Col span={14}>
              <FormItem  label="URL" style={{marginBottom:"0px"}} {...this.formItemLayout2}>
                {getFieldDecorator('URL', {
                  initialValue:""
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
          </Row>
        </Card>
      </Modal>
    );
  }
}

const HadoopOutputModel = Form.create()(HadoopOutputDialog)

export default connect(({ hadoopoutputmodel })=>({
  hadoopoutputmodel
}))(HadoopOutputModel);
