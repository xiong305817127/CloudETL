/**
  * Copyright 2017 bejson.com 
  */
package com.idatrix.unisecurity.freeipa.ldap.response.addUser2Group;
import java.util.List;

/**
 * Auto-generated: 2017-09-21 10:8:13
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Member {

    private List<List<String>> group;
    private List<List<String>> user;
    public void setGroup(List<List<String>> group) {
         this.group = group;
     }
     public List<List<String>> getGroup() {
         return group;
     }

    public void setUser(List<List<String>> user) {
         this.user = user;
     }
     public List<List<String>> getUser() {
         return user;
     }
	@Override
	public String toString() {
		return "Member [group=" + group + ", user=" + user + "]";
	}
     
     

}