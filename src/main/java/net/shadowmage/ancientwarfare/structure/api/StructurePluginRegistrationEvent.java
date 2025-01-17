/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Called when StructurePluginManager tries to load plugins, for each active mod
 * To be cancelled by mod wishing to have different block/entity support in structures, other than the default
 * StructurePluginModDefault
 */
@Cancelable
public class StructurePluginRegistrationEvent extends Event {

    public final IStructurePluginRegister register;
    public final String modId;

    public StructurePluginRegistrationEvent(IStructurePluginRegister register, String mod) {
        this.register = register;
        this.modId = mod;
    }

}
