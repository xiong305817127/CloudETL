import { Button,Input,message } from 'antd';
import { connect } from 'dva';
import { insert_platform_HDFStree,update_platform_HDFStree,delete_platform_HDFStree } from '../../../../services/metadata';
import { getChainByChildId } from 'utils/utils';
import Modal from 'components/Modal';

let reload=1;
class AddSubdirectories extends React.Component {
  constructor(props) {
    super(props);
    // 初始状态
    this.state = {
      directoryName: '',
    };
  }

  componentWillReceiveProps(nextProps) {
    console.log(nextProps)
    const { selectedTitle, visible } = nextProps.addsubdirectories;
    // 由隐藏切换为显示时执行
    if (visible) {
      this.setState({
        directoryName: selectedTitle,
      });
    }
  }
  // 输入框内容改变时
  handleChange = (e) => {
    this.setState({
      directoryName: e.target.value,
    });
  };

  handleCallBack(res){

    const { msg,code } = res.data;

    console.log(this.props)
    // if(msg && msg === "Success"){
    if(code && code === "200"){
        message.success("操作成功！");
        const { dispatch } = this.props;
        dispatch({ type: 'metadataCommon/getHdfsTree', force: true }); // 强制刷新公共HDFS信息
        dispatch({
          type:'datasystemsegistration/changeView',
          payload:{
            actionKey:"updatemodel"
          }
        })
    }
  }

  handleOk = () => {
    console.log(this,"qwe");
   
      const { account } = this.props;
      const { directoryName } = this.state;
      const { treeData, selectedKeys } = this.props.addsubdirectories;
      const parentId = selectedKeys && selectedKeys.length ? selectedKeys[0] : 0;
      const treeChain = getChainByChildId(treeData, parentId, 'value');
      console.log(treeData,"treeData=====",parentId);
      console.log(treeChain,"treeChain===");
      console.log( this.props.addsubdirectories," this.props.addsubdirectories");
      let allPathname = !Array.isArray(treeChain) ? '' : treeChain.map(c => `/${c.label}`).join('');
      allPathname += `/${directoryName}`;

      console.log(allPathname,"allPathname====")
    switch(this.props.addsubdirectories.doWhat){
      case '新建子目录':
        const obj1={
          directoryName: directoryName,
          creator: account.username,
        }
        if (this.props.addsubdirectories.selectedKeys) {
          obj1.parentId = parentId;
          obj1.allPathname = allPathname;
        }
        obj1.renterId = account.renterId;
        insert_platform_HDFStree(obj1).then((res)=>{this.handleCallBack(res)});
        break;
      case '重命名':
        const obj2= {
          id: parentId,
          newdirectoryName: directoryName,
          allPathname,
        }
        update_platform_HDFStree(obj2).then((res)=>{this.handleCallBack(res)});
        break;
      case '删除':
        const obj3=[{id: parentId}];
        delete_platform_HDFStree(obj3).then((res)=>{this.handleCallBack(res)});
        break;
      default:
        break;
    }
    const {dispatch}=this.props;
    dispatch({
      type:"addsubdirectories/show",
      visible:false,
    });
  };
  handleCancel = () => {
    const {dispatch}=this.props;
    dispatch({

      type:"addsubdirectories/show",
      visible:false,
      selectedTitle: '',
    });
  }
  render() {
    const {visible}=this.props.addsubdirectories;
    const title=this.props.addsubdirectories.doWhat;
    return (
      <div>
        <Modal
          title={title}
          visible={visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          maskClosable={false}
        >
          {title==="删除"?
          <div>确定删除？<Input disabled={true} ref="directoryName" value={this.state.directoryName}/></div>
          :
          <div>目录名称：<Input ref="directoryName" value={this.state.directoryName} onChange={this.handleChange} placeholder="请输入目录名"/></div>}
        </Modal>
      </div>
    );
  }
}
export default connect(({ addsubdirectories, account }) => ({
  addsubdirectories,
  account,
}))(AddSubdirectories);
