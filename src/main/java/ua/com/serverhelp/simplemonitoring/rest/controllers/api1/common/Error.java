package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.common;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class Error implements ErrorController {
    @RequestMapping("")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("code", statusCode);
        }
        Object statusMsg = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (statusMsg != null) {
            model.addAttribute("message", statusMsg);
        }
        return "error";
    }
}
