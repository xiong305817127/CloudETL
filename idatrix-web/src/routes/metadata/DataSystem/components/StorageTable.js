import React from 'react'
import { Input,Icon,Button,Radio ,Cascader,message,Tabs,Table,Popconfirm } from 'antd';
import { get_table_struct,edit_table_struct,get_metatable_id,add_table_struct,search_table_struct ,new_table_struct, exportMetadata, YongjiushanchuPUT} from '../../../../services/metadata';
import Style from './StorageTable.css'
import { connect } from 'dva'
import TableList from 'components/TableList';
import { downloadFile } from 'utils/utils';
import Empower from 'components/Empower';

const utits = {
  day: '天',
  hour: '小时',
  miunte: '分钟',
  week: '周',
  month: '月',
  quarter: '季度',
};

const TabPane = Tabs.TabPane;
const rows=10;
class StorageTable extends React.Component{
  state = {
    pagination:{},
    pagination1:{
      current:1,
      pageSize:10
    },
    data1:[],
    data2:[],
    loading:false,
    loading1:false,
    count:0,
    status:"newTable",
    info:{},
    metaid:"",
    selectedRowKeys:[],
    selectedRows:[],
    metaNameCn:"",
    oldmetaNameCn:""
  };
  
  constructor(props){
    super(props);
  }
  columns1 = [
    /*{
    title: '序号',
    dataIndex: 'key',
    key: 'key'
  },*/ {
    title: '数据表中文名称',
    dataIndex: 'metaNameCn',
    key: 'metaNameCn',
    render: (text,record) => {
      return <a disabled={!record.canEdited} onClick={()=>{this.handleView(record)}}>{text}</a>
    }
  }, {
    title: '创建者',
    dataIndex: 'creator',
    key: 'creator'
  },{
    title: '数据表英文名称',
    dataIndex: 'metaNameEn',
    key: 'metaNameEn'
  },{
    title: '提供频率',
    dataIndex: 'frequency',
    key: 'frequency',
    render: (text, record) => `${text || 0}${utits[record.frequencyUnits] || ''}`
  },{
    title: '是否已生成实体表',
    dataIndex: 'status',
    key: 'status',
  },/*{
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    render: (text) => (['无效', '有效'])[text],
  },*/{ title: '操作',
    dataIndex: '',
    /*<a onClick={()=>{this.showNewModel(record)}}><Icon type="edit" className="op-icon" /></a>*/
    render: (text,record) => record.canEdited ? (<div>
      <Popconfirm title="确认要删除该表吗？" onConfirm={() => this.handleDelete(record)}>
        <a><Icon type="delete" className="op-icon" style={{marginLeft: 10}} /></a>
      </Popconfirm>
    </div>) : null}
  ];

  handleView(record){
    console.log(record,"record");
    const {dispatch}=this.props;
    console.log(this.props.params.serverName,"props");
     dispatch({
          type:'newstoragetable/model',
          visible:true,
        });
      dispatch({
          type:'newstoragetable/show',
          visible:true,
          model:"editmodel",
          metaNameEn:record.metaNameEn,
          metaNameCn:record.metaNameCn,
          metaid:record.metaid,
          frequency:record.frequency,
          serverName:this.props.params.serverName,
          dsId:this.state.dsId,
          dbDatabasename:record.dataSource.dbDatabasename,

        });
  }

  handleTableChange (pagination){
    const pager = { ...this.state.pagination};
    pager.current = pagination.current;
    this.setState({
      pagination: pager,
    });
    if(this.state.oldmetaNameCn){
      this.Request1(this.state.oldmetaNameCn,pagination);
    }else{
      this.Request(pagination,this.state.dsId);
    }
  }
  async handleDelete(record) {
    const formData = [{metaid: record.metaid}];
    const { data } = await YongjiushanchuPUT(formData);
    if (data.code === '200') {
      const { current, pageSize } = this.state.pagination;
      message.success('删除成功');
      this.Request({ current, pageSize },this.state.dsId);
    }
  }
  rowSelection={
    onChange: (selectedRowKeys,selectedRows) => {
      console.log(selectedRowKeys,selectedRows,"selectedRowKeys,selectedRows");
      this.setState({
        selectedRowKeys,
        selectedRows
      })
    },
     getCheckboxProps: record => ({
      disabled:record.status === '是'
    }),
    selectedRowKeys: this.state.selectedRowKeys
  };
  handleExport(){
   /* debugger;
     const ids = this.state.selectedRowKeys.join(',');
    if (ids.length > 1) {
      message.warn('一次只能导出一个表');
    } else {
      downloadFile(`${exportMetadata}?ids=${ids}`);
      this.reloadList();
    }*/
    
    const ids = this.state.selectedRows.map(it => it.metaid).join(',');
    downloadFile(`${exportMetadata}?ids=${ids}`);
  }
  componentWillMount(){
    const {storagetable}=this.props;
    const { dsId,serverName,dbDatabasename } = this.props.params;
    this.setState({
      dsId:dsId
    },()=>{});
    this.Request({
      current:1,
      pageSize:10
    },dsId)
  }
  componentWillReceiveProps(nextProps){
    const { storagetable, dispatch } = nextProps;
    if (storagetable.reloadList) { // 监听到刷新列表要求
      this.Request({
        current:1,
        pageSize:10
      },this.state.dsId);
      dispatch({ type: 'storagetable/reloadList', reload: false });
    }
    if(nextProps.dsregistermodel){
      const { model } = nextProps.dsregistermodel;
      if( model === "exportModel" ||  model === "deleteModel" ||  model === "newTableModel" ){
        if(model === "deleteModel"){
          this.setState({
            selectedRowKeys1:[],
            selectedRows1:[]
          })
        }
        const pager = { ...this.state.pagination };
        pager.current = 1;
        this.setState({
          pagination: pager,
        });
        if(this.state.oldmetaNameCn){
          this.Request1(this.state.oldmetaNameCn,{
            current:1,
            pageSize:10
          });
        }else if(nextProps.dsregistermodel.info && nextProps.dsregistermodel.info.dsId){
          const info=this.state.info;
          info.dsId=nextProps.dsregistermodel.info.dsId;
          this.setState({ info });
          this.Request({
            current:1,
            pageSize:10
          },this.state.dsId);
        }
      }
    }
  }
  Request(pagination,dsId) {
    this.setState({
      loading: true,
       selectedRowKeys:[]
    });

    get_table_struct(pagination, dsId).then((res)=> {
      if (res.data) {
        const total = res.data.data.total;
        let args = res.data.data.rows;
        for (let index  of args) {
          // index.key = index.metaid;
          if (index.status === 1) {
            index.status = "是";
          } else {
            index.status = "否";
          }
        }
        pagination.total = total;
        this.setState({
          loading: false,
          data1: args,
          pagination: pagination
        })
      }
    })
  }
  showNewModel(record){
    const location = this.props;
    console.log(location,"location");
    const { serverName,dbDatabasename,metaid } = this.props.params;
    if(record){
      edit_table_struct(record.metaid).then((res)=>{
        let args = res.data.data;
        let count = 0;
        for(let index of args){
          index.key =  count++;
          if(!index.isNull){
            index.isNull = '1'
          }
        }
        const {dispatch}=this.props;
        dispatch({
          type:'newstoragetable/show',
          visible:true,
          model:"editTable",
          info:args,
          serverName:serverName,
          dbDatabasename:dbDatabasename,
          metaNameEn:record.metaNameEn,
          metaNameCn:record.metaNameCn,
          metaid:record.metaid,
          frequency:record.frequency,
          dsId:this.state.dsId
        });
      })
    }else{
      const { dispatch } = this.props;
       console.log(this.state.metaid,"metaid1234567");
      dispatch({
        type:'newstoragetable/show',
        model:"newTable",
        info:[{key:0}],
        serverName:serverName,
        dbDatabasename:dbDatabasename,
        visible:true,
        dsId:this.state.dsId,
        metaid:metaid,
      })
    }
  }
  handleNewTable(){
    // const args=[];
    // for(let index of this.state.selectedRows){
    //   args.push({
    //     iscreate:1,
    //     metaid:index.metaid,
    //     dsId:index.dsId,
    //     dsType:3,
    //     sourceId:1,
    //   });
    // }
    const { account } = this.props;
    const args = {
      userId: account.id,
      ids: this.state.selectedRows.map(it => it.metaid).join(','),
    };
    new_table_struct(args).then((res)=>{
      console.log("resres||",res);
      if(res.data.data === "true"){
         const { current, pageSize } = this.state.pagination;
        message.success('成功生成数据库实体表');
        this.Request({ current, pageSize },this.state.dsId);
        this.setState({
            selectedRowKeys:[]
          })
      } else {
        message.error(res.data.msg || '生成数据库实体表失败');
      }
      // dispatch({
      //   type:'dsregistermodel/editmodel',
      //   model:"newTableModel"
      // })
    })

    // dispatch({
    //   type:"deletetip/export",
    //   visible:true,
    //   model:"dsRegister",
    //   tip:"生成实体表",
    //   allSelected:this.state.selectedRowKeys,
    //   currentSelected:this.state.selectedRows
    // })
    //disabled={this.state.selectedRowKeys.length === 0}
  }
  render(){
    const disableNewTable = this.state.selectedRowKeys.length === 0 || this.state.selectedRows.some(it => it.status === '是');
    return(
      <div>
        <div className={Style.ButtonList}>
          <Button type="primary" ><a href="#/DataSystemSegistration">返回</a></Button>
          {/*新建乱码*/}
          <Empower api="/frontMetadataInfoController/ds/createMetadata">
            <Button type="primary" onClick={()=>{this.showNewModel()}}>新建</Button>
          </Empower>
          <Empower api="/frontMetadataInfoController/ds/exportMetadata">
            <Button type="primary" disabled={this.state.selectedRowKeys.length === 0} onClick={this.handleExport.bind(this)}>导出</Button>
          </Empower>
          <Empower api="/frontMetadataInfoController/createtable">
            <Button type="primary" disabled={disableNewTable} onClick={this.handleNewTable.bind(this)}>生成数据库实体表</Button>
          </Empower>
        </div>
        {/*表结构注册*/}
        <TableList
          rowKey="metaid"
          showIndex
          rowSelection={{
            type:"radio",
            onChange: (selectedRowKeys,selectedRows) => {
              console.log(selectedRowKeys,selectedRows,"selectedRowKeys,selectedRows");
              this.setState({selectedRowKeys,selectedRows})
            },
            selectedRowKeys: this.state.selectedRowKeys
          }}
          onChange={this.handleTableChange.bind(this)}
          pagination={this.state.pagination}
          loading={this.state.loading}
          columns={this.columns1}
          dataSource={this.state.data1}


        />
      </div>
    )
  }
}

export default connect(({ storagetable, account }) => ({
  storagetable,
  account,
}))(StorageTable);
