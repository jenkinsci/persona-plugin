package hudson.plugins.persona.simple;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

/**
 * @author Kohsuke Kawaguchi
 */
public class ProjectQuoteImpl extends AbstractQuoteImpl {
    public final AbstractProject<?,?> project;

    public ProjectQuoteImpl(SimplePersona persona, AbstractProject<?, ?> project) {
        super(persona);
        this.project = project;
    }

    @Override
    public String getQuote() {
        QuoteImpl q = getLastQuote();
        return q!=null ? q.getQuote() : persona.getRandomQuoteText();
    }

    @Override
    public Image getImage() {
        QuoteImpl q = getLastQuote();
        return q!=null ? q.getImage() : persona.getDefaultImage();
    }

    private QuoteImpl getLastQuote() {
        AbstractBuild<?, ?> b = project.getLastBuild();
        if (b==null)    return null;
        return b.getAction(QuoteImpl.class);
    }
}
