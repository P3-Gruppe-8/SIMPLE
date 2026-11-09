

CREATE TABLE it_staffs (
    it_staff_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL

    );

CREATE TABLE admins (
    admin_id SERIAL PRIMARY KEY,
    password_hash TEXT NOT NULL

    FOREIGN KEY (it_staff_id)
        REFERENCES it_staffs(it_staff_id)
    );

CREATE TABLE offices (
    office_id SERIAL PRIMARY KEY,
    office_address VARCHAR(200) NOT NULL,
    office_name VARCHAR(50) NOT NULL

    );

CREATE TABLE shelves (
    shelf_id SERIAL PRIMARY KEY,
    room VARCHAR(50),
    
    FOREIGN KEY (office_id)
            REFERENCES offices(office_id)
    
    );

CREATE TABLE descriptors (
    descriptor_id SERIAL PRIMARY KEY,
    descriptor_name VARCHAR(50) NOT NULL,
    is_loanable BOOLEAN NOT NULL,
    quantity INTEGER NOT NULL,
    
    );

CREATE TABLE descriptor_barcodes (
    barcode_id SERIAL PRIMARY KEY,
    barcode_key VARCHAR(50) NOT NULL,
    
    FOREIGN KEY (descriptor_id)
            REFERENCES descriptors(descriptor_id)
    );

CREATE TABLE loanables (
    loanable_id SERIAL PRIMARY KEY,
    loanable_barcode VARCHAR(50) NOT NULL,
    unit_number INTEGER NOT NULL,
    info_field VARCHAR(500),

    FOREIGN KEY (descriptor_id)
            REFERENCES descriptors(descriptor_id),

    FOREIGN KEY (shelf_id)
            REFERENCES shelves(shelf_id),
    
    );

CREATE TABLE descriptor_shelves (
    descriptor_id INTEGER NOT NULL REFERENCES descriptors(descriptor_id),
    shelf_id INTEGER NOT NULL REFERENCES shelves(shelf_id),

    PRIMARY KEY (descriptor_id, shelf_id)
    
    );


