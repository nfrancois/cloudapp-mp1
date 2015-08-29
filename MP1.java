import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};


    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        List<String> lines = readFile();
        List<String> words = flatMapByTokens(lines);
        List<String> filteredWords = filterWord(words);
        Map<String, Integer> tuples = countAsMap(filteredWords);
        List<Map.Entry<String, Integer>> ordered = orderByValue(tuples);
        List<Map.Entry<String, Integer>> firsts = ordered.subList(0, 20);
        List<String> top20 = mapToWords(firsts);
        return top20.toArray(new String[20]);
    }

    private List<String> readFile() throws IOException {
        Path path = Paths.get(inputFileName);
        return Files.readAllLines(path, Charset.defaultCharset());
    }

    private List<String> flatMapByTokens(List<String> lines){
        List<String> tokens = new ArrayList<>();
        for(String line : lines){
            StringTokenizer st = new StringTokenizer(line, delimiters);
            while(st.hasMoreTokens()) {
                tokens.add(st.nextToken().toLowerCase());
            }
        }
        return tokens;
    }

    private List<Map.Entry<String, Integer>> orderByValue(Map<String, Integer> tuples){
        List<Map.Entry<String, Integer>> ordered = new ArrayList<>();
        ordered.addAll(tuples.entrySet());
        Comparator<Map.Entry<String, Integer>> sortByValue = new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(final Map.Entry<String, Integer> left, final Map.Entry<String, Integer> right) {
                return right.getValue().compareTo(left.getValue());
            }
        };
        Collections.sort(ordered, sortByValue);
        return ordered;
    }

    private List<String> mapToWords(final List<Map.Entry<String, Integer>> firsts) {
        List<String> words = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : firsts){
            words.add(entry.getKey());
        }
        return words;
    }

    private List<String> filterWord(List<String> words){
        List<String> stopWords = Arrays.asList(stopWordsArray);
        List<String> filtered = new ArrayList<>();
        for(String word : words) {
            if(!stopWords.contains(word)){
                filtered.add(word);
            }
        }
        return filtered;
    }

    private Map<String, Integer> countAsMap(List<String> words){
        Map<String, Integer> counts = new HashMap<>();
        for(String word : words){
            if(!counts.containsKey(word)){
                counts.put(word, 1);
            } else {
                counts.put(word, counts.get(word)+1);
            }
        }
        return counts;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
