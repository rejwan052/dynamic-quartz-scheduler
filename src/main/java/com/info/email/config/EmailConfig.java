package com.info.email.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@PropertySource("classpath:email.properties")
public class EmailConfig {
	
	 private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	 
	 private static final long maxUploadSize = 26214400; // 25 Mb
	 
	 @Autowired
     private Environment env;

     // beans

     @Bean
     public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
         return new PropertySourcesPlaceholderConfigurer();
     }
     
     @Bean
     public JavaMailSenderImpl javaMailSenderImpl() {
         final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();

         try {
             mailSenderImpl.setHost(env.getRequiredProperty("smtp.host"));
             mailSenderImpl.setPort(env.getRequiredProperty("smtp.port", Integer.class));
             mailSenderImpl.setProtocol(env.getRequiredProperty("smtp.protocol"));
             mailSenderImpl.setUsername(env.getRequiredProperty("smtp.username"));
             mailSenderImpl.setPassword(env.getRequiredProperty("smtp.password"));
         } catch (IllegalStateException ise) {
             LOGGER.error("Could not resolve email.properties.  See email.properties.sample");
             throw ise;
         }
         final Properties javaMailProps = new Properties();
         javaMailProps.put("mail.smtp.auth", true);
         javaMailProps.put("mail.smtp.starttls.enable", true);
         mailSenderImpl.setJavaMailProperties(javaMailProps);
         return mailSenderImpl;
     }
     
     
     @Bean 
 	public ClassLoaderTemplateResolver emailTemplateResolver(){ 
 		ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver(); 
 		emailTemplateResolver.setPrefix("email-templates/"); 
 		emailTemplateResolver.setSuffix(".html"); 
 		emailTemplateResolver.setTemplateMode("HTML5"); 
 		emailTemplateResolver.setCharacterEncoding("UTF-8"); 
 		emailTemplateResolver.setOrder(1);
 		
 		return emailTemplateResolver; 
 	}
     
     
     @Bean
     public MultipartResolver multipartResolver() {
         CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
         multipartResolver.setMaxUploadSize(maxUploadSize);
         return multipartResolver;
     }

}

