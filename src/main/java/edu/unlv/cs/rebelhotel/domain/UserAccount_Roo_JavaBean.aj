// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package edu.unlv.cs.rebelhotel.domain;

import edu.unlv.cs.rebelhotel.domain.enums.UserGroup;
import java.lang.Boolean;
import java.lang.String;

privileged aspect UserAccount_Roo_JavaBean {
    
    public String UserAccount.getEmail() {
        return this.email;
    }
    
    public void UserAccount.setEmail(String email) {
        this.email = email;
    }
    
    public String UserAccount.getUserId() {
        return this.userId;
    }
    
    public void UserAccount.setUserId(String userId) {
        this.userId = userId;
    }
    
    public String UserAccount.getPassword() {
        return this.password;
    }
    
    public UserGroup UserAccount.getUserGroup() {
        return this.userGroup;
    }
    
    public void UserAccount.setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
    
    public Boolean UserAccount.getEnabled() {
        return this.enabled;
    }
    
    public void UserAccount.setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
}
