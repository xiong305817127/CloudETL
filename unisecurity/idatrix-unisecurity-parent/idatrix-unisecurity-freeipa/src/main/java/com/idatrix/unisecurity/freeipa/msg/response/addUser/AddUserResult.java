/**
  * Copyright 2017 bejson.com 
  */
package com.idatrix.unisecurity.freeipa.msg.response.addUser;

/**
 * Auto-generated: 2017-09-21 11:16:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class AddUserResult {

    private Result result;
    private String summary;
    private String value;
    public void setResult(Result result) {
         this.result = result;
     }
     public Result getResult() {
         return result;
     }

    public void setSummary(String summary) {
         this.summary = summary;
     }
     public String getSummary() {
         return summary;
     }

    public void setValue(String value) {
         this.value = value;
     }
     public String getValue() {
         return value;
     }

}