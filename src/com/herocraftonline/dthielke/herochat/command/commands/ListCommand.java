/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.command.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.chatters.Chatter;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class ListCommand extends BaseCommand {

    private static final int CHANNELS_PER_PAGE = 9;

    public ListCommand(HeroChat plugin) {
        super(plugin);
        setName("List");
        setDescription("Lists all publicly available channels");
        setUsage("§e/ch list §8[page#]");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("ch list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Chatter chatter = null;
        if (sender instanceof Player) {
            chatter = plugin.getChatterManager().getChatter((Player) sender);
        }

        Set<Channel> channels = plugin.getChannelManager().getChannels();
        List<Channel> visible = new ArrayList<Channel>();
        for (Channel channel : channels) {
            if (!channel.isHidden()) {
                visible.add(channel);
            }
        }

        int pages = (int) Math.ceil((double) visible.size() / CHANNELS_PER_PAGE);
        int p = 1;
        if (args.length > 0) {
            try {
                p = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                p = 1;
            }
        }
        if (p > pages) {
            p = pages;
        }

        sender.sendMessage("§c-----[ " + "§f" + "Channel List <" + p + "/" + pages + ">§c ]-----");
        for (int i = 0; i < CHANNELS_PER_PAGE; i++) {
            int index = (p - 1) * CHANNELS_PER_PAGE + i;
            if (index >= visible.size()) {
                break;
            }
            Channel channel = visible.get(index);
            String msg = "  " + channel.getColor() + "[" + channel.getNick() + "] " + channel.getName();
            if (chatter != null && channel.hasChatter(chatter)) {
                msg = msg.concat(" *");
            }
            if (chatter != null && chatter.getFocus().equals(channel)) {
                msg = msg.concat(" @");
            }
            sender.sendMessage(msg);
        }
    }

}
