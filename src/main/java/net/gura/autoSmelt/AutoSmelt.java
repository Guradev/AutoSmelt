package net.gura.autoSmelt;

import net.gura.autoSmelt.listener.AutoSmeltListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoSmelt extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new AutoSmeltListener(), this);
        getServer().getConsoleSender().sendMessage("[AutoSmelt] Plugin enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("[AutoSmelt] Plugin disabled");
    }
}
