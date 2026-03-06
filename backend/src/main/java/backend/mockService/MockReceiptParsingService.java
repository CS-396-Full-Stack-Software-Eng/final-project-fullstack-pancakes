package backend.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

@Service
public class MockReceiptParsingService {
    
    public Map<String, Map<String, Object>> parseReceipt(String imageUrl) throws InterruptedException {
        
        Thread.sleep(3000); // 3s sleep so we can see the skeleton

        Map<String, Map<String, Object>> items = new LinkedHashMap<>();
        items.put("item_1", Map.of("name", "Burger", "price", 15.00, "claimedBy", ""));
        items.put("item_2", Map.of("name", "Fries", "price", 5.00, "claimedBy", ""));
        items.put("item_3", Map.of("name", "Coke", "price", 3.00, "claimedBy", ""));
        items.put("item_4", Map.of("name", "Pizza", "price", 18.00, "claimedBy", ""));
        return items;
    }
}