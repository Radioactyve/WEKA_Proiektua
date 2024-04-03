package Data;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class GetRawData {

    public static void emojiParse(String csvPath, String csvPartial, String csvModify){
        /**
         * Metodo honek hiru String motako parametro hartzen ditu, csv-aren izena, lehen aldaketa gordtzeko csv-aren izena
         * eta azken aldaketa gordetzeko izena
         * Parametro hauekin deituko du emoji.py scripta eta honek terminalean idazten duena printeatuko du
         * Emoji.py egiten duena da web batean aurkitu ditugun emoji garrantzitzuenak lortu eta lista bat sortu, behin lista izanda,
         * csv guztia begiratu eta agertzen diren emoji garrantzitsuak eraldatu hitz bakarra izateko (lotu hitzak _ erabiliz)
         * */

        try {
            // Python scrpita exkutatzeko komandoa
            String comando = "python src/python/emoji.py " + csvPath  + " " + csvPartial + " " + csvModify;

            // Komanda exekutatu
            Process p = Runtime.getRuntime().exec(comando);

            // Irteera egongo balitz irakurri
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String linea;
            while ((linea = in.readLine()) != null) {
                System.out.println(linea);
            }

            //Itxaron scripta amaitu arte itxaron eta idatzi kodigoa 0-Ondo amaitu du (errorerik gabe)
            int exitCode = p.waitFor();
            System.out.println("El script de Python finalizó con el código de salida: " + exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
                String processedLine = processLine(line);
                writer.write(processedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processLine(String line) {
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


    public static void csvToArff(String csvPath, String arffPath) {
        /**
         * Metodo honek bi String motako parametro hartzen ditu, lehenengoa csv-aren path-a eta bigarrena arff baten path-a
         * Metodoak adierazitako csv-an dauden datuak hartzen ditu eta hauekin arff bat sortzen du (jada existitzen ez bada) eta
         * csv-an dauden datuak arff moduan idazten ditu
         * */

        String partial = "partial_" + csvPath.substring(csvPath.lastIndexOf("/") + 1);
        String modified = "modified_" + csvPath.substring(csvPath.lastIndexOf("/") + 1);
        emojiParse(csvPath, partial, modified);

        csvFinal("src/x_out/" + modified, "src/x_out/final_train.csv");

        try (
                BufferedReader br = new BufferedReader(new FileReader("src/x_out/final_train.csv"));
                PrintWriter pw = new PrintWriter(new FileWriter(arffPath))
        ) {
            // ARFF-aren goiburua idatzi
            pw.println("@relation IberLef-Challenge");
            pw.println("@attribute 'id' string");
            pw.println("@attribute 'clase' {0,1}");
            pw.println("@attribute 'text' string");
            pw.println("@attribute 'date' date 'yyyy-MM-dd HH:mm:ss'");
            pw.println("@data");

            String line;
            // Lehen ilara omititu csv-aren goiburua baita
            br.readLine();
            while ((line = br.readLine()) != null){
                List<String> values = parseLine(line);

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
        File partialFile = new File("src/x_out/" + partial);
        File modifiedFile = new File("src/x_out/" + modified);
        if (partialFile.delete() & modifiedFile.delete()){
            System.out.println("Artxibo lagungarriak ezabatu dira");
        }
    }

    private static List<String> parseLine(String line) {
        /**
         * Metodo hau csv-an dauden parametroak erauzituko ditu eta List batean gordeko ditu,
         * bakarrik " artean ez dagoen bitartean komak erabiliz egingo du
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
