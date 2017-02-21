package ee.ttu.idu0080.raamatupood.types;

import java.math.BigDecimal;

public enum Toode {
	MINT_TEA(1, "Mündi tee", new BigDecimal(10)),
	CEYLON_TEA(2, "Prince Wladimir", new BigDecimal(5)),
	MACHI_TEA(3, "Machi", new BigDecimal(20)),
	JAPANESE_GREEN_TEA(4, "Tenshi", new BigDecimal(15));
	
	public final int kood;
	public final String nimetus;
	public final BigDecimal hind;

	private Toode(int kood, String nimetus, BigDecimal hind) {
		this.kood = kood;
		this.nimetus = nimetus;
		this.hind = hind;
	}
	
	public String getText() {
		return nimetus + ", hind: " + hind;
	}
}
