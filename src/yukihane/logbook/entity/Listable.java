package yukihane.logbook.entity;

public interface Listable<T> extends Comparable<T> {

    String getHeader();

    String getBody();
}
