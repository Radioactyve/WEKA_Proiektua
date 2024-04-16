package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

import java.io.*;

public class NewStratifiedHoldOut {

    private static String trainArff;
    private static String newTrainArff;
    private static String devArff;
    private static String newDevArff;
    private static String combinedArff;


    public static void NewStratifiedHoldOut(int sampleSize,String trainArffPath, String newTrainArffPath, String devArffPath, String newDevArffPath, String combinedArffPath) {
        /**
         * Erabili ditugun train eta dev hartu, juntatu eta hauekin train eta dev berri sortuko ditu
         * parametro moduan sartzen den sample size (portzentaia) erabiliz
         */
        try {
            trainArff = trainArffPath;
            newTrainArff = newTrainArffPath;
            devArff = devArffPath;
            newDevArff = newDevArffPath;
            combinedArff = combinedArffPath;

            juntatu();
            banatu(sampleSize);
        }
        catch (Exception e){
            System.out.println("Errore bat egon da datuak juntatu eta banatzerakoan");
            e.printStackTrace();
        }

    }

    private static void juntatu() {
        /**
         * Train eta dev juntatu eta combined.arff bat sortu datu guztiekin
         */
        try {
            // Abrir el primer archivo
            BufferedReader reader1 = new BufferedReader(new FileReader(trainArff));
            // Crear el archivo de salida
            BufferedWriter writer = new BufferedWriter(new FileWriter(combinedArff));

            String line;
            boolean isDataSection = false;
            // Copiar el contenido del primer archivo al archivo de salida
            while ((line = reader1.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader1.close();

            // Abrir el segundo archivo
            BufferedReader reader2 = new BufferedReader(new FileReader(devArff));
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



    private static void banatu(double sampleSize) throws Exception {
        /**
         * Combined.arff-an dauden datuak banandu train eta dev berri batzuetan parametro moduan sartutako portzentaiarekin
         */
        // Datuak kargatu
        DataSource source = new DataSource(combinedArff);
        Instances data = source.getDataSet();

        // Klase atributua ezarri, normalean azkena izaten da
        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }

        // -------------[STRATIFIED BANAKETA]-----------
        // Settings
        Resample resampleFilter = new Resample();
        resampleFilter.setInputFormat(data);
        resampleFilter.setNoReplacement(true);
        resampleFilter.setSampleSizePercent(sampleSize);
        resampleFilter.setInvertSelection(false);

        // Train
        Instances trainData = Filter.useFilter(data, resampleFilter);

        // Dev
        resampleFilter.setInputFormat(data);
        resampleFilter.setSampleSizePercent(sampleSize);
        resampleFilter.setInvertSelection(true);
        Instances devData = Filter.useFilter(data, resampleFilter);

        //Gorde artxiboak (falta)
        FileWriter fwTrain = new FileWriter(newTrainArff);
        fwTrain.write(trainData.toString());
        fwTrain.close();

        FileWriter fwDev = new FileWriter(newDevArff);
        fwDev.write(devData.toString());
        fwDev.close();

    }
}
