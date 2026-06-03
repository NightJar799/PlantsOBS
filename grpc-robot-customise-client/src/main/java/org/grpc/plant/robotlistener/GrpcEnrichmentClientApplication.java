package org.grpc.plant.robotlistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcEnrichmentClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcEnrichmentClientApplication.class, args);
    }
}
