package com.example.cpsplatform.template.renderer;

import com.example.cpsplatform.auth.sender.EmailAuthCodeSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

public class ThymeleafTemplateRenderer implements TemplateRenderer{

    private final SpringTemplateEngine templateEngine;

    public ThymeleafTemplateRenderer(final SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String render(final Map<String, String> variables, final String path) {
        Context context = new Context();
        variables.keySet()
                .forEach((value) ->
                        context.setVariable(value,variables.get(value))
                );
        return templateEngine.process(path, context);
    }

}
