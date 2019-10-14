/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jwnlsimilarityfinder;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.PathFinder;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mahmed27
 */
public class WordNetSimilarityCalculator {
    private static ILexicalDatabase db = new NictWordNet();
    private static RelatednessCalculator[] rcs = { new Lesk(db), new HirstStOnge(db), new LeacockChodorow(db), new Lin(db)
            , new Path(db), new Resnik(db), new WuPalmer(db) };
    /**
     * 
     * @param word1
     * @param word2
     * @return 
     */
    public static double calculateSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(false);
        WS4JConfiguration.getInstance().setTrace(true);
        double s = 0;
        for (RelatednessCalculator rc : rcs) {
            s = rc.calcRelatednessOfWords(word1.toLowerCase().trim(), word2.toLowerCase().trim());
            System.out.println("Score for : " + s);
        }
        return s;
    }
    /**
     * 
     * @param word1
     * @param word2
     * @return 
     */
    public static int calculatePATHSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(false);
        WS4JConfiguration.getInstance().setTrace(true);
        double s = 0;
        Path rc = new Path(db);
        s = rc.calcRelatednessOfWords(word1.toLowerCase().trim(), word2.toLowerCase().trim());
        System.out.println("Score for Path: " + s);
        return (int)(1/s);
    }
    /**
     * 
     * @param words1
     * @param words2
     * @return 
     */
    public static double[][] calculateSimilarityMatrix(String[] words1, String[] words2) {
        WS4JConfiguration.getInstance().setMFS(false);
        WS4JConfiguration.getInstance().setTrace(false);
        Path rc = new Path(db);
        return rc.getSimilarityMatrix(words1, words2);
    }
    
    public static void getShortestPath(String word1, String word2, POS pos) {
        List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(word1, pos.name());       
        List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(word2, pos.name());
        PathFinder pathFinder = new PathFinder(db);
        int length = 123456;
        List<PathFinder.Subsumer> finalPath = null;
        List<PathFinder.Subsumer> path = null;
        for(Concept synset1 : synsets1){
            for(Concept synset2 : synsets2) {
                StringBuilder tracer = new StringBuilder();
                path = pathFinder.getShortestPaths(synset1, synset2, tracer);
                if(path.get(0).length <= length) {
                    finalPath = path;
                    length = path.get(0).length;
                }
            }
        }
        //System.out.println(Arrays.toString(finalPath.toArray()));
        System.out.println(finalPath.get(0).toString());
        System.out.println("Number of Node(Inclusive): " + length);
    }
    
}
