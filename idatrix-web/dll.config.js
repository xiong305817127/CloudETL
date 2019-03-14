const path = require('path');
const webpack = require('webpack');

// 需要提取成dll的node模块在此配置
const dlls = [
	"antd",
	"base64-utf8",
	"brace",
	"chance",
	"d3",
  "dva",
	"echarts-for-react",
	"fecha",
	"filesize",
	"immutable",
  "react-cookie",
  "lodash",
  "react-addons-create-fragment",
	"md5",
	"prop-types",
	"qs",
	"react",
	"react-ace",
	"react-dom",
	"safe-json-parse",
  "classnames",
  "react-iframe",
  "redux-logger"
];

console.log(process.env.NODE_ENV,"环境变量");

module.exports = {
  output: {
    path: path.resolve(__dirname, 'public'),
    filename: '[name].js',
    library: '[name]',
  },
  entry: {
    nodedll: dlls,
  },
  plugins: [
    new webpack.DllPlugin({
      path: 'manifest.json',
      name: '[name]',
      context: __dirname,
    }),
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development')
    }),
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false,
      },
    }),
  ],
};
