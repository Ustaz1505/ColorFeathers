package com.ustaz1505.cfs.tab_completer;

import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class CfsTabHandler implements Listener {
    @EventHandler
    public void onTabComplete(TabCompleteEvent e){
        List<String> completions=new ArrayList<>();
        if (e.getBuffer().startsWith("cfs ")){
            if (e.getSender().hasPermission("shop.reload"))    completions.add("reload");
            if (e.getSender().hasPermission("shop.setsale")) completions.add("setsale");
            e.setCompletions(completions);
        }
    }
}
