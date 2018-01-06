package tc.oc.pgm.experience;

import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.pgm.match.inject.MatchBinders;
import tc.oc.pgm.match.inject.MatchScoped;

public class ExperienceManifest extends HybridManifest implements MatchBinders {
    @Override
    protected void configure() {
        bind(ExperienceListener.class).in(MatchScoped.class);
        matchListener(ExperienceListener.class);
    }
}