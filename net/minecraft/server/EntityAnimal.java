package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public abstract class EntityAnimal extends EntityAgeable implements IAnimal {

    private int love;
    private int e = 0;

    public EntityAnimal(World world) {
        super(world);
    }

    protected void bj() {
        if (this.getAge() != 0) {
            this.love = 0;
        }

        super.bj();
    }

    public void c() {
        super.c();
        if (this.getAge() != 0) {
            this.love = 0;
        }

        if (this.love > 0) {
            --this.love;
            String s = "heart";

            if (this.love % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(s, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }
        } else {
            this.e = 0;
        }
    }

    protected void a(Entity entity, float f) {
        if (entity instanceof EntityHuman) {
            if (f < 3.0F) {
                double d0 = entity.locX - this.locX;
                double d1 = entity.locZ - this.locZ;

                this.yaw = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
                this.b = true;
            }

            EntityHuman entityhuman = (EntityHuman) entity;

            if (entityhuman.bP() == null || !this.c(entityhuman.bP())) {
                this.target = null;
            }
        } else if (entity instanceof EntityAnimal) {
            EntityAnimal entityanimal = (EntityAnimal) entity;

            if (this.getAge() > 0 && entityanimal.getAge() < 0) {
                if ((double) f < 2.5D) {
                    this.b = true;
                }
            } else if (this.love > 0 && entityanimal.love > 0) {
                if (entityanimal.target == null) {
                    entityanimal.target = this;
                }

                if (entityanimal.target == this && (double) f < 3.5D) {
                    ++entityanimal.love;
                    ++this.love;
                    ++this.e;
                    if (this.e % 4 == 0) {
                        this.world.addParticle("heart", this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D);
                    }

                    if (this.e == 60) {
                        this.c((EntityAnimal) entity);
                    }
                } else {
                    this.e = 0;
                }
            } else {
                this.e = 0;
                this.target = null;
            }
        }
    }

    private void c(EntityAnimal entityanimal) {
        EntityAnimal entityanimal1 = this.createChild(entityanimal);

        if (entityanimal1 != null) {
            this.setAge(6000);
            entityanimal.setAge(6000);
            this.love = 0;
            this.e = 0;
            this.target = null;
            entityanimal.target = null;
            entityanimal.e = 0;
            entityanimal.love = 0;
            entityanimal1.setAge(-24000);
            entityanimal1.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);

            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle("heart", this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }

            this.world.addEntity(entityanimal1);
        }
    }

    public abstract EntityAnimal createChild(EntityAnimal entityanimal);

    public boolean damageEntity(DamageSource damagesource, int i) {
        this.c = 60;
        this.target = null;
        this.love = 0;
        return super.damageEntity(damagesource, i);
    }

    public float a(int i, int j, int k) {
        return this.world.getTypeId(i, j - 1, k) == Block.GRASS.id ? 10.0F : this.world.o(i, j, k) - 0.5F;
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("InLove", this.love);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.love = nbttagcompound.getInt("InLove");
    }

    protected Entity findTarget() {
        if (this.c > 0) {
            return null;
        } else {
            float f = 8.0F;
            List list;
            Iterator iterator;
            EntityAnimal entityanimal;

            if (this.love > 0) {
                list = this.world.a(this.getClass(), this.boundingBox.grow((double) f, (double) f, (double) f));
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityanimal = (EntityAnimal) iterator.next();
                    if (entityanimal != this && entityanimal.love > 0) {
                        return entityanimal;
                    }
                }
            } else if (this.getAge() == 0) {
                list = this.world.a(EntityHuman.class, this.boundingBox.grow((double) f, (double) f, (double) f));
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityHuman entityhuman = (EntityHuman) iterator.next();

                    if (entityhuman.bP() != null && this.c(entityhuman.bP())) {
                        return entityhuman;
                    }
                }
            } else if (this.getAge() > 0) {
                list = this.world.a(this.getClass(), this.boundingBox.grow((double) f, (double) f, (double) f));
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityanimal = (EntityAnimal) iterator.next();
                    if (entityanimal != this && entityanimal.getAge() < 0) {
                        return entityanimal;
                    }
                }
            }

            return null;
        }
    }

    public boolean canSpawn() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.boundingBox.b);
        int k = MathHelper.floor(this.locZ);

        return this.world.getTypeId(i, j - 1, k) == Block.GRASS.id && this.world.k(i, j, k) > 8 && super.canSpawn();
    }

    public int aM() {
        return 120;
    }

    protected boolean bg() {
        return false;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    public boolean c(ItemStack itemstack) {
        return itemstack.id == Item.WHEAT.id;
    }

    public boolean c(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && this.c(itemstack) && this.getAge() == 0) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                --itemstack.count;
                if (itemstack.count <= 0) {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
                }
            }

            this.love = 600;
            this.target = null;

            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle("heart", this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }

            return true;
        } else {
            return super.c(entityhuman);
        }
    }

    public boolean r() {
        return this.love > 0;
    }

    public void s() {
        this.love = 0;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (entityanimal.getClass() != this.getClass() ? false : this.r() && entityanimal.r());
    }
}
