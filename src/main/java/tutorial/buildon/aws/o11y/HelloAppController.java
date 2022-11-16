package tutorial.buildon.aws.o11y;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
public class HelloAppController {

    private static final Logger log =
        LoggerFactory.getLogger(HelloAppController.class);

    @RequestMapping(method= RequestMethod.GET, value="/hello")
    public Response hello() {
        Response response = buildResponse();
        if (response.isValid()) {
            log.info("The response is valid.");
        }
        return response;
    }

    private Response buildResponse() {
        return new Response("Hello World");
    }

    @Getter @Setter
    @RequiredArgsConstructor
    private class Response {

        @NonNull
        private String message;

        public boolean isValid() {
            return true;
        }

    }
    
}
