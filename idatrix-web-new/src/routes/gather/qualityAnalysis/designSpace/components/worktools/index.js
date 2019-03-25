import React from 'react';
import { Menu, Row, Col, Select, AutoComplete } from 'antd';
import styles from './index.less';
import Tools from '../../../common/tools';
import Empower from 'components/Empower';
import { worktoolsType, worktoolsOtherType } from "../../../constant";

const Option = Select.Option;

class index extends React.Component {

    constructor() {
        super();
        this.state = {
            value: ""
        }
        this.getArgs = this.getArgs.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    getArgs = () => {
        const { value } = this.state;
        let args = Object.keys(Tools);
        let args1 = [];
        if (value && value.trim()) {
            if (worktoolsType.has(value)) {
                args1 = args.filter(index => {
                    if (Tools[index].typeFeil === "trans" && worktoolsType.get(value) != "other" && Tools[index].model == worktoolsType.get(value)) {
                        return true;
                    } else if (worktoolsType.get(value) === "other") {
                        if ([...worktoolsOtherType.values()].includes(Tools[index].model)) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                })
            } else {
                let newValue = value.toLowerCase();
                args1 = args.filter(index => {
                    if (Tools[index].typeFeil === "trans" && ((Tools[index].type.toLowerCase()).indexOf(newValue) >= 0 || (Tools[index].text.toLowerCase()).indexOf(newValue) >= 0 || (Tools[index].name.toLowerCase()).indexOf(newValue) >= 0)) {
                        return true;
                    } else {
                        return false;
                    }
                })
            }
        } else {
            args1 = args.filter(index => {
                if (Tools[index].typeFeil === "trans" && Tools[index].model == "12") {
                    return true;
                } else {
                    return false;
                }
            })
        }
        return args1.filter(index=>[...worktoolsType.values()].includes(Tools[index].model));
    };

    handleChange = (value) => {
        this.setState({ value });
    }

    render() {
        console.log(worktoolsType,'质量分析');

        const { value } = this.state;
        //拖拽事件
        const drag = (e, type) => {
            e.dataTransfer.setData('type', type);
        }

        console.log(this.getArgs(), "得到的数据");

        return (
            <div onDrop={e => { e.preventDefault() }} onDragOver={e => { e.preventDefault() }} className={styles.worktools}>
                <Row className={styles.headercolor}>
                    <Col span={8} className={styles.symbol}>&nbsp;</Col>
                    <Col span={14}>控件</Col>
                </Row>
                <Row className={styles.resselect}>
                    <Col span={8} className={styles.type}>类型</Col>
                    <Col span={14}>
                        {/* <Select mode="combobox" placeholder="请搜索或选择" defaultActiveFirstOption={false} showArrow={false} filterOption={false} value={value} style={{ width: 130 }} onChange={this.handleChange}>
                            {
                                [...worktoolsType.keys()].map(index => {
                                    return (
                                        <Option value={index} key={index}>{index}</Option>
                                    )
                                })
                            }
                        </Select> */}
                        <AutoComplete
                            dataSource={[...worktoolsType.keys()]}
                            placeholder="请搜索或选择"
                            onChange={this.handleChange}
                            value={value}
                        />
                    </Col>
                </Row>
                <div className={styles.divMenu} >
                    <Menu
                        className={styles.menuStyle}
                        mode="inline"
                        width={215}
                        inlineIndent="0">
                        {
                            this.getArgs().map(index => {
                                return (
                                    <Empower key={index} api={Tools[index].empowerApi} disable-type="hide">
                                        <li className={styles.liStyle} draggable="true" onDragStart={e => { drag(e, index) }}>
                                            <img src={Tools[index].imgData} title={Tools[index].text} draggable="false" />
                                            <div draggable="false">{Tools[index].name}</div>
                                        </li>
                                    </Empower>
                                )
                            })
                        }
                    </Menu>
                </div>
            </div>
        )
    }
}

export default index;


