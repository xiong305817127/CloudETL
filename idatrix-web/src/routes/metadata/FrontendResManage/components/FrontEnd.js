/**
 * Created by Administrator on 2017/8/24.
 */
import  React  from 'react';
import { Layout,Button,Icon,Popconfirm, message,Tooltip} from  'antd';
import TableList from "../../../../components/TableList";
import MFServerModel from "../MFServerModel";
import ZCRegisteredModel from "../ZCRegisteredModel";
import { withRouter } from 'react-router';
import { connect } from 'dva';
import { get_frontserver_table_fields,search_frontserver_table_fields,delete_front_server,getDepartmentTree } from '../../../../services/metadata';
import Empower from '../../../../components/Empower'; // 导入授权组件

class FrontEnd extends React.Component{
  state = {
    data: [],
    pagination: {
      current:1,
      pageSize:10
    },
    routerListened: false, // 是否已监听路由
    isMounted: false // 组件是否已挂载
  };

  componentDidMount(){
    this.setState({
      isMounted:true
    },()=>{
      const { router } = this.props;
      if(!this.state.routerListened){
        router.listen(location =>{
          let model = location.query.model?location.query.model:"1";
          if(location.pathname === "/FrontendResManage" && model == 1 && this.state.isMounted){
            this.Request();
          }
        });
        this.setState({
          routerListened:true
        })
      }
    });
  };

  componentWillMount(){
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
    this.Request();
  }

  componentWillReceiveProps(nextProps){
    const {actionKey} = nextProps.frontendfesmanage;
    if(actionKey === "updatemodel" && this.state.isMounted === true){
      this.Request();
    }
  }

  componentWillUnmount() {
    this.state.isMounted = false;
  }


  Request(){
    const { query } = this.props.location;
    const pager = { ...this.state.pagination };
    this.setState({
      loading: true
    });

    const {renterId} = this.props.account;
    get_frontserver_table_fields({
      current: query.page || 1,
      pageSize: query.pageSize || pager.pageSize
    },{renterId}).then((res)=>{
      if(res.data.code === '200'){
        const {total,rows} = res.data.data;
        pager.total = total;
        rows.map( (row, index) => {
          row.key = row.id;
          row.index = pager.pageSize * (pager.current - 1) + index + 1;
          return row;
        });
        this.setState({
          loading: false,
          data: rows,
          pagination:pager
        })
      }
    });
    setTimeout(()=>{
      if(this.state.loading){
        try{this.setState({loading:false})}catch(err){
        }
      }
    },4000)
  };

  //前置机
  columns = [
    {
      title: '前置机名称',
      dataIndex: 'serverName',
      key: 'serverName',
      width:"12%",
       render: (text,record) => {
         return <a onClick={()=>{this.ShowClickModel(record)}}>{text}</a>
      }
    }, {
      title: '创建者',
      dataIndex: 'manager',
      key: 'manager',
      width:"8%",
    },{
      title: '对接的组织',
      dataIndex: 'organization',
      key: 'organization',
      width:"20%",
      render: (text) => {
        const { departmentsOptions } = this.props.metadataCommon;
        try {
          const ids = JSON.parse(text);
          let result = departmentsOptions.filter(d => ids.indexOf(d.value) > -1);
          return result.map(d => d.label).join('、');
        } catch (err) {
          return text;
        }
      }
    },{
      title: 'IP地址',
      dataIndex: 'serverIp',
      className: 'td-nowrap',
      key: 'serverIp',
      width:"10%",
    },{
      title: '端口',
      dataIndex: 'dbPort',
      key: 'dbPort',
      width:"5%",
    },{
    title: '数据库类型',
    dataIndex: 'dsType',
    key: 'dsType',
    width:"10%",
    render: (text, record) => ({
      '2': 'Oracle',
      '3': 'MySQL',
      '4': 'Hive',
      '5': 'Hbase',
      '14':'DM',
      '8':"PostgreSql"
    })[record.dsType],
  },{
      title: '所在的机房',
      dataIndex: 'positionInfo',
      key: 'positionInfo',
      width:"15%",
    },{
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width:"10%",
       render: (text) => (<div className="word25" title={text}>{text}</div>)
    },{ title: '操作',
      dataIndex: '',
      className: 'td-nowrap',
      key: 'x',//这里的text和record没区别:修改onClick={()=>{this.editModel(record)}}；
      render: (text,record) => {

        return (<div>
         <Empower api="/frontEndServerController/update" disabled={!record.canEdited}>
            <a onClick={()=>{this.editModel(record)}}>
              <Tooltip title="编辑" >
                <Icon type="edit" className="op-icon"/>&nbsp;&nbsp;&nbsp;&nbsp;
              </Tooltip>
            </a>
          </Empower>
          {/**
          <Empower api="/frontEndServerController/updateStatus" disabled>
            <Popconfirm placement="topLeft" title="确认要删除该行吗？" onConfirm={()=>{this.confirm(record)}} onCancel={()=>{this.cancel()}} >
              <a>
                <Tooltip title="删除" >
                  <Icon type="delete" className="op-icon"/>
                </Tooltip>
              </a>
            </Popconfirm>
          </Empower> */}
        </div>)
      }}
  ];

  confirm(record){
    record.status=1;
    delete_front_server([record]).then((res)=>{
      if(res.data.msg==="Success"){
        this.Request();
        message.success('删除成功');
      }else if(res.data.code === "601"){
          message.success('数据库已存在实体表，无法删除');
      }else{
        message.success('删除失败')
      }
    });
  }

  editModel(record){
    console.log(record.id,"record.id");
    const { dispatch } = this.props;
    for(let index of this.state.data){
      if(index.id === record.id){
        dispatch({
          type:"mfservermodel/show",
          visible:true,
          model:"editmodel",
          info:index,
          id:index.id
        })
        this.Request();
      }
    }
  }

    ShowClickModel(record){
      const { dispatch } = this.props;
      for(let index of this.state.data){
        if(index.id === record.id){
          dispatch({
            type:"mfservermodel/show",
            visible:true,
            model:"showclickmodel",
            info:index
          })
          this.Request();
        }
      }
    }

  showModel(){
    const {dispatch} = this.props;
    dispatch({
      type:"mfservermodel/show",
      visible:true,
      model:"newmodel",
      info:{}
    })
  }

    ZCshowModel(){
    const {dispatch} = this.props;
    dispatch({
      type:"mfservermodel/show",
      visible:true,
      model:"newmodel",
      info:{}
    })
  }

  render(){
    return(
      <section style={{backgroundColor: '#fff'}} className="padding_0_20">
        <Empower api="/frontEndServerController/insert">
           <Button type="primary"  onClick={this.showModel.bind(this)} className="margin_20_0" >新建</Button>
        </Empower>
        {/*
        <Empower api="/frontEndServerController/insert">
           <Button type="primary"  onClick={this.ZCshowModel.bind(this)} style={{margin:10}} >注册</Button>
        </Empower>
      */}
        <TableList
          showIndex
          onRowClick={(record)=>{console.log(record)}}
          pagination={this.state.pagination}
          loading={this.state.loading}
          columns={this.columns}
          dataSource={this.state.data}
          className="th-nowrap "
        />
        <MFServerModel />
        <ZCRegisteredModel />
      </section>
    )
  }
}

export default withRouter(connect(({metadataCommon,account}) => ({
  metadataCommon,account
}))(FrontEnd));
