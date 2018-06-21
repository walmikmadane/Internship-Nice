package rest;

import domain.SubscriptionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.SubscriptionManagementService;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/subscriptionmanagement")
public class SubcriptionManagementController
{
    @Autowired
    private SubscriptionManagementService subscriptionManagementService;

    @SuppressWarnings("rawtypes")
    @CrossOrigin
    @GetMapping(path = "/allschemes",produces =MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAllSchemes()
    {
        List<String> list=subscriptionManagementService.getAllSchemes();
        Iterator<String> iterator=list.iterator();
        String response="";
        while(iterator.hasNext())
        {
            response=response+iterator.next();
        }
        return ResponseEntity.ok(response);
    }
    @CrossOrigin
    @GetMapping(value = "/allschemes/userdetail",produces =MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAllSchemes(@RequestParam(value = "mobno",required = true)String mobno)
    {
        List<String> list=subscriptionManagementService.getAllSchemes(mobno);
        Iterator<String> iterator=list.iterator();
        String response="";
        while(iterator.hasNext())
        {
            response=response+iterator.next();
        }
        return ResponseEntity.ok(response);
    }
    @CrossOrigin
    @GetMapping(value = "/scheme",produces =MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getScheme(@RequestParam(value = "schemeid",required = true)String id,@RequestParam(value = "element",required = true)String element)
    {
        return ResponseEntity.ok(subscriptionManagementService.getScheme(id,element));
    }
    @CrossOrigin
    @PostMapping(value = "/activate",consumes = MediaType.APPLICATION_JSON_VALUE,produces =MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> activateScheme(@RequestBody(required = true)SubscriptionItem subscriptionItem)
    {
        return ResponseEntity.ok(subscriptionManagementService.activateScheme(subscriptionItem.getMobno(),subscriptionItem.getSchemeid()));
    }
    @CrossOrigin
    @DeleteMapping(value = "/deactivate",consumes = MediaType.APPLICATION_JSON_VALUE,produces =MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deactivateScheme(@RequestBody(required = true)SubscriptionItem subscriptionItem)
    {
        return ResponseEntity.ok(subscriptionManagementService.deactivateScheme(subscriptionItem.getMobno(),subscriptionItem.getSchemeid()));
    }



}
