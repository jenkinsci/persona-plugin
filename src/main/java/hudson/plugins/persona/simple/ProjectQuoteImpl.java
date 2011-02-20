package hudson.plugins.persona.simple;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

/**
 * @author Kohsuke Kawaguchi
 */
public class ProjectQuoteImpl extends AbstractQuoteImpl {

    public final AbstractProject<?, ?> project;

    public ProjectQuoteImpl(SimplePersona persona, AbstractProject<?, ?> project) {
        super(persona);
        this.project = project;
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

    private QuoteImpl getLastQuote() {
        AbstractBuild<?, ?> b = project.getLastBuild();
        if (b == null) {
            return null;
        }
        return b.getAction(QuoteImpl.class);
    }
}
