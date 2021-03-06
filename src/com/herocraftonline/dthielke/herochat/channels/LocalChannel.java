/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.channels;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.chatters.Chatter;
import com.herocraftonline.dthielke.herochat.event.ChannelMessageEvent;
import com.herocraftonline.dthielke.herochat.messages.Message;
import com.herocraftonline.dthielke.herochat.messages.PlayerMessage;
import com.herocraftonline.dthielke.herochat.util.Messaging;

public class LocalChannel extends Channel {

    public static final int DEFAULT_DISTANCE = 100;
    protected int distance = DEFAULT_DISTANCE;

    public LocalChannel(HeroChat plugin, String name, String nick) {
        super(plugin, name, nick);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public void save(ConfigurationNode config, String path) {
        super.save(config, path);
        path += "." + name;
        config.setProperty(path + ".distance", 0);
    }

    @Override
    public boolean sendMessage(Message message) {
        if (!enabled) {
            return false;
        }

        if (message instanceof PlayerMessage) {
            PlayerMessage pMessage = (PlayerMessage) message;
            Chatter speaker = pMessage.getSender();
            pMessage.setRecipients(getNearbyChatters(speaker));
        }

        // fire a message event
        ChannelMessageEvent event = new ChannelMessageEvent(message);
        plugin.getServer().getPluginManager().callEvent(event);

        // check if the event was cancelled
        if (event.isCancelled()) {
            return false;
        }

        // format the message
        String formatted = Messaging.format(event.getData());

        // send the result to the recipients
        for (Chatter chatter : message.getRecipients()) {
            chatter.getPlayer().sendMessage(formatted);
        }

        return true;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    private Set<Chatter> getNearbyChatters(Chatter speaker) {
        Set<Chatter> nearbyChatters = new HashSet<Chatter>();
        Location sLoc = speaker.getPlayer().getLocation();
        String sWorld = sLoc.getWorld().getName();
        for (Chatter chatter : chatters) {
            if (chatter.equals(speaker)) {
                continue;
            }
            Player player = chatter.getPlayer();
            if (!chatter.isIgnoring(speaker)) {
                Location pLoc = player.getLocation();
                if (sWorld.equals(pLoc.getWorld().getName())) {
                    int dx = sLoc.getBlockX() - pLoc.getBlockX();
                    int dz = sLoc.getBlockZ() - pLoc.getBlockZ();
                    dx = dx * dx;
                    dz = dz * dz;
                    int d = (int) Math.sqrt(dx + dz);

                    if (d <= distance) {
                        nearbyChatters.add(chatter);
                    }
                }
            }
        }
        return nearbyChatters;
    }

    public static LocalChannel load(HeroChat plugin, ConfigurationNode config, String name) {
        // Collect necessary data from the config
        String nick = config.getString("nickname", "nick");
        String password = config.getString("password", "");
        String format = config.getString("format", "{default}");
        ChatColor color = ChatColor.valueOf(config.getString("color", "WHITE").toUpperCase());
        Mode mode = Mode.valueOf(config.getString("mode", "INCLUSIVE").toUpperCase());
        int distance = config.getInt("distance", DEFAULT_DISTANCE);

        boolean verbose = config.getBoolean("flags.join-messages", true);
        boolean quick = config.getBoolean("flags.shortcut-allowed", false);

        Set<String> bans = new HashSet<String>(config.getStringList("lists.bans", null));
        Set<String> mods = new HashSet<String>(config.getStringList("lists.moderators", null));

        // Create the channel
        LocalChannel channel = new LocalChannel(plugin, name, nick);

        // Apply the settings we collected earlier
        channel.setPassword(password);
        channel.setFormat(format.isEmpty() || format.equals("{default}") ? MSG_FORMAT : format);
        channel.setColor(color);
        channel.setMode(mode);
        channel.setVerbose(verbose);
        channel.setQuick(quick);
        channel.setDistance(distance);
        channel.bans = bans;
        channel.moderators = mods;

        // Return the finished channel
        return channel;
    }

}
