/**
 * Created by Administrator on 2017/11/30.
 */
import CommonLayout from 'components/layout/CommonLayout'
import { Modal } from 'antd'
import { getVersion } from '../../../../../services/gather';
import style from './style.css';
import baseInfo from 'config/baseInfo.config';

const GovernmentLayout = (props) => {

    const menuConfig = {
        title: 'ETL',
        path: '/gather',
        //自定义布局路由
        noCard: ["/gather/qualityAnalysis/designSpace","/gather/qualityAnalysis/aduit4W"],
        list: [
            {
                title: '任务中心',
                icon: 'taskcenter',
                path: '/gather/taskcenter',
                list: [
                    {
                        name: '我的转换任务',
                        path: '/gather/taskcenter/transcenter',
                        icon: 'transcenter'
                    },
                    {
                        name: '我的调度任务',
                        path: '/gather/taskcenter/jobscenter',
                        icon: 'jobscenter'
                    },
                    {
                        name: '全部转换任务',
                        path: '/gather/taskcenter/transcenter/all',
                        icon: 'transcenter'
                    },
                    {
                        name: '全部调度任务',
                        path: '/gather/taskcenter/jobscenter/all',
                        icon: 'jobscenter'
                    }
                ]
            },
            {
                name: '设计平台',
                path: '/gather/designplatform',
                icon: 'designplatform'
            },
            {
                title: '资源中心',
                icon: 'resourcecenter',
                path: '/gather/resourcelist',
                list: [
                    {
                        path: '/gather/resourcelist/DataSystem',
                        name: '数据库列表',
                        icon: 'database'
                    },
                    {
                        path: '/gather/resourcelist/Server',
                        name: '服务器列表',
                        icon: 'desktop'
                    },
                    {
                        path: '/gather/resourcelist/Cluster',
                        name: '服务器集群',
                        icon: 'appstore-o'
                    },
                    {
                        path: '/gather/resourcelist/HadoopCluster',
                        name: 'Hadoop集群',
                        icon: 'exception'
                    },
                    {
                        path: '/gather/resourcelist/SparkEngine',
                        name: 'Spark引擎',
                        icon: 'star-o'
                    },
                    {
                        path: '/gather/resourcelist/ExecutionEngine',
                        name: '执行引擎',
                        icon: 'trademark'
                    },
                    {
                        path: '/gather/resourcelist/FileSystem',
                        name: '文件管理',
                        icon: 'folder'
                    },
                    {
                        path: "/gather/resourcelist/auth",
                        name: "ETL授权",
                        icon: "user"
                    }
                ]

            }
        ],
    };

    if (baseInfo.premit && baseInfo.premit.includes("quality")) {
       menuConfig.list.splice(menuConfig.list.length-1,0,{
            title: '质量分析',
            path: '/gather/qualityAnalysis',
            icon: 'pie-chart',
            list: [
                {
                    name: '数据评估',
                    path: '/gather/qualityAnalysis/index',
                    icon: 'desktop'
                },
                {
                    name: '数据总览',
                    path: '/gather/qualityAnalysis/indexPage',
                    icon: 'desktop'
                },
                {
                    name: '分析任务',
                    path: '/gather/qualityAnalysis/taskList',
                    icon: 'bars'
                },
                {
                    name: '任务平台',
                    path: '/gather/qualityAnalysis/designSpace',
                    icon: 'bulb'
                },
                {
                    name: '数据字典',
                    path: '/gather/dataDictionary',
                    icon: 'profile'
				},
                {
                    name: '数据稽核4W',
                    path: '/gather/qualityAnalysis/aduit4W',
                    icon: 'safety'
                }
            ]
        });
    }

    console.log(menuConfig,"布局");

    const getPlatformInfo = (e) => {
        e.preventDefault();

        getVersion().then((res) => {
            const { code } = res.data;
            if (code === "200") {
                Modal.info({
                    title: '版本信息',
                    content: `${res.data.msg}`,
                    iconType: SITE_NAME==="noLogo"?"info-circle":baseInfo.iconType
                });
            }
        });
    };

    return (
        <CommonLayout className="gather" title="云化数据集成系统" getPlatformInfo={getPlatformInfo} menuConfig={menuConfig}  {...props} />
    )
};


export default GovernmentLayout;
