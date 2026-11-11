

CREATE TABLE it_staffs (
    it_staff_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,

    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE CASCADE
    );

CREATE TABLE admins (
    admin_id SERIAL PRIMARY KEY,
    password_hash TEXT NOT NULL,

    it_staff_id INTEGER NOT NULL REFERENCES it_staffs(it_staff_id) ON DELETE CASCADE
    );

CREATE TABLE offices (
    office_id SERIAL PRIMARY KEY,
    office_address VARCHAR(500) NOT NULL,
    office_name VARCHAR(50) NOT NULL

    );

CREATE TABLE shelves (
    shelf_name VARCHAR(50) PRIMARY KEY,
    room_name VARCHAR(50),
    
    office_id INTEGER NOT NULL REFERENCES offices(office_id) ON DELETE CASCADE
    
    );

CREATE TABLE descriptors (
    descriptor_id SERIAL PRIMARY KEY,
    descriptor_name VARCHAR(50) NOT NULL,
    is_loanable BOOLEAN NOT NULL,
    quantity INTEGER NOT NULL,
    info_field VARCHAR(500)
    
    );

CREATE TABLE descriptor_barcodes (
    barcode_id SERIAL PRIMARY KEY,
    barcode_key VARCHAR(50) NOT NULL,
    
    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE
    );

CREATE TABLE loanables (
    loanable_id SERIAL PRIMARY KEY,
    loanable_barcode VARCHAR(50) NOT NULL,
    unit_number INTEGER NOT NULL,
    info_field VARCHAR(800),

    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE,

    shelf_id INTEGER NOT NULL DEFAULT 'NOT ON SHELF' REFERENCES shelves(shelf_id) ON DELETE SET DEFAULT 
    
    );

CREATE TABLE descriptor_shelves (
    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE,
    shelf_id INTEGER NOT NULL REFERENCES shelves(shelf_id) ON DELETE CASCADE,

    PRIMARY KEY (descriptor_id, shelf_id)
    
    );

CREATE TABLE loan_agreements (
    loan_id SERIAL PRIMARY KEY,
    loan_time TIMESTAMPTZ NOT NULL,
    expected_return_date DATE NOT NULL,
    actual_return_time TIMESTAMPTZ DEFAULT NULL,
    borrower VARCHAR(50) NOT NULL,
    note VARCHAR(500),

    loanable_id INTEGER NOT NULL REFERENCES loanables(loanable_id) ON DELETE CASCADE
    );


CREATE TABLE stock_alert (
    stock_alert_id SERIAL PRIMARY KEY,
    urgency INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    muted BOOLEAN NOT NULL,
    min_quantity INTEGER NOT NULL,

    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id) ON DELETE CASCADE
    );

CREATE TABLE loan_alert (
    loan_alert_id SERIAL PRIMARY KEY,
    urgency INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    muted BOOLEAN NOT NULL,

    loan_id INTEGER NOT NULL REFERENCES loan_agreements(loan_id) ON DELETE CASCADE
    );

CREATE TABLE log_entries (
    entry_id SERIAL PRIMARY KEY,
    entry_type VARCHAR(50) NOT NULL,
    entry_time TIMESTAMPTZ NOT NULL,
    descriptor VARCHAR(50),
    actor VARCHAR(50),
    borower VARCHAR(50),
    info VARCHAR(500),

    office_id INTEGER NOT NULL REFERENCES offices(office_id)
    );