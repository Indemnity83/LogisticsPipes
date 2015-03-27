package logisticspipes.proxy.specialconnection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import logisticspipes.LogisticsPipes;
import logisticspipes.interfaces.routing.ISpecialPipedConnection;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.specialconnection.SpecialPipeConnection.ConnectionInformation;
import logisticspipes.routing.PipeRoutingConnectionType;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import logisticspipes.utils.tuples.Pair;
import logisticspipes.utils.tuples.Quartet;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;

/** Support for teleport pipes **/
public class TeleportPipes implements ISpecialPipedConnection {

	private static Class<? extends Pipe> PipeItemTeleport;
	private static Method teleportPipeMethod;
	private static Object teleportManager;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init() {
		try {
			try {
				PipeItemTeleport = (Class<? extends Pipe>) Class.forName("buildcraft.additionalpipes.pipes.PipeItemTeleport");
			} catch (Exception e) {
				PipeItemTeleport = (Class<? extends Pipe>) Class.forName("net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemTeleport");
			}
			teleportPipeMethod = PipeItemTeleport.getMethod("getConnectedPipes", boolean.class);
			LogisticsPipes.log.debug("Additional pipes detected, adding compatibility");
			return true;
		} catch (Exception e1) {
			try {
				PipeItemTeleport = (Class<? extends Pipe>) Class.forName("buildcraft.additionalpipes.pipes.PipeItemsTeleport");
				Class<?> tpmanager = Class.forName("buildcraft.additionalpipes.pipes.TeleportManager");
				teleportManager = tpmanager.getField("instance").get(null);
				teleportPipeMethod = tpmanager.getMethod("getConnectedPipes",Class.forName("buildcraft.additionalpipes.pipes.PipeTeleport"),boolean.class);
				LogisticsPipes.log.debug("Additional pipes detected, adding compatibility");
				return true;
			} catch (Exception e2) {
				LogisticsPipes.log.debug("Additional pipes not detected: " + e2.getMessage());
				return false;
			}
		}
	}


	@Override
	public boolean isType(IPipeInformationProvider tile) {
		if(tile.getTile() instanceof TileGenericPipe && ((TileGenericPipe)tile.getTile()).pipe != null) {
			if(PipeItemTeleport.isAssignableFrom(((TileGenericPipe)tile.getTile()).pipe.getClass())) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private LinkedList<? extends Pipe> getConnectedTeleportPipes(Pipe pipe) throws Exception {
		if (teleportManager != null) {
			return (LinkedList<? extends Pipe>) teleportPipeMethod.invoke(teleportManager, pipe, false);
		}
		return (LinkedList<? extends Pipe>) teleportPipeMethod.invoke(pipe, false);
	}
	
	@Override
	public List<ConnectionInformation> getConnections(IPipeInformationProvider tile, EnumSet<PipeRoutingConnectionType> connection, ForgeDirection side) {
		List<ConnectionInformation> list = new ArrayList<ConnectionInformation>();
		if(tile.getTile() instanceof TileGenericPipe && ((TileGenericPipe)tile.getTile()).pipe != null) {
			try {
				LinkedList<? extends Pipe> pipes = getConnectedTeleportPipes(((TileGenericPipe)tile.getTile()).pipe);
				for(Pipe pipe : pipes) {
					list.add(new ConnectionInformation(SimpleServiceLocator.pipeInformaitonManager.getInformationProviderFor(pipe.container), connection, side, ForgeDirection.UNKNOWN, 0));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
