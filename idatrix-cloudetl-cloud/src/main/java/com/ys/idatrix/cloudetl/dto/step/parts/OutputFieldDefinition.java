/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
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

package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 *  SPHBaseInput 的  outputFieldsDefinition 域DTO,等效  org.pentaho.big.data.kettle.plugins.hbase.input.OutputFieldDefinition
 * @author XH
 * @since 2017年6月21日
 *
 */
public class OutputFieldDefinition {

  private String alias;
  private boolean keyword;
  private String columnName;
  private String family;
  private String hbaseType;
  private String format;

  public boolean isKeyword() {
    return keyword;
  }

  public void setKeyword( boolean key ) {
    this.keyword = key;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias( String alias ) {
    this.alias = alias;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName( String columnName ) {
    this.columnName = columnName;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily( String family ) {
    this.family = family;
  }

  public String getHbaseType() {
    return hbaseType;
  }

  public void setHbaseType( String hbaseType ) {
    this.hbaseType = hbaseType;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat( String format ) {
    this.format = format;
  }
}
