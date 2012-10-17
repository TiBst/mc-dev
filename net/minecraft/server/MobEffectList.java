package net.minecraft.server;

public class MobEffectList {

    public static final MobEffectList[] byId = new MobEffectList[32];
    public static final MobEffectList b = null;
    public static final MobEffectList FASTER_MOVEMENT = (new MobEffectList(1, false, 8171462)).b("potion.moveSpeed").b(0, 0);
    public static final MobEffectList SLOWER_MOVEMENT = (new MobEffectList(2, true, 5926017)).b("potion.moveSlowdown").b(1, 0);
    public static final MobEffectList FASTER_DIG = (new MobEffectList(3, false, 14270531)).b("potion.digSpeed").b(2, 0).a(1.5D);
    public static final MobEffectList SLOWER_DIG = (new MobEffectList(4, true, 4866583)).b("potion.digSlowDown").b(3, 0);
    public static final MobEffectList INCREASE_DAMAGE = (new MobEffectList(5, false, 9643043)).b("potion.damageBoost").b(4, 0);
    public static final MobEffectList HEAL = (new InstantMobEffect(6, false, 16262179)).b("potion.heal");
    public static final MobEffectList HARM = (new InstantMobEffect(7, true, 4393481)).b("potion.harm");
    public static final MobEffectList JUMP = (new MobEffectList(8, false, 7889559)).b("potion.jump").b(2, 1);
    public static final MobEffectList CONFUSION = (new MobEffectList(9, true, 5578058)).b("potion.confusion").b(3, 1).a(0.25D);
    public static final MobEffectList REGENERATION = (new MobEffectList(10, false, 13458603)).b("potion.regeneration").b(7, 0).a(0.25D);
    public static final MobEffectList RESISTANCE = (new MobEffectList(11, false, 10044730)).b("potion.resistance").b(6, 1);
    public static final MobEffectList FIRE_RESISTANCE = (new MobEffectList(12, false, 14981690)).b("potion.fireResistance").b(7, 1);
    public static final MobEffectList WATER_BREATHING = (new MobEffectList(13, false, 3035801)).b("potion.waterBreathing").b(0, 2);
    public static final MobEffectList INVISIBILITY = (new MobEffectList(14, false, 8356754)).b("potion.invisibility").b(0, 1);
    public static final MobEffectList BLINDNESS = (new MobEffectList(15, true, 2039587)).b("potion.blindness").b(5, 1).a(0.25D);
    public static final MobEffectList NIGHT_VISION = (new MobEffectList(16, false, 2039713)).b("potion.nightVision").b(4, 1);
    public static final MobEffectList HUNGER = (new MobEffectList(17, true, 5797459)).b("potion.hunger").b(1, 1);
    public static final MobEffectList WEAKNESS = (new MobEffectList(18, true, 4738376)).b("potion.weakness").b(5, 0);
    public static final MobEffectList POISON = (new MobEffectList(19, true, 5149489)).b("potion.poison").b(6, 0).a(0.25D);
    public static final MobEffectList WITHER = (new MobEffectList(20, true, 3484199)).b("potion.wither").b(1, 2).a(0.25D);
    public static final MobEffectList w = null;
    public static final MobEffectList x = null;
    public static final MobEffectList y = null;
    public static final MobEffectList z = null;
    public static final MobEffectList A = null;
    public static final MobEffectList B = null;
    public static final MobEffectList C = null;
    public static final MobEffectList D = null;
    public static final MobEffectList E = null;
    public static final MobEffectList F = null;
    public static final MobEffectList G = null;
    public final int id;
    private String I = "";
    private int J = -1;
    private final boolean K;
    private double L;
    private boolean M;
    private final int N;

    protected MobEffectList(int i, boolean flag, int j) {
        this.id = i;
        byId[i] = this;
        this.K = flag;
        if (flag) {
            this.L = 0.5D;
        } else {
            this.L = 1.0D;
        }

        this.N = j;
    }

    protected MobEffectList b(int i, int j) {
        this.J = i + j * 8;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public void tick(EntityLiving entityliving, int i) {
        if (this.id == REGENERATION.id) {
            if (entityliving.getHealth() < entityliving.getMaxHealth()) {
                entityliving.heal(1);
            }
        } else if (this.id == POISON.id) {
            if (entityliving.getHealth() > 1) {
                entityliving.damageEntity(DamageSource.MAGIC, 1);
            }
        } else if (this.id == WITHER.id) {
            entityliving.damageEntity(DamageSource.WITHER, 1);
        } else if (this.id == HUNGER.id && entityliving instanceof EntityHuman) {
            ((EntityHuman) entityliving).j(0.025F * (float) (i + 1));
        } else if ((this.id != HEAL.id || entityliving.bx()) && (this.id != HARM.id || !entityliving.bx())) {
            if (this.id == HARM.id && !entityliving.bx() || this.id == HEAL.id && entityliving.bx()) {
                entityliving.damageEntity(DamageSource.MAGIC, 6 << i);
            }
        } else {
            entityliving.heal(6 << i);
        }
    }

    public void applyInstantEffect(EntityLiving entityliving, EntityLiving entityliving1, int i, double d0) {
        int j;

        if ((this.id != HEAL.id || entityliving1.bx()) && (this.id != HARM.id || !entityliving1.bx())) {
            if (this.id == HARM.id && !entityliving1.bx() || this.id == HEAL.id && entityliving1.bx()) {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entityliving == null) {
                    entityliving1.damageEntity(DamageSource.MAGIC, j);
                } else {
                    entityliving1.damageEntity(DamageSource.b(entityliving1, entityliving), j);
                }
            }
        } else {
            j = (int) (d0 * (double) (6 << i) + 0.5D);
            entityliving1.heal(j);
        }
    }

    public boolean isInstant() {
        return false;
    }

    public boolean a(int i, int j) {
        int k;

        if (this.id != REGENERATION.id && this.id != POISON.id) {
            if (this.id == WITHER.id) {
                k = 40 >> j;
                return k > 0 ? i % k == 0 : true;
            } else {
                return this.id == HUNGER.id;
            }
        } else {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        }
    }

    public MobEffectList b(String s) {
        this.I = s;
        return this;
    }

    public String a() {
        return this.I;
    }

    protected MobEffectList a(double d0) {
        this.L = d0;
        return this;
    }

    public double getDurationModifier() {
        return this.L;
    }

    public boolean i() {
        return this.M;
    }

    public int j() {
        return this.N;
    }
}
