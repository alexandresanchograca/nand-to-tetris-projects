package main;

import main.Tokenizer.JackTokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Our compiler point of entry.
 * This program is a syntax analizer for
 * our hack/jack programming language
 * */
public class Main {
    public static void main(String[] args) {

        System.out.println("Compilation process starting...");

        try{

            String filename = "";
            if(args.length > 0){
                filename = args[0];
            }

            Path filepath = Paths.get(filename);
            if(Files.isDirectory(filepath)){

                compileDirectory(filepath);

            }else if( validFileExtension(filepath)){

                JackTokenizer jackTokenizer = new JackTokenizer(filename);
                jackTokenizer.run();

            }

            System.out.println("Source was compiled sucessfully!");
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void compileDirectory(Path filepath) throws IOException {
        Files.list(filepath)
                .filter(file -> !Files.isDirectory(file) && validFileExtension(file))
                .forEach(file -> {
                            try {

                                JackTokenizer jackTokens = new JackTokenizer(file.toString());
                                jackTokens.run();

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public static boolean validFileExtension(Path filepath){
        return filepath.toString().endsWith(".jack");
    }
}
