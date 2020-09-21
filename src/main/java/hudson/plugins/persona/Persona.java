/*
 * The MIT License
 *
 * Copyright (c) 2010-2012, InfraDNA, Inc., Seiji Sogabe
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
package hudson.plugins.persona;

import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Items;
import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.plugins.persona.selector.LocationSelector;
import hudson.plugins.persona.simple.AbstractQuoteImpl;

/**
 * A person and his/her set of quotes.
 * <p>
 * Normally a persona is someone famous, funny, or both (such as Chuck Norris.)
 *
 * @author Kohsuke Kawaguchi
 */
@XStreamAliasType("persona")
public abstract class Persona implements ExtensionPoint, ModelObject {
    /**
     * Uniquely identifies this persona among other personas.
     */
    public final String id;

    protected Persona(String id) {
        this.id = id;
    }

    protected Persona() {
        id = getClass().getName();
    }

    /**
     * Generates a random quote for the given build.
     *
     * @see AbstractQuoteImpl
     */
    public abstract Quote generateQuote(AbstractBuild<?,?> build, LocationSelector selector);

    /**
     * Generates a random quote for the project top page.
     *
     * @see AbstractQuoteImpl
     */
    public abstract Quote generateProjectQuote(AbstractProject<?,?> project, LocationSelector selector);

    /**
     * Returns all the registered {@link Persona}s.
     */
    public static ExtensionList<Persona> all() {
        return Hudson.getInstance().getExtensionList(Persona.class);
    }

    public static Persona byId(String id) {
        for (Persona p : all()) {
            if (p.id.equals(id))
                return p;
        }
        return null;
    }

    public static class ConverterImpl extends AbstractSingleValueConverter {
        @Override
        public Persona fromString(String id) {
            return byId(id);
        }

        @Override
        public String toString(Object obj) {
            return ((Persona)obj).id;
        }

        @Override
        public boolean canConvert(Class type) {
            return Persona.class.isAssignableFrom(type);
        }
    }

    static {
        Items.XSTREAM.registerConverter(new Persona.ConverterImpl(),10);
        Run.XSTREAM.registerConverter(new Persona.ConverterImpl(),10);
    }
}
