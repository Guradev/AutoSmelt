package net.gura.autoSmelt.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AutoSmeltListener implements Listener {

    private final static Map<Material, Material> SMELT_MAP = new HashMap<>();

    static {
        SMELT_MAP.put(Material.IRON_ORE, Material.IRON_INGOT);
        SMELT_MAP.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        SMELT_MAP.put(Material.DIAMOND_ORE, Material.DIAMOND);
        SMELT_MAP.put(Material.EMERALD_ORE, Material.EMERALD);
        SMELT_MAP.put(Material.REDSTONE_ORE, Material.REDSTONE);
    }

    private static final Set<Material> FORTUNE_COMPATIBLE = EnumSet.of(
            Material.COAL_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.REDSTONE_ORE,
            Material.GLOWING_REDSTONE_ORE,
            Material.QUARTZ_ORE
    );

    private int random(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    private int getXpBlock(Material type) {
        switch (type) {
            case COAL_ORE: return random(0, 2);
            case DIAMOND_ORE:
            case EMERALD_ORE: return random(3, 7);
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE: return random(1, 5);
            case QUARTZ_ORE: return random(2, 5);
            case IRON_ORE:
            case GOLD_ORE: return random(1, 3);
            default: return 0;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();
        ItemStack item = player.getItemInHand();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (item != null && item.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        if (!SMELT_MAP.containsKey(type)) return;

        event.setCancelled(true);
        block.setType(Material.AIR);

        Material dropType = SMELT_MAP.get(type);
        int dropAmount = 1;

        if (FORTUNE_COMPATIBLE.contains(type)) {
            int fortuneLevel = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            if (fortuneLevel > 0) {
                int bonus = new Random().nextInt(fortuneLevel + 2) - 1;
                dropAmount += Math.max(bonus, 0);
            }
        }

        ItemStack drop = new ItemStack(dropType, dropAmount);
        block.getWorld().dropItemNaturally(block.getLocation(), drop);

        int xp = getXpBlock(type);
        block.getWorld().spawn(block.getLocation(), ExperienceOrb.class).setExperience(xp);

    }
}
