/*
 * Copyright (C) 2007-2010 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.values;

import com.siemens.ct.exi.util.datatype.XSDBase64;

public class BinaryValue extends AbstractValue {

	protected final byte[] bytes;

	public BinaryValue(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public byte[] toBytes() {
		return bytes;
	}
	
	public int getCharactersLength() {
		if (slen == -1) {
			slen = XSDBase64.getCharactersLength(bytes);
		}
		return slen;
	}
	
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		XSDBase64.encode(bytes, cbuffer, offset);
		return cbuffer;	
	}

}
