package net.hunnor.dict.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.testing.templateengine.context.web.SpringWebProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;

class TemplateTest {

  @Test
  void testViews() {

    final TestExecutor executor = new TestExecutor();

    final List<IDialect> dialects = new ArrayList<IDialect>();
    dialects.add(new SpringStandardDialect());
    executor.setDialects(dialects);

    final SpringWebProcessingContextBuilder builder = new SpringWebProcessingContextBuilder();
    builder.setApplicationContextConfigLocation(null);
    executor.setProcessingContextBuilder(builder);

    executor.execute("classpath:templates/views");
    assertTrue(executor.isAllOK());

  }

}
