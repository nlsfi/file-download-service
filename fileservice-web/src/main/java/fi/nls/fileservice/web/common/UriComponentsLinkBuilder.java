package fi.nls.fileservice.web.common;

import java.util.Map;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class UriComponentsLinkBuilder implements LinkBuilder {

    private UriComponents uriComponents;
    private String[] paths;
    private Map<String, String> queryParams;

    public UriComponentsLinkBuilder(UriComponents builder,
            Map<String, String> queryParams, String... pathSegments) {
        this.uriComponents = builder;
        this.paths = pathSegments;
        this.queryParams = queryParams;
    }

    @Override
    public String buildUri(String path) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(uriComponents.toUriString());
        if (paths != null) {
            for (String ps : paths) {
                builder.pathSegment(ps);
            }
        }

        if (queryParams != null) {
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                builder.queryParam(param.getKey(), param.getValue());
            }
        }

        if (path != null) {
            builder.path(path);
        }

        return builder.build().toUriString();
    }
}
