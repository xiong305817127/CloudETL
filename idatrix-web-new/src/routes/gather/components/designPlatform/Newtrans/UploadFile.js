/**
 * Created by Administrator on 2017/8/14.
 */
import { Upload,Button,Icon,message,Row,Checkbox,Col } from 'antd';
import Modal from "components/Modal.js";
import { withRouter } from 'react-router';
import {connect} from 'dva';
import { API_BASE_GATHER,STANDALONE_ETL,API_BASE_QUALITY } from '../../../../../constants';

/**
 * 兼容数据质量适配
 * @edit by pwj  2018/10/26
 */

let url = STANDALONE_ETL?"":API_BASE_GATHER;

const UploadFile = ({uploadfile,dispatch,account,location,router})=>{

  const { visible,value,model,fileList,filterType,title,action,disabled } = uploadfile;
  const { vt, username } = account;

  //兼容数据质量的上传
  url = action === "quality"?API_BASE_QUALITY:url;

  const props = {
		name: "file",
    action: url+"/cloud/uploadFile.do",
    headers: {
      authorization: 'authorization-text',
      VT:vt
    },
    data:{
        type: model,
        isCover: value,
        filterType:filterType,
				owner: username
    },
    onChange(info) {

        if (info.file.status === 'done' && info.file.response.code === "200") {
          message.success(`${info.file.name} 文件上传成功。`);
        } else if (info.file.status === 'error' && info.file.response === undefined) {
            message.error(`${info.file.name} 文件过大，请重选择文件。`);
            info.fileList.map(index=>{
              if(index.uid === info.file.uid){
                index.response = "上传文件过大";
                return index;
              }
            });
        }else if(info.file.status != 'removed' && info.file.response && info.file.response.code !== "200" ){
          if(info.file.status != "error"){
            info.file.status = "error";
            info.fileList.map(index=>{
                if(index.uid === info.file.uid){
                    index.status = "error" ;
                    index.response = info.file.response.msg;
                    return index;
                }
            });
            message.error(info.file.response.msg);
          }
        }

        dispatch({
           type:"uploadfile/showModal",
            payload:{
              fileList:info.fileList
            }
        });
    }
  };


  const setModelHide = ()=>{
    fileList.splice(0);

    dispatch({
        type:"uploadfile/hideModal",
        payload:{
          visible:false
        }
    });
    if(model === "ktr" || model === "kjb"){
      const { query } = location;


      const {page} = query;

      if(page){
        delete query.page;
      }else{
        query.page = 1;
      }
      router.push(location);
    }
  };

  const handleSure = ()=>{
    setModelHide();
  };

  const onHandleChange = (e)=>{
    dispatch({
      type:"uploadfile/showModal",
      payload:{
        value:e.target.checked
      }
    })
  };

  return(
      <Modal
        visible={visible}
				title={title}
				zIndex={1050}
        wrapClassName="vertical-center-modal out-model"
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={handleSure}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={setModelHide}>取消</Button>,
                ]}
        onCancel={setModelHide}
      >
              <div style={{float:"right",marginTop:"6px",marginRight:"20px"}}>
                <Checkbox disabled={disabled} checked={value} onChange={onHandleChange}>若文件已存在，是否覆盖</Checkbox>
              </div>
              <Upload {...props} multiple={true} fileList={fileList}>
                <Button>
                  <Icon type="upload" /> 请选择文件上传
                </Button>
              </Upload>

      </Modal>
    )
};

export default withRouter(connect(({ uploadfile,account }) => ({
  uploadfile,account
}))(UploadFile))
