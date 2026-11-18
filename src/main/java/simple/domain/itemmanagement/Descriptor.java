package simple.domain.itemmanagement;

public record Descriptor(int id, String name, boolean is_loanable, int quantity, String info_field,
        String info_template, int office_id) {
}
