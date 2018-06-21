package service;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface SubscriptionManagementService
{
    public List<String>  getAllSchemes();
    public List<String> getAllSchemes(String mobno);
    public String getScheme(String schemeid,String element);
    public String activateScheme(String mobno,String schemeid);
    public String deactivateScheme(String mobno,String schemeid);




}
