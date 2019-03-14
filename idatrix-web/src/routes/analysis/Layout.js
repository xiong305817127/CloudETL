import React from 'react';
import CommonLayout from '../../components/layout/CommonLayout';
import baseInfo from "config/baseInfo.config.js";

// 菜单配置
const menuConfig = {
  title: '数据分析&探索',
  //自定义布局路由
  noCard: ["/analysis/DataModel", "/analysis/ReportForms/New"],
  list: [
    {
      name: '数据查询',
      path: '/DataQueryTable',
      icon: 'search',
    },
    // {
    //   name: '统计分析（2.0）',
    //   path: '/StatisticalAnalysisTable',
    //   icon: 'dot-chart',
    // },
    {
      name: '目录管理',
      path: '/ListManagementTable',
      icon: 'appstore-o',
    },
    {
      name: '全文检索',
      path: '/analysis/FullTextSearch',
      icon: 'folder-open',
    },
    {
      name: '任务管理',
      path: '/analysis/TaskManage',
      icon: 'calendar',
    },
  ]
};

if (baseInfo.premit && baseInfo.premit.includes("bi") ) {
  menuConfig.list = [
    ...menuConfig.list,
    {
      title: 'BI分析',
      path: '/analysis/ReportForms',
      icon: 'layout',
      list: [
        {
          name: '数据模型',
          path: '/analysis/DataModel',
          icon: 'layout',
        },
        {
          name: '新建报表分析',
          path: '/analysis/ReportForms/New',
          icon: 'database'
        },
        {
          name: '打开报表分析',
          path: '/analysis/ReportForms/Open',
          icon: 'filter'
				},
				{
          name: '打开仪表盘',
          path: '/analysis/ReportForms/Dashboards',
          icon: 'pie-chart'
				},
        // {
        //   name: 'BI管理',
        //   path: '/analysis/ReportForms/manage',
        //   icon: 'filter'
        // }
      ]
    }
  ]
}

/*
  禁用仪表盘
        {
          name: '仪表盘',
          path: '/analysis/ReportForms/Dashboards',
          icon: 'filter'
        }
* {
    title: '多维分析',
    path: '/analysis/StatisticalAnalysisTable',
    icon: 'global',
    list:[
      {
        name: '数据集管理',
        path: '/analysis/StatisticalAnalysisTable/DataManagement',
        icon: 'database'
      },
      {
        name: '自助分析管理',
        path: '/analysis/StatisticalAnalysisTable/AnalysisManagement',
        icon: 'filter'
      }
    ]
  },
  ],
};

 
  ],

/*
* 
*
* */


// 输出内容
class Layout extends React.Component {
  render() {
    return (<CommonLayout title="数据分析&探索" menuConfig={menuConfig} {...this.props} />);
  }
}
export default Layout;
