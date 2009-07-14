package com.silentmatt.dss.parser;

import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.HashMap;

class Token {
	int kind;    // token kind
	int pos;     // token position in the source text (starting at 0)
	int col;     // token column (starting at 1)
	int line;    // token line (starting at 1)
	String val;  // token value
	Token next;  // ML 2005-03-11 Peek tokens are kept in linked list
}

//-----------------------------------------------------------------------------------
// Buffer
//-----------------------------------------------------------------------------------
class Buffer {
	// This Buffer supports the following cases:
	// 1) seekable stream (file)
	//    a) whole stream in buffer
	//    b) part of stream in buffer
	// 2) non seekable stream (network, console)

	public static final int EOF = Character.MAX_VALUE + 1;
	private static final int MIN_BUFFER_LENGTH = 1024; // 1KB
	private static final int MAX_BUFFER_LENGTH = MIN_BUFFER_LENGTH * 64; // 64KB
	private byte[] buf;   // input buffer
	private int bufStart; // position of first byte in buffer relative to input stream
	private int bufLen;   // length of buffer
	private int fileLen;  // length of input stream (may change if stream is no file)
	private int bufPos;      // current position in buffer
	private RandomAccessFile file; // input stream (seekable)
	private InputStream stream; // growing input stream (e.g.: console, network)

	public Buffer(InputStream s) {
		stream = s;
		fileLen = bufLen = bufStart = bufPos = 0;
		buf = new byte[MIN_BUFFER_LENGTH];
	}

	public Buffer(String fileName) {
		try {
			file = new RandomAccessFile(fileName, "r");
			fileLen = (int) file.length();
			bufLen = Math.min(fileLen, MAX_BUFFER_LENGTH);
			buf = new byte[bufLen];
			bufStart = Integer.MAX_VALUE; // nothing in buffer so far
			if (fileLen > 0) setPos(0); // setup buffer to position 0 (start)
			else bufPos = 0; // index 0 is already after the file, thus setPos(0) is invalid
			if (bufLen == fileLen) Close();
		} catch (IOException e) {
			throw new FatalError("Could not open file " + fileName);
		}
	}

	// don't use b after this call anymore
	// called in UTF8Buffer constructor
	protected Buffer(Buffer b) {
		buf = b.buf;
		bufStart = b.bufStart;
		bufLen = b.bufLen;
		fileLen = b.fileLen;
		bufPos = b.bufPos;
		file = b.file;
		stream = b.stream;
		// keep finalize from closing the file
		b.file = null;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		Close();
	}

	protected void Close() {
		if (file != null) {
			try {
				file.close();
				file = null;
			} catch (IOException e) {
				throw new FatalError(e.getMessage());
			}
		}
	}

	public int Read() {
		if (bufPos < bufLen) {
			return buf[bufPos++] & 0xff;  // mask out sign bits
		} else if (getPos() < fileLen) {
			setPos(getPos());         // shift buffer start to pos
			return buf[bufPos++] & 0xff; // mask out sign bits
		} else if (stream != null && ReadNextStreamChunk() > 0) {
			return buf[bufPos++] & 0xff;  // mask out sign bits
		} else {
			return EOF;
		}
	}

	public int Peek() {
		int curPos = getPos();
		int ch = Read();
		setPos(curPos);
		return ch;
	}

	public String GetString(int beg, int end) {
	    int len = end - beg;
	    char[] buf = new char[len];
	    int oldPos = getPos();
	    setPos(beg);
	    for (int i = 0; i < len; ++i) buf[i] = (char) Read();
	    setPos(oldPos);
	    return new String(buf);
	}

	public int getPos() {
		return bufPos + bufStart;
	}

	public void setPos(int value) {
		if (value >= fileLen && stream != null) {
			// Wanted position is after buffer and the stream
			// is not seek-able e.g. network or console,
			// thus we have to read the stream manually till
			// the wanted position is in sight.
			while (value >= fileLen && ReadNextStreamChunk() > 0);
		}

		if (value < 0 || value > fileLen) {
			throw new FatalError("buffer out of bounds access, position: " + value);
		}

		if (value >= bufStart && value < bufStart + bufLen) { // already in buffer
			bufPos = value - bufStart;
		} else if (file != null) { // must be swapped in
			try {
				file.seek(value);
				bufLen = file.read(buf);
				bufStart = value; bufPos = 0;
			} catch(IOException e) {
				throw new FatalError(e.getMessage());
			}
		} else {
			// set the position to the end of the file, Pos will return fileLen.
			bufPos = fileLen - bufStart;
		}
	}
	
	// Read the next chunk of bytes from the stream, increases the buffer
	// if needed and updates the fields fileLen and bufLen.
	// Returns the number of bytes read.
	private int ReadNextStreamChunk() {
		int free = buf.length - bufLen;
		if (free == 0) {
			// in the case of a growing input stream
			// we can neither seek in the stream, nor can we
			// foresee the maximum length, thus we must adapt
			// the buffer size on demand.
			byte[] newBuf = new byte[bufLen * 2];
			System.arraycopy(buf, 0, newBuf, 0, bufLen);
			buf = newBuf;
			free = bufLen;
		}
		
		int read;
		try { read = stream.read(buf, bufLen, free); }
		catch (IOException ioex) { throw new FatalError(ioex.getMessage()); }
		
		if (read > 0) {
			fileLen = bufLen = (bufLen + read);
			return read;
		}
		// end of stream reached
		return 0;
	}
}

//-----------------------------------------------------------------------------------
// UTF8Buffer
//-----------------------------------------------------------------------------------
class UTF8Buffer extends Buffer {
	UTF8Buffer(Buffer b) { super(b); }

	public int Read() {
		int ch;
		do {
			ch = super.Read();
			// until we find a utf8 start (0xxxxxxx or 11xxxxxx)
		} while ((ch >= 128) && ((ch & 0xC0) != 0xC0) && (ch != EOF));
		if (ch < 128 || ch == EOF) {
			// nothing to do, first 127 chars are the same in ascii and utf8
			// 0xxxxxxx or end of file character
		} else if ((ch & 0xF0) == 0xF0) {
			// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
			int c1 = ch & 0x07; ch = super.Read();
			int c2 = ch & 0x3F; ch = super.Read();
			int c3 = ch & 0x3F; ch = super.Read();
			int c4 = ch & 0x3F;
			ch = (((((c1 << 6) | c2) << 6) | c3) << 6) | c4;
		} else if ((ch & 0xE0) == 0xE0) {
			// 1110xxxx 10xxxxxx 10xxxxxx
			int c1 = ch & 0x0F; ch = super.Read();
			int c2 = ch & 0x3F; ch = super.Read();
			int c3 = ch & 0x3F;
			ch = (((c1 << 6) | c2) << 6) | c3;
		} else if ((ch & 0xC0) == 0xC0) {
			// 110xxxxx 10xxxxxx
			int c1 = ch & 0x1F; ch = super.Read();
			int c2 = ch & 0x3F;
			ch = (c1 << 6) | c2;
		}
		return ch;
	}
}

//-----------------------------------------------------------------------------------
// StartStates  -- maps characters to start states of tokens
//-----------------------------------------------------------------------------------
class StartStates {
	private static class Elem {
		public int key, val;
		public Elem next;
		public Elem(int key, int val) { this.key = key; this.val = val; }
	}

	private Elem[] tab = new Elem[128];

	public void set(int key, int val) {
		Elem e = new Elem(key, val);
		int k = key % 128;
		e.next = tab[k]; tab[k] = e;
	}

	public int state(int key) {
		Elem e = tab[key % 128];
		while (e != null && e.key != key) e = e.next;
		return e == null ? 0: e.val;
	}
}

//-----------------------------------------------------------------------------------
// Scanner
//-----------------------------------------------------------------------------------
public class Scanner {
	static final char EOL = '\n';
	static final int  eofSym = 0;
	static final int maxT = 57;
	static final int noSym = 57;


	public Buffer buffer; // scanner buffer

	Token t;           // current token
	int ch;            // current input character
	int pos;           // byte position of current character
	int col;           // column number of current character
	int line;          // line number of current character
	int oldEols;       // EOLs that appeared in a comment;
	static final StartStates start; // maps initial token character to start state
	static final Map<String, Integer> literals;      // maps literal strings to literal kinds

	Token tokens;      // list of tokens already peeked (first token is a dummy)
	Token pt;          // current peek token
	
	char[] tval = new char[16]; // token text used in NextToken(), dynamically enlarged
	int tlen;          // length of current token


	static {
		start = new StartStates();
		literals = new HashMap<String, Integer>();
		for (int i = 65; i <= 84; ++i) start.set(i, 1);
		for (int i = 86; i <= 90; ++i) start.set(i, 1);
		for (int i = 95; i <= 95; ++i) start.set(i, 1);
		for (int i = 97; i <= 122; ++i) start.set(i, 1);
		for (int i = 10; i <= 10; ++i) start.set(i, 2);
		for (int i = 13; i <= 13; ++i) start.set(i, 2);
		for (int i = 48; i <= 57; ++i) start.set(i, 3);
		for (int i = 9; i <= 9; ++i) start.set(i, 4);
		for (int i = 11; i <= 12; ++i) start.set(i, 4);
		for (int i = 32; i <= 32; ++i) start.set(i, 4);
		start.set(60, 5); 
		start.set(45, 101); 
		start.set(39, 10); 
		start.set(34, 11); 
		start.set(40, 12); 
		start.set(41, 13); 
		start.set(64, 102); 
		start.set(123, 19); 
		start.set(125, 20); 
		start.set(59, 25); 
		start.set(44, 71); 
		start.set(43, 72); 
		start.set(62, 73); 
		start.set(126, 103); 
		start.set(42, 104); 
		start.set(35, 74); 
		start.set(46, 75); 
		start.set(91, 76); 
		start.set(61, 77); 
		start.set(124, 79); 
		start.set(36, 81); 
		start.set(94, 83); 
		start.set(93, 86); 
		start.set(58, 87); 
		start.set(33, 88); 
		start.set(47, 98); 
		start.set(85, 105); 
		start.set(37, 100); 
		start.set(Buffer.EOF, -1);
		literals.put("url", new Integer(9));
		literals.put("all", new Integer(12));
		literals.put("aural", new Integer(13));
		literals.put("braille", new Integer(14));
		literals.put("embossed", new Integer(15));
		literals.put("handheld", new Integer(16));
		literals.put("print", new Integer(17));
		literals.put("projection", new Integer(18));
		literals.put("screen", new Integer(19));
		literals.put("tty", new Integer(20));
		literals.put("tv", new Integer(21));
		literals.put("n", new Integer(22));
		literals.put("-", new Integer(55));

	}
	
	public Scanner (String fileName) {
		buffer = new Buffer(fileName);
		Init();
	}
	
	public Scanner(InputStream s) {
		buffer = new Buffer(s);
		Init();
	}
	
	void Init () {
		pos = -1; line = 1; col = 0;
		oldEols = 0;
		NextCh();
		if (ch == 0xEF) { // check optional byte order mark for UTF-8
			NextCh(); int ch1 = ch;
			NextCh(); int ch2 = ch;
			if (ch1 != 0xBB || ch2 != 0xBF) {
				throw new FatalError("Illegal byte order mark at start of file");
			}
			buffer = new UTF8Buffer(buffer); col = 0;
			NextCh();
		}
		pt = tokens = new Token();  // first token is a dummy
	}
	
	void NextCh() {
		if (oldEols > 0) { ch = EOL; oldEols--; }
		else {
			pos = buffer.getPos();
			ch = buffer.Read(); col++;
			// replace isolated '\r' by '\n' in order to make
			// eol handling uniform across Windows, Unix and Mac
			if (ch == '\r' && buffer.Peek() != '\n') ch = EOL;
			if (ch == EOL) { line++; col = 0; }
		}

	}
	
	void AddCh() {
		if (tlen >= tval.length) {
			char[] newBuf = new char[2 * tval.length];
			System.arraycopy(tval, 0, newBuf, 0, tval.length);
			tval = newBuf;
		}
		if (ch != Buffer.EOF) {
			tval[tlen++] = (char)ch; 

			NextCh();
		}

	}
	

	boolean Comment0() {
		int level = 1, pos0 = pos, line0 = line, col0 = col;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == '/') {
						level--;
						if (level == 0) { oldEols = line - line0; NextCh(); return true; }
						NextCh();
					}
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			buffer.setPos(pos0); NextCh(); line = line0; col = col0;
		}
		return false;
	}


	void CheckLiteral() {
		String val = t.val;

		Object kind = literals.get(val);
		if (kind != null) {
			t.kind = ((Integer) kind).intValue();
		}
	}

	Token NextToken() {
		while (ch == ' ' ||
			ch >= 9 && ch <= 10 || ch == 13 || ch == ' '
		) NextCh();
		if (ch == '/' && Comment0()) return NextToken();
		t = new Token();
		t.pos = pos; t.col = col; t.line = line; 
		int state = start.state(ch);
		tlen = 0; AddCh();

		loop: for (;;) {
			switch (state) {
				case -1: { t.kind = eofSym; break loop; } // NextCh already done 
				case 0: { t.kind = noSym; break loop; }   // NextCh already done
				case 1:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z') {AddCh(); state = 1; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 2:
					{t.kind = 2; break loop;}
				case 3:
					{t.kind = 3; break loop;}
				case 4:
					{t.kind = 4; break loop;}
				case 5:
					if (ch == '!') {AddCh(); state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 6:
					if (ch == '-') {AddCh(); state = 7; break;}
					else {t.kind = noSym; break loop;}
				case 7:
					if (ch == '-') {AddCh(); state = 8; break;}
					else {t.kind = noSym; break loop;}
				case 8:
					{t.kind = 5; break loop;}
				case 9:
					{t.kind = 6; break loop;}
				case 10:
					{t.kind = 7; break loop;}
				case 11:
					{t.kind = 8; break loop;}
				case 12:
					{t.kind = 10; break loop;}
				case 13:
					{t.kind = 11; break loop;}
				case 14:
					if (ch == 'e') {AddCh(); state = 15; break;}
					else {t.kind = noSym; break loop;}
				case 15:
					if (ch == 'd') {AddCh(); state = 16; break;}
					else {t.kind = noSym; break loop;}
				case 16:
					if (ch == 'i') {AddCh(); state = 17; break;}
					else {t.kind = noSym; break loop;}
				case 17:
					if (ch == 'a') {AddCh(); state = 18; break;}
					else {t.kind = noSym; break loop;}
				case 18:
					{t.kind = 23; break loop;}
				case 19:
					{t.kind = 24; break loop;}
				case 20:
					{t.kind = 25; break loop;}
				case 21:
					if (ch == 'a') {AddCh(); state = 22; break;}
					else {t.kind = noSym; break loop;}
				case 22:
					if (ch == 's') {AddCh(); state = 23; break;}
					else {t.kind = noSym; break loop;}
				case 23:
					if (ch == 's') {AddCh(); state = 24; break;}
					else {t.kind = noSym; break loop;}
				case 24:
					{t.kind = 26; break loop;}
				case 25:
					{t.kind = 27; break loop;}
				case 26:
					if (ch == 'e') {AddCh(); state = 27; break;}
					else {t.kind = noSym; break loop;}
				case 27:
					if (ch == 'f') {AddCh(); state = 28; break;}
					else {t.kind = noSym; break loop;}
				case 28:
					if (ch == 'i') {AddCh(); state = 29; break;}
					else {t.kind = noSym; break loop;}
				case 29:
					if (ch == 'n') {AddCh(); state = 30; break;}
					else {t.kind = noSym; break loop;}
				case 30:
					if (ch == 'e') {AddCh(); state = 31; break;}
					else {t.kind = noSym; break loop;}
				case 31:
					{t.kind = 28; break loop;}
				case 32:
					if (ch == 'o') {AddCh(); state = 33; break;}
					else {t.kind = noSym; break loop;}
				case 33:
					if (ch == 'n') {AddCh(); state = 34; break;}
					else {t.kind = noSym; break loop;}
				case 34:
					if (ch == 't') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 35:
					if (ch == '-') {AddCh(); state = 36; break;}
					else {t.kind = noSym; break loop;}
				case 36:
					if (ch == 'f') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 37:
					if (ch == 'a') {AddCh(); state = 38; break;}
					else {t.kind = noSym; break loop;}
				case 38:
					if (ch == 'c') {AddCh(); state = 39; break;}
					else {t.kind = noSym; break loop;}
				case 39:
					if (ch == 'e') {AddCh(); state = 40; break;}
					else {t.kind = noSym; break loop;}
				case 40:
					{t.kind = 29; break loop;}
				case 41:
					if (ch == 'a') {AddCh(); state = 42; break;}
					else {t.kind = noSym; break loop;}
				case 42:
					if (ch == 'g') {AddCh(); state = 43; break;}
					else {t.kind = noSym; break loop;}
				case 43:
					if (ch == 'e') {AddCh(); state = 44; break;}
					else {t.kind = noSym; break loop;}
				case 44:
					{t.kind = 30; break loop;}
				case 45:
					if (ch == 'p') {AddCh(); state = 46; break;}
					else {t.kind = noSym; break loop;}
				case 46:
					if (ch == 'o') {AddCh(); state = 47; break;}
					else {t.kind = noSym; break loop;}
				case 47:
					if (ch == 'r') {AddCh(); state = 48; break;}
					else {t.kind = noSym; break loop;}
				case 48:
					if (ch == 't') {AddCh(); state = 49; break;}
					else {t.kind = noSym; break loop;}
				case 49:
					{t.kind = 31; break loop;}
				case 50:
					if (ch == 'c') {AddCh(); state = 51; break;}
					else {t.kind = noSym; break loop;}
				case 51:
					if (ch == 'l') {AddCh(); state = 52; break;}
					else {t.kind = noSym; break loop;}
				case 52:
					if (ch == 'u') {AddCh(); state = 53; break;}
					else {t.kind = noSym; break loop;}
				case 53:
					if (ch == 'd') {AddCh(); state = 54; break;}
					else {t.kind = noSym; break loop;}
				case 54:
					if (ch == 'e') {AddCh(); state = 55; break;}
					else {t.kind = noSym; break loop;}
				case 55:
					{t.kind = 32; break loop;}
				case 56:
					if (ch == 'a') {AddCh(); state = 57; break;}
					else {t.kind = noSym; break loop;}
				case 57:
					if (ch == 'r') {AddCh(); state = 58; break;}
					else {t.kind = noSym; break loop;}
				case 58:
					if (ch == 's') {AddCh(); state = 59; break;}
					else {t.kind = noSym; break loop;}
				case 59:
					if (ch == 'e') {AddCh(); state = 60; break;}
					else {t.kind = noSym; break loop;}
				case 60:
					if (ch == 't') {AddCh(); state = 61; break;}
					else {t.kind = noSym; break loop;}
				case 61:
					{t.kind = 33; break loop;}
				case 62:
					if (ch == 'a') {AddCh(); state = 63; break;}
					else {t.kind = noSym; break loop;}
				case 63:
					if (ch == 'm') {AddCh(); state = 64; break;}
					else {t.kind = noSym; break loop;}
				case 64:
					if (ch == 'e') {AddCh(); state = 65; break;}
					else {t.kind = noSym; break loop;}
				case 65:
					if (ch == 's') {AddCh(); state = 66; break;}
					else {t.kind = noSym; break loop;}
				case 66:
					if (ch == 'p') {AddCh(); state = 67; break;}
					else {t.kind = noSym; break loop;}
				case 67:
					if (ch == 'a') {AddCh(); state = 68; break;}
					else {t.kind = noSym; break loop;}
				case 68:
					if (ch == 'c') {AddCh(); state = 69; break;}
					else {t.kind = noSym; break loop;}
				case 69:
					if (ch == 'e') {AddCh(); state = 70; break;}
					else {t.kind = noSym; break loop;}
				case 70:
					{t.kind = 34; break loop;}
				case 71:
					{t.kind = 36; break loop;}
				case 72:
					{t.kind = 37; break loop;}
				case 73:
					{t.kind = 38; break loop;}
				case 74:
					{t.kind = 41; break loop;}
				case 75:
					{t.kind = 42; break loop;}
				case 76:
					{t.kind = 43; break loop;}
				case 77:
					{t.kind = 44; break loop;}
				case 78:
					{t.kind = 45; break loop;}
				case 79:
					if (ch == '=') {AddCh(); state = 80; break;}
					else {t.kind = noSym; break loop;}
				case 80:
					{t.kind = 46; break loop;}
				case 81:
					if (ch == '=') {AddCh(); state = 82; break;}
					else {t.kind = noSym; break loop;}
				case 82:
					{t.kind = 47; break loop;}
				case 83:
					if (ch == '=') {AddCh(); state = 84; break;}
					else {t.kind = noSym; break loop;}
				case 84:
					{t.kind = 48; break loop;}
				case 85:
					{t.kind = 49; break loop;}
				case 86:
					{t.kind = 50; break loop;}
				case 87:
					{t.kind = 51; break loop;}
				case 88:
					if (ch == 'i') {AddCh(); state = 89; break;}
					else {t.kind = noSym; break loop;}
				case 89:
					if (ch == 'm') {AddCh(); state = 90; break;}
					else {t.kind = noSym; break loop;}
				case 90:
					if (ch == 'p') {AddCh(); state = 91; break;}
					else {t.kind = noSym; break loop;}
				case 91:
					if (ch == 'o') {AddCh(); state = 92; break;}
					else {t.kind = noSym; break loop;}
				case 92:
					if (ch == 'r') {AddCh(); state = 93; break;}
					else {t.kind = noSym; break loop;}
				case 93:
					if (ch == 't') {AddCh(); state = 94; break;}
					else {t.kind = noSym; break loop;}
				case 94:
					if (ch == 'a') {AddCh(); state = 95; break;}
					else {t.kind = noSym; break loop;}
				case 95:
					if (ch == 'n') {AddCh(); state = 96; break;}
					else {t.kind = noSym; break loop;}
				case 96:
					if (ch == 't') {AddCh(); state = 97; break;}
					else {t.kind = noSym; break loop;}
				case 97:
					{t.kind = 52; break loop;}
				case 98:
					{t.kind = 53; break loop;}
				case 99:
					{t.kind = 54; break loop;}
				case 100:
					{t.kind = 56; break loop;}
				case 101:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z') {AddCh(); state = 1; break;}
					else if (ch == '-') {AddCh(); state = 106; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 102:
					if (ch == 'm') {AddCh(); state = 14; break;}
					else if (ch == 'c') {AddCh(); state = 107; break;}
					else if (ch == 'd') {AddCh(); state = 26; break;}
					else if (ch == 'f') {AddCh(); state = 32; break;}
					else if (ch == 'p') {AddCh(); state = 41; break;}
					else if (ch == 'i') {AddCh(); state = 108; break;}
					else if (ch == 'n') {AddCh(); state = 62; break;}
					else {t.kind = 35; break loop;}
				case 103:
					if (ch == '=') {AddCh(); state = 78; break;}
					else {t.kind = 39; break loop;}
				case 104:
					if (ch == '=') {AddCh(); state = 85; break;}
					else {t.kind = 40; break loop;}
				case 105:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z') {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 99; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 106:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z') {AddCh(); state = 1; break;}
					else if (ch == '>') {AddCh(); state = 9; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 107:
					if (ch == 'l') {AddCh(); state = 21; break;}
					else if (ch == 'h') {AddCh(); state = 56; break;}
					else {t.kind = noSym; break loop;}
				case 108:
					if (ch == 'm') {AddCh(); state = 45; break;}
					else if (ch == 'n') {AddCh(); state = 50; break;}
					else {t.kind = noSym; break loop;}

			}
		}
		t.val = new String(tval, 0, tlen);
		return t;
	}
	
	// get the next token (possibly a token already seen during peeking)
	public Token Scan () {
		if (tokens.next == null) {
			return NextToken();
		} else {
			pt = tokens = tokens.next;
			return tokens;
		}
	}

	// get the next token, ignore pragmas
	public Token Peek () {
		do {
			if (pt.next == null) {
				pt.next = NextToken();
			}
			pt = pt.next;
		} while (pt.kind > maxT); // skip pragmas

		return pt;
	}

	// make sure that peeking starts at current scan position
	public void ResetPeek () { pt = tokens; }

} // end Scanner

