package org.leolo.irc.giftbot.model;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GiftTypeTest {

    private GiftType giftType;

    @BeforeEach void setUp() {
        giftType = GiftType.builder()
                .singleMessage("single :num: item :list:")
                .multiMessage("multi :num: item :list: last :last:")
                .build();
        giftType.getGifts().add("item1");
        giftType.getGifts().add("item2");
        giftType.getGifts().add("item3");
        giftType.getGifts().add("item4");

        giftType.getOrdinals().add("num1");
        giftType.getOrdinals().add("num2");
        giftType.getOrdinals().add("num3");
        giftType.getOrdinals().add("num4");
    }

    @Test void testBuildMessage() {
        assertEquals("single num1 item item1", giftType.buildMessage(1));
        assertEquals("multi num2 item item2 last item1", giftType.buildMessage(2));
        assertEquals("multi num3 item item3, item2 last item1", giftType.buildMessage(3));
    }

    @Test void testNoException() {
        giftType.buildMessage(1);
        giftType.buildMessage(giftType.getGiftCount());
    }

    @Test void testException() {
        assertThrows(IllegalArgumentException.class, () -> giftType.buildMessage(0));
        assertThrows(IllegalArgumentException.class, () -> giftType.buildMessage(giftType.getGiftCount() + 1));
    }

    @Test void testSize() {
        assertEquals(4, giftType.getGiftCount());
        giftType.getGifts().add("item1");
        assertEquals(4, giftType.getGiftCount());
        giftType.getOrdinals().add("num1");
        assertEquals(5, giftType.getGiftCount());
        giftType.getOrdinals().add("num1");
        assertEquals(5, giftType.getGiftCount());
    }

    @AfterEach void tearDown() {
        giftType = null;
    }

}
