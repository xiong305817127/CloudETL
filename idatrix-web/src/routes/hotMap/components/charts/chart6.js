import React from 'react'
import Styles from "./style.less";
import { Table } from 'antd';
import {db} from "../../configs"
const columns = [{
    title: '中文名',
    dataIndex: 'c',
    align: "center",
    width: "50%"
  }, {
    title: '英文名',
    dataIndex: 'e',
    align: "center",
    width: "50%"
}];

// 左侧列表单个条目
const TabItem = ({database,ip,count,selected,index,onChange})=>{
    return (
        <div 
         className={Styles.tabItem + " " + (selected ? Styles.selectedTab : "")}
         onClick = {onChange(index)}
        >
            <p style={{textAlign: "left"}}>{database}</p>
            <p className="flex_r">
                <span className="flex_1" style={{textAlign: "left"}}>IP地址：{ip}</span>
                <span className="flex_1" style={{textAlign:"right"}}>注册的数据表：{count}</span>
            </p>
        </div>
    )
}

// 左侧列表
const TabsGroup = ({dataSource, selected , onChange })=>{
    return (
        <div className="flex_1" style={{position: "relative",zIndex:10}}>
            {
                dataSource.map((val,index)=>{
                    return (
                        <TabItem 
                        {...val} 
                        selected={index === selected} 
                        index={index} 
                        key={"tabdata"+val.ip}
                        onChange = {onChange}
                        />
                    )
                })
            }
            <div className={Styles.back_tab}></div>
        </div>
    );
}

// 右侧表单
const ContentGroup = ({dataSource})=>{
    return (
        <div className={"flex_1 "+ Styles.resetTable + " resetScroll"} style={{maxHeight:"100%",overflowY:"scroll"}}>
            <Table 
            columns={columns}

            // 注意此处在数据中添加了key
            dataSource={dataSource.map((v,i)=>(
                    {
                        c:v[0],
                        e:v[1],
                        key:v[0]+i  // 此处key用于设置rowkey
                    }))}
            bordered={false}
            size="small"
            pagination={false}
            rowKey={"key"}
            />
        </div>
    )
}


export default class TabContent extends React.Component{
    constructor(){
        super();
        this.state = {
            selected: 0,
            db: "mysql"
        }
    }
    handleChange = (index)=>{
        return ()=>{
            this.setState({
                selected: index
            })
        }
    }
    selectDb = (database)=>{
        return ()=>{
            this.setState({
                db: database,
                selected: 0
            })
        }
    }

    render(){
        return (
            <div className="flex_1 flex_c">
                <div className="flex_r flex_1">

                    {/* 三种db的选择框 */}
                    {
                        [
                            "mysql",
                            "dm",
                            "sftp"
                        ].map((val)=>(
                            <div
                                key={"dbselect" + val}
                                onClick={
                                    this.selectDb(val)
                                }
                                className={
                                    Styles.db_select 
                                    + " flex_1 flex_r " 
                                    + (this.state.db === val ? Styles.db_selected : "")
                                }>
                                <div className={Styles["db_" + val] + " flex_1"}>
                                    <div>
                                        {val.toUpperCase()}
                                    </div>
                                </div>
                            </div>
                        ))
                    }
                </div>
                    
                {/* 详细数据列表 */}
                <div className="flex_3 flex_r" style={{padding: "32px"}}>
                    <TabsGroup 
                        dataSource={db[this.state.db]}
                        selected={this.state.selected}
                        onChange = {this.handleChange}
                    />
                    <ContentGroup
                        dataSource={db[this.state.db][this.state.selected].tables}
                    />
                </div>
            </div>
        )
    }
}