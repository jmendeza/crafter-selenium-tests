package org.craftercms.views;

import org.craftercms.services.api.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Simple Hello controller that greets you.
 */
@Controller
public class HelloController {
    /**
     * Index view name.
     * .ftl added in freemarker config
     */
    private static final String INDEX_VIEW_NAME = "simple";

    /**
     * GreetingService services instance.
     */
    @Autowired
    private GreetingService greetingService;

    /**
     * Main Entry Point
     * @return Index View Name
     */
    @RequestMapping(value="/", method = RequestMethod.GET)
    public final String sayHello() {
       return INDEX_VIEW_NAME;
    }

    /**
     * Says hello to the user.
     * @param name User Name
     * @return A custom Greet to the user
     */
    @RequestMapping(value="/", method = RequestMethod.POST)
    public final ModelAndView sayHello(@RequestParam(value = "personsName") final String name) {
        ModelAndView mvc = new ModelAndView(INDEX_VIEW_NAME);
        String finalGreet = greetingService.greet(name);
        mvc.addObject("person" , finalGreet);
        return mvc;
    }
}
