package eu.dec21.wp.tasks.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${server.addr}")
    private String srvAddr;
    @Value("${server.protocol}")
    private String srvProto;
    @Value("${server.port}")
    private String srvPort;
    @Value("${application.owner.name}")
    private String appOwnerName;
    @Value("${application.owner.email}")
    private String appOwnerEmail;
    @Value("${application.owner.company}")
    private String appOwnerCompany;
    @Value("${application.owner.Url}")
    private String appOwnerURL;

    @Bean
    public OpenAPI wpOpenAPI() {
        Server devSrv = new Server();
        devSrv.setUrl(getDevSrvURL());
        devSrv.setDescription("Server URL in Development environment");

        Server stgSrv = new Server();
        stgSrv.setUrl(getStageSrvURL());
        devSrv.setDescription("Server URL in Stage environment");

        Server prdSrv = new Server();
        prdSrv.setUrl(getProdSrvURL());
        devSrv.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setName(appOwnerName);
        contact.setEmail(appOwnerEmail);
        contact.setUrl(appOwnerURL);

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Tutorial Management API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage Weekly ToDo Plan.").termsOfService("https://wp.dec21.eu/terms")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devSrv, stgSrv, prdSrv));
    }

    private String getDevSrvURL() {
        return srvProto + "://" + srvAddr + ":" + srvPort;
    }

    private String getProdSrvURL() {
        return "https://wp.dec21.eu";
    }

    private String getStageSrvURL() {
        return "https://wp-stage.dec21.eu";
    }
}
