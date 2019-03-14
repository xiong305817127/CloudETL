/**
 * Created by Administrator on 2017/8/7.
 */
import { Modal,Tree,message,Alert } from 'antd';
import { connect } from 'dva';
const TreeNode = Tree.TreeNode;

const FolderTree = ({foldertree,dispatch})=>{

  let path = "";
    console.log(foldertree);
  const {visible,treeList,setFolder,expandedKeys,root,pathStr} = foldertree;

  const handleOk = ()=>{
    setFolder(path[0]);
    handleCancel();
  };

  const handleCancel = ()=>{
    expandedKeys.splice(0);
    dispatch({
      type:"foldertree/hideTree"
    })
  };

  const onSelect = (e,selectedKeys,info) => {
    console.log(selectedKeys,"selectedKeys");
    if(selectedKeys.selectedNodes[0]){
      if(selectedKeys.selectedNodes[0].props.folder){
        path = "";
      }else{
        path = e;
      }
    }
  };

  const onLoadData = (treeNode)=>{
    console.log(treeNode.props.eventKey,"treeNode");
    return new Promise((resolve) => {
      setTimeout(() => {
        dispatch({
          type:"foldertree/getTreeModel",
          payload:{
            obj:{
              type:root,
              path:pathStr+treeNode.props.eventKey,
              depth:1
            },
      
          }
        });
        resolve();
      }, 1000);
    });
  };

  const onExpand = (expandedKeys)=>{
    dispatch({
      type:"foldertree/showTree",
      payload:{
        expandedKeys:expandedKeys
      }
    })
  };



  const getTreeList = data => data.map(index =>{
        if(index.children && index.children.length){
            return (
               <TreeNode  isLeaf={index.folder?false:true} folder={index.folder?true:false} title={index.fileName} key={index.path}>{getTreeList(index.children)}</TreeNode>
            )
        };
        return(
          <TreeNode title={index.fileName} key={index.path} folder={index.folder?true:false} isLeaf={index.folder?false:true}  className={index.folder?"folder":""} />
        )
  });


  const getTree = ()=>{
      if(treeList && treeList.fileName){
          if(treeList.children){
            return(
              <TreeNode folder={treeList.folder?true:false} disabled={treeList.folder?true:false} title={treeList.fileName} key={treeList.fileName}>
                {
                  getTreeList(treeList.children)
                }
              </TreeNode>
            )
          }else{
            return(
                <TreeNode folder={treeList.folder?true:false} disabled={treeList.folder?true:false} title={treeList.fileName} key={treeList.fileName} />
            )
          }
      }
  };

    console.log(expandedKeys?expandedKeys:"");
  const treeNodes = getTree();

    return(
      <Modal
        title="浏览"
        visible={visible}
        wrapClassName="vertical-center-modal out-model"
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <pre style={{whiteSpace:"pre-line"}}>
          只能选择文件，如需选择文件夹，请选择文件后自行修改。
        </pre>
        <div  style={{maxHeight:"600px",marginTop:"10px" ,overflowY:"scroll"}}>
          <Tree
            showLine
            expandedKeys = {expandedKeys?expandedKeys:[]}
            onSelect={onSelect}
            onExpand={onExpand}
            className="cloudetlTree"
            loadData={onLoadData}
          >
            { treeNodes }
          </Tree>
        </div>

      </Modal>
    )
};

export default connect(({ foldertree }) => ({
  foldertree
}))(FolderTree)
