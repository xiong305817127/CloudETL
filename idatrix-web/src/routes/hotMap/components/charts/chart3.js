import React from "react";
import Echarts from "echarts-for-react";
import { lineChart } from "../../configs";
import style from "./style.less";
import { Dropdown, Button, Menu , Icon} from "antd";
import theme from "../theme/macarons"
import echarts from "echarts"
import configs from "../../configs"

// 配置样式
echarts.registerTheme('macarons',theme);

/**
 * 获取图表配置信息
 * @param {*} data 
 * @param {*} index 
 */
const setOption = (data,index) =>{

  /**
   * 查找特定数据
   * @param {Object} datas 
   * @param {*} i 序号，一般是代表某个年份
   * @param {*} tag 所要查询的数据
   */
  const findData = (datas,i,tag)=>{
    let Arr = Array.from({length: 12}).map((v,k)=>k+1);
    return Arr.map((val)=>{
      const target = datas[i].datas.find((v)=>v.month ===val );
      if(target){
        const tempArr = target.tables[target.tables.findIndex(value=>value[0] === tag )];
        return  tempArr?tempArr[1]:0;
      }else{
        return 0;
      }
    })
  }

  return {
    tooltip : {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    legend: {
        data: ["DB","DOC","JPG","PDF","WPS","XLS"],
        textStyle: {
          color: configs.baseColor
        },
        left: 0,
        top: "2%"
    },
    grid: [{
        left: '0%',
        right: '2%',
        bottom: '3%',
        top: "20%",
        show: true,
        containLabel: true,
        borderWidth: 0,
        borderColor: configs.baseColor
    }],
    xAxis : 
        {
            type : 'category',
            boundaryGap : false,
            axisLabel: {
              color: "#fff",
              fontSize: 12,
              fontWeight: "bold",
              textShadowColor: "#000",
              textShadowOffsetY: 2
            },
            data : ['01','02','03','04','05','06','07',"08","09","10","11","12"]
        }
    ,
    yAxis : {
          type : 'value',
          axisLabel: {
            color: "#fff",
            fontSize: 10,
            textShadowColor: "#000",
            textShadowOffsetY: 2
          },
          splitLine:{
            show: true,
            lineStyle:{
              color: [configs.baseBlue]
            } 
          }
      },
    series : [
        {
            name:'DB',
            type:'line',
            stack: '总量',
            areaStyle: {},
            data: findData(data,index,"DB")
        },
        {
            name:'DOC',
            type:'line',
            stack: '总量',
            areaStyle: {},
            data: findData(data,index,"DOC")
        },
        {
            name:'JPG',
            type:'line',
            stack: '总量',
            areaStyle: {},
            data: findData(data,index,"JPG")
        },
        {
            name:'PDF',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data: findData(data,index,"PDF")
        },
        {
            name:'WPS',
            type:'line',
            stack: '总量',
            data: findData(data,index,"WPS")
        },
        {
            name:'XLS',
            type:'line',
            stack: '总量',
            data: findData(data,index,"XLS")
        }
    ]
  };
}

class chartLint extends React.Component{
  constructor(){
    super();

    this.state={
      selectedYear: 0,
      taskDrop: false,
      height: 0,
      width: 0
    }
  }

  getOption = ()=>{
    const option = setOption(lineChart,this.state.selectedYear);
    return option;
  }

  // 下拉列表样式
  menu = (
    <div className={style.clear_menu_back}>
        <Menu>
            {
              lineChart.map((val,index)=> (
                  <Menu.Item onClick={()=>{this.setState({selectedYear:index})}} key={"selctedYear" + index}>{val.year}</Menu.Item>
                )
              )
            }
        </Menu>
    </div>
  );

  // 延迟加载防止图表变形
  componentDidMount(){
        // 延迟加载图表
        setTimeout(
          ()=>{
              const ref = this.refdiv;
              this.setState({
                  taskDrop: true,
                  width: ref.clientWidth || ref.offsetWidth,
                  height: ref.clientHeight || ref.offsetHeight
              })
          },
          0
      )
  }

  render(){
    return (
      <div
        className="flex_1 padding_16 flex_c"
        style={{ paddingBottom: 0, height: "100%" }}
      >

        {/* 数据顶部控制和下拉列表 */}
        <div style={{zIndex:9999}}>
            <span className={style.baseFont}>数据月增长度</span>
            <div className={style.select_year+ " init-ant-btn"}>
              <span className={style.baseFont}>选择年份</span>
              <Dropdown overlay={this.menu} >
                  <Button style={{ marginLeft: 8 }} size="small">
                    {lineChart[this.state.selectedYear].year} <Icon type="down" />
                  </Button>
              </Dropdown>
            </div>
        </div>
        <div className="flex_1 flex_c" ref={(div)=>this.refdiv = div}>

        {/* 图表 */}
        {
          this.state.taskDrop
          &&
          <Echarts
            option={this.getOption()}
            notMerge={true}
            lazyUpdate={true}
            theme="macarons"
            style={{
               height: this.state.height, 
               width: this.state.width,
               flex: 1 
            }}
          />
        }

        </div>
      </div>
    );
  }
}

export default chartLint;