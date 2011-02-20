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
package hudson.plugins.persona.simple;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.plugins.persona.Persona;
import hudson.plugins.persona.Quote;

import java.util.List;
import java.util.Random;

/**
 * Partial implementation of {@link Persona} that renders the plain text quote with an icon.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class SimplePersona extends Persona {

    private volatile List<String> quotes;

    private volatile List<String> quotesSuccess;

    private volatile List<String> quotesFailure;

    private volatile List<String> quotesOther;

    private final Random random = new Random();

    /**
     * @param id
     *      Unique identifier of this persona.
     * @param quotes
     *      Collection of quotes.
     * @param quotesSuccess
     * 		Collection of success quotes.
     * @param quotesFailure
     * 		Collection of failure quotes.
     * @param quotesOther
     * 		Collection of other quotes.
     */
    protected SimplePersona(String id, List<String> quotes,
            List<String> quotesSuccess, List<String> quotesFailure,
            List<String> quotesOther) {
        super(id);
        this.quotes = quotes;
        this.quotesSuccess = quotesSuccess;
        this.quotesFailure = quotesFailure;
        this.quotesOther = quotesOther;
    }

    protected void setQuotes(List<String> quotes,
            List<String> quotesSuccess, List<String> quotesFailure,
            List<String> quotesOther) {
        this.quotes = quotes;
        this.quotesSuccess = quotesSuccess;
        this.quotesFailure = quotesFailure;
        this.quotesOther = quotesOther;
    }

    /**
     * Determines the icon and the background to render.
     */
    public abstract Image getImage(AbstractBuild<?, ?> build);

    public abstract Image getDefaultImage();

    @Override
    public Quote generateQuote(AbstractBuild<?, ?> build) {
        return new QuoteImpl(build, this, getRandomQuoteText(build));
    }

    public synchronized String getRandomQuoteText(AbstractBuild<?, ?> build) {
        String choosenQuote = null;
        Result r = build.getResult();
        if ((r == Result.SUCCESS) && !quotesSuccess.isEmpty()) {
            choosenQuote = quotesSuccess.get(random.nextInt(quotesSuccess.size()));
        } else if ((r == Result.FAILURE) && !quotesFailure.isEmpty()) {
            choosenQuote = quotesFailure.get(random.nextInt(quotesFailure.size()));
        } else if (!quotesOther.isEmpty()) {
            choosenQuote = quotesOther.get(random.nextInt(quotesOther.size()));
        }

        if (null == choosenQuote) {
            choosenQuote = quotes.get(random.nextInt(quotes.size()));
        }

        return choosenQuote;
    }

    @Override
    public Quote generateProjectQuote(AbstractProject<?, ?> project) {
        return new ProjectQuoteImpl(this, project);
    }
}
