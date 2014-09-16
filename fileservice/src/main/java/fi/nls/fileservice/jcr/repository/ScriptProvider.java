package fi.nls.fileservice.jcr.repository;

import java.io.InputStream;
import java.util.Set;

public interface ScriptProvider {

    public InputStream getScriptAsStream(String name);

    public Set<String> getScriptNames();
}
