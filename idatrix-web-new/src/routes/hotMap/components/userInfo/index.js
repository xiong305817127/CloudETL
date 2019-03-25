import React from "react";
import style from "./style.less"
import {Icon,Tooltip} from "antd";


export default ({handleChange,userInfo,goHome})=>{
    console.log(userInfo)
    return (
        <div className={style.userInfo + " flex_r"}>

            {/* 显示用户信息，目前只显示username，暂不可修改用户信息，后续添加 */}
            <div className="flex_1">
                <span style={{color: "#81d5f8"}}>欢迎 {userInfo ? userInfo.username : "暂未登录"}</span>
            </div>

            {/* 此处用于切换面板信息 */}
            <div className="flex_1">
                <Tooltip placement="top" title="切换面板" onClick={handleChange} style={{cursor:"pointer"}}>
                    <span style={{cursor:"pointer"}}>
                        <Icon type="swap" style={{color: "#81d5f8",}}/>
                        <span style={{color: "#81d5f8"}}>{" "}切换面板</span>
                    </span>           
                </Tooltip>
            </div>

            {/* 此处后续可以处理登录、登出等操作，后续优化 */}
            <div className="flex_1">
                <Tooltip placement="top" title="关闭展示，返回首页"  onClick={goHome}>
                    <span style={{cursor:"pointer"}}>
                        <Icon type="logout" style={{color: "#81d5f8",}}/>
                        <span style={{color: "#81d5f8"}}>{" "}返回首页</span>
                    </span>
                </Tooltip>
            </div>
        </div>
    )
}