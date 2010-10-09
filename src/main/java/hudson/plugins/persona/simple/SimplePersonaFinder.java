package hudson.plugins.persona.simple;

import hudson.Extension;
import hudson.ExtensionFinder;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import hudson.model.Result;
import hudson.plugins.persona.Persona;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instantiates simple persona by looking at a known location in plugins.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class SimplePersonaFinder extends ExtensionFinder {
    private static final String[] EXTENSIONS = {".jpg",".jpeg",".png",".gif",
                                                ".JPG",".JPEG",".PNG",".GIF"};


    @Override
    public <T> Collection<T> findExtensions(Class<T> type, Hudson hudson) {
        if (type!=Persona.class)    return Collections.emptyList();

        List<SimplePersona> r = new ArrayList<SimplePersona>();

        try {
            FilePath baseDir = new FilePath(hudson.getRootDir());
            for (FilePath xml : baseDir.list("persona/**/*.xml")) {
                try {
                    URL url = xml.toURI().toURL();
                    r.add(parsePersona(url,
                            xml.getParent().toURI().toURL(),
                            xml.getParent().getRemote().substring(baseDir.getRemote().length()+1)));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Faied to load a persona from "+xml,e);
                } catch (DocumentException e) {
                    LOGGER.log(Level.SEVERE, "Faied to load a persona from "+xml,e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Faied to load personas",e);
        } catch (InterruptedException e) {
            // all local processing. can't happen
            throw new Error(e);
        }

        return (List)r;
    }

    /**
     * Finds an image in ${imageBase}/${baseName}.* for some known extension
     */
    private String findImage(URL imageBase, String imageBasePath, String baseName) throws IOException {
        for (String ext : EXTENSIONS) {
            try {
                new URL(imageBase,baseName+ext).openStream().close();
                // found it.
                return imageBasePath+baseName+ext;
            } catch (IOException e) {
                // not found. try next
            }
        }
        throw new IOException("No image found that matches "+imageBase+"/"+baseName+".*");
    }



    private SimplePersona parsePersona(URL xml, final URL imageBase, final String imageBasePath) throws DocumentException, IOException {
        Document d = new SAXReader().read(xml);
        Element r = d.getRootElement();

        List<String> quotes = new ArrayList<String>();
        for (Element e : (List<Element>)r.elements("quote")) {
            quotes.add(e.getTextTrim());
        }
        final String displayName = r.attributeValue("displayName");

        final String icon = findImage(imageBase, imageBasePath, "icon");
        final String success = findImage(imageBase, imageBasePath, "success");
        final String failure = findImage(imageBase, imageBasePath, "failure");
        final String other = findImage(imageBase, imageBasePath, "other");

        return new SimplePersona(r.attributeValue("id"),quotes) {
            @Override
            public Image getImage(AbstractBuild<?,?> build) {
                Result r = build.getResult();
                if (r== Result.SUCCESS)
                    return new Image(icon,success);
                if (r== Result.FAILURE)
                    return new Image(icon,failure);
                return new Image(icon,other);
            }

            public String getDisplayName() {
                return displayName;
            }
        };
    }

    private static final Logger LOGGER = Logger.getLogger(SimplePersonaFinder.class.getName());
}
