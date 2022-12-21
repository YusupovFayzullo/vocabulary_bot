package com.company.container;

import com.company.bot.MyBot;
import com.company.enums.UserStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ComponentContainer {
    String BOT_TOKEN = "5905751474:AAEwxLG1SgZwffynM_dzshrB84tw6SEgOB8";
    String BOT_USERNAME = "https://t.me/TranslateFFD_bot";

    MyBot MY_BOT = new MyBot();
    Map<String , UserStatus> userStatus = new HashMap<>();
    Map<String , List<String >> objectMap = new HashMap<>();
    Map<String , List<String >> listContainerMap = new HashMap<>();
    Map<String , String> stringContainerMap = new HashMap<>();
}
