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
public class Result {

    private List<String> cn;
    private String dn;
    private List<String> gidnumber;
    private List<String> ipauniqueid;
    private List<String> member_user;
    private List<String> objectclass;
    public void setCn(List<String> cn) {
         this.cn = cn;
     }
     public List<String> getCn() {
         return cn;
     }

    public void setDn(String dn) {
         this.dn = dn;
     }
     public String getDn() {
         return dn;
     }

    public void setGidnumber(List<String> gidnumber) {
         this.gidnumber = gidnumber;
     }
     public List<String> getGidnumber() {
         return gidnumber;
     }

    public void setIpauniqueid(List<String> ipauniqueid) {
         this.ipauniqueid = ipauniqueid;
     }
     public List<String> getIpauniqueid() {
         return ipauniqueid;
     }

    public void setMember_user(List<String> member_user) {
         this.member_user = member_user;
     }
     public List<String> getMember_user() {
         return member_user;
     }

    public void setObjectclass(List<String> objectclass) {
         this.objectclass = objectclass;
     }
     public List<String> getObjectclass() {
         return objectclass;
     }

}