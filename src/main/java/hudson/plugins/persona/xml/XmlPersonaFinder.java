/*
 * The MIT License
 *
 * Copyright (c) 2010-2011, InfraDNA, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.persona.xml;

import hudson.Extension;
import hudson.ExtensionComponent;
import hudson.ExtensionFinder;
import hudson.FilePath;
import hudson.PluginWrapper;
import hudson.model.Hudson;
import hudson.plugins.persona.Persona;
import java.io.File;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instantiates {@link XmlBasedPersona}s }by looking at a known location in plugins.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class XmlPersonaFinder extends ExtensionFinder {
    @Override
    public <T> Collection<ExtensionComponent<T>> find(Class<T> type, Hudson hudson) {
        if (type!=Persona.class)    return Collections.emptyList();

        List<ExtensionComponent<XmlBasedPersona>> r = new ArrayList<ExtensionComponent<XmlBasedPersona>>();

        // locate personas from $HUDSON_HOME or $JENKINS_HOME
        try {
            FilePath baseDir = new FilePath(hudson.getRootDir());
            for (FilePath xml : baseDir.list("persona/**/*.xml")) {
                URL url = xml.toURI().toURL();
                parsePersonaInto(url,
                        xml.getParent().toURI().toURL(),
                        xml.getParent().getRemote().substring(baseDir.getRemote().length()+1).replace('\\','/'),r);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load personas",e);
        } catch (InterruptedException e) {
            // all local processing. can't happen
            throw new Error(e);
        }

        // locate personas from plugins
        for (PluginWrapper pw : hudson.getPluginManager().getPlugins()) {
            try {
                FilePath baseDir = new FilePath(new File(pw.baseResourceURL.getFile()));
                // support persona-1.0,1,1 style
                FilePath personaXML = baseDir.child("persona.xml");
                if (personaXML.exists()) {
                    LOGGER.log(Level.INFO, "loading old style persona from {0}.hpi", pw.getShortName());
                    URL url = personaXML.toURI().toURL();
                    parsePersonaInto(url, pw.baseResourceURL, "plugin/" + pw.getShortName(), r);
                    continue;
                }
                // support persona-1.2 or newer style
                for (FilePath xml : baseDir.list("**/persona.xml")) {
                    LOGGER.log(Level.INFO, "loading 1.2 or newer style persona from {0}.hpi", pw.getShortName());
                    URL url = xml.toURI().toURL();
                    parsePersonaInto(url,
                            xml.getParent().toURI().toURL(),
                            "plugin/" + pw.getShortName() + "/" + xml.getParent().getRemote().substring(baseDir.getRemote().length() + 1), r);
                }
            } catch (IOException e) {
                continue;   // no such file
            } catch (InterruptedException e) {
                // all local processing. can't happen
                throw new Error(e);
            }
        }

        return (List)r;
    }

    private void parsePersonaInto(URL xml, URL imageBase, String imageBasePath, Collection<ExtensionComponent<XmlBasedPersona>> result) {
        try {
            result.add(new ExtensionComponent<XmlBasedPersona>(XmlBasedPersona.create(xml,imageBase,imageBasePath)));
        } catch (DocumentException e) {
            LOGGER.log(Level.SEVERE, "Failed to load a persona from "+xml,e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load a persona from "+xml,e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(XmlPersonaFinder.class.getName());

}
