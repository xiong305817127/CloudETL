import React from 'react'
import Styles from "./style.less";
import CreateFragment from "react-addons-create-fragment";
import {taskPlay} from  "../../configs"

export default CreateFragment(
    {
        a:  <div className={Styles.share_cycle_1 + " " + Styles.share_cycle +  " flex_1 flex_c"}>
                <div className={Styles.share_cycle_h1 + " flex_1" }>
                    <p>{taskPlay.day}</p>
                </div>
                <div className={Styles.share_cycle_h2 + " flex_1"}>
                    <p>日处理</p>
                    <p>数据量</p>
                </div>
            </div>
        ,b: <div className={Styles.arrow_next}>
            </div>
        ,c: <div className={Styles.share_cycle_2 + " " +  Styles.share_cycle + " flex_1 flex_c"}>
                <div className={Styles.share_cycle_h1 + " flex_1" }>
                    <p>{taskPlay.month}</p>
                </div>
                <div className={Styles.share_cycle_h2 + " flex_1"}>
                    <p>月处理</p>
                    <p>数据量</p>
                </div>
            </div>
        ,d: <div className={Styles.arrow_next}>
            </div>
        ,e: <div className={Styles.share_cycle_3 + " " +  Styles.share_cycle + " flex_1 flex_c"}>
                <div className={Styles.share_cycle_h1 + " flex_1" }>
                    <p>{taskPlay.total}</p>
                </div>
                <div className={Styles.share_cycle_h2 + " flex_1"}>
                    <p>处理</p>
                    <p>数据总量</p>
                </div>
            </div>
        ,f: <div className={Styles.share_cycle_4  + " " +  Styles.share_cycle + " flex_1 flex_c"}>
                <div className={Styles.share_cycle_h1 + " flex_1" }>
                    <p>{taskPlay.total_d}</p>
                </div>
                <div className={Styles.share_cycle_h2 + " flex_1"}>
                    <p>调度任务</p>
                    <p>总处理次数</p>
                </div>
            </div>
    }
)
