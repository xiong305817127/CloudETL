import React from 'react'
import { connect } from 'dva'
import { Form,Input,Select,Radio } from 'antd'
import Modal from "components/Modal.js";
const FormItem = Form.Item
const Option = Select.Option
import { getSpark_list } from '../../../../../services/gather'

class DomConfigModel extends React.Component{


  setModal1Hide = ()=>{
    const { dispatch,form } = this.props;
    dispatch({
      type:'domconfig/hide'
    });
    form.resetFields();
  };

  handleCreate = () => {
    const { form,dispatch} = this.props;
    const { stepName,transName} = this.props.domconfig;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }
     /* if(values.distribute === "RoundRobin"){
          values.distribute = true;
        }else{
          values.distribute = false;
        }*/

      let obj = {
        "transName": transName,
        "stepName":stepName,
        "configs": {
          "clusterSchema": values.clusterSchema,// 集群配置
        }
      };

      dispatch({
        type:'domconfig/saveList',
        obj:obj
      });

        form.resetFields();
    });
  };

  handleFocus(){
    const { dispatch } = this.props;
    dispatch({
      type:"domconfig/queryList",
      domInfo:{}
    })
  }

  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
 onChange = (e) => {
    this.setState({
     value:e.target.value
    });
  };
  render(){
    const { visible,configs,clusterList } = this.props.domconfig;
    const { getFieldDecorator } = this.props.form;
     console.log(configs.distribute ?"RoundRobin":"copy","configsconfigsconfigs");
     console.log(configs.distribute,"ssssssssssssssssssssssssss");
    return(
      <Modal
        title="配置"
        wrapClassName="vertical-center-modal"
        visible={visible}
        onOk={this.handleCreate.bind(this)}
        onCancel={this.setModal1Hide.bind(this)}
      >
          <Form>
            <FormItem
              {...this.formItemLayout}
              label="集群名称"
              hasFeedback
              style={{marginBottom:"8px"}}
              >
                  {getFieldDecorator('clusterSchema', {
                    initialValue: configs.clusterSchema
                  })(
                    <Select placeholder="请选择spark集群" allowClear={true} onFocus={this.handleFocus.bind(this)}>
                      {
                        clusterList.map((index)=>
                          <Option  key={index.name} value={index.name}>{index.name}</Option>
                        )
                      }
                    </Select>
                  )}
              </FormItem>
          </Form>
      </Modal>
    )
  }
}

const DomConfig = Form.create()(DomConfigModel);

export default connect(({ domconfig }) => ({
  domconfig
}))(DomConfig)


/*
*  <FormItem
 {...this.formItemLayout}
 label="数据发送"
 style={{marginBottom:"8px"}}
 >
 </FormItem>
 <FormItem  label="发送模式"  {...this.formItemLayout}>
 {getFieldDecorator('distribute', {
 initialValue:configs.distribute?"RoundRobin":"copy",
 onChange:this.onChange.bind(this)
 })(
 <Radio.Group  style={{ marginBottom: 8}}>
 <Radio.Button value="RoundRobin">Round-Robin</Radio.Button>
 <Radio.Button value="copy">复制发送</Radio.Button>
 </Radio.Group>
 )}
 </FormItem>

 *
* */
