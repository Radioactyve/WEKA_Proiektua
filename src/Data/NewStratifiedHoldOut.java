package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

import java.io.*;

public class NewStratifiedHoldOut {

    public static void main() {
        try {
            juntatu();
            banatu();
        }
        catch (Exception e){
            System.out.println("Errore bat egon da datuak juntatu eta banatzerakoan");
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

            System.out.println("Los archivos ARFF han sido combinados exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error al combinar los archivos.");
        }
    }



    private static void banatu() throws Exception {
        // Datuak kargatu
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("src/x_out/combined.arff");
        Instances data = source.getDataSet();
        System.out.println(data.numInstances());

        // Klase atributua ezarri, normalean azkena izaten da
        if (data.classIndex() == -1){
            data.setClassIndex(1);
        }

        StratifiedRemoveFolds filter = new StratifiedRemoveFolds();

        filter.setNumFolds(10);
        filter.setFold(1);
        filter.setInvertSelection(true);
        filter.setInputFormat(data);

        Instances train = Filter.useFilter(data, filter);
        System.out.println(train.numInstances());

        filter.setInvertSelection(false);
        filter.setInputFormat(data);

        Instances dev = Filter.useFilter(data, filter);
        System.out.println(dev.numInstances());





        /*

        //Gorde artxiboak (falta)
        FileWriter fwTrain = new FileWriter("src/x_out/new_train.arff");
        fwTrain.write(train.toString());
        fwTrain.close();

        FileWriter fwDev = new FileWriter("src/x_out/new_dev.arff");
        fwDev.write(dev.toString());
        fwDev.close();

         */
    }
}
