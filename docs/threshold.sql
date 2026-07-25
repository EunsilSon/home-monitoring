CREATE TABLE threshold (
    device_id UUID PRIMARY KEY,
    temperature_min NUMERIC(5,2),
    temperature_max NUMERIC(5,2),
    humidity_min NUMERIC(5,2),
    humidity_max NUMERIC(5,2),
    heat_index_min NUMERIC(5,2),
    heat_index_max NUMERIC(5,2),
    slack_enabled BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),

    CONSTRAINT fk_threshold_device
        FOREIGN KEY (device_id)
        REFERENCES device(id)
        ON DELETE CASCADE
);
