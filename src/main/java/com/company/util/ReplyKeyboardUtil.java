package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplyKeyboardUtil {
    private static ReplyKeyboard getMarkup(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }
    private static List<KeyboardRow> getKeyboard(KeyboardRow ... rows){
        return new ArrayList<>(Arrays.asList(rows));
    }
    private static KeyboardRow getRow(KeyboardButton... buttons){
        return new KeyboardRow(Arrays.asList(buttons));
    }
    private static KeyboardButton getButton(String data){
        return new KeyboardButton(data);
    }

    public static ReplyKeyboard getMainMenu() {
        return getMarkup(getKeyboard(
                getRow(
                        getButton(ReplyKeyboardConstants.ADD_WORD),
                        getButton(ReplyKeyboardConstants.GET_ALL))));

    }

    public static ReplyKeyboard getBackToUserMainMenu() {
        return getMarkup(getKeyboard(
                getRow(
                        getButton(ReplyKeyboardConstants.BACK_TO_MAIN_MENU))));
    }
}
