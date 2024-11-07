package me.pixeldots.Extras;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class BedwarsChatRenderer implements ChatRenderer {

    public ChatRenderer renderer = null;
    public Component prefix = null;

    public BedwarsChatRenderer(ChatRenderer _renderer, Component _prefix) {
        this.renderer = _renderer;
        this.prefix = _prefix;
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component name, @NotNull Component message, @NotNull Audience recivers) {
        return renderer.render(player, prefix.append(name), message, recivers);
    }
    
}
