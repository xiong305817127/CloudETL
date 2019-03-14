import React from 'react';
import { connect } from 'dva'
import Layout from "./components/layout.hotmap.js";

import ExhibitionPage from "./dataExhibition"
import SharingPage from "./dataSharing"
import UserInfoComponent from "./components/userInfo"
import ToJS from "../../components/utils/toJS"

const App = ({userInfo})=>{
    return (
        <Layout 
            UserInfoComponent={UserInfoComponent} 
            userInfo={userInfo} 
            ExhibitionPage={ExhibitionPage} 
            SharingPage={SharingPage} 
        />
    )
};


export default connect(
    ({hotMap})=>({
        userInfo: hotMap.get("userInfo")
    })
)(ToJS(App));
