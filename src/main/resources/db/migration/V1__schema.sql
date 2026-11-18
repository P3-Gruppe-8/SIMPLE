-- Enables inventory management across multiple offices.
CREATE TABLE offices (
  office_id SERIAL PRIMARY KEY,
  -- Full address stored as a multiline string, lines seperated by '\n'.
  office_address VARCHAR(500) NOT NULL,
  office_name VARCHAR(50) NOT NULL,
  -- Soft delete, null if not deleted
  office_deleted TIMESTAMPTZ
);

-- The office name has to be unique for offices which are not soft deleted.
CREATE UNIQUE INDEX unique_active_office
ON offices (office_name)
WHERE office_deleted IS NULL;

-- Stores IT staff information for identification and logging
CREATE TABLE it_staffs (
  it_staff_id SERIAL PRIMARY KEY,
  -- The first digits before @ in the employees work e-mail adress.
  username VARCHAR(50) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  -- Soft delete, null if not deleted
  it_staff_deleted TIMESTAMPTZ,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE
);

-- The username has to be unique for users which are not soft deleted.
CREATE UNIQUE INDEX unique_active_username
ON it_staffs (username)
WHERE it_staff_deleted IS NULL;

-- Stores administrator credentials.
CREATE TABLE admins (
  admin_id SERIAL PRIMARY KEY,
  -- Argon2id hash generated and verified in Java.
  password_hash TEXT NOT NULL,
  -- Soft delete, null if not deleted
  admin_deleted TIMESTAMPTZ,
  it_staff_id INTEGER UNIQUE NOT NULL REFERENCES it_staffs (it_staff_id) ON DELETE CASCADE
);

-- Represents physical storage locations within an office.
CREATE TABLE shelves (
  shelf_id SERIAL PRIMARY KEY,
  -- Both the name and barcode key of the shelf.
  shelf_name VARCHAR(50) NOT NULL,
  -- Name of the room containing the shelf.
  room_name VARCHAR(50),
  -- Soft delete, null if not deleted
  shelf_deleted TIMESTAMPTZ,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE
);

-- The shelf name has to be unique within its office,
-- for shelves which are not soft deleted.
CREATE UNIQUE INDEX unique_active_shelf
ON shelves (office_id, shelf_name)
WHERE shelf_deleted IS NULL;

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
  -- Soft delete, null if not deleted
  -- IMPORTANT: Soft delete of descriptors, has to soft delete its barcodes.
  descriptor_deleted TIMESTAMPTZ,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE,
  -- Enables the composite foreign key in descriptor_barcodes.
  UNIQUE (descriptor_id, office_id)
);

CREATE TABLE descriptor_barcodes (
  barcode_id SERIAL PRIMARY KEY,
  barcode_key VARCHAR(50) NOT NULL,
  descriptor_id INTEGER NOT NULL,
  office_id INTEGER NOT NULL,
  barcode_deleted TIMESTAMPTZ,
  -- Ensures the descriptor belongs to the specified office.
  FOREIGN KEY (descriptor_id, office_id) REFERENCES descriptors (descriptor_id, office_id) ON DELETE CASCADE
);

-- Barcodes which are not soft deleted, has to be unique within its office
CREATE UNIQUE INDEX unique_active_barcode
ON descriptor_barcodes (office_id, barcode_key)
WHERE barcode_deleted IS NULL;

CREATE TABLE loanables (
  loanable_id SERIAL PRIMARY KEY,
  -- Barcode tag identifier used to identify an individual loanable.
  loanable_barcode VARCHAR(100) NOT NULL,
  -- "copy number" eg. laptop"3"
  unit_number INTEGER NOT NULL CONSTRAINT positive_unit_number CHECK (unit_number >= 0),
  -- The template from descriptor is filled out when adding loanables
  info_field VARCHAR(800),
  -- Soft delete, null if not deleted
  loanable_deleted TIMESTAMPTZ,
  descriptor_id INTEGER NOT NULL REFERENCES descriptors (descriptor_id) ON DELETE CASCADE,
  shelf_id INTEGER REFERENCES shelves (shelf_id) ON DELETE SET NULL
);

-- Barcodes must be unique among active loanables.
CREATE UNIQUE INDEX unique_active_loanable_barcode
ON loanables (loanable_barcode)
WHERE loanable_deleted IS NULL;

-- Unit numbers must be unique within each descriptor
-- for loanables which are not soft deleted.
CREATE UNIQUE INDEX unique_active_loanable_unit_number
ON loanables (descriptor_id, unit_number)
WHERE loanable_deleted IS NULL;

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
  -- Return date instead of soft delete, null if not deleted
  actual_return_date TIMESTAMPTZ,
  -- The agreed return date cannot be before the date borrowed
  CONSTRAINT chk_agreed_return_date CHECK (agreed_return_date > loan_time),
  loanable_id INTEGER NOT NULL REFERENCES loanables (loanable_id) ON DELETE RESTRICT
);

-- Ensures a loanable can only have one active loan agreement at a time.
CREATE UNIQUE INDEX unique_active_loan
ON loan_agreements (loanable_id)
WHERE actual_return_date IS NULL;

-- Monitors descriptor quantities against configured minimum thresholds.
CREATE TABLE stock_alerts (
  stock_alert_id SERIAL PRIMARY KEY,
  urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
  active BOOLEAN NOT NULL,
  muted BOOLEAN NOT NULL,
  min_quantity INTEGER NOT NULL CHECK (min_quantity >= 0),
  -- Soft delete, null if not deleted
  stock_alert_deleted TIMESTAMPTZ,
  descriptor_id INTEGER NOT NULL REFERENCES descriptors (descriptor_id) ON DELETE CASCADE
);

-- Represents alerts associated with loan agreements.
CREATE TABLE loan_alerts (
  loan_alert_id SERIAL PRIMARY KEY,
  urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
  active BOOLEAN NOT NULL,
  muted BOOLEAN NOT NULL,
  -- Soft delete, null if not deleted
  loan_alert_deleted TIMESTAMPTZ,
  loan_id INTEGER NOT NULL REFERENCES loan_agreements (loan_id) ON DELETE CASCADE
);

-- Monitors inactivity for an office based on the number of matching log entry types
-- within a configured time interval.
CREATE TABLE inactivity_alerts (
  inactivity_alert_id SERIAL PRIMARY KEY,
  alert_name VARCHAR(50) NOT NULL,
  log_types VARCHAR(500) NOT NULL,
  urgency VARCHAR(10) NOT NULL CONSTRAINT valid_urgency CHECK (urgency in ('INFO', 'WARNING', 'CRITICAL')),
  active BOOLEAN NOT NULL,
  muted BOOLEAN NOT NULL,
  -- Minimum number of matching log entries within the interval.
  threshold_transactions INTEGER NOT NULL CHECK (threshold_transactions >= 0),
  threshold_interval INTEGER NOT NULL CHECK (threshold_interval > 0),
  -- Soft delete, null if not deleted
  inactivity_alert_deleted TIMESTAMPTZ,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE CASCADE
);

-- Records system events for auditing and activity tracking.
-- Event details are stored as text to preserve historical information
-- and support filtered view
CREATE TABLE log_entries (
  entry_id SERIAL PRIMARY KEY,
  entry_type VARCHAR(50) NOT NULL,
  entry_time TIMESTAMPTZ NOT NULL,
  office_id INTEGER NOT NULL REFERENCES offices (office_id) ON DELETE RESTRICT,
  it_staff_id INTEGER REFERENCES it_staffs (it_staff_id) ON DELETE CASCADE,
  shelf_id INTEGER REFERENCES shelves (shelf_id) ON DELETE CASCADE,
  descriptor_id INTEGER REFERENCES descriptors (descriptor_id) ON DELETE CASCADE,
  loanable_id INTEGER REFERENCES loanables (loanable_id) ON DELETE CASCADE,
  loan_id INTEGER REFERENCES loan_agreements (loan_id) ON DELETE CASCADE,
  stock_alert_id INTEGER REFERENCES stock_alerts (stock_alert_id) ON DELETE CASCADE,
  loan_alert_id INTEGER REFERENCES loan_alerts (loan_alert_id) ON DELETE CASCADE,
  inactivity_alert_id INTEGER REFERENCES inactivity_alerts (inactivity_alert_id) ON DELETE CASCADE,
  borrower VARCHAR(50),
  info VARCHAR(500)
);