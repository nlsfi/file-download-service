package fi.nls.fileservice.web.common;

import java.io.InputStream;
import java.util.Set;

import javax.servlet.ServletContext;

import fi.nls.fileservice.jcr.repository.ScriptProvider;

public class ServletContextScriptProvider implements ScriptProvider {

    private final ServletContext servletContext;
    private final String path;

    public ServletContextScriptProvider(ServletContext servletContext,
            String path) {
        this.servletContext = servletContext;
        if (path != null && !path.endsWith("/")) {
            path = path + "/";
        }
        this.path = path;
    }

    @Override
    public InputStream getScriptAsStream(String name) {
        if (!name.startsWith("/")) {
            name = path + name;
        }
        return servletContext.getResourceAsStream(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getScriptNames() {
        return servletContext.getResourcePaths(path);
    }

}
