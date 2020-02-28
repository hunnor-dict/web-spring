package net.hunnor.dict.client;

import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
@EnableWebMvc
public class ResourceConfig implements WebMvcConfigurer {

  @Override
  public final void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("//**")
        .addResourceLocations("classpath:/public/")
        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
        .resourceChain(true)
        .addResolver(new VersionResourceResolver()
            .addContentVersionStrategy("/**"))
        .addTransformer(new CssLinkResourceTransformer());

  }

  @Bean
  public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
    return new ResourceUrlEncodingFilter();
  }

}
