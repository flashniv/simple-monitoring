package ua.com.serverhelp.simplemonitoring.rest.system;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class SimpleErrorController implements ErrorController {
    @RequestMapping("")
    public ResponseEntity<String> error(HttpServletRequest httpServletRequest) {
        var code = (Integer) httpServletRequest.getAttribute("jakarta.servlet.error.status_code");
        var errText = (String) httpServletRequest.getAttribute("jakarta.servlet.error.message");
        var response = """
                <html>
                    <head>
                    </head>
                    <body>
                        <h1>Error __code__</h1>
                        <p>error: __text__</>
                    </body>
                </html>
                """.replace("__text__", errText).replace("__code__", String.valueOf(code));
        return ResponseEntity.status(code).contentType(MediaType.TEXT_HTML).body(response);
    }
}
