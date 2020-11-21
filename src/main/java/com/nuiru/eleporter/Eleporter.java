package com.nuiru.eleporter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Eleporter extends JavaPlugin implements Listener {

    FileConfiguration config;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        config = getConfig();
        //error detection
        for(String s : config.getStringList("targetblock")) {
            try {
                Material.valueOf(s);
            } catch(Exception e) {
                getLogger().log(Level.WARNING, "failed to load config: " + e);
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }
        getLogger().info("Eleporter is enabled.");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("Eleporter is disabled.");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        //下のブロック検知
        if(p.getVelocity().getY() > 0 ) {
            Location l = p.getLocation();
            l.setY(l.getY() - 1);
            Block b = l.getWorld().getBlockAt(l);
            Material belowMaterial = b.getType();
            //下のブロックがコンフィグに存在するものか検知
            for(String s : config.getStringList("targetblock")) {
                //存在したとき
                if(Material.valueOf(s) == belowMaterial) {
                    Location detectRange = p.getLocation();
                    boolean upperBlock = false;
                    //上にテレポート可能なブロックが存在するか
                    for(int i = 0; i < config.getInt("range", 16); i++) {
                        Material m = l.getWorld().getBlockAt(detectRange).getType();
                        for(String ss : config.getStringList("targetblock")) {
                            //存在したとき
                            if(Material.valueOf(ss) == m) {
                                //テレポート先が埋まらないか確認
                                boolean teleportable = true;
                                Location upperBlockChkLoc = detectRange;
                                for(int j = 0; j < 2; j++) {
                                    upperBlockChkLoc.setY(upperBlockChkLoc.getY() + 1);
                                    Block bb = l.getWorld().getBlockAt(upperBlockChkLoc);
                                    if(!bb.isEmpty()) {
                                        teleportable = false;
                                    }
                                }
                                //テレポート
                                if(teleportable) {
                                    detectRange.setY(detectRange.getY() + 1);
                                    p.teleport(detectRange);
                                    String sound = config.getString("upSound", "entity.puffer_fish.blow_up");
                                    p.playSound(p.getLocation(), sound, 1, 1);
                                    upperBlock = true;
                                }
                            }
                        }
                        if(upperBlock) break;
                        detectRange.setY(detectRange.getY() + 1);
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if(e.isSneaking()) {
            Location l = p.getLocation();
            l.setY(l.getY() - 1);
            Block b = l.getWorld().getBlockAt(l);
            Material belowMaterial = b.getType();
            for(String s : config.getStringList("targetblock")) {
                if(Material.valueOf(s) == belowMaterial) {
                    Location detectRange = p.getLocation();
                    detectRange.setY(detectRange.getY() - 2);
                    boolean upperBlock = false;
                    for(int i = 0; i < config.getInt("range", 16); i++) {
                        Material m = l.getWorld().getBlockAt(detectRange).getType();
                        for(String ss : config.getStringList("targetblock")) {
                            if(Material.valueOf(ss) == m) {
                                //テレポート先が埋まらないか確認
                                boolean teleportable = true;
                                Location upperBlockChkLoc = detectRange;
                                for(int j = 0; j < 2; j++) {
                                    upperBlockChkLoc.setY(upperBlockChkLoc.getY() + 1);
                                    Block bb = l.getWorld().getBlockAt(upperBlockChkLoc);
                                    if(!bb.isEmpty()) {
                                        teleportable = false;
                                    }
                                }
                                //テレポート
                                if(teleportable) {
                                    detectRange.setY(detectRange.getY() + 1);
                                    p.teleport(detectRange);
                                    String sound = config.getString("downSound", "entity.puffer_fish.blow_out");
                                    p.playSound(p.getLocation(), sound, 1, 1);
                                    upperBlock = true;
                                }
                            }
                        }
                        if(upperBlock) break;
                        detectRange.setY(detectRange.getY() - 1);
                    }
                    break;
                }
            }
        }
    }
}
