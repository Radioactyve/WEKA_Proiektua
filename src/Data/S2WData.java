package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

import java.io.FileWriter;

public class S2WData {
    private static int maxWords = 20000;
    private static String newTrainArff;
    private static String BoWTrainArff;

    public static void S2WData (String newTrainArffIn, String BoWTrainArffIn){
        /**
         * StringToWordFiltroa aplikatu datuei
         */
        try {
            newTrainArff = newTrainArffIn;
            BoWTrainArff = BoWTrainArffIn;
            s2w();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void s2w() throws Exception {
        /**
         * StringToWordFiltroa aplikatu datuei
         */
        // Kargatu entrenamendu datuak
        DataSource trainSource = new DataSource(newTrainArff);
        Instances data = trainSource.getDataSet();

        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }

        // Sortu eta konfiguratu StringToWordVector filtroa
        StringToWordVector filter = new StringToWordVector();
        filter.setOutputWordCounts(false);
        filter.setWordsToKeep(maxWords);
        filter.setLowerCaseTokens(true);
        filter.setAttributeIndices(String.valueOf(data.attribute("textValue").index()+1));

        //Tokenizer
        WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.setDelimiters(".,`´@%$/¿[]{}>*+^\"?!\n -="); // Guretzat garrantzitsua da _ mantentzea
        filter.setTokenizer(tokenizer);

        filter.setInputFormat(data);
        Instances train = Filter.useFilter(data, filter);

        // Sparsetik Non Sparsera bihurtu
        SparseToNonSparse nonSparse = new SparseToNonSparse();
        nonSparse.setInputFormat(train);
        Instances dataNonSparse = Filter.useFilter(train, nonSparse);

        // Gorde .arff -a
        FileWriter fwTrain = new FileWriter(BoWTrainArff);
        fwTrain.write(dataNonSparse.toString());
        fwTrain.close();

        System.out.println("\nS2W filtroa era egokian aplikatu da. Fitxategia: " +  BoWTrainArff);
    }
}
