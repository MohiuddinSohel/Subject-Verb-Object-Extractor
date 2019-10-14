/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportpreprocessornlp;

/**
 *
 * @author mahmed27
 */
public class Regex {
    public static final String filePathMac = "([~]|[\\/])?((%([a-zA-Z]+)%)|([a-zA-Z]:)|([a-zA-Z]+))?(\\/[a-zA-Z0-9_. \\[\\]-]+)+\\/?";
    public static final String filePath = "([~]|[\\/])?((%([a-zA-Z]+)%)|([a-zA-Z]:))(\\\\[a-zA-Z0-9_. \\[\\]-]+)+\\\\?";
    public static final String IP = "([0-9]+\\.){3}([0-9]+)";
    public static final String registryKeyPathMac = "([a-zA-Z _-]+\\\\\\\\)+([a-zA-Z _-]+)"; //$
    public static final String registryKeyPath = "HKEY.*\\\\.*\\\\.*";//"([a-zA-Z _-]+\\\\)+([a-zA-Z _-]+)"; //$
    public static final String urlWithoutwww = "([a-zA-Z0-9_-]+\\.)+([a-zA-Z0-9._-]+)"; //$
    public static final String urlWithwww = "(([Ww][Ww][Ww]\\.)([a-zA-Z0-9_-]+\\.)+([a-zA-Z0-9._-]+)((\\/[a-zA-Z0-9._-]+)+)?\\/?)" ;
    public static final String url = "(\\[([Hh][Tt][Tt][Pp][Ss]?):\\/\\/\\])(([Ww][Ww][Ww]\\.)([a-zA-Z0-9_-]+\\.)+([a-zA-Z0-9._-]+)((\\/[a-zA-Z0-9._-]+)+)?\\/?)((\\[[Rr][Ee][Mm][Oo][Vv][Ee][Dd]\\]))?";
    public static final String str = "http";
    public static final String endOfLineMac = ":?(\\s)?(\\\\)";
    public static final String endOfLine = "(?m)^.*$";
    public static final String runKey = "HKEY.*\\\\[Rr]un\\\\.*";
    public static final String runKeyRText = "add registry entry on  runkeys";
    public static final String dllFile = ".*\\.[Dd][Ll][Ll]";
    public static final String dllFileRText = "DLL file";
    public static final String executableFile = ".*\\.[Ee][Xx][Ee]";
    public static final String executableFileRText = "Executable file";
    public static final String service = "HKEY.*\\\\[Ss]ervices?\\\\.*";
    public static final String serviceRText = "Add services on registry";
    public static final String cve = "([Cc][Vv][Ee])([-][0-9]{4}){2}";
    
}
