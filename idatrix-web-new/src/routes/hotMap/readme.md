#### 大屏展示图
（2018.10.26， v1，steven leo）
##### 1. 大屏展示页面逻辑
文件分布图
```javascript
components //组件和布局组件
    |--charts // 图表组件
    |--theme // 主题
    |--userInfo // 用户栏
layout.hotmap.js // 布局组件

dataExhibition // 展示页面大屏
    |--index.js
    |--model.js
dataSharing // 数据分享大屏
    |--index.js
    |--model.js
models
    |--mainPageModel.js

configs // 配置内容，如果修改数据可以从这里开始
hotMapApp.js // 用来数据绑定使用

route.js // 暂时不起作用
```

##### 2. 大屏项目注意事项

2.1 依赖echarts-for-react，配置主题需要引入echarts组件，但是不要使用自定义的echarts，因为echarts-for-react已经存在一个echarts版本
2.2 如需修改用户和数据请求绑定，使用models/mainPageModel.js进行修改（使用了immutable）