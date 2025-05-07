package win.trystage.velocityPlayerList;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static win.trystage.velocityPlayerList.VelocityPlayerList.*;
import static win.trystage.velocityPlayerList.VelocityPlayerList.getConfig;

public class CommandVTP implements SimpleCommand {
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return getProxyServer().getAllPlayers().stream()
                    .map(Player::getUsername)
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
            if (player.hasPermission("vpl.teleport")) {
                // null player name
                if (args.length == 0){
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("vtp-usage")));
                    return;
                }
                Optional<Player> targets = getProxyServer().getPlayer(args[0]);
                if (targets.isEmpty()){
                    //skip
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("null-player").replace("{player}",args[0])));
                    return;
                }
                Optional<ServerConnection> target = targets.get().getCurrentServer();
                if (target.isEmpty()){
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("teleport-failed").replace("{player}",args[0])));
                    return;
                }
                RegisteredServer targetServer = target.get().getServer();
                // send player to target server
                player.createConnectionRequest(targetServer).connect()
                        .thenAccept(result -> {
                            if (result.isSuccessful()) {
                                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.teleport").replace("{player}",args[0])));
                            } else {
                                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.teleport-failed").replace("{player}",args[0])));
                            }
                        });
            } else {
                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.noperm-run-command")));
            }
        } else {
            source.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.console-run-command")));
        }
    }
}
