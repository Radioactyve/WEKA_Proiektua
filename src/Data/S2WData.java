package Data;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.SparseToNonSparse;

import java.io.File;

public class S2WData {
    private static int maxWords = 20000;
    public static void main (){
        try {
            s2w("new_train.arff");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void s2w(String arffPath) throws Exception {
        // Kargatu entrenamendu datuak
        ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(arffPath);
        Instances trainData = trainSource.getDataSet();

        if (trainData.classIndex() == -1){
            trainData.setClassIndex(1);
        }

        // Sortu eta konfiguratu StringToWordVector filtroa
        StringToWordVector filter = new StringToWordVector();
        filter.setOutputWordCounts(false);
        filter.setWordsToKeep(maxWords);
        filter.setLowerCaseTokens(true);
        filter.setDictionaryFileToSaveTo(new File("src/x_out/dictionary.txt"));

        //Tokenizer
        WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.setDelimiters(".;:'\"()?!\n -"); // Guretzat garrantzitsua da _ mantentzea
        filter.setTokenizer(tokenizer);

        filter.setInputFormat(trainData);

        Instances train = Filter.useFilter(trainData, filter);

        //Sparsetik Non Sparsera bihurtu
        SparseToNonSparse nonSparse = new SparseToNonSparse();
        nonSparse.setInputFormat(train);

        Instances dataNonSparse = Filter.useFilter(train, nonSparse);

        //Gorde .arff -a
        ArffSaver saver = new ArffSaver();
        saver.setFile(new File("src/x_out/BoW_train.arff"));
        saver.setInstances(dataNonSparse);
        saver.writeBatch();
    }
}
