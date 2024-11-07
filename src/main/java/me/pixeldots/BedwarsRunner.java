package me.pixeldots;

import org.apache.logging.log4j.Logger;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import me.pixeldots.Commands.*;
import me.pixeldots.Events.EventsListener;
import me.pixeldots.Events.PlayerEventListener;
import me.pixeldots.Extras.InvisibilityHandler;
import me.pixeldots.Game.AsyncBedwarsGameTicker;
import me.pixeldots.Game.BedwarsGame;
import me.pixeldots.Game.BedwarsGameTicker;
import me.pixeldots.Game.VariableHandler;
import me.pixeldots.SaveData.DataHandler;
import me.pixeldots.Utils.Utils;

public class BedwarsRunner extends JavaPlugin
{

    public static World world;

    public static BedwarsGameTicker tickHandler;
    public static AsyncBedwarsGameTicker asyncTickHandler;
    public static VariableHandler Variables;
    public static InvisibilityHandler invisibilityHandler;

	public static boolean isRunning = false;
    public static boolean isStarting = false;
    public static boolean isTesting = false;
    public static long StartingTime = 0;
    
    public static String itemShopPath = "/itemshop.json";
    public static String upgradeShopPath = "/upgradeshop.json";
    public static String playerQuickBuyPath = "/quickbuy/%PlayerUUID%.json";
	public static String savePath = "/BedwarsRunner.json";

    public static Logger logger;
    public static JavaPlugin instance;

    @Override
    public void onEnable() {
        logger = this.getLog4JLogger();
        instance = this;

        savePath = this.getDataFolder().getAbsolutePath() + savePath;
        itemShopPath = this.getDataFolder().getAbsolutePath() + itemShopPath;
        upgradeShopPath = this.getDataFolder().getAbsolutePath() + upgradeShopPath;
        playerQuickBuyPath = this.getDataFolder().getAbsolutePath() + playerQuickBuyPath;
        BedwarsConf.loadConf(this);

        Variables = new VariableHandler();

        DataHandler.Load(savePath);
        DataHandler.LoadItemShops();
        this.addCommand("bedwarsgame", new BedwarsGameCommand());
        this.addCommand("bedwarsgenerators", new BedwarsGeneratorsCommand());
        this.addCommand("bedwarsteams", new BedwarsTeamsCommand());
        this.addCommand("bedwarstools", new BedwarsToolsCommand());
        this.addCommand("bedwarsgui", new BedwarsGUICommand());
        this.addCommand("bedwarsupgrade", new BedwarsUpgradeCommand());
        this.addCommand("bedwarsspawn", new BedwarsSpawnCommand());

        getServer().getPluginManager().registerEvents(new EventsListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        invisibilityHandler = new InvisibilityHandler();
        invisibilityHandler.Register();

        tickHandler = new BedwarsGameTicker();
        tickHandler.runTaskTimer(this, 0, 0);

        asyncTickHandler = new AsyncBedwarsGameTicker();
        asyncTickHandler.runTaskTimerAsynchronously(this, 0, 0);

        if (BedwarsConf.autoStartWithServer) {
            Utils.runDelayedTask(() -> {
                BedwarsRunner.startGame();
            }, Utils.toTickTime(BedwarsConf.autoStartDelay));
        }
    }

    @Override
    public void onDisable() {
        if (isRunning || isStarting) endGame();
    }

    public static void startGame() {
        if (isStarting || isRunning) Utils.Logger().info("A game is already running");
		Utils.Logger().info("Starting Bedwars game");
        BedwarsGame.start();
	}

    public static void runGame() {
        if (isStarting || isRunning) Utils.Logger().info("A game is already running");
		Utils.Logger().info("Starting Bedwars game");
        BedwarsGame.run();
	}

	public static void endGame() {
        if (!isStarting && !isRunning) Utils.Logger().info("There is no game running");
		Utils.Logger().info("Stopping Bedwars game");
		BedwarsGame.stop();
	}

    public void addCommand(String name, CommandExecutor executor) {
        PluginCommand command = this.getCommand(name);
        if (executor instanceof TabExecutor) command.setTabCompleter((TabExecutor)executor);
        command.setExecutor(executor);
    }

}
