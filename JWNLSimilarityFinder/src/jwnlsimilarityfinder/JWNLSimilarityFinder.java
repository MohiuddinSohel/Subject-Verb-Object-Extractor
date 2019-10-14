/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jwnlsimilarityfinder;

import edu.cmu.lti.jawjaw.pobj.POS;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.didion.jwnl.JWNLException;


/**
 *
 * @author mahmed27
 */
public class JWNLSimilarityFinder {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WordNetInfoExtractor extractor = null;
        try {
            WordNetSimilarityCalculator.getShortestPath("modify", "insert", POS.v);
            int pathLength = WordNetSimilarityCalculator.calculatePATHSimilarity("modify", "insert");
            System.out.println("No of Node(Inclusive): " + pathLength);
            extractor = WordNetInfoExtractor.getInstance();
//            extractor.getSenseGlossByWord(POS.VERB, "delete");
            extractor.getAllParents("modify", net.didion.jwnl.data.POS.VERB);
            extractor.getAllParents("insert", net.didion.jwnl.data.POS.VERB);
//            extractor.getAllPointerTypeByPOS(POS.NOUN);
            extractor.getAllPointerTypeByPOS(net.didion.jwnl.data.POS.VERB);
//            extractor.getAllPointerTypeByPOS(POS.ADJECTIVE);
        } catch (Exception ex) {
            Logger.getLogger(JWNLSimilarityFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        extractor.unloadWordNet();
    }    
    
}
