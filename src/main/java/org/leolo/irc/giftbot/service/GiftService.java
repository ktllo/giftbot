package org.leolo.irc.giftbot.service;

import org.leolo.irc.giftbot.ConfigurationManager;
import org.leolo.irc.giftbot.model.GiftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class GiftService {
    private static GiftService instance;

    private final ConfigurationManager config = ConfigurationManager.getInstance();
    private static Logger log = LoggerFactory.getLogger(GiftService.class);

    private Map<String, GiftType> giftTypes = new TreeMap<>();

    public synchronized static GiftService getInstance() {
        if (instance == null) {
            instance = new GiftService();
        }
        return instance;
    }

    private GiftService() {
        Properties prop = new Properties();
        try {
            prop.load(ClassLoader.getSystemResourceAsStream("gifts.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String [] keys = prop.getProperty("keys").split(",");
gift:   for (String key : keys) {
            log.info("Processing key {}", key);
            GiftType gift = GiftType.builder()
                    .singleMessage(prop.getProperty(key+".single"))
                    .multiMessage(prop.getProperty(key+".multi"))
                    .build();
            if (gift.getSingleMessage()==null || gift.getMultiMessage()==null) {
                log.error("Malformed gift type {} - Missing message pattern", gift);
                continue;
            }
            String lang = prop.getProperty(key+".lang");
            if (lang==null) {
                lang = "en";
                log.warn("Malformed gift type {} - Missing language type - Default of en used", lang);
            }
            for (int i=1; i<=12 ; i++) {
                String loopGift = prop.getProperty(key+"."+i);
                String ordinal = prop.getProperty(lang+"."+i);
                if (loopGift==null || loopGift.isEmpty()) {
                    log.warn("Malformed gift type {} - Missing {}-th gift pattern", gift, i);
                    continue gift;
                }
                if (ordinal==null || ordinal.isEmpty()) {
                    log.warn("Malformed gift type {} - Missing {}-th ordinal", gift, i);
                    continue gift;
                }
                gift.getGifts().add(loopGift);
                gift.getOrdinals().add(ordinal);
            }
            log.info("Adding gift {}", gift);
            giftTypes.put(key, gift);
        }
        log.info("Loaded {} gift types", giftTypes.size());
    }

    public List<String> getGiftTypes() {
        return new ArrayList<>(giftTypes.keySet());
    }

    public GiftType getGiftType(String key) {
        if (giftTypes.containsKey(key)) {
            return giftTypes.get(key);
        } else {
            return giftTypes.get(config.getProperty("default_key"));
        }
    }
}
