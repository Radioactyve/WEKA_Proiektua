package Data;

import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.File;

public class FSS {
    public static void main(){
        try {
            fss("src/x_out/BoW_train.arff");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void fss(String arffPath) throws Exception {

        DataSource source = new DataSource(arffPath);
        Instances data = source.getDataSet();

        if (data.classIndex() == -1) {
            data.setClassIndex(1);
        }

        AttributeSelection filter = new AttributeSelection();
        filter.setEvaluator(new GainRatioAttributeEval());
        filter.setSearch(new Ranker());
        filter.setInputFormat(data);
        Instances dataFiltered = Filter.useFilter(data, filter);

        //Gorde .arff -a
        ArffSaver saver = new ArffSaver();
        saver.setFile(new File("src/x_out/FSS_train.arff"));
        saver.setInstances(dataFiltered);
        saver.writeBatch();
    }
}
