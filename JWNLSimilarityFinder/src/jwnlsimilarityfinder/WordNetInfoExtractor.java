/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jwnlsimilarityfinder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

/**
 *
 * @author mahmed27
 */
public class WordNetInfoExtractor {
    private static String wordnetFilesProperties = "/Users/mahmed27/Desktop/Wordnet/jwnl14-rc2/config/file_properties.xml";
    private static Dictionary dictionary = null;
    public static WordNetInfoExtractor wordNetInfoExtractor = null;
    
    /**
     * 
     * @throws FileNotFoundException
     * @throws JWNLException 
     */
    private WordNetInfoExtractor() throws FileNotFoundException, JWNLException{
        this.loadWordNet();
    }
    
    public static WordNetInfoExtractor getInstance() throws FileNotFoundException, JWNLException {
        if(wordNetInfoExtractor == null) {
            wordNetInfoExtractor = new WordNetInfoExtractor();
        }
        return wordNetInfoExtractor;
    }  
    
    /**
     * 
     * @throws FileNotFoundException
     * @throws JWNLException 
     */
    public void loadWordNet()  throws FileNotFoundException, JWNLException{
        if(dictionary == null) {
            JWNL.initialize(new FileInputStream(wordnetFilesProperties));
            dictionary = Dictionary.getInstance();
        }
    }
    
    /**
     * 
     */
    public static void unloadWordNet() {
        dictionary.close();
        Dictionary.uninstall();
        JWNL.shutdown();
        dictionary = null;
        wordNetInfoExtractor = null;
    }
    
    /**
     * 
     * @param PartsOfSpeech
     * @param wordtoSerach
     * @throws JWNLException 
     */
    public void getSenseGlossByWord(POS PartsOfSpeech, String wordtoSerach) throws JWNLException {
        IndexWord word = dictionary.getIndexWord(PartsOfSpeech, wordtoSerach.toLowerCase().trim());
        Synset senses[] = word.getSenses();
        System.out.println("Number of Sense: " + senses.length);
        for(Synset sense : senses) {
            System.out.println(word + ": " + sense.getGloss());
        }
    }
    
    /**
     * 
     * @param wordtoSerach
     * @param partsOfSpeech
     * @throws JWNLException 
     */
    public void getAllParents(String wordtoSerach, POS partsOfSpeech) throws JWNLException {
        IndexWord word = dictionary.getIndexWord(partsOfSpeech, wordtoSerach.toLowerCase().trim());
        Synset senses[] = word.getSenses();
        Map<String, List> parentsWithSense = new HashMap<String, List>();
        Integer i = 0;
        for(Synset sense : senses) {
            List parents = new ArrayList();
            Word[] words = sense.getWords();
            for(Word wordt : words) {
                parents.add(wordt.getLemma());
            }
            
            getParents(sense, parents);
            parentsWithSense.put(sense.getGloss(), parents);
            i++;
        }
        printMap(parentsWithSense);
    }
    
    /**
     * 
     * @param sense
     * @param depth
     * @throws JWNLException 
     */
    public void getAllChildrenForASense(Synset sense, int depth) throws JWNLException{
        if(depth == 0) {
            PointerUtils.getInstance().getHyponymTree(sense);
        } else {
            PointerUtils.getInstance().getHyponymTree(sense, depth);
        }
    }
    
    /**
     * 
     * @param wordtoSerach
     * @param partsOfSpeech
     * @throws JWNLException 
     */
    public void getAllChildrenForAllSense(String wordtoSerach, POS partsOfSpeech) throws JWNLException{
        
        IndexWord word = dictionary.getIndexWord(partsOfSpeech, wordtoSerach.toLowerCase().trim());
        Synset senses[] = word.getSenses();
        for(Synset sense : senses) {
            PointerUtils.getInstance().getHyponymTree(sense).print();
        }
        
    }
    
    /**
     * 
     * @param wordtoSerach
     * @param partsOfSpeech
     * @param depth
     * @throws JWNLException 
     */
    public void getAllChildrenForAllSense(String wordtoSerach, POS partsOfSpeech, int depth) throws JWNLException{
        
        IndexWord word = dictionary.getIndexWord(partsOfSpeech, wordtoSerach.toLowerCase().trim());
        Synset senses[] = word.getSenses();
        for(Synset sense : senses) {
            PointerUtils.getInstance().getHyponymTree(sense, depth).print();
        }
        
    }
    
    /**
     * 
     * @param synset
     * @param parents
     * @throws JWNLException 
     */
    private void getParents(Synset synset, List parents) throws JWNLException {
        Pointer[] pointers=synset.getPointers();        
        for (Pointer pointer : pointers) {
            
            if (pointer.getType() == PointerType.HYPERNYM) {
                parents.add(" | ");
                Synset parent = pointer.getTargetSynset();
                Word[] words = parent.getWords();
                for(Word word : words) {
                    parents.add(word.getLemma());
                }               
                getParents(parent,parents);
            }
        }
    }
    
    /**
     * 
     * @param parentWithSense 
     */
    public void printMap(Map<String, List> parentWithSense) {
        for(String key : parentWithSense.keySet()) {
            System.out.println("Sense: " + key);
            System.out.println(Arrays.toString(parentWithSense.get(key).toArray()));
        }
    }
    
    /**
     * 
     * @param pos 
     */
    public void getAllPointerTypeByPOS(POS pos) {
        List pointerType = PointerType.getAllPointerTypesForPOS(pos);
        System.out.println(pos.getKey() + " : " + Arrays.toString(pointerType.toArray()));
    }
    
}
