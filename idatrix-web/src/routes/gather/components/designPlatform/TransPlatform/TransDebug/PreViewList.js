import { Tabs } from "antd";
import TableList from "components/TableList";
const TabPane = Tabs.TabPane;

const index = ({ data }) => {

    const getColumns = ( columns ) => {
        return columns.map(index => {
            return {
                title: index,
                dataIndex: index,
                key: index,
                width: "150px"
            }
        });
    };

    console.log(data,"数据");

    return (
        <Tabs animated={false} type="card">
            {
                [...data.keys()].map(index=>(
                    <TabPane tab={index} key={index}>
                        <TableList 
                            columns={getColumns(data.get(index).columns)}
                            dataSource={data.get(index).dataSource}
                            scroll={{ y: 600 }}
                            pagination={false} 
                        />
                    </TabPane>
                ))
            }
        </Tabs>
    )
}

export default index;