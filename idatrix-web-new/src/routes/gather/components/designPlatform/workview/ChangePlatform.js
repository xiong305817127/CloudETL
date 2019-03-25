/**
 * Created by Administrator on 2017/7/1.
 */
import React from 'react';
import { connect }  from 'dva';
import { Switch } from 'antd';



const ChangePlatform = ({changeStatus,status})=>{

  function onChange(checked) {
    changeStatus(checked?"job":"trans");
  }

  return(
      <Switch checkedChildren="调度"   unCheckedChildren="转换"  checked={status==="job"?true:false}  onChange={onChange} style={{ display: "inherit"}}/>
  )
}

export  default connect()(ChangePlatform);
