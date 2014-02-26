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
package net.shadowmage.ancientwarfare.core.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.shadowmage.ancientwarfare.core.config.AWLog;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class NBTTools
{


/**
* NBT Tag types, by tagID 
* 0-END
* 1-BYTE
* 2-SHORT
* 3-INT
* 4-LONG
* 5-FLOAT
* 6-DOUBLE
* 7-BYTE-ARRAY
* 8-STRING
* 9-TAG-LIST
* 10-TAG-COMPOUND
* 11-INT-ARRAY
*/

/************************************************************ NBT STRING READ ********************************************************************************/
/**
 * Returns a {@link NBTTagCompound} from the input list of strings.<br>
 * A suitable list of strings may be attained from {@link #getLinesFor(NBTTagCompound)}  
 * @param lines
 * @return
 */
public static NBTTagCompound readNBTFrom(List<String> lines)
  {
  return parseNBTFromLines(lines);
  }

/************************************************************ NBT STRING WRITE ********************************************************************************/
/**
 * deprecated in favor of {@link #getLinesFor(NBTTagCompound)}
 * @param tag
 * @param lines
 */
@Deprecated
public static void writeNBTToLines(NBTTagCompound tag, List<String> lines)
  {
  List<String> lines1 = getLinesFor(tag);
  lines.addAll(lines1);
  }

/**
 * returns a list of strings containing all of the information from the input compound tag.<br>
 * The list may be read back through {@link #readNBTFrom(List)}
 * @param tag
 * @return
 */
public static List<String> getLinesFor(NBTTagCompound tag)
  {
  return getLinesForNBT(tag);
  }

/************************************************************ NBT STREAM WRITE ********************************************************************************/

/**
 * Writes a compressed NBTTagCompound to the OutputStream
 */
public static void writeNBTTagCompound(NBTTagCompound tag, DataOutputStream data) throws IOException
  {
  if (tag == null)
    {
    data.writeShort(-1);
    }
  else
    {
    byte[] var2 = CompressedStreamTools.compress(tag);
    data.writeShort((short)var2.length);
    data.write(var2);
    }
  }

public static void writeTagToStream(NBTTagCompound tag, ByteArrayDataOutput data)
  {
  if (tag == null)
    {
    data.writeShort(-1);
    }
  else
    {
    byte[] var2;
    try
      {
      var2 = CompressedStreamTools.compress(tag);
      data.writeShort((short)var2.length);
      data.write(var2);
      } 
    catch (IOException e)
      {
      e.printStackTrace();      
      }
    }  
  }


/************************************************************ NBT STREAM READ ********************************************************************************/

/**
 * Reads a compressed NBTTagCompound from the InputStream
 */
public static NBTTagCompound readNBTTagCompound(DataInputStream data) throws IOException
  {
  short var1 = data.readShort();  
  if (var1 < 0)
    {
    return null;
    }
  else
    {
    byte[] var2 = new byte[var1];
    data.readFully(var2);
    return CompressedStreamTools.decompress(var2);
    }
  }

/**
 * read a tag from a datastream, using google iowrapper
 * @param data
 * @return
 */
public static NBTTagCompound readTagFromStream(ByteArrayDataInput data)
  {
  short var1 = data.readShort();  
  if (var1 < 0)
    {
    return null;
    }
  else
    {
    byte[] var2 = new byte[var1];
    data.readFully(var2);
    try
      {
      return CompressedStreamTools.decompress(var2);
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    }
  return new NBTTagCompound();
  }


/************************************************************ NBT PROXY HANDLING ********************************************************************************/

private static NBTTagCompound parseNBTFromLines(List<String> lines)
  {
  TagCompound tag = parseTagFromLines(lines);
  NBTTagCompound returnTag = (NBTTagCompound) tag.getNBT();
  return returnTag;
  }

private static List<String> getLinesForNBT(NBTTagCompound nbttag)
  {
  ArrayList<String> lines = new ArrayList<String>();
  TagCompound tag = new TagCompound();
  tag.createFromNBT(nbttag);
  tag.getTagLines("", lines);
  return lines;
  } 

private static TagCompound parseTagFromLines(List<String> lines)
  {
  TagCompound tag = new TagCompound();
  tag.parseFromLines(lines);
  return tag;
  }

private static TagBase getTag(int type)
  {
  switch(type)
  {
  case 0:
  return null;
  case 1:
  return new TagByte();
  case 2:
  return new TagShort();
  case 3:
  return new TagInt();
  case 4:
  return new TagLong();
  case 5:
  return new TagFloat();
  case 6:
  return new TagDouble();
  case 7:
  return new TagByteArray();
  case 8:
  return new TagString();
  case 9:
  return new TagList();
  case 10:
  return new TagCompound();
  case 11:
  return new TagIntArray();
  }
  return null;
  }

private static List<String> parseNextTag(List<String> lines)
  {
  int open = 0, close = 0;
  ArrayList<String> linesOut = new ArrayList<String>();
  Iterator<String> it = lines.iterator();
  String line;
  while(it.hasNext())
    {
    line = it.next();
    if(line.startsWith("TAG="))
      {
      open++;
      }   
    if(line.startsWith("}") || line.endsWith("}"))
      {
      close++;
      }
    linesOut.add(line);
    it.remove();
    if(open>0 && open==close)
      {
      break;
      }//exit, found whole tag
    }
  return linesOut;
  }

private static abstract class TagBase
{
abstract int getType();
abstract NBTBase getNBT();
abstract void getTagLines(String tagName, List<String> lines);
abstract void parseFromLines(List<String> lines);
abstract void createFromNBT(NBTBase nbt);
}

private static class TagInt extends TagBase
{
int data;
@Override
int getType()
  {
  return 3;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagInt(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=3="+tagName+"{"+data+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.safeParseInt(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagInt)nbt).func_150287_d();
  }
}

private static class TagDouble extends TagBase
{
double data;
@Override
int getType()
  {
  return 5;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagDouble(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=5="+tagName+"{"+data+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.safeParseDouble(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagDouble)nbt).func_150286_g();
  }
}

private static class TagFloat extends TagBase
{
float data;
@Override
int getType()
  {
  return 5;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagFloat(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=5="+tagName+"{"+data+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.safeParseFloat(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagFloat)nbt).func_150288_h();
  }
}

private static class TagByte extends TagBase
{
byte data;
@Override
int getType()
  {
  return 1;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagByte(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=1="+tagName+"{"+data+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.safeParseByte(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagByte)nbt).func_150290_f();
  }
}

private static class TagShort extends TagBase
{
short data;
@Override
int getType()
  {
  return 2;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagShort();
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=2="+tagName+"{"+data+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.safeParseShort(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagShort)nbt).func_150289_e();
  }
}

private static class TagString extends TagBase
{
String data;
@Override
int getType()
  {
  return 8;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagString(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=8="+tagName+"{"+data+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = data;
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagString)nbt).func_150285_a_();
  }
}

private static class TagByteArray extends TagBase
{
byte[] data;
@Override
int getType()
  {
  return 7;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagByteArray(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=7="+tagName+"{"+StringTools.getCSVStringForArray(data)+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.parseByteArray(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagByteArray)nbt).func_150292_c();
  }
}

private static class TagLong extends TagBase
{
long data;
@Override
int getType()
  {
  return 4;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagLong(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=4="+tagName+"{"+data+"}");
  }  
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.safeParseLong(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagLong)nbt).func_150291_c();
  }
}

private static class TagIntArray extends TagBase
{
int[] data;
@Override
int getType()
  {
  return 11;
  }
@Override
NBTBase getNBT()
  {
  return new NBTTagIntArray(data);
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=11="+tagName+"{"+StringTools.getCSVStringForArray(data)+"}");
  }
@Override
void parseFromLines(List<String> lines)
  {
  Iterator<String> it = lines.iterator();
  String line = it.next();
  it.remove();
  String data = line.split("\\{", -1)[1];
  data = data.split("\\}")[0];
  this.data = StringTools.parseIntArray(data);
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  this.data = ((NBTTagIntArray)nbt).func_150302_c();
  }
}

private static class TagList extends TagBase
{
List<TagBase> tags = new ArrayList<TagBase>();
@Override
int getType()
  {
  return 9;
  }
@Override
NBTBase getNBT()
  {
  NBTTagList list = new NBTTagList();
  for(TagBase tag : this.tags)
    {
    list.appendTag(tag.getNBT());
    }
  return list;
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=9="+tagName+"{");
  for(TagBase tag : this.tags)
    {
    tag.getTagLines("", lines);
    }
  lines.add("}");
  }

@Override
void parseFromLines(List<String> lines)
  {
  lines.remove(0);//remove head (the open for this tag)
  lines.remove(lines.size()-1);//remove tail (the close for this tag)
  List<String> tagLines;
  int tagType;
  String line;
  while(!lines.isEmpty())
    {
    tagLines = parseNextTag(lines);
    line = tagLines.get(0);//pull the first line, to query for tag-type
    tagType = StringTools.safeParseInt(line.split("=", -1)[1]);
    TagBase tag = getTag(tagType);
    tag.parseFromLines(tagLines);
    this.tags.add(tag);
    }
  }

@Override
void createFromNBT(NBTBase nbt)
  {
  NBTTagList tag = (NBTTagList)nbt.copy();
  NBTBase innerTag;
  byte type;
  TagBase realTag;
  for(int i = 0; i < tag.tagCount(); i++)
    {
    innerTag = tag.removeTag(0);
    type = innerTag.getId();
    realTag = getTag(type);
    realTag.createFromNBT(innerTag);
    this.tags.add(realTag);
    }
  }
}

private static class TagCompound extends TagBase
{
HashMap<String, TagBase> tags = new HashMap<String, TagBase>();
@Override
int getType()
  {
  return 10;
  }
@Override
NBTBase getNBT()
  {
  NBTTagCompound tag = new NBTTagCompound();
  for(String key : this.tags.keySet())
    {
    tag.setTag(key, tags.get(key).getNBT());
    }
  return tag;
  }
@Override
void getTagLines(String tagName, List<String> lines)
  {
  lines.add("TAG=10="+tagName+"{");
  for(String key : this.tags.keySet())
    {
    tags.get(key).getTagLines(key, lines);
    }
  lines.add("}");
  }

@Override
void parseFromLines(List<String> lines)
  {
  lines.remove(0);//remove head (the open for this tag)
  lines.remove(lines.size()-1);//remove tail (the close for this tag)
  List<String> tagLines;
  int tagType;
  String tagName;
  String line;
  String[] splits;
  
  AWLog.logDebug("parsing compound from lines...");
  for(String l : lines)
    {
    AWLog.logDebug(l);
    }
  while(!lines.isEmpty())
    {
    tagLines = parseNextTag(lines);
    AWLog.logDebug("remaining lines: ");
    for(String l : lines)
      {
      AWLog.logDebug(l);
      }
    AWLog.logDebug("tag lines: ");
    for(String l : tagLines)
      {
      AWLog.logDebug(l);
      }
    line = tagLines.get(0);//pull the first line, to query for tag-type
    splits = line.split("=",-1);
    tagType = StringTools.safeParseInt(splits[1]);
    tagName = splits[2].split("\\{")[0];
    TagBase tag = getTag(tagType);
    tag.parseFromLines(tagLines);
    this.tags.put(tagName, tag);
    }
  AWLog.logDebug("finished parsing compound...");
  }
@Override
void createFromNBT(NBTBase nbt)
  {
  NBTTagCompound tag = (NBTTagCompound) nbt;
  @SuppressWarnings("unchecked")
  Set<String> keys = tag.func_150296_c();
  NBTBase baseTag;
  for(String key : keys)
    {
    baseTag = tag.getTag(key);
    TagBase newTag = getTag(baseTag.getId());
    newTag.createFromNBT(baseTag);
    tags.put(key, newTag);
    }
  }
}

}
