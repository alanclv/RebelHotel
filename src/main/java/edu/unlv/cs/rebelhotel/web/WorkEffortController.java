package edu.unlv.cs.rebelhotel.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import edu.unlv.cs.rebelhotel.domain.Major;
import edu.unlv.cs.rebelhotel.domain.Student;
import edu.unlv.cs.rebelhotel.domain.Term;
import edu.unlv.cs.rebelhotel.domain.WorkEffort;
import edu.unlv.cs.rebelhotel.domain.enums.Semester;
import edu.unlv.cs.rebelhotel.service.RandomValidationService;
import edu.unlv.cs.rebelhotel.service.UserInformation;
import edu.unlv.cs.rebelhotel.validators.WorkEffortValidator;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "workefforts", formBackingObject = WorkEffort.class, exposeFinders=false)
@RequestMapping("/workefforts")
@Controller
public class WorkEffortController {
	@Autowired
	private UserInformation userInformation;
	
	@Autowired
	private RandomValidationService randomValidationService;
	
	@Autowired
	private WorkEffortValidator workEffortValidator;
	
	public void setWorkEffortValidator(WorkEffortValidator workEffortValidator) {
		this.workEffortValidator = workEffortValidator;
	}
	
	public void setRandomValidationService (RandomValidationService randomValidationService) {
		this.randomValidationService = randomValidationService;
	}
	
	// NOTE : the params string should not be equivalent to any of the fields in the form
	// otherwise the validator (?) will assume the params value is set to null (?) ... very annoying bug
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(value = "/{sid}", params = "forstudent", method = RequestMethod.POST)
    public String createStudent(@PathVariable("sid") Long sid, @Valid WorkEffort workEffort, BindingResult result, Model model, HttpServletRequest request) {
		if (result.hasErrors()) {
            model.addAttribute("workEffort", workEffort);
            addDateTimeFormatPatterns(model);
            Student student = Student.findStudent(sid);
            model.addAttribute("student", student);
            model.addAttribute("sid", sid);
            return "workefforts/createFromStudent";
        }
		workEffort.persist();
		
		Student student = workEffort.getStudent();
		student.addWorkEffort(workEffort);
		student.merge();
        return "redirect:/workefforts/" + encodeUrlPathSegment(workEffort.getId().toString(), request);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(method = RequestMethod.POST)
    public String create(WorkEffort workEffort, BindingResult result, Model model, HttpServletRequest request) {
        workEffortValidator.validate(workEffort, result);
		if (result.hasErrors()) {
            model.addAttribute("workEffort", workEffort);
            addDateTimeFormatPatterns(model);
            return "workefforts/create";
        }
        workEffort.persist();
        workEffort.getStudent().addWorkEffort(workEffort);
        workEffort.getStudent().merge();
        return "redirect:/workefforts/" + encodeUrlPathSegment(workEffort.getId().toString(), request);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(method = RequestMethod.PUT)
    public String update(WorkEffort workEffort, BindingResult result, Model model, HttpServletRequest request) {
        workEffortValidator.validate(workEffort, result);
		if (result.hasErrors()) {
            model.addAttribute("workEffort", workEffort);
            return "workefforts/update";
        }
        workEffort.merge();
        return "redirect:/workefforts/" + encodeUrlPathSegment(workEffort.getId().toString(), request);
    }

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("workEffort", new WorkEffort());
        addDateTimeFormatPatterns(model);
        List dependencies = new ArrayList();
        if (Student.countStudents() == 0) {
            dependencies.add(new String[]{"student", "students"});
        }
        model.addAttribute("dependencies", dependencies);
        return "workefforts/create";
    }
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(value = "/{sid}", params = "forstudent", method = RequestMethod.GET)
    public String createStudentForm(@PathVariable("sid") Long sid, Model model) {
        model.addAttribute("workEffort", new WorkEffort());
        addDateTimeFormatPatterns(model);
        List dependencies = new ArrayList();
        if (Student.countStudents() == 0) {
            dependencies.add(new String[]{"student", "students"});
        }
        Student student = Student.findStudent(sid);
        model.addAttribute("student", student);
        model.addAttribute("dependencies", dependencies);
        model.addAttribute("sid", sid);
        // TODO check if one is able to place the value of the student here without relying on the hidden form element
        // RESULT apparently it cannot be done
        return "workefforts/createFromStudent";
    }
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("workEffort", WorkEffort.findWorkEffort(id));
        addDateTimeFormatPatterns(model);
        return "workefforts/update";
    }
	
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(params = "mywork", method = RequestMethod.GET)
	public String listPersonalWork(Model model) {
		model.addAttribute("str", "A list to contain your completed jobs");
		Student student = userInformation.getStudent();
		List<WorkEffort> workEfforts = WorkEffort.findWorkEffortsByStudentEquals(student).getResultList();
		model.addAttribute("workefforts", workEfforts);
		return "workefforts/personalList";
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        WorkEffort.findWorkEffort(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/workefforts?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }
	
	@Secured("VIEW_WORK_EFFORT") // custom voter will check this request
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("workeffort", WorkEffort.findWorkEffort(id));
        model.addAttribute("itemId", id);
        return "workefforts/show";
    }
	
/* ===================================
 * Random Validation Controller Actions
 * Alan Chapman
 * Last Updated: 4-23-11
 * =================================== 
 */
	
	@RequestMapping(params = "random", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	public String randomValidationForm(Model model) {
		return "workefforts/random_validation_form";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERUSER')")
	@RequestMapping(params = "random", method = RequestMethod.POST)
	public String randomValidation(@RequestParam("semester") String semester, @RequestParam("year") String year, 
			@RequestParam("sampleSize") String sampleSize, Model model) {
		if (randomValidationService.inputIsValid(year, sampleSize)) {
			int term_year = Integer.valueOf(year);
			int sample_size = Integer.valueOf(sampleSize);
			
			Semester sem = randomValidationService.getSemester(semester);
			Term currentTerm;
			try { // TODO: need to find a way to redirect to my exception handler.
				  //Currently being intercepted by the after advice in the pointcut 
				currentTerm = Term.findTermsBySemesterAndTermYearEquals(sem, term_year).getSingleResult();
			} catch (NoResultException e) {
				return "workefforts/random_validation_form";
			}
			List<WorkEffort> randomResultList = randomValidationService.getRandomValidationList(currentTerm, sample_size);
			model.addAttribute("randomResultList", randomResultList );
			
			return "workefforts/random";
	    }
		else {
			return "workefforts/random_validation_form";
		}
	}
	
	
	void addDateTimeFormatPatterns(Model model) {
        model.addAttribute("workEffortDuration_startdate_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
        model.addAttribute("workEffortDuration_enddate_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }
}
