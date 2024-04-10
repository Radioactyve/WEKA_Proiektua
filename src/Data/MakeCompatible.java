package Data;

import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.*;

public class MakeCompatible {
    public static void main(){
        try{
            makeTestCompatible(getHeader("src/x_out/dev.arff"));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static List<String> getHeader(String arffPath) {
        List<String> headers = new ArrayList<>();

        try {
            // Leer el contenido del train.arff
            BufferedReader trainReader = new BufferedReader(new FileReader(arffPath));

            String line;
            int i =0;
            boolean dataSection = false;

            // Escribir los encabezados en headers.arff
            while ((line = trainReader.readLine()) != null) {
                if (line.toLowerCase().startsWith("@data")) {
                    dataSection = true;
                }
                if (!dataSection && !line.toLowerCase().startsWith("@relation")) {
                    headers.add(line);
                }
            }
            // Cerrar los lectores y escritores
            trainReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(headers.toString());

        return headers;
    }

    public static void makeTestCompatible(List<String> headers) throws Exception {
        // Carga el conjunto de datos original
        DataSource source = new DataSource("src/x_out/new_dev.arff");
        Instances data = source.getDataSet();

        // Asegura que el atributo de clase está correctamente establecido
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1); // Normalmente el último atributo
        }

        // Crear filtro StringToWordVector compatible
        StringToWordVector filter = new StringToWordVector();
        // Configurar filtro para incluir solo los atributos seleccionados
        String attributesToKeep = "-R ";
        for (int i = 0; i < headers.size(); i++) {
            attributesToKeep += (i + 1);
            if (i < headers.size() - 1) {
                attributesToKeep += ",";
            }
        }
        filter.setOptions(Utils.splitOptions(attributesToKeep));
        filter.setOutputWordCounts(false);
        filter.setWordsToKeep(20000);
        filter.setLowerCaseTokens(true);
        filter.setAttributeIndices("3");
        filter.setInputFormat(data);
        Instances devDataFiltered = Filter.useFilter(data, filter);

        // Aquí puedes usar devDataFiltered como el conjunto de datos procesado y compatible con train_bekt.arff
        FileWriter fwTrain = new FileWriter("src/x_out/compatible_dev.arff");
        fwTrain.write(devDataFiltered.toString());
        fwTrain.close();
    }

}
