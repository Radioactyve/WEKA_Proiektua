package Data;

import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.File;
import java.io.FileWriter;

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
            data.setClassIndex(data.attribute("claseValue").index());
        }

        AttributeSelection filter = new AttributeSelection();
        Ranker ranker = new Ranker();
        ranker.setNumToSelect(10); // Número de atributos seleccionados
        filter.setEvaluator(new GainRatioAttributeEval());
        filter.setSearch(ranker);
        filter.setInputFormat(data);
        Instances dataFiltered = Filter.useFilter(data, filter);

        //Gorde .arff -a
        FileWriter fwTrain = new FileWriter("src/x_out/FSS_train.arff");
        fwTrain.write(dataFiltered.toString());
        fwTrain.close();
    }
}
