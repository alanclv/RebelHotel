// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package edu.unlv.cs.rebelhotel.domain;

import java.lang.String;

privileged aspect Major_Roo_ToString {
    
    public String Major.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WorkRequirements: ").append(getWorkRequirements() == null ? "null" : getWorkRequirements().size()).append(", ");
        sb.append("Department: ").append(getDepartment()).append(", ");
        sb.append("CatalogTerm: ").append(getCatalogTerm());
        return sb.toString();
    }
    
}
