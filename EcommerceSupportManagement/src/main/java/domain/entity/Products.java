package domain.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "products")
public class Products
{
    private String productid;
    private String categoryname;
    private String description;
    private String name;
    private int price;
    private int availability;

    @DynamoDBHashKey
    public String getProductid() {
        return productid;
    }

    @DynamoDBAttribute
    public String getCategoryname() {
        return categoryname;
    }

    @DynamoDBAttribute
    public String getDescription() {
        return description;
    }

    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    @DynamoDBAttribute
    public int getPrice() {
        return price;
    }

    @DynamoDBAttribute
    public int getAvailability() {
        return availability;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public Products(String productid, String categoryname, String description, String name, int price, int availability) {
        this.productid = productid;
        this.categoryname = categoryname;
        this.description = description;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }
    public Products(){}
}
