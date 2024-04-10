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
    private static String newTrainArff = "src/x_out/Data/train/new_train.arff";
    private static String BoWTrainArff = "src/x_out/Data/train/BoW_train.arff";
    public static void S2WData (){
        try {
            s2w();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void s2w() throws Exception {
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
        filter.setAttributeIndices(String.valueOf(data.attribute("textValue").index()));

        //Tokenizer
        WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.setDelimiters(".,`´@%$/¿[]{}>*+^\"?!\n -="); // Guretzat garrantzitsua da _ mantentzea
        filter.setTokenizer(tokenizer);

        filter.setInputFormat(data);

        Instances train = Filter.useFilter(data, filter);

        //Sparsetik Non Sparsera bihurtu
        SparseToNonSparse nonSparse = new SparseToNonSparse();
        nonSparse.setInputFormat(train);

        Instances dataNonSparse = Filter.useFilter(train, nonSparse);

        //Gorde .arff -a
        FileWriter fwTrain = new FileWriter(BoWTrainArff);
        fwTrain.write(dataNonSparse.toString());
        fwTrain.close();
    }
}
