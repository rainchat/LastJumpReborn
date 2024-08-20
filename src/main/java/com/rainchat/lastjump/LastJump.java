package com.rainchat.lastjump;

import com.rainchat.lastjump.common.hooks.PlaceholderAPIBridge;
import com.rainchat.lastjump.common.hooks.PlaceholderAPIHook;
import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.common.utils.general.ArenaWriter;
import com.rainchat.lastjump.common.utils.general.Message;
import com.rainchat.lastjump.common.utils.general.PersistentDataSaver;
import com.rainchat.lastjump.common.utils.storage.YAML;
import com.rainchat.lastjump.plugin.data.database.Database;
import com.rainchat.lastjump.plugin.managers.ArenaManager;
import com.rainchat.lastjump.plugin.managers.PlayerScoreManager;
import com.rainchat.lastjump.plugin.managers.SelectManager;
import com.rainchat.lastjump.plugin.resourse.commands.ArenaAdminCommands;
import com.rainchat.lastjump.plugin.resourse.commands.ArenaUserCommands;
import com.rainchat.lastjump.plugin.resourse.listeners.ArenaListener;
import com.rainchat.lastjump.plugin.resourse.listeners.CuboidCreateListener;
import com.rainchat.lastjump.plugin.resourse.listeners.PlatformsListener;
import com.rainchat.lastjump.plugin.resourse.listeners.WorldListener;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class LastJump extends JavaPlugin{
	
	/*
	 * Changelog:
	 * Tracks quits
	 */
	private YAML config;
	private YAML arenas;

	private static LastJump instance;
	private static Database database;
	PlayerScoreManager playerScoreManager;
	private ArenaManager arenaManager;
	
	/**
	 * Enables the plugin
	 */
	public void onEnable() {
		instance = this;
		AUtility.initialize(this);
		new PersistentDataSaver(this);

		this.config = new YAML(this,"config");
		this.config.setup();
		this.arenas = new YAML(this,"arenas");
		this.arenas.setup();
		registerMessages(new YAML(this, "messages"));

		//Register manager
		database = new Database(this, config);
		database.set();

		SelectManager selectManager = new SelectManager();
		this.playerScoreManager = new PlayerScoreManager(database);
		arenaManager = new ArenaManager(this, arenas, database);
		ArenaWriter.setup(arenaManager, selectManager, playerScoreManager);


		//Commands register
		CommandManager commandManager = new CommandManager(this);
		commandManager.register(
				new ArenaUserCommands(playerScoreManager,arenaManager),
				new ArenaAdminCommands(playerScoreManager,arenaManager)
				);

		//Placeholder register
		PlaceholderAPIBridge placeholderAPIBridge = new PlaceholderAPIBridge();
		placeholderAPIBridge.setupPlugin();
		if (PlaceholderAPIBridge.hasValidPlugin()) {
			getLogger().info("Successfully hooked into PlaceholderAPI.");
			new PlaceholderAPIHook(this, arenaManager, selectManager).register();
		}

		//Listener register
		getLogger().info("Registered " + registerListeners(
				new ArenaListener(this, playerScoreManager),
				new CuboidCreateListener(selectManager),
				new PlatformsListener(),
				new WorldListener(arenaManager)
		) + " listener(s).");
	}

	public static LastJump getInstance() {
		return instance;
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	public PlayerScoreManager getScoreManager() {
		return playerScoreManager;
	}

	public static Database getDatabase() {
		return database;
	}

	private int registerListeners(Listener... listeners) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Arrays.asList(listeners).forEach(listener -> {
			atomicInteger.getAndAdd(1);
			getServer().getPluginManager().registerEvents(listener, this);
		});
		return atomicInteger.get();
	}

	public void onReload() throws SQLException {
		registerMessages(new YAML(this, "messages"));
		this.config.reload();
		this.arenas.reload();
		database.close();
		database.set();
		arenaManager.start();
	}

	private void registerMessages(YAML yaml) {
		yaml.setup();
		Message.setConfiguration(yaml.getFileConfiguration());

		int index = 0;
		for (Message message : Message.values()) {
			if (message.getList() != null) {
				yaml.getFileConfiguration().set(message.getPath(), message.getList());
			} else {
				index += 1;
				yaml.getFileConfiguration().set(message.getPath(), message.getDef());
			}
		}
		yaml.save();
		getLogger().info("Registered " + index + " message(s).");
	}

	public void onDisable() {
		arenaManager.closeServer();
	}

}
