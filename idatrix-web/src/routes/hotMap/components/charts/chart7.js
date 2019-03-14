import React from "react";
import { Progress, Row, Col, Dropdown, Button, Menu, Icon, Tooltip} from "antd"
import style from "./style.less";
import {dbRank} from "../../configs"

class chart7 extends React.Component{
    constructor(){
        super();
        this.state = {
            selectedDb: "mysql"
        }
    }

    menu = (
        <div className={style.clear_menu_back}>
            <Menu>
                {
                  Object.entries(dbRank.top).map((val,index)=> (
                      <Menu.Item onClick={()=>{this.setState({selectedDb:val[0]})}} key={"selectMoth" + index}>{val[0]}</Menu.Item>
                    )
                  )
                }
            </Menu>
        </div>
      );

    render(){

        const {total,tables} = dbRank.top[this.state.selectedDb];
        const {total1,tables1} = dbRank.bottom;

        return (
        <div className="flex_c flex_1">

            {/* 上方表格 */}
            <div className="flex_1 padding_16 flex_c" >
                <p className={style.baseFont}>数据库数据量增量排行榜</p>
                <p className={style.baseFont + " init-ant-btn"}>选择数据库：
                    <Dropdown overlay={this.menu} >
                        <Button style={{ marginLeft: 8 }} size="small">
                            {this.state.selectedDb} <Icon type="down" />
                        </Button>
                    </Dropdown>
                </p>
                <div className={style.unit_list + " flex_1 resetScroll"}>
                    {
                        tables.map((val,index)=>{
                            return (
                                <Row key={"dbRanktop" + index}>
                                    <Col span={8}>
                                        <p 
                                            className={style.baseColor}
                                            style={{overflow:"hidden", textOverflow:"ellipsis",whiteSpace:"nowrap"}}
                                        >   
                                            <Tooltip placement="left" title={val[0]}>
                                                <span
                                                    style={{display:"inline-block"}}
                                                >
                                                {val[0]}
                                                </span>
                                            </Tooltip>
                                        </p>
                                    </Col>
                                    <Col span={1} />
                                    <Col span={15}>
                                        <Progress showInfo percent={Math.floor(100*(val[1]/total))} strokeLinecap="square" strokeColor="#81d5f8" />
                                    </Col>
                                </Row>
                            )
                        })
                    }
                </div>
            </div>,

            {/* 下方表格 */}
            <div className="flex_1 padding_16 flex_c">
                <p className={style.baseFont}>数据处理量总量Top10</p>
                <div className={style.unit_list + " flex_1 resetScroll"}>
                    {
                        tables1.map((val,index)=>{
                            return (
                                <Row key={"dbRankBottom" + index}>
                                    <Col span={8}>
                                        <p 
                                            className={style.baseColor}
                                            style={{overflow:"hidden", textOverflow:"ellipsis",whiteSpace:"nowrap"}}
                                        >   
                                            <Tooltip placement="left" title={val[0]}>
                                                <span
                                                    style={{display:"inline-block"}}
                                                >
                                                {val[0]}
                                                </span>
                                            </Tooltip>
                                        </p>
                                    </Col>
                                    <Col span={1} />
                                    <Col span={15}>
                                        <Progress showInfo percent={Math.floor(100*(val[1]/total1))} strokeLinecap="square" strokeColor="#81d5f8" />
                                    </Col>
                                </Row>
                            )
                        })
                    }
                </div>
            </div>
        </div> )
    }
}

export default chart7;
            