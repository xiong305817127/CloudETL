/**
 * Created by Administrator on 2018/4/17.
 */
/**
 * Created by Administrator on 2018/4/3.
 */
import React from 'react';
import { Layout } from 'antd';
import { connect } from 'dva';
import styles from './rowId.less';
import RowIdSider from './components/RowIdSider/';
import RowIdContent from './components/RowIdContent/';

const { Sider,Content } = Layout;


class Index extends React.Component{

    componentWillMount(){
        const { dispatch,params:{rowid}} = this.props;
        dispatch({
            type:"rowid/open",
            payload:{rowid}
        })
    }


    render(){
        console.log(this.props,"父属性");

        const { collapsed } = this.props.rowid;

        return(
            <Layout className={styles.layout}>
                <Sider
                    className={styles.sider}
                    collapsed={collapsed}
                >
                    <RowIdSider />
                </Sider>
                <Content className={styles.content}>
                    <RowIdContent />
                </Content>
            </Layout>
        )
    }
}

export default connect(({ rowid })=>({  rowid }))(Index);