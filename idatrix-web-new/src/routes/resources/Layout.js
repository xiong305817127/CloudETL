import React from 'react';
import CommonLayout from '../../components/layout/CommonLayout';
import './index.less'

// 菜单配置
const menuConfig = {
  title: '数据共享交换平台',
  list: [
    {
      name: '资源概览',
      path: '/resources/sourceview',
      icon: 'appstore-o',
		},
		{
      name: '资源检索',
      path: '/resources/retrieval',
      icon: 'search',
    },
    {
      name: '云阳数据上报',
      path: '/resources/exchange/datauploader',
      icon: 'appstore-o',
		},
    {
      title:"交换管理",
      name: '交换管理',
      path: '/resources/exchange',
      icon: 'retweet',
       list: [
          {
            name: '日志管理',
            title: '日志管理',
            path: '/resources/exchange/serverData',
            icon:"calendar",
             list:[
              {
                name: '服务日志',
                path: '/resources/exchange/serverData/index',
              }
            ]
          },
           {
            name: '作业管理',
            title: '作业管理',
            path: '/resources/exchange/report',
            icon:"file-text",
             list:[
              {
                name: '交换作业',
                path: '/resources/exchange/report/index',
               
              },{
                name: '上报作业',
                path: '/resources/exchange/report/exchangeData',
              }
            ]
          },
          {
            name: '前置管理',
            title: '前置管理',
            path: '/resources/exchange/front',
            icon:"tags-o"
          } ,
      ]
    },
     {
      title:"系统管理",
      path:"/resources/database",
      icon:"database",
       list: [
          {
            name: '资源分类管理',
            path: '/resources/database/classify',
            icon: 'fork',
          },
          {
            name: '系统参数',
            path: '/resources/database/systemparm',
            icon: 'setting',
          },
          {
            name: '资源维护',
            path: '/resources/database/maintenance',
            icon: 'tool',
            
          },
          {
            name: '订阅关系管理',
            path: '/resources/database/subscription',
            icon: 'check-circle-o',
            
          },
          {
            title: '服务管理',
            path: '/resources/database/service',
            icon:"cloud-o",
            list:[
              {
                name: '源服务管理',
                path: '/resources/database/service/sourceservice',
              },
              {
                name: '共享服务管理',
                path: '/resources/database/service/shareservice',
				        
              }
            ]
          } ,
          {
            title: '基础数据',
            path: '/resources/exchange/basicData',
            icon:"menu-fold",
            list:[
              {
                name: '资源格式分类',
                path: '/resources/exchange/basicData/classification',
              },
              {
                name: '资源格式',
                path: '/resources/exchange/basicData/resourceFormat',
              },{
                name: '共享方式',
                path: '/resources/exchange/basicData/sharingMethod',
              }
            ]
          } ,
      ]
    },
    {
      title:"资源管理",
      path:"/resources/management",
      icon:"bars",
      list: [
        {
          name: '我的资源',
          path: '/resources/management/mysource',
          icon: 'user'
        },{
            name: '数据上报',
            path: '/resources/management/reporting',
            icon: 'windows-o',
          } 
      ]
    },
    {
      title:"注册管理",
      path:"/resources/register",
      icon:"login",
      list: [
        {
          name: '待我审批',
          path: '/resources/register/approval',
          icon: 'question-circle-o',
		     
        },
        {
          name: '我审批的',
          path: '/resources/register/approved',
          icon: 'check-circle-o',
		     
        }
      ]
    },
    {
      title:"发布管理",
      path:"/resources/release",
      icon:"notification",
	    empowerApi: '/resources/release',
      list: [
        {
            name: '待我审批',
            path: '/resources/release/approval',
            icon: 'question-circle-o'
        },
        {
            name: '我审批的',
            path: '/resources/release/approved',
            icon: 'check-circle-o'
        }
      ]
    },
     {
      title:"订阅管理",
      path:"/resources/subscription",
      icon:"link",
      list: [
        {
            name: '我订阅的',
            path: '/resources/subscription/mysubscriptions',
            icon: 'user',
            
        },
        {
            name: '订阅关系管理',
            path: '/resources/subscription/Relationship', //alisa 2019-9-26日填写
            icon: 'profile',
            
        },
        {
            name: '待我审批',
            path: '/resources/subscription/approval',
            icon: 'question-circle-o',
            
        },
        {
            name: '我审批的',
            path: '/resources/subscription/approved',
            icon: 'check-circle-o',
            
        }
      ]
    },
  /* {
      title:"框架测试",
      path:"/resources/test",
      icon:"notification",
      list: [
        {
            name: '测试1',
            path: '/resources/test/dvatest',
            icon: 'question-circle-o',
        },
        {
            name: '测试2',
            path: '/resources/test/fuctest',
            icon: 'check-circle-o',
        }
      ]
    },*/
  ],
};

// 输出内容
class Layout extends React.Component {

  render() {

    console.log({...this.props},"加载布局");
    console.log("资源目录布局",menuConfig);

    return (
      <CommonLayout menuConfig={menuConfig} title="数据共享交换平台" {...this.props} />
      );
  }
}
export default Layout;


/*
    {
      name: '基础数据',
      path: '/resources/database',
      icon: 'menu-unfold',
      // empowerApi: '/DataResourceController/getAllResource',
    }, {
      name: '基础服务资源',
      path: '/resources/server',
      icon: 'hourglass',
      // empowerApi: '/DataResourceController/getAllResource',
    },
    
    {
      name: '我的资源',
      path: '/resources/mysource',
      icon: 'environment-o',
      // empowerApi: '/myResourceController/getMyApprove',
    },

  {
      name: '数据上报',
      path: '/resources/reporting',
      icon: 'environment-o',
      // empowerApi: '/myResourceController/getMyApprove',
    },
    {
      name:"资源审批",
      path:"/resources/approval",
      icon:"fork",
      routes:[
         {
          name: '待审批',
          path: '/resources/approval/unapproved',
          icon: 'question-circle-o',
          // empowerApi: '/myResourceController/getMyWillApprove',
        },
        {
          name: '已审批',
          path: '/resources/approval/approved',
          icon: 'check-circle-o',
          // empowerApi: '/myResourceController/getMyApproved',
        },
      ]
    },
        {
      title:"交换管理",
      name: '交换管理',
      path: '/resources/exchange',
      icon: 'retweet',
      empowerApi: '/resources/exchange',
       list: [
          {
            name: '日志管理',
            title: '日志管理',
            path: '/resources/exchange/serverData',
            icon:"tags-o",
            empowerApi: '/resources/exchange/serverData',
             list:[
              {
                name: '服务日志',
                path: '/resources/exchange/serverData/index',
                empowerApi: '/resources/exchange/serverData/index',
              }
            ]
          },
           {
            name: '作业管理',
            title: '作业管理',
            path: '/resources/exchange/report',
            icon:"tags-o",
            empowerApi: '/resources/exchange/report',
             list:[
              {
                name: '交换作业',
                path: '/resources/exchange/report/index',
                empowerApi: '/resources/exchange/report/index',
              },{
                name: '上报作业',
                path: '/resources/exchange/report/exchangeData',
                empowerApi: '/resources/exchange/report/exchangeData',
              }
            ]
          },
          {
            name: '前置管理',
            title: '前置管理',
            path: '/resources/exchange/front',
            icon:"tags-o",
            empowerApi: '/resources/exchange/front',
          } ,
      ]
    },
 */