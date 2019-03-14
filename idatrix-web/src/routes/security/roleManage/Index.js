/**角色管理2.0*/
//定义
import React from 'react';
import { connect } from 'dva';
import {  Tooltip, Popconfirm, Icon, Button, Form, Input, message  } from 'antd';
import { getList, newRole, roleDeleteApi, roleUpdateApi, roleJurisdictionApi, addUsersToRole } from '../../../services/roleManage';
import Search from '../../../components/Search';
import TableList from '../../../components/TableList';
import Empower from '../../../components/Empower';
import RoleEmpower from './RoleEmpower';
import RoleUsers from './RoleUsers';
import Modal from 'components/Modal';
const FormItem = Form.Item;
const { TextArea } = Input;
//渲染
class RoleManagementTable extends React.Component{
  //1.初始化
  constructor(props){
    super(props);
    this.columns =[
      {
        title: '角色名称',
        dataIndex: 'name',
      }, {
        title: '角色描述',
        dataIndex: 'remark',
        render:(text) => (<div className="word25" title={text}>{text}</div>),
      }, {
        title: '操作',
        className:"td-nowrap",
        render: (text, record, index) => (
          <span>
          <Empower api="/role/addUserToRole">
            <a onClick={()=>this.showModalMember(record, index)}>
        <Tooltip title="角色用户"><Icon type="user" className="op-icon"/></Tooltip>
      </a></Empower>&nbsp;&nbsp;
            <Empower api="/permission/addPermission2Role.shtml">
            <a onClick={()=>this.showRoleEmpower(record, index)}>
        <Tooltip title="角色授权"><Icon type="contacts" className="op-icon"/></Tooltip>
      </a></Empower>&nbsp;&nbsp;
            <Empower api="/role/update.shtml">
            <a onClick={()=>this.showModalThere(record, index)}>
        <Tooltip title="修改"><Icon type="edit" className="op-icon"/></Tooltip>
      </a></Empower>&nbsp;&nbsp;
            <Empower api="/role/deleteRoleById.shtml">
            <a>
        <Popconfirm title="你确定要删除吗？" okText="确定" cancelText="取消" onConfirm={() => this.onDelete(record, index)}>
          <Tooltip title="删除" ><Icon type="delete" className="op-icon"/></Tooltip>
        </Popconfirm>
      </a></Empower>
    </span>
        )
      }
    ];
    this.state = {
			//赋权禁用提交
			submitLoading:false,
      dataSource:null,
      pageNo:1,//第几页
      pageSize:10,//没有设置列表数目
      totalCount: 0,//页面总数
      loading: false,
      confirmLoading: false,
      newVisible: false,
      editVisible: false,
      usersVisible: false,
      roleVisible: false,
      selectedRowKeys: [],
      ids: [],//导出/下载
      fileName: '',//上传名称
      filePath: '',//上传路径
      fileLen: '',//上传字节大小
      roleId: '',//角色id
      findContent: '',
      editorFields: {
        id: '',
        name: '',
        remark: '',
      },
      usersTitle: '',
      hide: true
    }
  };
  componentDidMount(){
    this.reloadList();
  };
  //刷新页面：清空数据
  reloadList= () => {
    const obj = {};
    obj.pageNo = this.state.pageNo;//默认为第1页
    obj.pageSize = this.state.pageSize;//默认每页为10条
    obj.findContent = this.state.findContent;//搜索值
    getList(obj).then(res=>{
      console.log('传参+结果',obj,res);
      if(res && res.data){

        this.setState({
          dataSource:res.data.data.list,
          totalCount: parseInt(res.data.data.totalCount),
          ids: [],//导出/下载
          fileName: '',//上传名称
          filePath: '',//上传路径
          fileLen: '',//上传字节大小
          roleId: '',//角色id
          editorFields: {
            id: '',
            name: '',
            remark: '',
          },
          usersTitle: '',
          findContent: '',
          selectedRowKeys: [],
          hide:false
        })
      }
    })
  };
  //2.搜索：
  onSearch(findContent){
    // console.log('搜索：',findContent);
    this.setState({
      findContent: findContent,
      totalCount: 0,
      pageNo: 1,
      hide: true
    },()=>{
      this.reloadList();
    });
    
  };
  //3.对话框：新建
  newTableClick= () =>{
    this.props.form.resetFields();
    this.setState({
      newVisible: true,
      confirmLoading: false,
      editorFields: {},
    });
  };
  //新建：提交表单
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(!err){
        const obj = {};
        obj.name = values.name;
        obj.remark = values.remark;
        newRole(obj).then(res=>{
          // console.log('新建：',obj);
          this.setState({ confirmLoading: true });
          if(res.data && res.data.code === "200" ){
            this.setState({newVisible: false, confirmLoading: false});
            message.success(res.data.msg || res.data.resultMsg);
            this.reloadList();
          }else {
            //message.error(res.data.message || res.data.resultMsg);
          }
        })
      }
    });
  };
  //4.对话框：修改当前行id
  showModalThere = (record) => {
    // console.log('修改',record);
    this.props.form.resetFields();
    this.setState({
      editVisible: true,
      editorFields: {
        id: record.id,
        name: record.name,
        remark: record.remark,
      }
    });
  };
  //修改：确认
  editHandleSubmit = (e) =>{
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(!err){
        // console.log('修改：角色名称',values.name);
        const obj = {};
        obj.id = this.state.editorFields.id;
        obj.name = values.name;
        obj.remark = values.remark;
        roleUpdateApi(obj).then(res=>{
            // console.log('修改参数：',res);
            if(res.data && res.data.code === "200" ){
              this.setState({editVisible: false});
              message.success(res.data.msg || res.data.resultMsg);
              this.reloadList();
            }
          })
      }
    })
  };
  //5.删除行：
  onDelete(record) {
    const obj = { ids : record.id };
    roleDeleteApi(obj).then(res => {
      if (res.data.code === "200") {
        message.success(res.data.msg || res.data.resultMsg);
        this.reloadList();
      } else {
        //message.error(res.data.message || res.data.resultMsg);
      }
    });
  }
  //6.其他：选中操作，数组转换成字符串
  onSelectChange = (selectedRowKeys) => {
    this.setState({
      ids:selectedRowKeys.join(","),
      selectedRowKeys:selectedRowKeys,
    });
  };
  // 提交用户
  submitUsers = async (roleId, uIds) => {
    const { data } = await addUsersToRole({ roleId, uids: uIds.join(',') });
    if (data.code === "200") {
      this.reloadList();
      this.setState({ usersVisible: false });
    } else {
      //message.error(data.message || data.resultMsg);
    }
  };
  //跳转分页：
  onChange = (page) => {
    // console.log('跳转分页:',page);
    this.setState({
      pageNo:page.current,
      pageSize: page.pageSize,
      totalCount: page.total,
    }, ()=> {
      this.reloadList();
    });
  };
  //成员对话框
  showModalMember = (record) => {
    this.setState({
      usersTitle: `角色【${record.name}】中的用户`,
      usersVisible: true,
      roleId: record.id,
    });
  };
  //对话框：角色授权
  showRoleEmpower = (record) => {
    this.setState({
      roleVisible: true,
      roleId: record.id,
    });
  };
  // 授权组件:角色授权ids,选中角色roleId
  handleEmpower = (ids) => {
		this.setState({ submitLoading:true });
    console.log('选择的授权ids2222222:', ids);
    const obj = {};
    obj.ids = ids.join(',');
    obj.roleId = this.state.roleId;
    roleJurisdictionApi(obj).then(res=>{
      // console.log('提交的选择的授权ids:', obj);
      // console.log('授权结果:',res);
      if(res.data && res.data.code === "200"){
        this.setState({roleVisible: false, submitLoading:false});
        message.success(res.data.msg);
        this.reloadList();
      }else {
        //message.error(res.data.message);
      }
    })
  };
  render(){
    const columns = this.columns;
    const dataSource = this.state.dataSource;
		const { getFieldDecorator } = this.props.form;
		const { submitLoading } = this.state;
    return(
      <div style={{ backgroundColor: '#fff' }}>
        {/*1.角色搜索*/}
        <header style={{padding: '20px 50px'}}>
          <Search onSearch={value => this.onSearch(value)} placeholder="可以按角色名称进行模糊搜索"/>
        </header>
        {/*2.角色新建：按钮*/}
        <section style={{marginLeft:10}}>
          <Empower api="/role/add.shtml">
            <Button onClick={this.newTableClick} type='primary'>新建</Button>
          </Empower>
        </section>
        {/*3.角色列表：*/}
        {
          !this.state.hide
          &&
          <TableList
            className="h-nowrap "
            showIndex
            rowKey={record => record.id}
            columns={columns}
            dataSource={dataSource}
            pagination={{current:1,total:this.state.totalCount}}
            onChange={this.onChange}//分页
            rowSelection={{
              selectedRowKeys:this.state.selectedRowKeys,//被选默认为空数组
              onChange: this.onSelectChange,
            }}
            style={{margin: 10}}
          />
        }

        {/*对话框:新建/修改*/}
        <Modal
          maskClosable={false}
          title={this.state.newVisible?"新建角色":"修改角色"}
          visible={this.state.newVisible||this.state.editVisible}
          // onOk={this.handleSubmit}
          onCancel={() => this.setState({ newVisible: false,editVisible: false })}
          confirmLoading={this.state.confirmLoading}
          footer={[
            <Button key="back" size="large" onClick={() => this.setState({ newVisible: false ,editVisible: false})}>取消</Button>,
            <Button key="submit" type="primary" size="large"  onClick={this.state.newVisible?this.handleSubmit:this.editHandleSubmit}>
              确定
            </Button>,
          ]}
        >
          <Form >
            <FormItem
              label="角色名称"
              labelCol={{span: 5}}
              wrapperCol={{ span: 18}}
            >
              {getFieldDecorator('name', {
                initialValue: this.state.editorFields.name,
                rules: [{ required: true,  message: '请输入角色名称!' },{  message: '只能允许中文、数字、字母和下划线作为角色名称' ,pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/}]
              })(
                <Input  placeholder="角色的称谓" spellCheck={false} maxLength="32"/>
              )}
            </FormItem>

            <FormItem
              label="角色描述"
              labelCol={{span: 5}}
              wrapperCol={{ span: 18}}
            >
              {getFieldDecorator('remark', {
                initialValue: this.state.editorFields.remark,
                rules: [{ required: false, message: '请输入角色描述!' }]
              })(
                <TextArea rows={4} placeholder="角色的描述" spellCheck={false} maxLength="200"/>
              )}
            </FormItem>

          </Form >
        </Modal>
        {/*对话框3：角色用户*/}
        <RoleUsers
          title={this.state.usersTitle}
          visible={this.state.usersVisible}
          onCancel={() => this.setState({ usersVisible: false })}
          onOk={this.submitUsers}
          id={this.state.roleId}
        />
        {/*对话框4：角色授权*/}
        <RoleEmpower
          visible={this.state.roleVisible}
          title="角色授权"
          id={this.state.roleId}
					onOk={this.handleEmpower}
					submitLoading = {submitLoading}
          onCancel={() => this.setState({ roleVisible: false })}
        />
      </div>
    )
  }
};
//调用
const WrappedApp = Form.create()(RoleManagementTable);
export default connect(({ system, roleManage }) => ({
  system,
  roleManage,
}))(WrappedApp);
