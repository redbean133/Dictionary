import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class DictionaryManagement {
    public static void insertFromFile() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:src/anh_viet.db");
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM anh_viet");
        while (rs.next()) {
            int index = rs.getInt("id");
            String english = rs.getString("word");
            String vietnamese = rs.getString("content");
            Word newWord = new Word(english.toLowerCase(), vietnamese);
            Dictionary.words.add(newWord);
        }
        Collections.sort(Dictionary.words, new WordComparator());
    }

    public static Word findWordInWordsArray(String english) {
        english = english.toLowerCase();
        int l = 0; //left
        int r = Dictionary.words.size(); //right
        while (l <= r) {
            int mid = (l + r) / 2;
            if (Dictionary.words.get(mid).getWordTarget().compareTo(english) < 0) {
                l = mid + 1;
            } else if (Dictionary.words.get(mid).getWordTarget().compareTo(english) > 0) {
                r = mid - 1;
            } else {
                return (Dictionary.words.get(mid));
            }
        }
        return (Dictionary.words.get(l));
    }

    public static void deleteWord(String english) throws ClassNotFoundException, SQLException {
        Word deleteWord = findWordInWordsArray(english);
        int indexOfDeleteWord = Dictionary.words.indexOf(deleteWord);
        Dictionary.words.remove(indexOfDeleteWord);
        // delete word in file
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:src/anh_viet.db");
        connection.setAutoCommit(false);
        String sql = "DELETE FROM anh_viet WHERE word = ?";
        PreparedStatement p = connection.prepareStatement(sql);
        p.setString(1, english);
        p.executeUpdate();
        connection.commit();
    }

    public static ArrayList<String> findGuessList(String findWord) {
        Word word = findWordInWordsArray(findWord);
        int l = Dictionary.words.indexOf(word);
        int countOfGuessWord = 0;
        ArrayList<String> guestWord = new ArrayList<String>();
        while (Dictionary.words.get(l).getWordTarget().indexOf(findWord) == 0 && countOfGuessWord <= 100) {
            guestWord.add(Dictionary.words.get(l).getWordTarget());
            l ++;
            countOfGuessWord ++;
        }
        return guestWord;
    }
}

