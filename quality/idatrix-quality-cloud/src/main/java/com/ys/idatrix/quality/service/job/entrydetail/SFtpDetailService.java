/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job.entrydetail;

import java.net.InetAddress;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.entries.sftp.SFTPClient;
import org.springframework.stereotype.Service;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;

/**
 *  AccessInput related Detail Service
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class SFtpDetailService implements EntryDetailService {


	@Override
	public String getEntryDetailType() {
		return "SFTP";
	}

	/**
	 * flag : getParameters 
	 * @throws Exception 
	 */
	@Override
	public Object dealEntryDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			throw new KettleException("flag is null!");
		}

		switch (flag) {
		case "test":
			return connectToSFTP(param);
		default:
			throw new KettleException("flag is not support!");

		}

	}

	  private Object connectToSFTP( Map<String, Object> params ) throws Exception {
		  
			checkDetailParam(params, "checkFolder","serverName", "serverPort", "userName", "password");
			String checkFolder = params.get("checkFolder").toString();
 			String serverName = params.get("serverName").toString();
			String serverPort = params.get("serverPort").toString();
			String userName = params.get("userName").toString();
			String password = params.get("password").toString();
			
			String Remotefoldername = (String) params.get("Remotefoldername");
			String keyfilename = (String) params.get("keyfilename");
			String keyfilepass = (String) params.get("keyfilepass");
			String proxyHost = (String) params.get("proxyHost");
			String proxyPort = (String) params.get("proxyPort");
			String proxyUsername = (String) params.get("proxyUsername");
			String proxyPassword = (String) params.get("proxyPassword");
			String proxyType = (String) params.get("proxyType");
			
		    boolean retval = false;
		    SFTPClient sftpclient = null;
			try {
		      if ( sftpclient == null ) {
		        // Create sftp client to host ...
		        sftpclient = new SFTPClient(
		          InetAddress.getByName( serverName ),
		          Const.toInt( serverPort, 22 ),
		          userName,
		          keyfilename,
		          keyfilepass );

		        // Set proxy?
		        if ( !Utils.isEmpty( proxyHost ) ) {
		          // Set proxy
		          sftpclient.setProxy( proxyHost,proxyPort,proxyUsername,proxyPassword,proxyType );
		        }
		        // login to ftp host ...
		        sftpclient.login( password );

		        retval = true;
		      }
		      if ( "true".equals(checkFolder ) ) {
		        retval = sftpclient.folderExists( Remotefoldername );
		      }
		    } finally {
		      if ( sftpclient != null ) {
		        try {
		          sftpclient.disconnect();
		        } catch ( Exception ignored ) {
		          // We've tried quitting the SFTP Client exception
		          // nothing else to be done if the SFTP Client was already disconnected
		        }
		        sftpclient = null;
		      }
		     
		    }
			
			return retval?new ReturnCodeDto(0,"success"): new ReturnCodeDto(1,"fail");
			
		  }

	
}
