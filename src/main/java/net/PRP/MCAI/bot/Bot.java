package net.PRP.MCAI.bot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.ProxyInfo.Type;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;
import world.World;
import net.PRP.MCAI.Inventory.*;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.UUID;

public class Bot {
    private final MinecraftProtocol account;
    
    private final String host;
    private final int port;
    
    private Session session;
    private Proxy proxy;
    private boolean movelocked = false;

    public double posX;
    public double posY;
    public double posZ;
    private float yaw;
    private float pitch;
    private UUID UUID;
    private boolean inAction;
    private boolean mainhost;
    private Inventory openedInventory;
    private int id;
    public EntityListener entityListener;
    public static World world;
    
    //private IInventory openedInventory;
    public int currentSlotInHand;
    //private PlayerInventory playerInventory;
    private int currentWindowId;
    
    public boolean tickMode = true;
    

    public Bot(MinecraftProtocol account, String host, int port, Proxy proxy) {
        this.account = account;
        this.proxy = proxy;

        this.host = host;
        this.port = port;
    }

    public void connect() {
    	//SessionService sessionService = new SessionService();
        //sessionService.setProxy(proxy);
    	world = new World();
        SocketAddress sa = proxy.address();
        String pt = (String)Main.getsett("proxytype");
        Type proxypype = null;
		if (pt.equalsIgnoreCase("socks4")) {
			proxypype = ProxyInfo.Type.SOCKS4;
		} else if (pt.equalsIgnoreCase("socks5")) {
			proxypype = ProxyInfo.Type.SOCKS5;
		} else if (pt.equalsIgnoreCase("http")) {
			proxypype = ProxyInfo.Type.HTTP;
		} else {
			
		}
        ProxyInfo pr = new ProxyInfo(proxypype, sa);
        Session client = null;
		if ((boolean) Main.getsett("useproxy")) {
        	client = new TcpClientSession(host, port, account, pr);
        } else {
        	client = new TcpClientSession(host, port, account);
        }
        //client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.addListener(new SessionListener(this));
        client.addListener(new PingPacketsManager());
        this.entityListener = new EntityListener(this);
        client.addListener(this.entityListener);
        client.addListener(new ChatListener(this));
        if (!Main.debug) {
        	this.tickMode = (boolean) Main.getsett("tickmode");
	        if ((boolean)Main.getsett("raidmode")) {
	        	client.addListener(new RaiderAIListener(this));
	        }
        }
        client.connect();
        register();

        this.session = client;
        if (tickMode) {
        	new Thread(()-> {
            	while (true) {
            		Vector3D before = getPosition();
            		ThreadU.sleep(50);
            		Vector3D now = getPosition();
            		if (!VectorUtils.equals(before, now)) {
            			getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
            		}
            	}
            }).start();
        }
    }

    public void register() {
        if (!isOnline()) return;
        ThreadU.sleep(200);
        session.send(new ClientChatPacket("/register 112233asdasd 112233asdasd"));
        ThreadU.sleep(100);
        session.send(new ClientChatPacket("/login 112233asdasd"));
        
    }
    
    public void addX(double i) {
    	this.posX += i;
    	if (!tickMode) getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void addY(double i) {
    	this.posY += i;
    	if (!tickMode) getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void addZ(double i) {
    	this.posZ += i;
    	if (!tickMode) getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void remX(double i) {
    	this.posX -= i;
    	if (!tickMode) getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void remY(double i) {
    	this.posY -= i;
    	if (!tickMode) getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void remZ(double i) {
    	this.posZ -= i;
    	if (!tickMode) getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }

    public boolean isOnline() {
        return session != null && session.isConnected();
    }

    public Session getSession() {
        return session;
    }

    public GameProfile getGameProfile() {
        return account.getProfile();
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public int getCurrentWindowId() {
		return currentWindowId;
	}

	public void setCurrentWindowId(int i) {
		currentWindowId = i;
	}
	
	public Vector3D getPosition() {
		return new Vector3D(this.posX, this.posY, this.posZ);
	}
	
	public Vector3D getPositionInt() {
		return new Vector3D((int)this.posX, (int)this.posY, (int)this.posZ);
	}
	
	@Deprecated
	public boolean isInLiquid() {
		int id = getWorld().getBlock(getPosition()).id;
		if (id == 8 || id == 9 || id == 10 || id == 11) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setmovelocked(boolean t) {
    	this.movelocked = t;
    }
    
    public boolean getmovelocked() {
    	return movelocked;
    }

	public UUID getUUID() {
		return UUID;
	}

	public void setUUID(UUID uUID) {
		UUID = uUID;
	}

	public boolean isInAction() {
		return inAction;
	}

	public void setInAction(boolean inAction) {
		this.inAction = inAction;
	}

	public boolean isMain() {
		return mainhost;
	}

	public void setMainhost(boolean mainhost) {
		this.mainhost = mainhost;
	}

	public Inventory getOpenedInventory() {
		return openedInventory;
	}

	public void setOpenedInventory(Inventory openedInventory) {
		this.openedInventory = openedInventory;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public World getWorld() {
		return world;
	}
}
