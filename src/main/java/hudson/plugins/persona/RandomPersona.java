/*
 * The MIT License
 *
 * Copyright (c) 2010-2011, InfraDNA, Inc.
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

import hudson.ExtensionList;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import hudson.plugins.persona.simple.Image;
import hudson.plugins.persona.simple.SimplePersona;
import hudson.plugins.persona.xml.XmlBasedPersona;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Random persona implementation on top of SimplePersona
 *
 * @author whren
 *
 */
public class RandomPersona extends SimplePersona {

    /**
     * Random persona display name
     */
    public static final String RANDOM_PERSONA_DISPLAYNAME = "Random Persona";

    /**
     * Random persona id
     */
    public static final String RANDOM_PERSONA_ID = "RandomPersonaId";

    /**
     * Persona Randomizer
     */
    private static final Random random = new Random();

    private Map<AbstractBuild<?, ?>, XmlBasedPersona> mapPersonas =
            new HashMap<AbstractBuild<?, ?>, XmlBasedPersona>();

    private XmlBasedPersona currentPersona;

    /**
     * Creates a RandomPersona
     *
     * @return A newly created RandomPersona
     */
    public static RandomPersona create() {
        return new RandomPersona();
    }

    /**
     * Default constructor
     */
    private RandomPersona() {
        super(RANDOM_PERSONA_ID, null, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getDefaultImage() {
        return getCurrentPersona().getDefaultImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(AbstractBuild<?, ?> build) {
        return getCurrentPersona().getImage(build);
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return RANDOM_PERSONA_DISPLAYNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getRandomQuoteText(AbstractBuild<?, ?> build) {
        return getCurrentPersona().getDisplayName()
                + " - "
                + getCurrentPersona().getRandomQuoteText(build);
    }

    /**
     * Returns the build associated persona
     *
     * @param build the build from wich to retrieve the associated persona
     * @return persona associated to the build
     */
    public XmlBasedPersona getPersona(AbstractBuild<?, ?> build) {
        return mapPersonas.get(build);
    }

    /**
     * Return the current persona, generate one if non existent
     *
     * @return The current persona
     */
    public XmlBasedPersona getCurrentPersona() {
        if (null == currentPersona) {
            currentPersona = resetCurrentPersona();
        }

        return currentPersona;
    }

    /**
     * Reset the current persona and return a new one
     *
     * @return The new current persona
     */
    public XmlBasedPersona resetCurrentPersona() {
        currentPersona = randomPersona();
        return currentPersona;
    }

    /**
     * Returns a random persona form all personas
     *
     * @return A random persona
     */
    public static XmlBasedPersona randomPersona() {
        ExtensionList<XmlBasedPersona> personas = allXmlBased();
        return personas.get(random.nextInt(personas.size()));
    }

    /**
     * Returns all the registered {@link XmlBasedPersona}s.
     */
    public static ExtensionList<XmlBasedPersona> allXmlBased() {
        return Hudson.getInstance().getExtensionList(XmlBasedPersona.class);
    }
}
