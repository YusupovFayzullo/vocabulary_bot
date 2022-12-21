package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InlineButtonUtil {

    public static ReplyKeyboard getYesNo() {
        return getMarkup(
                getRows(
                        getRow(
                                getButton(InlineKeyboardConstants.YES_DEMO,InlineKeyboardConstants.YES_DATA),
                                getButton(InlineKeyboardConstants.NO_DEMO,InlineKeyboardConstants.NO_DATA)
                                )
                )
        );
    }
    public static ReplyKeyboard getAddMyWords(String chatId, String text) {
        return getMarkup(
                getRows(
                        getRow(
                                getButton(InlineKeyboardConstants.ADD_MY_WORDS_DEMO,
                                        InlineKeyboardConstants.ADD_MY_WORDS_DATA+"/"+text)
                        )
                )
        );
    }
    private static InlineKeyboardMarkup getMarkup(List<List<InlineKeyboardButton>> keyboard){
        return new InlineKeyboardMarkup(keyboard);
    }
    private static List<List<InlineKeyboardButton>> getRows(List<InlineKeyboardButton> ... rows){
        return new ArrayList<>(Arrays.asList(rows));
    }
    private static List<InlineKeyboardButton> getRow(InlineKeyboardButton ... buttons){
        return new ArrayList<>(Arrays.asList(buttons));
    }
    private static InlineKeyboardButton getButton(String demo, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(demo);
        button.setCallbackData(callbackData);
        return button;
    }

    public static ReplyKeyboard getNothingTranslate(String text) {
        return getMarkup(
                getRows(
                        getRow(
                                getButton(InlineKeyboardConstants.NOTHING_TRANSLATE_DEMO,
                                        InlineKeyboardConstants.NOTHING_TRANSLATE_DATA+"/"+text)
                        )
                )
        );    }

    public static ReplyKeyboard getNothingDescription() {
        return getMarkup(
                getRows(
                        getRow(
                                getButton(InlineKeyboardConstants.NOTHING_DESCRIPTION_DEMO,InlineKeyboardConstants.NOTHING_DESCRIPTION_DATA)
                        )
                )
        );
    }

}
