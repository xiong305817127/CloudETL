
import React from "react";
import PropTypes from "prop-types";
import "./index.less";

class index extends React.Component {

	constructor(props){
		super(props);
		const { defaultValue } = props;
		this.state = {
			key:defaultValue?defaultValue:""
		}
	}

	handleClick(index){
		const { onChange } = this.props;
		this.setState({
			key:index.value?index.value:index
		});
		if(onChange && typeof onChange === "function"){
			onChange(index);
		}
	}

	render() {
		const { list,style } = this.props;
		const { key } = this.state;

		return (
			<ul className="protal-list-view" style={style}>
				{
					list.map(index => {
						if (typeof index === "string") {
							return <li key={index} onClick={()=>{ this.handleClick(index) }} className={`protal-list-li ${key===index?"protal-list-selected":""}`}>{index}</li>
						} else {
							return <li key={index.value} onClick={()=>{ this.handleClick(index) }} className={`protal-list-li ${key===index.value?"protal-list-selected":""}`} >{index.label}</li>
						}
					})
				}
			</ul>
		)
	}
}

index.propTypes = {
	list: PropTypes.array.isRequired,
	defaultValue: PropTypes.string,
	style:PropTypes.object,
	onChange:PropTypes.func
}


export default index;


{/* <li className="protal-list-li">库表</li>
<li className="protal-list-li">文件</li>
<li className="protal-list-li">文件夹</li>
<li className="protal-list-li">接口</li>
<li className="protal-list-li protal-list-selected">未挂资源</li> */}

