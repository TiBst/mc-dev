package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class BlockMinecartDetector extends BlockMinecartTrack {

    public BlockMinecartDetector(int i, int j) {
        super(i, j, true);
        this.b(true);
    }

    public int r_() {
        return 20;
    }

    public boolean isPowerSource() {
        return true;
    }

    public void a(World world, int i, int j, int k, Entity entity) {
        if (!world.isStatic) {
            int l = world.getData(i, j, k);

            if ((l & 8) == 0) {
                this.d(world, i, j, k, l);
            }
        }
    }

    public void b(World world, int i, int j, int k, Random random) {
        if (!world.isStatic) {
            int l = world.getData(i, j, k);

            if ((l & 8) != 0) {
                this.d(world, i, j, k, l);
            }
        }
    }

    public boolean b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.getData(i, j, k) & 8) != 0;
    }

    public boolean c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.getData(i, j, k) & 8) == 0 ? false : l == 1;
    }

    private void d(World world, int i, int j, int k, int l) {
        boolean flag = (l & 8) != 0;
        boolean flag1 = false;
        float f = 0.125F;
        List list = world.a(EntityMinecart.class, AxisAlignedBB.a().a((double) ((float) i + f), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) ((float) (j + 1) - f), (double) ((float) (k + 1) - f)));

        if (!list.isEmpty()) {
            flag1 = true;
        }

        if (flag1 && !flag) {
            world.setData(i, j, k, l | 8);
            world.applyPhysics(i, j, k, this.id);
            world.applyPhysics(i, j - 1, k, this.id);
            world.e(i, j, k, i, j, k);
        }

        if (!flag1 && flag) {
            world.setData(i, j, k, l & 7);
            world.applyPhysics(i, j, k, this.id);
            world.applyPhysics(i, j - 1, k, this.id);
            world.e(i, j, k, i, j, k);
        }

        if (flag1) {
            world.a(i, j, k, this.id, this.r_());
        }
    }
}
