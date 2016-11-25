package com.example.ehsueh.appygolucky;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;


//This code is based off of code we created in the labs

public class ElasticSearchUserController {
    private static JestDroidClient client;
    private static String teamName = "cmput301f16t03";
    private static String userType = "user";

    /**
     * Adds a user to the server.  If the user already has an ID, will update that user record.
     */
    //Instead of calling this directly, we should use UserController.newUserLogin
    //This function will take care of adding to the server, as well as saving to file.
    public static class AddUsersTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            //We only want to process one user at each call
            User user = users[0];

            verifySettings();

            Index index = new Index.Builder(user).index(teamName).type(userType).build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        user.setId(result.getId());
                    }
                    else {
                        Log.e("ESUser", "Elastic search was not able to add the user.");
                    }
                }
                catch (Exception e) {
                    Log.e("ESUser", "We failed to add a user to elastic search!");
                    e.printStackTrace();
                }

            return null;
        }
    }

    /**
     * Given a username, this will query the server for the corresponding user.  In order to avoid
     * freezing the UI thread, this uses a callback function to return the users.
     * This task should be initiated from an activity, and should be given an
     * ESQueryListener that does something meaningful in that particular context.
     * For example, depending on the data that is returned, the program may decide whether
     * or not to create a new user account.
     */
//Method to returning data without freezing UI taken from
    //http://stackoverflow.com/questions/7618614/return-data-from-asynctask-class
    //Nov 16 Adil Soomro
    public static class GetUserByUsernameTask extends AsyncTask<String, Void, List<User>> {
        private ESQueryListener queryListener;

        /**
         * Instantiates a new Get user by username task.
         *
         * @param queryListener Query listener that handles the returned data.
         */
        public GetUserByUsernameTask(ESQueryListener queryListener) {
            this.queryListener = queryListener;
        }

        @Override
        protected List<User> doInBackground(String... params) {
            verifySettings();

            if (params[0] == null || params[0].equals("")) {
                return null;
            }

            String search_string = "{\"query\": {\"match\": {\"username\": \"" + params[0] + "\"}}}";

            Search search = new Search.Builder(search_string)
                    .addIndex(teamName)
                    .addType(userType)
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {

                    return result.getSourceAsObjectList(User.class);

                } else {
                    return (List<User>) new ArrayList<User>();
                }
            } catch (IOException e) {
                Log.i("Error", "Failed to communicate with elasticsearch server");
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Called at the completion of the task.  Feeds the retrieved data to the ESQueryListener
         * to do any further computation/action.
         */
        @Override
        protected void onPostExecute(List<User> retrievedUsers) {
            queryListener.onQueryCompletion(retrievedUsers);
        }
    }

    /**
     * Given an ID, deletes the corresponding user from the server.
     */
    //Instead of calling this method directly, we can use UserController.deleteUser(id).
    //This may be a little simpler, and may also deal with deleting relevant local data.
    public static class DeleteUserTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... IDs) {

            verifySettings();


            try {
                client.execute(new Delete.Builder(IDs[0])
                        .index(teamName)
                        .type(userType)
                        .build());
            }
            catch (Exception e) {
                Log.e("ESUser", "We failed to delete a user from elastic search!");
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * If the client hasn't been initialized then we should make it!
     */
    private static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }


}
