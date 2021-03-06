package chushka.utils.htmlbuilder;


import chushka.utils.reader.Reader;
import chushka.utils.templatebuilder.TemplateBuilder;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class HtmlBuilderImpl implements HtmlBuilder {

    private static final String HTML_BASE_TEMPLATE_URI = "/html/templates/base.html";

    private final Reader fileReader;

    @Inject
    public HtmlBuilderImpl(Reader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public Optional<String> buildFrom(String baseTemplateUri,
                                      Map<String, String> templatesUris,
                                      Map<String, String> params) {
        Optional<String> skeleton = fileReader.read(baseTemplateUri);
        Optional<Map<String, String>> templates = fileReader.read(templatesUris);

        if (skeleton.isEmpty() || templates.isEmpty()) {
            return Optional.empty();
        }

        String html = TemplateBuilder
                .from(skeleton.orElse(""))
                .put(templates.orElse(Collections.emptyMap()))
                .put(params)
                .build();

        return Optional.of(html);
    }

    @Override
    public Optional<String> buildFrom(Map<String, String> templatesUris,
                                      Map<String, String> params) {
        return buildFrom(HTML_BASE_TEMPLATE_URI, templatesUris, params);
    }

    @Override
    public Optional<String> buildFrom(String baseTemplateUri,
                                      Map<String, String> params) {
        return buildFrom(baseTemplateUri, Collections.emptyMap(), params);
    }

    @Override
    public Optional<String> buildFrom(Map<String, String> params) {
        return buildFrom(HTML_BASE_TEMPLATE_URI, params);
    }

    @Override
    public Optional<String> buildFrom(String baseTemplateUri) {
        return buildFrom(baseTemplateUri, Collections.emptyMap(), Collections.emptyMap());
    }
}
