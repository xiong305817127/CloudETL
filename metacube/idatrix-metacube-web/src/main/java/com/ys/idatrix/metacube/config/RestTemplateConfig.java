package com.ys.idatrix.metacube.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置
 *
 * @author wzl
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate getRestTemplateBuilder() {

        RestTemplate restTemplate = new RestTemplate();

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
                StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(true);

        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.ALL);

        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();

        for (int i = 0; i < httpMessageConverters.size(); i++) {
            if (httpMessageConverters.get(i) instanceof StringHttpMessageConverter) {
                httpMessageConverters.set(i, stringHttpMessageConverter);
            }
            if (httpMessageConverters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                try {
                    ((MappingJackson2HttpMessageConverter) httpMessageConverters.get(i))
                            .setSupportedMediaTypes(mediaTypeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return restTemplate;
    }
}
