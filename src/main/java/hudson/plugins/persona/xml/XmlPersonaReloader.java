package hudson.plugins.persona.xml;

import hudson.Extension;
import hudson.ExtensionComponent;
import hudson.ExtensionList;
import hudson.Util;
import hudson.model.Hudson;
import hudson.model.RootAction;
import hudson.plugins.persona.Persona;
import org.dom4j.DocumentException;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Exposes an URL to reload XML persona.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class XmlPersonaReloader implements RootAction {

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "reload-persona";
    }

    public void doIndex(StaplerResponse rsp) throws IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        rsp.setContentType("text/plain");
        PrintWriter w = rsp.getWriter();

        ExtensionList<Persona> all = Persona.all();

        for (XmlBasedPersona p : Util.filter(all, XmlBasedPersona.class)) {
            try {
                p.reload();
                w.println("Reloaded " + p.xml);
            } catch (DocumentException de) {
                w.println("Error on reloading " + p.xml);
            }
        }

        // find new personas
        for (ExtensionComponent<Persona> c : new XmlPersonaFinder().find(Persona.class, Hudson.getInstance())) {
            XmlBasedPersona p = (XmlBasedPersona) c.getInstance();
            if (Persona.byId(p.id) == null) {
                all.add(all.size(), p);
            }
        }
    }
}
