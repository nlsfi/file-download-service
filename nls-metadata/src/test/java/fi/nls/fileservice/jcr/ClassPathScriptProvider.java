package fi.nls.fileservice.jcr;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import fi.nls.fileservice.jcr.repository.ScriptProvider;

public class ClassPathScriptProvider implements ScriptProvider {

    private String[] scriptNames = { "korkeusmalli_10m.js",
            "korkeusmalli_2m.js", "kuntajako.js", "laser.js",
            "maastokartta_100k.js", "maastokartta_250k.js",
            "maastokarttarasteri_100k.js", "maastokarttarasteri_250k.js",
            "maastokarttarasteri.js", "maastotietokanta.js",
            "maastotietokanta_tiesto_osoitteilla.js", "nimisto.js", "orto.js",
            "peruskarttarasteri.js", "taustakarttasarja.js",
            "yleiskartta_1000k.js", "yleiskartta_4500k.js",
            "yleiskarttarasteri_1000k.js", "yleiskarttarasteri_4500k.js",
            // "kiinteistorekisterikartta.js",
            "vinovalovarjoste.js", "korkeusvyohyke.js", "karttalehtijako.js"

    };

    @Override
    public InputStream getScriptAsStream(String name) {
        return ClassPathScriptProvider.class.getClassLoader()
                .getResourceAsStream(name);
    }

    @Override
    public Set<String> getScriptNames() {
        Set<String> scripts = new HashSet<String>();
        for (String script : scriptNames) {
            scripts.add(script);
        }
        return scripts;
    }

}
