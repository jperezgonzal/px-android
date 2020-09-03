package com.mercadopago.android.px.internal.util;

public final class MPCardMaskUtil {

    private static final String BASE_FRONT_SECURITY_CODE = "••••";
    private static final int LAST_DIGITS_LENGTH = 4;
    private static final char HIDDEN_NUMBER_CHAR = "•".charAt(0);

    private static final int CARD_NUMBER_AMEX_LENGTH = 15;
    private static final int CARD_NUMBER_DINERS_LENGTH = 14;
    private static final int CARD_NUMBER_MAESTRO_SETTING_1_LENGTH = 18;
    private static final int CARD_NUMBER_MAESTRO_SETTING_2_LENGTH = 19;

    private MPCardMaskUtil() {
    }

    public static String getCardNumberHidden(final int cardNumberLength, final String lastFourDigits) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardNumberLength - LAST_DIGITS_LENGTH; i++) {
            sb.append(HIDDEN_NUMBER_CHAR);
        }
        sb.append(lastFourDigits);
        return buildNumberWithMask(cardNumberLength, sb.toString());
    }

    public static String buildNumberWithMask(final int cardLength, final String number) {
        String result = "";
        if (cardLength == CARD_NUMBER_AMEX_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 4; i < 10; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 10; i < CARD_NUMBER_AMEX_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else if (cardLength == CARD_NUMBER_DINERS_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 4; i < 10; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 10; i < CARD_NUMBER_DINERS_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else if (cardLength == CARD_NUMBER_MAESTRO_SETTING_1_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 10; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 10; i < 15; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 15; i < CARD_NUMBER_MAESTRO_SETTING_1_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else if (cardLength == CARD_NUMBER_MAESTRO_SETTING_2_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 9; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 9; i < CARD_NUMBER_MAESTRO_SETTING_2_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else {
            final StringBuffer mask = new StringBuffer();
            for (int i = 1; i <= cardLength; i++) {
                mask.append(getCharOfCard(number, i - 1));
                if (i % 4 == 0) {
                    mask.append(" ");
                }
            }
            result = mask.toString();
        }
        return result;
    }

    public static char getCharOfCard(final String number, final int i) {
        if (i < number.length()) {
            return number.charAt(i);
        }
        return "•".charAt(0);
    }

    public static String buildSecurityCode(final int securityCodeLength, final String code) {
        final StringBuffer securityCode = new StringBuffer();
        if (code == null || code.isEmpty()) {
            return BASE_FRONT_SECURITY_CODE;
        }
        for (int i = 0; i < securityCodeLength; i++) {
            final char charOfCard = getCharOfCard(code, i);
            securityCode.append(charOfCard);
        }
        return securityCode.toString();
    }
}