package net.hunnor.dict;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * The launcher class of the application.
 */
@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter {

	/**
	 * The launcher method of the application.
	 * @param args command line parameters
	 */
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Locale resolver with default locale set to Hungarian.
	 * @return the locale resolver
	 */
	//CHECKSTYLE:OFF
	@Bean
	//CHECKSTYLE:ON
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("hu"));
		return localeResolver;
	}

	/**
	 * Locale change interceptor listening to request parameter 'lang'.
	 * @return the locale change interceptor
	 */
	//CHECKSTYLE:OFF
	@Bean
	//CHECKSTYLE:ON
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor =
				new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	/**
	 * Add the locale change interceptor to the list of default interceptors.
	 */
	@Override
	public final void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Override
	public final void addResourceHandlers(
			final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**").addResourceLocations(
				"classpath:/META-INF/resources/webjars/");
	}

}
