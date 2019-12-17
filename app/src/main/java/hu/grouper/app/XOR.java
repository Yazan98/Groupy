package hu.grouper.app;

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 12:03 PM
 */

public class XOR {

    public static String encryptDecrypt(String input) {
        char[] key = {'K', 'C', 'Q'};
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ key[i % key.length]));
        }
        return output.toString();
    }

}
