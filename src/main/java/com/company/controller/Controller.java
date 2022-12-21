package com.company.controller;


import com.company.container.ComponentContainer;
import com.company.database.DataBaseService;
import com.company.domain.DefinitionsItem;
import com.company.domain.MeaningsItem;
import com.company.domain.Translate;
import com.company.enums.UserStatus;
import com.company.service.TranslateService;
import com.company.service.WordService;
import com.company.service.WorkWithNet;
import com.company.util.InlineButtonUtil;
import com.company.util.InlineKeyboardConstants;
import com.company.util.ReplyKeyboardConstants;
import com.company.util.ReplyKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.company.enums.UserStatus.ENTER_TRANSLATE_FOR_API_WORD;

public class Controller {

    public static void handleMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        String chatId = String.valueOf(message.getChatId());
        String text = message.getText();
        if (text.equals("/start")) {

            sendMessage.setText("Welcome, " + message.getFrom().getFirstName() + "!" +
                    "\n\n I help you to remind words and to save necessary words.\nSend me any word to search");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(ReplyKeyboardConstants.ADD_WORD)) {
            sendMessage.setText("Send word");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            ComponentContainer.userStatus.put(chatId, UserStatus.ENTER_WORD);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            TimerTask task = new TimerTask() {
                public void run() {
                    String sendText = WordService.sendDailyReminder(chatId);
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(sendText);
                    if(!sendText.equals("no")){
                        sendMessage.setParseMode("Markdown");
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    }
                }
            };
            Timer timer = new Timer("Timer");
            timer.schedule(task, 1, 60000);
//            if(LocalTime.now().getSecond()==15){
//                String dailyMessage = WordService.sendDailyReminder();
//                ComponentContainer.MY_BOT.sendMsg(new SendMessage(chatId,dailyMessage));
//            }
        } else if (text.equals(ReplyKeyboardConstants.BACK_TO_MAIN_MENU)) {
            sendMessage.setText("Main menu");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenu());
            ComponentContainer.listContainerMap.remove(chatId);
            ComponentContainer.userStatus.remove(chatId);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);


        } else if (text.equals(ReplyKeyboardConstants.GET_ALL)) {
            SendDocument sendDocument = new SendDocument();
            File file = WordService.getFile(chatId);
            sendDocument.setChatId(chatId);
            sendDocument.setCaption("All words");
            sendDocument.setDocument(new InputFile(file));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
        } else if (!ComponentContainer.userStatus.containsKey(chatId)) {
            sendMessage.setChatId(chatId);
            Translate translate = TranslateService.getTranslate(text);
            if (translate != null) {
                List<MeaningsItem> meanings = translate.getMeanings();
                StringBuilder sb = getStringBuilder(translate, meanings,"");
                sendMessage.setText(sb.toString());
                sendMessage.setParseMode("Markdown");
                sendMessage.setReplyMarkup(InlineButtonUtil.getAddMyWords(chatId, text));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                String audioPath = translate.getPhonetics().get(0).getAudio();
                File file = WorkWithNet.getAudio(audioPath);
                if (file != null) {
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setDocument(new InputFile(file));
                    sendDocument.setChatId(chatId);
                    ComponentContainer.MY_BOT.sendMsg(sendDocument);
                }
                if(WordService.haveNotWord(text,chatId)){

                }
            } else {
                sendMessage.setChatId(chatId);
                sendMessage.setText(text + " is not found");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        } else {
            UserStatus userStatus = ComponentContainer.userStatus.get(chatId);

            if (userStatus.equals(UserStatus.ENTER_WORD)) {
                sendMessage.setChatId(chatId);
                if (!WordService.haveNotWord(text,chatId)) {
                    sendMessage.setText("This word already exists");
                    sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenu());
                    ComponentContainer.userStatus.remove(chatId);
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else {
                    sendMessage.setText("Enter translate ");
                    ComponentContainer.userStatus.put(chatId, UserStatus.ENTER_TRANSLATE);
                    sendMessage.setReplyMarkup(ReplyKeyboardUtil.getBackToUserMainMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    List<String> strings = new ArrayList<>();
                    strings.add(text);
                    ComponentContainer.listContainerMap.put(chatId, strings);
                }
            } else if (userStatus.equals(UserStatus.ENTER_TRANSLATE)) {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Send description of the word");
                sendMessage.setReplyMarkup(InlineButtonUtil.getNothingDescription());
                ComponentContainer.userStatus.put(chatId, UserStatus.ENTER_DESCRIPTION);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                List<String> strings = ComponentContainer.listContainerMap.get(chatId);
                strings.add(text);
                ComponentContainer.listContainerMap.put(chatId, strings);
            } else if (userStatus.equals(UserStatus.ENTER_DESCRIPTION)) {
                DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId() - 1);
                ComponentContainer.MY_BOT.sendMsg(deleteMessage);
                sendMessage.setChatId(chatId);
                sendMessage.setText("Do you want to add examples");
                sendMessage.setReplyMarkup(InlineButtonUtil.getYesNo());
                ComponentContainer.userStatus.remove(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                List<String> strings = ComponentContainer.listContainerMap.get(chatId);
                strings.add(text);
                ComponentContainer.listContainerMap.put(chatId, strings);
            } else if (userStatus.equals(UserStatus.ADD_EXAMPLE)) {

                sendMessage.setChatId(chatId);
                sendMessage.setText("Do you want to add other example");
                sendMessage.setReplyMarkup(InlineButtonUtil.getYesNo());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                List<String> strings = ComponentContainer.listContainerMap.get(chatId);
                strings.add(text);
                ComponentContainer.listContainerMap.put(chatId, strings);
            }else if(userStatus.equals(ENTER_TRANSLATE_FOR_API_WORD)){
                String word = ComponentContainer.stringContainerMap.get(chatId);
                Translate translate = TranslateService.getTranslate(word);
                if (translate!=null) {
                    sendMessage.setChatId(chatId);
                    List<MeaningsItem> meanings = translate.getMeanings();
                    StringBuilder stringBuilder = getStringBuilder(translate, meanings,text);
                    DefinitionsItem definitionsItem = meanings.get(0).getDefinitions().get(0);
                    DataBaseService.saveWord(chatId, word, text, definitionsItem.getDefinition(),
                            new ArrayList<>(List.of(definitionsItem.getExample())));
                    sendMessage.setText(stringBuilder + "\nSuccessfully added");
                    sendMessage.setParseMode("Markdown");
                    ComponentContainer.stringContainerMap.remove(chatId, text);
                    sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenu());
                    ComponentContainer.userStatus.remove(chatId, ENTER_TRANSLATE_FOR_API_WORD);
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                }
            }
        }
    }

    public static void handleQuery(Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        String text = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
        ComponentContainer.MY_BOT.sendMsg(deleteMessage);
        if (data.equals(InlineKeyboardConstants.YES_DATA)) {
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            sendMessage.setText("Send example ");
            ComponentContainer.userStatus.put(chatId, UserStatus.ADD_EXAMPLE);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (data.equals(InlineKeyboardConstants.NO_DATA)) {
            List<String> strings = ComponentContainer.listContainerMap.get(chatId);
            if (strings.isEmpty()) {
                DataBaseService.saveWord(chatId, strings.get(0), strings.get(1), strings.get(2), new ArrayList<>());
            } else {
                DataBaseService.saveWord(chatId, strings.get(0), strings.get(1),strings.get(2), strings.subList(3, strings.size()));
            }
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenu());
            sendMessage.setText(strings.get(0) + " is successfully added");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            ComponentContainer.userStatus.remove(chatId);
            ComponentContainer.listContainerMap.remove(chatId);
        } else if (data.equals(InlineKeyboardConstants.NOTHING_DESCRIPTION_DATA)) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getBackToUserMainMenu());
            sendMessage.setText("All right!");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            sendMessage.setText("Do you want to add any example ? ");
            sendMessage.setReplyMarkup(InlineButtonUtil.getYesNo());
            List<String> strings = ComponentContainer.listContainerMap.get(chatId);
            strings.add("");
            ComponentContainer.listContainerMap.put(chatId, strings);
            ComponentContainer.userStatus.remove(chatId);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (data.startsWith(InlineKeyboardConstants.ADD_MY_WORDS_DATA)) {
            String[] split = data.split("/");
            String word = split[1];
            sendMessage.setText("Send translate of "+word);
            ComponentContainer.stringContainerMap.put(chatId,word);
            sendMessage.setReplyMarkup(InlineButtonUtil.getNothingTranslate(word));
            ComponentContainer.userStatus.put(chatId, ENTER_TRANSLATE_FOR_API_WORD);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }else if (data.startsWith(InlineKeyboardConstants.NOTHING_TRANSLATE_DATA)) {
            String[] split = data.split("/");
            String word = split[1];
            Translate translate = TranslateService.getTranslate(word);
            if (translate!=null){
                List<MeaningsItem> meanings = translate.getMeanings();
                StringBuilder stringBuilder = getStringBuilder(translate, meanings,"");
                DefinitionsItem definitionsItem = meanings.get(0).getDefinitions().get(0);

                DataBaseService.saveWord(chatId,word,"",definitionsItem.getDefinition(),
                        new ArrayList<>(List.of(definitionsItem.getExample())));

                sendMessage.setText(stringBuilder+"\nSuccessfully added");
                sendMessage.setParseMode("Markdown");
                ComponentContainer.stringContainerMap.remove(chatId,text);
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenu());
                ComponentContainer.userStatus.remove(chatId, ENTER_TRANSLATE_FOR_API_WORD);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }
    }

    private static StringBuilder getStringBuilder(Translate translate, List<MeaningsItem> meanings,String  translated) {
        StringBuilder sb = new StringBuilder("");
        sb.append("*").append(translate.getWord()).append("* - *").append(translated).append("*\n\n");
        for (MeaningsItem meaning : meanings) {
            DefinitionsItem definitionsItem = meaning.getDefinitions().get(0);
            sb.append("*").append(meaning.getPartOfSpeech()).append("* - ").append(definitionsItem.getDefinition());
            sb.append("\n*example* : ").append(definitionsItem.getExample()).append("\n\n");
        }
        return sb;
    }
}
