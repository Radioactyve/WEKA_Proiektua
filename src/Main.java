import Data.AnalizeData;
import Iragarpenak.Iragarpenak;
import Sailkatzailea.J48BaseLine;
import Sailkatzailea.ParametroEkorketa;
import Sailkatzailea.Prueba;

import java.util.Scanner;

public class Main {
    // ------------------------------ [PATHS] --------------------------------------
    private static String TRAIN_ARFF_PATH = "src/x_out/Data/train/train.arff";
    private static String DEV_ARFF_PATH = "src/x_out/Data/dev/dev.arff";
    private static String ANALISIS_TRAIN_TXT_PATH = "src/x_out/Data/train/analisis.txt";
    private static String ANALISIS_DEV_TXT_PATH = "src/x_out/Data/dev/analisis.txt";
    private static String NEW_TRAIN_ARFF_PATH = "src/x_out/Data/train/new_train.arff";
    private static String NEW_DEV_ARFF_PATH = "src/x_out/Data/dev/new_dev.arff";
    private static String DATA_COMBINED_PATH = "src/x_out/Data/combined.arff";
    private static String BOW_TRAIN_ARFF_PATH = "src/x_out/Data/train/BoW_train.arff";
    private static String FSS_TRAIN_ARFF_PATH = "src/x_out/Data/train/FSS_train.arff";
    private static String DICTIONARY_TXT_PATH = "src/x_out/Data/dictionary.txt";
    private static String COMPATIBLE_DEV_ARFF_PATH = "src/x_out/Data/dev/compatible_dev.arff";

    // ------------------------------ [SETTINGS] --------------------------------------
    private static int HOLD_OUT_PERCENTAGE = 70;







    // ------------------------------ [MAIN] --------------------------------------
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Menú de opciones
            System.out.println("MENU NAGUSIA:");
            System.out.println("1. Full exec");
            System.out.println("2. Fitxategien kokapena aldatu");
            System.out.println("3. Programen exekuzio aukerak aldatu");
            System.out.println("4. Irten");

            int aukera = scanner.nextInt();

            switch (aukera) {
                case 1:
                    fullExec();
                    break;
                case 2:
                    fitxategiKokapenAldaketa();
                    break;
                case 3:
                    settingsAldaketa();
                    break;
                case 4:
                    System.out.println("Irtetzen...");
                    return;
                default:
                    System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
            }
        }
    }



    // ------------------------------ [METODOAK] --------------------------------------

    private static void fullExec() throws Exception {
        // Ejecutar los métodos
        Data.GetRawData.GetRawData("train", TRAIN_ARFF_PATH);
        Data.GetRawData.GetRawData("dev", DEV_ARFF_PATH);

        AnalizeData.main(TRAIN_ARFF_PATH, ANALISIS_TRAIN_TXT_PATH);
        AnalizeData.main(DEV_ARFF_PATH, ANALISIS_DEV_TXT_PATH);

        Data.NewStratifiedHoldOut.NewStratifiedHoldOut(HOLD_OUT_PERCENTAGE, TRAIN_ARFF_PATH, NEW_TRAIN_ARFF_PATH,  DEV_ARFF_PATH, NEW_DEV_ARFF_PATH, DATA_COMBINED_PATH);

        Data.S2WData.S2WData(NEW_TRAIN_ARFF_PATH, BOW_TRAIN_ARFF_PATH);
        Data.FSS.FSS(BOW_TRAIN_ARFF_PATH, FSS_TRAIN_ARFF_PATH, DICTIONARY_TXT_PATH);

        Data.MakeCompatible.MakeCompatible(NEW_DEV_ARFF_PATH, COMPATIBLE_DEV_ARFF_PATH, DICTIONARY_TXT_PATH);
        Sailkatzailea.J48BaseLine.main(FSS_TRAIN_ARFF_PATH);

        //ParametroEkorketa.main(new String[]{TRAIN_ARFF_PATH, DEV_ARFF_PATH});
        //Prueba.main(new String[]{});
        J48BaseLine.main(FSS_TRAIN_ARFF_PATH);
        Iragarpenak.main();
    }

    private static void fitxategiKokapenAldaketa() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Fitxategien kokapenaren aldaketa:");
        System.out.println("1. TRAIN_ARFF_PATH: " + TRAIN_ARFF_PATH);
        System.out.println("2. DEV_ARFF_PATH: " + DEV_ARFF_PATH);
        System.out.println("3. ANALISIS_TRAIN_TXT_PATH: " + ANALISIS_TRAIN_TXT_PATH);
        System.out.println("4. ANALISIS_DEV_TXT_PATH: " + ANALISIS_DEV_TXT_PATH);
        System.out.println("5. NEW_TRAIN_ARFF_PATH: " + NEW_TRAIN_ARFF_PATH);
        System.out.println("6. NEW_DEV_ARFF_PATH: " + NEW_DEV_ARFF_PATH);
        System.out.println("7. DATA_COMBINED_PATH: " + DATA_COMBINED_PATH);
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
                DATA_COMBINED_PATH = pathAldaketa("DATA_COMBINED_PATH", scanner);
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
                break;
            default:
                System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
        }
    }

    private static String pathAldaketa(String pathIzena, Scanner scanner) {
        System.out.println("Hurrengo path-erako balio berria sakatu " + pathIzena + ": ");
        String pathBerria = scanner.next();
        System.out.println(pathIzena + " -ren path berria: " + pathBerria + " -ra");
        return pathBerria;
    }



    private static void settingsAldaketa() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Setting-en aldaketa:");
        System.out.println("1. HOLD_OUT_PERCENTAGE: " + HOLD_OUT_PERCENTAGE);
        System.out.println("2. Irten");

        int aukera = scanner.nextInt();

        switch (aukera) {
            case 1:
                HOLD_OUT_PERCENTAGE = aukeraAldatu("HOLD_OUT_PERCENTAGE",  scanner);
                break;
            case 2:
                System.out.println("Menu nagusira bueltatzen...");
                break;
            default:
                System.out.println("Arazoren bat egon da, mesedez aukera egoki bat sakatu");
        }
    }

    private static int aukeraAldatu(String settingIzena, Scanner scanner) {
        System.out.println("Introduce el nuevo valor para " + settingIzena + ": ");
        String newValue = scanner.next();
        System.out.println(settingIzena + " -ren balioa hurrengoa da orain: " + newValue );
        return Integer.parseInt(newValue);
    }




}
