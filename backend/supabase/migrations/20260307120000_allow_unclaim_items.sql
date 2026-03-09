-- allow unclaiming items (setting claimedBy back to empty)
-- only block when a DIFFERENT user tries to claim an already-claimed item
CREATE OR REPLACE FUNCTION enforce_single_item_claim()
RETURNS TRIGGER AS $$
DECLARE
  item_key TEXT;
  old_claimed_by TEXT;
  new_claimed_by TEXT;
BEGIN
  FOR item_key IN SELECT jsonb_object_keys(NEW.items)
  LOOP
    old_claimed_by := OLD.items -> item_key ->> 'claimedBy';
    new_claimed_by := NEW.items -> item_key ->> 'claimedBy';

    -- block only if: item was claimed, new claimer is different, AND new claimer is not empty (unclaim)
    IF (old_claimed_by IS NOT NULL AND old_claimed_by <> '')
       AND (new_claimed_by IS DISTINCT FROM old_claimed_by)
       AND (new_claimed_by IS NOT NULL AND new_claimed_by <> '')
    THEN
      RAISE EXCEPTION 'Item % is already claimed by %. Two users cannot claim the same item.', item_key, old_claimed_by;
    END IF;
  END LOOP;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
