import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    BufferedWriter bufferedWriter;

    Writer(){

        String path="/home/sergio/Documents/RecommenderData.txt";
        File file = new File(path);

        try {
            // If file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
        }catch (IOException IOE){
            IOE.printStackTrace();
        }

    }

    public void write(String userID, String itemID, String rating){
        try {
            bufferedWriter.write(String.format("%s,%s,%s\n", userID, itemID, rating));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
