package net.hunnor.dict.client.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Controller
public class RedirectController {

  @GetMapping(value = {"/m", "/m/*", "/no", "/no/*", "/search", "/gramm", "/gramm/*"})
  public final String redirect() {
    return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/";
  }

}
