/*
 * The MIT License
 *
 * Copyright (c) 2010-12, InfraDNA, Inc., Seiji Sogabe
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
import hudson.plugins.persona.selector.LocationSelector;

/**
 * @author Kohsuke Kawaguchi
 */
public class QuoteImpl extends AbstractQuoteImpl {
    public final AbstractBuild<?,?> build;
    public final String quote;
    public final LocationSelector locationSelector;

    public QuoteImpl(AbstractBuild<?,?> build, SimplePersona persona, String quote, LocationSelector selector) {
        super(persona);
        this.build = build;
        this.quote = quote;
        this.locationSelector = selector;
    }

    @Override
    public String getQuote() {
        return quote;
    }

    @Override
    public Image getImage() {
        return persona!=null ? persona.getImage(build) : null;
    }

    @Override
    public LocationSelector getLocationSelector() {
        return locationSelector;
    }
    
}
