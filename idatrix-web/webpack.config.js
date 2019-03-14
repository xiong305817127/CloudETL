const path = require('path');
const webpack = require('webpack');
const StringReplacePlugin = require('string-replace-webpack-plugin');
const pkg = require('./package.json');

// 单独打包ETL
const STANDALONE_ETL = process.env.STANDALONE_ETL === 'true';

module.exports = (webpackConfig, env) => {
  console.log(env,"开发环境");
  console.log(STANDALONE_ETL,"是否独立部署etl");

  const config = webpackConfig;
  config.output.chunkFilename = 'chunk/[name].chunk-[chunkhash:8].js';
  // 处理环境变量
  config.module = config.module || {};
  config.module.rules = config.module.rules || [];
  config.module.rules.push({
    test: /constants\.js$/,
    exclude: /node_modules/,
    use: [{
      loader: StringReplacePlugin.replace({
        replacements: [
          {
            pattern: /\[package\._env_\.([\w-]+)\]/g,
            replacement: (match, p1) => {
              return pkg._env_[env][p1];
            },
          },
          {
            pattern: /STANDALONE_ETL\s*=\s*(?:true|false)/g,
            replacement: () => {
              return `STANDALONE_ETL = ${STANDALONE_ETL}`;
            },
          },
        ],
      }),
    }],
  });
  config.module.rules.push({
    test: /router\.js$/,
    exclude: /node_modules/,
    use: [{
      loader: StringReplacePlugin.replace({
        replacements: [
          {
            pattern: /'\[STANDALONE_ETL\]'/g,
            replacement: () => {
              return String(STANDALONE_ETL);
            },
          },
        ],
      }),
    }],
  });
  config.resolve.alias = config.resolve.alias || {};
  Object.assign(config.resolve.alias, {
    assets: path.resolve(__dirname, './src/assets'),
    components: path.resolve(__dirname, './src/components'),
    config: path.resolve(__dirname, './src/config'),
    services: path.resolve(__dirname, './src/services'),
    utils: path.resolve(__dirname, './src/utils'),
    constants: path.resolve(__dirname, './src/constants'),
  });

  // 此处可添加开发环境配置
  if (env === 'development') {
    // config.plugins.push('...')
  }

  // 此处可添加生产环境配置
  if (env === 'production') {
    // 加载dll
    config.plugins.push(new webpack.DllReferencePlugin({
      context: __dirname,
      manifest: require('./manifest.json'),
    }));

  }
  return config;
};
