package com.neotys.rte.TerminalEmulator.ch;

import java.util.function.Function;

import com.google.common.base.Ascii;

public final class SpecialKeysConverter implements Function<String, SpecialKeys> {
	public static final Function<String, SpecialKeys> INSTANCE = new SpecialKeysConverter();

	public static final String CR="CR";
	public static final String ESC="ESC";
	public static final String DEL="DEL";
	public static final String BS="BS";
	public static final String HT="HT";
	public static final String LF="LF";
	public static final String VT="VT";
	public static final String CTRLA="CTRLA";
	public static final String CTRLB="CTRLB";
	public static final String CTRLC="CTRLC";
	public static final String CTRLD="CTRLD";
	public static final String CTRLE="CTRLE";
	public static final String CTRLF="CTRLF";
	public static final String CTRLG="CTRLG";
	public static final String CTRLH="CTRLH";
	public static final String CTRLI="CTRLI";
	public static final String CTRLJ="CTRLJ";
	public static final String CTRLK="CTRLK";
	public static final String CTRLL="CTRLL";
	public static final String CTRLM="CTRLM";
	public static final String CTRLN="CTRLN";
	public static final String CTRLO="CTRLO";
	public static final String CTRLP="CTRLP";
	public static final String CTRLQ="CTRLQ";
	public static final String CTRLR="CTRLR";
	public static final String CTRLS="CTRLS";
	public static final String CTRLT="CTRLT";
	public static final String CTRLU="CTRLU";
	public static final String CTRLV="CTRLV";
	public static final String CTRLW="CTRLW";
	public static final String CTRLX="CTRLX";
	public static final String CTRLY="CTRLY";
	public static final String CTRLZ="CTRLZ";
	public static final String UP="UP";
	public static final String DOWN="DOWN";
	public static final String LEFT="LEFT";
	public static final String RIGHT="RIGHT";

	public static String[] KEYS = {CR,ESC,DEL,BS,HT,LF,VT,CTRLA,CTRLB,CTRLC,CTRLD,CTRLE,CTRLF,CTRLG,CTRLH,CTRLI,CTRLJ,CTRLH,CTRLM,CTRLN,CTRLO,CTRLP,CTRLQ,CTRLR,CTRLS,CTRLT,CTRLU,CTRLV,CTRLW,CTRLX,CTRLY,CTRLZ,UP,DOWN,LEFT,RIGHT};

	public SpecialKeysConverter() {
	}

	@Override
	public SpecialKeys apply(final String specialKeys) {
		switch (specialKeys.toUpperCase())
		{
		case CR:
			return  SpecialKeys.of(Ascii.CR);
		case ESC:
			return  SpecialKeys.of(Ascii.ESC);
		case VT:
			return  SpecialKeys.of(Ascii.VT);
		case BS:
			return  SpecialKeys.of(Ascii.BS);
		case HT:
			return  SpecialKeys.of(Ascii.HT);
		case DEL:
			return  SpecialKeys.of(Ascii.DEL);
		case LF:
			return  SpecialKeys.of(Ascii.LF);
		case CTRLA:
			return  SpecialKeys.of(Ascii.SOH);
		case CTRLB:
			return  SpecialKeys.of(Ascii.STX);
		case CTRLC:
			return  SpecialKeys.of(Ascii.ETX);
		case CTRLD:
			return  SpecialKeys.of(Ascii.EOT);
		case CTRLE:
			return  SpecialKeys.of(Ascii.ENQ);
		case CTRLF:
			return  SpecialKeys.of(Ascii.ACK);
		case CTRLG:
			return  SpecialKeys.of(Ascii.BEL);
		case CTRLH:
			return  SpecialKeys.of(Ascii.BS);
		case CTRLI:
			return  SpecialKeys.of(Ascii.HT);
		case CTRLJ:
			return  SpecialKeys.of(Ascii.LF);
		case CTRLK:
			return  SpecialKeys.of(Ascii.VT);
		case CTRLL:
			return  SpecialKeys.of(Ascii.FF);
		case CTRLM:
			return  SpecialKeys.of(Ascii.CR);
		case CTRLN:
			return  SpecialKeys.of(Ascii.SO);
		case CTRLO:
			return  SpecialKeys.of(Ascii.SI);
		case CTRLP:
			return  SpecialKeys.of(Ascii.DLE);
		case CTRLQ:
			return  SpecialKeys.of(Ascii.DC1);
		case CTRLR:
			return  SpecialKeys.of(Ascii.DC2);
		case CTRLS:
			return  SpecialKeys.of(Ascii.DC3);
		case CTRLT:
			return  SpecialKeys.of(Ascii.DC4);
		case CTRLU:
			return  SpecialKeys.of(Ascii.NAK);
		case CTRLV:
			return  SpecialKeys.of(Ascii.SYN);
		case CTRLW:
			return  SpecialKeys.of(Ascii.ETB);
		case CTRLX:
			return  SpecialKeys.of(Ascii.CAN);
		case CTRLY:
			return  SpecialKeys.of(Ascii.EM);
		case CTRLZ:
			return  SpecialKeys.of(Ascii.SUB);
		default:
			switch (specialKeys.toUpperCase())
			{
			case UP:
				return SpecialKeys.of(Ascii.ESC, "[A".getBytes());
			case DOWN:
				return SpecialKeys.of(Ascii.ESC, "[B".getBytes());
			case LEFT:
				return SpecialKeys.of(Ascii.ESC, "[D".getBytes());
			case RIGHT:
				return SpecialKeys.of(Ascii.ESC, "[C".getBytes());
			default:
				return SpecialKeys.of(Ascii.ESC);
			}
		}
	}

	public static boolean IsKeyInTheList(final String key)
	{
		for(int i=0;i<KEYS.length;i++)
		{
			if(KEYS[i].equalsIgnoreCase(key))
				return true;
		}
		return false;
	}
}
