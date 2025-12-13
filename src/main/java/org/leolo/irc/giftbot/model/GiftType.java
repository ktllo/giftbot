package org.leolo.irc.giftbot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Getter
@Builder
@Slf4j
public class GiftType {
    private String singleMessage;
    private String multiMessage;

    @Builder.Default
    private ArrayList<String> gifts = new ArrayList<>();
    @Builder.Default
    private ArrayList<String> ordinals = new ArrayList<>();

    public String buildMessage(int day) {
        if (day < 1 || day > getGiftCount()) {
            throw new IllegalArgumentException("Invalid day: " + day);
        }
        if (day == 1) {
            String pattern = singleMessage;
            pattern = pattern.replace(":num:", ordinals.getFirst());
            pattern = pattern.replace(":list:", gifts.getFirst());
            return pattern;
        } else {
            day--; //array index are 0-indexed
            String pattern = multiMessage;
            pattern = pattern.replace(":num:", ordinals.get(day));
            ArrayList<String> todayGift = new ArrayList<>();
            for (int i = day; i > 0; i--) {
                todayGift.add(gifts.get(i));
            }
            pattern = pattern.replace(":list:", String.join(", ", todayGift));
            pattern = pattern.replace(":last:", gifts.getFirst()); //Last gift is the one from day 1
            return pattern;
        }
    }

    public int getGiftCount() {
        return Math.min(gifts.size(), ordinals.size());
    }
}
