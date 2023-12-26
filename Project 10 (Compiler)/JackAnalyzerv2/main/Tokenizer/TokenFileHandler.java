package main.Tokenizer;

import java.io.*;
import java.util.List;

public class TokenFileHandler {

    private String sourceFilename;
    private String targetFilename;

    public TokenFileHandler(String sourceFilename, String targetFilename){
        this.sourceFilename = sourceFilename;
        this.targetFilename = targetFilename;
    }

    public TokenFileHandler(String sourceFilename){
        this.sourceFilename = sourceFilename;
        this.targetFilename = sourceFilename.substring(0, sourceFilename.indexOf(".")) + "Tokens";
    }

    //Could also be done with Files class easier
    public String openFile() throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( new FileInputStream(sourceFilename)));

        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while(line != null){
            String cleanLine = line.trim();

            stringBuilder.append(cleanLine + '\n');
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public void writeFile(List<Token> tokens) throws IOException {

        try{
            FileWriter fileWriter =  new FileWriter(targetFilename + ".xml");

            fileWriter.write( "<tokens>" );
            while(!tokens.isEmpty()){
                fileWriter.write( tokens.get(0).toString() );
                tokens.remove(0);
            }
            fileWriter.write( "</tokens>" );

            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
