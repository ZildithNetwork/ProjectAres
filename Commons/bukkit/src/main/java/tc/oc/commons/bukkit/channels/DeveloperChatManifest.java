package tc.oc.commons.bukkit.channels;

import tc.oc.commons.bukkit.settings.SettingBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.plugin.PluginFacetBinder;

public class DeveloperChatManifest extends HybridManifest {
    @Override
    protected void configure() {
        new SettingBinder(publicBinder())
            .addBinding().toInstance(DeveloperChannel.SETTING);

        new PluginFacetBinder(binder())
            .register(DeveloperChannel.class);

        expose(DeveloperChannel.class);
    }
}
