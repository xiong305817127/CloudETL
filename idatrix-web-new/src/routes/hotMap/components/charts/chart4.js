import React from "react";
import { Progress, Row, Col } from "antd"
import style from "./style.less";
import createFragment from "react-addons-create-fragment"
import {rank} from "../../configs"

export default createFragment({
    first: <div className="flex_1 padding_16 flex_c">
    <p className={style.baseFont}>数据贡献排行榜</p>
    <div className={style.unit_list + " flex_1 resetScroll"}>
        {
            rank.top.map((val,index)=>{
                return (
                    <Row key={"percent" + index}>
                        <Col span={8}>
                            <p className={style.baseColor}>
                                {val[0]}
                            </p>
                        </Col>
                        <Col span={14}>
                            <Progress showInfo percent={(100*(val[1]/rank.total)).toFixed(3)} strokeLinecap="square" strokeColor="#81d5f8" />
                        </Col>
                    </Row>
                )
            })
        }
    </div>
</div>,
second: <div className="flex_1 padding_16 flex_c">
<p className={style.baseFont}>数据共享排行榜</p>
<div className={style.unit_list + " flex_1 resetScroll"}>
    {
        rank.bottom.map((val,index)=>{
            return (
                <Row key={"percentbottom" + index}>
                    <Col span={8}>
                        <p className={style.baseColor}>
                            {val[0]}
                        </p>
                    </Col>
                    <Col span={14}>
                        <Progress showInfo percent={(100*(val[1]/rank.total)).toFixed(3)} strokeLinecap="square" strokeColor="#81d5f8" />
                    </Col>
                </Row>
            )
        })
    }

</div>
</div>
})
            