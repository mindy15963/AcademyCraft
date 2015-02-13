/**
 * 
 */
package cn.academy.core.energy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cn.academy.api.energy.IWirelessNode;
import cn.academy.api.energy.IWirelessTile;
import cn.annoreg.core.RegistrationClass;
import cn.annoreg.mc.RegEventHandler;
import cn.annoreg.mc.RegEventHandler.Bus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * Basic handling class of all wireless tiles.
 * @author WeathFolD
 */
@RegistrationClass
@RegEventHandler(Bus.FML)
public class WirelessSystem {
	
	private static final WirelessSystem INSTANCE = new WirelessSystem();
	
	static Map<Integer, WiWorldData> worldData = new HashMap<Integer, WiWorldData>();
	
	public static boolean hasNetwork(World world, String id) {
		return getData(world).hasChannel(id);
	}
	
	/**
	 * Link a IWirelessGenerator or IWirelessReceiver to a (usually nearest) node.
	 * @param tile
	 * @param node
	 */
	public static void attachTile(IWirelessTile tile, IWirelessNode node) {
		getData(((TileEntity)tile).getWorldObj()).registerUser(tile, node);
	}

	/**
	 * Register an wireless node into the network.
	 */
	public static void registerNode(IWirelessNode node, String channel) {
		assert(node instanceof TileEntity);
		instance().getData(((TileEntity) node).getWorldObj()).registerNode(node, channel);
	}
	
	public static String getPassword(World world, String channel) {
		return getData(world).getPassword(channel);
	}
	
	public static void setPassword(World world, String channel, String pwd) {
		getData(world).setPassword(channel, pwd);
	}
	
	/**
	 * Unregister an user tile from the current channel it is in.
	 * @param tile
	 */
	public static void unregisterTile(IWirelessTile tile) {
		assert(tile instanceof TileEntity);
		instance().getData(((TileEntity) tile).getWorldObj()).unregister(tile);
	}
	
	public static boolean isTileIn(IWirelessTile tile, String channel) {
		return getData(((TileEntity)tile).getWorldObj()).isInChannel(tile, channel);
	}
	
	public static IWirelessNode getNearestNode(World world, int x, int y, int z) {
		return getData(world).getNearestNode(x, y, z);
	}
	
	public static List<IWirelessNode> getAvailableNodes(World world, int x, int y, int z, double range, int max) {
		return getData(world).getNodesIn(x, y, z, range, max);
	}
	
	private static WiWorldData getData(World world) {
		WiWorldData res = worldData.get(world.provider.dimensionId);
		if(res == null) {
			res = new WiWorldData(world);
			worldData.put(world.provider.dimensionId, res);
		}
		return res;
	}
	
	public static WirelessSystem instance() {
		return INSTANCE;
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		for(WiWorldData data : worldData.values()) {
			data.onTick();
		}
	}

}