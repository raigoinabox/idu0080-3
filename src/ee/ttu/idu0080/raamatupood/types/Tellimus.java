package ee.ttu.idu0080.raamatupood.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public final class Tellimus implements Serializable, Iterable<TellimuseRida> {
	
	private final List<TellimuseRida> rows = new ArrayList<TellimuseRida>();
	
	public void add(TellimuseRida rida) {
		rows.add(rida);
	}

	@Override
	public Iterator<TellimuseRida> iterator() {
		return rows.iterator();
	}
	
	public String getText() {
		String result = "Tellimus [";
		for (TellimuseRida row : rows) {
			result += row.getText() + "; ";
		}
		result += "]";
		
		return result;
	}
}
