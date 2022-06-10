import java.util.Comparator;
// Để sort theo Word Target  :>
public class WordComparator implements Comparator<Word> {
    public int compare(Word w1, Word w2) {
        return w1.getWordTarget().compareTo(w2.getWordTarget());
    }
}
