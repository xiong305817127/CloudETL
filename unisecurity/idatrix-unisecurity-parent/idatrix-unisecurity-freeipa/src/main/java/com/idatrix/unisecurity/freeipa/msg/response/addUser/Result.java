/**
  * Copyright 2017 bejson.com 
  */
package com.idatrix.unisecurity.freeipa.msg.response.addUser;
import java.util.List;

/**
 * Auto-generated: 2017-09-21 11:16:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Result {

    private List<String> cn;
    private List<String> displayname;
    private String dn;
    private List<String> gecos;
    private List<String> gidnumber;
    private List<String> givenname;
    private boolean has_keytab;
    private boolean has_password;
    private List<String> homedirectory;
    private List<String> initials;
    private List<String> ipauniqueid;
    private List<String> krbprincipalname;
    private List<String> loginshell;
    private List<String> mail;
    private List<String> objectclass;
    private List<String> sn;
    private List<String> uid;
    private List<String> uidnumber;
    public void setCn(List<String> cn) {
         this.cn = cn;
     }
     public List<String> getCn() {
         return cn;
     }

    public void setDisplayname(List<String> displayname) {
         this.displayname = displayname;
     }
     public List<String> getDisplayname() {
         return displayname;
     }

    public void setDn(String dn) {
         this.dn = dn;
     }
     public String getDn() {
         return dn;
     }

    public void setGecos(List<String> gecos) {
         this.gecos = gecos;
     }
     public List<String> getGecos() {
         return gecos;
     }

    public void setGidnumber(List<String> gidnumber) {
         this.gidnumber = gidnumber;
     }
     public List<String> getGidnumber() {
         return gidnumber;
     }

    public void setGivenname(List<String> givenname) {
         this.givenname = givenname;
     }
     public List<String> getGivenname() {
         return givenname;
     }

    public void setHas_keytab(boolean has_keytab) {
         this.has_keytab = has_keytab;
     }
     public boolean getHas_keytab() {
         return has_keytab;
     }

    public void setHas_password(boolean has_password) {
         this.has_password = has_password;
     }
     public boolean getHas_password() {
         return has_password;
     }

    public void setHomedirectory(List<String> homedirectory) {
         this.homedirectory = homedirectory;
     }
     public List<String> getHomedirectory() {
         return homedirectory;
     }

    public void setInitials(List<String> initials) {
         this.initials = initials;
     }
     public List<String> getInitials() {
         return initials;
     }

    public void setIpauniqueid(List<String> ipauniqueid) {
         this.ipauniqueid = ipauniqueid;
     }
     public List<String> getIpauniqueid() {
         return ipauniqueid;
     }

    public void setKrbprincipalname(List<String> krbprincipalname) {
         this.krbprincipalname = krbprincipalname;
     }
     public List<String> getKrbprincipalname() {
         return krbprincipalname;
     }

    public void setLoginshell(List<String> loginshell) {
         this.loginshell = loginshell;
     }
     public List<String> getLoginshell() {
         return loginshell;
     }

    public void setMail(List<String> mail) {
         this.mail = mail;
     }
     public List<String> getMail() {
         return mail;
     }

    public void setObjectclass(List<String> objectclass) {
         this.objectclass = objectclass;
     }
     public List<String> getObjectclass() {
         return objectclass;
     }

    public void setSn(List<String> sn) {
         this.sn = sn;
     }
     public List<String> getSn() {
         return sn;
     }

    public void setUid(List<String> uid) {
         this.uid = uid;
     }
     public List<String> getUid() {
         return uid;
     }

    public void setUidnumber(List<String> uidnumber) {
         this.uidnumber = uidnumber;
     }
     public List<String> getUidnumber() {
         return uidnumber;
     }

}