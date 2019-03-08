/**
  * Copyright 2017 bejson.com 
  */
package com.idatrix.unisecurity.freeipa.ldap.response.addUser2Group;

/**
 * Auto-generated: 2017-09-21 10:8:13
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class LdapRtqResult {

    private int completed;
    private Failed failed;
    private Result result;
    public void setCompleted(int completed) {
         this.completed = completed;
     }
     public int getCompleted() {
         return completed;
     }

    public void setFailed(Failed failed) {
         this.failed = failed;
     }
     public Failed getFailed() {
         return failed;
     }

    public void setResult(Result result) {
         this.result = result;
     }
     public Result getResult() {
         return result;
     }
     
	@Override
	public String toString() {
		return "LdapRtqResult [completed=" + completed + ", failed=" + failed + ", result=" + result + "]";
	}
     
     

}