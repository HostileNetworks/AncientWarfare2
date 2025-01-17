package net.shadowmage.ancientwarfare.vehicle;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.vehicle.ballistics.TrajectoryPlotter;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.crafting.AWVehicleCrafting;
import net.shadowmage.ancientwarfare.vehicle.entity.AWVehicleEntityLoader;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItemLoader;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputReply;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputState;
import net.shadowmage.ancientwarfare.vehicle.proxy.VehicleCommonProxy;

@Mod
        (
                name = "Ancient Warfare Vehicles",
                modid = "AncientWarfareVehicle",
                version = "@VERSION@",
                dependencies = "required-after:AncientWarfare"
        )

public class AncientWarfareVehicles {

    @Instance(value = "AncientWarfareVehicle")
    public static AncientWarfareVehicles instance;

    @SidedProxy
            (
                    clientSide = "net.shadowmage.ancientwarfare.vehicle.proxy.VehicleClientProxy",
                    serverSide = "net.shadowmage.ancientwarfare.vehicle.proxy.VehicleCommonProxy"
            )
    public static VehicleCommonProxy proxy;

    public static AWVehicleStatics statics;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {

        ModuleStatus.vehiclesLoaded = true;

        /**
         * setup module-owned config file and config-access class
         */
        statics = new AWVehicleStatics("AncientWarfareVehicle");

        /**
         * load pre-init (items, blocks, entities)
         */
        proxy.registerClient();
        AWVehicleEntityLoader.load();
        AWVehicleItemLoader.load();

        /**
         * register tick-handlers
         */
        PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE_INPUT_STATE, PacketInputState.class);
        PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE_INPUT_RESPONSE, PacketInputReply.class);
        for (int i = 0; i < 100; i++) {
            TrajectoryPlotter.loadTest();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {

        /**
         * construct recipes, load plugins
         */
        AWVehicleCrafting.loadRecipes();
        /**
         * save config for any changes that were made during loading stages
         */
        statics.save();
    }
}
