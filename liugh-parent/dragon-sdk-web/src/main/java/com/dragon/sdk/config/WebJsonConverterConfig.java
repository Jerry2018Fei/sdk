package com.dragon.sdk.config;

/**
 * @author dingpengfei
 * @date 2019-05-20 23:00
 */
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/** Created by nqq on 2019/1/17. */
@EnableWebMvc
@Configuration
@Slf4j
public class WebJsonConverterConfig extends WebMvcConfigurerAdapter {
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
        new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule();
    JsonSerializer<Long> longJsonSerializer =
        new JsonSerializer<Long>() {

          @Override
          public void serialize(
              Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
              throws IOException, JsonProcessingException {
            String text = (aLong == null ? null : String.valueOf(aLong));
            if (text != null) {
              jsonGenerator.writeString(text);
            }
          }
        };
    simpleModule.addSerializer(Long.class, longJsonSerializer);
    simpleModule.addSerializer(Long.TYPE, longJsonSerializer);
    JsonSerializer<Date> dateJsonSerializer =
        new JsonSerializer<Date>() {

          @Override
          public void serialize(
              Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
              throws IOException, JsonProcessingException {
            String text = (date == null ? null : String.valueOf(date.getTime()));
            if (text != null) {
              jsonGenerator.writeString(text);
            }
          }
        };
    simpleModule.addSerializer(Date.class, dateJsonSerializer);
    objectMapper.registerModule(simpleModule);

    jackson2HttpMessageConverter.setObjectMapper(objectMapper);
    converters.add(jackson2HttpMessageConverter);
  }
}
