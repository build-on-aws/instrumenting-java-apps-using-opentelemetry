package tutorial.buildon.aws.o11y;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import static tutorial.buildon.aws.o11y.Constants.*;

import java.util.Objects;

import static java.lang.Runtime.*;

import javax.annotation.PostConstruct;

@RestController
public class HelloAppController {

    private static final Logger log =
        LoggerFactory.getLogger(HelloAppController.class);

    @Value("otel.traces.api.version")
    private String tracesApiVersion;

    @Value("otel.metrics.api.version")
    private String metricsApiVersion;

    private final Tracer tracer =
        GlobalOpenTelemetry.getTracer("io.opentelemetry.traces.hello",
            tracesApiVersion);

    private final Meter meter =
        GlobalOpenTelemetry.meterBuilder("io.opentelemetry.metrics.hello")
            .setInstrumentationVersion(metricsApiVersion)
            .build();

    private LongCounter numberOfExecutions;

    @PostConstruct
    public void createMetrics() {

        numberOfExecutions =
            meter
                .counterBuilder(NUMBER_OF_EXEC_NAME)
                .setDescription(NUMBER_OF_EXEC_DESCRIPTION)
                .setUnit("int")
                .build();

        meter
            .gaugeBuilder(HEAP_MEMORY_NAME)
            .setDescription(HEAP_MEMORY_DESCRIPTION)
            .setUnit("byte")
            .buildWithCallback(
                r -> {
                    r.record(getRuntime().totalMemory() - getRuntime().freeMemory());
                });

    }

    @RequestMapping(method= RequestMethod.GET, value="/hello")
    public Response hello() {
        Response response = buildResponse();
        // Creating a custom span
        Span span = tracer.spanBuilder("mySpan").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (response.isValid()) {
                log.info("The response is valid.");
            }
            // Update the synchronous metric
            numberOfExecutions.add(1);
        } finally {
            span.end();
        }
        return response;
    }

    @WithSpan
    private Response buildResponse() {
        return new Response("Hello World");
    }

    private record Response (String message) {
        private Response {
            Objects.requireNonNull(message);
        }
        private boolean isValid() {
            return true;
        }
    }

}
