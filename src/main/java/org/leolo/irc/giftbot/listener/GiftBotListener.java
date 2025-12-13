package org.leolo.irc.giftbot.listener;

import org.leolo.irc.giftbot.ConfigurationManager;
import org.leolo.irc.giftbot.service.GiftService;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Hashtable;

public class GiftBotListener extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(GiftBotListener.class);
    private final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    private final GiftService giftService = GiftService.getInstance();

    private final long COOLDOWN_PERIOD = Long.parseLong(configurationManager.getProperty("cooldown"));

    private Instant baseTime = Instant.parse(configurationManager.getProperty("baseTime"));

    private Hashtable<String, Instant> lastRequest = new Hashtable<>();

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
        Instant now = Instant.now();
        if (messageTokens.length == 0) {
            return;
        }
        if (
                messageTokens.length == 2
                && messageTokens[0].equals("!halt")
                && messageTokens[1].equals(configurationManager.getProperty("halt_key"))
        ) {
            log.error("Received halt command from {}", event.getUser().getHostmask());
            System.exit(100);
        } else if (
                messageTokens[0].equalsIgnoreCase("!listtype")
        ) {
            String list = String.join(", ", giftService.getGiftTypes());
            event.respondWith("Available gifts: " + list);
        } else if (
                messageTokens[0].equalsIgnoreCase("!gift")
        ) {
            String giftType = messageTokens.length > 1 ? messageTokens[1] : configurationManager.getProperty("default_key");
            String hostmask = event.getUser().getHostmask().toLowerCase();
            log.info("{} gift requested from {}", giftType, event.getUser().getHostmask());
            if (lastRequest.containsKey(hostmask)) {
                Instant lastRequestTime = lastRequest.get(hostmask);
                long diff = Duration.between(lastRequestTime, now).toSeconds();
                log.debug("Time from last request for {} is {} seconds", event.getUser().getHostmask(), diff);
                if (diff < COOLDOWN_PERIOD) {
                    log.info("Last request from {} is too recent. {} seconds only and {} required",
                            event.getUser().getHostmask(),
                            diff,
                            COOLDOWN_PERIOD
                    );
                    return;
                }
            }
            int day = (int)Duration.between(baseTime, now).toDays();
            log.info("{} day requested from {}", day, baseTime);
            event.respondWith(giftService.getGiftType(giftType).buildMessage(day + 1));
            lastRequest.put(hostmask, now);
        }
    }
}
