import React from "react";
import formulaFuc from "config/formulaFuc.config.js";
import  Modal from "components/Modal";
import styles from "./FormulaFucModel.less";
import { Tree, Table } from 'antd';

import AceEditor from 'react-ace';
import 'brace/mode/mysql';
import 'brace/theme/xcode';
import 'brace/ext/language_tools';

let Timer = null;

const DirectoryTree = Tree.DirectoryTree;
const TreeNode = Tree.TreeNode;

const formatObj = (()=>{
	let obj = {};
	for(let index of formulaFuc){
		let str = index.category.split(".")[1];
		if(obj[str]){
			obj[str].push(index);
		}else{
			obj[str] = [];
			obj[str].push(index);
		}
	}
	return obj;
})();

class FormulaFucModal extends React.Component{

	constructor(props){
		super(props);
		const { value } = props;
		this.state = {
			config:{},
			showError:false,
			error:"",
			value:value
		}
	}

	onSelect = (e,{node:{props}}) => {
		if(props.name){
			this.setState({
				config:{...props},
				showError:false
			})
		}
	};

	componentWillReceiveProps(nextProps){
		const {visible,value} = nextProps;
		if(visible){
			this.setState({value})
		}
	}
	

	getTreeNode = (obj)=>{
		let count = 0;
		return Object.keys(obj).map(index=>{
			return (
				<TreeNode title={index} key={index} >{
					obj[index].map(item=><TreeNode title={item.name} {...item} key={count++} ></TreeNode>)
				}</TreeNode>
			)
		})
	}

	  /*文件表格*/
	  columns =  [
	    {
	    title: 'expression',
	    dataIndex: 'expression',
	    width:"40%",
	    key: 'expression'
	  }, {
	    title: 'result',
	    dataIndex: 'result',
	    width:"10%",
	    className:"result",
	    key: 'result'
	  },{
	    title: 'comment',
	    dataIndex: 'comment',
	    key: 'comment'
	  }];

	  //编辑文本框
	  handleChange = (text)=>{
	  	const { onChange } = this.props;
	  	this.setState({
	  		value:text
	  	})
	  	if(Timer){
	  		clearTimeout(Timer);
	  		Timer = null
	  	};
	  	Timer = setTimeout(()=>{
	  		onChange(text,data=>{
	  			this.setState({
		  			showError:true,
		  			error:data.report
		  		});
	  		});
	  	},800)
	  }

	  handleOk = ()=>{
	  	const { onOk } = this.props;
	  	const { value } = this.state;
	  	onOk(value)
	  }


	render(){
		const { visible,onOk,onCancel} = this.props;
		const { config,showError,error,value } = this.state;

		console.log(formatObj,"公式对象");

		return(
			<Modal
				visible={visible}
				title="公式编辑"
				onOk={this.handleOk}
				onCancel={onCancel}
				zIndex={1020}
				width={1000}
			>	
				<div className={styles.layout}>
					<div className={styles.silder}>
					  <DirectoryTree
				        multiple
				        showIcon={false}
				        onSelect={this.onSelect}
				        onExpand={this.onExpand}
				      >
				        {
				        	this.getTreeNode(formatObj)
				        }
				      </DirectoryTree>
					</div>
					<div className={styles.content}>
						<div className={styles.contentHeader}>
							<AceEditor
							  mode="mysql"
							  theme="xcode"
							  name="prepSql"
							  fontSize={14}
							  showPrintMargin={true}
							  onChange={this.handleChange}
							  value={value}
							  showGutter={true}
							  highlightActiveLine={true}
							  style={{ width: 752, height:200, border: '1px solid #ddd' }}
							  readOnly={false}
							  setOptions={{
								enableBasicAutocompletion: true,
								enableLiveAutocompletion: true,
								enableSnippets: false,
								showLineNumbers: true,
								tabSize: 2,
							  }}
							/>
						</div>
						<div className={styles.footer}>
							{
								showError?(
									<div>{error}</div>
								):Object.keys(config)?(
									<div>
										<div className={styles.name}>{config.name}</div>
										<div>{config.description?
											(<div>
												<span className={styles.description}>Description : </span>{config.description}
											</div>):null}
										</div>
										<div>{config.syntax?
											(<div>
												<span className={styles.description}>Syntax : </span>{config.syntax}
											</div>):null}
										</div>
										<div>{config.returns?
											(<div>
												<span className={styles.description}>Returns : </span>{config.returns}
											</div>):null}
										</div>
										<div>{config.constraints?
											(<div>
												<span className={styles.description}>Constraints : </span>{config.constraints}
											</div>):null}
										</div>
										<div>{config.semantics?
											(<div>
												<span className={styles.description}>Semantics : </span>{config.semantics}
											</div>):null}
										</div>
										<div>{config.examples && config.examples.example instanceof Array?
											(<div>
												<div className={styles.description}>Examples : </div>
												<Table 
													rowKey="expression"
													columns = {this.columns}
													dataSource = {config.examples.example}
													extendDisabled={true}
													pagination={false}
												/>
											</div>):null}
										</div>
									</div>
								):null
							}
						</div>
					</div>
				</div>
			</Modal>
		)
	}
}

export default FormulaFucModal;