package net.vaemendis.hccd;

public enum FalseValue {
    NONE(null, "[disabled]"),
    EMPTY("", "[empty]"),
    DASH("-", "-");

    public final String value;
    public final String label;

    FalseValue(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
