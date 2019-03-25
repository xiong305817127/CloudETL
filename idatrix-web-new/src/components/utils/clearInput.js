import React from "react"

/**
 * 用于清理input自动填充
 */
export default ()=>(
    <div style={{height:"0",width: "0",opacity: 0}}>
        <input type="text" name="username"/>
        <input type="password" name="password"/>
    </div>
)