package physics;

public abstract class Geometry {
    public static final String ID_SEPARATOR=" ";
    public int indexes[];
    public String id;
    
    public Geometry(int i[]) throws ArrayLengthException {
        if (i.length < 2) {
            throw new ArrayLengthException("Array length is less than "+finalArrayMinLength()+".");
        }
        indexes = i;
        setId();
    }
    
    protected final void setId() {
        this.id = generateId();
    }
    
    protected final int finalArrayMinLength() {
        return arrayMinLength();
    }

    public String getId() {
        return id;
    }
    
    abstract protected int arrayMinLength();
    
    abstract protected String generateId();
}
