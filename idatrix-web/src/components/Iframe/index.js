import { iframeConfig } from "config/iframe.config.js";
import Iframe from 'react-iframe';
import { goLogin } from 'utils/goLogin';

class MyIframe extends React.Component{


	componentDidMount(){
		window.addEventListener("message", (data)=>{
			console.log(data,"收到数据");
			if(data.data.reLogin){
				goLogin();
			}
		}, false);
	}

	render(){

		const { url,height } = this.props;
		const item = iframeConfig.filter(index=>index.route === url)[0];
		let realHeight = height?height:item.config.height;

		return(
			<Iframe 
				ref="MyIframe"
				url={item.url}
				{...item.config}
				height = {realHeight}

			/>
		)
	}
}

export default MyIframe