// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package edu.unlv.cs.rebelhotel.domain;

import java.lang.String;

privileged aspect WorkRequirement_Roo_ToString {
    
    public String WorkRequirement.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("Hours: ").append(getHours()).append(", ");
        sb.append("Major: ").append(getMajor()).append(", ");
        sb.append("WorkEffort: ").append(getWorkEffort() == null ? "null" : getWorkEffort().size());
        return sb.toString();
    }
    
}
