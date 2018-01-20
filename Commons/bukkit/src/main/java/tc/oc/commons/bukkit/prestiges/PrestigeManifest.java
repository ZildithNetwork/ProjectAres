package tc.oc.commons.bukkit.prestiges;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.permissions.PermissionBinder;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.plugin.PluginFacetBinder;

public class PrestigeManifest extends HybridManifest {
    @Override
    protected void configure()
    {
       // requestStaticInjection(PrestigeUtil.class);

        new PluginFacetBinder(binder())
                .register(PrestigeCommands.class);

        //final PermissionsBinder permissions = new PermissionBinder(binder());
    }
}