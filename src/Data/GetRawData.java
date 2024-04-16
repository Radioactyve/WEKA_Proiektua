package Data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

public class GetRawData {

    private static String pathIn;
    private static String pathOut;
    private static String modified;
    private static String finala;
    private static String emojiList;
    private static boolean readEmoji;

    public static void GetRawData(String csvName, String arffPath, String ppathIn, String ppathOut, String pmodified, String pfinala, String pemojiList, boolean preadEmoji) {
        /**
         * Metodo honek bi String motako parametro hartzen ditu, lehenengoa csv-aren path-a eta bigarrena arff baten path-a
         * Metodoak adierazitako csv-an dauden datuak hartzen ditu eta hauekin arff bat sortzen du (jada existitzen ez bada) eta
         * csv-an dauden datuak arff moduan idazten ditu
         * */

        //set params
        pathIn = ppathIn;
        pathOut = ppathOut;
        modified = pmodified;
        finala = pfinala;
        emojiList = pemojiList;
        readEmoji = preadEmoji;

        List<String> emojis = new ArrayList<>();

        try {
            emojis = Files.readAllLines(Paths.get(emojiList));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String modifiedPath = pathOut + csvName + modified + csvName + ".csv";
        emojiModify(csvName, emojis, modifiedPath);

        String modifiedPath2 = pathOut + csvName + modified + csvName + "2.csv";
        groupInstancesByID(modifiedPath,modifiedPath2);
        System.out.println("ID-z batu dira instantziak " + csvName + "-an.");

        String finalPath = pathOut + csvName + finala + csvName + ".csv";
        csvFinal(modifiedPath2, finalPath);
        System.out.println("Komilla doble errepikatuak ordezkatu egin dira " + csvName + "-an. Fitxategia: " + finalPath);

        try (
                BufferedReader br = new BufferedReader(new FileReader(finalPath));
                PrintWriter pw = new PrintWriter(new FileWriter(arffPath))
        ) {
            // ARFF-aren goiburua idatzi
            pw.println("@relation IberLef-Challenge");
            pw.println("@attribute 'claseValue' {0,1}");
            pw.println("@attribute 'textValue' string");
            pw.println("@data");

            String line;
            // Lehen ilara omititu csv-aren goiburua baita
            br.readLine();
            while ((line = br.readLine()) != null){
                List<String> values = commaSeparate(line);

                String processedLine = values.get(1) + ",\"" + values.get(2) + "\"";
                pw.println(processedLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(csvName + ".arff-a sortu egin da. Fitxategia: " + arffPath);

        //Ezabatu pausuak emateko egin diren artxibo lagungarriak partial, modified
        File modified = new File(modifiedPath);
        modified.delete();
        File modified2 = new File(modifiedPath2);
        modified2.delete();
    }


    // --------------------- METODOAK -------------------------

    private static void groupInstancesByID(String inputFilePath, String outputFilePath) {
        /**
         * CSV fitxategiko instantziak ID-aren arabera taldekatzen ditu csv berri bat sortuz
         */
        Map<String, StringBuilder> labelMap = new HashMap<>();
        Map<String, StringBuilder> messagesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String header = reader.readLine(); // header-a irakurri
            // Date header-a kendu
            String[] headerParts = header.split(",");
            StringBuilder modifiedHeader = new StringBuilder();
            for (String part : headerParts) {
                if (!part.trim().equalsIgnoreCase("date")) {
                    modifiedHeader.append(part.trim()).append(",");
                }
            }
            // Azkenengo koma kendu
            modifiedHeader.deleteCharAt(modifiedHeader.length() - 1);
            // Write Header
            writer.write(modifiedHeader.toString());
            writer.newLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = commaSeparate(line);
                String id = parts.get(0);
                String label = parts.get(1);
                String message = parts.get(2);

                if (!messagesMap.containsKey(id)) {
                    labelMap.put(id, new StringBuilder());
                    messagesMap.put(id, new StringBuilder());
                    // Etiketa behin bakarrik gehitu ID bakoitzerako
                    labelMap.get(id).append(label);
                }
                // Mezua gehitu
                messagesMap.get(id).append(message).append(" ");
            }

            for (Map.Entry<String, StringBuilder> entry : messagesMap.entrySet()) {
                String id = entry.getKey();
                String label = labelMap.get(id).toString();
                String message = entry.getValue().toString();
                message = message.substring(0, message.length() - 2); // koma eta zuriunea kentzeko
                writer.write(id + ", " + label + ", \"" + message + "\"");
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Errore bat gauzatu egin da: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void emojiModify(String csvPath, List<String> emojiSet, String outputPath) {
        /**
         * Datuetan agertzen diren emojiak aldatzen ditu hitz bakar bat bezala kontsidera daitezen,
         * hau da, emojiaren erdian dauden espazioak _-az aldatzen ditu
         * */
        try (BufferedReader br = new BufferedReader(new FileReader(pathIn + csvPath + ".csv"));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String modifiedLine = line;
                for (String emoji : emojiSet) {
                    // Emoji bakoitzerako adierazpen-eredu erregular bat sortzea
                    String regexPattern = "\\b" + Pattern.quote(emoji) + "\\b";
                    Pattern pattern = Pattern.compile(regexPattern);
                    Matcher matcher = pattern.matcher(modifiedLine);
                    // Linean aurkitutako emoji guztiak ordezkatu

                    if (readEmoji){
                        modifiedLine = matcher.replaceAll(emoji.replace(" ", "_"));

                    }
                    else{
                        modifiedLine = matcher.replaceAll("");
                    }
                }
                // Aldatu den linea irteera-fitxategira idatzi
                bw.write(modifiedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (readEmoji){
            System.out.println("\nEmojiak hitz bakar bat bezala gorde dira.");
        }
        else{
            System.out.println("\nEmojiak ezabatu egin dira.");
        }
    }

    private static void csvFinal(String modifiedFilePath, String outputPath) {
        /**
         * Csv-aren aldaketa finala burutuko duen metodoa da
         * */
        try (BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = parseDoubleQuotes(line);
                writer.write(processedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parseDoubleQuotes(String line) {
        /**
         * Komilla doble errepikatuak ("") aldatuko ditu komilla bakar batengatik (')
         * hau bakarrik gertatuko da lehen karakterea " bat baldin ez bada text parametroan
         * */
        // Aurkitu komatxo bikoitzen lehen parearen aurkibidea
        int indexOfFirstDoubleQuote = line.indexOf("\"");

        // Gutxienez pare bat komatxo bikoitz badaude
        if (indexOfFirstDoubleQuote != -1) {
            // Katea hiru zatitan banatzen du: lehen komatxo bikoitzaren aurretik, bitartean, eta ondoren
            String beforeFirstDoubleQuote = line.substring(0, indexOfFirstDoubleQuote + 1); // Sartu lehen komatxoa "before" zatian
            String afterFirstDoubleQuote = line.substring(indexOfFirstDoubleQuote + 1); // Katearen hondarra lehen komatxoaren ondoren

            // Ordeztu gainerako komatxo bikoitz pare guztiak "after" zatian
            String processedAfterFirstDoubleQuote = afterFirstDoubleQuote.replace("\"\"", "'");

            // Batu eta emaitza itzuli
            return beforeFirstDoubleQuote + processedAfterFirstDoubleQuote;
        }

        // Jarraian komatxo bikoitzik ez badago, itzuli lerroa aldaketarik gabe
        return line;
    }

    private static List<String> commaSeparate(String line) {
        /**
         * Metodo hau csv-an dauden parametroak erauzituko ditu eta List batean gordeko ditu,
         * bakarrik, komilla artean ez dagoen bitartean, komak erabiliz banatuko ditu du
         * */
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;

        for (char ch : line.toCharArray()) {
            if (ch == '"') {
                insideQuotes = !insideQuotes; // Komillak agertu dira beraz komilla amaitu arte ez da amaitzen gelaxka
            } else if (ch == ',' && !insideQuotes) {
                // Koma agertu da komilla artean ez dagoenean beraz amaitu da galaxka
                values.add(value.toString());
                value = new StringBuilder(); // Berriz hasieratu hurrengo gelaxkarako
            } else {
                value.append(ch); // Gehitu karakterea
            }
        }

        // Azken balioa gehitu
        values.add(value.toString());

        return values;
    }
}