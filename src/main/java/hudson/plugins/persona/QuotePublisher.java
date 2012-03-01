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
package hudson.plugins.persona;

import hudson.plugins.persona.selector.LocationSelector;
import hudson.plugins.persona.random.RandomPersona;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.plugins.persona.selector.BottomLeftSelector;
import hudson.plugins.persona.selector.LocationSelectorDescriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Kohsuke Kawaguchi
 */
public class QuotePublisher extends Notifier {

    public final Persona persona;

    private final LocationSelector selector;
    
    @DataBoundConstructor
    public QuotePublisher(String personaId, LocationSelector selector) {
        this.persona = Persona.byId(personaId);
        this.selector = selector;
    }

    public LocationSelector getLocationSelector() {
        return selector != null? selector : new BottomLeftSelector();
    }
    
    public String getPersonaId() {
        return persona != null ? persona.id : null;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return persona != null ? persona.generateProjectQuote(project, selector) : null;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if (persona instanceof RandomPersona) {
            ((RandomPersona) persona).resetCurrentPersona();
        }
        if (persona != null) {
            build.getActions().add(persona.generateQuote(build, selector));
        }
        
        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Associate Persona";
        }

        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject json) throws FormException {
            String personaId = json.getString("personaId");
            LocationSelector h = req.bindJSON(LocationSelector.class, json.optJSONObject("locationSelector"));
            return new QuotePublisher(personaId, h);
        }
        
        public LocationSelectorDescriptor getDefaultLocationSelector() {
            return Hudson.getInstance().getDescriptorByType(BottomLeftSelector.DescriptorImpl.class);
        }
        
        public ListBoxModel doFillPersonaIdItems() {
            ListBoxModel r = new ListBoxModel();
            for (Persona p : Persona.all()) {
                r.add(p.getDisplayName(), p.id);
            }
            return r;
        }
    }
}
