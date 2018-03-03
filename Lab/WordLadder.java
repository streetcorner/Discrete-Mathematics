package wordladder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class WordLadder {
    
    /*
     * 从文件中将单词读入到List<String>. 假设一行一个单词,单词没有重复
     */
    public static List<String> read(final String filepath) {
        List<String> wordList = new ArrayList<String>();

        File file = new File("resource/vacabulary.txt");
        FileReader fr = null;
        BufferedReader br = null;
        String lines = null;
        String word = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = null;
            int index = -1;
            while ((lines = br.readLine()) != null) {
                // word = line.substring(0, line.indexOf(" ")).trim();
                line = lines.trim();
                index = line.indexOf(" ");
                if (index == -1)
                    continue;
                word = line.substring(0, line.indexOf(" "));
                wordList.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
                br.close();
            } catch (IOException e) {

            }
        }

        return wordList;
    }

    /**
     * 根据单词构造邻接表
     * @param theWords 包含所有单词List
     * @return Map<String, List<string>>key:表示某个单词, Value:与该单词只差一个字符的单词
     */
    public static Map<String, List<String>> computeAdjacentWords(
            List<String> theWords) {
        Map<String, List<String>> adjWords = new TreeMap<>();
        Map<Integer, List<String>> wordsByLength = new TreeMap<>();

        for (String word : theWords)
            update(wordsByLength, word.length(), word);

        for (List<String> groupWords : wordsByLength.values()) {
            String[] words = new String[groupWords.size()];
            groupWords.toArray(words);

            for (int i = 0; i < words.length; i++)
                for (int j = i + 1; j < words.length; j++)
                    if (oneCharOff(words[i], words[j])) {
                        update(adjWords, words[i], words[j]);
                        update(adjWords, words[j], words[i]);
                    }

        }
        return adjWords;
    }

    //判断两个单词 只替换一个字符变成另一单词
    private static boolean oneCharOff(String word1, String word2) {
        if (word1.length() != word2.length())//单词长度不相等,肯定不符合条件. 
            return false;
        int diffs = 0;
        for (int i = 0; i < word1.length(); i++)
            if (word1.charAt(i) != word2.charAt(i))
                if (++diffs > 1)
                    return false;
        return diffs == 1;
    }

    //将单词添加到邻接表中
    private static <T> void update(Map<T, List<String>> m, T key, String value) {
        List<String> lst = m.get(key);
        if (lst == null) {//该 Key是第一次出现
            lst = new ArrayList<String>();
            m.put(key, lst);
        }
        lst.add(value);
    }
    
    
/**
 * 使用Dijkstra算法求解从 start 到 end 的最短路径
 * @param adjcentWords 保存单词Map,Map<String, List<string>>key:表示某个单词, Value:与该单词只差一个字符的单词
 * @param start 起始单词
 * @param end 结束单词
 * @return 从start 转换成 end 经过的中间单词
 */
    public static List<String> findChain(Map<String, List<String>> adjcentWords, String start, String end){
        Map<String, String> previousWord = new HashMap<String, String>();//Key:某个单词,Value:该单词的前驱单词
        Queue<String> queue = new LinkedList<>();
        
        queue.offer(start);
        while(!queue.isEmpty()){
            String preWord = queue.poll();
            List<String> adj = adjcentWords.get(preWord);
            
            for (String word : adj) {
                //代表这个word的'距离'(前驱单词)没有被更新过.(第一次遍历到该word),每个word的'距离'只会被更新一次.
                if(previousWord.get(word) == null){//理解为什么需要if判断
                    previousWord.put(word, preWord);
                    queue.offer(word);
                }
                
            }
        }
        previousWord.put(start, null);//记得把源点的前驱顶点添加进去
        return geChainFromPreviousMap(previousWord, start, end);
    }
    
    private static List<String> geChainFromPreviousMap(Map<String, String> previousWord, String start, String end){
        LinkedList<String> result = null;
        
        if(previousWord.get(end) != null){
            result = new LinkedList<>();
            for(String pre = end; pre != null; pre = previousWord.get(pre))
                result.addFirst(pre);
        }
        return result;
    }
    
    public static void main(String[] args){
    	List<String> thewords = read("resource/vacabulary.txt");
    	Map<String, List<String>> adjWords =computeAdjacentWords(thewords);
    	
    	/*for(Map.Entry<String, List<String>> entry : adjWords.entrySet()){
    		System.out.print("Key = "+entry.getKey()+",value="+entry.getValue());
    	}*/
    	System.out.println(findChain(adjWords,"zero","five"));
    	System.out.println(findChain(adjWords,"accident","occident"));
    	System.out.println(findChain(adjWords,"map","bat"));
    }
}
