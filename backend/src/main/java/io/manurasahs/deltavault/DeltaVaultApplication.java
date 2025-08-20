package io.manurasahs.deltavault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DeltaVaultApplication
{

    static void main(String[] args)
    {
        SpringApplication.run(DeltaVaultApplication.class, args);
    }
}
