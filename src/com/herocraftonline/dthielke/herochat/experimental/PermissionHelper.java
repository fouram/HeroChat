package com.herocraftonline.dthielke.herochat.experimental;

import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

public class PermissionHelper {

    private PermissionHandler security;

    public PermissionHelper(PermissionHandler security) {
        this.security = security;
    }

    public String getGroup(Player p) {
        String world = p.getWorld().getName();
        String name = p.getName();
        return security.getGroup(world, name);
    }

    public String getPrefix(Player p) {
        String world = p.getWorld().getName();
        String name = p.getName();
        String prefix = security.getUserPermissionString(world, name, "prefix");
        if (prefix == null || prefix.isEmpty()) {
            String group = security.getGroup(world, name);
            prefix = security.getGroupPrefix(world, group);
            if (prefix == null) {
                prefix = "";
            }
        }
        return prefix;
    }

    public String getSuffix(Player p) {
        String world = p.getWorld().getName();
        String name = p.getName();
        String suffix = security.getUserPermissionString(world, name, "suffix");
        if (suffix == null || suffix.isEmpty()) {
            String group = security.getGroup(world, name);
            suffix = security.getGroupPrefix(world, group);
            if (suffix == null) {
                suffix = "";
            }
        }
        return suffix;
    }
    
    public boolean isAdmin(Player p) {
        return security.has(p, "herochat.admin");
    }
    
    public boolean canCreate(Player p) {
        boolean admin = security.has(p, "herochat.admin");
        boolean create = security.has(p, "herochat.create");
        return admin || create;
    }

}
