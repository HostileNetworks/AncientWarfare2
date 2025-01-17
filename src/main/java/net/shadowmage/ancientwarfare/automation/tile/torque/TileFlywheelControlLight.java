package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public final class TileFlywheelControlLight extends TileFlywheelControl {

    @Override
    protected double getEfficiency() {
        return AWAutomationStatics.low_efficiency_factor;
    }

    @Override
    protected double getMaxTransfer() {
        return AWAutomationStatics.low_transfer_max;
    }

}
