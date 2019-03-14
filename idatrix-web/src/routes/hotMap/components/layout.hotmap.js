import React from "react";
import Styles from "./layout.less";
import {hashHistory} from "dva/router"
import Clock from "react-live-clock"
import configs from "config/baseInfo.config.js"

/**
 * 背景布局方式
 * ---------------------------
 * | 固 |       适      |  固 |
 * | 定 |       应      |  定 |
 * ---------------------------
 * 
 * 容器布局方式
 * PAGE: Exhibition
 * ---------------------------
 *  1   |     2       |     |
 * -------------------|  4  |
 *          3         |     |
 * ---------------------------
 * 
 * PAGE: SHARE (参考上方)
 */
class layout extends React.Component{
    constructor(){
        super();
        this.state = {
            selected: 1
        }
    }

    // 返回管理端首页
    changeState = ()=>{
        this.setState({
            selected: this.state.selected === 1 ? 2 : 1
        })
    }

    // 返回管理端首页
    goHome = ()=>{
        hashHistory.push("/")
    }

    render(){
        const {ExhibitionPage, SharingPage , UserInfoComponent,userInfo} = this.props;

        return (
            (
                <div className={Styles.layout_back}>
                    <div className={Styles.layout_left}>
                    </div>
                    <div className={Styles.layout_center}>
                        <p className={Styles.layout_title}>
                            {
                                this.state.selected === 1
                                ? 
                                "数据共享交换平台"
                                :

                                "数据管理平台"
                            }
                        </p>
                        <p className={Styles.layout_title_sub}>

                            {/* 此时间可以配置 */}
                            {/* 服务端配置时间后，会自动转为中文显示，无需特意修改中文时区 */}
                            <Clock 
                                format={' h:mm:ss A, YYYY, Mo'}
                                ticking={true}
                            />
                        </p>
                        <p className={Styles.layout_footer}>
                            {
                                typeof CUSTOM_PARAMS !== "undefined" && CUSTOM_PARAMS.COPY_RIGHT
                                ? CUSTOM_PARAMS.COPY_RIGHT
                                : configs.copyright
                            }
                        </p>
                    </div>
                    <div className={Styles.layout_right}>
                    </div>

                    <UserInfoComponent handleChange={this.changeState} userInfo={userInfo} goHome={this.goHome}/>
                    {/* 对应上方图示，布局 */}
                    {
                        this.state.selected === 1
                        &&
                        <ExhibitionPage handleChange={this.changeState} />
                    }

                    {
                        this.state.selected === 2
                        &&
                        <SharingPage handleChange={this.changeState} />
                    }
                </div>
            )
        );
    }
}

export default layout;