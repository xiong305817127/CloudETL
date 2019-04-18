/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class StreamUtils {

	private static ByteArrayOutputStream cacheStream ;
	private static final PrintStream systemOutStream = System.out;

	public static void closeDefaultSystemOut() {
		
		if( cacheStream == null){
			cacheStream = new ByteArrayOutputStream();
		}
		System.setOut(new PrintStream(cacheStream));// 不打印到控制台
	}

	
	public static String OpenDefaultSystemOut() {
		PrintStream oldStream = null;
		try {
			oldStream = System.out;
			if (cacheStream != null) {
				cacheStream.flush();
				String strMsg = cacheStream.toString();
				return strMsg;
			}
		} catch (IOException e) {
		} finally {
			System.setOut(systemOutStream);// 打印到控制台
			
			if (oldStream != null && oldStream != systemOutStream) {
				oldStream.close();
			}
			if (cacheStream != null) {
				try {
					cacheStream.close();
					cacheStream = null;
				} catch (IOException e) {
				}
			}
		}

		return null;
	}


}
