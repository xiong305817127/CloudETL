/**
 * Created by Administrator on 2017/9/5.
 */

import {Input,Button,Row} from 'antd';
import Style from './ListHeader.css'
import { setTimer } from '../../method';
import Empower from '../../../../components/Empower';
import {withRouter} from 'react-router';

const InputGroup = Input.Group;

const ListHeader = ({location,router,onClick,showBtn,disabled,api})=>{

  const { query } = location;

  const handleChange = (e)=>{
    setTimer(e.target.value,500,(value)=>{
      if(value){
        query.keyword = encodeURIComponent(value);
        query.page = 1;
      }else{
        delete query.keyword
      }
      router.push(location);
    });
  };

  console.log(disabled,"禁用状态");

    return(
      <div id={Style.ListHeader}>
        <InputGroup  size={"small"} style={{ width: 540,padding:"20" }} compact>
          <Input placeholder="请根据名称搜索"  defaultValue={query.keyword?decodeURIComponent(query.keyword):""}  onChange={handleChange} size="large" style={{ width: '82%',height:"40px" }}  />
          <Button size="large" className={Style.btn}   style={{ width: '10%',height:"40px" }}>&nbsp;</Button>
        </InputGroup>
        <hr className={Style.Hr} />
        <Row style={{marginTop:"15px",textAlign:"right",display:showBtn?"none":"block"}}>
          <Empower api={api?api:""}>
            <Button icon="plus" disabled={disabled} onClick={onClick}>新增</Button>
          </Empower>
        </Row>
      </div>

    )
};

export default withRouter(ListHeader);
