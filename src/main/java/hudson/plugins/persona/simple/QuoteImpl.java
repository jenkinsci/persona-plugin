package hudson.plugins.persona.simple;

import hudson.model.AbstractBuild;

/**
 * @author Kohsuke Kawaguchi
 */
public class QuoteImpl extends AbstractQuoteImpl {
    public final AbstractBuild<?,?> build;
    public final String quote;

    public QuoteImpl(AbstractBuild<?,?> build, SimplePersona persona, String quote) {
        super(persona);
        this.build = build;
        this.quote = quote;
    }

    @Override
    public String getQuote() {
        return quote;
    }

    public Image getImage() {
        return persona!=null ? persona.getImage(build) : null;
    }
}
