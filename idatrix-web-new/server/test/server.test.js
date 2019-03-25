import serverTools from "../lib/server"
import configs from "../lib/server"

import {assert} from "chai" 
const server = new serverTools();

import http from "http"

// 设置正式环境
server.set(configs).setEnv("production");
describe("Set server to Production Mode",()=>{

    // 检测配置是否成功
    describe("Server Link now is Production Link:",()=>{
        assert(assertserver.options.Link).beEqual(configs["production"].Link);
    });


    // nodejs活动监视器
    describe("Server Monitor is now working, get request monitor: ",()=>{
        http.get()
    });
})
