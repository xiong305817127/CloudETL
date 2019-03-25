import { connect } from "dva";
import { Icon,message } from "antd";
import { params,calc,intType } from "config/jsplumb.config.js";
import styles from '../index.less';
import EditCell from "components/common/EditCell"

const index = ({ biModelId,dispatch,canEdit })=>{

	const { measure,mainTableId,dimension,measureView } = biModelId;

	//修改值
	const modifyField = (column,value,record)=>{
		if(!canEdit){
			return;
		}
		dispatch({
			type:"biModelId/save",
			payload:{
				measure:measure.map(index=>{
					if(index.id === record.id){
						index[column] = value
					}
					return index;
				})
			}
		})
	}

	const onDragStart = (ev,item)=>{
		if(!canEdit){
			return false;
		}
      	ev.dataTransfer.setData("otherName","");
		ev.dataTransfer.setData("otherId",mainTableId);
		for(let index of params){
			ev.dataTransfer.setData(index,item[index]);
		}
    } 

    const onDragOver = (ev)=>{
		ev.preventDefault();
	}

	const onDrop = (ev)=>{
		if(!canEdit){
			return false;
		}
		let id=ev.dataTransfer.getData("otherId");
		let name=ev.dataTransfer.getData("otherName");
		let item = {};
		for(let index of params){
			item[index] = ev.dataTransfer.getData(index);
		}
		if(id && mainTableId === id && name){
			if(intType.includes(item.fieldType.toUpperCase())){
				let newDimension = dimension;
				let newMeasure = measure;
				for(let index of newDimension){
					if(index.id === id && index.name === name){
						index.Level = index.Level.filter(obj=>{
							return parseInt(obj.id) !== parseInt(item.id);
						});
					}
				}
				newMeasure.push({
					...item,aggregator:"sum",
					formatString:"#,###.00"
				});
				dispatch({type:"biModelId/save",payload:{dimension:newDimension,measure:newMeasure}});
			}else{
				message.warn(`${item.fieldType}类型不能转换成数字类型`)
			}
		}else{
			return false;
		}
	}


	return(
		<div className={styles.rightTable} onDragOver={onDragOver} onDrop={onDrop}>
			<div className={styles.rightHeader}>
				<span>名称</span>
				<span>别名</span>
				<span>聚合方式</span>
				<span>格式</span>
				<span>可见性</span>
			</div>
			<div className={styles.rightContent} >
				{(()=>{
					let newArgs = measure;
					if(measureView){
						newArgs = newArgs.filter(index=>index.visible === true)
					}
					return newArgs.map(item=>(		      			
	      				<div key={item.column} draggable={canEdit?true:false} onDragStart={(ev)=>{onDragStart(ev,item)}} id={item.id} className={styles.item}>
		      				<span className={styles.itemSpan}>{item.column}</span>
							<span className={styles.itemSpan}>
								{
									canEdit?<EditCell text={item.name} onChange={(e)=>{modifyField("name",e,item)}} />:item.name
								}		
							</span>
							<span className={styles.itemSpan}>
								{
									canEdit?<EditCell text={item.aggregator} selectArgs={calc} type="select" onChange={(e)=>{modifyField("aggregator",e,item)}} />:item.aggregator
								}	
							</span>
							<span className={styles.itemSpan}>
								{
									canEdit?<EditCell text={item.formatString}  onChange={(e)=>{modifyField("formatString",e,item)}} />:item.formatString
								}
							</span>
							<span className={styles.itemSpan}>
								<Icon style={{fontSize:"20px",
		  	 						color:item.visible?"#6A9EBB":"#ccc",cursor:"pointer"}} onClick={()=>{modifyField("visible",!item.visible,item,index.name)}} type="eye-o" />
		  	 				</span>
		      			</div>	
		      		))
				})()}
			</div>
		</div>
	)
}

export default connect(({
	biModelId
})=>({ biModelId }))(index);