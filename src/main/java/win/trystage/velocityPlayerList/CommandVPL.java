package win.trystage.velocityPlayerList;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import static win.trystage.velocityPlayerList.VelocityPlayerList.*;

public class CommandVPL implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        // 获取命令执行者
        CommandSource source = invocation.source();
        // 获取参数
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            Player player = (Player) source;
            if (!player.hasPermission("vpl.admin")) {
                player.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.noperm-run-command")));
                return;
            }
        }
        source.sendMessage(getMiniMessage().deserialize(getConfig().getString("message.reloaded")));
        VelocityPlayerList.loadConfig();
    }
}
