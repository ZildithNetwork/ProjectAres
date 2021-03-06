package tc.oc.commons.core.format;

import javax.inject.Inject;

import net.md_5.bungee.api.chat.BaseComponent;
import tc.oc.commons.core.chat.Component;

import static net.md_5.bungee.api.ChatColor.*;

public class GeneralFormatter {

    @Inject private GeneralFormatter() {}

    private final Component brandName = new Component("Zildith Network", DARK_PURPLE);

    public BaseComponent brandName() {
        return brandName;
    }

    public String publicHostname() {
        return "zildith.net";
    }
}
