import java.io.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class UploadTxtOnS3
{
    public static void main(String[] args) {
        String clientRegion = "us-west-2";
        String bucketName = "icinternschatbot";
       // String stringObjKeyName = "*** String object key name ***";
        String fileObjKeyName = "Chatbottrainingdataset";
        String fileName = "D:\\chatbot.txt";

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSCredentialsProvider() {
                        @Override
                        public AWSCredentials getCredentials() {
                            return new AWSCredentials() {
                                @Override
                                public String getAWSAccessKeyId() {
                                    return "AKIAJ3ZQUIJVNUD7UKZA";
                                }

                                @Override
                                public String getAWSSecretKey() {
                                    return "EKUBWJ4eDnWklcEkkv1HBDYXX7Uzt0hD3GDsMCy2";
                                }
                            };
                        }

                        @Override
                        public void refresh() {

                        }
                    })
                    .build();

            // Upload a text string as a new object.
           // s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);

            //get object from s3
            System.out.println("Downloading an object");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, fileObjKeyName));
            System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(s3object.getObjectContent());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private static void displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
    }
}

