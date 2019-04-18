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

package com.ys.idatrix.quality.dto.step.parts;

public class FilterDefinition {

  private String alias;
  private String fieldType;
  //org.pentaho.bigdata.api.hbase.mapping.ColumnFilter.ComparisonType
  private String comparisonType;
  private boolean signedComparison;
  private String constant;
  private String format;

  public String getAlias() {
    return alias;
  }

  public void setAlias( String alias ) {
    this.alias = alias;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType( String fieldType ) {
    this.fieldType = fieldType;
  }

  public String getComparisonType() {
    return comparisonType;
  }

  public void setComparisonType( String comparisonType ) {
    this.comparisonType = comparisonType;
  }

  public boolean isSignedComparison() {
    return signedComparison;
  }

  public void setSignedComparison( boolean signedComparison ) {
    this.signedComparison = signedComparison;
  }

  public String getConstant() {
    return constant;
  }

  public void setConstant( String constant ) {
    this.constant = constant;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat( String format ) {
    this.format = format;
  }

}
