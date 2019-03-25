/**
 * 公共布局组件，内页大部分页面都可使用
 */
import { SITE_CUSTOM_THEME } from 'constants';

import DefaultTheme from './default/Index'; // 默认皮肤
import GovernmentTheme from './government/Index'; // 政府皮肤
import { LocaleProvider,message } from 'antd';
import zhCN from 'antd/lib/locale-provider/zh_CN';
import { connect } from "dva";
import { STANDALONE_ETL } from 'constants';


let Comp;

switch (SITE_CUSTOM_THEME) {
  default:
    Comp = DefaultTheme;
    break;
  case 'government':
    Comp = GovernmentTheme;
    break;
}

const CommonLayout = (props)=>{

  const {currentSystemId,permits } = props.system;

  let canLoad = true;
  if(!STANDALONE_ETL){
     canLoad =  permits && permits[currentSystemId] && permits[currentSystemId].length>0;
  }

  return(
    <LocaleProvider locale={zhCN}>
      {
        canLoad?<Comp {...props} />:<div></div>
      }
    </LocaleProvider>
  )
}

export default  connect(({
  system
})=>({system}))(CommonLayout);
