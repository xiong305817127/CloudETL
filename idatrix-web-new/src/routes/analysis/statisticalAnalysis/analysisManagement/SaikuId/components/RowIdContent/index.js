/**
 * Created by Administrator on 2018/4/18.
 */
import { Layout,Icon,Card,Switch } from 'antd';
import { connect } from 'dva';
import AnalysisTable from 'components/AnalysisTable/';
import styles from './index.less';

const { Header,Content,Sider } = Layout;

const Index = ({ rowid,dispatch })=>{

    const { collapsed,view } = rowid;

    const toggle = ()=>{
        dispatch({
            type:"rowid/save",
            payload:{
                collapsed:!collapsed
            }
        })
    };

    const onChange = ()=>{
        dispatch({
            type:"rowid/save",
            payload:{view:!view}
        })
    };

    return(
        <Layout className={styles.rowidcontent}>
            <Header  className={styles.header}>
                <Icon
                    className="trigger"
                    type={collapsed ? 'menu-unfold' : 'menu-fold'}
                    onClick={toggle}
                />

                <Switch className={styles.switchBtn} checked={view} onChange={onChange} />
            </Header>
            <Layout className={styles.layout}>
                <Sider className={styles.sider}>
                    <Card title="指标"  className={styles.card} extra={<a href="#">More</a>} >
                        <p>Card content</p>
                        <p>Card content</p>
                        <p>Card content</p>
                    </Card>
                    <Card title="列"  className={styles.card} extra={<a href="#">More</a>} >
                        <p>Card content</p>
                        <p>Card content</p>
                        <p>Card content</p>
                    </Card>
                    <Card title="行"  className={styles.card} extra={<a href="#">More</a>} >
                        <p>Card content</p>
                        <p>Card content</p>
                        <p>Card content</p>
                    </Card>
                    <Card title="过滤"  className={styles.card} extra={<a href="#">More</a>} >
                        <p>Card content</p>
                        <p>Card content</p>
                        <p>Card content</p>
                    </Card>
                </Sider>
                <Content className={styles.content}>
                    <AnalysisTable />
                </Content>
            </Layout>
        </Layout>
    )
};

export default connect(({ rowid })=>({ rowid }))(Index);