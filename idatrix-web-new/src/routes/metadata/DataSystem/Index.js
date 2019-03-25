/*二.数据系统注册：*/
//1.
import { connect } from 'dva';
import  RadioView  from '../common/RadioView';
import DataSystemFrontEnd from './components/DataSystemFrontEnd';
import DataSystemPlatform from './components/DataSystemPlatform';
import DatabaseModel from './components/DatabaseModel'
import RegisterModel from './components/DSRegisterModel'
import FTPmodel from './components/FTPModel'
import RegistersModel from './components/RegisterModel'

//2.
const DataSystemSegistration = ({location,datasystemsegistration})=>{
  //左侧菜单、内容单选菜单radio切换:并行页面
  const option = {
    type:"DSRegister",
    title:"数据系统注册",
    defaultValue:"1",
    radioArgs:[
      {value:"1",name:"前置机"},
      {value:"2",name:"平台"}
    ],
    location:location
  };
  const { model} = datasystemsegistration;
  //按钮：在DataSystemFrontEnd或DataSystemPlatform内触发
  const showView = ()=>{
      if(model == 1){
          return(
            <DataSystemFrontEnd  location={location} datasystemsegistration={datasystemsegistration}/>
          )
      }else{
         return(
            <DataSystemPlatform location={location} datasystemsegistration={datasystemsegistration}/>
         )
      }
  };

  return(
     <div>
       {/*单选切换页面*/}
       <RadioView  option={option} />
       <div>
         {/*切换的主页面组件在此处展示showView：*/}
         {
           showView()
         }
       </div>

       {/*以下为附属组件：*/}
       {/*1.数据库：新建对话框*/}
       <DatabaseModel />
       {/*2.数据库：展示表格*/}
       <RegisterModel />
       {/*3.数据库：编辑对话框*/}
       <FTPmodel />

       <RegistersModel />
     </div>
  )
};

//3.
export default connect(({ datasystemsegistration }) => ({
  datasystemsegistration
}))(DataSystemSegistration);
