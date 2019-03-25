/**
 * 	Description : 双击编辑的单元格 
 *  Date   : 2018.8.17
 *  Author : pwj
 */

import React from "react";
import PropTypes from "prop-types";
import { Input,Select } from 'antd';
import styles from "./EditCell.less";

const Option = Select.Option;

class EditCell extends React.Component{

	constructor(){
		super();
		this.state = {
			visible:false
		}
	}

	//改变状态
	handleChangeVisible(bool){
		console.log("触发onblur事件");

		this.setState({
			visible:bool
		})
	}

	getDomItem(type,props,selectArgs){

		switch(type){
			case "input" :
				return (<Input {...props} />);
				break;
			case "select" :
				return (
					<Select style={{ width:"100%" }} {...props} >
						{
							selectArgs.map(index=><Option key={index.value?index.value:index} value={index.value?index.value:index} >{index.value?index.value:index}</Option>)
						}
					</Select>);
				break;
			default:
				return (<Input {...props} />)
		}
	}


	render(){

		const { visible } = this.state;
		const {
			text = "",
			type = "input",
			onChange,
			selectArgs
		} = this.props;

		return(
			<div className={styles.EditCell} onDoubleClick={()=>{this.handleChangeVisible(true)}} >
				{
					visible?(
						this.getDomItem(type,{
							value:text,
							autoFocus:true,
							onBlur:()=>{this.handleChangeVisible(false)},
							onChange:(e)=>{ 
								if(type && type === "select"){
									onChange(e)
								}else{
									onChange(e.target.value)
								}
							}
						},selectArgs)
					):(<span>{text}</span>)
				}
			</div>
		)
	}
}

React.propTypes = {
	type:PropTypes.string,
	text:PropTypes.string,
	selectArgs:PropTypes.array,
	onChange:PropTypes.func.isRequired
}


export default EditCell;