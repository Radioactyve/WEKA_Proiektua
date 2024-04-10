import Data.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        //GetRawData.csvToArff("train.csv", "src/x_out/train.arff");
        //GetRawData.csvToArff("dev.csv", "src/x_out/dev.arff");
        //AnalizeData.main();
        //NewStratifiedHoldOut.main();
        //S2WData.main();
        //FSS.main();
        MakeCompatible.main();
    }
}