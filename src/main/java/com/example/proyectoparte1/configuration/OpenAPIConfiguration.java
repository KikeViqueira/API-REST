package com.example.proyectoparte1.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Proyecto ENSE - 2024",
                description = "API del proyecto de Enxeñaría de Servizos",
                version = "1.0.0",
                contact = @Contact(
                        name = "Enrique Viqueira & Alejandro Vedo",
                        email = "enrique.viqueira@rai.usc.es, alejandro.vedo@rai.usc.es"
                ),
                license = @License(
                        name = "MIT Licence",
                        url = "https://opensource.org/licenses/MIT")
        ),
        servers = {
                @Server(url = "/", description = "General use server"),
        }
)
@SecurityScheme(
        name = "JWT",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfiguration {
}
