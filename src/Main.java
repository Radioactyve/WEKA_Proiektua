import Data.*;
import Iragarpenak.*;
import Sailkatzailea.*;

public class Main {
    public static void main(String[] args) throws Exception {
        GetRawData.GetRawData("train.csv", "src/x_out/Data/train/train.arff");
        GetRawData.GetRawData("dev.csv", "src/x_out/Data/dev/dev.arff");
        GetRawData.GetRawData("test.csv", "src/x_out/Data/test/test.arff");
        AnalizeData.AnalizeData();
        NewStratifiedHoldOut.NewStratifiedHoldOut(70);
        S2WData.S2WData();
        FSS.FSS();
        MakeCompatible.MakeCompatible("src/x_out/Data/dev/new_dev.arff", "src/x_out/Data/dev/compatible_dev.arff");
        MakeCompatible.MakeCompatible("src/x_out/Data/test/new_test.arff", "src/x_out/Data/test/compatible_test.arff");

        J48BaseLine.main("src/x_out/Data/test/compatible_test.arff");

        Iragarpenak.main();
    }
}