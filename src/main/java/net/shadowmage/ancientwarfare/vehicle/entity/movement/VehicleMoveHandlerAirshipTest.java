package net.shadowmage.ancientwarfare.vehicle.entity.movement;

import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputKey;

public class VehicleMoveHandlerAirshipTest extends VehicleInputHandler {

    public VehicleMoveHandlerAirshipTest(VehicleBase vehicle) {
        super(vehicle);
    }

    @Override
    public void updateVehicleMotion(boolean[] inputStates) {
        float rotation = 0;
        double forward = 0;
        double ascent = 0;
        if (inputStates[VehicleInputKey.FORWARD.ordinal()]) {
            forward += 0.25d;
        }
        if (inputStates[VehicleInputKey.REVERSE.ordinal()]) {
            forward -= 0.25d;
        }
        if (inputStates[VehicleInputKey.LEFT.ordinal()]) {
            rotation += 1.f;
        }
        if (inputStates[VehicleInputKey.RIGHT.ordinal()]) {
            rotation -= 1.f;
        }
        if (inputStates[VehicleInputKey.ASCEND.ordinal()]) {
            ascent += 0.25d;
        }
        if (inputStates[VehicleInputKey.DESCEND.ordinal()]) {
            ascent -= 0.25d;
        }
        /**
         * first move the vehicle forward along its current move vector
         */
        Vec3 forwardAxis = vehicle.getLookVec();
        double mx = forwardAxis.xCoord * forward;
        double mz = forwardAxis.zCoord * forward;
        double my = ascent;
        vehicle.moveEntity(mx, my, mz);
        /**
         * then rotate the vehicle towards its new orientation
         */
        if (rotation != 0) {
            vehicle.moveHelper.rotateVehicle(rotation);
        }
    }
}
