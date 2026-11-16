-- Fictional development data. Do not use as production seed data.
-- Loan dates and alert states reflect the time this migration runs.

DO $$
DECLARE
    aalborg_id INTEGER;
    aarhus_id INTEGER;

    aalborg_supply_a INTEGER;
    aalborg_supply_b INTEGER;
    aalborg_loan_shelf INTEGER;
    aarhus_supply INTEGER;

    cable_id INTEGER;
    toner_id INTEGER;
    battery_id INTEGER;
    laptop_id INTEGER;
    aarhus_cable_id INTEGER;

    borrowed_laptop_id INTEGER;
    overdue_laptop_id INTEGER;
    current_loan_id INTEGER;
    overdue_loan_id INTEGER;
BEGIN
    -- Offices
    INSERT INTO offices (office_address, office_name)
    VALUES ('Test address 1, Aalborg', 'Aalborg')
    RETURNING office_id INTO aalborg_id;

    INSERT INTO offices (office_address, office_name)
    VALUES ('Test address 2, Aarhus', 'Aarhus')
    RETURNING office_id INTO aarhus_id;

    -- IT staff
    INSERT INTO it_staffs
        (username, first_name, last_name, office_id)
    VALUES
        ('demo.anna', 'Anna', 'Nielsen', aalborg_id),
        ('demo.peter', 'Peter', 'Hansen', aalborg_id),
        ('demo.sofie', 'Sofie', 'Larsen', aarhus_id);

    -- Shelf names also serve as their barcode identifiers.
    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('AAL-SUP-01', 'IT storage', aalborg_id)
    RETURNING shelf_id INTO aalborg_supply_a;

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('AAL-SUP-02', 'IT storage', aalborg_id)
    RETURNING shelf_id INTO aalborg_supply_b;

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('AAL-LOAN-01', 'IT storage', aalborg_id)
    RETURNING shelf_id INTO aalborg_loan_shelf;

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('AAR-SUP-01', 'IT storage', aarhus_id)
    RETURNING shelf_id INTO aarhus_supply;

    -- Consumable quantities are totals across all their locations.
    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity, info_field, office_id)
    VALUES
        ('USB-C cable', FALSE, 24, 'USB-C to USB-C, 1 metre', aalborg_id)
    RETURNING descriptor_id INTO cable_id;

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity, info_field, office_id)
    VALUES
        ('Black toner', FALSE, 2, 'Black toner cartridge', aalborg_id)
    RETURNING descriptor_id INTO toner_id;

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity, info_field, office_id)
    VALUES
        ('AA batteries', FALSE, 0, 'Pack of 4 AA batteries', aalborg_id)
    RETURNING descriptor_id INTO battery_id;

    -- Loanable quantities are derived from individual units.
    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity,
         info_field, info_template, office_id)
    VALUES
        ('Loan laptop', TRUE, NULL,
         '14-inch laptop with charger', 'Serial number:', aalborg_id)
    RETURNING descriptor_id INTO laptop_id;

    -- The same product is tracked independently at another office.
    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity, info_field, office_id)
    VALUES
        ('USB-C cable', FALSE, 12, 'USB-C to USB-C, 1 metre', aarhus_id)
    RETURNING descriptor_id INTO aarhus_cable_id;

    -- Synthetic barcode values for scanner testing.
    -- The same product barcode can exist at different offices.
    INSERT INTO descriptor_barcodes
        (barcode_key, descriptor_id, office_id)
    VALUES
        ('DEMO-USB-C-1M', cable_id, aalborg_id),
        ('DEMO-TONER-BK', toner_id, aalborg_id),
        ('DEMO-AA-4PK', battery_id, aalborg_id),
        ('DEMO-USB-C-1M', aarhus_cable_id, aarhus_id);

    -- Possible storage locations
    INSERT INTO descriptor_shelves (descriptor_id, shelf_id)
    VALUES
        (cable_id, aalborg_supply_a),
        (cable_id, aalborg_supply_b),
        (toner_id, aalborg_supply_a),
        (battery_id, aalborg_supply_b),
        (laptop_id, aalborg_loan_shelf),
        (aarhus_cable_id, aarhus_supply);

    -- Available laptop
    INSERT INTO loanables
        (loanable_RFID, unit_number, info_field, descriptor_id, shelf_id)
    VALUES
        ('DEMO-RFID-LAPTOP-001', 1, 'Serial number: DEMO-L001',
         laptop_id, aalborg_loan_shelf);

    -- Borrowed laptops have no current shelf location.
    INSERT INTO loanables
        (loanable_RFID, unit_number, info_field, descriptor_id, shelf_id)
    VALUES
        ('DEMO-RFID-LAPTOP-002', 2, 'Serial number: DEMO-L002',
         laptop_id, NULL)
    RETURNING loanable_id INTO borrowed_laptop_id;

    INSERT INTO loanables
        (loanable_RFID, unit_number, info_field, descriptor_id, shelf_id)
    VALUES
        ('DEMO-RFID-LAPTOP-003', 3, 'Serial number: DEMO-L003',
         laptop_id, NULL)
    RETURNING loanable_id INTO overdue_laptop_id;

    -- Current loan
    INSERT INTO loan_agreements
        (loan_time, agreed_return_date, borrower, note, loanable_id)
    VALUES
        (CURRENT_TIMESTAMP - INTERVAL '2 days',
         CURRENT_TIMESTAMP + INTERVAL '5 days',
         'Demo Borrower A', 'Temporary replacement laptop.',
         borrowed_laptop_id)
    RETURNING loan_id INTO current_loan_id;

    -- Overdue loan
    INSERT INTO loan_agreements
        (loan_time, agreed_return_date, borrower, note, loanable_id)
    VALUES
        (CURRENT_TIMESTAMP - INTERVAL '10 days',
         CURRENT_TIMESTAMP - INTERVAL '3 days',
         'Demo Borrower B', 'Laptop for a business trip.',
         overdue_laptop_id)
    RETURNING loan_id INTO overdue_loan_id;

    -- Stock alerts match the seeded quantities.
    INSERT INTO stock_alerts
        (urgency, active, muted, min_quantity, descriptor_id)
    VALUES
        ('WARNING', FALSE, FALSE, 10, cable_id),
        ('WARNING', TRUE, FALSE, 5, toner_id),
        ('CRITICAL', TRUE, FALSE, 4, battery_id),
        ('WARNING', FALSE, FALSE, 5, aarhus_cable_id);

    INSERT INTO loan_alerts (urgency, active, muted, loan_id)
    VALUES
        ('WARNING', FALSE, FALSE, current_loan_id),
        ('WARNING', TRUE, FALSE, overdue_loan_id);

    -- Example log entries corresponding to the seeded loans.
    INSERT INTO log_entries
        (entry_type, entry_time, descriptor, loanable,
         actor, borrower, info, office_id)
    VALUES
        ('BORROW', CURRENT_TIMESTAMP - INTERVAL '2 days',
         'Loan laptop', 'DEMO-RFID-LAPTOP-002',
         'demo.anna', 'Demo Borrower A',
         'Temporary replacement laptop.', aalborg_id),
        ('BORROW', CURRENT_TIMESTAMP - INTERVAL '10 days',
         'Loan laptop', 'DEMO-RFID-LAPTOP-003',
         'demo.peter', 'Demo Borrower B',
         'Laptop for a business trip.', aalborg_id);
END;
$$;