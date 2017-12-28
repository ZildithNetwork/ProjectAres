package tc.oc.commons.bukkit.channels;

import tc.oc.commons.bukkit.settings.SettingBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.plugin.PluginFacetBinder;

public class ModChatManifest extends HybridManifest {
    @Override
    protected void configure() {
        new SettingBinder(publicBinder())
            .addBinding().toInstance(ModChannel.SETTING);

        new PluginFacetBinder(binder())
            .register(ModChannel.class);

        expose(ModChannel.class);
    }
}
