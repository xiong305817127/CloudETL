import React from 'react';
import { connect } from 'dva'

import Style from './Index.less';

import { SITE_CUSTOM_THEME } from 'constants';
import GovernmentLayout from "./components/style/government/GovernmentLayout";
import NormalLayout from "./components/style/normal/NormalLayout";
import NewTrans from "./components/designPlatform/Newtrans/NewTrans"
import RunTrans from './components/designPlatform/TransPlatform/RunTrans';
import RunDebugger from './components/designPlatform/TransPlatform/RunDebugger';
import RunJob from './components/designPlatform/JobPlatform/RunJob';
import Tip from "./components/designPlatform/Tip/Tip"
import TaskDetails from './components/taskCenter/taskDetails/TaskDetails'
import UploadFile from './components/designPlatform/Newtrans/UploadFile'

import FolderTree from './components/designPlatform/Newtrans/FolderTree'
import FileModel from './components/designPlatform/Newtrans/FileModel'
import TreeView from './components/designPlatform/Newtrans/TreeView'

import { LocaleProvider } from 'antd';
import zhCN from 'antd/lib/locale-provider/zh_CN';


const App = (props) => {

    const { children, location } = props;

    const getStyle = () => {
        switch (SITE_CUSTOM_THEME) {
            case "government":
                return <GovernmentLayout   {...props} />;
            default:
                return <NormalLayout children={children} />
        }
    };

    return (
        <LocaleProvider locale={zhCN}>
            <div style={{ height: "100%" }}>
                {
                    getStyle()
                }
                <NewTrans />
                <Tip />
                <TaskDetails />
                <RunTrans />
                <RunJob />
                <RunDebugger />
                <UploadFile location={location} />

                <FolderTree />
                <FileModel />
                <TreeView />
            </div>
        </LocaleProvider>
    )
};


export default connect(({ app }) => ({
    app
}))(App);
