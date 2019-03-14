const webPageMonitor = require("../lib/monitor");
const errorReporter = require("../lib/error_reporter");

console.log(new errorReporter());
module.exports = [

    // 页面监视器
    new webPageMonitor(),

    // 前端bug收集
    new errorReporter()
]