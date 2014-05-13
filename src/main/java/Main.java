import java.util.List;

import javax.smartcardio.*;

import com.elogiclab.cns.CnsCardException;
import com.elogiclab.cns.DatiPersonaliReader;


public class Main {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws CnsCardException {
		DatiPersonaliReader r = new DatiPersonaliReader();
		CardChannel channel = r.connect();
		r.selectFile(channel);
		String raw = r.readRaw(channel);
		List<String> value = r.toList(raw);
		System.out.println(value);
	}

}
