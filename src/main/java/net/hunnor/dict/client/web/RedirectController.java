package net.hunnor.dict.client.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Controller
public class RedirectController {

  @RequestMapping(value = {"/m", "/m/*", "/no", "/no/*", "/search", "/gramm", "/gramm/*"},
      method = RequestMethod.GET)
  public final String redirect() {
    return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/";
  }

}
