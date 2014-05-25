package mods.flammpfeil.slashblade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import scala.annotation.varargs;

public class CommandHandler implements ICommand {


	@Override
	public String getCommandName() {
		return "slashblade";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return null;
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(!icommandsender.getEntityWorld().isRemote){
			World w = icommandsender.getEntityWorld();

            if(astring.length >= 1 && astring[0].equals("ps")){
                EntityPlayer pl = w.getPlayerEntityByName(icommandsender.getCommandSenderName());
                ItemStack item = pl.getHeldItem();
                if(item.getItem() instanceof ItemSlashBlade){
                    NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(item);

                    if(((ItemSlashBlade)item.getItem()).getSwordType(item).contains(ItemSlashBlade.SwordType.Bewitched)){
                        int ps = tag.getInteger(ItemSlashBlade.proudSoulStr);
                        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, item);
                        if(1 <= ps && 0 < level){
                            ps-=1;
                            tag.setInteger(ItemSlashBlade.proudSoulStr,ps);

                            float magicDamage = 1 + level;

                            EntityPhantomSword entityDrive = new EntityPhantomSword(w, pl, magicDamage,90.0f);
                            if (entityDrive != null) {
                                entityDrive.setInitialSpeed(1.75f);
                                entityDrive.setLifeTime(30);
                                w.spawnEntityInWorld(entityDrive);
                            }
                        }
                    }

                }
            }
//				icommandsender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("required BlockID or Metadata /uim [id][:meta]",""));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

}