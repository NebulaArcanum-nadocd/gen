package bruh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final String
            header = """
            <html lang="\0">
            <head>
                <meta charset="UTF-8">
                <title>y r u reading ts?</title>
            </head>
            <body>
            <h1>Content</h1>
            """,
            ending= """
            </body>
            </html>""";
    @SuppressWarnings("DataFlowIssue")
    public static void main(String[] args) throws Throwable{
        File bundlesDir = new File("./technologium/assets/bundles");
        t(bundlesDir.exists());
        t(bundlesDir.canRead());
        File[] locales = bundlesDir.listFiles();
        for (File locale : locales) {
            String localeStr = locale.getName().replace(".properties","").replaceAll("bundle","");
            t(locale.exists());
            t(locale.canRead());
            List<String> lines=Files.readAllLines(locale.toPath());
            for (String line : lines) {
                if(line.startsWith("#"))continue;
                String[] split = line.split("\\.");
                if(split.length!=3)continue;
                String[] propVal = split[2].split(" = ",2);
                if (propVal.length<2) continue;
                String type=split[0],id=split[1],property=propVal[0],value=propVal[1];
                data
                        .computeIfAbsent(localeStr, k -> new HashMap<>())
                        .computeIfAbsent(type, k -> new HashMap<>())
                        .computeIfAbsent(id, k -> new HashMap<>())
                        .putIfAbsent(property,value.replaceAll("[<>]","[T]").replace("\\n","\n"));
            }
        }
        data.forEach((locale,depth1)->{try {
            File output = new File("./outf/index".concat(locale).concat(".html"));
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8);
            osw.append(header.replaceFirst("\0",locale.isEmpty()?"en":locale.substring(1)));
            depth1.forEach((type,depth2)->{try{
                osw
                        .append("    <details class=\"type\">\n")
                        .append("        <summary>").append(capitalize(type)).append("</summary>\n");
                depth2.forEach((id,depth3)->{try{
                    String title=depth3.getOrDefault("name",id+"[no 'name' property]");
                osw
                        .append("            <details class=\"content\">\n")
                        .append("                <summary>").append(title).append("</summary>\n");
                    depth3.forEach((prop,value)->{if(prop.equals("name"))return;try{
                osw
                        .append("                    <div class=\"container\">\n")
                        .append("                        <div class=\"property\">").append(prop).append("</div>\n")
                        .append("                        <div class=\"value\">").append(value).append("</div>\n")
                        .append("                    </div>\n");
                    }catch (Throwable th){throw new RuntimeException(th);}});
                osw
                        .append("            </details>\n");
                }catch(Throwable th){throw new RuntimeException(th);}});
                osw.write("     </details>\n");
            }catch(Throwable th){throw new RuntimeException(th);}});
            osw.write(ending);
            osw.flush();
            osw.close();
        }catch(Throwable th){throw new RuntimeException(th);}});
    }
    //HM<Locale,HM<Type,HM<ID,HM<Property,Value>>>>
    static HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> data=new HashMap<>();

    private static void t(boolean bool){
        if(!bool)throw new RuntimeException(Thread.currentThread().getStackTrace()[2].toString());
    }

    private static String capitalize(String s){
        if(s.length()<=1)return s.toUpperCase();
        return s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();
    }
}