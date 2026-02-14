CREATE TYPE parsing_status_type AS ENUM ('PENDING', 'COMPLETED', 'FAILED');

CREATE TABLE sessions
(
    session_id     uuid PRIMARY KEY    DEFAULT gen_random_uuid(),
    party_size     int,
    users          jsonb,
    items          jsonb,

    -- (10 digits max, 2 of which are after the decimal)
    tip_per_person numeric(10, 2),
    tax_per_person numeric(10, 2),

    parsing_status parsing_status_type DEFAULT 'PENDING',
    created_at     timestamptz         DEFAULT now()
);