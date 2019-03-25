/**
 * Written by steven leo
 * on 2018/09/17
 */

const express = require('express')
;

const app = express();
const path = require('path');
const httpProxy = require('http-proxy');

const proxy = httpProxy.createProxyServer();


/**
 * the server methods
 * set server mode
 */
class Server {
  constructor({ options, plugins }) {
    this.options = options;
    this.plugins = plugins;
    this.adapters = {};
        // this.static = this.static.bind(this)
        // this.pluginApply = this.pluginApply.bind(this)
        // this.proxy = this.proxy.bind(this)
        // this.appStart = this.appStart.bind(this)
  }

  init() {
    this.static();
    this.pluginApply(this.plugins);
    this.proxy();
    this.appStart();
  }

  static() {
    console.log(this.options.static);
    app.use(express.static(path.resolve(this.options.static)));
  }

  pluginApply(plugins) {
    for (const i of plugins) {
      if (!i.adapter || !i.route) {
        throw new Error('A plugin must have an Adapter property and route path');
      }

            // add route and adapter to props
      this.adapters[i.route] = {
        route: i.route,
        adapter: i.adapter,
      };
    }
  }

  proxy() {
    const plugins = this.plugins,
      options = this.options,
      adapters = this.adapters;

    app.use('/:route', (req, res) => {
      const route = req.params.route;
      const ifPlugin = plugins.some(val => (val.route && val.route === route));

            // check if its a proxy needed route
            // then take the request and response to proxy-http.web
            // set `changeOrigin` options as `true` for default
      if (!ifPlugin) {
        proxy.web(req, res, {
          target: `${options.proxy[route]
                        ? optionss.proxy[route]
                        : options.proxy.default}/${route}`,
          changeOrigin: true,
        });
      }

            // if you want to transmit the req,res to plugins ,
            // you need to set plugin before using it
      if (ifPlugin) {
        adapters[route].adapter(req, res);
      }
    });
  }

  appStart() {
    const port = this.options.port;
    app.listen(port, () => {
      console.log(`app listen on port ${port}`);
    });
  }
}

module.exports = Server;

