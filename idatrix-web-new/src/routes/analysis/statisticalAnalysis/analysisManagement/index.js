/**
 * Created by Administrator on 2018/4/17.
 */
import { Input,Icon,Row,Button,Col } from 'antd';
import { Link } from 'dva/router';
import { connect } from 'dva';
import styles from './index.less';
import TableList from 'components/TableList'; // 自定义表格
import Search from '../../../../components/Search';
const ButtonGroup = Button.Group;

const Index = ({dispatch})=>{
    const data = [
        { key: 1, analysis: '超市销售统计',creator: 'admin', description: '公开',date:"2018-10-11 09:12:00" },
        { key: 2, analysis: '工作量统计',creator: 'user1', description: '仅自己',date:"2018-10-11 09:12:00"},
    ];
    
    const handleClick = (name)=>{
        dispatch({
            type:"rowid/excute",
            payload:{
                name
            }
        })  
    };

    const columns = [
        {title: '分析', dataIndex: 'analysis', key: 'analysis'},
        {title: '创建人', dataIndex: 'creator', key: 'creator'},
        {title: '描述', dataIndex: 'description', key: 'description'},
        {title: '更新时间', dataIndex: 'date', key: 'date'},
        {title: '操作', dataIndex: 'action', key: 'action',
            render: (text, record) => {
                return(
                    <Link to={`/analysis/StatisticalAnalysisTable/AnalysisManagement/${record.analysis}`}  onClick={()=>{handleClick(record.analysis)}}>编辑</Link>
                )
            }
        }
    ];

    return(
        <div className={styles.analysisManagement}>
            <header style={{padding:20, marginLeft: 40, marginBottom: 20}}>
                <Search
                    placeholder="输入名称"
                    style={{ width: '500px' }}
                    onSearch={value => this.onSearch(value)}
                />
            </header>
            <div className={styles.content}>
                <Row className={styles.row}>
                    <Col span={12}>
                        <ButtonGroup >
                            <Button type="primary">新增</Button>
                            <Button type="primary">删除</Button>
                        </ButtonGroup>
                    </Col>
                </Row>
                <TableList columns={columns} dataSource={data}/>
            </div>
        </div>
    )
};

export default connect()(Index);