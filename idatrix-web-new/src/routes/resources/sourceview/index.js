import React from 'react';
import { connect } from 'dva';
import { withRouter,hashHistory } from 'react-router';
import ReactEcharts from 'echarts-for-react';
import { Row,Col,Icon,Radio ,Card, Tooltip} from 'antd';
import style from './index.less';
import CheckView from '../common/CheckView/index'
import { displayPartsToString } from 'typescript';

class Demo extends React.Component {
   constructor(props){
    super(props);
      this.state = {
        size: 'small',
        dataId:'',
        TetleName:'base',
        monthNamelist:'',
        subCountlist:'',
        pubCountlist:'',
        regCountlist:'',
        echarts: false
      }


      /**
       * 合并方法绑定
       * @edited by steven leo on 2018/09/22
       */
      this.Restubiao = this.Restubiao.bind(this);
      this.ReleaseVolume = this.ReleaseVolume.bind(this);
      this.Subscriptions = this.Subscriptions.bind(this);
      this.OnclickBasis = this.OnclickBasis.bind(this);
      this.OnclickType = this.OnclickType.bind(this);
      this.onClickTitlle = this.onClickTitlle.bind(this);
      this.handleClick = this.handleClick.bind(this);
    }


  /**
   * 以下三个方法简化为统一由model触发数据更新，
   * 不再反复请求数据
   * @edited by steven leo on 2018/09/22
   */
  /*注册量数据*/
  Restubiao(){
    const {dispatch} = this.props;
    dispatch({
      type: "indexModel/echartsData",
      payload:{
        select: "regCount"
      }
    })
  }

  /**
   * 防止echarts变形
   */
  componentDidMount(){
    this.setState({
      echarts: true
    })
  }
  /*发布量数据*/
  ReleaseVolume=()=>{
    const {dispatch} = this.props;
    dispatch({
      type: "indexModel/echartsData",
      payload:{
        select: "pubCount"
      }
    })
  }
   
   /*订阅量数据*/
  Subscriptions(){
    const {dispatch} = this.props;
    dispatch({
      type: "indexModel/echartsData",
      payload:{
        select: "subCount"
      }
    })
  }
   

   /*点击主题库跳转页面*/
  OnclickBasis(){
    const { dispatch }=this.props;
    hashHistory.push("/resources/sourceview/viwe/TypeText/base/"+"base");
    dispatch({
      type:"indexModel/setMetaId",
        payload:{
            TetleName:"base",
        }
    });
  }

  /**
   * 用户换算显示数，超出10000则显示万，超过1000000则显示百万，10000000则代表千万，
   * 以此类推
   */
  getRightNum = (n)=>{
    const num = n.toString();
    const length = num.length;

    const result = 
      length<=4 ? 
      (<span>{num}</span>) : 
      (length <= 8) ? 
      (<span>{num.substr(0,length-4) + "." + num.substr(length-4,1)}<span style={{fontSize:"10"}}>万</span></span>) :
      (<span>>1<span style={{fontSize:"10"}}>亿</span></span>);
    return result;
  }

  /*点击部门库跳转页面*/
  OnclickType(){
    const { dispatch }=this.props;
    hashHistory.push("/resources/sourceview/viwe/TypeText/department/"+"department");
      dispatch({
        type:"indexModel/setMetaId",
          payload:{
              TetleName:"department",
              size: 'small',
          }
    });
  }
  /*点击主题库跳转页面*/
  onClickTitlle(){
    const { dispatch }=this.props;
    hashHistory.push("/resources/sourceview/viwe/TypeText/topic/"+"topic");
        dispatch({
          type:"indexModel/setMetaId",
            payload:{
                TetleName:"topic"
            }
        });
  }
  //无数次重复绑定，待优化
  handleClick=(e)=>{
  const { dispatch }=this.props;
  dispatch({
    type:"checkview/getEditResource",
    payload:{id:e.target.id,statue:"hide",type:"count"}
  })
  
  /**
   * for(var i = 0;i<arr.length;i++){
    arr[i].onclick = function(){
      dispatch({
        type:"checkview/getEditResource",
        payload:{id:this.id,statue:"hide",type:"count"}
      })
      }
    }  
   */
  }
 
  render() {
    const { option,subCount,pubCount, regCount,BaseText,select } =this.props.indexModel;
    const { size } = this.state;
    console.log(BaseText,"BaseText")
    return (
      <div  className="padding_20">
        <Row gutter={20}>
          <Col span={18}>
            <Card
              className="resetCardTitle"
              title={
                <div style={{margin:0,padding:0}}>
                  <span>最新6个月的情况</span>
                  <Radio.Group size="small" defaultValue="regCount" onChange={this.onChange} style={{marginLeft:'20px'}} buttonStyle="solid">
                    <Radio.Button key="small" value="regCount" onClick={this.Restubiao}>注册量</Radio.Button>
                    <Radio.Button key="default"  value="pubCount" onClick={this.ReleaseVolume}>发布量</Radio.Button>
                    <Radio.Button key="large" value="subCount" onClick={this.Subscriptions}>订阅量</Radio.Button>
                  </Radio.Group>
                </div>
              }
            >
            {
              this.state.echarts
              &&
              <ReactEcharts
                option={option}
                style={{height: '260px', width: '100%'}}
                notMerge={false}
                lazyUpdate={false}
               

                /**
                 * 以下三个属性无任何用途
                 * 暂时注释
                 * @edited by steven leo on 2018/09/22
                 */
                // theme={"theme_name"}
                // onChartReady={this.onChartReadyCallback}
                // onEvents={this.EventsDict}
              />
            }
             
            </Card>
          </Col>
          <Col span={6}>
          <Card
              className="resetCardTitle"
              title={
                <div style={{margin:0,padding:0}}>
                  <span>总体情况</span>
                </div>
              }
              bodyStyle={{height:308}}
            >
              <div style={{position:"absolute",top: 0, left: 0, top: 48, width: "100%", height: 308}} className="flex_c">
                <div className="flex_r" style={{height:120,paddingTop:20}}>
                  <div className="flex_1">
                    <p className={style.countTitle} style={{color: "#53C806"}}>
                      <Tooltip title={regCount}>
                        {regCount ? this.getRightNum(regCount): "--"}
                      </Tooltip>
                    </p>
                    <p className={style.countTag}>注册量</p>
                  </div>
                  <div className="flex_1">
                    <p className={style.countTitle} style={{color: "#0EAEF5"}}>
                      <Tooltip title={pubCount}>
                        {pubCount ? this.getRightNum(pubCount): "--"}
                      </Tooltip>
                    </p>
                    <p className={style.countTag}>发布量</p>
                  </div>
                  <div className="flex_1">
                    <p className={style.countTitle} style={{color: "#b859fa"}}>
                      <Tooltip title={subCount}>
                        {subCount ? this.getRightNum(subCount): "--"}
                      </Tooltip>
                    </p>
                    <p className={style.countTag}>订阅量</p>
                  </div>
                </div>
                <div className="flex_1 padding_0_20">
                  <div className={style.subButton} style={{backgroundColor:"#53C806"}} onClick={this.OnclickBasis}>
                    <Icon type="schedule" />
                    <span>基础库</span>
                    <Icon type="right" />
                  </div>
                  <div className={style.subButton} style={{backgroundColor:"#0EAEF5"}} onClick={this.OnclickType}>
                    <Icon type="layout" />
                    <span>部门库</span>
                    <Icon type="right" />
                  </div>
                  <div className={style.subButton} style={{backgroundColor:"#b859fa"}} onClick={this.onClickTitlle}>
                    <Icon type="picture" />
                    <span>主题库</span>
                    <Icon type="right" />
                  </div>
                </div>
              </div>
            </Card>
          </Col>
          <Col span={24} style={{marginTop: 20}}>
            <Card
              className="resetCardTitle"
              title="最新资源"
              extra={
                <a href={"#/resources/sourceview/viwe/More"} style={{fontWeight:'bold'}} >查看更多资源>></a>
              }
            >
            <div className="flex_r">
              { BaseText.length ===0 ? (
                <Row >
                    <Col span={24} style={{textAlign:'center',margin:50}}>
                        暂无更多最新资源
                    </Col>
                  </Row>
              ):'' }
              { BaseText?BaseText.map((index)=>(
                  <div
                    key={index.resourceId}
                    className={style.shotCardDiv}
                  >
                    <Card
                      // className={style.shotCard}
                      id={index.resourceId}
                      actions={[
                        <Tooltip title={index.updateTime + "（更新时间）"}>
                          <span type="setting" style={{whiteSpace:"nowrap",textOverflow:"ellipsis",width:"100%",display:"block",textAlign:"center"}}>{index.updateTime}</span>
                        </Tooltip>, 
                        <span id={index.resourceId}>
                          <Tooltip title={"查看次数"}>
                            <Icon type="eye-o"/>
                            <span style={{marginRight:"10px"}}>{index.visitCount}</span>
                          </Tooltip>
                          <Tooltip title={"修改次数"}>
                            <Icon type="edit"/>{index.subCount}
                          </Tooltip>
                        </span>
                      ]}
                    >
                      <div style={{minHeight:100}}>
                        <Tooltip title={"点击查看此资源"}>
                          <p className={style.shotCardTitle} onClick={this.handleClick}  id={index.resourceId}>{index.name}</p>
                        </Tooltip>
                        <p className={style.shotCardContent}>{index.remark}</p>                    
                      </div>
                    </Card>
                  </div>
                )):''}
            </div>
            </Card>
          </Col>
        </Row>
        <CheckView />
      </div>
    );
  }
}
export default connect(({indexModel})=>({
  indexModel
}))(Demo);