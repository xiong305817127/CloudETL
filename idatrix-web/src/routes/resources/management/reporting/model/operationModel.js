operationModel.js
import { connect } from 'dva';
import ReactDOM from 'react-dom';
import ReactEcharts from 'echarts-for-react';
import { withRouter,hashHistory } from 'react-router';
import { Tree,Menu,Dropdown,Row,Col,Icon,Tabs, Radio,Button,Form,Input,Select,Steps,DatePicker,Upload,message,Tooltip,Popconfirm} from 'antd';
import { API_BASE_CATALOG } from 'constants';
import { uploadFile, convertArrayToTree,downloadFile } from 'utils/utils';
const TreeNode = Tree.TreeNode;
const { TabPane } = Tabs;
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
const Step = Steps.Step;
import TableList from "components/TableList";
import Modal from 'components/Modal';
import style from './style.less';

import { MLZYdeleteDLRecordById,MLZYgetAllDateUploadRecords,MLZYsaveOrUpdateUploadData,MLZYgetPub,MLZYisExistedResourceFile,MLZYdownloadTemplate,
getETLTaskDetailInfoById,getTempDataUploadRecords } from  'services/DirectoryOverview';


class Myreporting extends React.Component {
   constructor(props){
    super(props);
         this.state = {
          current: 0,
          visible:false,
          pagination:{
            pageNum:1,
            pageSize:10
          },
      }   
  }

 
  columnsLoge = [
   {
      title: '上报作业',
      dataIndex: 'importTaskId',
      key: 'importTaskId',
      width: '15%',
     
    },  {
      title: '资源代码',
      dataIndex: 'code',
      key: 'code',
      width: '6%',
    }, {
      title: '资源名称',
      dataIndex: 'name',
      key: 'name',
      width: '15%',
    }, {
      title: '文件名',
      dataIndex: 'pubFileName',
      key: 'pubFileName',
      width: '18%',
    }, {
      title: '数据批次',
      dataIndex: 'dataBatch',
      key: 'dataBatch',
      width: '10%',
     
    }, {
      title: '上传时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: '10%',
    }, {
      title: '入库时间',
      dataIndex: 'importTime',
      key: 'importTime',
      width: '10%',
    }, {
      title: '入库数据量',
      dataIndex: 'importCount',
      key: 'importCount',
      width: '6%',
    }];

 componentDidMount() {
    
 }

   /*点击上报作业 确定*/
   handleOkLoge(){
    this,setState({
        visibleLogs:false
     })
   }

   /*点击上报作业 取消*/
   handleCancelLoge(){
     this,setState({
        visible:true
     })
   }



  render() {
     const { getFieldDecorator } = this.props.form;
    

      const formItemLayout = {
        labelCol: {span:6},
        wrapperCol:{span:12},
      };
     
    return (
          <div>
             <Modal
	            title="执行信息"
	            visible={visible}
	            width={"50%"}
	            footer={[
	                  <Button key="back" size="large" onClick={this.handleCancelLoge.bind(this)}>取消</Button>,
	                  <Button key="submit" type="primary" size="large" onClick={this.handleOkLoge.bind(this)}>确定</Button>,
	                  ]}
	            onCancel={this.handleCancelLoge.bind(this)} >
	                

	                
	    
	          </Modal>
          </div>
    );
  }
}
const MyreportingForm = Form.create()(Myreporting);
export default connect(({reportingModel})=>({
  reportingModel
}))(MyreportingForm);