package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //regex = [\\w|\\d]+\\.*
    public static int syllable_counter(String word) {
        String i = "(?i)[aiouy][aeiouy]*|e[aeiouy]*(?!d?\\b)";
        Matcher m = Pattern.compile(i).matcher(word);
        int count = 0;

        while (m.find()) {
            count++;
        }

        // return at least 1
        return Math.max(count, 1);
    }

    public static int[] easy_word_counter(String input) {
        String[] sentences = input.split("[?.!]");
        int sentence_size = sentences.length;
        String[] word_count = input.split(" ");
        int word_size = word_count.length;

        Pattern pattern = Pattern.compile("[.\\S]");
        Matcher characters = pattern.matcher(input);
        int character_number = (int) characters.results().count();
        int total_syllables = 0;
        int polysyllables = 0;


        for (int i = 0; i < word_size; i++) {
            int syllables = syllable_counter(word_count[i]);
            total_syllables += syllables;
            if (syllables > 2) {
                polysyllables += 1;
            }

        }

        return new int[]{character_number, word_size, sentence_size, total_syllables, polysyllables};

    }

    public static String fileHandler(String file) {
        try {
            //need to add stuff to file path to make it valid, probably ./
            file = "./" + file;
            return new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            return null;
        }
    }

    public static float score(double characters, double words, double sentences) {
        return (float) (4.71 * (characters / words) + 0.5 * (words / sentences) - 21.43);
    }

    public static void populate_hashmap(HashMap<Integer, String> age_brackets) {
        age_brackets.put(1, "6");
        age_brackets.put(2, "7");
        age_brackets.put(3, "8");
        age_brackets.put(4, "9");
        age_brackets.put(5, "10");
        age_brackets.put(6, "11");
        age_brackets.put(7, "12");
        age_brackets.put(8, "13");
        age_brackets.put(9, "14");
        age_brackets.put(10, "15");
        age_brackets.put(11, "16");
        age_brackets.put(12, "17");
        age_brackets.put(13, "18");
        age_brackets.put(14, "22");

    }

    public static float Flesch_Kincaid(float words, float sentences, float syllables) {
        return (float) (0.39 * (words / sentences) + 11.8 * (syllables / words) - 15.39);
    }

    public static float SMOG(int polysyllables, float sentences) {
        return (float) (1.043 * Math.sqrt(polysyllables * (30 / sentences)) + 3.1291);
    }

    public static float Coleman_liau(double average_char, double average_sentences) {
        return (float) (((0.0588 * average_char - 0.296 * average_sentences)) - 15.8);
    }

    public static float print_indexes(int[] statistics, String alg_choice, HashMap<Integer, String> age_brackets) {
        int index = 0;
        float readibility;
        String readbility_string;
        float average_age = 0;


        switch (alg_choice) {
            case "ARI":
                readibility = score(statistics[0], statistics[1], statistics[2]);
                index = (int) Math.ceil(readibility);
                readbility_string = rounding(readibility);
                if (index > 14) {
                    index = 14;
                }
                System.out.printf("Automated Readability Index: %s (about %s-year-olds).\n", readbility_string, age_brackets.get(index));
                average_age += Integer.parseInt(age_brackets.get(index));
                break;
            case "FK":
                readibility = Flesch_Kincaid(statistics[1], statistics[2], statistics[3]);
                index = (int) Math.ceil(readibility);
                readbility_string = rounding(readibility);
                if (index > 14) {
                    index = 14;
                }
                System.out.printf("Flesch" + (char) (8211) + "Kincaid readability tests: %s (about %s-year-olds).\n", readbility_string, age_brackets.get(index));
                average_age += Integer.parseInt(age_brackets.get(index));
                break;
            case "SMOG":
                readibility = SMOG(statistics[4], statistics[2]);
                index = (int) Math.ceil(readibility);
                readbility_string = rounding(readibility);
                if (index > 14) {
                    index = 14;
                }
                System.out.printf("Simple Measure of Gobbledygook: %s (about %s-year-olds).\n", readbility_string, age_brackets.get(index));

                average_age += Integer.parseInt(age_brackets.get(index));
                break;
            case "CL":
                double l = (double) statistics[0]/ (double) statistics[1] * 100;
                double s = (double) statistics[2] / (double) statistics[1] * 100;

                readibility = Coleman_liau(l,s);
                index = (int) Math.ceil(readibility);
                readbility_string = rounding(readibility);
                if (index > 14) {
                    index = 14;
                }

                System.out.printf("Coleman" + (char) (8211) + "Liau index: %s0 (about %s-year-olds).\n", readbility_string, age_brackets.get(index));
                average_age += Integer.parseInt(age_brackets.get(index));
                break;
        }
        return average_age;
    }

    public static String rounding(float score) {
        return (Float.toString((float) (Math.round(score * 100.0) / 100.0)));
    }

    public static void main(String[] args) {
        String input;
        HashMap<Integer, String> age_brackets = new HashMap<Integer, String>();
        String ages;
        String readbility_string;
        float readibility;
        int index;
        int average_age = 0;

        Scanner scanner = new Scanner(System.in);

        populate_hashmap(age_brackets);

        input = fileHandler(args[0]);
        int[] statistics = easy_word_counter(input);

        System.out.println("The text is:");
        System.out.println(input);

        System.out.printf("\nWords: %d \n" + "Sentences: %d\n" + "Characters: %d\n" + "Syllables: %d\n", statistics[1], statistics[2], statistics[0], statistics[3]);
        System.out.println("Polysyllables: " + statistics[4]);

        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String alg_choice = scanner.nextLine();
        String[] choices = new String[]{"ARI", "FK", "SMOG", "CL"};
        System.out.println();


        if (alg_choice.equals("all")) {
            for (String value : choices) {
                average_age += print_indexes(statistics, value, age_brackets);
            }

            int average_age_string = (int) (average_age * 100 / 4.00) / 100;
            System.out.printf("\nThis text should be understood in average by %d-year-olds.", average_age_string);
        } else {
            average_age += print_indexes(statistics, alg_choice, age_brackets);
        }

    }
}
