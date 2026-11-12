
CREATE TABLE offices (
    office_id SERIAL PRIMARY KEY,
    office_address VARCHAR(500) NOT NULL,
    office_name VARCHAR(50) UNIQUE NOT NULL

    );

CREATE TABLE it_staffs (
    it_staff_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,

    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE CASCADE
    );

CREATE TABLE admins (
    admin_id SERIAL PRIMARY KEY,
    password_hash TEXT NOT NULL,

    it_staff_id INTEGER UNIQUE NOT NULL REFERENCES it_staffs(it_staff_id) ON DELETE CASCADE
    );

CREATE TABLE shelves (
    shelf_id SERIAL PRIMARY KEY,
    shelf_name VARCHAR(50) NOT NULL,
    room_name VARCHAR(50),
    
    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE CASCADE,
    UNIQUE (office_id, shelf_name)
    
    );

CREATE TABLE descriptors (
    descriptor_id SERIAL PRIMARY KEY,
    descriptor_name VARCHAR(50) NOT NULL,
    is_loanable BOOLEAN NOT NULL,
    quantity INTEGER NOT NULL CONSTRAINT positive_quantity CHECK (quantity >= 0),
    info_field VARCHAR(500),
    info_template VARCHAR(500),

    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE CASCADE,

    UNIQUE (descriptor_id, office_id)
    );

CREATE TABLE descriptor_barcodes (
    barcode_id SERIAL PRIMARY KEY,
    barcode_key VARCHAR(50) NOT NULL,

    descriptor_id INTEGER NOT NULL,
    office_id INTEGER NOT NULL,
    
    FOREIGN KEY (descriptor_id, office_id)
        REFERENCES descriptors(descriptor_id, office_id)
        ON DELETE CASCADE,
    
    -- Barcodes til en descriptor er unik for kontoret
    UNIQUE (office_id, barcode_key)
    );

CREATE TABLE loanables (
    loanable_id SERIAL PRIMARY KEY,
    loanable_RFID VARCHAR(100) UNIQUE NOT NULL,
    unit_number INTEGER NOT NULL CONSTRAINT positive_unit_number CHECK (unit_number >= 0),
    info_field VARCHAR(800),

    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE,
    shelf_id INTEGER REFERENCES shelves(shelf_id) ON DELETE SET NULL,

    UNIQUE (descriptor_id, unit_number)
    );

CREATE TABLE descriptor_shelves (
    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE,
    shelf_id INTEGER NOT NULL REFERENCES shelves(shelf_id) ON DELETE CASCADE,

    PRIMARY KEY (descriptor_id, shelf_id)
    
    );

CREATE TABLE loan_agreements (
    loan_id SERIAL PRIMARY KEY,
    loan_time TIMESTAMPTZ NOT NULL,
    expected_return_date TIMESTAMPTZ NOT NULL,
    borrower VARCHAR(50) NOT NULL,
    note VARCHAR(500),

    CONSTRAINT chk_expected_return_date CHECK (expected_return_date > loan_time),

    loanable_id INTEGER NOT NULL REFERENCES loanables(loanable_id) ON DELETE RESTRICT
    );


CREATE TABLE stock_alerts (
    stock_alert_id SERIAL PRIMARY KEY,
    urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
    active BOOLEAN NOT NULL,
    muted BOOLEAN NOT NULL,
    min_quantity INTEGER NOT NULL,

    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE
    );

CREATE TABLE loan_alerts (
    loan_alert_id SERIAL PRIMARY KEY,
    urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
    active BOOLEAN NOT NULL,
    muted BOOLEAN NOT NULL,

    loan_id INTEGER NOT NULL REFERENCES loan_agreements(loan_id) ON DELETE CASCADE
    );

CREATE TABLE inactivity_alerts (
    inactivity_alert_id SERIAL PRIMARY KEY,
    alert_name VARCHAR(50) NOT NULL,
    alert_types VARCHAR(500) NOT NULL,
    urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
    active BOOLEAN NOT NULL,
    muted BOOLEAN NOT NULL,
    -- Log entries pr. time interval
    threshold_transactions INTEGER NOT NULL,
    threshold_interval INTEGER NOT NULL,

    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE CASCADE
    );

CREATE TABLE log_entries (
    entry_id SERIAL PRIMARY KEY,
    entry_type VARCHAR(50) NOT NULL,
    entry_time TIMESTAMPTZ NOT NULL,
    descriptor VARCHAR(50),
    loanable VARCHAR(50),
    actor VARCHAR(50),
    borrower VARCHAR(50),
    info VARCHAR(500),

    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE RESTRICT
    );