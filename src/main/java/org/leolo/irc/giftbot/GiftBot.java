package org.leolo.irc.giftbot;

import org.leolo.irc.giftbot.listener.GiftBotListener;
import org.leolo.irc.giftbot.service.GiftService;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class GiftBot {
    private static Logger log = LoggerFactory.getLogger(GiftBot.class);


    public static void main(String[] args) {
        log.info("Starting GiftBot...");
        ConfigurationManager config = ConfigurationManager.getInstance();
        //Create the instance of GiftService
        GiftService gs = GiftService.getInstance();
        Configuration configuration = new Configuration.Builder()
                .setName(config.getProperty("irc.nick"))
                .addServer(
                        config.getProperty("irc.host"),
                        Integer.parseInt(config.getProperty("irc.port","6667"))
                )
                .addListener(new GiftBotListener())
                .addAutoJoinChannels(Arrays.asList(config.getProperty("irc.channels").split(",")))
                .buildConfiguration();
        log.info("Config built");
        try (PircBotX bot = new PircBotX(configuration)) {
            bot.startBot();
        } catch (IOException|IrcException e) {
            throw new RuntimeException(e);
        }
        log.info("Ending GiftBot...");
    }


}
