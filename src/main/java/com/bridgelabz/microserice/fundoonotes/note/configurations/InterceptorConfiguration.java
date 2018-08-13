/*package com.bridgelabz.microserice.fundoonotes.note.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bridgelabz.microserice.fundoonotes.note.interceptors.NoteInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer{
	
	@Autowired
	NoteInterceptor noteInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(noteInterceptor).addPathPatterns("/notes/**","/labels/**");
	}
	
}
*/