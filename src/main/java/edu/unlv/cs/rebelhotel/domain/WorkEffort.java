package edu.unlv.cs.rebelhotel.domain;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import edu.unlv.cs.rebelhotel.domain.Student;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import edu.unlv.cs.rebelhotel.domain.enums.Verification;
import edu.unlv.cs.rebelhotel.domain.Employer;
import edu.unlv.cs.rebelhotel.domain.Supervisor;
import javax.persistence.Embedded;
import edu.unlv.cs.rebelhotel.domain.WorkEffortDuration;
import edu.unlv.cs.rebelhotel.domain.enums.VerificationType;
import javax.persistence.Enumerated;
import edu.unlv.cs.rebelhotel.domain.enums.Validation;
import edu.unlv.cs.rebelhotel.domain.enums.PayStatus;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.ManyToMany;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findWorkEffortsByStudentEquals", "findWorkEffortsByValidationAndTermSubmitted" })
public class WorkEffort {

    @NotNull
    @ManyToOne
    private Student student;

    private String workPosition;

    private String comment;

    @Embedded
    private Supervisor supervisor;

    @Embedded
    private Employer employer;

    @Enumerated
    private VerificationType verificationType;

    @Enumerated
    private Validation validation;

    @Enumerated
    private Verification verification;

    @Enumerated
    private PayStatus payStatus;

    @Embedded
    private WorkEffortDuration duration;

    @ManyToMany
    private Set<CatalogRequirement> catalogRequirements = new HashSet<CatalogRequirement>();

    @ManyToOne
    private Term termSubmitted;

    @PrePersist
    public void persistHours() {
        Set<Major> majors = student.getMajors();
        Long totalHours[] = new Long[majors.size()];
        Long majorHours[] = new Long[majors.size()];
        Major majorArray[] = new Major[majors.size()];
        int it = 0;
        for (Major major : majors) {
            majorArray[it] = major;
            majorHours[it] = (long) 0;
            totalHours[it++] = (long) 0;
        }
        Set<WorkEffort> jobs = student.getWorkEffort();
        for (WorkEffort job : jobs) {
            if (job.getValidation().equals(Validation.FAILED_VALIDATION)) {
                continue;
            }
            if (job.getVerification().equals(Verification.DENIED)) {
                continue;
            }
            for (int i = 0; i < majorArray.length; i++) {
                boolean found = false;
                Set<CatalogRequirement> catalogRequirements = job.getCatalogRequirements();
                for (CatalogRequirement catalogRequirement : catalogRequirements) {
                    if (catalogRequirement.getDegreeCodePrefix().equals(majorArray[i].getDegreeCode())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    majorHours[i] += job.getDuration().getHours();
                }
                totalHours[i] += job.getDuration().getHours();
            }
        }
        for (int i = 0; i < majorArray.length; i++) {
            boolean found = false;
            if (catalogRequirements != null) {
                for (CatalogRequirement catalogRequirement : catalogRequirements) {
                    if (catalogRequirement.getDegreeCodePrefix().equals(majorArray[i].getDegreeCode())) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                majorHours[i] += duration.getHours();
            }
            totalHours[i] += duration.getHours();
        }
        Session session = ((Session) CatalogRequirement.entityManager().unwrap(Session.class)).getSessionFactory().openSession();
        session.beginTransaction();
        for (int i = 0; i < majorArray.length; i++) {
            Criteria query = session.createCriteria(CatalogRequirement.class).add(Restrictions.eq("degreeCodePrefix", majorArray[i].getDegreeCode()));
            List<CatalogRequirement> reqs = query.list();
            CatalogRequirement cr = null;
            for (CatalogRequirement catalogRequirement : reqs) {
                Major major = majorArray[i];
                if (major.getCatalogTerm().getTermYear().compareTo(catalogRequirement.getStartTerm().getTermYear()) < 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().equals(catalogRequirement.getStartTerm().getTermYear()) && major.getCatalogTerm().getSemester().compareTo(catalogRequirement.getStartTerm().getSemester()) < 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().compareTo(catalogRequirement.getEndTerm().getTermYear()) > 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().equals(catalogRequirement.getEndTerm().getTermYear()) && major.getCatalogTerm().getSemester().compareTo(catalogRequirement.getEndTerm().getSemester()) > 0) {
                    continue;
                }
                cr = catalogRequirement;
                break;
            }
            if (cr == null) {
                session.close();
                return;
            }
            Integer totalNeeded = cr.getTotalHoursNeeded();
            Integer relatedNeeded = cr.getTotalRelatedHoursNeeded();
            Integer totalOffseted = totalNeeded - relatedNeeded;
            Integer relatedValue = Math.min(relatedNeeded.intValue(), majorHours[i].intValue());
            totalHours[i] -= relatedValue;
            Integer totalValue = Math.min(totalOffseted.intValue(), totalHours[i].intValue());
            majorArray[i].setTotalHours(new Long(totalValue + relatedValue));
        }
        session.close();
    }

    @PreUpdate
    public void updateHours() {
        Set<Major> majors = student.getMajors();
        Long totalHours[] = new Long[majors.size()];
        Long majorHours[] = new Long[majors.size()];
        Major majorArray[] = new Major[majors.size()];
        int it = 0;
        for (Major major : majors) {
            majorArray[it] = major;
            majorHours[it] = (long) 0;
            totalHours[it++] = (long) 0;
        }
        Set<WorkEffort> jobs = student.getWorkEffort();
        for (WorkEffort job : jobs) {
            if (job.getValidation().equals(Validation.FAILED_VALIDATION)) {
                continue;
            }
            if (job.getVerification().equals(Verification.DENIED)) {
                continue;
            }
            if (job.getId().equals(getId())) {
                continue;
            }
            for (int i = 0; i < majorArray.length; i++) {
                boolean found = false;
                Set<CatalogRequirement> catalogRequirements = job.getCatalogRequirements();
                for (CatalogRequirement catalogRequirement : catalogRequirements) {
                    if (catalogRequirement.getDegreeCodePrefix().equals(majorArray[i].getDegreeCode())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    majorHours[i] += job.getDuration().getHours();
                }
                totalHours[i] += job.getDuration().getHours();
            }
        }
        for (int i = 0; i < majorArray.length; i++) {
            boolean found = false;
            if (catalogRequirements != null) {
                for (CatalogRequirement catalogRequirement : catalogRequirements) {
                    if (catalogRequirement.getDegreeCodePrefix().equals(majorArray[i].getDegreeCode())) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                majorHours[i] += duration.getHours();
            }
            totalHours[i] += duration.getHours();
        }
        Session session = ((Session) CatalogRequirement.entityManager().unwrap(Session.class)).getSessionFactory().openSession();
        session.beginTransaction();
        for (int i = 0; i < majorArray.length; i++) {
            Criteria query = session.createCriteria(CatalogRequirement.class).add(Restrictions.eq("degreeCodePrefix", majorArray[i].getDegreeCode()));
            List<CatalogRequirement> reqs = query.list();
            CatalogRequirement cr = null;
            for (CatalogRequirement catalogRequirement : reqs) {
                Major major = majorArray[i];
                if (major.getCatalogTerm().getTermYear().compareTo(catalogRequirement.getStartTerm().getTermYear()) < 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().equals(catalogRequirement.getStartTerm().getTermYear()) && major.getCatalogTerm().getSemester().compareTo(catalogRequirement.getStartTerm().getSemester()) < 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().compareTo(catalogRequirement.getEndTerm().getTermYear()) > 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().equals(catalogRequirement.getEndTerm().getTermYear()) && major.getCatalogTerm().getSemester().compareTo(catalogRequirement.getEndTerm().getSemester()) > 0) {
                    continue;
                }
                cr = catalogRequirement;
                break;
            }
            if (cr == null) {
                session.close();
                return;
            }
            Integer totalNeeded = cr.getTotalHoursNeeded();
            Integer relatedNeeded = cr.getTotalRelatedHoursNeeded();
            Integer totalOffseted = totalNeeded - relatedNeeded;
            Integer relatedValue = Math.min(relatedNeeded.intValue(), majorHours[i].intValue());
            totalHours[i] -= relatedValue;
            Integer totalValue = Math.min(totalOffseted.intValue(), totalHours[i].intValue());
            majorArray[i].setTotalHours(new Long(totalValue + relatedValue));
        }
        session.close();
    }

    @PreRemove
    public void removeHours() {
        Set<Major> majors = student.getMajors();
        Long totalHours[] = new Long[majors.size()];
        Long majorHours[] = new Long[majors.size()];
        Major majorArray[] = new Major[majors.size()];
        int it = 0;
        for (Major major : majors) {
            majorArray[it] = major;
            majorHours[it] = (long) 0;
            totalHours[it++] = (long) 0;
        }
        Set<WorkEffort> jobs = student.getWorkEffort();
        for (WorkEffort job : jobs) {
            if (job.getValidation().equals(Validation.FAILED_VALIDATION)) {
                continue;
            }
            if (job.getVerification().equals(Verification.DENIED)) {
                continue;
            }
            if (job.getId().equals(getId())) {
                continue;
            }
            for (int i = 0; i < majorArray.length; i++) {
                boolean found = false;
                Set<CatalogRequirement> catalogRequirements = job.getCatalogRequirements();
                for (CatalogRequirement catalogRequirement : catalogRequirements) {
                    if (catalogRequirement.getDegreeCodePrefix().equals(majorArray[i].getDegreeCode())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    majorHours[i] += job.getDuration().getHours();
                }
                totalHours[i] += job.getDuration().getHours();
            }
        }
        Session session = ((Session) CatalogRequirement.entityManager().unwrap(Session.class)).getSessionFactory().openSession();
        session.beginTransaction();
        for (int i = 0; i < majorArray.length; i++) {
            Criteria query = session.createCriteria(CatalogRequirement.class).add(Restrictions.eq("degreeCodePrefix", majorArray[i].getDegreeCode()));
            List<CatalogRequirement> reqs = query.list();
            CatalogRequirement cr = null;
            for (CatalogRequirement catalogRequirement : reqs) {
                Major major = majorArray[i];
                if (major.getCatalogTerm().getTermYear().compareTo(catalogRequirement.getStartTerm().getTermYear()) < 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().equals(catalogRequirement.getStartTerm().getTermYear()) && major.getCatalogTerm().getSemester().compareTo(catalogRequirement.getStartTerm().getSemester()) < 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().compareTo(catalogRequirement.getEndTerm().getTermYear()) > 0) {
                    continue;
                }
                if (major.getCatalogTerm().getTermYear().equals(catalogRequirement.getEndTerm().getTermYear()) && major.getCatalogTerm().getSemester().compareTo(catalogRequirement.getEndTerm().getSemester()) > 0) {
                    continue;
                }
                cr = catalogRequirement;
                break;
            }
            if (cr == null) {
                session.close();
                return;
            }
            Integer totalNeeded = cr.getTotalHoursNeeded();
            Integer relatedNeeded = cr.getTotalRelatedHoursNeeded();
            Integer totalOffseted = totalNeeded - relatedNeeded;
            Integer relatedValue = Math.min(relatedNeeded.intValue(), majorHours[i].intValue());
            totalHours[i] -= relatedValue;
            Integer totalValue = Math.min(totalOffseted.intValue(), totalHours[i].intValue());
            majorArray[i].setTotalHours(new Long(totalValue + relatedValue));
        }
        session.close();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Position: ").append(getWorkPosition()).append("\n");
        sb.append("At: ").append(getEmployer().getName()).append("\n");
        sb.append("Duration: ").append(getDuration()).append("\n").append("\n");
        return sb.toString();
    }
}
