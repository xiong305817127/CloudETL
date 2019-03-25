/**
 * 政府风格首页
 */
import React from "react";
import { connect } from "dva";
import { Layout, Row, Col, Icon, Dropdown, Menu } from "antd";
import HomeComponent from "components/HomeComponent";
import Modal from "components/Modal";
import baseInfo from "config/baseInfo.config";
import UserProfile from "../user/Profile";
import { inletDecorator } from "./homeDecorator";

import Style from "./Government.less";

const { Header, Content } = Layout;

//功能菜单配置
const menuFuc = {
  dataMap: {
		path: "#/hotMap",
		name:"数据展示"
  },
  bbsPage: {
		path: "#/bbsPage",
		name:"大数据论坛"
  },
  dataView: {
		path: "#/dataView",
		name:"数据可视化"
	},
	portalPage: {
		path: "#/portalPage",
		name:"门户网站"
  },
	kibanaPage: {
		path: "#/kibanaPage",
		name:"kibana"
  },
	logAnalysis: {
		path: "#/logAnalysis",
		name:"日志分析"
  }
};

@inletDecorator
class HomePage extends HomeComponent {
  methodAdmin = () => {
    const { dispatch } = this.props;
    dispatch({ type: "account/showProfile" });
  };

  methodLogout(e) {
    const { dispatch } = this.props;
    e.preventDefault();
    Modal.confirm({
      title: "退出登录提醒",
      content: "您将退出登录，是否继续？",
      onOk: () => {
        dispatch({ type: "account/loginOut", payload: "/login" });
      },
      okText: "确定",
      cancelText: "取消"
    });
  }
  //跳转神算子平台链接
  titleA() {
		const { username } = this.props.account;

		let DIVINE_URL = `"http://113.207.110.254:8088/dmp-oppm/users/ssologin?username=${username}&appkey=${username}`

		//增加神算子可配置化
		if(CUSTOM_PARAMS && CUSTOM_PARAMS.DIVINE_URL){
			DIVINE_URL = `${CUSTOM_PARAMS.DIVINE_URL}?username=${username}&appkey=${username}`
		}

    window.open(DIVINE_URL);
    //location.href="https://senses.jusfoun.com/home/recommend/guide?username="+username+"&appkey="+username;
  }
  //根据可配置的变量去配置什么环境需要神算子平台
  createCol(col) {
    if (baseInfo.premit && baseInfo.premit.includes("shen")) {
      // col.allow =false;
      if (col.title === "大数据应用开发环境") {
        return (
          <div
            className={Style.navItem + (!col.allow ? ` ${Style.disabled}` : "")}
            onClick={() => (col.allow === true ? this.titleA() : "")}
          >
            <dl>
              <dd>
                <img src={col.icon2} />
              </dd>
              <dt>{col.title}</dt>
            </dl>
            <p>{col.desc}</p>
          </div>
        );
      } else {
        return (
          <div
            className={Style.navItem + (!col.allow ? ` ${Style.disabled}` : "")}
            onClick={() => {
              if (col.title === "元数据管理") {
                window.location.href = "/new/#/metadata";
              } else {
                col.allow && this.props.openSystem(col.path);
              }
            }}
          >
            <dl>
              <dd>
                <img src={col.icon2} />
              </dd>
              <dt>{col.title}</dt>
            </dl>
            <p>{col.desc}</p>
          </div>
        );
      }
    } else {
      return (
        <div
          className={Style.navItem + (!col.allow ? ` ${Style.disabled}` : "")}
          onClick={() => {
            if (col.title === "元数据管理") {
              window.location.href = "/new/#/metadata";
            } else {
              col.allow && this.props.openSystem(col.path);
            }
          }}
        >
          <dl>
            <dd>
              <img src={col.icon2} />
            </dd>
            <dt>{col.title}</dt>
          </dl>
          <p>{col.desc}</p>
        </div>
      );
    }
  }

  /**
  *  createCol(col) {
      return (<div
          className={Style.navItem + (!col.allow ? ` ${Style.disabled}` : '')}
          onClick={() => col.allow && this.props.openSystem(col.path)}
        >
          <dl>
            <dd><img src={col.icon2} /></dd>
            <dt>{col.title}</dt>
          </dl>
          <p>{col.desc}</p>
        </div>);
  }
  */

  //home页面大屏  dataMap
  //bbs 论坛入口  bbsPage
  //可视化				dataView

  menu = (menuList) => {
    return (
      <Menu>
				{
					menuList.map(index=>(
						<Menu.Item key={index}>
						<a
							rel="noopener noreferrer"
							href={menuFuc[index].path}
						>
							{menuFuc[index].name}
						</a>
					</Menu.Item>
					))
				}
      </Menu>
    );
  };

  render() {
    const { inletsConfig } = this.state;
    const inletList = Object.keys(inletsConfig).map(key => inletsConfig[key]);
    const rowList1 = inletList.slice(0, 4); // 获取第一行数组
    const rowList2 = inletList.slice(4, 8); // 获取第一行数组
    const menuList = baseInfo.premit.filter(
      index =>
				index === "dataMap" || index === "bbsPage" || index === "dataView" || index === "portalPage"
				|| index === "kibanaPage" || index === "logAnalysis"
    );

    return (
      <Layout className={Style.page}>
        <Header className={Style.header}>
          <Row>
            <Col span="12">
              <img
                className={Style.logo}
                style={SITE_NAME === "noLogo" ? { display: "none" } : null}
                src={baseInfo.logo}
                height="50"
              />
              <span className={Style.siteName}>{baseInfo.siteName}</span>
            </Col>
            <Col span="12" style={{ textAlign: "right" }}>
              <a href="#home" onClick={this.methodAdmin}>
                <img src={require("../../assets/images/user.png")} />
                <span style={{ paddingLeft: 7 }}>
                  {this.props.account.username}
                </span>
              </a>
              {/* {baseInfo.premit &&
                // baseInfo.premit.includes("dataMap") &&
                typeof SHOW_EXTRA !== "undefined" &&
                SHOW_EXTRA && (
                  <a href="#/hotMap">
                    <Icon type="right-circle" />
                    <span style={{ paddingLeft: 7 }}>数据展示</span>
                  </a>
								)}
							 */}
              {menuList.length > 0 ? (
                <Dropdown overlay={this.menu(menuList)}>
                  <a>
                    功能菜单
                    <Icon type="down" />
                  </a>
                </Dropdown>
              ) : null}
              <a href="lingjiluping1://">
                <Icon type="video-camera" />
                <span style={{ paddingLeft: 7 }}>打开录屏</span>
              </a>
              <a href="#" onClick={this.methodLogout.bind(this)}>
                <img src={require("../../assets/images/drop_out.png")} />
                <span style={{ paddingLeft: 7 }}>退出</span>
              </a>
            </Col>
          </Row>
        </Header>

        <Content className={Style.content}>
          <section className={Style.navWrap}>
            <Row>
              <Col span="12">
                <div style={{ margin: "3px 3px -3px 3px" }}>
                  <img
                    className={Style.banner}
                    src={require("../../assets/images/gov/banner-gov.png")}
                  />
                </div>
              </Col>
              <Col span="12">
                {rowList1.map((col, index) => (
                  <Col span="12" key={index}>
                    {this.createCol(col)}
                  </Col>
                ))}
              </Col>
            </Row>
            {rowList2.length > 0 ? (
              <Row>
                {rowList2.map((col, index) => (
                  <Col span="6" key={index}>
                    {this.createCol(col)}
                  </Col>
                ))}
              </Row>
            ) : null}
          </section>
        </Content>

        <UserProfile />
      </Layout>
    );
  }
}

export default connect(({ account, system }) => ({
  account,
  system
}))(HomePage);
