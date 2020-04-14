package io.github.hhservers.lockperms;

import com.google.inject.Inject;
import io.github.hhservers.lockperms.commands.Base;
import io.github.hhservers.lockperms.config.MainConfiguration;
import io.github.hhservers.lockperms.config.ConfigLoader;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;

@Plugin(
        id = "lockperms",
        name = "LockPerms",
        description = "blvxr",
        authors = {
                "blvxr"
        }
)
public class LockPerms {

    private static LockPerms instance;

    public static Boolean isActive;

    @Getter
    private MainConfiguration mainConfig;

    @Getter
    private ConfigLoader configLoader;

    @Inject
    private Logger logger;

    public Logger getLogger(){
        return logger;
    }

    private final GuiceObjectMapperFactory factory;
    private final File configDir;

    @Inject
    public LockPerms(GuiceObjectMapperFactory factory, @ConfigDir(sharedRoot = false) File configDir) {
        this.factory=factory;
        this.configDir=configDir;
        instance=this;
    }


    @Listener
    public void onGamePreInit(GamePreInitializationEvent e) {
        configLoader =new ConfigLoader(this);
        if (configLoader.loadConfig()) mainConfig = configLoader.getMainConf();
    }

    @Listener
    public void onGameInit(GameInitializationEvent e) throws ObjectMappingException {
        instance = this;
        Sponge.getCommandManager().register(instance, Base.build(), "lockperms", "lockp");
        isActive=true;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        //managerConfig = new ManagerConfig(loader);
    }

    @SuppressWarnings("warningToken")
    public void reload() throws IOException, ObjectMappingException {
        //mainConfig = ManagerConfig.reloadConfig();
    }

    @Listener
    public void onGameReload(GameReloadEvent e) throws IOException, ObjectMappingException {
        reload();
    }

    @Listener
    public void commandSentEvent(SendCommandEvent e, @First CommandSource src) throws ObjectMappingException, IOException {
        String cmd = e.getCommand();
        String[] cmdArray = cmd.split(" ");

        if(cmdArray[0].equals("lp") || cmdArray.equals("luckperms:lp")){
            if(mainConfig.getCmdList().isActive) {
                e.setCancelled(true);
                src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&l&8[&bLogged LP command for confirmation by admin.&r&l&8]"));
                String finalCmdString = e.getCommand() + " " + e.getArguments();
                mainConfig.getCmdList().commands.add(finalCmdString);
                configLoader.saveConfig(mainConfig);
                mainConfig= configLoader.getMainConfig();
                //ManagerConfig.saveConfig();
            }
        }
    }

    public static LockPerms getInstance(){
        return instance;
    }

    public GuiceObjectMapperFactory getFactory() {
        return factory;
    }

    public File getConfigDir() {
        return configDir;
    }
}
