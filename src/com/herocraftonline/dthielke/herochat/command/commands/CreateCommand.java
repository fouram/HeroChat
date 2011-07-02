/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.chatters.Chatter;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;
import com.herocraftonline.dthielke.herochat.util.Messaging;
import com.herocraftonline.dthielke.herochat.util.PermissionManager.Permission;

public class CreateCommand extends BaseCommand {

    private static final String[] RESERVED_NAMES = { "ch", "join", "leave", "ignore", "help", "ban", "create", "kick", "list", "mod", "qm", "reload", "remove", "who", "focus", "gmute", "mute", "toggle" };

    public CreateCommand(HeroChat plugin) {
        super(plugin);
        name = "Create";
        description = "Creates a channel";
        usage = "§e/ch create §9<name> <nick> §8[password]";
        minArgs = 2;
        maxArgs = 3;
        identifiers.add("ch create");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.getPermissionManager().hasPermission(player, Permission.CREATE)) {
                Messaging.send(player, "Insufficient permission.");
                return;
            }
        }
        
        String name = args[0];
        String nick = args[1];
        String password = args.length == 2 ? args[2] : "";
        
        for (String reserved : RESERVED_NAMES) {
            if (reserved.equalsIgnoreCase(name) || reserved.equalsIgnoreCase(nick)) {
                Messaging.send(sender, "$1 is a reserved name.", reserved.toUpperCase());
                return;
            }
        }
        
        ChannelManager channelManager = plugin.getChannelManager();
        if (channelManager.getChannel(name) != null) {
            Messaging.send(sender, "That name is taken.");
            return;
        }
        if (channelManager.getChannel(nick) != null) {
            Messaging.send(sender, "That name is taken.");
            return;
        }
        
        Channel channel = new Channel(plugin, name, nick, password);
        channelManager.addChannel(channel);
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chatter chatter = plugin.getChatterManager().getChatter(player);
            
            channel.addChatter(chatter, true);
            channel.addModerator(chatter, true);
            chatter.setFocus(channel, true);
        }
        
        plugin.getConfigManager().save();
        
        Messaging.send(sender, "Channel created.");
    }
/*
    private ChannelOld createChannel(String[] args, boolean full) {
        ChannelOld c = new ChannelOld(plugin);
        c.setName(args[0]);
        c.setNick(args[1]);
        c.setMsgFormat("{default}");
        for (int i = 2; i < args.length; i++) {
            String tmp = args[i].toLowerCase();

            if (tmp.startsWith("p:")) {
                c.setPassword(tmp.substring(2));
            } else if (tmp.startsWith("color:")) {
                try {
                    int color = Integer.parseInt(tmp.substring(6), 16);
                    c.setColor(ChatColor.values()[color]);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (tmp.startsWith("-")) {
                tmp = tmp.substring(1);
                applyOptions(c, tmp.toCharArray(), full);
            }
        }
        return c;
    }

    private void applyOptions(ChannelOld c, char[] args, boolean full) {
        for (char option : args) {
            switch (option) {
                case 'h':
                    c.setHidden(true);
                    break;
                case 'j':
                    c.setVerbose(true);
                    break;
                case 'a':
                    if (full) {
                        c.setAutoJoined(true);
                    }
                    break;
                case 'q':
                    if (full) {
                        c.setQuickMessagable(true);
                    }
                    break;
                case 'f':
                    if (full) {
                        c.setForced(true);
                    }
                    break;
            }
        }
    }
*/
}
