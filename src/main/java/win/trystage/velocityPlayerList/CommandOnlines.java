package win.trystage.velocityPlayerList;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.stream.Collectors;

import static win.trystage.velocityPlayerList.VelocityPlayerList.*;

public class CommandOnlines implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        // 获取命令执行者
        CommandSource source = invocation.source();
        // 获取参数
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            Player player = (Player) source;
            if (player.hasPermission("vpl.onlines")) {
                if (args.length > 0){
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.onlines")
                            .replace("{count}", String.valueOf(getProxyServer().getPlayerCount()))));
                    String playerNames = getProxyServer().getServer(args[0]).get().getPlayersConnected().stream()
                            .map(Player::getUsername)
                            .collect(Collectors.joining(", "));
                    player.sendMessage(Component.text(playerNames));
                    return;
                }
                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.onlines")
                        .replace("{count}", String.valueOf(getProxyServer().getPlayerCount()))));
                String playerNames = getProxyServer().getAllPlayers().stream()
                        .map(Player::getUsername)
                        .collect(Collectors.joining(", "));
                player.sendMessage(Component.text(playerNames));
            } else {
                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.noperm-run-command")));
            }
        } else {
            source.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.console-run-command")));
        }
    }
}
