package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

import java.io.*;

public class MakeCompatible {
    private static String dictionaryTxt = "src/x_out/Data/dictionary.txt";
    public static void MakeCompatible(String path, String output){
        try{
            compatible(path, output);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void compatible(String path, String output) throws Exception {
        // Carga el conjunto de datos original
        DataSource source = new DataSource(path);
        Instances data = source.getDataSet();

        // Asegura que el atributo de clase está correctamente establecido
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1); // Normalmente el último atributo
        }

        // Configura y aplica StringToWordVector al conjunto de datos original, replicando la configuración usada para headers.arff
        FixedDictionaryStringToWordVector fixedFilter = new FixedDictionaryStringToWordVector();
        fixedFilter.setDictionaryFile(new File(dictionaryTxt));
        fixedFilter.setInputFormat(data);

        Instances filteredData = Filter.useFilter(data, fixedFilter);

        SparseToNonSparse nonSparse = new SparseToNonSparse();
        nonSparse.setInputFormat(filteredData);
        Instances dataNonSparse = Filter.useFilter(filteredData, nonSparse);

        // Guarda el nuevo conjunto de datos ajustado
        FileWriter fwTrain = new FileWriter(output);
        fwTrain.write(dataNonSparse.toString());
        fwTrain.close();
    }
}
