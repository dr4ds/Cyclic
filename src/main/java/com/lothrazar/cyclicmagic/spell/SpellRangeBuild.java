package com.lothrazar.cyclicmagic.spell;

import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.SpellCaster;
import com.lothrazar.cyclicmagic.SpellRegistry;
import com.lothrazar.cyclicmagic.gui.InventoryWand;
import com.lothrazar.cyclicmagic.item.ItemCyclicWand;
import com.lothrazar.cyclicmagic.net.MessageSpellFromServer;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack; 
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpellRangeBuild extends BaseSpellRange implements ISpellFromServer{
 
	public static enum PlaceType {
		PLACE, UP, DOWN;
	}
	private PlaceType type;
	public SpellRangeBuild(int id, String n,PlaceType t){

		super.init(id, n);
		this.cooldown = 8;
		this.cost = 5;
		this.type = t;
	}

	@Override
	public boolean cast(World world, EntityPlayer p, BlockPos pos, EnumFacing side){

		if(world.isRemote){
			// only client side can call this method. mouseover does not exist
			// on server
			BlockPos mouseover = ModMain.proxy.getBlockMouseoverExact(maxRange);
			BlockPos offset = ModMain.proxy.getBlockMouseoverOffset(maxRange);
			
			
			if(mouseover != null && offset != null){

				ModMain.network.sendToServer(new MessageSpellFromServer(mouseover, offset, this.getID()));
			}
		}

		return false;
	}

	public  void castFromServer(BlockPos posMouseover, BlockPos posOffset, EntityPlayer p){

		World world = p.worldObj;
		
		ItemStack heldWand = SpellCaster.getPlayerWandIfHeld(p);
		
		if(heldWand == null){
			return;
		}

		int itemSlot = InventoryWand.getSlotByBuildType(heldWand, world.getBlockState(posMouseover));
		ItemStack[] invv = InventoryWand.readFromNBT(heldWand);
		ItemStack toPlace = InventoryWand.getFromSlot(heldWand, itemSlot);

		if(toPlace != null && toPlace.getItem() != null && Block.getBlockFromItem(toPlace.getItem()) != null){

			IBlockState state = Block.getBlockFromItem(toPlace.getItem()).getStateFromMeta(toPlace.getMetadata());

			if(state != null){
				BlockPos posToPlaceAt = null;
			
				int max = 32;
				
				switch(type){
				case DOWN:
					//start at posMouseover, go DOWN until air
					BlockPos posLoop = posMouseover;
					for(int i = 0; i < max; i++){
						if(world.isAirBlock(posLoop)){
							posToPlaceAt = posLoop;
							break;
						}
						else{
							posLoop = posLoop.down();
						}
					}
					break;
				case PLACE:
					//use offset NOT mouseover
					posToPlaceAt = posOffset;
					break;
				case UP:
					//start at posMouseover, go up until air
					BlockPos pLoop = posMouseover;
					for(int i = 0; i < max; i++){
						if(world.isAirBlock(pLoop)){
							posToPlaceAt = pLoop;
							break;
						}
						else{
							pLoop = pLoop.up();
						}
					}
					break;
				default:
					break;
				}

				if(placeStateSafe(p.worldObj, p, posToPlaceAt, state)){

					SpellRegistry.caster.castSuccess(this, p.worldObj, p, posOffset);
					
					if(state.getBlock().getStepSound() != null && state.getBlock().getStepSound().getBreakSound() != null){
						UtilSound.playSound(p, state.getBlock().getStepSound().getPlaceSound());
					}

					if(p.capabilities.isCreativeMode == false){
						invv[itemSlot].stackSize--;
						// player.inventoryContainer.detectAndSendChanges();
						InventoryWand.writeToNBT(heldWand, invv);
					}

					// yes im spawning particles on the server side, but the
					// util handles that
					this.spawnParticle(p.worldObj, p, posMouseover);
					this.playSound(p.worldObj, state.getBlock(), posOffset);
				}
			}
		}
	}

	private  boolean placeStateSafe(World world, EntityPlayer player, BlockPos placePos, IBlockState placeState){
		if(placePos == null){
			return false;
		}
		if(world.isAirBlock(placePos) == false){

			// if there is a block here, we might have to stop
			IBlockState stateHere = world.getBlockState(placePos);
			Block blockHere = stateHere.getBlock();
			
			if(blockHere.isReplaceable(world, placePos) == false){
				// for example, torches, and the top half of a slab if you click
				// in the empty space
				return false;
			}

			// ok its a soft block so try to break it first try to destroy it
			// first
			// unless it is liquid, don't try to destroy liquid
			if(blockHere.getMaterial(stateHere) != Material.water && blockHere.getMaterial(stateHere) != Material.lava){
				boolean dropBlock = true;
				world.destroyBlock(placePos, dropBlock);
			}
		}

		// either it was air, or it wasnt and we broke it
		return world.setBlockState(placePos, placeState);
	}
}
