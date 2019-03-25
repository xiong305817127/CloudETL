/**
 * Created by Administrator on 2018/4/3.
 */
import React from 'react';
import { connect } from 'dva';
import styles from './index.less';
import { Row,Select,Menu,Icon } from 'antd';

const Option = Select.Option;
const SubMenu = Menu.SubMenu;

const Index = ({ rowid })=>{

    const handleChange = (e)=>{
        console.log(e);
    };

    const handleClick = (e)=>{
        console.log(e);
    };

    return(
        <div className={styles.rowidsider}>
            <div className={styles.header}>数据集</div>
            <Row className={styles.select}>
                <Select
                    defaultValue="all"
                    style={{ width: "100%" }}
                    onChange={handleChange}
                >
                    <Option value="all" key="all">选择多维数据</Option>
                </Select>
            </Row>
            <Row>
                <div className={styles.index}>指标</div>
                <Menu
                    onClick={handleClick}
                    defaultSelectedKeys={['1']}
                    defaultOpenKeys={['sub1']}
                    mode="inline"
                >
                    <SubMenu key="sub1" title={<span><Icon type="mail" /><span>Store</span></span>}>
                            <Menu.Item key="1">Store Sqft</Menu.Item>
                            <Menu.Item key="2">Grocery Sqft</Menu.Item>
                    </SubMenu>
                </Menu>
            </Row>
            <Row>
                <div className={styles.index}>维度</div>
                <div>
                    <Menu
                        onClick={handleClick}
                        defaultSelectedKeys={['1']}
                        defaultOpenKeys={['sub1']}
                        mode="inline"
                    >
                        <SubMenu key="sub1" title={<span><Icon type="mail" /><span>Has coffee bar</span></span>}>
                            <Menu.Item key="1">(All)</Menu.Item>
                            <Menu.Item key="2">Grocery Sqft</Menu.Item>
                        </SubMenu>
                        <SubMenu key="sub2" title={<span><Icon type="mail" /><span>Store</span></span>}>
                            <Menu.Item key="3">Stores</Menu.Item>
                            <Menu.Item key="4">(All)</Menu.Item>
                            <Menu.Item key="5">Store Country</Menu.Item>
                            <Menu.Item key="6">Store State</Menu.Item>
                            <Menu.Item key="7">Store City</Menu.Item>
                            <Menu.Item key="8">(All)</Menu.Item>
                            <Menu.Item key="9">Store Sqft</Menu.Item>
                            <Menu.Item key="10">(All)</Menu.Item>
                            <Menu.Item key="11">Store Type</Menu.Item>
                        </SubMenu>
                        <SubMenu key="sub3" title={<span><Icon type="mail" /><span>Store Type</span></span>}>
                            <Menu.Item key="12">(All)</Menu.Item>
                            <Menu.Item key="13">Store Type</Menu.Item>
                        </SubMenu>
                    </Menu>
                </div>

            </Row>
        </div>
    )
};

export default  connect(({ rowid })=>({ rowid }))(Index);


/*

 */