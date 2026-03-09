-- drop the unused receipt_url column (JPA maps receiptUrl to "receipturl", not "receipt_url")
ALTER TABLE sessions DROP COLUMN IF EXISTS receipt_url;
