package com.example.cpsplatform.template.renderer;

import java.util.Map;

public interface TemplateRenderer {

    String render(Map<String, String> variables, String path);

}
