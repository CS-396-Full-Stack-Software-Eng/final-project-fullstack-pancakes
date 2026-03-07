package backend.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.LinkedHashMap;
import net.sourceforge.tess4j.Tesseract;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReceiptParsingService {

    private final Tesseract tesseract;

    public ReceiptParsingService() {
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("/opt/homebrew/share/tessdata");
    }

    // parse the actual Tesseract results w/ Regex
    private Map<String, Map<String, Object>> parseResult(String result) {
        Map<String, Map<String, Object>> items = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(.+?)\\s+\\$?(\\d+\\.\\d{2})");
        Pattern qtyPattern = Pattern.compile("^(\\d+)\\s+(.+)");

        String[] lines = result.split("\\r?\\n");
        int itemCount = 1;

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String fullName = matcher.group(1).trim();
                double totalPrice = Double.parseDouble(matcher.group(2));

                if (isTotalLine(fullName))
                    continue;

                Matcher qtyMatcher = qtyPattern.matcher(fullName);

                if (qtyMatcher.find()) {
                    int quantity = Integer.parseInt(qtyMatcher.group(1));
                    String actualName = qtyMatcher.group(2).trim();

                    if (quantity > 1) {
                        double unitPrice = totalPrice / quantity;
                        for (int i = 0; i < quantity; i++) {
                            items.put("item_" + itemCount, createItemMap(actualName, unitPrice));
                            itemCount++;
                        }
                    } else {
                        items.put("item_" + itemCount, createItemMap(actualName, totalPrice));
                        itemCount++;
                    }
                } else {
                    items.put("item_" + itemCount, createItemMap(fullName, totalPrice));
                    itemCount++;
                }
            }
        }
        return items;
    }

    private Map<String, Object> createItemMap(String name, double price) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("price", price);
        item.put("claimedBy", "");
        return item;
    }

    private boolean isTotalLine(String name) {
        String upper = name.toUpperCase();
        return upper.contains("TOTAL") || upper.contains("SUBTOTAL") || upper.contains("TAX") || upper.contains("TIP");
    }

    public Map<String, Map<String, Object>> parseReceipt(String imageUrl) throws InterruptedException {
        try {
            URL url = URI.create(imageUrl).toURL();
            File tempFile = File.createTempFile("receipt_", ".png");

            try (InputStream in = url.openStream()) {
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("Processing image from Supabase: " + imageUrl);
            String result = tesseract.doOCR(tempFile);
            System.out.println("OCR RESULT: " + result);

            tempFile.delete();
            return parseResult(result);

        } catch (Exception e) {
            System.err.println("Error during OCR processing: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }
}