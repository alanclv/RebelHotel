package edu.unlv.cs.rebelhotel.file;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

<<<<<<< HEAD
import javax.persistence.TypedQuery;

=======
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
>>>>>>> 17a733858fae4f1ee8a4b4079c3f66e55b437353
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import edu.unlv.cs.rebelhotel.domain.Major;
import edu.unlv.cs.rebelhotel.domain.Term;
import edu.unlv.cs.rebelhotel.domain.enums.Departments;
import edu.unlv.cs.rebelhotel.domain.enums.Semester;

import edu.unlv.cs.rebelhotel.file.enums.FileDepartments;

@RooJavaBean
@RooToString
public class Line {
	private static final int EXPECTED_SIZE = 13;
	private static final String SPACE = " ";
	private static final Logger LOG = Logger.getLogger(Line.class);
	private String studentId;
	private String lastName;
	private String firstName;
	private String middleName;
	private String email;
	private Set<Major> majors = new HashSet<Major>();
	private Term admitTerm;
	private Term gradTerm;

<<<<<<< HEAD
	public Line convert(List<String> tokens){
		System.out.println(tokens); 
		Line line = new Line();
		String[] field = (String[]) tokens.toArray();
		line.setStudentId(field[0]);
		line.setLastName(field[1]);
		line.setFirstName(field[2]);
		line.setMiddleName(field[3]);
		line.setEmail(field[4]);

		Set<Major> majors = line.getMajors();
		Major major;
		boolean shouldIgnore = shouldIgnore(field[5]);
		if (shouldIgnore) {
			return null;
		} else {
			major = makeMajor(field[5],field[6]);
			majors.add(major);
		}
		shouldIgnore = shouldIgnore(field[7]);
		if (!shouldIgnore) {
			major = makeMajor(field[7],field[8]);
			majors.add(major);
		}
		shouldIgnore = shouldIgnore(field[9]);
		if (!shouldIgnore) {
			major = makeMajor(field[9],field[10]);
			majors.add(major);
		}

		line.setAdmitTerm(doMakeTerm(field[11]));
		line.setGradTerm(doMakeTerm(field[12]));

		return line;
	}

	private boolean shouldIgnore(String major) {
		boolean ignore = (major.equals(FileDepartments.RECBS.toString()) 
				|| major.equals(FileDepartments.RECMIN.toString()) 
				|| major.equals(FileDepartments.RECPGMBS.toString())
				|| major.equals(FileDepartments.ENTMIN.toString())
				|| major.equals(" "));
		if (ignore) {
			return true;
		} else {
			return false;
		}
	}

	private Term makeTerm(String yearAndTerm) {
=======
	public Line(List<String> tokens){
		if (tokens.size() != EXPECTED_SIZE){
			throw new InvalidLineException("Invalid number of elements.");
		}
		if (hasAtLeastOneMajor(tokens.get(5))) {
			this.setStudentId(tokens.get(0));
			this.setLastName(tokens.get(1));
			this.setFirstName(tokens.get(2));
			this.setMiddleName(tokens.get(3));
			this.setEmail(tokens.get(4));
	
			Set<Major> majors = this.getMajors();
			Major major;
			if (shouldInclude(tokens.get(5))) {
				major = makeMajor(tokens.get(5),tokens.get(6));
				majors.add(major);
			}
			if (shouldInclude(tokens.get(7))) {
				major = makeMajor(tokens.get(7),tokens.get(8));
				majors.add(major);
			}
			if (shouldInclude(tokens.get(9))) {
				major = makeMajor(tokens.get(9),tokens.get(10));
				majors.add(major);
			}
		
			this.setAdmitTerm(createOrFindTerm(tokens.get(11)));
			if (!StringUtils.isBlank(tokens.get(12))){
				this.setGradTerm(createOrFindTerm(tokens.get(12)));
			}
		}
	}
	
	private boolean hasAtLeastOneMajor(String major1) {
		return major1 == SPACE;
	}
	
	private boolean shouldInclude(String major) {
		return major == SPACE;
	}

	private Term createOrFindTerm(String yearAndTerm) {
		if (yearAndTerm.equals(" ")){
			throw new InvalidTokenException("Invalid Term:" + yearAndTerm);
		}
>>>>>>> 17a733858fae4f1ee8a4b4079c3f66e55b437353
		char[] character = {0,0,0,0};
		Integer termYear = null;
		Semester semester = null;

		yearAndTerm.getChars(0,4,character,0);
		termYear = convertToYear(character[0], character[1], character[2]);
		semester = convertToSemester(character[3]);

<<<<<<< HEAD
		if (semester.equals(Semester.FALL)) {
			term.setSemester(Semester.FALL);
		} else if (semester.equals(Semester.SPRING)) {
			term.setSemester(Semester.SPRING);
		} else if (semester.equals(Semester.SUMMER)) {
			term.setSemester(Semester.SUMMER);
		} else {
			throw new IllegalArgumentException("Invalid semester:" + semester);
		}
		return term;
	}

=======
		Term term;
		try {
			term = Term.findTermsBySemesterAndTermYearEquals(semester, termYear).getSingleResult();
			return term;
		} catch(EmptyResultDataAccessException e) {
			term = new Term();
			term.setSemester(semester);
			term.setTermYear(termYear);
			term.persist();
		}
		return term;
	}
	
>>>>>>> 17a733858fae4f1ee8a4b4079c3f66e55b437353
	private Integer convertToYear(char century, char leftYear, char rightYear) {
		Integer year = null;
		if ('0' == century) { 
			year = 1900;
		} else if ('2' == century) { 
			year = 2000;
		} else {
			throw new InvalidTokenException("Invalid century:" + century);
		}
		String yearString = new StringBuilder().append(leftYear).append(rightYear).toString();
		year += Integer.valueOf(yearString);
		return year;
	}

	private Semester convertToSemester(char semester){
		if ('8' == semester) {
			return Semester.FALL;
		} else if ('2' == semester) {
			return Semester.SPRING;
		} else if ('5' == semester) {
			return Semester.SUMMER;
		} else {
			throw new InvalidTokenException("Invalid semester:" + semester);
		}
	} 
<<<<<<< HEAD

	private Term doMakeTerm(String term) {
		if (term.equals(" ")){
			return null;
		} else {
			Term aterm = makeTerm(term);
			TypedQuery<Term> q = Term.findTermsBySemesterAndTermYearEquals(aterm.getSemester(), aterm.getTermYear());
            /*if (0 < q.getResultList().size()) {
                aterm = aterm.merge();
            } else {
                aterm.persist();
            }*/
			if (0 >= q.getResultList().size()) {
				aterm.persist();
			}
			return aterm;
		}
	}

	private Major makeMajor(String amajor, String aterm) {
		Major major = new Major();
		Departments department = departmentMapper(amajor);
		major.setDepartment(department);
		Term term = doMakeTerm(aterm);
		major.setCatalogTerm(term);
		major.setCompleted_work_requirements(false);
		major.setReachedMilestone(false);
		return major;
	}

	private Departments departmentMapper(String major) {
		if (major.equals(FileDepartments.CAMBSCM.toString())) {
			return Departments.CAMBSCM;
		} else if (major.equals(FileDepartments.CAMPRE.toString())) {
			return Departments.CAMPRE;
		} else if (major.equals(FileDepartments.CBEVBSCM.toString())) {
			return Departments.CBEVBSCM;
		} else if (major.equals(FileDepartments.CBEVPRBSCM.toString())) {
			return Departments.CBEVPRBSCM;
		} else if (major.equals(FileDepartments.FDMBSHA.toString())) {
			return Departments.FDMBSHA;
		} else if (major.equals(FileDepartments.FDMPRE.toString())) {
			return Departments.FDMPRE;
		} else if (major.equals(FileDepartments.GAMBSGM.toString())) {
			return Departments.GAMBSGM;
		} else if (major.equals(FileDepartments.GAMPRE.toString())) {
			return Departments.GAMPRE;
		} else if (major.equals(FileDepartments.HBEVBSHA.toString())) {
			return Departments.HBEVBSHA;
		} else if (major.equals(FileDepartments.HBEVPRBSHA.toString())) {
			return Departments.HBEVPRBSHA;
		} else if (major.equals(FileDepartments.HOSSINBSMS.toString())) {
			return Departments.HOSSINBSMS;
		} else if (major.equals(FileDepartments.LRMBSHA.toString())) {
			return Departments.LRMBSHA;
		} else if (major.equals(FileDepartments.LRMPRE.toString())) {
			return Departments.LRMPRE;
		} else if (major.equals(FileDepartments.MEMBSHA.toString())) {
			return Departments.MEMBSHA;
		} else if (major.equals(FileDepartments.MEMPRE.toString())) {
			return Departments.MEMPRE;
		} else {
			throw new IllegalArgumentException("Invalid Major: " + major);
		}
	}
}

=======
	
	private Major makeMajor(String amajor, String aterm) {
		Term term = createOrFindTerm(aterm);
		Major major = new Major(amajor,term);
		return major;
	}
}
>>>>>>> 17a733858fae4f1ee8a4b4079c3f66e55b437353
