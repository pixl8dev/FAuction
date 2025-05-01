package fr.florianpal.fauction.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {

    public static String format(String msg) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

        Matcher match = pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(msg);
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String durationFormat(String format, Duration duration) {
        format = format.replace("yyyy", "" + Math.abs(duration.getSeconds() / 31557600));
        format = format.replace("MM", "" + Math.abs(duration.getSeconds() / 2629800));
        format = format.replace("dd", "" + Math.abs(duration.getSeconds() / 86400));
        format = format.replace("HH", "" + Math.abs(duration.getSeconds() / 3600));
        format = format.replace("mm", "" + Math.abs(duration.getSeconds() % 3600 / 60));
        format = format.replace("ss", "" + Math.abs(duration.getSeconds() % 60));

        return format;
    }

    public static String titleItemFormat(ItemStack item, String replacement, String title) {
        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
            return title.replace(replacement, item.getType().name().replace('_', ' ').toLowerCase());
        }
        return title.replace(replacement, item.getItemMeta().getDisplayName());
    }

    public static String titleItemFormat(ItemStack item) {
        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
            return item.getType().name().replace('_', ' ').toLowerCase();
        }
        return item.getItemMeta().getDisplayName();
    }
}
