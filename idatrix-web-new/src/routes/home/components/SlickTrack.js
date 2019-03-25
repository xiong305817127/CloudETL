//1.定义标签
import React from 'react';
import { Carousel } from 'antd';
import sysIds from '../../../config/systemIdsMap.config'; // 导入子系统id映射表
import homeinlet from '../../../config/homeInlet.config'; // 导入首页入口信息定义
import Style from './SlickTrack.css';

//2.轮播效果：走马灯
class LunBo extends React.Component{

  state = {
    carouselList: ['routes/metadata', 'routes/gather', 'routes/resources'],
  };

  //输出组件内容：html
  render(){
    const { inletsConfig } = this.props;
    return (
      <div className={Style.LunBotu}>
        {/*1.轮播*/}
        <Carousel autoplay={true}>
          {this.state.carouselList.map((path, index) => {
            const sysId = sysIds[path];
            return (<div key={index} className={Style.LunBoimg}>
              <div className={Style.LunBogo}><img src={homeinlet[sysId].img}/></div>
              <div className={Style.LunBoText}>
                <p style={{fontSize:40}}>{homeinlet[sysId].title}</p>
                <span><a href={inletsConfig[sysId].path} style={{color: '#fff'}}>查看详情</a></span>
                <h3>{homeinlet[sysId].desc}</h3>
              </div>
            </div>)
          })}
        </Carousel>
      </div>
    )
  }
}
//3.调用方法
export default  LunBo;
