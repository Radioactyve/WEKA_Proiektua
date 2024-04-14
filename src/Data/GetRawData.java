package Data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

        String finalPath = pathOut + csvName + finala + csvName + ".csv";
        csvFinal(modifiedPath, finalPath);

        try (
                BufferedReader br = new BufferedReader(new FileReader(finalPath));
                PrintWriter pw = new PrintWriter(new FileWriter(arffPath))
        ) {
            // ARFF-aren goiburua idatzi
            pw.println("@relation IberLef-Challenge");
            pw.println("@attribute 'idValue' string");
            pw.println("@attribute 'claseValue' {0,1}");
            pw.println("@attribute 'textValue' string");
            pw.println("@attribute 'dateValue' date 'yyyy-MM-dd HH:mm:ss'");
            pw.println("@data");

            String line;
            // Lehen ilara omititu csv-aren goiburua baita
            br.readLine();
            while ((line = br.readLine()) != null){
                List<String> values = commaSeparate(line);

                // Orain, values' lista bat da non elementu bakoitza csv-aren zutabe bat den
                /*
                System.out.println("Columna 1: " + values.get(0)); // Lehen parametroa
                System.out.println("Columna 2: " + values.get(1)); // Bigarren parametroa
                System.out.println("Columna 3: " + values.get(2)); // Hirugarren parametroa
                System.out.println("Columna 4: " + values.get(3)); // Laugarren parametroa
                */

                //ARFF-an idatziko den formatuan idatzi ilara osoa
                String processedLine = values.get(0) + "," + values.get(1) + ",\"" + values.get(2) + "\",\"" + values.get(3) + "\"";
                pw.println(processedLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Ezabatu pausuak emateko egin diren artxibo lagungarriak partial, modified
        File modified = new File(modifiedPath);
        modified.delete();

        //Finala ezabatu?
        //File finala = new File("src/x_out/final_" + csvPath);
        //finala.delete();

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
                    // Crear un patrón de expresión regular para cada emoji
                    String regexPattern = "\\b" + Pattern.quote(emoji) + "\\b";
                    Pattern pattern = Pattern.compile(regexPattern);
                    Matcher matcher = pattern.matcher(modifiedLine);
                    // Reemplazar todos los emojis encontrados en la línea

                    if (readEmoji){
                        modifiedLine = matcher.replaceAll(emoji.replace(" ", "_"));
                    }
                    else{
                        modifiedLine = matcher.replaceAll("");
                    }


                }
                // Escribir la línea modificada al archivo de salida
                bw.write(modifiedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        // Encuentra el índice del primer par de comillas dobles
        int indexOfFirstDoubleQuote = line.indexOf("\"");

        // Si se encuentra al menos un par de comillas dobles
        if (indexOfFirstDoubleQuote != -1) {
            // Divide la cadena en tres partes: antes, durante, y después del primer par de comillas dobles
            String beforeFirstDoubleQuote = line.substring(0, indexOfFirstDoubleQuote + 1); // Incluye la primera comilla en la parte "antes"
            String afterFirstDoubleQuote = line.substring(indexOfFirstDoubleQuote + 1); // Resto de la cadena después de la primera comilla

            // Reemplaza todos los demás pares de comillas dobles en la parte "después"
            String processedAfterFirstDoubleQuote = afterFirstDoubleQuote.replace("\"\"", "'");

            // Concatena y devuelve el resultado
            return beforeFirstDoubleQuote + processedAfterFirstDoubleQuote;
        }

        // Si no hay comillas dobles seguidas, devuelve la línea sin cambios
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
