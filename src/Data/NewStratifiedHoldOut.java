package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

import java.io.*;

public class NewStratifiedHoldOut {

    public static void main() {
        try {
            juntatu();
            banatu();
        }
        catch (Exception e){
            System.out.println("Errore bat egon da datuak juntatu eta banatzerakoan");
            e.printStackTrace();
        }

    }

    private static void juntatu() {
        // Nombres de archivos ARFF a juntar
        String firstFile = "src/x_out/train.arff";
        String secondFile = "src/x_out/dev.arff";
        // Archivo de salida
        String outputFile = "src/x_out/combined.arff";

        try {
            // Abrir el primer archivo
            BufferedReader reader1 = new BufferedReader(new FileReader(firstFile));
            // Crear el archivo de salida
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            String line;
            boolean isDataSection = false;
            // Copiar el contenido del primer archivo al archivo de salida
            while ((line = reader1.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader1.close();

            // Abrir el segundo archivo
            BufferedReader reader2 = new BufferedReader(new FileReader(secondFile));
            boolean copy = false;
            // Copiar solo la sección de datos del segundo archivo
            while ((line = reader2.readLine()) != null) {
                if (line.toLowerCase().startsWith("@data")) {
                    copy = true; // Comenzar a copiar después de "@data"
                    continue; // No escribir la línea "@data" del segundo archivo
                } else if (copy) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            reader2.close();

            // Cerrar el archivo de salida
            writer.close();

            System.out.println("Ondo juntatu dira ARFF-ak.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore bat egon da datuak juntatzerakoan.");
        }
    }



    private static void banatu() throws Exception {
        // Datuak kargatu
        DataSource source = new DataSource("src/x_out/combined.arff");
        Instances data = source.getDataSet();

        // Klase atributua ezarri, normalean azkena izaten da
        if (data.classIndex() == -1){
            data.setClassIndex(1);
        }

        // -------------[STRATIFIED BANAKETA]-----------
        // Settings
        Resample resampleFilter = new Resample();
        resampleFilter.setInputFormat(data);
        resampleFilter.setNoReplacement(true);
        resampleFilter.setSampleSizePercent(70.0);
        resampleFilter.setInvertSelection(false);

        // Train
        Instances trainData = Filter.useFilter(data, resampleFilter);


        // Dev
        resampleFilter.setInputFormat(data);
        resampleFilter.setSampleSizePercent(70.0);
        resampleFilter.setInvertSelection(true);
        Instances devData = Filter.useFilter(data, resampleFilter);

        //Gorde artxiboak (falta)
        FileWriter fwTrain = new FileWriter("src/x_out/new_train.arff");
        fwTrain.write(trainData.toString());
        fwTrain.close();

        FileWriter fwDev = new FileWriter("src/x_out/new_dev.arff");
        fwDev.write(devData.toString());
        fwDev.close();

    }
}
