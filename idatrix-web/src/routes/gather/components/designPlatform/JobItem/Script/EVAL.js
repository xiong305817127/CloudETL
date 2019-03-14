import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Checkbox} from 'antd';
import Modal from "components/Modal.js";
import AceEditor from 'react-ace';
import brace from 'brace';

import 'brace/mode/javascript';
import 'brace/theme/github';
import 'brace/ext/language_tools';

const FormItem = Form.Item;
const { TextArea } = Input;

class EVAL extends React.Component {

  constructor(props){
    super(props);
    const { config } = props.model;
    this.state = {
      textAreaValue:decodeURIComponent(config.script) != "null"?decodeURIComponent(config.script):""
    }
  }


  hideModal = () => {
    const { dispatch,form } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate(e){
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
         script:encodeURIComponent( this.state.textAreaValue)
      };
      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };

  onTextAreaChange(newValue){
    this.setState({
      textAreaValue:newValue
    });
  }

  render() {
     const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckJobName,config,nextStepNames,parallel } = this.props.model;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    return (
       <Modal
        visible={visible}
        title="使用JavaScript脚本验证"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
        onCancel={this.hideModal.bind(this)}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal.bind(this)}>取消</Button>,
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
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'11rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }

           <FormItem label="JavaScript"  {...formItemLayout} style={{marginBottom:"8px",display:'none'}}>
            {getFieldDecorator('script', {
              initialValue: decodeURIComponent(config.script) != "null"?decodeURIComponent(config.script):"",
            })(
               <TextArea   />
            )}
          </FormItem>
          <div style={{padding:"0px 35px 10px 35px"}}>
            <AceEditor
              mode="javascript"
              theme="github"
              onChange={this.onTextAreaChange.bind(this)}
              name="gather_tableInput"
              className="autoTextArea"
              showGutter={true}
              width={"100%"}
              height={"300px"}
              fontSize={16}
              editorProps={{$blockScrolling: true}}
              value={this.state.textAreaValue}
              wrapEnabled={true}
              setOptions={{
                  enableBasicAutocompletion: true,
                  enableLiveAutocompletion: true,
                  enableSnippets: false,
                  showLineNumbers: true,
                  tabSize: 2
             }}
            />
          </div>
        </Form>
      </Modal>
    );
  }
}
const EvalForm = Form.create()(EVAL);
export default connect()(EvalForm);
