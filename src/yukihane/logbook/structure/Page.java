package yukihane.logbook.structure;

import java.util.List;

import yukihane.logbook.entity.Listable;
import android.os.Bundle;

public interface Page<T extends Listable<T>> {
    List<T> getItems();
    Bundle getNextParam();
}
