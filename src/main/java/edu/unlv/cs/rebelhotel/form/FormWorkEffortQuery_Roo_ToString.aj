// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package edu.unlv.cs.rebelhotel.form;

import java.lang.String;

privileged aspect FormWorkEffortQuery_Roo_ToString {
    
    public String FormWorkEffortQuery.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StudentFirstName: ").append(getStudentFirstName()).append(", ");
        sb.append("StudentLastName: ").append(getStudentLastName()).append(", ");
        sb.append("StudentMiddleName: ").append(getStudentMiddleName()).append(", ");
        sb.append("UserId: ").append(getUserId()).append(", ");
        sb.append("EmployerName: ").append(getEmployerName()).append(", ");
        sb.append("EmployerLocation: ").append(getEmployerLocation()).append(", ");
        sb.append("SortOptions: ").append(getSortOptions()).append(", ");
        sb.append("Validation: ").append(getValidation()).append(", ");
        sb.append("Verification: ").append(getVerification()).append(", ");
        sb.append("VerificationType: ").append(getVerificationType()).append(", ");
        sb.append("Validation: ").append(getValidation()).append(", ");
        sb.append("StartDate: ").append(getStartDate()).append(", ");
        sb.append("EndDate: ").append(getEndDate());
        return sb.toString();
    }
    
}
