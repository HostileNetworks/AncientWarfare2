package net.shadowmage.ancientwarfare.npc.event;

import java.util.HashSet;
import java.util.Iterator;

import com.cosmicdan.pathfindertweaks.events.PathfinderEvent.PathfinderAvoidAdditionalEvent;
import com.cosmicdan.pathfindertweaks.events.PathfinderEvent.PathfinderCheckCanPathBlock;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.util.BlockAndMeta;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;

public class EventHandler {
    private EventHandler() {}
    
    public static final EventHandler INSTANCE = new EventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        
        if (event.entity instanceof EntityPlayer && !event.world.isRemote) {
            if (ModAccessors.FTBU_LOADED && !HeadquartersTracker.get(event.world).validateCurrentHq(event.entity.getCommandSenderName(), event.world)) {
                final String playerName = event.entity.getCommandSenderName();
                Thread thread = new Thread(){
                    public void run(){
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {}
                        HeadquartersTracker.notifyHqMissing(playerName);
                    }
                };
                thread.start();
            }
        }
        
        
        if (event.entity instanceof NpcBase)
            return;
        if (!(event.entity instanceof EntityCreature))
            return;
        if (!AncientWarfareNPC.statics.shouldEntityTargetNpcs(EntityList.getEntityString(event.entity)))
            return;
        if (AncientWarfareNPC.statics.autoTargetting) {
            // Use new "auto injection"
            EntityCreature entity = (EntityCreature) event.entity;
            
            // only for melee units
            if (entity.tasks.taskEntries.size() > 0) { // verify that the entity uses new AI
                int taskPriority = -1;
                Iterator taskEntriesIterator = entity.tasks.taskEntries.iterator();
                // search for entity's existing "EntityAIAttackOnCollide" task
                // so we can inject NPC targetting with the same priority
                while (taskEntriesIterator.hasNext()) {
                    EntityAITaskEntry taskEntry = (EntityAITaskEntry) taskEntriesIterator.next();
                    if (taskEntry.action instanceof EntityAIAttackOnCollide) {
                        if (((EntityAIAttackOnCollide)taskEntry.action).classTarget == EntityPlayer.class) {
                            // TODO: Task-based exclusions?
                            taskPriority = taskEntry.priority;
                        }
                    }
                }
                if (taskPriority != -1) {
                    entity.tasks.addTask(taskPriority, new EntityAIAttackOnCollide(entity, NpcBase.class, 1.0D, false));
                    //System.out.println("Injected EntityAIAttackOnCollide on " + EntityList.getEntityString(entity) + " @" + taskPriority);
                }
            }
            
            // all mobs that can attack somebody should have targetTasks
            if (entity.targetTasks.taskEntries.size() > 0) {
                int targetTaskPriority = -1;
                Iterator taskEntriesIterator = entity.targetTasks.taskEntries.iterator();
                // Again search for a task to use for the same priority as our attack NPC task, but
                // look for "EntityAINearestAttackableTarget" instead which is for ranged units
                while (taskEntriesIterator.hasNext()) {
                    EntityAITaskEntry taskEntry = (EntityAITaskEntry) taskEntriesIterator.next();
                    if (taskEntry.action instanceof EntityAINearestAttackableTarget) {
                        if (((EntityAINearestAttackableTarget)taskEntry.action).targetClass == EntityPlayer.class) {
                            // TODO: Task-based exclusions?
                            targetTaskPriority = taskEntry.priority;
                        }
                    }
                }
                if (targetTaskPriority != -1) {
                    entity.targetTasks.addTask(targetTaskPriority, new EntityAINearestAttackableTarget(entity, NpcBase.class, 0, AncientWarfareNPC.statics.autoTargettingConfigLos));
                    // add this entity to the internal list of hostile mobs, so NPC's know to fight it
                    NpcAI.addHostileEntity(entity);
                    //System.out.println("Injected EntityAINearestAttackableTarget on " + EntityList.getEntityString(entity) + " @" + targetTaskPriority);
                }
            }
        } else {
            // Legacy whitelist/manual method
            EntityCreature e = (EntityCreature) event.entity;
            if (event.entity instanceof EntitySkeleton) {
                EntitySkeleton skel = (EntitySkeleton) event.entity;
                if (skel.getSkeletonType() == 0) { //normal
                    e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
                } else { //wither
                    e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));
                    e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
                }
            } else if (event.entity instanceof IRangedAttackMob) {
                e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
            } else {
                e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));
                e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
            }
        }
    }

    @SubscribeEvent
    public void pathfinderCheckCanPathBlock(PathfinderCheckCanPathBlock event) {
        if (event.entity instanceof NpcBase) {
            if (event.block instanceof BlockDoor) {
                event.setResult(Result.ALLOW);
            }
        }
    }
    
    @SubscribeEvent
    public void pathfinderAvoidAdditionals(PathfinderAvoidAdditionalEvent event) {
        //System.out.println("Firing!");
        if (AWNPCStatics.pathfinderAvoidChests || AWNPCStatics.pathfinderAvoidFences || AWNPCStatics.getPathfinderAvoidCustomBlocks() != null) {
            Block block = event.entity.worldObj.getBlock(event.posX, event.posY, event.posZ);
            int meta = event.entity.worldObj.getBlockMetadata(event.posX, event.posY, event.posZ);
            if (AWNPCStatics.pathfinderAvoidChests) {
                if (block.getRenderType() == 22 || block instanceof BlockChest) {
                    event.setResult(Result.DENY);
                    return;
                }
            }
            if (AWNPCStatics.pathfinderAvoidFences) {
                if (block.getRenderType() == 11 || block instanceof BlockFence || block instanceof BlockWall) {
                    event.setResult(Result.DENY);
                    return;
                }
            }
            if (AWNPCStatics.getPathfinderAvoidCustomBlocks() != null) {
                for ( BlockAndMeta blockAndMeta : AWNPCStatics.getPathfinderAvoidCustomBlocks() ) {
                    if ( Block.isEqualTo(blockAndMeta.block, block) ) {
                        if ( blockAndMeta.meta == -1 || blockAndMeta.meta == meta ) {
                            event.setResult(Result.DENY);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.Clone event) {
        if (event.entityPlayer.worldObj.isRemote)
            return;
        if (event.wasDeath) {
            if (event.entityPlayer.getBedLocation(event.entityPlayer.dimension) == null) {
                // player has no bed in the respawned dimension
                int[] hqPos = HeadquartersTracker.get(event.entityPlayer.worldObj).getHqPos(event.entityPlayer.getCommandSenderName(), event.entityPlayer.worldObj);
                if (hqPos != null) {
                    EntityTools.teleportPlayerToBlock(event.entityPlayer, event.entityPlayer.worldObj, hqPos, true);
                }
            }
        }
    }
}
