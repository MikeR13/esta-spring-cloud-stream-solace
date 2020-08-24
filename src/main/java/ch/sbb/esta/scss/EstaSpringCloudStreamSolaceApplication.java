package ch.sbb.esta.scss;

import io.opentracing.contrib.spring.cloud.solace.SolaceAutoConfiguration;
import io.opentracing.contrib.spring.integration.messaging.OpenTracingChannelInterceptorAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SolaceAutoConfiguration.class)
public class EstaSpringCloudStreamSolaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstaSpringCloudStreamSolaceApplication.class, args);
    }

}
