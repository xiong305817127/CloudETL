
import { Modal,Tree,message,Alert,Input,Icon } from 'antd';
const TreeNode = Tree.TreeNode;
import { connect } from 'dva';
import Style from './DbTable.css'

const FileModel = ({filemodel,dispatch})=>{
const {visible,treeList,expandedKeys,setFolder,title,model,value,root,pathStr} = filemodel;


 const handleOk = ()=>{
   setFolder(value);
    handleCancel();
  };
 const handleCancel = () => {
     dispatch({
      type:"filemodel/hideModel"
    });
  };

  const onSelect = (e,selectedKeys,info) => {
    console.log(selectedKeys);
    if(selectedKeys.selectedNodes[0]){
      let str = "";
      if(model === "file"){
        if(selectedKeys.selectedNodes[0].props.folder){
          str = e[0];
        }else{
          let num = e[0].lastIndexOf(".");
          if(num === -1){
            str = e[0];
          }else{
            str = e[0].substring(0,num);
          }
        };
      }else{
        if(selectedKeys.selectedNodes[0].props.folder){
          str = e[0];
        }else{
          let num = e[0].lastIndexOf("/");
          if(num === -1){
            str = "";
          }else{
            str = e[0].substring(0,num+1);
          }
        };
      }

      dispatch({
        type:"filemodel/showModel",
        payload:{
          value:str
        }
      })
    }

  };


  const handleChange = (e)=>{
     dispatch({
         type:"filemodel/showModel",
          payload:{
             value:e.target.value
          }
     })
  };

  const onLoadData = (treeNode)=>{
    return new Promise((resolve) => {
      setTimeout(() => {
        dispatch({
          type: "filemodel/getTreeModel",
          payload: {
            obj: {
              type: root,
              path: pathStr + treeNode.props.eventKey,
              depth: 1
            },
            evenKey: treeNode.props.eventKey
          }
        });
        resolve();
      },1000)
    });
  };

  const onExpand = (expandedKeys)=>{
    dispatch({
      type:"filemodel/showModel",
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


  const treeNodes = getTree();
  

    return(
      <Modal
        title="浏览文件"
        visible={visible}
        wrapClassName="vertical-center-modal out-model"
        onOk={handleOk}
        onCancel={handleCancel}>
        <pre style={{whiteSpace:"pre-line"}}>
          {title}
        </pre>
        <Input value={value} onChange={handleChange} style={{margin:"10px 0"}}/>

        <div  style={{maxHeight:"600px",marginTop:"10px" ,overflowY:"scroll"}}>
          <Tree
            showLine
            expandedKeys={expandedKeys?expandedKeys:[]}
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
export default connect(({ filemodel }) => ({
  filemodel
}))(FileModel)
