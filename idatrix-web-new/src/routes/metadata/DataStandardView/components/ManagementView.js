/**
 * Created by Administrator on 2017/8/24.
 */
import  React  from 'react';
import { Layout,Button,Icon,Popconfirm, messag,Tooltip,TreeSelect,Row, Col,Form,Input,Tabs,Radio  } from  'antd';
const TreeNode = TreeSelect.TreeNode;
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
import { connect } from 'dva';
import TableList from "../../../../components/TableList";
import Style from '../components/ConleStyle.css';
import Modal from 'components/Modal';
const { TextArea } = Input;
class Platform extends React.Component{
constructor(props) {
    super(props);
    this.state = {
      mode: '0',
    };
  }
  handleModeChange = (e) => {
    const mode = e.target.value;
    this.setState({ mode });
  }
  onChange = (value) => {
    console.log(arguments);
    this.setState({ value });
  }
   handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
      }
    });
  }
 columns = [
   {
      title: '名称',
      dataIndex: 'size',
      key: 'size',
      width: '30%',
    }, {
      title: '版本',
      dataIndex: 'createTime',
      key: 'createTime',
      width: '30%',
    }
    ,{ title: '说明',
      dataIndex: 'modifier',
      key: 'modifier',
      width: '30%',
    }];
handleModeChange = (e) =>{
    const mode =e.target.value;
    this.setState({ mode });
    this.setState({
      visible: true,
    })
    console.log(mode,"mode");
  };
       formItemLayout1 = {
	      labelCol: { span: 3 },
	      wrapperCol: { span: 18 },
	    };

      formItemLayout = {
	      labelCol: { span: 3 },
	      wrapperCol: { span: 10 },
	    };

   showModals(mode){
   	const { getFieldDecorator } = this.props.form;
    console.log(mode,"11");
    if(mode === "1"){
    	return(
    		<div>
                <FormItem
		          {...this.formItemLayout1}
		          label="上级分类">
		          <span className="ant-form-text">数据标准名称</span>
		        </FormItem>
		        <FormItem {...this.formItemLayout} label="分类名称">
		          {getFieldDecorator('nickname', {

		          })(
		            <Input placeholder="请输入上级分类名称" />
		          )}
		        </FormItem>

		         <FormItem {...this.formItemLayout} label="版本">
		          {getFieldDecorator('nickname', {

		          })(
		            <Input placeholder="请输入版本名称" />
		          )}
		        </FormItem>

		        <FormItem {...this.formItemLayout} label="说明">
                <TextArea
                  ref="remark"
                  name="remark"
                  placeholder=""
                  autosize={{ minRows: 3, maxRows: 6 }}
                  rows={4}
                  spellCheck={false}
                   maxLength="200"
                />
              </FormItem>

    		</div>
    		)
    }else if(mode === "2"){
        return(
    		<div>
                <FormItem
		          {...this.formItemLayout1}
		          label="上级分类">
		          <span className="ant-form-text">数据标准名称</span>
		        </FormItem>
		        <FormItem {...this.formItemLayout} label="分类名称">
		          {getFieldDecorator('nickname', {

		          })(
		            <Input placeholder="请输入上级分类名称" />
		          )}
		        </FormItem>

		         <FormItem {...this.formItemLayout} label="版本">
		          {getFieldDecorator('nickname', {

		          })(
		            <Input placeholder="请输入版本名称" />
		          )}
		        </FormItem>

		        <FormItem {...this.formItemLayout} label="说明">
                <TextArea
                  ref="remark"
                  name="remark"
                  placeholder=""
                  autosize={{ minRows: 3, maxRows: 6 }}
                  rows={4}
                  spellCheck={false}
                   maxLength="200"
                />
              </FormItem>

    		</div>
    		)
    }else if(mode === "3"){
        return(
    		<div>
                <FormItem
		          {...this.formItemLayout1}
		          label="上级分类">
		          <span className="ant-form-text">数据标准名称</span>
		        </FormItem>
		        <FormItem {...this.formItemLayout} label="分类名称">
		          {getFieldDecorator('nickname', {

		          })(
		            <Input placeholder="请输入上级分类名称" />
		          )}
		        </FormItem>

		         <FormItem {...this.formItemLayout} label="版本">
		          {getFieldDecorator('nickname', {

		          })(
		            <Input placeholder="请输入版本名称" />
		          )}
		        </FormItem>

		        <FormItem {...this.formItemLayout} label="说明">
                <TextArea
                  ref="remark"
                  name="remark"
                  placeholder=""
                  autosize={{ minRows: 3, maxRows: 6 }}
                  rows={4}
                  spellCheck={false}
                   maxLength="200"
                />
              </FormItem>

    		</div>
    		)
    }else if(mode === "0"){
    	 return(
           <div>
                包含分类：
                  <TableList
		            showIndex
		            style={{margin:"10px"}}
		             onRowClick={()=>{return false}}
		            ref="editTable"
		            columns={this.columns}
		            className="th-nowrap "
		          />
    	 	</div>
    	 	)
    }
   }


  render(){
      const { platformServerInfo } = this.props.frontendfesmanage;
      const { getFieldDecorator } = this.props.form;
       const { mode } = this.state;
      const formItemLayout = {
	      labelCol: { span: 6 },
	      wrapperCol: { span: 14 },
	    };

    return(
      <section style={{backgroundColor: '#fff', padding:10, marginTop:20}}>
       <Form onSubmit={this.handleSubmit}>
        <Row>
           <Col span={5} style={{padding:"10px"}}>
             <TreeSelect
		        showSearch
		        style={{ width: 200 }}
		        value={this.state.value}
		        dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
		        placeholder="请输入关键字查找"
		        allowClear
		        treeDefaultExpandAll
		        onChange={this.onChange}
		      >
		        <TreeNode value="parent 1" title="parent 1" key="0-1">
		          <TreeNode value="parent 1-0" title="parent 1-0" key="0-1-1">
		            <TreeNode value="leaf1" title="my leaf" key="random" />
		            <TreeNode value="leaf2" title="your leaf" key="random1" />
		          </TreeNode>
		          <TreeNode value="parent 1-1" title="parent 1-1" key="random2">
		            <TreeNode value="sss" title={<b style={{ color: '#08c' }}>sss</b>} key="random3" />
		          </TreeNode>
		        </TreeNode>
		      </TreeSelect>
		    </Col>
		    <Col span={19} style={{borderLeft:"1px #ccc solid"}}>
		       <Row>
		          <Col span={4} style={{textAlign:"center",marginTop:"10px"}}>
			       数据项详情:
			       </Col>
			       <Col span={20} className={Style.DataNone}>

                   <Radio.Group onChange={this.handleModeChange} value={mode} style={{ marginBottom: 8 }}>
			          <Radio.Button value="1">新增分类</Radio.Button>
			          <Radio.Button value="2">新增数据项</Radio.Button>
			          <Radio.Button value="3">编辑</Radio.Button>
			          <Radio.Button value="4">保存</Radio.Button>
			          <Radio.Button value="5">删除</Radio.Button>
			        </Radio.Group>

                    {
                    	this.showModals(mode)
                    }
			       </Col>

		        </Row>




			 </Col>
		</Row>
	  </Form>
     </section>
    )
  }
}
const WrappedDemo = Form.create()(Platform);
export default connect(({ frontendfesmanage }) => ({
  frontendfesmanage,
}))(WrappedDemo);
