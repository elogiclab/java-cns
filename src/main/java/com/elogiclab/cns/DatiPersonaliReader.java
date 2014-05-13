package com.elogiclab.cns;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

@SuppressWarnings("restriction")
public class DatiPersonaliReader {
	CommandAPDU SELECT_MF = new CommandAPDU(0x00, 0xA4, 0x00, 0x00, new byte[] {0x3F, 0x00});
	CommandAPDU SELECT_DF1 = new CommandAPDU(0x00, 0xA4, 0x00, 0x00, new byte[] {0x11, 0x00});
	CommandAPDU SELECT_EF_PERS = new CommandAPDU(0x00, 0xA4, 0x00, 0x00, new byte[] {0x11, 0x02});
	CommandAPDU READ_LENGTH = new CommandAPDU(0x00, 0xB0, 0x00, 0x00, new byte[] {0x00, 0x00}, 6);
	
	public CardChannel connect() throws CnsCardException {
        TerminalFactory factory = TerminalFactory.getDefault();
        Card card = null;
        CardChannel channel = null;
        try {
	        List<CardTerminal> terminals = factory.terminals().list();
	        if(terminals.size() == 0) {
	        	return null;
	        }
	        CardTerminal terminal = terminals.get(0);
	        card = terminal.connect("T=1");
	        channel = card.getBasicChannel();
	        
        } catch (CardException exc) {
        	throw new CnsCardException(exc);
        }
        return channel;
	}

	
	public boolean selectFile(CardChannel channel) throws CnsCardException  {
        try {
			ResponseAPDU r = channel.transmit(SELECT_MF);
			if(r.getSW() != 0x9000) {
				System.out.println(r);
				throw new CnsCardException("Errore selezionando la directory");
			}
			r = channel.transmit(SELECT_DF1);
			if(r.getSW() != 0x9000) {
				throw new CnsCardException("Errore selezionando la directory");
			}
			r = channel.transmit(SELECT_EF_PERS);
			if(r.getSW() != 0x9000) {
				throw new CnsCardException("Errore selezionando il file");
			}
		} catch (CardException e) {
        	throw new CnsCardException(e);
		}

		
		return true;
	}
	
	public String readRaw(CardChannel channel) throws CnsCardException {
		ResponseAPDU r;
		String data = null;
		try {
			r = channel.transmit(READ_LENGTH);
	        String s = new String(r.getData());
	        int l = Integer.parseInt(s, 16);
			CommandAPDU READ_DATA = new CommandAPDU(0x00, 0xB0, 0x00, 0x00, new byte[] {0x00, 0x00}, l);
	        r = channel.transmit(READ_DATA);
	        data = new String(r.getData());
		} catch (CardException e) {
        	throw new CnsCardException(e);
		}
		return data;

	}
	
	public List<String> toList(String rawData) {
		int pos = 6;
		final ArrayList<String> returnValue = new ArrayList<String>();
		while(pos < rawData.length()) {
			String strLen = rawData.substring(pos, pos + 2);
			pos += 2;
			int len = Integer.parseInt(strLen, 16);
			String value = rawData.substring(pos, pos + len);
			pos += len;
			returnValue.add(value);
		}
		return returnValue;
	}
	

}
