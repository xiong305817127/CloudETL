/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.idatrix.unisecurity.common.sso.CookieUtil;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.EncryptUtil;

/**
 * Global filter to ensure correct JSON data wrapper during every request and
 * response.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class GlobalFilter implements Filter {
	
	public static final Log  logger = LogFactory.getLog("GlobalFilter");

	private String OwnerKey = "resource_owner";

	@Override
	public void destroy() {

	}

	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
    	HttpServletRequest request = (HttpServletRequest)req;
    	HttpServletResponse response = (HttpServletResponse)res;
    	
    	RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request,response));
    	
    	String ownerStr = getOwner(request , OwnerKey);
    	String username = CloudSession.getLoginUser() ;
    	if( !Utils.isEmpty(ownerStr) && !Utils.isEmpty(username) ) {
    		//资源目录需要越权操作
    		String rentId = CloudSession.getLoginRenterId() ;
    		String owner = EncryptUtil.getInstance().strDec(ownerStr, username, rentId, "GBXVDHSKENCNJDUSBZACXBLMKDICDHJNBC");
    		if(!Utils.isEmpty(owner) && StringUtils.isAsciiPrintable(owner) && !owner.equals(username) ) {
    			//密文解析成功
    			request.getSession().setAttribute(CloudSession.ATTR_SESSION_USER_NAME,owner);
    			logger.info("租户越权查询资源:登录租户["+username+"],资源用户["+owner+"]");
    		}
    	}
        fc.doFilter(req, res);
        //清理线程用户信息
		CloudSession.clearThreadInfo();
		
		
    }

	public String getOwner(HttpServletRequest request,String key) {
		String owner = CookieUtil.getCookie(key, request);
		if(Utils.isEmpty(owner)) {
			owner = request.getHeader(key);
		}
		if(Utils.isEmpty(owner)) {
			owner = (String) request.getAttribute(key);
		}
		if(Utils.isEmpty(owner)) {
			owner = (String) request.getParameter(key);
		}
		return owner;
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {

	}

}
