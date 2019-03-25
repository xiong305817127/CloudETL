import React from 'react';
import CommonLayout from '../../components/layout/CommonLayout';
import DRMNewFileModel from './DataRelationship/components/Model/DRMNewFileModel';
import DRMselectTable from './DataRelationship/components/Model/DRMselectTable';
import DeleteTip from './common/DeleteTip';
import AddSubdirectories from './DataSystem/components/AddSubdirectories';
import NewStorageTable from './DataSystem/components/NewStorageTable';

import acquisition from './DataAcquisition/model/acquisition';
import { LocaleProvider } from 'antd';
import zhCN from 'antd/lib/locale-provider/zh_CN';



/*{
  name: '元数据采集',
  path: '/DataAcquisition',
  icon: 'area-chart',
},*/
// 菜单配置
const menuConfig = {
  title: '元数据管理',
  list: [
    {
      name: '前置机管理',
      path: '/FrontendResManage',
      icon: 'desktop',
    },
    {
      name: '数据系统注册',
      path: '/DataSystemSegistration',
      icon: 'medicine-box',
    },
    ,
    {
      name: '元数据定义',
      path: '/MetadataDefine',
      icon: 'environment-o',
    },
    {
      name: '数据地图',
      path: '/DataMap',
      icon: 'global',
    },
    {
      name: '数据关系管理',
      path: '/DataRelationshipManagement',
      icon: 'share-alt',
    },
    {
      name: '数据标准文档',
      path: '/DataStandardView',
      icon: 'search',
    },
    {
      name: '数据资源目录',
      path: '/resources/directory',
      icon: 'menu-unfold',
      // empowerApi: '/DataResourceController/getAllResource',
    },
    {
      name: '我的资源',
      path: '/resources/myResource',
      icon: 'appstore-o',
      // empowerApi: '/myResourceController/search',
    },
    {
      name: '我的申请',
      path: '/resources/myApplication',
      icon: 'environment-o',
      // empowerApi: '/myResourceController/getMyApprove',
    },
    {
      name: '待审批',
      path: '/resources/unapproved',
      icon: 'question-circle-o',
      // empowerApi: '/myResourceController/getMyWillApprove',
    },
    {
      name: '已审批',
      path: '/resources/approved',
      icon: 'check-circle-o',
      // empowerApi: '/myResourceController/getMyApproved',
    },
  ],
};

// 输出内容
class Layout extends React.Component {
  render() {
    return (
      <LocaleProvider locale={zhCN}>
        <div style={{ height: '100%' }}>
          <CommonLayout title="元数据管理" menuConfig={menuConfig} {...this.props} />

          <DRMNewFileModel />
          <DRMselectTable />
          <DeleteTip />
          <acquisition />
          <AddSubdirectories />
          <NewStorageTable />

        </div>
      </LocaleProvider>
    );
  }
}
export default Layout;
