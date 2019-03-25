import React from 'react';
import { connect } from 'dva';
import {Layout,Table,Button,Tabl,Tabs, Form, Row, Col,Checkbox } from 'antd';
import SiderContentThree from "../components/SiderContentThree";
const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
const {  Sider, Content } = Layout;
const TabPane = Tabs.TabPane;
//多选按钮：表格1
const plainOptions = ['新建', '修改', '废弃', '删除'];
const defaultCheckedList = ['新建', '修改', '废弃', '删除'];
function onChangeBox(e) {
  console.log(`checked = ${e.target.checked}`);
}
//表格2：
const columns = [
  {
  title: '菜单',
  dataIndex: 'menu',
}, {
  title: '操作',
  dataIndex: 'action',
  width:'70%'
}];
const data = [
  {
  key: '1',
  num: '1',
  menu: '数据资源目录',
  action: '',
}, {
  key: '2',
  num: '2',
  menu: '元数据管理',
  action: '',
}, {
  key: '3',
  num: '3',
  menu: '数据分析&探索',
  action: '',
}, {
  key: '4',
  num: '4',
  menu: '',
  action: '',
}, {
  key: '5',
  num: '5',
  menu: '',
  action: '',
}, {
  key: '6',
  num: '6',
  menu: '',
  action: '',
}, {
  key: '7',
  num: '7',
  menu: '',
  action: '',
}, {
  key: '8',
  num: '8',
  menu: '',
  action: '',
}];
function callback(key) {
  console.log(key);
}
// 全选与反选
const rowSelection = {
  onChange: (selectedRowKeys, selectedRows) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
  },
  getCheckboxProps: record => ({
    disabled: record.name === 'Disabled User',    // Column configuration not to be checked
  }),
};

class MenuManagementTable extends React.Component {
  state = {
    checkedList: defaultCheckedList,
  };
  onChange = (checkedList) => {
    console.log(checkedList,111)
    this.setState({
      checkedList,
    });
  };
  render() {
    return (
      <div>
        <Layout>
          <Sider style={{backgroundColor: '#fff', height: 'calc(100vh - 140px)'}}>
            <SiderContentThree/>
          </Sider>

          <Content style={{marginLeft: 10, backgroundColor: '#fff'}}>
            {/*二个按键+一个表格,全选：rowSelection={rowSelection}*/}
            <FormItem
              wrapperCol={{ span: 20, offset: 2}} //单独设置间距
              style={{marginTop:'20px'}}
            >
              <Row style={{ border:'1px solid #ddd',textAlign:'center'}}>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd',backgroundColor:'#ddd'}}>操作</Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd',backgroundColor:'#ddd'}}>菜单</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{marginLeft:'-50px'}}>数据资源目录</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>
                  &nbsp;&nbsp;
                </Col>

                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{marginLeft:'-60px'}}>元数据管理</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>
                  &nbsp;&nbsp;
                </Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'40px'}}>组织机构管理</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>
                  <CheckboxGroup options={plainOptions} value={this.state.checkedList} onChange={this.onChange} />
                </Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'60px'}}>元数据定义及授权</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>
                  <CheckboxGroup options={plainOptions} value={this.state.checkedList} onChange={this.onChange} />
                </Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{marginLeft:'-60px'}}>数据分析&探索</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{marginLeft:'-20px'}}>查询服务</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>

                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{marginLeft:'-20px'}}>任务调度</Checkbox>
               </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>&nbsp;&nbsp;</Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd', borderBottom:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12} style={{ borderBottom:'1px solid #ddd'}}>
                  <Checkbox >新建</Checkbox>
                  <Checkbox >删除</Checkbox>
                </Col>
                <Col span={12} style={{ borderRight:'1px solid #ddd'}}>
                  <Checkbox onChange={onChangeBox} style={{paddingLeft:'50px'}}>数据资源全景图</Checkbox>
                </Col>
                <Col span={12}>
                  <Checkbox >新建</Checkbox>
                  <Checkbox >删除</Checkbox>
                </Col>
              </Row>
            </FormItem>

          </Content>

        </Layout>
      </div>
    );
  }
}

export default connect(({ system, menuManage }) => ({
  system,
  menuManage,
}))(MenuManagementTable);
