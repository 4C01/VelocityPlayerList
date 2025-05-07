package win.trystage.velocityPlayerList;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static win.trystage.velocityPlayerList.VelocityPlayerList.*;

public class CommandOnlines implements SimpleCommand {
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return getProxyServer().getAllServers().stream()
                    .map(RegisteredServer::getServerInfo)
                    .map(ServerInfo::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
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
                    Optional<RegisteredServer> server = getProxyServer().getServer(args[0]);
                    if (server.isEmpty()){
                        player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.server-not-exists").replace("{server}",args[0])));
                        return;
                    }
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.onlines")
                            .replace("{count}", String.valueOf(server.get().getPlayersConnected().size()))));
                    String playerNames = server.get().getPlayersConnected().stream()
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
