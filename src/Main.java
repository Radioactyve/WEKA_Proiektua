import Data.AnalizeData;
import Iragarpenak.Iragarpenak;
import Sailkatzailea.ParametroEkorketa;
import Sailkatzailea.ParametroEkorketaXGB;

import java.util.Scanner;

public class Main {
    // ------------------------------ [PATHS] --------------------------------------
    private static String TRAIN_ARFF_PATH = "src/x_out/Data/train/train.arff";
    private static String DEV_ARFF_PATH = "src/x_out/Data/dev/dev.arff";
    private static String TEST_ARFF_PATH = "src/x_out/Data/test/test.arff";
    private static String ANALISIS_TRAIN_TXT_PATH = "src/x_out/Data/train/analisis.txt";
    private static String ANALISIS_DEV_TXT_PATH = "src/x_out/Data/dev/analisis.txt";
    private static String NEW_TRAIN_ARFF_PATH = "src/x_out/Data/train/new_train.arff";
    private static String NEW_DEV_ARFF_PATH = "src/x_out/Data/dev/new_dev.arff";
    private static String COMBINED_ARFF_PATH = "src/x_out/Data/combined.arff";
    private static String BOW_TRAIN_ARFF_PATH = "src/x_out/Data/train/BoW_train.arff";
    private static String FSS_TRAIN_ARFF_PATH = "src/x_out/Data/train/FSS_train.arff";
    private static String DICTIONARY_TXT_PATH = "src/x_out/Data/dictionary.txt";
    private static String COMPATIBLE_DEV_ARFF_PATH = "src/x_out/Data/dev/compatible_dev.arff";
    private static String COMPATIBLE_TEST_PATH = "src/x_out/Data/test/compatible_test.arff";
    private static String J48_MODEL_PATH = "src/x_out/Sailkatzailea/j48.model";
    private static String RANDOMFOREST_MODEL_PATH = "src/x_out/Sailkatzailea/rf.model";
    private static String XGBOOST_MODEL_PATH = "src/x_out/Sailkatzailea/boost.model";
    private static String IRAGARPENAK_PATH = "src/x_out/Iragarpenak/iragarpenakBaseLine.txt";



    // ------------------------------ [SETTINGS] --------------------------------------
    private static int HOLD_OUT_PERCENTAGE = 70;
    private static int FSS_WORDS_TO_KEEP = 2000;
    private static String IRAGARPEN_MODELOA = "RF";
    // ---- (RF PARAMETERS) ----
    private static Boolean PN = true;
    private static Boolean BSP = true;
    private static Boolean MD = true;
    private static Boolean NT = true;






    // ------------------------------ [MAIN] --------------------------------------
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Menú de opciones
            System.out.println("MENU NAGUSIA:");
            System.out.println("1. Default full exec");
            System.out.println("2. Baseline");
            System.out.println("3. RandomForest");
            System.out.println("4. XGBoost");
            System.out.println("5. Iragarpenak");
            System.out.println("7. Fitxategien kokapena aldatu");
            System.out.println("8. Programen exekuzio aukerak aldatu");
            System.out.println("9. Irten");

            int aukera = scanner.nextInt();

            switch (aukera) {
                case 1:
                    //fullExec();
                    break;
                case 2:
                    prestatuData();
                    baselineExec();
                    break;
                case 3:
                    prestatuData();
                    randomForestExec();
                    break;
                case 7:
                    fitxategiKokapenAldaketa();
                    break;
                case 8:
                    settingsAldaketa();
                    break;
                case 9:
                    System.out.println("Irtetzen...");
                    return;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }



    // ------------------------------ [METODOAK] --------------------------------------


    private static void prestatuData() throws Exception {
        Data.GetRawData.GetRawData("train", TRAIN_ARFF_PATH);
        Data.GetRawData.GetRawData("dev", DEV_ARFF_PATH);

        AnalizeData.main(TRAIN_ARFF_PATH, ANALISIS_TRAIN_TXT_PATH);
        AnalizeData.main(DEV_ARFF_PATH, ANALISIS_DEV_TXT_PATH);

        Data.NewStratifiedHoldOut.NewStratifiedHoldOut(HOLD_OUT_PERCENTAGE, TRAIN_ARFF_PATH, NEW_TRAIN_ARFF_PATH,  DEV_ARFF_PATH, NEW_DEV_ARFF_PATH, COMBINED_ARFF_PATH);

        Data.S2WData.S2WData(NEW_TRAIN_ARFF_PATH, BOW_TRAIN_ARFF_PATH);
        Data.FSS.FSS(BOW_TRAIN_ARFF_PATH, FSS_TRAIN_ARFF_PATH, DICTIONARY_TXT_PATH,FSS_WORDS_TO_KEEP);

        Data.MakeCompatible.MakeCompatible(NEW_DEV_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH, DICTIONARY_TXT_PATH);
    }

    private static void baselineExec() throws Exception {
        Sailkatzailea.J48BaseLine.main(FSS_TRAIN_ARFF_PATH);
        Iragarpenak.main(J48_MODEL_PATH,"j48",TEST_ARFF_PATH,IRAGARPENAK_PATH);
    }

    private static void randomForestExec() throws Exception {
        ParametroEkorketa.main(new String[]{FSS_TRAIN_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH},PN,BSP,MD,NT);
        Iragarpenak.main(RANDOMFOREST_MODEL_PATH,"RF",TEST_ARFF_PATH,IRAGARPENAK_PATH);
    }

    private static void XGBoostExec() throws Exception {


        ParametroEkorketaXGB.main(new String[]{FSS_TRAIN_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH});
        Iragarpenak.main(RANDOMFOREST_MODEL_PATH,"RF",TEST_ARFF_PATH,IRAGARPENAK_PATH);
    }


    /*
    private static void fullExec() throws Exception {
        Sailkatzailea.J48BaseLine.main(FSS_TRAIN_ARFF_PATH);

        ParametroEkorketa.main(new String[]{TRAIN_ARFF_PATH, DEV_ARFF_PATH});
        //Prueba.main(new String[]{});
        //J48BaseLine.main(FSS_TRAIN_ARFF_PATH);
        //Iragarpenak.main();
    }
    */


    private static void fitxategiKokapenAldaketa() {
        Boolean exitMenu = false;
        while (!exitMenu){
            Scanner scanner = new Scanner(System.in);

            System.out.println("Fitxategien kokapenaren aldaketa:");
            System.out.println("1. TRAIN_ARFF_PATH: " + TRAIN_ARFF_PATH);
            System.out.println("2. DEV_ARFF_PATH: " + DEV_ARFF_PATH);
            System.out.println("3. ANALISIS_TRAIN_TXT_PATH: " + ANALISIS_TRAIN_TXT_PATH);
            System.out.println("4. ANALISIS_DEV_TXT_PATH: " + ANALISIS_DEV_TXT_PATH);
            System.out.println("5. NEW_TRAIN_ARFF_PATH: " + NEW_TRAIN_ARFF_PATH);
            System.out.println("6. NEW_DEV_ARFF_PATH: " + NEW_DEV_ARFF_PATH);
            System.out.println("7. DATA_COMBINED_PATH: " + COMBINED_ARFF_PATH);
            System.out.println("8. BOW_TRAIN_ARFF_PATH: " + BOW_TRAIN_ARFF_PATH);
            System.out.println("9. FSS_TRAIN_ARFF_PATH: " + FSS_TRAIN_ARFF_PATH);
            System.out.println("10. DICTIONARY_TXT_PATH: " + DICTIONARY_TXT_PATH);
            System.out.println("11. COMPATIBLE_DEV_ARFF_PATH: " + COMPATIBLE_DEV_ARFF_PATH);
            System.out.println("12. Irten");

            int aukera = scanner.nextInt();

            switch (aukera) {
                case 1:
                    TRAIN_ARFF_PATH = pathAldaketa("TRAIN_ARFF_PATH", scanner);
                    break;
                case 2:
                    DEV_ARFF_PATH = pathAldaketa("DEV_ARFF_PATH", scanner);
                    break;
                case 3:
                    ANALISIS_TRAIN_TXT_PATH = pathAldaketa("ANALISIS_TRAIN_TXT_PATH", scanner);
                    break;
                case 4:
                    ANALISIS_DEV_TXT_PATH = pathAldaketa("ANALISIS_DEV_TXT_PATH", scanner);
                    break;
                case 5:
                    NEW_TRAIN_ARFF_PATH = pathAldaketa("NEW_TRAIN_ARFF_PATH", scanner);
                    break;
                case 6:
                    NEW_DEV_ARFF_PATH = pathAldaketa("NEW_DEV_ARFF_PATH", scanner);
                    break;
                case 7:
                    COMBINED_ARFF_PATH = pathAldaketa("DATA_COMBINED_PATH", scanner);
                    break;
                case 8:
                    BOW_TRAIN_ARFF_PATH = pathAldaketa("BOW_TRAIN_ARFF_PATH", scanner);
                    break;
                case 9:
                    FSS_TRAIN_ARFF_PATH = pathAldaketa("FSS_TRAIN_ARFF_PATH", scanner);
                    break;
                case 10:
                    DICTIONARY_TXT_PATH = pathAldaketa("DICTIONARY_TXT_PATH", scanner);
                    break;
                case 11:
                    COMPATIBLE_DEV_ARFF_PATH = pathAldaketa("COMPATIBLE_DEV_ARFF_PATH", scanner);
                    break;
                case 12:
                    System.out.println("Menu nagusira bueltatzen...");
                    exitMenu = true;
                    break;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }

    private static String pathAldaketa(String pathIzena, Scanner scanner) {
        System.out.println("Hurrengo path-erako balio berria sakatu " + pathIzena + ": ");
        String pathBerria = scanner.next();
        System.out.println(pathIzena + " -ren path berria: " + pathBerria + " -ra");
        return pathBerria;
    }

    private static void settingsAldaketa() {
        Boolean exitMenu = false;
        while (!exitMenu){
            Scanner scanner = new Scanner(System.in);

            System.out.println("Setting-en aldaketa:");
            System.out.println("1. HOLD_OUT_PERCENTAGE: " + HOLD_OUT_PERCENTAGE);
            System.out.println("2. IRAGARPEN_MODELOA: " + IRAGARPEN_MODELOA);
            System.out.println("3. FSS_WORDS_TO_KEEP: " + FSS_WORDS_TO_KEEP);
            System.out.println("4. RF PARAMETROAK ALDATU");
            System.out.println("10. Irten");

            int aukera = scanner.nextInt();

            switch (aukera) {
                case 1:
                    HOLD_OUT_PERCENTAGE = Integer.parseInt(aukeraAldatu("HOLD_OUT_PERCENTAGE",  scanner));
                    break;
                case 2:
                    IRAGARPEN_MODELOA = aukeraAldatu("IRAGARPEN_MODELOA",  scanner);
                    break;
                case 3:
                    FSS_WORDS_TO_KEEP = Integer.parseInt(aukeraAldatu("FSS_WORDS_TO_KEEP",  scanner));
                    break;
                case 4:
                    Boolean exitMenuRF = false;
                    while (!exitMenuRF) {
                        System.out.println("Random Forest Setting-en aldaketa:");
                        System.out.println("1. PN: " + PN);
                        System.out.println("2. BSP: " + BSP);
                        System.out.println("3. MD: " + MD);
                        System.out.println("4. NT: " + NT);
                        System.out.println("0. Irten");

                        int aukeraRF = scanner.nextInt();

                        switch (aukeraRF) {
                            case 1:
                                PN = Boolean.parseBoolean(aukeraAldatu("PN", scanner));
                                break;
                            case 2:
                                BSP = Boolean.parseBoolean(aukeraAldatu("BSP", scanner));
                                break;
                            case 3:
                                MD = Boolean.parseBoolean(aukeraAldatu("MD", scanner));
                                break;
                            case 4:
                                NT = Boolean.parseBoolean(aukeraAldatu("NT", scanner));
                                break;
                            case 0:
                                System.out.println("Menu nagusira bueltatzen...");
                                exitMenuRF = true;
                                break;
                        }
                    }
                    break;
                case 10:
                    System.out.println("Menu nagusira bueltatzen...");
                    exitMenu = true;
                    break;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }

    private static String aukeraAldatu(String settingIzena, Scanner scanner) {
        System.out.println("Introduce el nuevo valor para " + settingIzena + ": ");
        String newValue = scanner.next();
        System.out.println(settingIzena + " -ren balioa hurrengoa da orain: " + newValue );

        return newValue;
    }




}
