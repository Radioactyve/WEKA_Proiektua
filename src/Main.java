import Data.GetRawData;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        GetRawData.csvToArff("train.csv", "train.arff");
    }
}