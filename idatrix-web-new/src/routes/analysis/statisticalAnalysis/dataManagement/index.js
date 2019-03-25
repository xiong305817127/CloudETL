/**
 * Created by Administrator on 2018/4/17.
 */
import { Input,Icon,Row,Button,Col } from 'antd';
import styles from './index.less';
import TableList from 'components/TableList'; // 自定义表格
import Search from '../../../../components/Search';
import { getOlap } from 'services/analysisStatistical'

const ButtonGroup = Button.Group;

const Index = ()=>{

    const data = [
        { key: 1, name: '超市销售统计', status: "启用", creator: 'admin', see: '公开',cubes:"sales",date:"2018-10-11 09:12:00" },
        { key: 2, name: '工作量统计', status: 42, creator: 'user1', see: '仅自己',cubes:"cube1",date:"2018-10-11 09:12:00"},
    ];

    const columns = [
        {title: '数据集', dataIndex: 'name', key: 'name'},
        {title: '状态', dataIndex: 'status', key: 'status'},
        {title: '创建人', dataIndex: 'creator', key: 'creator'},
        {title: '可见性', dataIndex: 'see', key: 'see'},
        {title: 'cubes', dataIndex: 'cubes', key: 'cubes'},
        {title: '更新时间', dataIndex: 'date', key: 'date'},
        {title: '操作', dataIndex: 'action', key: 'action',
            render: () => {
                return(
                    <div>
                        <a href="javascript:;">编辑</a>&nbsp;&nbsp;
                        <a href="javascript:;">复制</a>
                    </div>

                )
            }
        }
    ];

    const handleAdd = ()=>{
        getOlap().then((data)=>{
            console.log(data);
        })
    };

    return(
        <div className={styles.dataManagement}>
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
                        <ButtonGroup  >
                            <Button type="primary" onClick={handleAdd}>新增</Button>
                            <Button type="primary">启用</Button>
                        </ButtonGroup>
                    </Col>
                    <Col span={12} style={{textAlign:"right"}}>
                        <Button type="primary" >禁用</Button>
                    </Col>
                </Row>
                <TableList columns={columns} dataSource={data}/>
            </div>
        </div>
    )
};

export default Index;