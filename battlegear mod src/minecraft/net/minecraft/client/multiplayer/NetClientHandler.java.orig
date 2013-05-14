package net.minecraft.client.multiplayer;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenDisconnectedOnline;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet104WindowItems;
import net.minecraft.network.packet.Packet105UpdateProgressbar;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet200Statistic;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet203AutoComplete;
import net.minecraft.network.packet.Packet205ClientCommand;
import net.minecraft.network.packet.Packet206SetObjective;
import net.minecraft.network.packet.Packet207SetScore;
import net.minecraft.network.packet.Packet208SetDisplayObjective;
import net.minecraft.network.packet.Packet209SetPlayerTeam;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet252SharedKey;
import net.minecraft.network.packet.Packet253ServerAuthData;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet26EntityExpOrb;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet35EntityHeadRotation;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet40EntityMetadata;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet42RemoveEntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet52MultiBlockChange;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet55BlockDestroy;
import net.minecraft.network.packet.Packet56MapChunks;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet62LevelSound;
import net.minecraft.network.packet.Packet63WorldParticles;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.network.packet.Packet8UpdateHealth;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringTranslate;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import org.lwjgl.input.Keyboard;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class NetClientHandler extends NetHandler
{
    /** True if kicked or disconnected from the server. */
    private boolean disconnected = false;

    /** Reference to the NetworkManager object. */
    private INetworkManager netManager;
    public String field_72560_a;

    /** Reference to the Minecraft object. */
    private Minecraft mc;
    private WorldClient worldClient;

    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of a player position packet,
     * reset upon respawning.
     */
    private boolean doneLoadingTerrain = false;
    public MapStorage mapStorage = new MapStorage((ISaveHandler)null);

    /** A HashMap of all player names and their player information objects */
    private Map playerInfoMap = new HashMap();

    /**
     * An ArrayList of GuiPlayerInfo (includes all the players' GuiPlayerInfo on the current server)
     */
    public List playerInfoList = new ArrayList();
    public int currentServerMaxPlayers = 20;
    private GuiScreen field_98183_l = null;

    /** RNG. */
    Random rand = new Random();

    private static byte connectionCompatibilityLevel;

    public NetClientHandler(Minecraft par1Minecraft, String par2Str, int par3) throws IOException
    {
        this.mc = par1Minecraft;
        Socket socket = new Socket(InetAddress.getByName(par2Str), par3);
        this.netManager = new TcpConnection(par1Minecraft.getLogAgent(), socket, "Client", this);
        FMLNetworkHandler.onClientConnectionToRemoteServer(this, par2Str, par3, this.netManager);
    }

    public NetClientHandler(Minecraft par1Minecraft, String par2Str, int par3, GuiScreen par4GuiScreen) throws IOException
    {
        this.mc = par1Minecraft;
        this.field_98183_l = par4GuiScreen;
        Socket socket = new Socket(InetAddress.getByName(par2Str), par3);
        this.netManager = new TcpConnection(par1Minecraft.getLogAgent(), socket, "Client", this);
        FMLNetworkHandler.onClientConnectionToRemoteServer(this, par2Str, par3, this.netManager);
    }

    public NetClientHandler(Minecraft par1Minecraft, IntegratedServer par2IntegratedServer) throws IOException
    {
        this.mc = par1Minecraft;
        this.netManager = new MemoryConnection(par1Minecraft.getLogAgent(), this);
        par2IntegratedServer.getServerListeningThread().func_71754_a((MemoryConnection)this.netManager, par1Minecraft.session.username);
        FMLNetworkHandler.onClientConnectionToIntegratedServer(this, par2IntegratedServer, this.netManager);
    }

    /**
     * sets netManager and worldClient to null
     */
    public void cleanup()
    {
        if (this.netManager != null)
        {
            this.netManager.wakeThreads();
        }

        this.netManager = null;
        this.worldClient = null;
    }

    /**
     * Processes the packets that have been read since the last call to this function.
     */
    public void processReadPackets()
    {
        if (!this.disconnected && this.netManager != null)
        {
            this.netManager.processReadPackets();
        }

        if (this.netManager != null)
        {
            this.netManager.wakeThreads();
        }
    }

    public void handleServerAuthData(Packet253ServerAuthData par1Packet253ServerAuthData)
    {
        String s = par1Packet253ServerAuthData.getServerId().trim();
        PublicKey publickey = par1Packet253ServerAuthData.getPublicKey();
        SecretKey secretkey = CryptManager.createNewSharedKey();

        if (!"-".equals(s))
        {
            String s1 = (new BigInteger(CryptManager.getServerIdHash(s, publickey, secretkey))).toString(16);
            String s2 = this.sendSessionRequest(this.mc.session.username, this.mc.session.sessionId, s1);

            if (!"ok".equalsIgnoreCase(s2))
            {
                this.netManager.networkShutdown("disconnect.loginFailedInfo", new Object[] {s2});
                return;
            }
        }

        this.addToSendQueue(new Packet252SharedKey(secretkey, publickey, par1Packet253ServerAuthData.getVerifyToken()));
    }

    /**
     * Send request to http://session.minecraft.net with user's sessionId and serverId hash
     */
    private String sendSessionRequest(String par1Str, String par2Str, String par3Str)
    {
        try
        {
            URL url = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + urlEncode(par1Str) + "&sessionId=" + urlEncode(par2Str) + "&serverId=" + urlEncode(par3Str));
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s3 = bufferedreader.readLine();
            bufferedreader.close();
            return s3;
        }
        catch (IOException ioexception)
        {
            return ioexception.toString();
        }
    }

    /**
     * Encode the given string for insertion into a URL
     */
    private static String urlEncode(String par0Str) throws IOException
    {
        return URLEncoder.encode(par0Str, "UTF-8");
    }

    public void handleSharedKey(Packet252SharedKey par1Packet252SharedKey)
    {
        this.addToSendQueue(FMLNetworkHandler.getFMLFakeLoginPacket());
        this.addToSendQueue(new Packet205ClientCommand(0));
    }

    public void handleLogin(Packet1Login par1Packet1Login)
    {
        this.mc.playerController = new PlayerControllerMP(this.mc, this);
        this.mc.statFileWriter.readStat(StatList.joinMultiplayerStat, 1);
        this.worldClient = new WorldClient(this, new WorldSettings(0L, par1Packet1Login.gameType, false, par1Packet1Login.hardcoreMode, par1Packet1Login.terrainType), par1Packet1Login.dimension, par1Packet1Login.difficultySetting, this.mc.mcProfiler, this.mc.getLogAgent());
        this.worldClient.isRemote = true;
        this.mc.loadWorld(this.worldClient);
        this.mc.thePlayer.dimension = par1Packet1Login.dimension;
        this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
        this.mc.thePlayer.entityId = par1Packet1Login.clientEntityId;
        this.currentServerMaxPlayers = par1Packet1Login.maxPlayers;
        this.mc.playerController.setGameType(par1Packet1Login.gameType);
        FMLNetworkHandler.onConnectionEstablishedToServer(this, netManager, par1Packet1Login);
        this.mc.gameSettings.sendSettingsToServer();
    }

    public void handleVehicleSpawn(Packet23VehicleSpawn par1Packet23VehicleSpawn)
    {
        double d0 = (double)par1Packet23VehicleSpawn.xPosition / 32.0D;
        double d1 = (double)par1Packet23VehicleSpawn.yPosition / 32.0D;
        double d2 = (double)par1Packet23VehicleSpawn.zPosition / 32.0D;
        Object object = null;

        if (par1Packet23VehicleSpawn.type == 10)
        {
            object = EntityMinecart.createMinecart(this.worldClient, d0, d1, d2, par1Packet23VehicleSpawn.throwerEntityId);
        }
        else if (par1Packet23VehicleSpawn.type == 90)
        {
            Entity entity = this.getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);

            if (entity instanceof EntityPlayer)
            {
                object = new EntityFishHook(this.worldClient, d0, d1, d2, (EntityPlayer)entity);
            }

            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 60)
        {
            object = new EntityArrow(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 61)
        {
            object = new EntitySnowball(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 71)
        {
            object = new EntityItemFrame(this.worldClient, (int)d0, (int)d1, (int)d2, par1Packet23VehicleSpawn.throwerEntityId);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 65)
        {
            object = new EntityEnderPearl(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 72)
        {
            object = new EntityEnderEye(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 76)
        {
            object = new EntityFireworkRocket(this.worldClient, d0, d1, d2, (ItemStack)null);
        }
        else if (par1Packet23VehicleSpawn.type == 63)
        {
            object = new EntityLargeFireball(this.worldClient, d0, d1, d2, (double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 64)
        {
            object = new EntitySmallFireball(this.worldClient, d0, d1, d2, (double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 66)
        {
            object = new EntityWitherSkull(this.worldClient, d0, d1, d2, (double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 62)
        {
            object = new EntityEgg(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 73)
        {
            object = new EntityPotion(this.worldClient, d0, d1, d2, par1Packet23VehicleSpawn.throwerEntityId);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 75)
        {
            object = new EntityExpBottle(this.worldClient, d0, d1, d2);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 1)
        {
            object = new EntityBoat(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 50)
        {
            object = new EntityTNTPrimed(this.worldClient, d0, d1, d2, (EntityLiving)null);
        }
        else if (par1Packet23VehicleSpawn.type == 51)
        {
            object = new EntityEnderCrystal(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 2)
        {
            object = new EntityItem(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 70)
        {
            object = new EntityFallingSand(this.worldClient, d0, d1, d2, par1Packet23VehicleSpawn.throwerEntityId & 65535, par1Packet23VehicleSpawn.throwerEntityId >> 16);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }

        if (object != null)
        {
            ((Entity)object).serverPosX = par1Packet23VehicleSpawn.xPosition;
            ((Entity)object).serverPosY = par1Packet23VehicleSpawn.yPosition;
            ((Entity)object).serverPosZ = par1Packet23VehicleSpawn.zPosition;
            ((Entity)object).rotationPitch = (float)(par1Packet23VehicleSpawn.pitch * 360) / 256.0F;
            ((Entity)object).rotationYaw = (float)(par1Packet23VehicleSpawn.yaw * 360) / 256.0F;
            Entity[] aentity = ((Entity)object).getParts();

            if (aentity != null)
            {
                int i = par1Packet23VehicleSpawn.entityId - ((Entity)object).entityId;

                for (int j = 0; j < aentity.length; ++j)
                {
                    aentity[j].entityId += i;
                }
            }

            ((Entity)object).entityId = par1Packet23VehicleSpawn.entityId;
            this.worldClient.addEntityToWorld(par1Packet23VehicleSpawn.entityId, (Entity)object);

            if (par1Packet23VehicleSpawn.throwerEntityId > 0)
            {
                if (par1Packet23VehicleSpawn.type == 60)
                {
                    Entity entity1 = this.getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);

                    if (entity1 instanceof EntityLiving)
                    {
                        EntityArrow entityarrow = (EntityArrow)object;
                        entityarrow.shootingEntity = entity1;
                    }
                }

                ((Entity)object).setVelocity((double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            }
        }
    }

    /**
     * Handle a entity experience orb packet.
     */
    public void handleEntityExpOrb(Packet26EntityExpOrb par1Packet26EntityExpOrb)
    {
        EntityXPOrb entityxporb = new EntityXPOrb(this.worldClient, (double)par1Packet26EntityExpOrb.posX, (double)par1Packet26EntityExpOrb.posY, (double)par1Packet26EntityExpOrb.posZ, par1Packet26EntityExpOrb.xpValue);
        entityxporb.serverPosX = par1Packet26EntityExpOrb.posX;
        entityxporb.serverPosY = par1Packet26EntityExpOrb.posY;
        entityxporb.serverPosZ = par1Packet26EntityExpOrb.posZ;
        entityxporb.rotationYaw = 0.0F;
        entityxporb.rotationPitch = 0.0F;
        entityxporb.entityId = par1Packet26EntityExpOrb.entityId;
        this.worldClient.addEntityToWorld(par1Packet26EntityExpOrb.entityId, entityxporb);
    }

    /**
     * Handles weather packet
     */
    public void handleWeather(Packet71Weather par1Packet71Weather)
    {
        double d0 = (double)par1Packet71Weather.posX / 32.0D;
        double d1 = (double)par1Packet71Weather.posY / 32.0D;
        double d2 = (double)par1Packet71Weather.posZ / 32.0D;
        EntityLightningBolt entitylightningbolt = null;

        if (par1Packet71Weather.isLightningBolt == 1)
        {
            entitylightningbolt = new EntityLightningBolt(this.worldClient, d0, d1, d2);
        }

        if (entitylightningbolt != null)
        {
            entitylightningbolt.serverPosX = par1Packet71Weather.posX;
            entitylightningbolt.serverPosY = par1Packet71Weather.posY;
            entitylightningbolt.serverPosZ = par1Packet71Weather.posZ;
            entitylightningbolt.rotationYaw = 0.0F;
            entitylightningbolt.rotationPitch = 0.0F;
            entitylightningbolt.entityId = par1Packet71Weather.entityID;
            this.worldClient.addWeatherEffect(entitylightningbolt);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityPainting(Packet25EntityPainting par1Packet25EntityPainting)
    {
        EntityPainting entitypainting = new EntityPainting(this.worldClient, par1Packet25EntityPainting.xPosition, par1Packet25EntityPainting.yPosition, par1Packet25EntityPainting.zPosition, par1Packet25EntityPainting.direction, par1Packet25EntityPainting.title);
        this.worldClient.addEntityToWorld(par1Packet25EntityPainting.entityId, entitypainting);
    }

    /**
     * Packet handler
     */
    public void handleEntityVelocity(Packet28EntityVelocity par1Packet28EntityVelocity)
    {
        Entity entity = this.getEntityByID(par1Packet28EntityVelocity.entityId);

        if (entity != null)
        {
            entity.setVelocity((double)par1Packet28EntityVelocity.motionX / 8000.0D, (double)par1Packet28EntityVelocity.motionY / 8000.0D, (double)par1Packet28EntityVelocity.motionZ / 8000.0D);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityMetadata(Packet40EntityMetadata par1Packet40EntityMetadata)
    {
        Entity entity = this.getEntityByID(par1Packet40EntityMetadata.entityId);

        if (entity != null && par1Packet40EntityMetadata.getMetadata() != null)
        {
            entity.getDataWatcher().updateWatchedObjectsFromList(par1Packet40EntityMetadata.getMetadata());
        }
    }

    public void handleNamedEntitySpawn(Packet20NamedEntitySpawn par1Packet20NamedEntitySpawn)
    {
        double d0 = (double)par1Packet20NamedEntitySpawn.xPosition / 32.0D;
        double d1 = (double)par1Packet20NamedEntitySpawn.yPosition / 32.0D;
        double d2 = (double)par1Packet20NamedEntitySpawn.zPosition / 32.0D;
        float f = (float)(par1Packet20NamedEntitySpawn.rotation * 360) / 256.0F;
        float f1 = (float)(par1Packet20NamedEntitySpawn.pitch * 360) / 256.0F;
        EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.mc.theWorld, par1Packet20NamedEntitySpawn.name);
        entityotherplayermp.prevPosX = entityotherplayermp.lastTickPosX = (double)(entityotherplayermp.serverPosX = par1Packet20NamedEntitySpawn.xPosition);
        entityotherplayermp.prevPosY = entityotherplayermp.lastTickPosY = (double)(entityotherplayermp.serverPosY = par1Packet20NamedEntitySpawn.yPosition);
        entityotherplayermp.prevPosZ = entityotherplayermp.lastTickPosZ = (double)(entityotherplayermp.serverPosZ = par1Packet20NamedEntitySpawn.zPosition);
        int i = par1Packet20NamedEntitySpawn.currentItem;

        if (i == 0)
        {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = null;
        }
        else
        {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = new ItemStack(i, 1, 0);
        }

        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.worldClient.addEntityToWorld(par1Packet20NamedEntitySpawn.entityId, entityotherplayermp);
        List list = par1Packet20NamedEntitySpawn.getWatchedMetadata();

        if (list != null)
        {
            entityotherplayermp.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleEntityTeleport(Packet34EntityTeleport par1Packet34EntityTeleport)
    {
        Entity entity = this.getEntityByID(par1Packet34EntityTeleport.entityId);

        if (entity != null)
        {
            entity.serverPosX = par1Packet34EntityTeleport.xPosition;
            entity.serverPosY = par1Packet34EntityTeleport.yPosition;
            entity.serverPosZ = par1Packet34EntityTeleport.zPosition;
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D + 0.015625D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = (float)(par1Packet34EntityTeleport.yaw * 360) / 256.0F;
            float f1 = (float)(par1Packet34EntityTeleport.pitch * 360) / 256.0F;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3);
        }
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize())
        {
            this.mc.thePlayer.inventory.currentItem = par1Packet16BlockItemSwitch.id;
        }
    }

    public void handleEntity(Packet30Entity par1Packet30Entity)
    {
        Entity entity = this.getEntityByID(par1Packet30Entity.entityId);

        if (entity != null)
        {
            entity.serverPosX += par1Packet30Entity.xPosition;
            entity.serverPosY += par1Packet30Entity.yPosition;
            entity.serverPosZ += par1Packet30Entity.zPosition;
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = par1Packet30Entity.rotating ? (float)(par1Packet30Entity.yaw * 360) / 256.0F : entity.rotationYaw;
            float f1 = par1Packet30Entity.rotating ? (float)(par1Packet30Entity.pitch * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3);
        }
    }

    public void handleEntityHeadRotation(Packet35EntityHeadRotation par1Packet35EntityHeadRotation)
    {
        Entity entity = this.getEntityByID(par1Packet35EntityHeadRotation.entityId);

        if (entity != null)
        {
            float f = (float)(par1Packet35EntityHeadRotation.headRotationYaw * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    public void handleDestroyEntity(Packet29DestroyEntity par1Packet29DestroyEntity)
    {
        for (int i = 0; i < par1Packet29DestroyEntity.entityId.length; ++i)
        {
            this.worldClient.removeEntityFromWorld(par1Packet29DestroyEntity.entityId[i]);
        }
    }

    public void handleFlying(Packet10Flying par1Packet10Flying)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        double d0 = entityclientplayermp.posX;
        double d1 = entityclientplayermp.posY;
        double d2 = entityclientplayermp.posZ;
        float f = entityclientplayermp.rotationYaw;
        float f1 = entityclientplayermp.rotationPitch;

        if (par1Packet10Flying.moving)
        {
            d0 = par1Packet10Flying.xPosition;
            d1 = par1Packet10Flying.yPosition;
            d2 = par1Packet10Flying.zPosition;
        }

        if (par1Packet10Flying.rotating)
        {
            f = par1Packet10Flying.yaw;
            f1 = par1Packet10Flying.pitch;
        }

        entityclientplayermp.ySize = 0.0F;
        entityclientplayermp.motionX = entityclientplayermp.motionY = entityclientplayermp.motionZ = 0.0D;
        entityclientplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        par1Packet10Flying.xPosition = entityclientplayermp.posX;
        par1Packet10Flying.yPosition = entityclientplayermp.boundingBox.minY;
        par1Packet10Flying.zPosition = entityclientplayermp.posZ;
        par1Packet10Flying.stance = entityclientplayermp.posY;
        this.netManager.addToSendQueue(par1Packet10Flying);

        if (!this.doneLoadingTerrain)
        {
            this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
            this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
            this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    public void handleMultiBlockChange(Packet52MultiBlockChange par1Packet52MultiBlockChange)
    {
        int i = par1Packet52MultiBlockChange.xPosition * 16;
        int j = par1Packet52MultiBlockChange.zPosition * 16;

        if (par1Packet52MultiBlockChange.metadataArray != null)
        {
            DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(par1Packet52MultiBlockChange.metadataArray));

            try
            {
                for (int k = 0; k < par1Packet52MultiBlockChange.size; ++k)
                {
                    short short1 = datainputstream.readShort();
                    short short2 = datainputstream.readShort();
                    int l = short2 >> 4 & 4095;
                    int i1 = short2 & 15;
                    int j1 = short1 >> 12 & 15;
                    int k1 = short1 >> 8 & 15;
                    int l1 = short1 & 255;
                    this.worldClient.setBlockAndMetadataAndInvalidate(j1 + i, l1, k1 + j, l, i1);
                }
            }
            catch (IOException ioexception)
            {
                ;
            }
        }
    }

    /**
     * Handle Packet51MapChunk (full chunk update of blocks, metadata, light levels, and optionally biome data)
     */
    public void handleMapChunk(Packet51MapChunk par1Packet51MapChunk)
    {
        if (par1Packet51MapChunk.includeInitialize)
        {
            if (par1Packet51MapChunk.yChMin == 0)
            {
                this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, false);
                return;
            }

            this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, true);
        }

        this.worldClient.invalidateBlockReceiveRegion(par1Packet51MapChunk.xCh << 4, 0, par1Packet51MapChunk.zCh << 4, (par1Packet51MapChunk.xCh << 4) + 15, 256, (par1Packet51MapChunk.zCh << 4) + 15);
        Chunk chunk = this.worldClient.getChunkFromChunkCoords(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh);

        if (par1Packet51MapChunk.includeInitialize && chunk == null)
        {
            this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, true);
            chunk = this.worldClient.getChunkFromChunkCoords(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh);
        }

        if (chunk != null)
        {
            chunk.fillChunk(par1Packet51MapChunk.getCompressedChunkData(), par1Packet51MapChunk.yChMin, par1Packet51MapChunk.yChMax, par1Packet51MapChunk.includeInitialize);
            this.worldClient.markBlockRangeForRenderUpdate(par1Packet51MapChunk.xCh << 4, 0, par1Packet51MapChunk.zCh << 4, (par1Packet51MapChunk.xCh << 4) + 15, 256, (par1Packet51MapChunk.zCh << 4) + 15);

            if (!par1Packet51MapChunk.includeInitialize || !(this.worldClient.provider instanceof WorldProviderSurface))
            {
                chunk.resetRelightChecks();
            }
        }
    }

    public void handleBlockChange(Packet53BlockChange par1Packet53BlockChange)
    {
        this.worldClient.setBlockAndMetadataAndInvalidate(par1Packet53BlockChange.xPosition, par1Packet53BlockChange.yPosition, par1Packet53BlockChange.zPosition, par1Packet53BlockChange.type, par1Packet53BlockChange.metadata);
    }

    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.netManager.networkShutdown("disconnect.kicked", par1Packet255KickDisconnect.reason);
        this.disconnected = true;
        this.mc.loadWorld((WorldClient)null);

        if (this.field_98183_l != null)
        {
            this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.disconnected", "disconnect.genericReason", new Object[] {par1Packet255KickDisconnect.reason}));
        }
        else
        {
            this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.disconnected", "disconnect.genericReason", new Object[] {par1Packet255KickDisconnect.reason}));
        }
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        if (!this.disconnected)
        {
            this.disconnected = true;
            this.mc.loadWorld((WorldClient)null);

            if (this.field_98183_l != null)
            {
                this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.lost", par1Str, par2ArrayOfObj));
            }
            else
            {
                this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", par1Str, par2ArrayOfObj));
            }
        }
    }

    public void quitWithPacket(Packet par1Packet)
    {
        if (!this.disconnected)
        {
            this.netManager.addToSendQueue(par1Packet);
            this.netManager.serverShutdown();
            FMLNetworkHandler.onConnectionClosed(this.netManager, this.getPlayer());
        }
    }

    /**
     * Adds the packet to the send queue
     */
    public void addToSendQueue(Packet par1Packet)
    {
        if (!this.disconnected)
        {
            this.netManager.addToSendQueue(par1Packet);
        }
    }

    public void handleCollect(Packet22Collect par1Packet22Collect)
    {
        Entity entity = this.getEntityByID(par1Packet22Collect.collectedEntityId);
        Object object = (EntityLiving)this.getEntityByID(par1Packet22Collect.collectorEntityId);

        if (object == null)
        {
            object = this.mc.thePlayer;
        }

        if (entity != null)
        {
            if (entity instanceof EntityXPOrb)
            {
                this.worldClient.playSoundAtEntity(entity, "random.orb", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            else
            {
                this.worldClient.playSoundAtEntity(entity, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, entity, (Entity)object, -0.5F));
            this.worldClient.removeEntityFromWorld(par1Packet22Collect.collectedEntityId);
        }
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
        par1Packet3Chat = FMLNetworkHandler.handleChatMessage(this, par1Packet3Chat);
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(par1Packet3Chat.message);
        if (!MinecraftForge.EVENT_BUS.post(event) && event.message != null)
        {
            this.mc.ingameGUI.getChatGUI().printChatMessage(par1Packet3Chat.message);
        }
    }

    public void handleAnimation(Packet18Animation par1Packet18Animation)
    {
        Entity entity = this.getEntityByID(par1Packet18Animation.entityId);

        if (entity != null)
        {
            if (par1Packet18Animation.animate == 1)
            {
                EntityLiving entityliving = (EntityLiving)entity;
                entityliving.swingItem();
            }
            else if (par1Packet18Animation.animate == 2)
            {
                entity.performHurtAnimation();
            }
            else if (par1Packet18Animation.animate == 3)
            {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.wakeUpPlayer(false, false, false);
            }
            else if (par1Packet18Animation.animate != 4)
            {
                if (par1Packet18Animation.animate == 6)
                {
                    this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, entity));
                }
                else if (par1Packet18Animation.animate == 7)
                {
                    EntityCrit2FX entitycrit2fx = new EntityCrit2FX(this.mc.theWorld, entity, "magicCrit");
                    this.mc.effectRenderer.addEffect(entitycrit2fx);
                }
                else if (par1Packet18Animation.animate == 5 && entity instanceof EntityOtherPlayerMP)
                {
                    ;
                }
            }
        }
    }

    public void handleSleep(Packet17Sleep par1Packet17Sleep)
    {
        Entity entity = this.getEntityByID(par1Packet17Sleep.entityID);

        if (entity != null)
        {
            if (par1Packet17Sleep.field_73622_e == 0)
            {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.sleepInBedAt(par1Packet17Sleep.bedX, par1Packet17Sleep.bedY, par1Packet17Sleep.bedZ);
            }
        }
    }

    /**
     * Disconnects the network connection.
     */
    public void disconnect()
    {
        this.disconnected = true;
        this.netManager.wakeThreads();
        this.netManager.networkShutdown("disconnect.closed", new Object[0]);
    }

    public void handleMobSpawn(Packet24MobSpawn par1Packet24MobSpawn)
    {
        double d0 = (double)par1Packet24MobSpawn.xPosition / 32.0D;
        double d1 = (double)par1Packet24MobSpawn.yPosition / 32.0D;
        double d2 = (double)par1Packet24MobSpawn.zPosition / 32.0D;
        float f = (float)(par1Packet24MobSpawn.yaw * 360) / 256.0F;
        float f1 = (float)(par1Packet24MobSpawn.pitch * 360) / 256.0F;
        EntityLiving entityliving = (EntityLiving)EntityList.createEntityByID(par1Packet24MobSpawn.type, this.mc.theWorld);
        entityliving.serverPosX = par1Packet24MobSpawn.xPosition;
        entityliving.serverPosY = par1Packet24MobSpawn.yPosition;
        entityliving.serverPosZ = par1Packet24MobSpawn.zPosition;
        entityliving.rotationYawHead = (float)(par1Packet24MobSpawn.headYaw * 360) / 256.0F;
        Entity[] aentity = entityliving.getParts();

        if (aentity != null)
        {
            int i = par1Packet24MobSpawn.entityId - entityliving.entityId;

            for (int j = 0; j < aentity.length; ++j)
            {
                aentity[j].entityId += i;
            }
        }

        entityliving.entityId = par1Packet24MobSpawn.entityId;
        entityliving.setPositionAndRotation(d0, d1, d2, f, f1);
        entityliving.motionX = (double)((float)par1Packet24MobSpawn.velocityX / 8000.0F);
        entityliving.motionY = (double)((float)par1Packet24MobSpawn.velocityY / 8000.0F);
        entityliving.motionZ = (double)((float)par1Packet24MobSpawn.velocityZ / 8000.0F);
        this.worldClient.addEntityToWorld(par1Packet24MobSpawn.entityId, entityliving);
        List list = par1Packet24MobSpawn.getMetadata();

        if (list != null)
        {
            entityliving.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleUpdateTime(Packet4UpdateTime par1Packet4UpdateTime)
    {
        this.mc.theWorld.func_82738_a(par1Packet4UpdateTime.worldAge);
        this.mc.theWorld.setWorldTime(par1Packet4UpdateTime.time);
    }

    public void handleSpawnPosition(Packet6SpawnPosition par1Packet6SpawnPosition)
    {
        this.mc.thePlayer.setSpawnChunk(new ChunkCoordinates(par1Packet6SpawnPosition.xPosition, par1Packet6SpawnPosition.yPosition, par1Packet6SpawnPosition.zPosition), true);
        this.mc.theWorld.getWorldInfo().setSpawnPosition(par1Packet6SpawnPosition.xPosition, par1Packet6SpawnPosition.yPosition, par1Packet6SpawnPosition.zPosition);
    }

    /**
     * Packet handler
     */
    public void handleAttachEntity(Packet39AttachEntity par1Packet39AttachEntity)
    {
        Object object = this.getEntityByID(par1Packet39AttachEntity.entityId);
        Entity entity = this.getEntityByID(par1Packet39AttachEntity.vehicleEntityId);

        if (par1Packet39AttachEntity.entityId == this.mc.thePlayer.entityId)
        {
            object = this.mc.thePlayer;

            if (entity instanceof EntityBoat)
            {
                ((EntityBoat)entity).func_70270_d(false);
            }
        }
        else if (entity instanceof EntityBoat)
        {
            ((EntityBoat)entity).func_70270_d(true);
        }

        if (object != null)
        {
            ((Entity)object).mountEntity(entity);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityStatus(Packet38EntityStatus par1Packet38EntityStatus)
    {
        Entity entity = this.getEntityByID(par1Packet38EntityStatus.entityId);

        if (entity != null)
        {
            entity.handleHealthUpdate(par1Packet38EntityStatus.entityStatus);
        }
    }

    private Entity getEntityByID(int par1)
    {
        return (Entity)(par1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : this.worldClient.getEntityByID(par1));
    }

    /**
     * Recieves player health from the server and then proceeds to set it locally on the client.
     */
    public void handleUpdateHealth(Packet8UpdateHealth par1Packet8UpdateHealth)
    {
        this.mc.thePlayer.setHealth(par1Packet8UpdateHealth.healthMP);
        this.mc.thePlayer.getFoodStats().setFoodLevel(par1Packet8UpdateHealth.food);
        this.mc.thePlayer.getFoodStats().setFoodSaturationLevel(par1Packet8UpdateHealth.foodSaturation);
    }

    /**
     * Handle an experience packet.
     */
    public void handleExperience(Packet43Experience par1Packet43Experience)
    {
        this.mc.thePlayer.setXPStats(par1Packet43Experience.experience, par1Packet43Experience.experienceTotal, par1Packet43Experience.experienceLevel);
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn par1Packet9Respawn)
    {
        if (par1Packet9Respawn.respawnDimension != this.mc.thePlayer.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.worldClient.getScoreboard();
            this.worldClient = new WorldClient(this, new WorldSettings(0L, par1Packet9Respawn.gameType, false, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), par1Packet9Respawn.terrainType), par1Packet9Respawn.respawnDimension, par1Packet9Respawn.difficulty, this.mc.mcProfiler, this.mc.getLogAgent());
            this.worldClient.func_96443_a(scoreboard);
            this.worldClient.isRemote = true;
            this.mc.loadWorld(this.worldClient);
            this.mc.thePlayer.dimension = par1Packet9Respawn.respawnDimension;
            this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.mc.setDimensionAndSpawnPlayer(par1Packet9Respawn.respawnDimension);
        this.mc.playerController.setGameType(par1Packet9Respawn.gameType);
    }

    public void handleExplosion(Packet60Explosion par1Packet60Explosion)
    {
        Explosion explosion = new Explosion(this.mc.theWorld, (Entity)null, par1Packet60Explosion.explosionX, par1Packet60Explosion.explosionY, par1Packet60Explosion.explosionZ, par1Packet60Explosion.explosionSize);
        explosion.affectedBlockPositions = par1Packet60Explosion.chunkPositionRecords;
        explosion.doExplosionB(true);
        this.mc.thePlayer.motionX += (double)par1Packet60Explosion.getPlayerVelocityX();
        this.mc.thePlayer.motionY += (double)par1Packet60Explosion.getPlayerVelocityY();
        this.mc.thePlayer.motionZ += (double)par1Packet60Explosion.getPlayerVelocityZ();
    }

    public void handleOpenWindow(Packet100OpenWindow par1Packet100OpenWindow)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        switch (par1Packet100OpenWindow.inventoryType)
        {
            case 0:
                entityclientplayermp.displayGUIChest(new InventoryBasic(par1Packet100OpenWindow.windowTitle, par1Packet100OpenWindow.useProvidedWindowTitle, par1Packet100OpenWindow.slotsCount));
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 1:
                entityclientplayermp.displayGUIWorkbench(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 2:
                TileEntityFurnace tileentityfurnace = new TileEntityFurnace();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentityfurnace.func_94129_a(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIFurnace(tileentityfurnace);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 3:
                TileEntityDispenser tileentitydispenser = new TileEntityDispenser();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitydispenser.setCustomName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIDispenser(tileentitydispenser);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 4:
                entityclientplayermp.displayGUIEnchantment(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), par1Packet100OpenWindow.useProvidedWindowTitle ? par1Packet100OpenWindow.windowTitle : null);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 5:
                TileEntityBrewingStand tileentitybrewingstand = new TileEntityBrewingStand();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitybrewingstand.func_94131_a(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIBrewingStand(tileentitybrewingstand);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 6:
                entityclientplayermp.displayGUIMerchant(new NpcMerchant(entityclientplayermp), par1Packet100OpenWindow.useProvidedWindowTitle ? par1Packet100OpenWindow.windowTitle : null);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 7:
                TileEntityBeacon tileentitybeacon = new TileEntityBeacon();
                entityclientplayermp.displayGUIBeacon(tileentitybeacon);

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitybeacon.func_94047_a(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 8:
                entityclientplayermp.displayGUIAnvil(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 9:
                TileEntityHopper tileentityhopper = new TileEntityHopper();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentityhopper.setInventoryName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIHopper(tileentityhopper);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 10:
                TileEntityDropper tileentitydropper = new TileEntityDropper();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitydropper.setCustomName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIDispenser(tileentitydropper);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
        }
    }

    public void handleSetSlot(Packet103SetSlot par1Packet103SetSlot)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        if (par1Packet103SetSlot.windowId == -1)
        {
            entityclientplayermp.inventory.setItemStack(par1Packet103SetSlot.myItemStack);
        }
        else
        {
            boolean flag = false;

            if (this.mc.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.mc.currentScreen;
                flag = guicontainercreative.func_74230_h() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (par1Packet103SetSlot.windowId == 0 && par1Packet103SetSlot.itemSlot >= 36 && par1Packet103SetSlot.itemSlot < 45)
            {
                ItemStack itemstack = entityclientplayermp.inventoryContainer.getSlot(par1Packet103SetSlot.itemSlot).getStack();

                if (par1Packet103SetSlot.myItemStack != null && (itemstack == null || itemstack.stackSize < par1Packet103SetSlot.myItemStack.stackSize))
                {
                    par1Packet103SetSlot.myItemStack.animationsToGo = 5;
                }

                entityclientplayermp.inventoryContainer.putStackInSlot(par1Packet103SetSlot.itemSlot, par1Packet103SetSlot.myItemStack);
            }
            else if (par1Packet103SetSlot.windowId == entityclientplayermp.openContainer.windowId && (par1Packet103SetSlot.windowId != 0 || !flag))
            {
                entityclientplayermp.openContainer.putStackInSlot(par1Packet103SetSlot.itemSlot, par1Packet103SetSlot.myItemStack);
            }
        }
    }

    public void handleTransaction(Packet106Transaction par1Packet106Transaction)
    {
        Container container = null;
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        if (par1Packet106Transaction.windowId == 0)
        {
            container = entityclientplayermp.inventoryContainer;
        }
        else if (par1Packet106Transaction.windowId == entityclientplayermp.openContainer.windowId)
        {
            container = entityclientplayermp.openContainer;
        }

        if (container != null && !par1Packet106Transaction.accepted)
        {
            this.addToSendQueue(new Packet106Transaction(par1Packet106Transaction.windowId, par1Packet106Transaction.shortWindowId, true));
        }
    }

    public void handleWindowItems(Packet104WindowItems par1Packet104WindowItems)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        if (par1Packet104WindowItems.windowId == 0)
        {
            entityclientplayermp.inventoryContainer.putStacksInSlots(par1Packet104WindowItems.itemStack);
        }
        else if (par1Packet104WindowItems.windowId == entityclientplayermp.openContainer.windowId)
        {
            entityclientplayermp.openContainer.putStacksInSlots(par1Packet104WindowItems.itemStack);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        boolean flag = false;

        if (this.mc.theWorld.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            TileEntity tileentity = this.mc.theWorld.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if (tileentity instanceof TileEntitySign)
            {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (tileentitysign.isEditable())
                {
                    for (int i = 0; i < 4; ++i)
                    {
                        tileentitysign.signText[i] = par1Packet130UpdateSign.signLines[i];
                    }

                    tileentitysign.onInventoryChanged();
                }

                flag = true;
            }
        }

        if (!flag && this.mc.thePlayer != null)
        {
            this.mc.thePlayer.sendChatToPlayer("Unable to locate sign at " + par1Packet130UpdateSign.xPosition + ", " + par1Packet130UpdateSign.yPosition + ", " + par1Packet130UpdateSign.zPosition);
        }
    }

    public void handleTileEntityData(Packet132TileEntityData par1Packet132TileEntityData)
    {
        if (this.mc.theWorld.blockExists(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition))
        {
            TileEntity tileentity = this.mc.theWorld.getBlockTileEntity(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition);

            if (tileentity != null)
            {
                if (par1Packet132TileEntityData.actionType == 1 && tileentity instanceof TileEntityMobSpawner)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.customParam1);
                }
                else if (par1Packet132TileEntityData.actionType == 2 && tileentity instanceof TileEntityCommandBlock)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.customParam1);
                }
                else if (par1Packet132TileEntityData.actionType == 3 && tileentity instanceof TileEntityBeacon)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.customParam1);
                }
                else if (par1Packet132TileEntityData.actionType == 4 && tileentity instanceof TileEntitySkull)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.customParam1);
                }
                else
                {
                    tileentity.onDataPacket(netManager,  par1Packet132TileEntityData);
                }
            }
        }
    }

    public void handleUpdateProgressbar(Packet105UpdateProgressbar par1Packet105UpdateProgressbar)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        this.unexpectedPacket(par1Packet105UpdateProgressbar);

        if (entityclientplayermp.openContainer != null && entityclientplayermp.openContainer.windowId == par1Packet105UpdateProgressbar.windowId)
        {
            entityclientplayermp.openContainer.updateProgressBar(par1Packet105UpdateProgressbar.progressBar, par1Packet105UpdateProgressbar.progressBarValue);
        }
    }

    public void handlePlayerInventory(Packet5PlayerInventory par1Packet5PlayerInventory)
    {
        Entity entity = this.getEntityByID(par1Packet5PlayerInventory.entityID);

        if (entity != null)
        {
            entity.setCurrentItemOrArmor(par1Packet5PlayerInventory.slot, par1Packet5PlayerInventory.getItemSlot());
        }
    }

    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
    {
        this.mc.thePlayer.func_92015_f();
    }

    public void handleBlockEvent(Packet54PlayNoteBlock par1Packet54PlayNoteBlock)
    {
        this.mc.theWorld.addBlockEvent(par1Packet54PlayNoteBlock.xLocation, par1Packet54PlayNoteBlock.yLocation, par1Packet54PlayNoteBlock.zLocation, par1Packet54PlayNoteBlock.blockId, par1Packet54PlayNoteBlock.instrumentType, par1Packet54PlayNoteBlock.pitch);
    }

    public void handleBlockDestroy(Packet55BlockDestroy par1Packet55BlockDestroy)
    {
        this.mc.theWorld.destroyBlockInWorldPartially(par1Packet55BlockDestroy.getEntityId(), par1Packet55BlockDestroy.getPosX(), par1Packet55BlockDestroy.getPosY(), par1Packet55BlockDestroy.getPosZ(), par1Packet55BlockDestroy.getDestroyedStage());
    }

    public void handleMapChunks(Packet56MapChunks par1Packet56MapChunks)
    {
        for (int i = 0; i < par1Packet56MapChunks.getNumberOfChunkInPacket(); ++i)
        {
            int j = par1Packet56MapChunks.getChunkPosX(i);
            int k = par1Packet56MapChunks.getChunkPosZ(i);
            this.worldClient.doPreChunk(j, k, true);
            this.worldClient.invalidateBlockReceiveRegion(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
            Chunk chunk = this.worldClient.getChunkFromChunkCoords(j, k);

            if (chunk == null)
            {
                this.worldClient.doPreChunk(j, k, true);
                chunk = this.worldClient.getChunkFromChunkCoords(j, k);
            }

            if (chunk != null)
            {
                chunk.fillChunk(par1Packet56MapChunks.getChunkCompressedData(i), par1Packet56MapChunks.field_73590_a[i], par1Packet56MapChunks.field_73588_b[i], true);
                this.worldClient.markBlockRangeForRenderUpdate(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);

                if (!(this.worldClient.provider instanceof WorldProviderSurface))
                {
                    chunk.resetRelightChecks();
                }
            }
        }
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return this.mc != null && this.mc.theWorld != null && this.mc.thePlayer != null && this.worldClient != null;
    }

    public void handleGameEvent(Packet70GameEvent par1Packet70GameEvent)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        int i = par1Packet70GameEvent.eventType;
        int j = par1Packet70GameEvent.gameMode;

        if (i >= 0 && i < Packet70GameEvent.clientMessage.length && Packet70GameEvent.clientMessage[i] != null)
        {
            entityclientplayermp.addChatMessage(Packet70GameEvent.clientMessage[i]);
        }

        if (i == 1)
        {
            this.worldClient.getWorldInfo().setRaining(true);
            this.worldClient.setRainStrength(0.0F);
        }
        else if (i == 2)
        {
            this.worldClient.getWorldInfo().setRaining(false);
            this.worldClient.setRainStrength(1.0F);
        }
        else if (i == 3)
        {
            this.mc.playerController.setGameType(EnumGameType.getByID(j));
        }
        else if (i == 4)
        {
            this.mc.displayGuiScreen(new GuiWinGame());
        }
        else if (i == 5)
        {
            GameSettings gamesettings = this.mc.gameSettings;

            if (j == 0)
            {
                this.mc.displayGuiScreen(new GuiScreenDemo());
            }
            else if (j == 101)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.movement", new Object[] {Keyboard.getKeyName(gamesettings.keyBindForward.keyCode), Keyboard.getKeyName(gamesettings.keyBindLeft.keyCode), Keyboard.getKeyName(gamesettings.keyBindBack.keyCode), Keyboard.getKeyName(gamesettings.keyBindRight.keyCode)});
            }
            else if (j == 102)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.jump", new Object[] {Keyboard.getKeyName(gamesettings.keyBindJump.keyCode)});
            }
            else if (j == 103)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.inventory", new Object[] {Keyboard.getKeyName(gamesettings.keyBindInventory.keyCode)});
            }
        }
        else if (i == 6)
        {
            this.worldClient.playSound(entityclientplayermp.posX, entityclientplayermp.posY + (double)entityclientplayermp.getEyeHeight(), entityclientplayermp.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        }
    }

    /**
     * Contains logic for handling packets containing arbitrary unique item data. Currently this is only for maps.
     */
    public void handleMapData(Packet131MapData par1Packet131MapData)
    {
        FMLNetworkHandler.handlePacket131Packet(this, par1Packet131MapData);
    }

    public void fmlPacket131Callback(Packet131MapData par1Packet131MapData)
    {
        if (par1Packet131MapData.itemID == Item.map.itemID)
        {
            ItemMap.getMPMapData(par1Packet131MapData.uniqueID, this.mc.theWorld).updateMPMapData(par1Packet131MapData.itemData);
        }
        else
        {
            this.mc.getLogAgent().logWarning("Unknown itemid: " + par1Packet131MapData.uniqueID);
        }
    }

    public void handleDoorChange(Packet61DoorChange par1Packet61DoorChange)
    {
        if (par1Packet61DoorChange.getRelativeVolumeDisabled())
        {
            this.mc.theWorld.func_82739_e(par1Packet61DoorChange.sfxID, par1Packet61DoorChange.posX, par1Packet61DoorChange.posY, par1Packet61DoorChange.posZ, par1Packet61DoorChange.auxData);
        }
        else
        {
            this.mc.theWorld.playAuxSFX(par1Packet61DoorChange.sfxID, par1Packet61DoorChange.posX, par1Packet61DoorChange.posY, par1Packet61DoorChange.posZ, par1Packet61DoorChange.auxData);
        }
    }

    /**
     * Increment player statistics
     */
    public void handleStatistic(Packet200Statistic par1Packet200Statistic)
    {
        this.mc.thePlayer.incrementStat(StatList.getOneShotStat(par1Packet200Statistic.statisticId), par1Packet200Statistic.amount);
    }

    /**
     * Handle an entity effect packet.
     */
    public void handleEntityEffect(Packet41EntityEffect par1Packet41EntityEffect)
    {
        Entity entity = this.getEntityByID(par1Packet41EntityEffect.entityId);

        if (entity instanceof EntityLiving)
        {
            PotionEffect potioneffect = new PotionEffect(par1Packet41EntityEffect.effectId, par1Packet41EntityEffect.duration, par1Packet41EntityEffect.effectAmplifier);
            potioneffect.setPotionDurationMax(par1Packet41EntityEffect.isDurationMax());
            ((EntityLiving)entity).addPotionEffect(potioneffect);
        }
    }

    /**
     * Handle a remove entity effect packet.
     */
    public void handleRemoveEntityEffect(Packet42RemoveEntityEffect par1Packet42RemoveEntityEffect)
    {
        Entity entity = this.getEntityByID(par1Packet42RemoveEntityEffect.entityId);

        if (entity instanceof EntityLiving)
        {
            ((EntityLiving)entity).removePotionEffectClient(par1Packet42RemoveEntityEffect.effectId);
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return false;
    }

    /**
     * Handle a player information packet.
     */
    public void handlePlayerInfo(Packet201PlayerInfo par1Packet201PlayerInfo)
    {
        GuiPlayerInfo guiplayerinfo = (GuiPlayerInfo)this.playerInfoMap.get(par1Packet201PlayerInfo.playerName);

        if (guiplayerinfo == null && par1Packet201PlayerInfo.isConnected)
        {
            guiplayerinfo = new GuiPlayerInfo(par1Packet201PlayerInfo.playerName);
            this.playerInfoMap.put(par1Packet201PlayerInfo.playerName, guiplayerinfo);
            this.playerInfoList.add(guiplayerinfo);
        }

        if (guiplayerinfo != null && !par1Packet201PlayerInfo.isConnected)
        {
            this.playerInfoMap.remove(par1Packet201PlayerInfo.playerName);
            this.playerInfoList.remove(guiplayerinfo);
        }

        if (par1Packet201PlayerInfo.isConnected && guiplayerinfo != null)
        {
            guiplayerinfo.responseTime = par1Packet201PlayerInfo.ping;
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
    {
        this.addToSendQueue(new Packet0KeepAlive(par1Packet0KeepAlive.randomId));
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        entityclientplayermp.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying();
        entityclientplayermp.capabilities.isCreativeMode = par1Packet202PlayerAbilities.isCreativeMode();
        entityclientplayermp.capabilities.disableDamage = par1Packet202PlayerAbilities.getDisableDamage();
        entityclientplayermp.capabilities.allowFlying = par1Packet202PlayerAbilities.getAllowFlying();
        entityclientplayermp.capabilities.setFlySpeed(par1Packet202PlayerAbilities.getFlySpeed());
        entityclientplayermp.capabilities.setPlayerWalkSpeed(par1Packet202PlayerAbilities.getWalkSpeed());
    }

    public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete)
    {
        String[] astring = par1Packet203AutoComplete.getText().split("\u0000");

        if (this.mc.currentScreen instanceof GuiChat)
        {
            GuiChat guichat = (GuiChat)this.mc.currentScreen;
            guichat.func_73894_a(astring);
        }
    }

    public void handleLevelSound(Packet62LevelSound par1Packet62LevelSound)
    {
        this.mc.theWorld.playSound(par1Packet62LevelSound.getEffectX(), par1Packet62LevelSound.getEffectY(), par1Packet62LevelSound.getEffectZ(), par1Packet62LevelSound.getSoundName(), par1Packet62LevelSound.getVolume(), par1Packet62LevelSound.getPitch(), false);
    }

    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload)
    {
        FMLNetworkHandler.handlePacket250Packet(par1Packet250CustomPayload, netManager, this);
    }

    public void handleVanilla250Packet(Packet250CustomPayload par1Packet250CustomPayload)
    {
        if ("MC|TPack".equals(par1Packet250CustomPayload.channel))
        {
            String[] astring = (new String(par1Packet250CustomPayload.data)).split("\u0000");
            String s = astring[0];

            if (astring[1].equals("16"))
            {
                if (this.mc.texturePackList.getAcceptsTextures())
                {
                    this.mc.texturePackList.requestDownloadOfTexture(s);
                }
                else if (this.mc.texturePackList.func_77300_f())
                {
                    this.mc.displayGuiScreen(new GuiYesNo(new NetClientWebTextures(this, s), StringTranslate.getInstance().translateKey("multiplayer.texturePrompt.line1"), StringTranslate.getInstance().translateKey("multiplayer.texturePrompt.line2"), 0));
                }
            }
        }
        else if ("MC|TrList".equals(par1Packet250CustomPayload.channel))
        {
            DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));

            try
            {
                int i = datainputstream.readInt();
                GuiScreen guiscreen = this.mc.currentScreen;

                if (guiscreen != null && guiscreen instanceof GuiMerchant && i == this.mc.thePlayer.openContainer.windowId)
                {
                    IMerchant imerchant = ((GuiMerchant)guiscreen).getIMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.readRecipiesFromStream(datainputstream);
                    imerchant.setRecipes(merchantrecipelist);
                }
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }
    }

    /**
     * Handle a set objective packet.
     */
    public void handleSetObjective(Packet206SetObjective par1Packet206SetObjective)
    {
        Scoreboard scoreboard = this.worldClient.getScoreboard();
        ScoreObjective scoreobjective;

        if (par1Packet206SetObjective.change == 0)
        {
            scoreobjective = scoreboard.func_96535_a(par1Packet206SetObjective.objectiveName, ScoreObjectiveCriteria.field_96641_b);
            scoreobjective.setDisplayName(par1Packet206SetObjective.objectiveDisplayName);
        }
        else
        {
            scoreobjective = scoreboard.getObjective(par1Packet206SetObjective.objectiveName);

            if (par1Packet206SetObjective.change == 1)
            {
                scoreboard.func_96519_k(scoreobjective);
            }
            else if (par1Packet206SetObjective.change == 2)
            {
                scoreobjective.setDisplayName(par1Packet206SetObjective.objectiveDisplayName);
            }
        }
    }

    /**
     * Handle a set score packet.
     */
    public void handleSetScore(Packet207SetScore par1Packet207SetScore)
    {
        Scoreboard scoreboard = this.worldClient.getScoreboard();
        ScoreObjective scoreobjective = scoreboard.getObjective(par1Packet207SetScore.scoreName);

        if (par1Packet207SetScore.updateOrRemove == 0)
        {
            Score score = scoreboard.func_96529_a(par1Packet207SetScore.itemName, scoreobjective);
            score.func_96647_c(par1Packet207SetScore.value);
        }
        else if (par1Packet207SetScore.updateOrRemove == 1)
        {
            scoreboard.func_96515_c(par1Packet207SetScore.itemName);
        }
    }

    /**
     * Handle a set display objective packet.
     */
    public void handleSetDisplayObjective(Packet208SetDisplayObjective par1Packet208SetDisplayObjective)
    {
        Scoreboard scoreboard = this.worldClient.getScoreboard();

        if (par1Packet208SetDisplayObjective.scoreName.length() == 0)
        {
            scoreboard.func_96530_a(par1Packet208SetDisplayObjective.scoreboardPosition, (ScoreObjective)null);
        }
        else
        {
            ScoreObjective scoreobjective = scoreboard.getObjective(par1Packet208SetDisplayObjective.scoreName);
            scoreboard.func_96530_a(par1Packet208SetDisplayObjective.scoreboardPosition, scoreobjective);
        }
    }

    /**
     * Handle a set player team packet.
     */
    public void handleSetPlayerTeam(Packet209SetPlayerTeam par1Packet209SetPlayerTeam)
    {
        Scoreboard scoreboard = this.worldClient.getScoreboard();
        ScorePlayerTeam scoreplayerteam;

        if (par1Packet209SetPlayerTeam.mode == 0)
        {
            scoreplayerteam = scoreboard.func_96527_f(par1Packet209SetPlayerTeam.teamName);
        }
        else
        {
            scoreplayerteam = scoreboard.func_96508_e(par1Packet209SetPlayerTeam.teamName);
        }

        if (par1Packet209SetPlayerTeam.mode == 0 || par1Packet209SetPlayerTeam.mode == 2)
        {
            scoreplayerteam.func_96664_a(par1Packet209SetPlayerTeam.teamDisplayName);
            scoreplayerteam.func_96666_b(par1Packet209SetPlayerTeam.teamPrefix);
            scoreplayerteam.func_96662_c(par1Packet209SetPlayerTeam.teamSuffix);
            scoreplayerteam.func_98298_a(par1Packet209SetPlayerTeam.friendlyFire);
        }

        Iterator iterator;
        String s;

        if (par1Packet209SetPlayerTeam.mode == 0 || par1Packet209SetPlayerTeam.mode == 3)
        {
            iterator = par1Packet209SetPlayerTeam.playerNames.iterator();

            while (iterator.hasNext())
            {
                s = (String)iterator.next();
                scoreboard.func_96521_a(s, scoreplayerteam);
            }
        }

        if (par1Packet209SetPlayerTeam.mode == 4)
        {
            iterator = par1Packet209SetPlayerTeam.playerNames.iterator();

            while (iterator.hasNext())
            {
                s = (String)iterator.next();
                scoreboard.removePlayerFromTeam(s, scoreplayerteam);
            }
        }

        if (par1Packet209SetPlayerTeam.mode == 1)
        {
            scoreboard.func_96511_d(scoreplayerteam);
        }
    }

    /**
     * Handle a world particles packet.
     */
    public void handleWorldParticles(Packet63WorldParticles par1Packet63WorldParticles)
    {
        for (int i = 0; i < par1Packet63WorldParticles.getQuantity(); ++i)
        {
            double d0 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getOffsetX();
            double d1 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getOffsetY();
            double d2 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getOffsetZ();
            double d3 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getSpeed();
            double d4 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getSpeed();
            double d5 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getSpeed();
            this.worldClient.spawnParticle(par1Packet63WorldParticles.getParticleName(), par1Packet63WorldParticles.getPositionX() + d0, par1Packet63WorldParticles.getPositionY() + d1, par1Packet63WorldParticles.getPositionZ() + d2, d3, d4, d5);
        }
    }

    /**
     * Return the NetworkManager instance used by this NetClientHandler
     */
    public INetworkManager getNetManager()
    {
        return this.netManager;
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return mc.thePlayer;
    }

    public static void setConnectionCompatibilityLevel(byte connectionCompatibilityLevel)
    {
        NetClientHandler.connectionCompatibilityLevel = connectionCompatibilityLevel;
    }

    public static byte getConnectionCompatibilityLevel()
    {
        return connectionCompatibilityLevel;
    }
}
