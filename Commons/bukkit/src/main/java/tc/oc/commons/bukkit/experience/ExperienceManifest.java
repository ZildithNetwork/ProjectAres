package tc.oc.commons.bukkit.experience;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.permissions.PermissionBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.plugin.PluginFacetBinder;

public class ExperienceManifest extends HybridManifest {
    @Override
    protected void configure() {
        requestStaticInjection(ExperienceUtil.class);

        new PluginFacetBinder(binder())
            .register(ExperienceCommands.class);

        final PermissionBinder permissions = new PermissionBinder(binder());
        for(int i = ExperienceConstants.MULTIPLIER_MAX; i > 0; i = i - ExperienceConstants.MULTIPLIER_INCREMENT) {
            permissions.bindPermission().toInstance(new Permission("experience.multiplier." + i, PermissionDefault.FALSE));
        }
    }
}
