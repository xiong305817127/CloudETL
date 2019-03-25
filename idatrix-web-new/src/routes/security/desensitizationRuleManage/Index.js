/**敏感规则2.0版本*/
//一：引入组件
import React from 'react';
import { connect } from 'dva';
import { Form, Button, Popconfirm, Icon, Tooltip, message, Input, Upload, Transfer, Radio, Row, Col } from 'antd';
import TableList from '../../../components/TableList';
import Search from '../../../components/Search';
import Modal from '../../../components/Modal';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
import Empower from '../../../components/Empower'; // 导入授权组件
import { API_BASE_SECURITY } from './../../../constants';
import { getSensitiveInfoList , addSensitiveInfo, updateSensitiveInfo, deleteSensitiveInfo } from './../../../services/service';
import { deepCopy , downloadFile } from '../../../utils/utils';
//二：渲染
class DesensitizationRuleTable extends React.Component {
  //初始化整合
  constructor(props) {
    super(props);
    this.state = {
      //表格参数、数据
      pageNo: 1,//当前分页
      pageSize: 10,//每页条数
      totalCount: null,//总页数total
      selectedRowKeys: [],//全选
      ids: '',//批量导出,拼接id
      dataSource:[],//新建、修改、删除
      editDataSource: [],
      newVisible: false,
      editVisible:false,
      value: 1,//单选切换：默认是
      name:'',
      isFixedLength:'',
      begin:'',
      end:'',
      symbol:'',
      originalInfo:'',
      sentiveInfo:'',
      //搜索内容、删除id
      findContent: '',
      allList: '',
      id: '',
      //其他：
    }
    //自定义表格标题、模拟数据
    this.columns = [
      {
        title: '敏感词类别',
        dataIndex: 'name',
        className:"td-center",
        width:30,
      }, {
        title: '长度是否固定',
        dataIndex: 'isFixedLength',
        className:"td-center",
        width:30,
        render: (text, record, index) => (text==="Y"?"是":"否")
      }, {
        title: '脱敏规则',
        children: [{
          title: '开始位置',
          dataIndex: 'begin',
          width:50,
          className:"td-center"
        }, {
          title: '结束位置',
          dataIndex: 'end',
          width:50,
          className:"td-center"
        }, {
          title: '脱敏字符',
          dataIndex: 'symbol',
          width:50,
          className:"td-center"
        }],
      }, {
        title: '敏感词样例',
        dataIndex: 'originalInfo',
        className:"td-center",
        width:300,
        render:(text) => (<div className="td-center" title={text}>{text}</div>),
      }, {
        title: '脱敏后信息',
        dataIndex: 'sentiveInfo',
        className:"td-center",
        width:300,
        render:(text) => (<div className="td-center" title={text}>{text}</div>),
      }, {
        title: '创建人',
        dataIndex: 'creater',
        className:"td-center",
        width:50,
      }, {
        title: '创建人部门',
        dataIndex: 'deptName',
        className:"td-center",
        width:50,
      }, {
        title: '操作',
        width:50,
        className:"td-nowrap td-center",
        render: (text, record, index) => (
          // console.log('操作数据：',record)
          <span>
            <Empower api="/member/update.shtml">
              <a onClick={()=>this.showEditModal(record, index)} style={{marginRight:10}}>
                <Tooltip title="修改" ><Icon type="edit" className="op-icon"/></Tooltip>
              </a>
            </Empower>
            <Empower api="/member/delete.shtml">
              <a>
                <Popconfirm title="你确定要删除吗？" okText="确定" cancelText="取消" onConfirm={() => this.onDelete(record, index)}>
                  <Tooltip title="删除" >
                      <Icon type="delete" className="op-icon"/>
                  </Tooltip>
                </Popconfirm>
              </a>
            </Empower>
          </span>
        )
      }];
    this.dataSource = [];
    for (let i = 0; i < 100; i++) {
      this.dataSource.push({
        key: i,
        name: '姓名',
        isFixedLength: '是',
        begin: '1',
        end: '3',
        symbol: '*',
        originalInfo: 'Lake Street 42',
        sentiveInfo: '***e Street 42',
        creater: 'root',
        deptName: '研发部门',
      });
    }
  };
  //生命周期
  componentWillMount(){
    this.reloadList();
  };
  //1.预加载/搜索内容
  reloadList(){
    //定义一个常量对象参数（设置搜索条件，如当前页、每页条数、关键字搜索）
    const obj ={
      pageNo: this.state.pageNo,//点击分页器的第几页，传给后台就是当前页current
      pageSize: this.state.pageSize,
      // totalCount: this.state.totalCount,//总条数不需要传给后台,反而要从后台获取
      findContent:this.state.findContent,
    };
    getSensitiveInfoList(obj).then(res => {
      //打印返回结果
      console.log(res);
      if(res && res.data){
        this.setState({
          dataSource: res.data.list, //刷新表格内容
          totalCount: parseInt(res.data.totalCount), //这里建议使用parseInt()处理总条数
          findContent: '',//刷新后清空表单
          name:'',
          isFixedLength:'',
          begin:'',
          end:'',
          symbol:'',
          originalInfo:'',
          sentiveInfo:'',
          value:'',
        })
      }
    })
  }
  //搜索:
  handleSearch=(findContent)=> {
    console.log('搜索值',findContent);
    this.state.findContent = findContent;
    this.reloadList();
  };
  //2.新建:务必清空表单,初始化有操作的状态
  handleNewContent() {
    // console.log('清空表单',111);
    this.props.form.resetFields();//无效
    this.setState({
      newVisible: true,
      ids: [],
      name:'',
      isFixedLength:'',
      begin:'',
      end:'',
      symbol:'',
      originalInfo:'',
      sentiveInfo:'',
      value:'',
    });
    // console.log('清空表单',222);
  };
  //新建：
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err, values) => {
      if (!err) {
        // console.log('提交前的表单数据', values);单选需切换转换
        const obj = {};
        obj.name = values.name;
        obj.isFixedLength = values.isFixedLength===1?'Y':'N';
        obj.begin = values.begin;
        obj.end = values.end;
        obj.symbol = values.symbol;//字符
        obj.originalInfo = values.originalInfo;//样例
        // obj.pswd = MD5(`#${values.pswd}`);
        addSensitiveInfo(obj).then(res=>{
          console.log('新建表单数据', obj);
          if(res.data && res.data.code === "200"){
            this.setState({newVisible: false});
            message.success(res.data.msg || res.data.resultMsg);
            this.reloadList()
          }else {
            //错误提示：自定义
            // message.error(res.data.message || res.data.resultMsg);
          }
        });
      }
    });
  };
  //3.修改：单选
  showEditModal=(e)=>{
    console.log('修改前获取当前id+清空表单再赋值',e);
    this.state.id = e.id;
    this.props.form.resetFields();//清空有效
    this.setState({
      editVisible: true,
      name:e.name,
      // isFixedLength:e.isFixedLength==="Y"?1:2,
      value:e.isFixedLength === "Y"?1:2,//控制选中
      begin:e.begin,
      end:e.end,
      symbol:e.symbol,
      originalInfo:e.originalInfo,
      sentiveInfo:e.sentiveInfo,
    });
  };
  onChangeRadio = (e) => {
    console.log('单选', e.target.value);
    this.setState({
      value: e.target.value,
    });
  }
  editHandleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(!err){
        console.log('修改当前行id',values);
        const obj = {};
        obj.id = this.state.id;
        obj.name = values.name;
        obj.isFixedLength = values.isFixedLength===1?"Y":"N";
        obj.begin = values.begin;
        obj.end = values.end;
        obj.symbol = values.symbol;
        obj.originalInfo = values.originalInfo;// sentiveInfo脱敏后信息不可修改
        updateSensitiveInfo(obj).then(res=>{
            console.log('修改参数+返回结果：',obj,res);
            if(res.data && res.data.code === "200" ){
              this.setState({editVisible: false});
              message.success(res.data.msg || res.data.resultMsg);
              this.reloadList();
            }else {
              //错误提示：自定义
              //message.error(res.data.message || res.data.resultMsg);
            }
          })
      }
    })
  };
  //4.删除当前列：id
  onDelete=(d)=>{
    console.log('获取当前列id',d);
    //  直接删除对应的id
    const obj = { id : d.id };
    deleteSensitiveInfo(obj).then(res => {
      console.log('删除后返回结果：',res);
      if (res.data.code === "200") {
        message.success(res.data.msg || res.data.resultMsg);
        //  刷新列表
        this.reloadList();
      }
    });
  };
  // 5.其他：全选为异步操作，打印值与实际值(已赋值)不同步
  onSelectChange=(a,b)=>{
    console.log('全选与单选触发',a,b);
    this.setState({
      selectedRowKeys:a,
      ids:a.join(",")
    });
    console.log(this.state.selectedRowKeys);
    console.log(this.state.ids);
  };
  //5.2分页：获取第几页和分页数，并根据总条数计算分页
  onPaginationChange = (page) => {
    console.log('跳转分页:',page);
    this.setState({
      pageNo:page.current,
      pageSize: page.pageSize,
      totalCount: page.total,
    }, ()=> {
      this.reloadList();
    });
  };
  //5.3上传文件:
  uploadStatusChange=(info)=> {
    if (info.file.status !== 'uploading') {
      // console.log('正在上传：',info);
    }
    if (info.file.status === 'done') {
      message.success(`${info.file.name} 上传成功`);
      message.success(info.file.response.msg);
      this.reloadList();
    } else if (info.file.status === 'error') {
      message.error(`${info.file.name} 上传失败`);
    }
  }
  //5.4下载/导出：
  DownloadClick = () =>{
    const url = `${API_BASE_SECURITY}/member/export.shtml`;
    const id = this.state.ids;
    console.log('下载/导出请求:',url + '?ids=' + id);
    downloadFile( url + '?ids=' + id );
  };
  //5.5下载模板：
  downloadExcel = () => {
    downloadFile('files/excel-template/脱敏规则.xlsx');
  };
  //渲染
  render(){
    const { getFieldDecorator } = this.props.form;
    const allList = deepCopy(this.state.allList);
    return(
      <div style={{ padding: '20px 10px', backgroundColor: '#fff' }}>
        {/*1.搜索*/}
        <header style={{padding: '0 40px 20px'}}>
          <Search
            placeholder="可以按敏感词名称进行模糊搜索"
            onSearch={this.handleSearch.bind(this)}
          />
        </header>
        {/*2.按钮*/}
        <section style={{paddingBottom: 10}}>
          {/*新建*/}
          <Empower api="/member/import.shtml">
            <Button  onClick={this.handleNewContent.bind(this)} type='primary'>新建</Button>
          </Empower>
          {/*导入*/}
          <Empower api="/member/import.shtml">
            <Upload
              name="file"
              accept='application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
              action = {`${API_BASE_SECURITY}/member/import.shtml`}
              onChange={this.uploadStatusChange.bind(this)}
              showUploadList={false}
              beforeUpload={()=>message.loading('正在导入，请稍候...')}
            >
              <Button style={{marginLeft:10}} type='primary'>批量导入</Button>
            </Upload>
          </Empower>
          {/*导出/下载*/}
          <Empower api="/member/export.shtml">
            <Button style={{marginLeft:10}} onClick={this.DownloadClick.bind(this)} disabled={!this.state.ids.length} type='primary'>导出</Button>
          </Empower>
          {/*下载模板*/}
          <Empower api="/member/import.shtml">
            <Button style={{marginLeft:10}} onClick={this.downloadExcel.bind(this)} type='primary'>下载模板</Button>
          </Empower>
        </section>
        {/*3.表格*/}
        <TableList
          columns={this.columns}
          dataSource={this.state.dataSource}
          showIndex//序号
          onChange={this.onPaginationChange}
          pagination={{
            total: this.state.totalCount,//从后台获取totalCount赋值total，以及组件自动计算分页
          }}
          rowSelection={{
            selectedRowKeys:this.state.selectedRowKeys,//被选默认为空数组，清空时需要用到
            onChange: this.onSelectChange,//全选/单选时触发，获取选择的ids参数
          }}
        />
        {/*4.对话框：新建表单*/}
        <Modal
          maskClosable={false}//去除蒙版、选用自定义Modal可自由移动
          title={this.state.newVisible?"新建敏感词规则":"修改敏感词规则"}
          visible={this.state.newVisible||this.state.editVisible}
          onCancel={() => this.setState({ newVisible: false,editVisible: false})}
          // onOk={() => this.setState({ newVisible: false}||{editVisible: false})}//自定义提交按钮
          footer={[
            <Button key="back" size="large" onClick={() => this.setState({ newVisible: false ,editVisible: false})}>取消</Button>,
            <Button key="submit" type="primary" size="large"  onClick={this.state.newVisible?this.handleSubmit:this.editHandleSubmit}>
              确定
            </Button>,
          ]}
        >
          <Form>
            <FormItem
              labelCol={{span: 5}}
              wrapperCol={{ span: 19}}
              label="敏感词类别"
              // validateStatus={!this.state.resultError? '':'error'}
              // help={!this.state.resultError?'':this.state.resultError}
              // hasFeedback
            >
              {getFieldDecorator('name', {
                initialValue:this.state.name,
                rules: [{ required: true, message: '敏感词类别不能为空'}]
              })(
                <Input  placeholder="请输入敏感词类别" maxLength="20" spellCheck={false}/>
              )}
            </FormItem>

            <FormItem
              labelCol={{span: 5}}
              wrapperCol={{ span: 19}}
              label="长度是否固定"
            >
              {getFieldDecorator('isFixedLength', {
                valuePropName: 'defaultChecked',//接管子节点的参数属性
                initialValue: true,
                rules: [{ required: false, message: '请选择敏感词固定长度'}]
              })(
                <RadioGroup onChange={this.onChangeRadio } value={this.state.value}>
                  <Radio value={1}>是</Radio>
                  <Radio value={2}>否</Radio>
                </RadioGroup>
              )}
            </FormItem>

            <FormItem
              labelCol={{span: 5}}
              wrapperCol={{ span: 19}}
              label="敏感词样例"
            >
              {getFieldDecorator('originalInfo', {
                initialValue:this.state.originalInfo,
                rules: [{
                  required: false, message: '敏感词样例',
                }],
              })(
                <Input  placeholder="敏感词样例" maxLength="100" spellCheck={false}/>
              )}
            </FormItem>

            <FormItem
              labelCol={{span: 5}}
              wrapperCol={{ span: 19}}
              label="敏感规则"
            >
              <Row>
                <Col span={8}>
                  <FormItem
                    label="开始位置"
                    labelCol={{span: 12}}
                    wrapperCol={{ span: 12}}
                  >
                    {getFieldDecorator('begin', {
                      initialValue:this.state.begin,
                      rules: [{ required: false, message: '大于1值',pattern:/^[1-9]*[1-9][0-9]*$/}]
                    })(
                       <Input style={{width:50}}  placeholder="" type="number" min="1" maxlength={3} spellCheck={false}/>
                    )}
                  </FormItem>
                </Col>

                <Col span={8}>
                  <FormItem
                    label="结束位置"
                    labelCol={{span: 12}}
                    wrapperCol={{ span: 12}}
                  >
                    {getFieldDecorator('end', {
                      initialValue:this.state.end,
                      rules: [{ required: false, message: '大于1值',pattern:/^[1-9]*[1-9][0-9]*$/}]
                    })(
                       <Input style={{width:50}}  placeholder="" type="number" min="1" maxlength={3} spellCheck={false}/>
                    )}
                  </FormItem>
                </Col>
                {/*只允许在"@#$%^&*￥…"中选取一种特定字符*/}
                <Col span={8}>
                  <FormItem
                    label="敏感字符"
                    labelCol={{span: 12}}
                    wrapperCol={{ span: 12}}
                  >
                    {getFieldDecorator('symbol', {
                      initialValue:this.state.symbol,
                      rules: [{ required: false, message: '须为字符',pattern:/^[@#$%^&*￥]+$/}]
                    })(
                       <Input style={{width:50}}  placeholder="" maxLength="1" spellCheck={false}/>
                    )}
                  </FormItem>
                </Col>
              </Row>
            </FormItem>

            <FormItem
              labelCol={{span: 6}}
              wrapperCol={{ span: 18}}
              label="脱敏后信息"
            >
              {getFieldDecorator('sentiveInfo', {
                initialValue:this.state.sentiveInfo,
                rules: [{ required: false, message: '脱敏后信息'}]
              })(
                <Input  placeholder="脱敏后信息" maxLength="100" spellCheck={false} disabled/>
              )}
            </FormItem>
          </Form>
        </Modal>
      </div>
    )
  }
}
//三：调用
const WrappedApp = Form.create()(DesensitizationRuleTable);
export default connect(({ system, account, desensitizationRuleManage }) => ({
  system,
  account,
  desensitizationRuleManage,
}))(WrappedApp);
