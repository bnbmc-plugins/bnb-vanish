package net.bnbdiscord.vanish2;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Vanish2 extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("vanish").setExecutor(new VanishLogic());
        getServer().getPluginManager().registerEvents(new VanishLogic(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}
