/**
 * 在执行完build命令后的一些处理
 */
const fs = require('fs');
const CleanCSS = require('clean-css');

const timestamp = new Date().getTime();

// 处理index.html文件，给静态资源加时间戳
let strIndexHtml = fs.readFileSync('./dist/index.html', 'utf-8');
strIndexHtml = strIndexHtml.replace(/config\.js"/g, 'config.js?v=' + timestamp + '"');
strIndexHtml = strIndexHtml.replace(/nodedll\.js"/g, 'nodedll.js?v=' + timestamp + '"');
strIndexHtml = strIndexHtml.replace(/index\.(js|css)"/g, 'index.$1?v=' + timestamp + '"');
strIndexHtml = strIndexHtml.replace('<script src="roadhog.dll.js"></script>', '');
fs.writeFile('./dist/index.html', strIndexHtml, () => {});

// 压缩css
const rawCss = fs.readFileSync('./dist/index.css', 'utf-8');
const minCss = new CleanCSS({ level: 2 }).minify(rawCss);
fs.writeFile('./dist/index.css', minCss.styles, () => {});

console.log('All building is OK!');
