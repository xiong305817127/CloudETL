/**
 * Created by Administrator on 2017/8/26.
 */
import TableList from "../../../components/TableList"
import  React  from 'react';
import { Icon,TreeSelect,Input,Button,Table,Tooltip,Popconfirm,message,Tree} from  'antd';
const TreeNode = Tree.TreeNode;
import Style from './style.css'
import { getStoreDatabase,getAcquisition,getSourceTable } from '../../../services/metadataCommon'
import { edit_table_struct } from'../../../services/metadata'
import {connect} from 'dva';
import { withRouter } from 'react-router';
import { getLabelByTreeValue } from 'utils/metadataTools';
import Search from '../../../components/Search';
import { convertArrayToTree } from '../../../utils/utils';
import Empower from '../../../components/Empower'; // 导入授权组件

class NewDataRelationShip extends React.Component{
  state = {
    pagination:{
      current:1,
      pageSize:10
    },
    data:[],
    loading:false,
    //选择部门
    options:[],
    text:"",
    treeData: [],
    expandedKeys: ['0-0'],
    searchValue: '',
    autoExpandParent: true,
  };
  
   onLoadData = (treeNode) => {
    const { renterId,status,id} = this.props.account;
    const { drmselecttable } = this.props;
    return new Promise((resolve) => {
      if (treeNode.props.children) {
        resolve();
        return;
      }
        getAcquisition({
           sourceId:"2",
           dsId:treeNode.props.dsId,
        }).then((res)=>{
            const {data,code} = res.data;
            if(code == "200"){
                 const { rows } = data;
                  let args = [];

                 for(let index of rows){
                    args.push({
                        title:index.metaNameEn,
                        key:index.metaid,
                        metaNameCn:index.metaNameCn,
                         isLeaf:true
                    })
                 }
                  treeNode.props.dataRef.children = args;
                  this.setState({
                    treeData: [...this.state.treeData],
                  });
            }
            resolve();      
        })
     })
  }
   onExpand = (expandedKeys) => {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    });
  }

   renderTreeNodes = (data) => {
    return data.map((item) => {
      if (item.children) {
        return (
          <TreeNode {...item} dataRef={item}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode {...item} dataRef={item} />;
    });
  }
   search = (record) =>{
    const { drmnewfilemodel } = this.props;
    const { renterId,id ,status} = this.props.account;
    const { dispatch }=this.props;
       let MList={};
       MList.renterId=renterId;
       MList.sourceId=2,
       MList.dsType=3,
       MList.status=2,
       MList.userId=id,
       getStoreDatabase(MList).then((res)=>{
        let treeData = [];
         const {code,data } = res.data;
        let key = 0;
        for(let inde of data.rows){
           treeData.push({
             title:inde.dbDatabasename,
             dsId:inde.dsId,
             id:inde.id,
             renterId:inde.renterId,
             sourceId:inde.sourceId,
             status:inde.status,
             modelLeft:"left",
             modelRight:"right",
             key:key++
           })
          }
          this.setState({
             treeData
          })
        })
   }
    componentWillMount(){
       this.search();
    }

   /*
   
     Alisa   2018-09-26-17-53
     没用到的方法注释了
     onDragEnd= (event, node) => {
    }
    onDrop=(event, node, dragNode, dragNodesKeys)=>{
    }
    onDragLeave=(event, node)=>{
    }*/

  onDragEnter = (info) => {
    console.log(info,"onDragEnter");
    const { dispatch } = this.props;
 /*   this.props.drmnewfilemodel.selectedRowKeysLeft = [];
    this.props.drmnewfilemodel.selectedRowKeysRight = [];
    this.props.drmnewfilemodel.selectLeft = [];
     this.props.drmnewfilemodel.selectRight = []*/
     
    console.log(this.props.drmnewfilemodel.selectedRowKeysLeft = [],this.props.drmnewfilemodel.selectedRowKeysRight = [],"null");
      let idSimble = this.props.drmnewfilemodel; 
     let id = this.props.drmnewfilemodel.metadataId; 
     let odiv2 =  info.node.selectHandle.firstElementChild.className;

       if(this.props.drmnewfilemodel.dataRight.length === 0){
           edit_table_struct(id).then((res)=>{
         if(res.data.code === "200"){
                   dispatch({
                      type:"drmnewfilemodel/changeModel",
                      payload:{
                       dataRight:res.data.data,
                      /* dataRight:res.data.data,*/
                       rightName:idSimble.node,
                       metadataId:id
                       }
                    }); 
                }
                if(idSimble.node === this.props.drmnewfilemodel.leftName){
                   dispatch({
                      type:"drmnewfilemodel/changeModel",
                        payload:{
                         rightName:"",
                         dataRight:"",
                       }
                    }); 
                    message.error("两个表的不能相同");
                }
            })
       }else{
         edit_table_struct(id).then((res)=>{
              if(res.data.code === "200"){
                   dispatch({
                      type:"drmnewfilemodel/changeModel",
                      payload:{
                       /*dataLeft:res.data.data,*/
                       leftName:idSimble.node,
                       dataLeft:res.data.data,
                       metadataId:id
                       }
                    }); 
                 }
                 if(idSimble.node === this.props.drmnewfilemodel.rightName){
                     dispatch({
                      type:"drmnewfilemodel/changeModel",
                        payload:{
                         leftName:"",
                         dataLeft:"",
                       }
                    }); 
                      message.error("两个表的不能相同");
                }
            })
       }
    // expandedKeys 需要受控时设置
     this.setState({
       expandedKeys: info.expandedKeys,
     });
  }
  onDragStart= (event, node) => {
     console.log(event, node,"onDragStart====");
      const { dispatch ,drmnewfilemodel } = this.props;
     if(drmnewfilemodel.rightmetaid === "" || drmnewfilemodel.leftmetaid === ""){
       console.log(event.node.props,"event.node.props");
           dispatch({
              type:"drmnewfilemodel/changeModel",
              payload:{
                 metadataId:event.node.props.eventKey,
                 rightmetaid:event.node.props.eventKey,
                 node:event.node.props.dataRef.metaNameCn
               }
           }); 
       }else if(drmnewfilemodel.rightmetaid === "" || drmnewfilemodel.leftmetaid === ""){
          dispatch({
              type:"drmnewfilemodel/changeModel",
                payload:{
                 metadataId:event.node.props.eventKey,
                 leftmetaid:event.node.props.eventKey,
                 node:event.node.props.dataRef.metaNameCn
               }
           }); 
       }else{
          message.error("还差一点点哦!");
       }
  }


   onSelect = (selectedKeys, e) => {
    console.log(selectedKeys,"selectedKeys");
     const { dispatch ,drmnewfilemodel } = this.props;
     let s = this.refs.drag;
     if(drmnewfilemodel.rightmetaid === "" || drmnewfilemodel.leftmetaid === ""){
           dispatch({
              type:"drmnewfilemodel/changeModel",
              payload:{
                 metadataId:selectedKeys,
                 rightmetaid:selectedKeys[0],
                 node:e.selectedNodes[0].props.metaNameCn
               }
           }); 
       }else if(drmnewfilemodel.rightmetaid === "" || drmnewfilemodel.leftmetaid === ""){
          dispatch({
              type:"drmnewfilemodel/changeModel",
                payload:{
                 metadataId:selectedKeys,
                 leftmetaid:selectedKeys[0],
                 node:e.selectedNodes[0].props.metaNameCn
               }
           }); 
       }else{
          message.error("还差一点点哦!qqqqqq");
       }
    }

     reload = (obj)=>{
       const { dispatch } = this.props;
        const {dataTable,searchIndex,text} = this.props.drmselecttable;
        const { id,renterId } = this.props.account;
        let dept = obj.text !== undefined?obj.text:text;
        let keyword = obj.searchIndex !== undefined?obj.searchIndex:searchIndex;
        let pageSize = obj.pageSize !== undefined?obj.pageSize:10;
        let current = obj.current !== undefined?obj.current:1;
        dispatch({
          type:"drmselecttable/showFileTable",
          payload:{
            obj:{
              "dept":dept,
              "keyword":keyword,
              "metaType" : "",
              "renterId": renterId,
              "sourceId":2,
              "status":1,
            },
            paper:{
              pageSize:pageSize,
              current:current
            }
          }
        });
     };

     handleSearch = (e)=>{
      const { dispatch } = this.props;
          dispatch({
            type:"drmselecttable/setMetaId",
            payload:{
              searchIndex:e
            }
          });
          this.reload({
            searchIndex:e
          });
      };

       onChange = (e) => {
          const value = e.target.value;
          const expandedKeys = dataList.map((item) => {
            if (item.key.indexOf(value) > -1) {
              return getParentKey(item.key, gData);
            }
            return null;
          }).filter((item, i, self) => item && self.indexOf(item) === i);
          this.setState({
            expandedKeys,
            searchValue: value,
            autoExpandParent: true,
          });
        }

  render(){
    const { metadataBase,dataLeft,actionType } = this.props.drmnewfilemodel;
     const file = {...metadataBase};
     const { treeData,expandedKeys,data,searchValue, autoExpandParent } = this.state;
     console.log(this.props.drmnewfilemodel,"this.props.drmnewfilemodel",data,dataLeft);
     return(
       <div className={Style.DataRelationshipManagementStyle} >
           {/*
               <div style={{display:"inline-flex",position:"relative",top:"4px",width:"100%"}}>
                     <Search disabled={actionType === "edit"}
                        placeholder="可以根据表中文名称或者表英文名称进行搜索"
                       onChange={this.onChange} />
               </div>
           */}
        
          <Tree loadData={this.onLoadData}
          draggable
              defaultExpandedKeys={expandedKeys} 
              onDragEnter={this.onDragEnter}
              onDragStart={this.onDragStart}
              onSelect={this.onSelect}
              onExpand={this.onExpand}
              expandedKeys={expandedKeys}
              autoExpandParent={autoExpandParent}>
              {this.renderTreeNodes(treeData)}
          </Tree>
       </div>
     )
  }
};

export default withRouter(connect(({ account,metadataCommon,drmnewfilemodel,drmselecttable }) => ({
  account,
  metadataCommon,
  drmnewfilemodel,
  drmselecttable,
}))(NewDataRelationShip));
