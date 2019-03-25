import React from 'react'
import { Input,Icon,Button,Radio } from 'antd';
const RadioGroup = Radio.Group;
const InputGroup = Input.Group;
import Style from './Search.css'
import { connect } from 'dva'
import Search from '../../../../components/Search';

class Search1 extends React.Component{

  state = {
    metaNameCn:""
  };

  handleChange(e){
      const { type } = this.props.option;
      const { dispatch } = this.props;
      console.log(e.target.value);
      console.log(type);
      if(type === "FrontendResManage"){
          dispatch({
             type:"frontendfesmanage/changeView",
              view:e.target.value,
              metaNameCn:this.state.metaNameCn
          })
      }else if(type === "DSRegister"){
        dispatch({
          type:"datasystemsegistration/changeView",
          view:e.target.value,
          metaNameCn:this.state.metaNameCn
        })
      }
  }

  handleClick(keyword){

          const { type } = this.props.option;
          const { dispatch } = this.props;


          if(type === "FrontendResManage"){
            dispatch({
              type:"frontendfesmanage/search",
              // metaNameCn:this.state.metaNameCn
              metaNameCn: keyword
            })
          }else if(type === "DSRegister"){
            dispatch({
              type:"datasystemsegistration/search",
              // metaNameCn:this.state.metaNameCn
              metaNameCn: keyword
            })
          }

  }

  handleSearchChange(e){
      this.setState({
        metaNameCn:e.target.value.trim()
      })
  }


  render(){
    const { option } = this.props;

    return(
      <div>
      <div className={Style.ContentHeader} id="Search">
        {/*<Search
          placeholder={option.placeholder}
          onSearch={this.handleClick.bind(this)}
        />*/}
          <div  className={Style.searchClass}>
            {/*单选框：前置机侧、平台侧*/}
            <RadioGroup onChange = {this.handleChange.bind(this)} defaultValue={option.defaultValue}>
              {
                option.radio.map((index)=>{
                  return(
                    <Radio key={index.value} value={index.value}>{index.name}</Radio>
                  )
                })
              }
            </RadioGroup>
        </div>
      </div>
      </div>
    )
  }
}

export default connect()(Search1);
