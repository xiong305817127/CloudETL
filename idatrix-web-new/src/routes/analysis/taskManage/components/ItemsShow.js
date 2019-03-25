/**
 * Created by Administrator on 2017/3/13.
 */
/**
 * Created by Administrator on 2017/3/13.
 */
import Modal from 'components/Modal';
import { connect } from 'dva'
import Command from './Items/Command'
import HadoopJava from './Items/HadoopJava'
import Hive from './Items/Hive'
import Spark from './Items/Spark'

const ItemsShow = ({dispatch,items})=>{

  const saveItem = (obj)=>{
    dispatch({
      type:"flowspace/saveItem",
      obj:obj
    });
  };


  const { panel } = items;
  items.saveItem = saveItem;

  if(panel === "command"){
    return(
      <Command items={items} />
    )
  }else if(panel === "hive"){
    return(
      <Hive  items={items}/>
    )
  }else if(panel === "spark"){
    return(
      <Spark  items={items}/>
    )
  }else{
    return(
      <HadoopJava items={items}/>
    )
  }

}

export default connect(({ items }) => ({
  items
}))(ItemsShow)
