-- only allow items to be claimed by 1 user (first-come-first-served)
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

    -- if old_claimed_by != null and != "" (meaning a user has already claimed it)
    -- and if old claim is different from new claim, then raise exception
    IF (old_claimed_by IS NOT NULL AND old_claimed_by <> '')
       AND (new_claimed_by IS DISTINCT FROM old_claimed_by)
    THEN
      RAISE EXCEPTION 'Item % is already claimed by %. Two users cannot claim the same item.', item_key, old_claimed_by;
    END IF;
  END LOOP;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- apply trigger to sessions table
CREATE OR REPLACE TRIGGER single_claim_guard
BEFORE UPDATE OF items ON public.sessions
FOR EACH ROW
EXECUTE FUNCTION enforce_single_item_claim();