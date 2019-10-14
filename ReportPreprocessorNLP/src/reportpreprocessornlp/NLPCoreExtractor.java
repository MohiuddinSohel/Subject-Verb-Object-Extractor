  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportpreprocessornlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author mahmed27
 */
public class NLPCoreExtractor {
    
    private final StanfordCoreNLP pipeline;
    private static NLPCoreExtractor nlpExtractor = null;
    private final static String outputFilePath = "D:\\project\\FinalReport\\";

    
    private NLPCoreExtractor() {
        Properties property = new Properties();
        property.put("annotators", "tokenize, ssplit, pos, parse");//"tokenize, ssplit, pos, lemma, ner, parse, dcoref"
        pipeline = new StanfordCoreNLP(property);
        
    }
    
    public static NLPCoreExtractor getInstance(){
        if(nlpExtractor == null) {
            nlpExtractor = new NLPCoreExtractor();
        }
        return nlpExtractor;
    }   
    
    public List<String> extractActionTreeBasedApproach(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//        System.out.println("Size: " + sentences.size());
        
        List<String> fSentences = new ArrayList<>();
        
        for(CoreMap sentence: sentences) {
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            Collection<IndexedWord> rootList = dependencies.getRoots();
            List<SemanticGraphEdge> allEdge = new ArrayList<>();
            for(IndexedWord root : rootList) {
                treeTraversalDFS(dependencies, root, allEdge);
                this.parseSVO(sentence, allEdge, fSentences);
            }
            
        }
//        System.out.println("No of Sentence: " + fSentences.size());
        return fSentences;
    }
    
    private void parseSVO(CoreMap sentence, List<SemanticGraphEdge> allEdge
            , List<String> fSentences) {
        String nsubj = "", dobj = "", verb = "", det = "", aux = "", auxpass = ""
                , mark = "", advmod = "", case1 = "", amod = "", nmod = "", acl = ""
                , previous = "", aclDobj = "", compound = "";
        IndexedWord word;
        String fs = "";
        boolean bnsubj = false, bdobj = false, bnmod = false, bacl = false;
        for(SemanticGraphEdge edge : allEdge) {
            switch(edge.getRelation().getShortName().trim()) {
                case "nsubj":
                    if(!nsubj.isEmpty()) {                        
                        fs = nsubj +  (nsubj.isEmpty()? "": " ") + verb 
                                +  (verb.isEmpty()? "": " ") + dobj + (dobj.isEmpty()? "": " ") 
                                + nmod + (nmod.isEmpty()? "": " ") + acl + (acl.isEmpty()? "": " ") + aclDobj;
//                        System.out.println("Sentence:" + fs);
                        if(!fs.trim().isEmpty()) {
                            fSentences.add("[T1034]," + fs.trim());
                        }
                        mark = aux = auxpass = dobj = nsubj = nmod = acl = case1 = det = amod = advmod = compound = "";
                    }
                    if(getThePOS(sentence, edge.getSource().word().trim()).contains("V")) {
                        nsubj = edge.getTarget().word().trim();
                        verb = edge.getSource().word().trim();
                        bnsubj = true;
                    }
                    
                    break;
                case "conj":
                    if(bnsubj) {
                        nsubj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " "   + nsubj;
                        case1 = det = amod = advmod = compound = "";
                        bnsubj = false;
                    } else if(bdobj) {
                        dobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " "   + dobj;
                        case1 = det = amod = advmod = compound = "";
                        bdobj = false;
                    } else if(bacl) {
                        aclDobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " "   + aclDobj;
                        case1 = det = amod = advmod = compound = "";
                        bacl = false;
                    } else if(bnmod) {
                        nmod = case1 + " " + det + " " + amod + " " + advmod + " " + nmod;
                        case1 = det = amod = advmod = "";
                        bacl = false;
                    }
                    if(getThePOS(sentence, edge.getSource().word().trim()).equalsIgnoreCase("VB")  
                                && getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("VB")) {
                        
                        fs = nsubj +  (nsubj.isEmpty()? "": " ") + verb +  (verb.isEmpty()? "": " ") + dobj 
                                + (dobj.isEmpty()? "": " ") + nmod + (nmod.isEmpty()? "": " ") 
                                + acl + (acl.isEmpty()? "": " ") + aclDobj;
//                        System.out.println("Sentence:" + fs);
                        if(!fs.trim().isEmpty()) {
                            fSentences.add("[T1034]," + fs.trim());
                        }
                        verb = edge.getTarget().word().trim();
                        mark = aux = auxpass = dobj = nsubj = nmod = acl = case1 = det = amod = advmod = compound = "";
                    }
                    break;
                case "advcl":
                    if(bnsubj) {
                        nsubj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " "  + nsubj;
                        case1 = det = amod = advmod = compound = "";
                        bnsubj = false;
                    } else if(bdobj) {
                        dobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " "  + dobj;
                        case1 = det = amod = advmod = compound = "";
                        bdobj = false;
                    } else if(bacl) {
                        aclDobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " "  + aclDobj;
                        case1 = det = amod = advmod = compound = "";
                        bacl = false;
                    } else if(bnmod) {
                        nmod = case1 + " " + det + " " + amod + " " + advmod + " " + nmod;
                        case1 = det = amod = advmod = "";
                        bacl = false;
                    }
                    if(getThePOS(sentence, edge.getSource().word().trim()).equalsIgnoreCase("VB")  
                                && getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("VBN")) {
                        
                        fs = nsubj +  (nsubj.isEmpty()? "": " ") + verb +  (verb.isEmpty()? "": " ") + dobj + (dobj.isEmpty()? "": " ") 
                                + nmod + (nmod.isEmpty()? "": " ") + acl + (acl.isEmpty()? "": " ") + aclDobj;
//                        System.out.println("Sentence:" + fs);
                        if(!fs.trim().isEmpty()) {
                            fSentences.add("[T1034]," + fs.trim());
                        }
                        verb = edge.getTarget().word().trim();
                        mark = aux = auxpass = dobj = nmod = acl = case1 = det = amod = advmod = compound = "";
                    } else if(getThePOS(sentence, edge.getSource().word().trim()).equalsIgnoreCase("VB")  
                                && getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("NN")) {
                        fs = nsubj +  (nsubj.isEmpty()? "": " ") + verb +  (verb.isEmpty()? "": " ") + dobj + (dobj.isEmpty()? "": " ") 
                                + nmod + (nmod.isEmpty()? "": " ") + acl + (acl.isEmpty()? "": " ") + aclDobj;
//                        System.out.println("Sentence:" + fs);
                        if(!fs.trim().isEmpty()) {
                            fSentences.add("[T1034]," + fs.trim());
                        }
                        verb = edge.getSource().word().trim();
                        dobj = edge.getTarget().word().trim();
                        mark = aux = auxpass = nmod = acl = case1 = det = amod = advmod = compound = "";
                    }
                    break;
                case "dobj":
                    if(bacl) {
                        aclDobj = edge.getTarget().word().trim();
                    } else {
                        dobj = edge.getTarget().word().trim();
                        verb = getThePOS(sentence, edge.getSource().word().trim()).equalsIgnoreCase("VB")? edge.getSource().word().trim() : verb;
                        if(bnsubj) {
                            nsubj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + nsubj;
                            case1 = det = amod = advmod = compound = "";
                            bnsubj = false;
                        } else if(bnmod) {
                            nmod = case1 + " " + det + " " + amod + " " + advmod + " " + nmod;
                            case1 = det = amod = advmod = "";
                            bnmod = false;
                        }
                        bdobj = true;
                    }
                    break;
                case "nsubjpass":
                    nsubj = edge.getTarget().word().trim();
                    verb = edge.getSource().word().trim();
                    break;
                case "compound":
                    compound = edge.getTarget().word().trim();
                    break;
                case "aux":
                    aux = edge.getTarget().word().trim();
                    break;
                case "auxpass":
                    auxpass = edge.getTarget().word().trim();
                    break;
                case "case":
                    case1 =  edge.getTarget().word().trim();
                    break;
                case "det":
//                    det =  edge.getTarget().word().trim();
                    break;
                case "advmod":
                    advmod = ((getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("WRB") 
                            || getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("RB"))
                            && !edge.getTarget().word().trim().equalsIgnoreCase("then")
                            )? edge.getTarget().word().trim() : "";
                    break;
                case "amod":
                    amod =  ((getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("VBG") 
                            || getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("VBN")
                            || getThePOS(sentence, edge.getTarget().word().trim()).equalsIgnoreCase("JJ")) 
                            && !edge.getTarget().word().trim().equalsIgnoreCase("following")
                            )? edge.getTarget().word().trim() : "";
                    break;
                case "nmod":
                    nmod = edge.getTarget().word().trim();
                    verb = getThePOS(sentence, edge.getSource().word().trim()).equalsIgnoreCase("VB")? edge.getSource().word().trim() : verb;
                    if(bnsubj) {
                        nsubj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + nsubj;
                        case1 = det = amod = advmod = compound = "";
                        bnsubj = false;
                    } else if(bdobj) {
                        dobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + dobj;
                        case1 = det = amod = advmod = compound = "";
                        bdobj = false;
                    } else if(bacl) {
                        aclDobj =  case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + aclDobj;
                        case1 = det = amod = advmod = compound = "";
                        bacl = false;
                    }
                    bnmod = true;
                    break;
                case "acl":
                    acl = edge.getTarget().word().trim();
                    if(bnsubj) {
                        nsubj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + nsubj;
                        case1 = det = amod = advmod = compound = "";
                        bnsubj = false;
                    } else if(bdobj) {
                        dobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + dobj;
                        case1 = det = amod = advmod = compound = "";
                        bdobj = false;
                    } else if(bnmod) {
                        nmod = case1 + " " + det + " " + amod + " " + advmod + " " + nmod;
                        case1 = det = amod = advmod = "";
                        bnmod = false;
                    }
                    bacl = true;
                    break;
                case "mark":
                    mark = edge.getTarget().word().trim();
                    break;
                case "default":
                    break;
            }
        }
        
        if(bnsubj) {
            nsubj = case1 + " " + det + " " + amod + " " + advmod + " " + compound+  " " + nsubj;
        } else if(bdobj) {
            dobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + dobj;
        } else if(bacl) {
            aclDobj = case1 + " " + det + " " + amod + " " + advmod + " " + compound + " " + aclDobj;
        } else if(bnmod) {
            nmod = case1 + " " + det + " " + amod + " " + advmod + " " + nmod;
        }
        fs = nsubj +  (nsubj.isEmpty()? "": " ") + verb +  (verb.isEmpty()? "": " ") + dobj + (dobj.isEmpty()? "": " ") 
                + nmod + (nmod.isEmpty()? "": " ") + acl + (acl.isEmpty()? "": " ") + aclDobj;
//        System.out.println("Sentence:" + fs);
        if(!fs.trim().isEmpty()) {
            fSentences.add("[T1034]," + fs.trim());
        }
    }
    
    private void treeTraversalDFS(SemanticGraph dependencies, IndexedWord word, List<SemanticGraphEdge> allEdgeList) {
        List<SemanticGraphEdge> outEdgeList = dependencies.getOutEdgesSorted(word);
        if(outEdgeList == null || outEdgeList.size() == 0 ) {
            return;
        }
        for(SemanticGraphEdge edge : outEdgeList) {
            allEdgeList.add(edge);
            //System.out.println(edge.toString() + " Relation: " + edge.getRelation().getShortName());
            treeTraversalDFS(dependencies, edge.getTarget(), allEdgeList);
            
        }
    }
    
    public void writeSentenceToFile(String filePath, List<String> sentences) throws IOException {
        File newF = new File(outputFilePath + filePath);
        //newF.mkdirs();
        newF.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(newF));
        String strToFile = "" ;
        for(String sentence : sentences) {
            strToFile += sentence + "\n";
        }
        writer.write(strToFile);
        writer.close();

    }
    
    private String getThePOS(CoreMap sentence, String txt) {
        String pos = null;
        for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
            pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if(token.getString(TextAnnotation.class).trim().contentEquals(txt)) {
                break;
            }
        }
        return pos;
    }
}
