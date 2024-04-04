package Data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.*;

public class MakeCompatible {
    public static void main(){
        try{
            List<String> header = getHeader("src/x_out/new_dev.arff");
            System.out.println(header.toString());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static List<String> getHeader(String arffPath) throws Exception {
        DataSource source = new DataSource(arffPath);
        Instances data = source.getDataSet();

        if (data.classIndex() == -1) {
            data.setClassIndex(1);
        }

        List<String> header = new ArrayList<>();

        header.add(data.relationName());
        for (int i=0; i< data.numAttributes(); i++){
            header.add(data.attribute(i).toString());
        }

        return header;
    }

    public static void makeTestCompatible(){

    }

}
