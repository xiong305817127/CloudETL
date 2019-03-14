/**
 * Created by Administrator on 2017/5/22.
 */
import React from 'react';

import {connect} from 'dva';
import { Button,Radio } from 'antd';
import Style from './DeleteTip.css'
import { delete_front_server,delete_database_table_fields,delete_ftp_table_fields,export_table_struct,delete_table_struct,new_table_struct } from '../../services/example'
import Modal from 'components/Modal';

class DeleteTip extends React.Component{

  state = {
    radioValue:"current"
  }


  hideModel(){
    const {dispatch} = this.props;
    dispatch({
      type:"deletetip/hide",
      visible:false
    })
  }

  handleSizeChange(e){
      this.setState({
        radioValue:e.target.value
      })
  }

  handleSure(){
    const { allSelected,currentSelected,model } = this.props.deletetip;
    const { dispatch } = this.props;
    let args = currentSelected;
    if(this.state.radioValue === "all"){
      args = allSelected;
    }
    if(currentSelected.length === 0){
      notification.error({
        message: '通知信息',
        description: '当前页面无勾选项',
      });
      return false;
    }else {
      if(model === "mfserver"){

        delete_front_server(args).then((res)=>{

        })
        dispatch({
          type:'frontendfesmanage/editmodel',
          model:"deletemodel"
        })
      }else if(model === "database"){
        delete_database_table_fields(args).then((res)=>{
          dispatch({
            type:'datasystemsegistration/editmodel',
            model:"deletemodel"
          })
        })

      }else if(model === "ftp"){
        delete_ftp_table_fields(args).then((res)=>{
          dispatch({
            type:'datasystemsegistration/editmodel',
            model:"deletemodel"
          })
        })
      }else if( model === "dsRegister"){
        const { tip } = this.props.deletetip
          if(tip === "导出"){
            let str = "";
            for(let index of args){
                str = str+","+index.metaid;
            }
            this.downloadFile(str.substring(1));

          }else if(tip === "删除"){

            for(let index of args){
               index.Isremove  = 3;
            }
            delete_table_struct(args).then((res)=>{
                dispatch({
                  type:'dsregistermodel/editmodel',
                  model:"deleteModel"
                })
            })

          }else if(tip === "生成实体表"){
            for(let index of args){
              index.Iscreate  = 1;
            }
            new_table_struct(args).then((res)=>{

                dispatch({
                  type:'dsregistermodel/editmodel',
                  model:"newTableModel"
                })
            })
          }

      }

    }
    this.hideModel();
  }

  downloadFile(str){
      var aLink = document.createElement('a');
      var evt = document.createEvent("HTMLEvents");
      evt.initEvent("click", false, false);//initEvent 不加后两个参数在FF下会报错, 感谢 Barret Lee 的反馈
      aLink.href = "http://192.168.1.117:8080/metadataTable/exportMetadata?ids="+str;
      aLink.dispatchEvent(evt);
      aLink.click();
  }

  render(){
    const { visible,tip } = this.props.deletetip;
    const title = "请确定"+tip+"项";

    return(

      <Modal
        visible={visible}
        title={title}
        wrapClassName="vertical-center-modal MFServerTip"
        footer={[
            <Button key="submit" type="primary" size="large"  onClick={this.handleSure.bind(this)}>确定</Button>,
            <Button key="back" size="large" onClick={this.hideModel.bind(this)}>取消</Button>,
          ]}
        onCancel = {this.hideModel.bind(this)}
      >
        <Radio.Group style={{textAlign:"center"}}  onChange={this.handleSizeChange.bind(this)} defaultValue={this.state.radioValue}>
          <Radio.Button value="current">{tip}当前页勾选项</Radio.Button>
          <Radio.Button value="all">{tip}所有勾选项</Radio.Button>
        </Radio.Group>
      </Modal>
    )
  }
}


export default connect(({ deletetip }) => ({
  deletetip
}))(DeleteTip)
