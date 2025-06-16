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

        var year = 0;
        var month = 0;
        var day = 0;
        var hour = 0;
        var minute = 0;
        var seconde = 0;

        if (!duration.isNegative()) {

            long baseSecond = duration.getSeconds();
            year = (int) Math.floor((double) duration.getSeconds() / 31557600);
            baseSecond = baseSecond - year * 31557600L;
            month = (int) Math.floor((double) baseSecond / 2629800);
            baseSecond = baseSecond - month * 2629800L;
            day = (int) Math.floor((double) baseSecond / 86400);
            baseSecond = baseSecond - day * 86400L;
            hour = (int) Math.floor((double) baseSecond / 3600);
            baseSecond = baseSecond - hour * 3600L;
            minute = (int) Math.floor((double) baseSecond / 60);
            baseSecond = baseSecond - minute * 60L;
            seconde = (int) baseSecond;
        }

        format = format.replace("yyyy", "" + year);
        format = format.replace("MM", "" + month);
        format = format.replace("dd", "" + day);
        format = format.replace("HH", "" + hour);
        format = format.replace("mm", "" + minute);
        format = format.replace("ss", "" + seconde);

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
