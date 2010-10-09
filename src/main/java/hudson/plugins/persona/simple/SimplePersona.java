package hudson.plugins.persona.simple;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.plugins.persona.Persona;

import java.util.List;
import java.util.Random;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class SimplePersona extends Persona {
    private final List<String> quotes;
    private final Random random = new Random();

    /**
     * @param id
     *      Unique identifier of this persona.
     * @param quotes
     *      Collection of quotes.
     */
    protected SimplePersona(String id, List<String> quotes) {
        super(id);
        this.quotes = quotes;
    }

    /**
     * Determines the icon and the background to render.
     */
    public abstract Image getImage(AbstractBuild<?, ?> build);

    @Override
    public synchronized Action generateQuote(AbstractBuild<?, ?> build) {
        return new DefaultQuoteImpl(build,this,quotes.get(random.nextInt(quotes.size())));
    }

    public static class ConverterImpl extends Persona.ConverterImpl {}
}
