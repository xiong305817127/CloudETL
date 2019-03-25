/**
 * [INSTRUCTIONS]
 * the server tools can be divided into three parts
 * 
 * First, 
 * Your config files for proxy, port ...
 * Put them in configs folder, and require them in configs
 * We highly recommend that you make multi configs into one `module.exports
 * 
 * Second, 
 * Your Plugins , like monitor, bug reporters...
 * Use them by making a Class
 * 
 * Third, 
 * Core Functions
 * Make Server by ./lib/server
 */

/**
 * <<<<<< CONFIGS
 * your configs list includes: 
 * [
 *   "port",
 *  "proxy", -> {default,{path,changeOrigin,url}}
 *   "static"
 * ]
 * <<<<<< PLUGINS
 * Plugins apis
 * {
 *      route: "/*", // this is your plugin files
 *      adapter: (req,res)=>{...}, // 
 * }
 * <<<<<<<<<<<<<<
 */

const Server        = require("./lib/server")
const configs       = require("./configs/configs.proxy")
const plugins       = require("./configs/plugins")

// make a new server
const FrontServer   = new Server({
        options:configs,
        plugins
});

FrontServer
    // Note: initial method also means starting
    .init();
