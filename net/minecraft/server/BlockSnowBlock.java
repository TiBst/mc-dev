package net.minecraft.server;

import java.util.Random;

public class BlockSnowBlock extends Block {

    protected BlockSnowBlock(int i) {
        super(i, Material.SNOW_BLOCK);
        this.b(true);
        this.a(CreativeModeTab.b);
    }

    public int getDropType(int i, Random random, int j) {
        return Item.SNOW_BALL.id;
    }

    public int a(Random random) {
        return 4;
    }

    public void a(World world, int i, int j, int k, Random random) {
        if (world.b(EnumSkyBlock.BLOCK, i, j, k) > 11) {
            this.c(world, i, j, k, world.getData(i, j, k), 0);
            world.setAir(i, j, k);
        }
    }
}
