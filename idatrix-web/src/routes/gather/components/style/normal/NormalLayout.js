/**
 * Created by Administrator on 2017/11/30.
 */

import { Layout} from 'antd';
const { Header,Footer } = Layout;
import AppHeader from '../../AppHeader'
import Style from './NormalLayout.css';
import baseInfo from 'config/baseInfo.config';

import { LocaleProvider } from 'antd';
import zhCN from 'antd/lib/locale-provider/zh_CN';



const NormalLayout = ({children})=>{
    
    
    return (
        <LocaleProvider locale={zhCN}>
            <Layout id="gatherSystem" className={Style.Layout} style={{ minHeight:"980px",minWidth:"1300px",width:"100%"}}>
                <Header className={Style.Header}>
                    <AppHeader />
                </Header>
                {
                    children
                }
                <Footer  className={Style.Footer}  style={{ textAlign: 'center' }}>
                    {baseInfo.copyright}
                </Footer>
      
            </Layout>
        </LocaleProvider>
    )
};


export default NormalLayout