package net.minecraft.server;

public class ItemCloth extends ItemBlock {

    public ItemCloth(int i) {
        super(i);
        this.setMaxDurability(0);
        this.a(true);
    }

    public int filterData(int i) {
        return i;
    }

    public String c_(ItemStack itemstack) {
        return super.getName() + "." + ItemDye.a[BlockCloth.e_(itemstack.getData())];
    }
}
