/*
 * The MIT License
 *
 * Copyright (c) 2010-2013, InfraDNA, Inc., Seiji Sogabe
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
import jenkins.ExtensionComponentSet;
import jenkins.ExtensionRefreshException;
import jenkins.model.Jenkins;

/**
 * Instantiates {@link XmlBasedPersona}s }by looking at a known location in
 * plugins.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class XmlPersonaFinder extends ExtensionFinder {

    private List<PluginWrapper> parsedWrappers;

    @Override
    public <T> Collection<ExtensionComponent<T>> find(Class<T> type, Hudson hudson) {
        if ((type != Persona.class) && (type != XmlBasedPersona.class)) {
            return Collections.EMPTY_LIST;
        }

        Collection<ExtensionComponent<T>> r = new ArrayList<ExtensionComponent<T>>();

        // locate persona from $JENKINS_HOME
        loadFromJenkinsHomeTo(hudson, r);

        if (parsedWrappers == null) {
            // locate personas from plugins
            parsedWrappers = hudson.getPluginManager().getPlugins();
            r.addAll(new ExtensionComponentSetImpl(parsedWrappers).find(type));
        }

        if (r.isEmpty()) {
            LOGGER.warning("[Persona] No Persona found.");
        }

        return (List) r;
    }

    @Override
    public ExtensionComponentSet refresh() throws ExtensionRefreshException {
        List<PluginWrapper> newWrappers = Jenkins.getInstance().getPluginManager().getPlugins();
        List<PluginWrapper> delta = new ArrayList<PluginWrapper>(newWrappers);
        delta.removeAll(parsedWrappers);
        parsedWrappers = newWrappers;
        return new ExtensionComponentSetImpl(delta);
    }

    private <T> void loadFromJenkinsHomeTo(Hudson hudson, Collection<ExtensionComponent<T>> r) {

        try {
            FilePath baseDir = new FilePath(hudson.getRootDir());
            for (FilePath xml : baseDir.list("persona/**/*.xml")) {
                URL url = xml.toURI().toURL();
                parsePersonaInto(url,
                        xml.getParent().toURI().toURL(),
                        xml.getParent().getRemote().substring(baseDir.getRemote().length() + 1).replace('\\', '/'), r);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed to load personas", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load personas", e);
        } catch (InterruptedException e) {
            // all local processing. can't happen
            throw new Error(e);
        }
    }

    private static <T> void parsePersonaInto(URL xml, URL imageBase, String imageBasePath,
            Collection<ExtensionComponent<T>> result) {
        try {
            result.add(new ExtensionComponent(XmlBasedPersona.create(xml, imageBase, imageBasePath)));
        } catch (DocumentException e) {
            LOGGER.log(Level.SEVERE, "Failed to load a persona from " + xml, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load a persona from " + xml, e);
        }
    }

    private static class ExtensionComponentSetImpl extends ExtensionComponentSet {

        private List<PluginWrapper> wrappers;

        public ExtensionComponentSetImpl(List<PluginWrapper> wrappers) {
            this.wrappers = wrappers;
        }

        @Override
        public <T> Collection<ExtensionComponent<T>> find(Class<T> type) {
            Collection<ExtensionComponent<T>> r = new ArrayList<ExtensionComponent<T>>();
            if (type != Persona.class && type != XmlBasedPersona.class) {
                return r;
            }

            for (PluginWrapper pw : wrappers) {
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
                } catch (RuntimeException e) {
                    continue;   // FilePath#list throws BuildException
                } catch (IOException e) {
                    continue;   // no such file
                } catch (InterruptedException e) {
                    // all local processing. can't happen
                    throw new Error(e);
                }
            }

            return r;
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(XmlPersonaFinder.class.getName());
}
