package org.leolo.irc.giftbot.listener;

import org.leolo.irc.giftbot.ConfigurationManager;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiftBotListener extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(GiftBotListener.class);
    private final ConfigurationManager configurationManager = ConfigurationManager.getInstance();

    @Override
    public void onInvite(InviteEvent event) throws Exception {
        super.onInvite(event);
        if (configurationManager.getProperty("irc.join-on-invite").equals("true")) {
            log.info("Joining {} on invitation by {}", event.getChannel(), event.getUser());
            event.getBot().send().joinChannel(event.getChannel());
        }
    }

    @Override
    public void onKick(KickEvent event) throws Exception {
        super.onKick(event);
        User kickedUser = event.getRecipient();
        if (kickedUser != null && kickedUser.equals(event.getBot().getUserBot())) {
            log.error("We are being kicked from {} because {}", event.getChannel().getName(), event.getReason());
        }
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        super.onPrivateMessage(event);
        messageEvent(event);
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);
        messageEvent(event);
    }

    private void messageEvent(GenericMessageEvent event) {
        String message = event.getMessage();
        String [] messageTokens = message.split(" ");
        if (
                messageTokens.length == 2
                && messageTokens[0].equals("!halt")
                && messageTokens[1].equals(configurationManager.getProperty("halt_key"))
        ) {
            log.error("Received halt command");
            System.exit(100);
        }
    }
}
