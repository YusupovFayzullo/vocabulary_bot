package com.company.service;

import com.company.database.DataBaseService;
import com.company.domain.Word;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class WordService {

      public static File getFile(String chatId) {
        List<Word> words = DataBaseService.getTodos(chatId);
          System.out.println("salom");
          System.out.println(words);
          if (words==null || words.isEmpty()) return null;
        File file =new File("src/main/resources/user_words.xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Words");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("id");
            row.createCell(1).setCellValue("word");
            row.createCell(2).setCellValue("translate");
            row.createCell(3).setCellValue("examples");
            int rowIndex=0;
            for (Word word :words ) {
                XSSFRow row1 = sheet.createRow(++rowIndex);
                row1.createCell(0).setCellValue(word.getId());
                row1.createCell(1).setCellValue(word.getWord());
                row1.createCell(2).setCellValue(word.getTranslate().toString());
                row1.createCell(3).setCellValue(word.getExamples().toString());
            }
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
        } catch (IOException e) {
            return null;
        }
        return file;
    }


    public static boolean haveWord(String text, String chatId) {
        List<Word> words = DataBaseService.getTodos(chatId);
        Word word = words.stream().filter(e -> e.getWord().equalsIgnoreCase(text)).findAny().orElse(null);
        return word==null;
    }

    public static Word getRandomWord(String chatId) {

        List<Word> words = DataBaseService.getWordList();

        List<Word> userWords = words.stream().filter(word -> word.getUserId().equals(chatId)).toList();
        Random random = new Random();

        int randomNumber = random.nextInt(userWords.size()-1);




        return userWords.get(randomNumber);
      }
      public static String sendDailyReminder(String chatId) {

          Word randomWord = getRandomWord(chatId);
          StringBuilder dailymessage;
          if(randomWord!=null){
              dailymessage = new StringBuilder();
              dailymessage.append("*").append(randomWord.getWord()).append("* - *").append(randomWord.getTranslate()).append("*");
              dailymessage.append("\n*definition*: ").append(randomWord.getDescription());
              if(!randomWord.getExamples().isEmpty()){
                  dailymessage.append("\n*examples* : ");
                  for (String example : randomWord.getExamples()) {
                      dailymessage.append(example).append("\n");
                  }
              }
              return dailymessage.toString();
          }
          return "no";
    }

    public static boolean haveNotWord(String text, String chatId) {
        List<Word> words = DataBaseService.getTodos(chatId);
        Word word =words.stream().filter(e -> e.getWord().equalsIgnoreCase(text)).findAny().orElse(null);
        return word==null;
    }
}
