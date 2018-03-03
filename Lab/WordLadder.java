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
     * ���ļ��н����ʶ��뵽List<String>. ����һ��һ������,����û���ظ�
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
     * ���ݵ��ʹ����ڽӱ�
     * @param theWords �������е���List
     * @return Map<String, List<string>>key:��ʾĳ������, Value:��õ���ֻ��һ���ַ��ĵ���
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

    //�ж��������� ֻ�滻һ���ַ������һ����
    private static boolean oneCharOff(String word1, String word2) {
        if (word1.length() != word2.length())//���ʳ��Ȳ����,�϶�����������. 
            return false;
        int diffs = 0;
        for (int i = 0; i < word1.length(); i++)
            if (word1.charAt(i) != word2.charAt(i))
                if (++diffs > 1)
                    return false;
        return diffs == 1;
    }

    //��������ӵ��ڽӱ���
    private static <T> void update(Map<T, List<String>> m, T key, String value) {
        List<String> lst = m.get(key);
        if (lst == null) {//�� Key�ǵ�һ�γ���
            lst = new ArrayList<String>();
            m.put(key, lst);
        }
        lst.add(value);
    }
    
    
/**
 * ʹ��Dijkstra�㷨���� start �� end �����·��
 * @param adjcentWords ���浥��Map,Map<String, List<string>>key:��ʾĳ������, Value:��õ���ֻ��һ���ַ��ĵ���
 * @param start ��ʼ����
 * @param end ��������
 * @return ��start ת���� end �������м䵥��
 */
    public static List<String> findChain(Map<String, List<String>> adjcentWords, String start, String end){
        Map<String, String> previousWord = new HashMap<String, String>();//Key:ĳ������,Value:�õ��ʵ�ǰ������
        Queue<String> queue = new LinkedList<>();
        
        queue.offer(start);
        while(!queue.isEmpty()){
            String preWord = queue.poll();
            List<String> adj = adjcentWords.get(preWord);
            
            for (String word : adj) {
                //�������word��'����'(ǰ������)û�б����¹�.(��һ�α�������word),ÿ��word��'����'ֻ�ᱻ����һ��.
                if(previousWord.get(word) == null){//���Ϊʲô��Ҫif�ж�
                    previousWord.put(word, preWord);
                    queue.offer(word);
                }
                
            }
        }
        previousWord.put(start, null);//�ǵð�Դ���ǰ��������ӽ�ȥ
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
