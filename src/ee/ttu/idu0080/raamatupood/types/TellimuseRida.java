package ee.ttu.idu0080.raamatupood.types;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class TellimuseRida implements Serializable {

	public final Toode toode;
	public final long kogus;
	
	public TellimuseRida(Toode toode, long kogus) {
		this.toode = toode;
		this.kogus = kogus;
	}
	
	public String getText() {
		return toode.getText() + ", kogus: " + kogus;
	}
}
