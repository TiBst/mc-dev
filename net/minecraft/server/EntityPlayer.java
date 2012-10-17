package net.minecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EntityPlayer extends EntityHuman implements ICrafting {

    private LocaleLanguage locale = new LocaleLanguage("en_US");
    public NetServerHandler netServerHandler;
    public MinecraftServer server;
    public ItemInWorldManager itemInWorldManager;
    public double d;
    public double e;
    public final List chunkCoordIntPairQueue = new LinkedList();
    public final List removeQueue = new LinkedList();
    private int cn = -99999999;
    private int co = -99999999;
    private boolean cp = true;
    private int lastSentExp = -99999999;
    private int invulnerableTicks = 60;
    private int cs = 0;
    private int ct = 0;
    private boolean cu = true;
    private int containerCounter = 0;
    public boolean h;
    public int ping;
    public boolean viewingCredits = false;

    public EntityPlayer(MinecraftServer minecraftserver, World world, String s, ItemInWorldManager iteminworldmanager) {
        super(world);
        iteminworldmanager.player = this;
        this.itemInWorldManager = iteminworldmanager;
        this.cs = minecraftserver.getServerConfigurationManager().o();
        ChunkCoordinates chunkcoordinates = world.getSpawn();
        int i = chunkcoordinates.x;
        int j = chunkcoordinates.z;
        int k = chunkcoordinates.y;

        if (!world.worldProvider.f && world.getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
            int l = Math.max(5, minecraftserver.getSpawnProtection() - 6);

            i += this.random.nextInt(l * 2) - l;
            j += this.random.nextInt(l * 2) - l;
            k = world.i(i, j);
        }

        this.setPositionRotation((double) i + 0.5D, (double) k, (double) j + 0.5D, 0.0F, 0.0F);
        this.server = minecraftserver;
        this.X = 0.0F;
        this.name = s;
        this.height = 0.0F;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("playerGameType")) {
            this.itemInWorldManager.setGameMode(EnumGamemode.a(nbttagcompound.getInt("playerGameType")));
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("playerGameType", this.itemInWorldManager.getGameMode().a());
    }

    public void levelDown(int i) {
        super.levelDown(i);
        this.lastSentExp = -1;
    }

    public void syncInventory() {
        this.activeContainer.addSlotListener(this);
    }

    protected void e_() {
        this.height = 0.0F;
    }

    public float getHeadHeight() {
        return 1.62F;
    }

    public void j_() {
        this.itemInWorldManager.a();
        --this.invulnerableTicks;
        this.activeContainer.b();
        if (!this.chunkCoordIntPairQueue.isEmpty()) {
            ArrayList arraylist = new ArrayList();
            Iterator iterator = this.chunkCoordIntPairQueue.iterator();
            ArrayList arraylist1 = new ArrayList();

            while (iterator.hasNext() && arraylist.size() < 5) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();

                iterator.remove();
                if (chunkcoordintpair != null && this.world.isLoaded(chunkcoordintpair.x << 4, 0, chunkcoordintpair.z << 4)) {
                    arraylist.add(this.world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z));
                    arraylist1.addAll(((WorldServer) this.world).getTileEntities(chunkcoordintpair.x * 16, 0, chunkcoordintpair.z * 16, chunkcoordintpair.x * 16 + 16, 256, chunkcoordintpair.z * 16 + 16));
                }
            }

            if (!arraylist.isEmpty()) {
                this.netServerHandler.sendPacket(new Packet56MapChunkBulk(arraylist));
                Iterator iterator1 = arraylist1.iterator();

                while (iterator1.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator1.next();

                    this.b(tileentity);
                }
            }
        }

        if (!this.removeQueue.isEmpty()) {
            int i = Math.min(this.removeQueue.size(), 127);
            int[] aint = new int[i];
            Iterator iterator2 = this.removeQueue.iterator();
            int j = 0;

            while (iterator2.hasNext() && j < i) {
                aint[j++] = ((Integer) iterator2.next()).intValue();
                iterator2.remove();
            }

            this.netServerHandler.sendPacket(new Packet29DestroyEntity(aint));
        }
    }

    public void g() {
        super.j_();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (itemstack != null && Item.byId[itemstack.id].f() && this.netServerHandler.lowPriorityCount() <= 5) {
                Packet packet = ((ItemWorldMapBase) Item.byId[itemstack.id]).c(itemstack, this.world, this);

                if (packet != null) {
                    this.netServerHandler.sendPacket(packet);
                }
            }
        }

        if (this.getHealth() != this.cn || this.co != this.foodData.a() || this.foodData.e() == 0.0F != this.cp) {
            this.netServerHandler.sendPacket(new Packet8UpdateHealth(this.getHealth(), this.foodData.a(), this.foodData.e()));
            this.cn = this.getHealth();
            this.co = this.foodData.a();
            this.cp = this.foodData.e() == 0.0F;
        }

        if (this.expTotal != this.lastSentExp) {
            this.lastSentExp = this.expTotal;
            this.netServerHandler.sendPacket(new Packet43SetExperience(this.exp, this.expTotal, this.expLevel));
        }
    }

    public void die(DamageSource damagesource) {
        this.server.getServerConfigurationManager().sendAll(new Packet3Chat(damagesource.getLocalizedDeathMessage(this)));
        if (!this.world.getGameRules().getBoolean("keepInventory")) {
            this.inventory.l();
        }
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        if (this.invulnerableTicks > 0) {
            return false;
        } else {
            if (!this.server.getPvP() && damagesource instanceof EntityDamageSource) {
                Entity entity = damagesource.getEntity();

                if (entity instanceof EntityHuman) {
                    return false;
                }

                if (entity instanceof EntityArrow) {
                    EntityArrow entityarrow = (EntityArrow) entity;

                    if (entityarrow.shooter instanceof EntityHuman) {
                        return false;
                    }
                }
            }

            return super.damageEntity(damagesource, i);
        }
    }

    protected boolean h() {
        return this.server.getPvP();
    }

    public void b(int i) {
        if (this.dimension == 1 && i == 1) {
            this.a((Statistic) AchievementList.C);
            this.world.kill(this);
            this.viewingCredits = true;
            this.netServerHandler.sendPacket(new Packet70Bed(4, 0));
        } else {
            if (this.dimension == 1 && i == 0) {
                this.a((Statistic) AchievementList.B);
                ChunkCoordinates chunkcoordinates = this.server.getWorldServer(i).getDimensionSpawn();

                if (chunkcoordinates != null) {
                    this.netServerHandler.a((double) chunkcoordinates.x, (double) chunkcoordinates.y, (double) chunkcoordinates.z, 0.0F, 0.0F);
                }

                i = 1;
            } else {
                this.a((Statistic) AchievementList.x);
            }

            this.server.getServerConfigurationManager().changeDimension(this, i);
            this.lastSentExp = -1;
            this.cn = -1;
            this.co = -1;
        }
    }

    private void b(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.l();

            if (packet != null) {
                this.netServerHandler.sendPacket(packet);
            }
        }
    }

    public void receive(Entity entity, int i) {
        super.receive(entity, i);
        this.activeContainer.b();
    }

    public EnumBedResult a(int i, int j, int k) {
        EnumBedResult enumbedresult = super.a(i, j, k);

        if (enumbedresult == EnumBedResult.OK) {
            Packet17EntityLocationAction packet17entitylocationaction = new Packet17EntityLocationAction(this, 0, i, j, k);

            this.p().getTracker().a(this, packet17entitylocationaction);
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.netServerHandler.sendPacket(packet17entitylocationaction);
        }

        return enumbedresult;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (this.isSleeping()) {
            this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(this, 3));
        }

        super.a(flag, flag1, flag2);
        if (this.netServerHandler != null) {
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }
    }

    public void mount(Entity entity) {
        super.mount(entity);
        this.netServerHandler.sendPacket(new Packet39AttachEntity(this, this.vehicle));
        this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
    }

    protected void a(double d0, boolean flag) {}

    public void b(double d0, boolean flag) {
        super.a(d0, flag);
    }

    private void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    public void startCrafting(int i, int j, int k) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 1, "Crafting", 9));
        this.activeContainer = new ContainerWorkbench(this.inventory, this.world, i, j, k);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void startEnchanting(int i, int j, int k) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 4, "Enchanting", 9));
        this.activeContainer = new ContainerEnchantTable(this.inventory, this.world, i, j, k);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openAnvil(int i, int j, int k) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 8, "Repairing", 9));
        this.activeContainer = new ContainerAnvil(this.inventory, this.world, i, j, k, this);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openContainer(IInventory iinventory) {
        if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
        }

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 0, iinventory.getName(), iinventory.getSize()));
        this.activeContainer = new ContainerChest(this.inventory, iinventory);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openFurnace(TileEntityFurnace tileentityfurnace) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 2, tileentityfurnace.getName(), tileentityfurnace.getSize()));
        this.activeContainer = new ContainerFurnace(this.inventory, tileentityfurnace);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openDispenser(TileEntityDispenser tileentitydispenser) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 3, tileentitydispenser.getName(), tileentitydispenser.getSize()));
        this.activeContainer = new ContainerDispenser(this.inventory, tileentitydispenser);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 5, tileentitybrewingstand.getName(), tileentitybrewingstand.getSize()));
        this.activeContainer = new ContainerBrewingStand(this.inventory, tileentitybrewingstand);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openBeacon(TileEntityBeacon tileentitybeacon) {
        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 7, tileentitybeacon.getName(), tileentitybeacon.getSize()));
        this.activeContainer = new ContainerBeacon(this.inventory, tileentitybeacon);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openTrade(IMerchant imerchant) {
        this.nextContainerCounter();
        this.activeContainer = new ContainerMerchant(this.inventory, imerchant, this.world);
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
        InventoryMerchant inventorymerchant = ((ContainerMerchant) this.activeContainer).getMerchantInventory();

        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 6, inventorymerchant.getName(), inventorymerchant.getSize()));
        MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);

        if (merchantrecipelist != null) {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

                dataoutputstream.writeInt(this.containerCounter);
                merchantrecipelist.a(dataoutputstream);
                this.netServerHandler.sendPacket(new Packet250CustomPayload("MC|TrList", bytearrayoutputstream.toByteArray()));
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public void a(Container container, int i, ItemStack itemstack) {
        if (!(container.getSlot(i) instanceof SlotResult)) {
            if (!this.h) {
                this.netServerHandler.sendPacket(new Packet103SetSlot(container.windowId, i, itemstack));
            }
        }
    }

    public void updateInventory(Container container) {
        this.a(container, container.a());
    }

    public void a(Container container, List list) {
        this.netServerHandler.sendPacket(new Packet104WindowItems(container.windowId, list));
        this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getCarried()));
    }

    public void setContainerData(Container container, int i, int j) {
        this.netServerHandler.sendPacket(new Packet105CraftProgressBar(container.windowId, i, j));
    }

    public void closeInventory() {
        this.netServerHandler.sendPacket(new Packet101CloseWindow(this.activeContainer.windowId));
        this.k();
    }

    public void broadcastCarriedItem() {
        if (!this.h) {
            this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getCarried()));
        }
    }

    public void k() {
        this.activeContainer.a((EntityHuman) this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(Statistic statistic, int i) {
        if (statistic != null) {
            if (!statistic.f) {
                while (i > 100) {
                    this.netServerHandler.sendPacket(new Packet200Statistic(statistic.e, 100));
                    i -= 100;
                }

                this.netServerHandler.sendPacket(new Packet200Statistic(statistic.e, i));
            }
        }
    }

    public void l() {
        if (this.vehicle != null) {
            this.mount(this.vehicle);
        }

        if (this.passenger != null) {
            this.passenger.mount(this);
        }

        if (this.sleeping) {
            this.a(true, false, false);
        }
    }

    public void m() {
        this.cn = -99999999;
    }

    public void b(String s) {
        LocaleLanguage localelanguage = LocaleLanguage.a();
        String s1 = localelanguage.b(s);

        this.netServerHandler.sendPacket(new Packet3Chat(s1));
    }

    protected void n() {
        this.netServerHandler.sendPacket(new Packet38EntityStatus(this.id, (byte) 9));
        super.n();
    }

    public void a(ItemStack itemstack, int i) {
        super.a(itemstack, i);
        if (itemstack != null && itemstack.getItem() != null && itemstack.getItem().d_(itemstack) == EnumAnimation.b) {
            this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(this, 5));
        }
    }

    public void copyTo(EntityHuman entityhuman, boolean flag) {
        super.copyTo(entityhuman, flag);
        this.lastSentExp = -1;
        this.cn = -1;
        this.co = -1;
        this.removeQueue.addAll(((EntityPlayer) entityhuman).removeQueue);
    }

    protected void a(MobEffect mobeffect) {
        super.a(mobeffect);
        this.netServerHandler.sendPacket(new Packet41MobEffect(this.id, mobeffect));
    }

    protected void b(MobEffect mobeffect) {
        super.b(mobeffect);
        this.netServerHandler.sendPacket(new Packet41MobEffect(this.id, mobeffect));
    }

    protected void c(MobEffect mobeffect) {
        super.c(mobeffect);
        this.netServerHandler.sendPacket(new Packet42RemoveMobEffect(this.id, mobeffect));
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.netServerHandler.a(d0, d1, d2, this.yaw, this.pitch);
    }

    public void b(Entity entity) {
        this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(entity, 6));
    }

    public void c(Entity entity) {
        this.p().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(entity, 7));
    }

    public void updateAbilities() {
        if (this.netServerHandler != null) {
            this.netServerHandler.sendPacket(new Packet202Abilities(this.abilities));
        }
    }

    public WorldServer p() {
        return (WorldServer) this.world;
    }

    public void a(EnumGamemode enumgamemode) {
        this.itemInWorldManager.setGameMode(enumgamemode);
        this.netServerHandler.sendPacket(new Packet70Bed(3, enumgamemode.a()));
    }

    public void sendMessage(String s) {
        this.netServerHandler.sendPacket(new Packet3Chat(s));
    }

    public boolean a(int i, String s) {
        return "seed".equals(s) && !this.server.T() ? true : (!"tell".equals(s) && !"help".equals(s) && !"me".equals(s) ? this.server.getServerConfigurationManager().isOp(this.name) : true);
    }

    public String q() {
        String s = this.netServerHandler.networkManager.getSocketAddress().toString();

        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void a(Packet204LocaleAndViewDistance packet204localeandviewdistance) {
        if (this.locale.b().containsKey(packet204localeandviewdistance.d())) {
            this.locale.a(packet204localeandviewdistance.d());
        }

        int i = 256 >> packet204localeandviewdistance.f();

        if (i > 3 && i < 15) {
            this.cs = i;
        }

        this.ct = packet204localeandviewdistance.g();
        this.cu = packet204localeandviewdistance.h();
        if (this.server.I() && this.server.H().equals(this.name)) {
            this.server.c(packet204localeandviewdistance.i());
        }

        this.b(1, !packet204localeandviewdistance.j());
    }

    public LocaleLanguage getLocale() {
        return this.locale;
    }

    public int getChatFlags() {
        return this.ct;
    }

    public void a(String s, int i) {
        String s1 = s + " + i;

        this.netServerHandler.sendPacket(new Packet250CustomPayload("MC|TPack", s1.getBytes()));
    }

    public ChunkCoordinates b() {
        return new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY + 0.5D), MathHelper.floor(this.locZ));
    }
}
