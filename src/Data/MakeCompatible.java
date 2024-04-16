package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

import java.io.*;

public class MakeCompatible {
    private static String dictionaryTxt;
    public static void MakeCompatible(String path, String output, String dictionaryTxtIn){
        /**
         * Sartutako .arff-a train-ekin konpatiblea izateko eraldaketa burutu
         */
        try{
            dictionaryTxt = dictionaryTxtIn;
            compatible(path, output);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void compatible(String path, String output) throws Exception {
        /**
         * Sartutako .arff-a train-ekin konpatiblea izateko eraldaketa burutu
         */
        // Kargatu jatorrizko datu-multzoa
        DataSource source = new DataSource(path);
        Instances data = source.getDataSet();

        // Klase-atributua behar bezala ezarrita dagoela ziurtatzen du
        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }

        // StringToWordVector konfiguratu eta aplikatzen dio jatorrizko datu-multzoari, train.arff sistemarako erabilitako konfigurazioa erreplikatuz.
        FixedDictionaryStringToWordVector fixedFilter = new FixedDictionaryStringToWordVector();
        fixedFilter.setDictionaryFile(new File(dictionaryTxt));
        fixedFilter.setInputFormat(data);

        Instances filteredData = Filter.useFilter(data, fixedFilter);

        SparseToNonSparse nonSparse = new SparseToNonSparse();
        nonSparse.setInputFormat(filteredData);
        Instances dataNonSparse = Filter.useFilter(filteredData, nonSparse);

        // Gorde arff konpatiblea
        FileWriter fwTrain = new FileWriter(output);
        fwTrain.write(dataNonSparse.toString());
        fwTrain.close();
    }
}
