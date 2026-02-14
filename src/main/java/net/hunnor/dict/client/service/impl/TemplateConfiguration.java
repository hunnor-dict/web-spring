package net.hunnor.dict.client.service.impl;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateConfiguration {

  @Autowired
  private TemplateEngine templateEngine;

  /**
   * Add a template resolver for text templates.
   */
  @PostConstruct
  public void addTextTemplateResolver() {
    ClassLoaderTemplateResolver textTemplateResolver = new ClassLoaderTemplateResolver();
    textTemplateResolver.setOrder(templateEngine.getTemplateResolvers().size());
    textTemplateResolver.setPrefix("/templates/");
    textTemplateResolver.setSuffix(".txt");
    textTemplateResolver.setTemplateMode(TemplateMode.TEXT);
    textTemplateResolver.setCharacterEncoding("UTF8");
    Set<String> patterns = new HashSet<>();
    patterns.add("mail/*");
    textTemplateResolver.setResolvablePatterns(patterns);
    templateEngine.addTemplateResolver(textTemplateResolver);
  }

}
