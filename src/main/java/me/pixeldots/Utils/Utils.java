package me.pixeldots.Utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.pixeldots.BedwarsRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextColor;

public class Utils {

    public static Component text(String text) {
        return Component.text(text);
    }

    public static Component text(String text, TextColor color) {
        return Component.text(text, color);
    }

    public static String text(Component component) {
        if (component == null || component instanceof TranslatableComponent) return "";
        String text = ((TextComponent)component).content();
        
        List<Component> children = component.children();
        for (int i = 0; i < children.size(); i++) {
            text += ((TextComponent)children.get(i)).content();
        }
        
        return text;
    }

    public static String text(ItemStack stack) {
        Component text = stack.getItemMeta().displayName();
        if (text == null) return "";
        return ((TextComponent)text).content();
    }

    public static String getTeamColor(int id) {
        return BedwarsRunner.Variables.Teams.get(id);
    }

    public static ChatColor getChatColor(String color) {
        if (color.equals("orange")) return ChatColor.GOLD;
        else if (color.equals("pink")) return ChatColor.LIGHT_PURPLE;
        return ChatColor.valueOf(color.toUpperCase());
    }

    public static TextColor getTextColor(String color) {
        Color clr = getColorFromName(color.toLowerCase());
        return TextColor.color(clr.getRed(), clr.getGreen(), clr.getBlue());
    }

    public static Color getColorFromName(String s) {
        switch (s) {
            case "orange":
                return Color.ORANGE;
            case "white":
                return Color.WHITE;
            case "black":
                return Color.BLACK;
            case "red":
                return Color.RED;
            case "yellow":
                return Color.YELLOW;
            case "green":
                return Color.GREEN;
            case "pink":
                return Color.PURPLE;
            case "blue":
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }
    public static String toChatColors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static int randomRange(float min, float max) {
		return (int)Math.round(Math.random() * (min + max) - min);
	}

    public static long getDateTime() {
        return Instant.now().toEpochMilli();
    }

    public static boolean equals(Vector a, Vector b) {
        return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }

    public static long toMillisecondTime(String s) {
        s = s.toLowerCase();
        if (s.endsWith("ms")) return Long.parseLong(s.replace("ms", ""));
        else if (s.endsWith("s")) return Math.round(Float.parseFloat(s.replace("s", ""))*1000);
        else if (s.endsWith("m")) return Math.round(Float.parseFloat(s.replace("m", ""))*1000)*60;
        return 0;
    }
    public static int toTickTime(String s) {
        s = s.toLowerCase();
        if (s.endsWith("s")) return Math.round(Float.parseFloat(s.replace("s", ""))*20);
        else if (s.endsWith("m")) return Math.round(Float.parseFloat(s.replace("m", ""))*20)*60;
        return 0;
    }

    public static String formatTimer(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("mm:ss"));
    }

    public static String toRomanNumerics(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return "";
        }
    }

    public static float getEmeraldDivider(int i) {
        switch (i) {
            case 2:
                return 1.33333333333f;
            case 3:
                return 2f;
            default:
                return 1f;
        }
    }
    public static float getDiamondDivider(int i) {
        switch (i) {
            case 2:
                return 1.5f;
            case 3:
                return 3f;
            default:
                return 1f;
        }
    }

    public static String getItemName(ItemStack stack) {
        return getItemName(stack.getType());
    }
    public static String getItemName(Material mat) {
        String name = "";
        String[] matName = mat.name().toLowerCase().split("_");
        for (int i = 0; i < matName.length; i++) {
            if (i != 0) name += " ";
            name += TextUtils.upperCaseFirst(matName[i]);
        }
        return name;
    }

    public static void runDelayedTask(RunnableFunction runnable, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() { runnable.run(); }  
        }.runTaskLater(BedwarsRunner.instance, delay);
    }

    public static void broadcastChat(String message, TextColor color) {
        broadcastChat(Utils.text(message, color));
    }
    public static void broadcastChat(Component message) {
        List<Player> players = BedwarsRunner.world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).sendMessage(message);
        }
    }

    public static Logger Logger() {
        return BedwarsRunner.logger;
    }

    public static interface RunnableFunction { public void run(); }

}
