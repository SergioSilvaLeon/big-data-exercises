
import java.io.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class MovieRecommender {


    private String pathName;
    private BiMap<String,Integer> products = HashBiMap.create();
    private BiMap<String,Integer> users = HashBiMap.create();
    private int reviews;

    UserBasedRecommender recommender;
    // Data Model
    String dataModel = "";



    public MovieRecommender(String pathName){

        this.pathName = pathName;
        run();
        runUserBasedRecommender();
    }

    private void runUserBasedRecommender() {
        try {
            DataModel model = new FileDataModel(new File("/home/sergio/Documents/RecommenderData.txt"));
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }

    }

    private void run(){

        File file = new File(pathName);
        Writer writer = new Writer();

        // Read huge file
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            GZIPInputStream gzip = new GZIPInputStream(fileInputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));

            String[] array = new String[2];
            Integer itemIDRef = null;
            Integer userIDRef = null;
            String ratingIDRef = "";
            // Read Line by Line
            String line;
            while ( (line = reader.readLine()) != null){

                if (line.startsWith("review/userId: ")){

                    array = line.split(" ");
                    String userID = array[1];
                    if (!users.containsKey(userID)){
                        users.put(userID, users.size());
                    }
                    userIDRef = users.get(userID);


                }else if (line.startsWith("product/productId: ")){

                    array = line.split(" ");
                    String itemID = array[1];
                    if (!products.containsKey(itemID)){
                        products.put(itemID, products.size());
                    }
                    itemIDRef = products.get(itemID);

                }else if(line.startsWith("review/score: ")){

                    array = line.split(" ");
                    ratingIDRef = array[1];
                    reviews ++;

                    // Append Data

                    writer.write(userIDRef.toString(), itemIDRef.toString(), ratingIDRef.toString());


                }


            }

            System.out.print("");
            // Close Writer
            writer.close();




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getTotalReviews(){
        return reviews;
    }

    public int getTotalProducts(){
        return products.size();
    }

    public int getTotalUsers(){
        return users.size();
    }

    public List<String> getRecommendationsForUser(String userID){

        List<String> recommandations = new ArrayList<String>();
        if (users.containsKey(userID)){
            List<RecommendedItem> recommendations = null;
            try {
                recommendations = recommender.recommend(users.get(userID), 3);

            for (RecommendedItem recommendation : recommendations) {
                recommandations.add(products.inverse().get((int)recommendation.getItemID()));
            }
            } catch (TasteException e) {
                e.printStackTrace();
            }
        }

        return recommandations;
    }

}
