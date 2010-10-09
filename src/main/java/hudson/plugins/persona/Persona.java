/*
 * The MIT License
 *
 * Copyright (c) 2010, InfraDNA, Inc.
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

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.ModelObject;
import hudson.plugins.persona.simple.DefaultQuoteImpl;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class Persona implements ExtensionPoint, ModelObject {
    public final String id;

    protected Persona(String id) {
        this.id = id;
    }

    /**
     * Generates a random quote for the given build.
     *
     * @see DefaultQuoteImpl
     */
    public abstract Action generateQuote(AbstractBuild<?,?> build);

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
}
