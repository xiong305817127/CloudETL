import React from 'react';
import { Menu, Icon,Row,Col,Select } from 'antd';
import { connect } from 'dva';
import Style from './Worktools.css';
import ToolsItem from '../../common/ToolsItem';
const Option = Select.Option;
import Tools from '../../config/Tools';
import Empower from '../../../../../components/Empower';

let Timer = null;

const Worktools = ({worktools,dispatch})=>{

  const {model2} = worktools;

 const handleChange = (value) =>{
    let num = "";
    if(Timer){
        clearTimeout(Timer);
    }
    Timer = setTimeout(()=>{
      dispatch({
        type:"worktools/changeModel",
        payload:{
          model2:value
        }
      })
    },200);
  }

  const config = new Map([
       ['通用','00'],
       ['文件传输','01'],
       ['文件管理','02'],
       ['大数据','03'],
       ['脚本','04'],
       ['条件','05']
   ]);

  const getArgs = ()=>{
      let args = Object.keys(Tools);
      let args1 = [];
      if(model2 && model2.trim()){
        if(config.has(model2)){
          args1 = args.filter(index=>{
            if(Tools[index].typeFeil === "job" && Tools[index].model == config.get(model2)){
              return true;
            }else{
              return false;
            }
          })
        }else{
          let value = model2.toLowerCase();
          args1 = args.filter(index=>{
            if(Tools[index].typeFeil === "job" && ((Tools[index].type.toLowerCase()).indexOf(value) >= 0 || (Tools[index].text.toLowerCase()).indexOf(value) >= 0 || (Tools[index].name.toLowerCase()).indexOf(value) >= 0)){
              return true;
            }else{
              return false;
            }
          })
        }
      }else{
        args1 = args.filter(index=>{
          if(Tools[index].typeFeil === "job" && Tools[index].model == "00"){
            return true;
          }else{
            return false;
          }
        })
      }


      return args1;
  }



  return (

    <div  onDrop={e=>{ e.preventDefault()}}  onDragOver={e=>{ e.preventDefault()}} id="worktools">
              <Row className={Style.headercolor} >
                <Col span={8} className={Style.control}>&nbsp;</Col>
                <Col span={14}>控件</Col>
              </Row>
              <Row className={Style.resselect}>
                  <Col span={8} className={Style.type}>类型</Col>
                  <Col span={14}>
                      <Select mode="combobox" placeholder="请搜索或选择" defaultActiveFirstOption={false} showArrow={false} filterOption={false} value={model2} style={{width: 130}} onChange={handleChange}>
                        <Option value="通用">通用</Option>
                        <Option value="文件传输">文件传输</Option>
                        <Option value="文件管理">文件管理</Option>
                        <Option value="大数据">大数据</Option>
                        <Option value="脚本">脚本</Option>
                        <Option value="条件">条件</Option>
                     </Select>
                  </Col>
              </Row>
          <Menu
            className={Style.menuStyle}
            mode="inline"
            width={215}
            inlineIndent = "00" >
             {
                 getArgs().map(index=>{
                    return(
                    <Empower  key={index} api={Tools[index].empowerApi} disable-type="hide">
                      <li className="liStyle">
                        <ToolsItem type={index} />
                      </li>
                    </Empower>
                    )
                 })
              }
      </Menu>
    </div>
  )
}


export default  connect(({ worktools }) => ({
  worktools
}))(Worktools);
