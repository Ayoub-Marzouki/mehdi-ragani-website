package art.mehdiragani.mehdiragani.auth.models.enums;

public enum UserRole {
    ADMIN("Admin"),
    CUSTOMER("Customer"); 

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}
