import React from "react";
import Echarts from "echarts-for-react";
import echarts from "echarts" // echarts for react 自带的echarts库，无需安装
import configs,{PieChart} from "../../configs"
import theme from "../theme/macarons"

// 注册样式
echarts.registerTheme('macarons',theme);

// 设置图标配置
const option = {
    title:{
        text: '各单位分布图',
        subtext: '前10位占比',
        x:'left',
        textStyle:{
            color: configs.baseColor
        }
    },
    tooltip: {
        trigger: 'item',
        formatter: "{a} <br/>{b}: {c} ({d}%)",
        alwaysShowContent: true
    },
    legend: {
        orient: 'horizontal',
        x: 'center',
        y: "bottom",

        // 取前十名
        data: PieChart.data.map(val=>val.name).splice(0,10),
        textStyle: {
            color: "#fff"
        },
    },
    series: [
        {
            name:'资源条目占比',
            type:'pie',
            center: ["50%","45%"],
            radius: ['45%', '70%'],
            roseType : 'radius',
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    show: true,
                    textStyle: {
                        fontSize: '20',
                        fontWeight: 'bold'
                    }
                }
            },
            labelLine: {
                normal: {
                    show: false
                }
            },
            
            data: PieChart.data
        }
    ]
};
                    
// 图表
class chart1 extends React.Component{

    constructor(){
        super();
        this.state = {
            taskDrop: false, // 父级组件是否加载完成 
            width: 0,  // 父级div宽度
            height: 0  // 父级div高度
        }
    }

    componentDidMount(){

        // 延迟加载图表，防止图表
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

        console.log(this.state);
        return (
            <div className="flex_1" style={{position: "relative"}} ref={(refdiv)=>this.refdiv = refdiv}>
                {
                    this.state.taskDrop
                    &&
                    <Echarts
                        option={option}
                        notMerge={true}
                        lazyUpdate={true}
                        theme={"macarons"}

                        // 延迟加载图表样式
                        // 防止图表变形
                        style={{ 
                            height: this.state.height,
                            width: this.state.width, 
                            padding:"48px 16px 32px 64px"
                        }}
                    />
                }
            </div>
        )
    }
}

export default chart1