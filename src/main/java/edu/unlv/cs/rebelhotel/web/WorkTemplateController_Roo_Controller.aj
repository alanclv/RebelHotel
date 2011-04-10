// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package edu.unlv.cs.rebelhotel.web;

import edu.unlv.cs.rebelhotel.domain.Term;
import edu.unlv.cs.rebelhotel.domain.WorkTemplate;
import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect WorkTemplateController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String WorkTemplateController.create(@Valid WorkTemplate workTemplate, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("workTemplate", workTemplate);
            return "worktemplates/create";
        }
        workTemplate.persist();
        return "redirect:/worktemplates/" + encodeUrlPathSegment(workTemplate.getId().toString(), request);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String WorkTemplateController.createForm(Model model) {
        model.addAttribute("workTemplate", new WorkTemplate());
        return "worktemplates/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String WorkTemplateController.show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("worktemplate", WorkTemplate.findWorkTemplate(id));
        model.addAttribute("itemId", id);
        return "worktemplates/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String WorkTemplateController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("worktemplates", WorkTemplate.findWorkTemplateEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) WorkTemplate.countWorkTemplates() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("worktemplates", WorkTemplate.findAllWorkTemplates());
        }
        return "worktemplates/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String WorkTemplateController.update(@Valid WorkTemplate workTemplate, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("workTemplate", workTemplate);
            return "worktemplates/update";
        }
        workTemplate.merge();
        return "redirect:/worktemplates/" + encodeUrlPathSegment(workTemplate.getId().toString(), request);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String WorkTemplateController.updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("workTemplate", WorkTemplate.findWorkTemplate(id));
        return "worktemplates/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String WorkTemplateController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        WorkTemplate.findWorkTemplate(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/worktemplates?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }
    
    @RequestMapping(params = { "find=ByNameEquals", "form" }, method = RequestMethod.GET)
    public String WorkTemplateController.findWorkTemplatesByNameEqualsForm(Model model) {
        return "worktemplates/findWorkTemplatesByNameEquals";
    }
    
    @RequestMapping(params = "find=ByNameEquals", method = RequestMethod.GET)
    public String WorkTemplateController.findWorkTemplatesByNameEquals(@RequestParam("name") String name, Model model) {
        model.addAttribute("worktemplates", WorkTemplate.findWorkTemplatesByNameEquals(name).getResultList());
        return "worktemplates/list";
    }
    
    @ModelAttribute("terms")
    public Collection<Term> WorkTemplateController.populateTerms() {
        return Term.findAllTerms();
    }
    
    String WorkTemplateController.encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
