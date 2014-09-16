package fi.nls.fileservice.jcr.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.jcr.Node;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavascriptMetadataServiceExecutor implements
        MetadataUpdateExecutor {

    private static final Logger logger = LoggerFactory
            .getLogger(JavascriptMetadataServiceExecutor.class);

    public static final String CORE_SCRIPT_NAME = "core.js";
    public static final String ENGINE_NAME = "JavaScript";
    private ScriptEngine scriptEngine;
    private Map<String, CompiledScript> scriptCache;
    private ScriptProvider scriptProvider;

    public JavascriptMetadataServiceExecutor() {
        this.scriptCache = new WeakHashMap<String, CompiledScript>();
    }

    public void setScriptProvider(ScriptProvider provider) {
        this.scriptProvider = provider;
    }

    @Override
    public void init() {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName(ENGINE_NAME);
        if (engine == null) {
            throw new IllegalStateException("ScriptEngine not found by name '"
                    + ENGINE_NAME + "'");
        }
        this.scriptEngine = engine;
        CompiledScript coreScript = loadScript(CORE_SCRIPT_NAME,
                scriptProvider.getScriptAsStream(CORE_SCRIPT_NAME));
        scriptCache.put("core", coreScript);
        logger.debug("Loaded core script file {}", CORE_SCRIPT_NAME);

        Set<String> scriptNames = scriptProvider.getScriptNames();
        for (String scriptName : scriptNames) {
            if (!scriptName.contains("core.js")) {
                CompiledScript script = loadScript(scriptName,
                        scriptProvider.getScriptAsStream(scriptName));
                scriptCache.put(scriptName, script);
                logger.debug("Loaded script file {}", scriptName);
            }
        }
    }

    private CompiledScript loadScript(String name, InputStream stream) {
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            if (scriptEngine instanceof Compilable) {
                Compilable c = (Compilable) scriptEngine;
                CompiledScript compiledScript = c.compile(reader);
                compiledScript.eval();
                return compiledScript;
            } else {
                scriptEngine.eval(reader);
            }
        } catch (ScriptException e) {
            throw new IllegalStateException("Error parsing script: " + name, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new ScriptExecutionException(
                            "Error reading javascript file from path", e);
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> processNode(Node node) {

        CompiledScript script = scriptCache.get("core");

        Map<String, Object> outputProperties = new HashMap<String, Object>();

        Invocable invocable = (Invocable) script.getEngine();
        try {
            invocable.invokeFunction("processNode", node, outputProperties);
            return outputProperties;
        } catch (ScriptException e) {
            throw new ScriptExecutionException(e);
        } catch (NoSuchMethodException e) {
            throw new ScriptExecutionException(e);
        }

    }

}
