package assets.battlegear2.common.blocks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import assets.battlegear2.common.BattleGear;
import assets.battlegear2.common.BattlegearPacketHandeler;
import assets.battlegear2.common.gui.ContainerBattle;
import assets.battlegear2.common.heraldry.SigilHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBanner extends TileEntity {
	//0-7 (on ground top)
	//8-15 (on ground base)
	//16-19 (on wall top)
	//20-24 (on wall base)
	private byte state;
	
	private byte[] heraldry;
	
	public TileEntityBanner(){
		state = 0;
		heraldry = SigilHelper.getDefault();
	}

	public TileEntityBanner(byte state, byte[] heraldry) {
		super();
		this.state = state;
		this.heraldry = heraldry;
	}
	
	public boolean isBase() {
		return state < 8 || (state>15 && state< 20);
	}

	public boolean isTop(){
		return !(isBase());
	}

	public boolean isOnGround(){
		return state < 16;
	}
	public boolean isOnWall(){
		return state > 15;
	}
	
	public boolean isAngled(){
		return isOnGround() & state%2 == 1;
	}

	public int getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
	
	public byte[] getHeraldry() {
		return heraldry;
	}
	
	public float getAngle(){
		if(isOnGround()){
			if(isBase())
				return state * 45;
			else
				 return (state-8) * 45;
		}else{
			return (state - 16) * -90 - 90;
		}
	}
	
	
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		state = nbtTagCompound.getByte("state");
		heraldry = nbtTagCompound.getByteArray("hc2");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setByte("state", state);
		nbtTagCompound.setByteArray("hc2", heraldry);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(4 * 3 + 1 + SigilHelper.length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeByte(state);
			outputStream.write(heraldry);
			
			outputStream.writeInt(xCoord);
			outputStream.writeInt(yCoord);
			outputStream.writeInt(zCoord);
			return new Packet250CustomPayload(BattlegearPacketHandeler.bannerUpdate, bos.toByteArray());

		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}	
}
