package Data;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.FileWriter;

public class FSS {
    private static String BoWTrainArff;
    private static String fssTrainArff;
    private static String dictionaryTxt;
    private static int wordsToKeep;
    public static void FSS(String BoWTrainArffIn, String fssTrainArffIn, String dictionaryTxtIn, int wordsToKeepIn){
        /**
         * FeatureSubsetSelection aplikatu train-eko datuei
         */
        try {
            BoWTrainArff = BoWTrainArffIn;
            fssTrainArff = fssTrainArffIn;
            dictionaryTxt = dictionaryTxtIn;
            wordsToKeep = wordsToKeepIn;
            fss();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void fss() throws Exception {
        /**
         * FeatureSubsetSelection aplikatu train-eko datuei, honez gain, hiztegi bat sortu
         * eta espezifikatutako hitz kopuru maximo bat finkatu atributu gutxiago sortzeko
         */

        DataSource source = new DataSource(BoWTrainArff);
        Instances data = source.getDataSet();

        if (data.classIndex() == -1) {
            data.setClassIndex(data.attribute("claseValue").index());
        }

        Ranker ranker = new Ranker();
        //ranker.setThreshold(0.1);
        ranker.setNumToSelect(wordsToKeep); // Número de atributos seleccionados

        AttributeSelection filter = new AttributeSelection();
        filter.setEvaluator(new InfoGainAttributeEval());
        filter.setSearch(ranker);
        filter.setInputFormat(data);
        Instances dataFiltered = Filter.useFilter(data, filter);

        //Gorde .arff -a
        FileWriter fwTrain = new FileWriter(fssTrainArff);
        fwTrain.write(dataFiltered.toString());
        fwTrain.close();

        //Hiztegia gorde
        FileWriter fw = new FileWriter(dictionaryTxt);
        for (int i=0; i<dataFiltered.numAttributes()-1; i++) {
            if(!dataFiltered.attribute(i).isDate()){
                String s = dataFiltered.attribute(i).name();
                fw.write(s + "\n");
            }
        }
        fw.close();
    }
}
