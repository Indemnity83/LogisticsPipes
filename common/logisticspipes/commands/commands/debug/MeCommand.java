package logisticspipes.commands.commands.debug;

import logisticspipes.commands.abstracts.ICommandHandler;
import logisticspipes.ticks.DebugGuiTickHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class MeCommand implements ICommandHandler {

	@Override
	public String[] getNames() {
		return new String[] { "me", "self" };
	}

	@Override
	public boolean isCommandUsableBy(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public String[] getDescription() {
		return new String[] { "Start debugging the CommandSender" };
	}

	@Override
	public void executeCommand(ICommandSender sender, String[] args) {
		DebugGuiTickHandler.instance().startWatchingOf(sender, (EntityPlayer) sender);
		sender.addChatMessage(new ChatComponentText("Starting SelfDebuging"));
	}
}
