import  React  from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import { Form,Icon,Cascader,Input,Button,message,Popconfirm,Tooltip,Row,Col,Radio} from  'antd';
const RadioGroup = Radio.Group;
const FormItem = Form.Item;
const { TextArea } = Input;
import { API_BASE_METADATA ,DEFAULT_PAGE_SIZE} from '../../../../constants';
import { uploadFile, convertArrayToTree } from '../../../../utils/utils';
import TableList from '../../../../components/TableList';
import Search from '../../../../components/Search';
import Modal from 'components/Modal';

// import Style from '../DataRelationship/style.css';
import { getDepartmentTree, SJBZCXsearch, SJBZCXxinjian, SJBZCXDelete } from '../../../../services/metadata';
class DataStandardView extends React.Component{
  //1.初始化
  state={
    pagination:{
      current:1,
      pageSize:10
    },
     data:[],
    loading:false,

    //选择部门
    options:[],
    text:"",
    textBuMenAnNiu:"",

    routerListened: false, // 是否已监听路由
    isMounted: false, // 组件是否已挂载
    visible: false,
    /*dataSource:[],
    remark:'',
    title:'',
    visible: false,
    selectedRowKeys:[],
    id:''*/
  };
//选择部门级联
  xuanzebumendid = (value, selectedOptions) => {
    this.setState({
      text: selectedOptions.map(o => o.label).join(', '),
      textBuMenAnNiu: '取消选择',
    });
  };

  //更新表格
  componentDidMount(){
    this.setState({
      isMounted:true
    },()=>{
      const { router } = this.props;
      if(!this.state.routerListened){
        router.listen(location =>{
          if(location.pathname === "/DataStandardView" && this.state.isMounted){
            this.RequestList();
          }
        });
        this.setState({
          routerListened:true
        })
      }
    });
  };

  componentWillUnmount() {
    this.setState({
      options:[],
      text:"",
      textBuMenAnNiu:"",
      isMounted:false
    });
    const location = this.props.location;
    delete location.query.keyword;
  }

 RequestList(){
    this.setState({
       loading:true
    });
    const { query } = this.props.location;
    const pager = this.state.pagination;
    const { id } = this.props.account;
      let obj = {
      "dept":this.state.text,
      "keyword":query.keyword?decodeURIComponent(query.keyword):""
    };
    SJBZCXsearch(obj,{
      current: query.page || 1,
      pageSize: query.pageSize || pager.pageSize,
    }).then((res)=> {
      if (res.data && res.data.data) {
        const { total, rows } = res.data.data;
        pager.total = total;
        rows.map( (row, index) => {
          row.key = row.id;
          row.index = pager.pageSize * (pager.current - 1) + index + 1;
          return row;
        });
        console.log(pager);
        this.setState({
          data: rows,
          pagination: pager,
          loading: false
        });
      }
    });

    getDepartmentTree(id).then((res)=>{
      const { code,data } = res.data;

      if(code === "200"){
        let options=convertArrayToTree(data || '[]', 0, 'id', 'parentId', 'children', child => ({
          value: child.id,
          label: child.deptName,
        }));
        this.setState({
          options:options
        })
      }else {
         message.error("获取部门失败！");
      }
    });
  };

  Search(e){
    console.log(e);
     const location = this.props.location;
    if(e && e.trim()){
      location.query.keyword = encodeURIComponent(e);
    }else{
       delete location.query.keyword;
    }
    this.props.router.push(location);
  }



  columns = [
    {
      title: '文档标题',
      dataIndex: 'title',
      key: 'title',
      render: (text,record) => {
        //一个未声明的函数onClick={()=>{this.handleIconClick(record)}}
        return <a onClick={()=>{this.openFile(record)}}>{text}</a>
      }
    }, {
      title: '所属部门',
      dataIndex: 'dept',
      key: 'dept',
    }, {
      title: '大小',
      dataIndex: 'size',
      key: 'size',
    }, {
      title: '上传日期',
      dataIndex: 'createTime',
      key: 'createTime',
      width: '10%',
    }
    ,{ title: '上传人',
      dataIndex: 'modifier',
      key: 'modifier',
      width: '10%',
    },{ title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      render: (text) => (<div className="word25" title={text}>{text}</div>)
    },{
      title: '操作',
      key: 'x123',
      render: (text,record) => {
        return(<div>
          <Popconfirm placement="topLeft" title="确认要删除该行吗？" onConfirm={()=>{this.getDelete(record)}} >
            <a>
              <Tooltip title="删除" >
                <Icon type="delete" className="op-icon"/>&nbsp;&nbsp;&nbsp;&nbsp;
              </Tooltip>
            </a>
          </Popconfirm>
          <a target="_blank">
            <Tooltip title="下载" >
              <Icon onClick={()=>this.GetXiaZai(record)} type="download" className="op-icon"/>
            </Tooltip>
          </a>
        </div>)
      }}
  ];





 //点击打开文件
  openFile(record){
  {/* window.open('${API_BASE_METADATA}/fileOperate/download/`+ record.title');*/}
    window.open('${API_BASE_METADATA}/fileOperate/show/`+ record.title');
   }

  //新建弹出框《start》
  showModals(){
    this.setState({
      visible: true,
    });
  }

  handelChange(e){
    let remark = e.target.value;
    this.setState({
      remark:remark
    })
  }

  handleOk = (e) =>{
    let file;
    let file1 = e.target.name;
    let remark =  e.target.value;
    var oMyForm = new FormData();

    oMyForm.append("sourceFile", this.refs.file1.files[0]);
    oMyForm.append("remark", this.state.remark);
    oMyForm.append("renterId", this.props.account.renterId);
    if(file === undefined){
      // message.error('新建文档上传失败');
    }
    if(file1 === this.refs.file1.files[0].file){
      uploadFile(`${API_BASE_METADATA}/fileOperate/upload`,oMyForm,(request)=>{
        console.log(request);
        const { code } = request;
        ReactDOM.findDOMNode(this.refs['submitAction']).reset();
        if(code == "200" ){
          message.success('文件上传成功！');

          oMyForm.append("sourceFile", "");
          oMyForm.append("remark", "");
          oMyForm.append("renterId", "");
          this.setState({
            visible: false
          });
          this.RequestList();
          this.Search();

        }else{
          message.error('文件上传失败！');
            ReactDOM.findDOMNode(this.refs['submitAction']).reset();
        }
      });
    }else{
      message.error('新建文档上传失败');
        ReactDOM.findDOMNode(this.refs['submitAction']).reset();
    }
  };

  handleCancelAlert = (e) =>{
    this.setState({
      visible: false,
    })
  };
  handleCancel = () => {
    console.log('Clicked cancel button');
    this.setState({
      visible: false,
    })
  };
  // 新建弹出框《end》

  handleModeChange = (e) =>{
    const mode =this.value;
    this.setState({ mode });
    this.setState({
      visible: true,
    })
  };
  //删除文件接口
  getDelete(record){
    let ids = record.id;
    SJBZCXDelete(ids).then((res)=>{
      this.Search();
    })
  };
  //下载：0_0
  GetXiaZai(record){
    window.location.href = `${API_BASE_METADATA}/fileOperate/download/`+ record.title;
  }
  render(){
    const {query}  = this.props.location;
    const pagination  = this.state.pagination;
    const {visible} = this.state;
    return(

      <Row style={{backgroundColor:'white'}}>

        {/*1.搜索className={Style.DataRelationshipManagement} className={Style.xuanzebumen}*/}
        <Col span={24} style={{padding:"30px 0px 10px 50px"}}>
           <Search  placeholder="可以按相互关联的文档名称，代码进行模糊查询"
                   onSearch={(e)=>{this.Search(e)}}
                    defaultValue={query.keyword?decodeURIComponent(query.keyword):""} />
          <span >
            <Cascader ref="xuanzebumen" placeholder="选择部门" options={this.state.options} onChange={this.xuanzebumendid.bind(this)} >
            </Cascader>
          </span>
        </Col>
        {/*2.表格和按钮*/}
        <Col span={24}>
          {/*2.1.新建对话框*/}
          <Button type="primary" style={{margin:"20px"}} onClick={() => this.showModals()}>新建</Button>
          <Modal
            title="数据标准文档上传(仅支持txt，PDF，Word，Excel格式)" ref="ModalUp"
            visible={visible}
            onCancel={this.handleCancel}
            footer={[
              <Button key="back"  size="large" loading={this.state.loading} onClick={this.handleCancelAlert}>
                取消
              </Button>,
              <Button key="next" id="fileBut" type="primary" size="large" onClick={this.handleOk.bind(this)}>上传</Button>,
            ]}
          >
            {/* className={Style.GetFil} className="login-form"*/}
            <Form onSubmit={this.handleSubmit} name="submitAction" id="submitAction" ref="submitAction">
              <FormItem
                label="上传内容"
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 18 }}
                required
              >
                <input
                  type="file"
                  ref="file1"
                  name="filename"
                  accept='application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                />
              </FormItem>
              {/*className={Style.BoxP}*/}
              <FormItem
                label="备注"
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 18 }}
                // required
              >
                <TextArea
                  ref="remark"
                  onChange={this.handelChange.bind(this)}
                  name="remark"
                  placeholder=""
                  autosize={{ minRows: 3, maxRows: 6 }}
                  rows={4}
                  spellCheck={false}
                   maxLength="200"
                />
              </FormItem>
            </Form>
          </Modal>

          {/*2.2.表格*/}
          <TableList
            showIndex
            style={{margin:"10px"}}
             onRowClick={()=>{return false}}
             pagination={pagination}
            ref="editTable"
            columns={this.columns}
            dataSource={this.state.data}
            loading={this.state.loading}
            className="th-nowrap "
          />
        </Col>
      </Row>
    )
  }
}

export default withRouter(connect(({ account }) => ({
  account
}))(DataStandardView));
