package queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.example.demo.configuration.AzureBlobStorageConfig;

@Configuration
public class QueueConnection {
    
    @Value("${azure.storage.account-name}")
    private String accountName = new AzureBlobStorageConfig().getAccountName();

    @Value("${azure.storage.account-key}")
    private String accountKey = new AzureBlobStorageConfig().getAccountKey();

    private String azureStorageConnection = 
    "DefaultEndpointsProtocol=https;" +
    "AccountName="+ accountName +";" +
    "AccountKey="+accountKey;

    public String getAzureStorageConnection() {
        return azureStorageConnection;
    }
}
