package net.PRP.MCAI.GUI;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.specific.Crafting;
import net.PRP.MCAI.bot.specific.Crafting.craftingRecepie;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.ThreadU;

public class SteeringWheel {
	
	JFrame frame;
	JFrame taskmanager;
	//public DefaultListModel<String> listofbots = new DefaultListModel<>();
	public int botnum = 0;
	public List<String> hi = new CopyOnWriteArrayList<>();
	JLabel stats;
	public Canvas canvas;
	public List<String> readyhi = new CopyOnWriteArrayList<String>() {
	private static final long serialVersionUID = 1L; {
		add("ку");
		add("хай");
		add("всем прив");
		add("привееетц");
		add("qq");
		add("q");
		add("здарова");
		add("банжур");
		add("кукукуку");
		add("прив");
		add("здрасте");
		add("privet");
		add("приветули");
	}};
	public JComboBox<String> servers;
	
	public SteeringWheel() {
		frame = new JFrame("-------------------------------");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(600,500);
	    frame.setLayout(null);
	    
	    SetupTM();
	    
	    JTextField b = new JTextField("текст для отправки");
	    b.setBounds(0,0,400,30);
	    frame.add(b);
	    
	    this.stats = new JLabel("?noinfo?");
	    this.stats.setBounds(0,30,300,20);
	    frame.add(this.stats);
	    
	    JButton letshi = new JButton("cлучайное приветствие от ботов поочередно");
	    letshi.setBounds(0, 70, 330, 20);
	    letshi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (botnum > Main.bots.size()-1) botnum = 0;
				if (hi.size() == 0) hi.addAll(readyhi);
				String randomfrase = hi.get(MathU.rnd(0, hi.size()-1));
				BotU.chat(Main.bots.get(botnum), randomfrase);
				hi.remove(randomfrase);
				botnum++;
			}
	    });
	    frame.add(letshi);
	    
	    JButton sendall = new JButton("отослать от имени всех ботов");
	    sendall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				for (Bot bot : Main.bots) {
					BotU.chat(bot, b.getText());
				}
			}
	    });
	    sendall.setBounds(0,90,270,20);
	    frame.add(sendall);
	    
	    JButton sendasrandom = new JButton("отослать от имени случайного бота");
	    sendasrandom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				BotU.chat(Main.bots.get(MathU.rnd(0, Main.bots.size()-1)), b.getText());
			}
	    });
	    sendasrandom.setBounds(0,50,270,20);
	    frame.add(sendasrandom);
	    
	    JTextField host = new JTextField((String) Main.getset("host"));
	    host.setBounds(330,50,200,20);
	    frame.add(host);
	    
	    JButton connectbot = new JButton("присоединить бота");
	    connectbot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				new Thread(()->{
				String name = Main.nextNick();
				Proxy proxy = Main.nextProxy();
				new Thread(new Bot(name, host.getText(), proxy,false)).start();
				}).start();
			}
	    });
	    connectbot.setBounds(0,110,180,20);
	    frame.add(connectbot);
	    
	    JButton delbot = new JButton("удалить бота");
	    delbot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Bot cl = Main.bots.get(0);
				if (cl.isOnline()) cl.kill();
				try {Main.bots.remove(cl);} catch(Exception e) {Main.bots.remove(0);}
			}
	    });
	    delbot.setBounds(0,130,190,20);
	    frame.add(delbot);
	    
	    JButton systemgc = new JButton("System gc");
	    systemgc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				System.gc();
			}
	    });
	    systemgc.setBounds(0,150,100,20);
	    frame.add(systemgc);
	    
	    JButton rndpasta = new JButton("рандом паста от рандом бота");
	    rndpasta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				BotU.chat(Main.bots.get(MathU.rnd(0, Main.bots.size()-1)), Main.pasti.get(MathU.rnd(0, Main.pasti.size()-1)));
			}
	    });
	    rndpasta.setBounds(0,170,250,20);
	    frame.add(rndpasta);
	    
	    JButton updatesett = new JButton("перезагрузить настройки");
	    updatesett.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.updateSettings();
			}
	    });
	    updatesett.setBounds(0,190,250,20);
	    frame.add(updatesett);
	    
	    
	    List<String> tmp = getServers();
	    String[] ips = new String[tmp.size()];
	    for (int i = 0; i < tmp.size();i++) {
	    	ips[i] = tmp.get(i);
	    }
	    this.servers = new JComboBox<>(ips);
	    ActionListener actionListener = new ActionListener() {
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
                JComboBox<String> box = (JComboBox<String>)e.getSource();
                String item = (String)box.getSelectedItem();
                host.setText(item);
            }
        };
        servers.addActionListener(actionListener);
	    servers.setBounds(330, 70, 200, 20);
	    frame.add(servers);
	    
	    JButton updateips = new JButton("обновить мониторинг");
	    updateips.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				List<String> tmp = getServers();
			    String[] ips = new String[tmp.size()];
			    for (int i = 0; i < tmp.size();i++) {
			    	ips[i] = tmp.get(i);
			    }
				servers = new JComboBox<>(ips);
			}
	    });
	    updateips.setBounds(0,210,250,20);
	    frame.add(updateips);
	    
	    
	    this.canvas = new Canvas();
	    canvas.setSize(200, 200);
	    canvas.setBounds(0, 250, 200, 200);
	    frame.add(canvas);
	    frame.setVisible(true);
	    updater();
	    //ServerChatPacket p = new ServerChatPacket();
	}
	
	@SuppressWarnings("deprecation")
	public void SetupTM() {
		//new Thread(()->{
			taskmanager = new JFrame("shit");
			taskmanager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			taskmanager.setSize(500,200);
			taskmanager.setLayout(null);
			taskmanager.move(600, 0);
			
			JLabel mining = new JLabel("копать блок");
			mining.setBounds(0,0,80,20);
			taskmanager.add(mining);
			
			JTextField bname = new JTextField("назв. блока");
		    bname.setBounds(105,0,85,20);
		    taskmanager.add(bname);
		    
		    JTextField bcount = new JTextField("количество");
		    bcount.setBounds(195,0,70,20);
		    taskmanager.add(bcount);
			
			JButton bmine = new JButton("(GO)");
			bmine.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					for (Bot bot : Main.bots) {
						for (int i = 0; i<Integer.parseInt(bcount.getText());i++) {
							bot.rl.tasklist.add("mine "+bname.getText());
						}
					}
				}
		    });
			bmine.setBounds(265,0,80,20);
		    taskmanager.add(bmine);
		    
		    
		    
		    JLabel gotoo = new JLabel("идти на...хуй");
		    gotoo.setBounds(0,21,80,20);
			taskmanager.add(gotoo);
			
			JTextField xyz = new JTextField("корды x и z");
			 xyz.setBounds(105,21,85,20);
		    taskmanager.add(xyz);
		    
		    JTextField radiusw = new JTextField("радиус");
		    radiusw.setBounds(195,21,70,20);
		    taskmanager.add(radiusw);
			
			JButton walkk = new JButton("(GO)");
			walkk.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					for (Bot bot : Main.bots) {
						bot.rl.tasklist.add("goto "+xyz.getText()+" "+radiusw.getText());
					}
				}
		    });
			walkk.setBounds(265,21,80,20);
		    taskmanager.add(walkk);
		    
		    JLabel craftt = new JLabel("скрафтить");
		    craftt.setBounds(0,42,80,20);
			taskmanager.add(craftt);
			
			//-----------
			Crafting c = new Crafting(null);
		    String[] ips = new String[c.Recepies.size()];
		    int i = 0;
		    for (Entry<String, craftingRecepie> entry : c.Recepies.entrySet()) {
		    	ips[i++] = entry.getKey();
		    }
		    JComboBox<String> itemname = new JComboBox<>(ips);
		    itemname.setBounds(105,42,90,20);
		    taskmanager.add(itemname);
		    //----------
		    
		    
		    JTextField howmuch = new JTextField("кол-во");
		    howmuch.setBounds(195,42,70,20);
		    taskmanager.add(howmuch);
			
			JButton craft = new JButton("(GO)");
			craft.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					for (Bot bot : Main.bots) {
						for (int i = 0; i<Integer.parseInt(howmuch.getText());i++) {
							bot.rl.tasklist.add("craft "+itemname.getSelectedItem());
							System.out.println("craft "+itemname.getSelectedItem());
						}
					}
				}
		    });
			craft.setBounds(265,42,80,20);
		    taskmanager.add(craft);
		    
		    
		    JLabel scr = new JLabel("скрипты");
		    scr.setBounds(0,63,80,20);
			taskmanager.add(scr);
		    
			//-----------
			List<String> files = new ArrayList<>();
		    for (final File fileEntry : new File("scripts").listFiles()) {
		        if (fileEntry.isDirectory()) {
		            //huy
		        } else {
		            files.add(fileEntry.getName());
		        }
		    }
		    
		    String[] scrpts = new String[c.Recepies.size()];
		    for (i = 0; i < files.size();i++) {
		    	scrpts[i] = files.get(i);
		    }
		    JComboBox<String> scripts = new JComboBox<>(scrpts);
		    scripts.setBounds(105,63,160,20);
		    taskmanager.add(scripts);
		    //----------
			
		    JButton addscripts = new JButton("(GO)");
		    addscripts.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					File file = new File("scripts/"+scripts.getSelectedItem());
					System.out.println((String) scripts.getSelectedItem());
					for (Bot bot : Main.bots) {
				        if (file.exists()) {
				            try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));){
				            	while (reader.ready()) {
				                	bot.rl.tasklist.add(reader.readLine());
				                }
				            }
				            catch (Exception pohuy) {}
				        } else {
				        	System.out.println("script file dont exisis");
				        	break;
				        }
					}
				}
		    });
		    addscripts.setBounds(265,63,80,20);
		    taskmanager.add(addscripts);
		    
		    
		    JButton jump = new JButton("jump");
		    jump.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					for (Bot bot : Main.bots) {
						ThreadU.sleep(MathU.rnd(10, 500));
						bot.pm.jump();
					}
				}
		    });
		    jump.setBounds(0,90,250,20);
		    taskmanager.add(jump);
		    
		    taskmanager.setVisible(true);
		//}).start();
	}
	
	public void drawPixel(int x, int y) {
		//this.canvas.
	}
	
	public void updater() {
		new Thread(() -> {
		while (true) {
			ThreadU.sleep(2000);
			this.stats.setText("треды:"+Thread.activeCount()+", ботов запущено:"+Main.bots.size()+" ");
		}
		}).start();
	}
	
	public List<String> getServers() {
		String pattern = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):\\d{1,5}\\b";
		List<String> temp = new CopyOnWriteArrayList<>();
        try {
            Document document = Jsoup.connect("https://monitoringminecraft.ru/novie-servera-1.16.5").get();
            Elements elements = document.getElementsByAttributeValue("class", "server");

            for (Element element : elements)
                temp.addAll(findStringsByRegex(element.text(), Pattern.compile(pattern)));

            temp.removeIf(str -> !str.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):\\d{1,5}\\b"));
            
        } catch (Exception ignd) {}
        temp.add("localhost:25565");
        return temp;
	}
	
	public static List<String> findStringsByRegex(String text, Pattern regex) {
        List<String> strings = new ArrayList<>();
        Matcher match = regex.matcher(text);

        while (match.find())
            strings.add(text.substring(match.start(), match.end()));

        return strings;
    }
}
