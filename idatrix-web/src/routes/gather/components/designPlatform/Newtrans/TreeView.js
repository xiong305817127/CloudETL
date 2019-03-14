/**
 * Created by Administrator on 2017/8/31.
 */
import { Tree,Input,Row,Col } from 'antd';
import Modal from "components/Modal.js";
import { connect } from 'dva';
import './TreeView.css';
import {getFileList } from  'services/gather';
import { getQuaFileList } from "services/quality";

const TreeNode = Tree.TreeNode;

const TreeView = ({treeview,dispatch})=>{
  const { visible,treeList,expandedKeys,loadedKeys,updateModel,rootType,needFileName,needType,prefixStr,value,filterType,viewPath,upFolder,needUpFolder } = treeview;

  const handleOk = ()=>{
    updateModel(value);
    handleCancel();
  };
  const handleCancel = () => {
    expandedKeys.splice(0);
    dispatch({
      type:"treeview/hideTree"
    });
  };

  const onSelect = (e,selectedKeys,extra) => {

    if(e[0] === "0-0-0"){
      handleClick();
      return false;
    }

  if(!selectedKeys){
        return ;
    }

    if(selectedKeys){
      let str = "";
      if(e[0] === rootType){
        str = selectedKeys.node.props.title;
      }else{
        if((needType === "file" || needType === "all") && !selectedKeys.node.props.folder ){
          if(needFileName){
            str = e[0];
          }else{
            let num = e[0].lastIndexOf(".");
            if(num === -1){
              str = e[0];
            }else{
              str = e[0].substring(0,num);
            }
          }
        }else if(needType === "folder" ){
          if(selectedKeys.node.props.folder){
            str = e[0]
          }else{
            let num = e[0].lastIndexOf("/");
            if(num === -1){
              str = "";
            }else{
              str = e[0].substring(0,num+1);
            }
          }
        }else if(needType === "all"  && selectedKeys.node.props.folder){
          str = e[0]
        }
      }

      dispatch({
        type:"treeview/showTree",
        payload:{
          value:str
        }
      })
    }

  };

  const handleChange = (e)=>{
    dispatch({
      type:"treeview/showTree",
      payload:{
        value:e.target.value
      }
    })
  };

  const onExpand = (expandedKeys,{ node })=>{

    if(node.props.eventKey === "0-0-0"){
      handleClick();
      return false;
    }
    dispatch({
      type:"treeview/showTree",
      payload:{
        expandedKeys:expandedKeys
      }
    })
  
   
  };

  const onLoadData = (treeNode)=>{
	
    return new Promise((resolve) => {
      if (treeNode.props.children) {
        resolve();
        return;
			}

			if(treeList && !treeList.folder ){
				resolve();
				return false;
			}
			
      let obj =  {
          type: rootType,
          path: prefixStr+treeNode.props.eventKey,
          depth: 1,
          filterType:filterType
      };

      let method = treeview.action === "quality"?getQuaFileList:getFileList;

      method(obj).then( res=>{
          const {code,data} = res.data;
          if(code === "200"){
              resolve();
              dispatch({
                type: "treeview/addTree",
                treeData:data.children,
                evenKey:treeNode.props.eventKey
              });
          }
      });
    })
  };

  const onLoad = (data)=>{
    dispatch({
      type:"treeview/showTree",
      payload:{
        loadedKeys:data
      }
    })
  }

  const getTreeList = data => data.map(index =>{
    if(index.children && index.children.length){
      return (
        <TreeNode  isLeaf={index.folder?false:true} folder={index.folder?true:false} title={index.fileName} key={index.path}>{getTreeList(index.children)}</TreeNode>
      )
    }
    return(
      <TreeNode title={index.fileName} key={index.path} key={index.path} folder={index.folder?true:false} isLeaf={index.folder?false:true}  className={index.folder?"folder":""} />
    )
  });


  const getTree = ()=>{
    if(treeList && treeList.fileName){
      if(treeList.children){
        return(
          <TreeNode folder={treeList.folder?true:false} disabled={viewPath?false:true}  title={viewPath?viewPath:treeList.fileName} key={treeList.path?treeList.path:treeList.fileName}>
            {/* {
              needUpFolder?viewPath&&!upFolder?null:<TreeNode className="newTreeNode"  disabled={upFolder?false:true} onClick={handleClick} title={"..."} checkable /> : null
            } */}
            {
              getTreeList(treeList.children)
            }
          </TreeNode>
        )
      }else{
        return(
          <TreeNode folder={treeList.folder?true:false} disabled={viewPath?false:true} title={viewPath?viewPath:treeList.fileName} key={treeList.path?treeList.path:treeList.fileName} >
            {
              needUpFolder? viewPath&&!upFolder ?null:<TreeNode className="newTreeNode"  disabled={upFolder?true:false}   onClick={handleClick} key={"...upFolder"} title={"..."} checkable /> : null
            }
          </TreeNode>
        )
      }
    }
  };



  const treeNodes = getTree();

  const handleClick = ()=>{
    dispatch({
      type:"treeview/getParentFolder",
      payload:{
         path:upFolder
      }
    });
    dispatch({
      type:"treeview/reloadTreeModel",
      payload:{
        path:upFolder,
        depth:1,
        filterType:filterType
      }
    })
	};

  return(
    <Modal
      title="浏览目录"
      visible={visible}
      wrapClassName="vertical-center-modal out-model"
      onOk={handleOk}
      onCancel={handleCancel}>
      <Row>
         <Col span={6} style={{lineHeight:"48px",textAlign:"center"}}>目录或文件 : </Col>
          <Col span={16}>
            <Input value={value} onChange={handleChange} style={{margin:"10px 0"}}/>
          </Col>
      </Row>
      <div  style={{maxHeight:"600px",marginTop:"10px" ,overflowY:"scroll"}}>
        <Tree
          showLine
          expandedKeys={expandedKeys?expandedKeys:[]}
          onSelect={onSelect}
          onExpand={onExpand}
          className="cloudetlTree"
          loadData={onLoadData}
          onLoad={onLoad}
          loadedKeys={loadedKeys}
        >
          { treeNodes }
        </Tree>
      </div>

    </Modal>
  )
};
export default connect(({ treeview }) => ({
  treeview
}))(TreeView)
