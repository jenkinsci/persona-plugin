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
package hudson.plugins.persona.simple;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.persona.selector.LocationSelector;

/**
 * @author Kohsuke Kawaguchi
 */
public class ProjectQuoteImpl extends AbstractQuoteImpl {

    public final AbstractProject<?, ?> project;
    
    public final LocationSelector locationSelector;

    public ProjectQuoteImpl(SimplePersona persona, AbstractProject<?, ?> project, LocationSelector selector) {
        super(persona);
        this.project = project;
        this.locationSelector = selector;
    }

    @Override
    public String getQuote() {
        QuoteImpl q = getLastQuote();

        if (q != null) {
            return q.getQuote();
        }

        AbstractBuild<?, ?> b = project.getLastBuild();

        return persona.getRandomQuoteText(b);
    }

    @Override
    public Image getImage() {
        QuoteImpl q = getLastQuote();
        return q != null ? q.getImage() : persona.getDefaultImage();
    }

    @Override
    public LocationSelector getLocationSelector() {
        return locationSelector;
    }
    
    private QuoteImpl getLastQuote() {
        AbstractBuild<?, ?> b = project.getLastBuild();
        if (b == null) {
            return null;
        }
        return b.getAction(QuoteImpl.class);
    }
}
