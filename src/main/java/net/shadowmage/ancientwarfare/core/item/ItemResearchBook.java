package net.shadowmage.ancientwarfare.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemResearchBook extends ItemClickable
{

public ItemResearchBook(String localizationKey)
  {
  super(localizationKey);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  this.setMaxStackSize(1);
  }

@Override
public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("researcherName"))
    {
    stack.setTagInfo("researcherName", new NBTTagString(player.getCommandSenderName()));
    }
  }

@Override
@SideOnly(Side.CLIENT)
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
  {
  super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
  String name = getResearcherName(par1ItemStack);
  if(name==null)
    {
    par3List.add(StatCollector.translateToLocal("guistrings.research.researcher_name")+": "+StatCollector.translateToLocal("guistrings.research.no_researcher"));
    }
  else
    {
    par3List.add(StatCollector.translateToLocal("guistrings.research.researcher_name")+": "+name);
    }
  }

public static final String getResearcherName(ItemStack stack)
  {
  if(stack!=null && stack.getItem()==AWItems.researchBook && stack.hasTagCompound() && stack.getTagCompound().hasKey("researcherName"))
    {
    return stack.getTagCompound().getString("researcherName");
    }
  return null;
  }

}