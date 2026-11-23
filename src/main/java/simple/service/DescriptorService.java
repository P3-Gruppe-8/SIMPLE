package simple.service;

import java.util.Optional;
import java.sql.*;

import simple.dataaccess.jdbc.JdbcDescriptorRepository;
import simple.dataaccess.config.Databaseconnection;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.exception.databaseUpdateException;

public class DescriptorService {
    public void writeOff(String barcode, int office_id, int change) {
        change = -change;
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        boolean result = jdbc.changeQuantity(conn, barcode, office_id, change);

        if (result) {
            db.executeCommit(conn);
        } else {
            db.executeRollback(conn);
        }

        db.closeConnection(conn);
    }

    public void deregister(String barcode, int office_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        Optional<Descriptor> opt_descriptor = jdbc.findByBarcode(conn, barcode, office_id);

        if (opt_descriptor.isPresent()) {
            Descriptor descriptor = opt_descriptor.get();
            boolean result = jdbc.softDelete(conn, descriptor.id(), descriptor.office_id());

            if (!result) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new databaseUpdateException("Failed to delete descriptor: 0 rows affected");
            }
        } else {
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new databaseUpdateException("Failed to delete descriptor: Unable to find descriptor via barcode");
        }
        db.executeCommit(conn);
        db.closeConnection(conn);
    }

    public void register(String descriptor_name, boolean is_loanable, String info_field, int office_id,
            String barcode) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();

        int descriptor_id = jdbc.createNew(conn, descriptor_name, is_loanable, info_field, office_id);
        boolean result = jdbc.addBarcode(conn, descriptor_id, barcode, office_id);

        if (!result) {
            db.executeRollback(conn);
            throw new databaseUpdateException("Failed to delete descriptor: 0 rows affected");
        }
        db.executeCommit(conn);
        db.closeConnection(conn);
    }

    public void restock(String barcode, int office_id, int change) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        boolean result = jdbc.changeQuantity(conn, barcode, office_id, change);

        if (result) {
            db.executeCommit(conn);
        } else {
            db.executeRollback(conn);
        }

        db.closeConnection(conn);
    }

    public void getItemDetails(String barcode) {
    }

    public void removeShelf(String barcode, int shelf_id) {
    }

    public void addShelf(String barcode, int shelf_id) {
    }
}
