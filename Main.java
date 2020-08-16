package encryptdecrypt;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Crypt {

    private CryptStrategy strategy;


    public Crypt(CryptStrategy strategy) {
        this.strategy = strategy;

    }

    String performCrypt(String message, int key) {
        return this.strategy.doCrypt(message, key);
    }


    public static String readFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void writeToFile(String fileName, String data) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(data);
        }
    }
}
    interface CryptStrategy {

        String doCrypt(String message, int key);
    }

    class ShiftCrypt implements CryptStrategy {

        public String doCrypt(String message, int key) {

            StringBuilder result = new StringBuilder();
            for (char character : message.toCharArray()) {
                if (character != ' ') {
                    int originalAlphabetPosition = character - 'a';
                    int newAlphabetPosition = (originalAlphabetPosition + key) % 26;
                    char newCharacter = (char) ('a' + newAlphabetPosition);
                    result.append(newCharacter);
                } else {
                    result.append(character);
                }
            }
            return result.toString();
        }
    }

    class UnicodeCrypte implements CryptStrategy {

        StringBuilder sb = new StringBuilder();

        public String doCrypt(String message, int key) {


            for (int i = 0; i < message.length(); i++) {
                char ch = message.charAt(i);
                sb.append((char) ((int) ch + key));
            }
        return sb.toString();

        }

    }

    public class Main {
        public static void main(String[] args) {

            String mode = "enc";
            int key = 0;
            String data = "";
            String in = "";
            String out = "";
            String alg = "shift";
            Crypt crypt = null;
            String cryptOutput = "undefinied";



            for (int i = 1; i < args.length; i++) {
                String setValue = args[i - 1];
                if (setValue.equals("-mode")) {
                    mode = args[i];
                } else if (setValue.equals("-key")) {
                    key = Integer.parseInt(args[i]);
                } else if (setValue.equals("-data")) {
                    data = args[i];
                } else if (setValue.equals("-out")) {
                    out = args[i];
                } else if (setValue.equals("-in")) {
                    in = args[i];
                } else if (setValue.equals("-alg")) {
                    alg = args[i];
                }
            }

            if (data.isEmpty() && !in.isEmpty()) {
                try {
                    data = Crypt.readFromFile(in);
                } catch (IOException e) {
                    System.out.println("Error");
                    System.exit(1);
                }
            }



            switch (alg) {
                case "shift":
                    crypt = new Crypt(new ShiftCrypt());
                    key = key % 26;
                    if (mode.equals("dec")) {
                        key = 26 - (key);

                    }
                    break;
                case "unicode":
                    crypt = new Crypt(new UnicodeCrypte());
                    if (mode.equals("dec")) {
                        key *= (-1);
                    }
                    break;
                default:
                    break;

            }
            if (crypt == null) {
                throw new RuntimeException(
                        "Unknown strategy type passed. Please, write to the author of the problem.");
            }

            cryptOutput = crypt.performCrypt(data, key);

            if (!out.isEmpty()) {
                try {
                    Crypt.writeToFile(out, cryptOutput);
                } catch (IOException e) {
                    System.out.println("Error");
                    System.exit(1);
                }
            } else System.out.println(cryptOutput);

        }
    }

