-- Enables inventory management across multiple offices.
CREATE TABLE offices (
  office_id SERIAL PRIMARY KEY,
  -- Full address stored as a multiline string, lines seperated by '\n'.
  office_address VARCHAR(500) NOT NULL,
  office_name VARCHAR(50) UNIQUE NOT NULL
);

-- Stores IT staff information for identification and logging
CREATE TABLE it_staffs (
  it_staff_id SERIAL PRIMARY KEY,
  -- The first digits before @ in the employees work e-mail adress.
  username VARCHAR(50) UNIQUE NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE
);

-- Stores administrator credentials.
CREATE TABLE admins (
  admin_id SERIAL PRIMARY KEY,
  -- Argon2id hash generated and verified in Java.
  password_hash TEXT NOT NULL,
  it_staff_id INTEGER UNIQUE NOT NULL REFERENCES it_staffs (it_staff_id) ON DELETE CASCADE
);

-- Represents physical storage locations within an office.
CREATE TABLE shelves (
  shelf_id SERIAL PRIMARY KEY,
  -- Both the name and barcode key of the shelf.
  shelf_name VARCHAR(50) NOT NULL,
  -- Name of the room containing the shelf.
  room_name VARCHAR(50),
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE,
  -- The shelf name has to be unique within its office.
  UNIQUE (office_id, shelf_name)
);

CREATE TABLE descriptors (
  descriptor_id SERIAL PRIMARY KEY,
  descriptor_name VARCHAR(50) NOT NULL,
  -- Whether the item is a loanable or consumable.
  is_loanable BOOLEAN NOT NULL,
  -- Quantity is only saved for consumables, but is calculated for loanables when needed.
  quantity INTEGER CONSTRAINT positive_quantity CHECK (quantity >= 0),
  -- For relevant info about the descriptor, eg. model name
  info_field VARCHAR(500),
  -- For loanables, a template can be stored for unique fields, eg. serial number.
  info_template VARCHAR(500),
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE,
  -- Enables the composite foreign key in descriptor_barcodes.
  UNIQUE (descriptor_id, office_id)
);

CREATE TABLE descriptor_barcodes (
  barcode_id SERIAL PRIMARY KEY,
  barcode_key VARCHAR(50) NOT NULL,
  descriptor_id INTEGER NOT NULL,
  office_id INTEGER NOT NULL,
  -- Ensures the descriptor belongs to the specified office.
  FOREIGN KEY (descriptor_id, office_id) REFERENCES descriptors (descriptor_id, office_id) ON DELETE CASCADE,
  -- Ensures barcodes are unique within each office.
  UNIQUE (office_id, barcode_key)
);

CREATE TABLE loanables (
  loanable_id SERIAL PRIMARY KEY,
  -- -- RFID tag identifier used to identify an individual loanable.
  loanable_RFID VARCHAR(100) UNIQUE NOT NULL,
  -- "copy number" eg. laptop"3"
  unit_number INTEGER NOT NULL CONSTRAINT positive_unit_number CHECK (unit_number >= 0),
  -- The template from descriptor is filled out when adding loanables
  info_field VARCHAR(800),
  descriptor_id INTEGER NOT NULL REFERENCES descriptors (descriptor_id) ON DELETE CASCADE,
  shelf_id INTEGER REFERENCES shelves (shelf_id) ON DELETE SET NULL,
  -- Ensures unit_number are unique within each descriptor.
  UNIQUE (descriptor_id, unit_number)
);

-- Associates descriptors with their possible storage locations.
-- A descriptor may be stored on multiple shelves, and shelves can contain multible descriptors.
CREATE TABLE descriptor_shelves (
  descriptor_id INTEGER NOT NULL REFERENCES descriptors (descriptor_id) ON DELETE CASCADE,
  shelf_id INTEGER NOT NULL REFERENCES shelves (shelf_id) ON DELETE CASCADE,
  PRIMARY KEY (descriptor_id, shelf_id)
);

-- Records the borrowing of individual loanable items.
-- Loan agreements cannot directly be deleted through deletion of a loanable.
CREATE TABLE loan_agreements (
  loan_id SERIAL PRIMARY KEY,
  loan_time TIMESTAMPTZ NOT NULL,
  agreed_return_date TIMESTAMPTZ NOT NULL,
  borrower VARCHAR(50) NOT NULL,
  note VARCHAR(500),
  -- The agreed return date cannot be before the date borrowed
  CONSTRAINT chk_agreed_return_date CHECK (agreed_return_date > loan_time),
  loanable_id INTEGER NOT NULL REFERENCES loanables (loanable_id) ON DELETE RESTRICT
);

-- Monitors descriptor quantities against configured minimum thresholds.
CREATE TABLE stock_alerts (
  stock_alert_id SERIAL PRIMARY KEY,
  urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
  active BOOLEAN NOT NULL,
  muted BOOLEAN NOT NULL,
  min_quantity INTEGER NOT NULL,
  descriptor_id INTEGER NOT NULL REFERENCES descriptors (descriptor_id) ON DELETE CASCADE
);

-- Represents alerts associated with loan agreements.
CREATE TABLE loan_alerts (
  loan_alert_id SERIAL PRIMARY KEY,
  urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
  active BOOLEAN NOT NULL,
  muted BOOLEAN NOT NULL,
  loan_id INTEGER NOT NULL REFERENCES loan_agreements (loan_id) ON DELETE CASCADE
);

-- Monitors inactivity for an office based on the number of matching log entry types
-- within a configured time interval.
CREATE TABLE inactivity_alerts (
  inactivity_alert_id SERIAL PRIMARY KEY,
  alert_name VARCHAR(50) NOT NULL,
  alert_types VARCHAR(500) NOT NULL,
  urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
  active BOOLEAN NOT NULL,
  muted BOOLEAN NOT NULL,
  -- Minimum number of matching log entries within the interval.
  threshold_transactions INTEGER NOT NULL,
  threshold_interval INTEGER NOT NULL,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE
);

-- Records system events for auditing and activity tracking.
-- Event details are stored as text to preserve historical information
-- and support filtered view
CREATE TABLE log_entries (
  entry_id SERIAL PRIMARY KEY,
  entry_type VARCHAR(50) NOT NULL,
  entry_time TIMESTAMPTZ NOT NULL,
  descriptor VARCHAR(50),
  loanable VARCHAR(50),
  actor VARCHAR(50),
  borrower VARCHAR(50),
  info VARCHAR(500),
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE RESTRICT
);
