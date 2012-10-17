package net.minecraft.server;

public class ItemFood extends Item {

    public final int a;
    private final int b;
    private final float c;
    private final boolean ck;
    private boolean cl;
    private int cm;
    private int cn;
    private int co;
    private float cp;

    public ItemFood(int i, int j, float f, boolean flag) {
        super(i);
        this.a = 32;
        this.b = j;
        this.ck = flag;
        this.c = f;
        this.a(CreativeModeTab.h);
    }

    public ItemFood(int i, int j, boolean flag) {
        this(i, j, 0.6F, flag);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        --itemstack.count;
        entityhuman.getFoodData().a(this);
        world.makeSound(entityhuman, "random.burp", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        this.c(itemstack, world, entityhuman);
        return itemstack;
    }

    protected void c(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!world.isStatic && this.cm > 0 && world.random.nextFloat() < this.cp) {
            entityhuman.addEffect(new MobEffect(this.cm, this.cn * 20, this.co));
        }
    }

    public int a(ItemStack itemstack) {
        return 32;
    }

    public EnumAnimation d_(ItemStack itemstack) {
        return EnumAnimation.b;
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (entityhuman.f(this.cl)) {
            entityhuman.a(itemstack, this.a(itemstack));
        }

        return itemstack;
    }

    public int getNutrition() {
        return this.b;
    }

    public float getSaturationModifier() {
        return this.c;
    }

    public boolean i() {
        return this.ck;
    }

    public ItemFood a(int i, int j, int k, float f) {
        this.cm = i;
        this.cn = j;
        this.co = k;
        this.cp = f;
        return this;
    }

    public ItemFood j() {
        this.cl = true;
        return this;
    }
}
