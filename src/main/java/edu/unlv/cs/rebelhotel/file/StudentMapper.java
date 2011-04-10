package edu.unlv.cs.rebelhotel.file;

<<<<<<< HEAD
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.unlv.cs.rebelhotel.domain.Major;
import edu.unlv.cs.rebelhotel.domain.Student;
import edu.unlv.cs.rebelhotel.domain.UserAccount;
import edu.unlv.cs.rebelhotel.domain.enums.UserGroup;
import edu.unlv.cs.rebelhotel.service.WorkRequirementService;

@Component
public class StudentMapper {
	WorkRequirementService workRequirementService;

	@Autowired
	public StudentMapper(WorkRequirementService workRequirementService) {
		this.workRequirementService = workRequirementService;
	}

=======
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import edu.unlv.cs.rebelhotel.domain.Student;
import edu.unlv.cs.rebelhotel.domain.UserAccount;
import edu.unlv.cs.rebelhotel.file.RandomPasswordGenerator;

@Component
public class StudentMapper {
>>>>>>> 17a733858fae4f1ee8a4b4079c3f66e55b437353
	public Student findOrReplace(FileStudent fileStudent){
		Student student = existingOrNewStudent(fileStudent);
		student.setUserId(fileStudent.getStudentId());
		student.setFirstName(fileStudent.getFirstName());
		student.setLastName(fileStudent.getLastName());
		student.setMiddleName(fileStudent.getMiddleName());
		student.setEmail(fileStudent.getEmail());
		student.setGradTerm(fileStudent.getGradTerm());
		student.setAdmitTerm(fileStudent.getAdmitTerm());
<<<<<<< HEAD

		/* if both major columns are populated, and there is only one requirement term,
		 * then can we assume that it is the same for both majors? I suppose...but what if
		 * they take up another major three years after declaring the first one?
		 */
		Set<Major> majors = workRequirementService.updateStudentInformation(student.getMajors(),fileStudent.getMajors());
		student.setMajors(majors);

		//boolean codeOfConductSigned = student.getCodeOfConductSigned();
		// case 1) a new student, so it will not be true, and so we will initialize it to false
		// case 2) a preexisting student, so it can either be true or false; if true, then true; otherwise, false
		//codeOfConductSigned = (codeOfConductSigned) ? true : false;
		student.setCodeOfConductSigned(false);

		UserAccount student_account = new UserAccount();
		student_account.setUserId(fileStudent.getStudentId());
		student_account.setPassword("password");
		student_account.setUserGroup(UserGroup.ROLE_USER);
		student_account.setEmail(fileStudent.getEmail());
		
		if (UserAccount.findUserAccountsByUserId(fileStudent.getStudentId()).getResultList().size() > 0) {
			student_account.merge();
		} else {
			student_account.persist();
		}
=======
		student.updateMajors(fileStudent.getMajors());
		student.setCodeOfConductSigned(false);

		UserAccount studentAccount = existingOrNewAccount(fileStudent);
		student.setUserAccount(studentAccount);
>>>>>>> 17a733858fae4f1ee8a4b4079c3f66e55b437353
		
		return student;
	}

	private Student existingOrNewStudent(FileStudent fileStudent) {
		Student student;
		try {
			student = Student.findStudentsByUserIdEquals(fileStudent.getStudentId()).getSingleResult();
			return student;
		} catch(EmptyResultDataAccessException e) {
			student = new Student();
		}
		return student;
	}
	
	private UserAccount existingOrNewAccount(FileStudent fileStudent) {
		UserAccount studentAccount;
		try {
			studentAccount = UserAccount.findUserAccountsByUserId(fileStudent.getStudentId()).getSingleResult();
			return studentAccount;
		} catch(EmptyResultDataAccessException e) {
			RandomPasswordGenerator rpg = new RandomPasswordGenerator();
			studentAccount = new UserAccount(fileStudent,rpg.generateRandomPassword());
			studentAccount.persist();
		}
		return studentAccount;
	}
}
