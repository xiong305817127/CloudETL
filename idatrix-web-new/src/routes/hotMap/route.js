
//热点图主页
// 热点图主页model
const HotMapApp = resolve => require(['./hotMapApp.js'], resolve);
const mapMainPageModel = resolve => require(['./models/mainPageModel.js'], resolve);

// 数据展示
const dataExhibitionModel = resolve => require(["./dataExhibition/model.js"],resovle)
const dataExhibition = resolve => require(["./dataExhibition/index.js"],resovle)

// 数据分享
// const dataSharingModel = resolve => require(["./dataSharing/model.js"],resovle)
// const dataSharing = resolve => require(["./dataSharing/index.js"],resovle)

export default [
  {
    path: '/hotMap',
    name: 'hotMap', // 热点图文件夹
    component: HotMapApp,
    models: [
        mapMainPageModel
    ],
    routes: [
      {
        path: '/hotMap/dataExhibition',
        name: 'dataExhibition',
        component: dataExhibition,
        model: dataExhibitionModel,
      }
    ]
  }
];
