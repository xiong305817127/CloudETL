/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.def;

import org.pentaho.di.i18n.BaseMessages;

/**
 * 系统常量<br/>
 * 版权信息，版本发布信息等<br/>
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
public class CloudConst {

	private static Class<?> PKG = CloudConst.class;

	/**
	 * Application name
	 */
	public static final String APP_NAME = "iDatrix CloudETL";

	/**
	 * Copyright year
	 */
	public static final String COPYRIGHT_YEAR = "2017";

	/**
	 * Release Type
	 */
	public enum ReleaseType {
		DEMO {
			public String getMessage() {
				return BaseMessages.getString(PKG, "Const.Demo.HelpAboutText");
			}
		},
		RELEASE_CANDIDATE {
			public String getMessage() {
				return BaseMessages.getString(PKG, "Const.Candidate.HelpAboutText");
			}
		},
		MILESTONE {
			public String getMessage() {
				return BaseMessages.getString(PKG, "Const.Milestone.HelpAboutText");
			}
		},
		PREVIEW {
			public String getMessage() {
				return BaseMessages.getString(PKG, "Const.PreviewRelease.HelpAboutText");
			}
		},
		GA {
			public String getMessage() {
				return BaseMessages.getString(PKG, "Const.GA.HelpAboutText");
			}
		};

		public abstract String getMessage();
	}

}
