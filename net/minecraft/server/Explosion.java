package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Explosion {

    public boolean a = false;
    public boolean b = true;
    private int i = 16;
    private Random j = new Random();
    private World world;
    public double posX;
    public double posY;
    public double posZ;
    public Entity source;
    public float size;
    public List blocks = new ArrayList();
    private Map l = new HashMap();

    public Explosion(World world, Entity entity, double d0, double d1, double d2, float f) {
        this.world = world;
        this.source = entity;
        this.size = f;
        this.posX = d0;
        this.posY = d1;
        this.posZ = d2;
    }

    public void a() {
        float f = this.size;
        HashSet hashset = new HashSet();

        int i;
        int j;
        int k;
        double d0;
        double d1;
        double d2;

        for (i = 0; i < this.i; ++i) {
            for (j = 0; j < this.i; ++j) {
                for (k = 0; k < this.i; ++k) {
                    if (i == 0 || i == this.i - 1 || j == 0 || j == this.i - 1 || k == 0 || k == this.i - 1) {
                        double d3 = (double) ((float) i / ((float) this.i - 1.0F) * 2.0F - 1.0F);
                        double d4 = (double) ((float) j / ((float) this.i - 1.0F) * 2.0F - 1.0F);
                        double d5 = (double) ((float) k / ((float) this.i - 1.0F) * 2.0F - 1.0F);
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        float f1 = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);

                        d0 = this.posX;
                        d1 = this.posY;
                        d2 = this.posZ;

                        for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
                            int l = MathHelper.floor(d0);
                            int i1 = MathHelper.floor(d1);
                            int j1 = MathHelper.floor(d2);
                            int k1 = this.world.getTypeId(l, i1, j1);

                            if (k1 > 0) {
                                Block block = Block.byId[k1];
                                float f3 = this.source != null ? this.source.a(this, block, l, i1, j1) : block.a(this.source);

                                f1 -= (f3 + 0.3F) * f2;
                            }

                            if (f1 > 0.0F) {
                                hashset.add(new ChunkPosition(l, i1, j1));
                            }

                            d0 += d3 * (double) f2;
                            d1 += d4 * (double) f2;
                            d2 += d5 * (double) f2;
                        }
                    }
                }
            }
        }

        this.blocks.addAll(hashset);
        this.size *= 2.0F;
        i = MathHelper.floor(this.posX - (double) this.size - 1.0D);
        j = MathHelper.floor(this.posX + (double) this.size + 1.0D);
        k = MathHelper.floor(this.posY - (double) this.size - 1.0D);
        int l1 = MathHelper.floor(this.posY + (double) this.size + 1.0D);
        int i2 = MathHelper.floor(this.posZ - (double) this.size - 1.0D);
        int j2 = MathHelper.floor(this.posZ + (double) this.size + 1.0D);
        List list = this.world.getEntities(this.source, AxisAlignedBB.a().a((double) i, (double) k, (double) i2, (double) j, (double) l1, (double) j2));
        Vec3D vec3d = this.world.getVec3DPool().create(this.posX, this.posY, this.posZ);

        for (int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = (Entity) list.get(k2);
            double d7 = entity.f(this.posX, this.posY, this.posZ) / (double) this.size;

            if (d7 <= 1.0D) {
                d0 = entity.locX - this.posX;
                d1 = entity.locY + (double) entity.getHeadHeight() - this.posY;
                d2 = entity.locZ - this.posZ;
                double d8 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                if (d8 != 0.0D) {
                    d0 /= d8;
                    d1 /= d8;
                    d2 /= d8;
                    double d9 = (double) this.world.a(vec3d, entity.boundingBox);
                    double d10 = (1.0D - d7) * d9;

                    entity.damageEntity(DamageSource.EXPLOSION, (int) ((d10 * d10 + d10) / 2.0D * 8.0D * (double) this.size + 1.0D));
                    entity.motX += d0 * d10;
                    entity.motY += d1 * d10;
                    entity.motZ += d2 * d10;
                    if (entity instanceof EntityHuman) {
                        this.l.put((EntityHuman) entity, this.world.getVec3DPool().create(d0 * d10, d1 * d10, d2 * d10));
                    }
                }
            }
        }

        this.size = f;
    }

    public void a(boolean flag) {
        this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
        if (this.size >= 2.0F && this.b) {
            this.world.addParticle("hugeexplosion", this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);
        } else {
            this.world.addParticle("largeexplode", this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);
        }

        Iterator iterator;
        ChunkPosition chunkposition;
        int i;
        int j;
        int k;
        int l;

        if (this.b) {
            iterator = this.blocks.iterator();

            while (iterator.hasNext()) {
                chunkposition = (ChunkPosition) iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.world.getTypeId(i, j, k);
                if (flag) {
                    double d0 = (double) ((float) i + this.world.random.nextFloat());
                    double d1 = (double) ((float) j + this.world.random.nextFloat());
                    double d2 = (double) ((float) k + this.world.random.nextFloat());
                    double d3 = d0 - this.posX;
                    double d4 = d1 - this.posY;
                    double d5 = d2 - this.posZ;
                    double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / (double) this.size + 0.1D);

                    d7 *= (double) (this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    this.world.addParticle("explode", (d0 + this.posX * 1.0D) / 2.0D, (d1 + this.posY * 1.0D) / 2.0D, (d2 + this.posZ * 1.0D) / 2.0D, d3, d4, d5);
                    this.world.addParticle("smoke", d0, d1, d2, d3, d4, d5);
                }

                if (l > 0) {
                    Block.byId[l].dropNaturally(this.world, i, j, k, this.world.getData(i, j, k), 0.3F, 0);
                    if (this.world.setRawTypeIdAndData(i, j, k, 0, 0, this.world.isStatic)) {
                        this.world.applyPhysics(i, j, k, 0);
                    }

                    Block.byId[l].wasExploded(this.world, i, j, k);
                }
            }
        }

        if (this.a) {
            iterator = this.blocks.iterator();

            while (iterator.hasNext()) {
                chunkposition = (ChunkPosition) iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.world.getTypeId(i, j, k);
                int i1 = this.world.getTypeId(i, j - 1, k);

                if (l == 0 && Block.q[i1] && this.j.nextInt(3) == 0) {
                    this.world.setTypeId(i, j, k, Block.FIRE.id);
                }
            }
        }
    }

    public Map b() {
        return this.l;
    }
}
