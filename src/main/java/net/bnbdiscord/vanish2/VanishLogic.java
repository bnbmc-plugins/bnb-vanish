package net.bnbdiscord.vanish2;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.bnbdiscord.vanish2.Vanish2;

import java.util.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.*;
import static org.graalvm.compiler.debug.DebugOptions.Time;

public class VanishLogic implements CommandExecutor, Listener {

    Vanish2 plugin = Vanish2.getPlugin(Vanish2.class);

    public static ArrayList<Player> vanished = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        Player p = (Player) sender;
        EntityPlayer ep = ((CraftPlayer)p).getHandle();

        if(p.hasPermission("bnb.vanish")){
            if(vanished.contains(p)){
                //unvanish
                p.sendMessage(GREEN + "" + BOLD + "UNVANISHED! " + GREEN + "You are now visible to other players.");

                // Remove from set
                vanished.remove(p);

                // Create the packet to show the player in the tablist
                PacketPlayOutPlayerInfo showPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);

                // Make everyone see the player
                for(Player online : Bukkit.getOnlinePlayers()){
                    PlayerConnection connection = ((CraftPlayer) online).getHandle().playerConnection;
                    connection.sendPacket(showPacket);
                    online.showPlayer(plugin, p);
                }



            } else {
                //vanish
                p.sendMessage(GREEN + "" + BOLD + "VANISHED! " + GREEN + "You are now invisible to other players.");

                // Add to set
                vanished.add(p);

                // Create the packet to hide the player in the tablist
                PacketPlayOutPlayerInfo hidePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);

                // Make everyone not see the player
                for(Player online : Bukkit.getOnlinePlayers()){
                    PlayerConnection connection = ((CraftPlayer) online).getHandle().playerConnection;
                    connection.sendPacket(hidePacket);
                    online.hidePlayer(plugin, p);
                }

                sendActionbar(ep, p);

            }
        } else {
            p.sendMessage(RED + "" + BOLD + "NO PERMISSION!" + RED + " You do not have permission to vanish!");
        }


        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){

        System.out.println(vanished);
        for (Player user : vanished){
            System.out.println(user);

            EntityPlayer ep = ((CraftPlayer)user).getHandle();

            PacketPlayOutPlayerInfo hidePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
            PlayerConnection connection = ((CraftPlayer) user).getHandle().playerConnection;
            connection.sendPacket(hidePacket);
            e.getPlayer().hidePlayer(plugin, user);
        }
    }

    public void sendActionbar(EntityPlayer ep, Player p){
        new BukkitRunnable() {
            public void run() {
                if(vanished.contains(p)){
                    PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR,
                            IChatBaseComponent.ChatSerializer.a("{\"text\":\"You are currently §cVANISHED\"}"));
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
