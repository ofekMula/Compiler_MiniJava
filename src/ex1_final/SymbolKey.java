package ex1_final;

public class SymbolKey {
    private final String name;
    private final SymbolType type;

    public SymbolKey(String name, SymbolType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SymbolKey)) return false;
        SymbolKey key = (SymbolKey) o;
        return name.equals(key.name) && type == key.type;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }
}
