/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

package com.ys.idatrix.cloudetl.fs.hadoop;

import org.apache.hadoop.fs.FileStatus;

import com.ys.idatrix.cloudetl.fs.hadoop.interfaces.HadoopFileStatus;
import com.ys.idatrix.cloudetl.fs.hadoop.interfaces.HadoopFileSystemPath;

/**
 * Created by bryan on 5/28/15.
 */
public class HadoopFileStatusImpl implements HadoopFileStatus {
	private final FileStatus fileStatus;

	public HadoopFileStatusImpl(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	@Override
	public long getLen() {
		return fileStatus.getLen();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isDir() {
		return fileStatus.isDir();
	}

	@Override
	public long getModificationTime() {
		return fileStatus.getModificationTime();
	}

	@Override
	public HadoopFileSystemPath getPath() {
		return new HadoopFileSystemPathImpl(fileStatus.getPath());
	}
}
