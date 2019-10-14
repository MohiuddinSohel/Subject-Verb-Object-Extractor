/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportpreprocessornlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mahmed27
 */
public class TextPreprocessor {
    
    private int dllfile;
    private int exefile;
    private int registrykey;
    private int servicekey;
    private int runkey;
    private int cve;
    private int url;
    private int ip;
    private int filePath;
    private final static String statsfFilePath = "D:\\project\\IOCStats.csv";
    
    public TextPreprocessor() {
        this.dllfile = this.exefile = this.registrykey = this.servicekey = this.runkey 
                = this.cve = this.url = this.ip = this.filePath = 0;
    }
    
    public String preProcessText(File file, int buttonNo) throws FileNotFoundException {
        Scanner in = new Scanner(file);
        String text = in.useDelimiter("\\Z").hasNext() ? in.useDelimiter("\\Z").next(): "" ;
        if (text.isEmpty()) return text;
        if(buttonNo == 1) {
            text = replacePathWithRegex(text);
        } else {
            this.calculateIOCStatistics(text);
//            System.out.println("this.dllfile = " + this.dllfile + " this.exefile = " + this.exefile 
//                    + " this.registrykey = " + this.registrykey + " this.servicekey = " + this.servicekey 
//                    + " this.runkey = " + this.runkey + " this.cve = " + this.cve + " this.url = " + this.url 
//                    + " this.ip = " + this.ip + " this.filePath = " + this.filePath );
        }
        return text;
    }
    
    private String addDotAtEndOfLine(String text) {
        Pattern p = null;
        String out = "";
        
        p = Pattern.compile(Regex.endOfLine);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String tmp = m.group().trim();
            if(tmp.isEmpty()) {
                continue;
            }
            tmp = tmp.replaceAll(":", "");
            out +=  tmp+ ".\n";
        }
        return out;
    }
    
   
    
    private String replacePathWithRegex(String text) {
        Pattern p = null;
        String out = null;
        
        p = Pattern.compile(Regex.runKey);
        out = p.matcher(text).replaceAll(Regex.runKeyRText);
        
        p = Pattern.compile(Regex.service);
        out = p.matcher(out).replaceAll(Regex.serviceRText);
        
        p = Pattern.compile(Regex.registryKeyPath);
        out = p.matcher(out).replaceAll("Add registry entries");
        
        p = Pattern.compile(Regex.dllFile);
        out = p.matcher(out).replaceAll(Regex.dllFileRText);
        
        p = Pattern.compile(Regex.executableFile);
        out = p.matcher(out).replaceAll(Regex.executableFileRText);
        
        p = Pattern.compile(Regex.filePath);
        out = p.matcher(out).replaceAll("Create files");
        
        p = Pattern.compile(Regex.registryKeyPath);
        out = p.matcher(out).replaceAll("Add registry entries");
        
        p = Pattern.compile(Regex.url);
        out = p.matcher(out).replaceAll("URL");
        
        p = Pattern.compile(Regex.urlWithwww);
        out = p.matcher(out).replaceAll("URL");
        
        p = Pattern.compile(Regex.urlWithoutwww);
        out = p.matcher(out).replaceAll("website");
        
        p = Pattern.compile(Regex.IP);
        out = p.matcher(out).replaceAll("IP");
        
//        p = Pattern.compile(this.endOfLine);
//        out = p.matcher(out).replaceAll(".");
        out = this.addDotAtEndOfLine(out);
        
        
        return out;
    }
    
    public void calculateIOCStatistics(String text) {
        Pattern p, p1, p2;
        p = Pattern.compile(Regex.IP);
        if(p.matcher(text).find()) {
            this.ip++;
        }
        p = Pattern.compile(Regex.dllFile);
        if(p.matcher(text).find()) {
            this.dllfile++;
        }
        p = Pattern.compile(Regex.executableFile);
        if(p.matcher(text).find()) {
            this.exefile++;
        }
        p = Pattern.compile(Regex.runKey);
        if(p.matcher(text).find()) {
            this.runkey++;
        }
        p = Pattern.compile(Regex.service);
        if(p.matcher(text).find()) {
            this.servicekey++;
        }
        p = Pattern.compile(Regex.registryKeyPath);
        if(p.matcher(text).find()) {
            this.registrykey++;
        }
        p = Pattern.compile(Regex.filePath);
        p1 = Pattern.compile(Regex.filePathMac);
        if(p.matcher(text).find()) {
            this.filePath++;
        } else if(p1.matcher(text).find()) {
            this.filePath++;
        }
        p = Pattern.compile(Regex.cve);
        if(p.matcher(text).find()) {
            this.cve++;
        }
        p = Pattern.compile(Regex.url);
        p1 = Pattern.compile(Regex.urlWithoutwww);
        p2 = Pattern.compile(Regex.urlWithwww);
        if(p.matcher(text).find()) {
            //System.out.println("sfdsf#################");
            this.url++;
        } else if( p1.matcher(text).find()) {
            //System.out.println("%%%%");
            //this.url++;
        } else if(p2.matcher(text).find()) {
            //System.out.println("$$$$$$");
            this.url++;
        }
    }
    
    public void writeIOCStatsToFile() throws IOException {
        File newF = new File(statsfFilePath);
        //newF.mkdirs();
        newF.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(newF));
        String strToFile = "" ;
        strToFile += "CVE," +this.cve + "\n";
        strToFile += "DllFile," + this.dllfile + "\n";
        strToFile += "ExeFile," + this.exefile + "\n";
        strToFile += "FilePath," + this.filePath + "\n";
        strToFile += "IP," + this.ip + "\n";
        strToFile += "RegistryKey," + this.registrykey + "\n";
        strToFile += "RunRegistryKey," + this.runkey + "\n";
        strToFile += "ServiceRegistryKey," + this.servicekey + "\n";
        strToFile += "URL," + this.url + "\n";
        writer.write(strToFile);
        writer.close();
    }
    
}
