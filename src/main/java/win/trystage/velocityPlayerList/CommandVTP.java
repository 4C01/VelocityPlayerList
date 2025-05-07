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

        // 只在第一个参数提供玩家名补全
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return getProxyServer().getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return List.of(); // 其他参数不提供补全
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
                if (args[0] == null){
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("vtp-usage")));
                }
                Optional<Player> targets = getProxyServer().getPlayer(args[0]);
                if (targets.isEmpty()){
                    //skip
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("null-player").replace("{player}",player.getUsername())));
                    return;
                }
                Optional<ServerConnection> target = targets.get().getCurrentServer();
                if (target.isEmpty()){
                    player.sendMessage(getMiniMessage().deserialize(getConfig().getString("teleport-failed").replace("{player}",player.getUsername())));
                    return;
                }
                RegisteredServer targetServer = target.get().getServer();
                // send player to target server
                player.createConnectionRequest(targetServer).connect()
                        .thenAccept(result -> {
                            if (result.isSuccessful()) {
                                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.teleport").replace("{player}",player.getUsername())));
                            } else {
                                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.teleport-failed").replace("{player}",player.getUsername())));
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
