
-- Fictional development data. Do not use as production seed data.
-- Intended for an empty database with the current schema.
-- Loan dates and alert states reflect the time this seed runs.

DO $$
DECLARE
    -- Offices
    aalborg_id INTEGER;
    aarhus_id INTEGER;

    -- IT staff
    anna_id INTEGER;
    peter_id INTEGER;

    -- Shelves
    aalborg_supply_a INTEGER;
    aalborg_supply_b INTEGER;
    aalborg_loan_shelf INTEGER;
    aarhus_supply INTEGER;

    -- Descriptors
    cable_id INTEGER;
    toner_id INTEGER;
    battery_id INTEGER;
    laptop_id INTEGER;
    aarhus_cable_id INTEGER;

    -- Loanables
    borrowed_laptop_id INTEGER;
    overdue_laptop_id INTEGER;

    -- Loan agreements
    current_loan_id INTEGER;
    overdue_loan_id INTEGER;

BEGIN
    -- =========================================
    -- Offices
    -- =========================================

    INSERT INTO offices (office_address, office_name)
    VALUES ('Test address 1, Aalborg', 'Aalborg')
    RETURNING office_id INTO aalborg_id;

    INSERT INTO offices (office_address, office_name)
    VALUES ('Test address 2, Aarhus', 'Aarhus')
    RETURNING office_id INTO aarhus_id;


    -- =========================================
    -- IT staff
    -- =========================================

    INSERT INTO it_staffs
        (username, first_name, last_name, office_id)
    VALUES
        ('anna', 'Anna', 'Nielsen', aalborg_id)
    RETURNING it_staff_id INTO anna_id;

    INSERT INTO it_staffs
        (username, first_name, last_name, office_id)
    VALUES
        ('peter', 'Peter', 'Hansen', aalborg_id)
    RETURNING it_staff_id INTO peter_id;

    INSERT INTO it_staffs
        (username, first_name, last_name, office_id)
    VALUES
        ('sofie', 'Sofie', 'Larsen', aarhus_id);


    -- =========================================
    -- Shelves
    -- =========================================

    -- Shelf names also serve as barcode identifiers.

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('supply 1', 'IT storage', aalborg_id)
    RETURNING shelf_id INTO aalborg_supply_a;

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('supply 2', 'IT storage', aalborg_id)
    RETURNING shelf_id INTO aalborg_supply_b;

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('loan 1', 'IT storage', aalborg_id)
    RETURNING shelf_id INTO aalborg_loan_shelf;

    INSERT INTO shelves (shelf_name, room_name, office_id)
    VALUES ('supply 1', 'IT storage', aarhus_id)
    RETURNING shelf_id INTO aarhus_supply;


    -- =========================================
    -- Descriptors
    -- =========================================

    -- Consumable quantities are totals across
    -- all their possible storage locations.

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity,
         descriptor_info_field, office_id)
    VALUES
        ('USB-C cable', FALSE, 24,
         'USB-C to USB-C, 1 metre', aalborg_id)
    RETURNING descriptor_id INTO cable_id;

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity,
         descriptor_info_field, office_id)
    VALUES
        ('Black toner', FALSE, 2,
         'Black toner cartridge', aalborg_id)
    RETURNING descriptor_id INTO toner_id;

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity,
         descriptor_info_field, office_id)
    VALUES
        ('AA batteries', FALSE, 0,
         'Pack of 4 AA batteries', aalborg_id)
    RETURNING descriptor_id INTO battery_id;

    -- Loanable quantities are derived from
    -- individual units instead of being stored.

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity,
         descriptor_info_field, info_template, office_id)
    VALUES
        ('Loan laptop', TRUE, NULL,
         '14-inch laptop with charger',
         'Serial number:', aalborg_id)
    RETURNING descriptor_id INTO laptop_id;

    -- The same product is independently tracked
    -- at another office.

    INSERT INTO descriptors
        (descriptor_name, is_loanable, quantity,
         descriptor_info_field, office_id)
    VALUES
        ('USB-C cable', FALSE, 12,
         'USB-C to USB-C, 1 metre', aarhus_id)
    RETURNING descriptor_id INTO aarhus_cable_id;


    -- =========================================
    -- Descriptor barcodes
    -- =========================================

    -- Synthetic barcode values for scanner testing.
    -- The same product barcode may exist at
    -- different offices.

    INSERT INTO descriptor_barcodes
        (barcode_key, descriptor_id, office_id)
    VALUES
        ('2001000000012', cable_id, aalborg_id),
        ('2001000000029', toner_id, aalborg_id),
        ('2001000000036', battery_id, aalborg_id),
        ('2001000000012', aarhus_cable_id, aarhus_id);


    -- =========================================
    -- Descriptor shelves
    -- =========================================

    -- Defines possible storage locations.

    INSERT INTO descriptor_shelves
        (descriptor_id, shelf_id)
    VALUES
        (cable_id, aalborg_supply_a),
        (cable_id, aalborg_supply_b),
        (toner_id, aalborg_supply_a),
        (battery_id, aalborg_supply_b),
        (laptop_id, aalborg_loan_shelf),
        (aarhus_cable_id, aarhus_supply);


    -- =========================================
    -- Loanables
    -- =========================================

    -- Available laptop stored on a shelf.

    INSERT INTO loanables
        (loanable_barcode, unit_number, loanable_info_field, descriptor_id, shelf_id)
    VALUES
        ('04a3f21a6b5c80', 1,
         'Serial number: DEMO-L001',
         laptop_id, aalborg_loan_shelf);

    -- Borrowed laptops have no current shelf.

    INSERT INTO loanables
        (loanable_barcode, unit_number, loanable_info_field, descriptor_id, shelf_id)
    VALUES
        ('04b71e9a2c4d80', 2,
         'Serial number: DEMO-L002',
         laptop_id, NULL)
    RETURNING loanable_id INTO borrowed_laptop_id;

    INSERT INTO loanables
        (loanable_barcode, unit_number, loanable_info_field, descriptor_id, shelf_id)
    VALUES
        ('04c58b3e7f1a80', 3,
         'Serial number: DEMO-L003',
         laptop_id, NULL)
    RETURNING loanable_id INTO overdue_laptop_id;


    -- =========================================
    -- Loan agreements
    -- =========================================

    -- Current loan: borrowed two days ago,
    -- expected to return in five days.

    INSERT INTO loan_agreements
        (loan_time, agreed_return_date,
         borrower, note, loanable_id)
    VALUES
        (CURRENT_TIMESTAMP - INTERVAL '2 days',
         CURRENT_TIMESTAMP + INTERVAL '5 days',
         'Demo Borrower A',
         'Temporary replacement laptop.',
         borrowed_laptop_id)
    RETURNING loan_id INTO current_loan_id;

    -- Overdue loan: borrowed ten days ago,
    -- expected to return three days ago.

    INSERT INTO loan_agreements
        (loan_time, agreed_return_date,
         borrower, note, loanable_id)
    VALUES
        (CURRENT_TIMESTAMP - INTERVAL '10 days',
         CURRENT_TIMESTAMP - INTERVAL '3 days',
         'Demo Borrower B',
         'Laptop for a business trip.',
         overdue_laptop_id)
    RETURNING loan_id INTO overdue_loan_id;


    -- =========================================
    -- Stock alerts
    -- =========================================

    -- Alert states correspond to seeded quantities.

    INSERT INTO stock_alerts
        (urgency, raised, muted,
         min_quantity, descriptor_id)
    VALUES
        ('WARNING', NULL, FALSE, 10, cable_id),
        ('WARNING', CURRENT_TIMESTAMP, FALSE, 5, toner_id),
        ('CRITICAL', CURRENT_TIMESTAMP, FALSE, 4, battery_id),
        ('WARNING', NULL, FALSE, 5, aarhus_cable_id);


    -- =========================================
    -- Loan alerts
    -- =========================================

    -- Current loan is not overdue.
    -- Overdue loan has a raised warning.

    INSERT INTO loan_alerts
        (urgency, raised, muted, loan_id)
    VALUES
        ('WARNING', NULL, FALSE, current_loan_id),
        ('WARNING', CURRENT_TIMESTAMP - INTERVAL '3 days', FALSE, overdue_loan_id);


    -- =========================================
    -- Log entries
    -- =========================================

    -- Log entries now reference actual database
    -- entities through foreign keys.

    INSERT INTO log_entries
        (entry_type, entry_time,
         office_id, it_staff_id,
         descriptor_id, loanable_id, loan_id,
         borrower, info)
    VALUES
        (
            'BORROW',
            CURRENT_TIMESTAMP - INTERVAL '2 days',
            aalborg_id,
            anna_id,
            laptop_id,
            borrowed_laptop_id,
            current_loan_id,
            'Demo Borrower A',
            'Temporary replacement laptop.'
        ),
        (
            'BORROW',
            CURRENT_TIMESTAMP - INTERVAL '10 days',
            aalborg_id,
            peter_id,
            laptop_id,
            overdue_laptop_id,
            overdue_loan_id,
            'Demo Borrower B',
            'Laptop for a business trip.'
        );

END;
$$;
