package databaseApp.db.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationBeenConfiguration {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        // Skip null values
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // Skip blank strings as well
        modelMapper.getConfiguration().setPropertyCondition(ctx -> {
            Object source = ctx.getSource();
            if (source instanceof String) {
                return !((String) source).isBlank();
            }
            return source != null;
        });

        return modelMapper;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){

        return restTemplateBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
