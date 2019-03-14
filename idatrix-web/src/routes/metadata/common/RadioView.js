/**
 * Created by Administrator on 2017/8/23.
 */
import React from 'react';
import { Input,Icon,Button,Radio } from 'antd';
const RadioGroup = Radio.Group;
import { withRouter,location } from 'react-router';
import Style from './RadioView.css';

const RadioView = ({option,router})=>{

  const {location ,radioArgs,defaultValue} = option;
  const { query } = location;

  const handleChange = (e)=>{
      let obj = {};
      if(e.target.value){
        obj.model = e.target.value;
      }
      router.push({...location,query:{...obj}});
  };

  const getdefaultValue = ()=>{
      return query.model?query.model:defaultValue
  };

  return(
      <div className={Style.ContentHeader} id="Search">
        <div  className={Style.searchClass}>
          <RadioGroup  onChange={handleChange} value={getdefaultValue()} >
            {
              radioArgs.map((index)=>{
                return(
                  <Radio key={index.value} value={index.value}>{index.name}</Radio>
                )
              })
            }
          </RadioGroup>
        </div>
      </div>
  )
};

export default withRouter(RadioView);
