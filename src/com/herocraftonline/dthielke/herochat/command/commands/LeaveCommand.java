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

public class LeaveCommand extends BaseCommand {

    public LeaveCommand(HeroChat plugin) {
        super(plugin);
        setName("Leave");
        setDescription("Leaves a channel");
        setUsage("§e/ch leave §9<channel>");
        setMinArgs(1);
        setMaxArgs(1);
        getIdentifiers().add("ch leave");
        getIdentifiers().add("leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            ChannelManager channelManager = plugin.getChannelManager();
            Channel channel = channelManager.getChannel(args[0]);
            Player player = (Player) sender;
            Chatter chatter = plugin.getChatterManager().getChatter(player);

            if (channel == null) {
                Messaging.send(sender, "Channel not found.");
                return;
            }

            if (!channel.hasChatter(chatter)) {
                Messaging.send(sender, "You are not in this channel.");
                return;
            }

            if (!channel.canLeave(chatter)) {
                Messaging.send(sender, "You can't leave this channel.");
                return;
            }

            channel.removeChatter(chatter, true);
        }
    }

}
