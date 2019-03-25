import React from 'react'
import Style from "./Welcome.css"


class Welcome extends React.Component{

    render(){
      return(
        <div className={Style.welcomeHeader}>
           <div >欢迎使用云化数据集成系统</div>
           <p>请新建或打开转换（Trans）</p>
        </div>
      )
    }
}

export default Welcome;
