package com.company.database;


import com.company.domain.Word;
import org.postgresql.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataBaseService {

	private final static String url="jdbc:postgresql://localhost:5432/translate_db";
	private  final static String dbUser="postgres";
	private  final static String dbpassword="08203";


	public static void saveWord(String chatId, String word, String translate,String description, List<String> examples ) {

		String w= examples.toString();
		try {
			DriverManager.registerDriver(new org.postgresql.Driver());
			Class.forName("org.postgresql.Driver");
			Connection connection= DriverManager.getConnection(url,dbUser,dbpassword);

			String query="""
     insert into words(chatid,word,translate,description,examples) 
     values(?,?,?,?,?);
					""";

			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1,chatId);
			ps.setString(2,word);
			ps.setString(3,translate);
			ps.setString(4,description);
			ps.setString(5, w);

			int execute = ps.executeUpdate();
			System.out.println("execute = " + execute);


		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}


	}


	public static List<Word> getTodos(String chatId) {

		List<Word> words = getWordList();
		return words.stream().filter(e->e.getUserId().equals(chatId)).collect(Collectors.toList());
	}

	public static List<Word> getWordList(){

		List<Word> words = new ArrayList<>();

		try {

			DriverManager.registerDriver(new Driver());
			Class.forName("org.postgresql.Driver");

			Connection connection= DriverManager.getConnection(url,dbUser,dbpassword);

			Statement statement = connection.createStatement();

			String query = """ 
                    select * from words order by id; 
                    """;

			ResultSet rs = statement.executeQuery(query);

			while (rs.next()){
				int id = rs.getInt(1);
				String userId= rs.getString("chatid");
				String word = rs.getString("word");
				String translate = rs.getString("translate");
				String des = rs.getString("description");
				String examples = rs.getString("examples");
				words.add(new Word(String.valueOf(id),userId,word,translate,des,examples.lines().toList()));
			}


		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return words;
	}


}
