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
package hudson.plugins.persona.random;

import hudson.ExtensionList;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import hudson.plugins.persona.simple.Image;
import hudson.plugins.persona.simple.SimplePersona;
import hudson.plugins.persona.xml.XmlBasedPersona;

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
        XmlBasedPersona persona = getCurrentPersona();
        return persona != null ? persona.getDefaultImage() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(AbstractBuild<?, ?> build) {
        if (build == null) {
            return getDefaultImage();
        }
        XmlBasedPersona persona = getCurrentPersona();
        return persona != null ? persona.getImage(build) : null;
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
        XmlBasedPersona persona = getCurrentPersona();
        return persona == null ? null : persona.getDisplayName() + " - " + persona.getRandomQuoteText(build);
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
        if (allXmlBased().isEmpty()) {
            return null;
        }
        return personas.get(random.nextInt(personas.size()));
    }

    /**
     * Returns all the registered {@link XmlBasedPersona}s.
     */
    public static ExtensionList<XmlBasedPersona> allXmlBased() {
        return Hudson.getInstance().getExtensionList(XmlBasedPersona.class);
    }
}
